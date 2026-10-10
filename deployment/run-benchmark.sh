#!/usr/bin/env bash
# Usage: ./run-benchmark.sh   (env: COOLDOWN_SECONDS=60, RESULTS_DIR=./results)
# Runs the REST and gRPC k6 benchmarks via Testkube and prints a comparison table.
set -euo pipefail

NS="load-testing"
DIR="$(cd "$(dirname "$0")" && pwd)"
COOLDOWN_SECONDS="${COOLDOWN_SECONDS:-60}"
RESULTS_DIR="${RESULTS_DIR:-$DIR/results/$(date +%Y%m%d-%H%M%S)}"
MARKER="BENCHMARK_SUMMARY_JSON:"
SAMPLER_PIDS=()

for tool in kubectl jq awk; do
  command -v "$tool" >/dev/null || { echo "Missing required tool: $tool" >&2; exit 1; }
done
kubectl testkube version >/dev/null 2>&1 \
  || { echo "Testkube CLI (kubectl-testkube) is required." >&2; exit 1; }

mkdir -p "$RESULTS_DIR"

cleanup() {
  for pid in "${SAMPLER_PIDS[@]:-}"; do
    [ -n "$pid" ] && kill "$pid" 2>/dev/null || true
  done
}
trap cleanup EXIT

echo "==> 1/5 Checking readiness of databases and applications"
for dep in rest-postgres grpc-postgres rest grpc; do
  kubectl -n "$NS" rollout status "deployment/$dep" --timeout=180s
done
kubectl -n "$NS" wait --for=condition=Ready pod --all \
  -l app.kubernetes.io/part-of=poc-springboot --timeout=180s

echo "==> Publishing k6 scripts and workflows"
kubectl -n "$NS" create configmap k6-benchmark-scripts \
  --from-file=common.js="$DIR/k6/common.js" \
  --from-file=test-rest.js="$DIR/k6/test-rest.js" \
  --from-file=test-grpc.js="$DIR/k6/test-grpc.js" \
  --from-file=academic.proto="$DIR/k6/proto/academic.proto" \
  --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -f "$DIR/manifests/testkube/testworkflow-k6-rest.yaml"
kubectl apply -f "$DIR/manifests/testkube/testworkflow-k6-grpc.yaml"

# Samples pod CPU/memory every 5s (needs metrics-server; best effort) and
# records the peak values as "<cpu millicores> <memory MiB>".
sample_resources() {
  local app="$1" out="$2"
  : > "$out.samples"
  while true; do
    kubectl -n "$NS" top pod -l "app.kubernetes.io/name=$app" --no-headers 2>/dev/null \
      | awk '{gsub("m","",$2); gsub("Mi","",$3); print $2, $3}' >> "$out.samples" || true
    sleep 5
  done
}

peak_resources() {
  awk 'BEGIN{c=0;m=0} {if($1+0>c)c=$1+0; if($2+0>m)m=$2+0} END{print c, m}' "$1.samples" 2>/dev/null \
    || echo "0 0"
}

run_benchmark() {
  local protocol="$1" workflow="$2" app="$3"
  local log="$RESULTS_DIR/$protocol.log" json="$RESULTS_DIR/$protocol.json"
  local res="$RESULTS_DIR/$protocol.resources"

  sample_resources "$app" "$res" &
  local sampler=$!
  SAMPLER_PIDS+=("$sampler")

  # k6 returns non-zero when thresholds are crossed; the metrics are still valuable.
  kubectl testkube run testworkflow "$workflow" -n "$NS" --watch 2>&1 | tee "$log" || \
    echo "WARNING: $workflow finished with failures (thresholds crossed?)" >&2

  kill "$sampler" 2>/dev/null || true
  wait "$sampler" 2>/dev/null || true

  grep -a "$MARKER" "$log" | tail -n 1 | sed "s/.*$MARKER//" > "$json" || true
  if ! jq -e . "$json" >/dev/null 2>&1; then
    echo "ERROR: no k6 summary found for $protocol (see $log)" >&2
    exit 1
  fi
  read -r cpu mem < <(peak_resources "$res")
  jq --argjson cpu "${cpu:-0}" --argjson mem "${mem:-0}" \
    '. + {peak_cpu_millicores: $cpu, peak_memory_mib: $mem}' "$json" > "$json.tmp"
  mv "$json.tmp" "$json"
}

echo "==> 2/5 Running REST load test"
run_benchmark rest k6-rest-benchmark rest

echo "==> 3/5 Cool-down for ${COOLDOWN_SECONDS}s"
sleep "$COOLDOWN_SECONDS"

echo "==> 4/5 Running gRPC load test (identical load profile)"
run_benchmark grpc k6-grpc-benchmark grpc

echo "==> 5/5 Comparison"
R="$RESULTS_DIR/rest.json"
G="$RESULTS_DIR/grpc.json"

row() { # label jq-path format
  local label="$1" path="$2" fmt="$3"
  local r g
  r=$(jq -r "$path // 0" "$R")
  g=$(jq -r "$path // 0" "$G")
  awk -v l="$label" -v r="$r" -v g="$g" -v f="$fmt" \
    'BEGIN{ rv=sprintf(f,r); gv=sprintf(f,g); d=(r>0)?(g-r)/r*100:0;
            printf "| %-26s | %14s | %14s | %+9.1f%% |\n", l, rv, gv, d }'
}

line="+----------------------------+----------------+----------------+------------+"
{
  echo "$line"
  printf "| %-26s | %14s | %14s | %10s |\n" "Metric" "REST" "gRPC" "gRPC vs REST"
  echo "$line"
  row "Latency p50 (ms)" '.latency_ms.p50' '%.2f'
  row "Latency p90 (ms)" '.latency_ms.p90' '%.2f'
  row "Latency p95 (ms)" '.latency_ms.p95' '%.2f'
  row "Latency p99 (ms)" '.latency_ms.p99' '%.2f'
  row "Throughput (iter/s)" '.throughput_rps' '%.2f'
  row "Error rate (%)" '(.error_rate * 100)' '%.3f'
  row "Dropped iterations" '.dropped_iterations' '%.0f'
  row "Peak CPU (millicores)" '.peak_cpu_millicores' '%.0f'
  row "Peak memory (MiB)" '.peak_memory_mib' '%.0f'
  row "Data received (MB)" '(.data_received_bytes / 1000000)' '%.2f'
  echo "$line"
} | tee "$RESULTS_DIR/summary.txt"

rest_p95=$(jq -r '.latency_ms.p95 // 0' "$R"); grpc_p95=$(jq -r '.latency_ms.p95 // 0' "$G")
rest_rps=$(jq -r '.throughput_rps // 0' "$R"); grpc_rps=$(jq -r '.throughput_rps // 0' "$G")
rest_err=$(jq -r '.error_rate // 0' "$R"); grpc_err=$(jq -r '.error_rate // 0' "$G")

{
  echo
  echo "Recommendation:"
  awk -v rl="$rest_p95" -v gl="$grpc_p95" -v rt="$rest_rps" -v gt="$grpc_rps" \
      -v re="$rest_err" -v ge="$grpc_err" 'BEGIN{
    if (gl < rl && gt >= rt) print "- gRPC performed better: lower p95 latency and equal/higher throughput. Prefer gRPC for internal service-to-service traffic, high request rates and streaming.";
    else if (rl < gl && rt >= gt) print "- REST performed better: lower p95 latency and equal/higher throughput. Prefer REST for public/browser-facing APIs, simple payloads and easy debugging.";
    else print "- Mixed result: compare latency and throughput rows; neither protocol dominates in this run.";
    if (ge < re) print "- gRPC had the lower error rate under peak load.";
    else if (re < ge) print "- REST had the lower error rate under peak load.";
    print "- REST wins when: payloads are small, clients are browsers/third parties, caching/HTTP tooling matters.";
    print "- gRPC wins when: payloads are large or structured, binary encoding and HTTP/2 multiplexing reduce overhead, strict contracts are needed.";
  }'
  echo
  echo "Raw results: $RESULTS_DIR"
} | tee -a "$RESULTS_DIR/summary.txt"
