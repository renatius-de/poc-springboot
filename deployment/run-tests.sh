#!/usr/bin/env bash
# Usage: ./run-tests.sh [k6|testkube]   (default: k6)
set -euo pipefail

MODE="${1:-k6}"
NS="load-testing"
DIR="$(cd "$(dirname "$0")" && pwd)"

apply_stack() {
  kubectl apply -f "$DIR/manifests/namespace"
  kubectl apply -f "$DIR/manifests/postgres"
  kubectl apply -f "$DIR/manifests/applications"
  kubectl apply -f "$DIR/manifests/configmaps"
}

wait_for_stack() {
  kubectl -n "$NS" wait \
    --for=condition=Ready \
    --timeout=300s \
    pod \
    -l app.kubernetes.io/part-of=poc-springboot
}

case "$MODE" in
  k6)
    apply_stack
    wait_for_stack
    kubectl -n "$NS" delete job k6-load-test --ignore-not-found
    kubectl apply -f "$DIR/manifests/jobs"
    kubectl -n "$NS" wait --for=condition=complete --timeout=300s job/k6-load-test \
      || { kubectl -n "$NS" logs job/k6-load-test || true; exit 1; }
    kubectl -n "$NS" logs job/k6-load-test
    ;;
  testkube)
    apply_stack
    wait_for_stack
    kubectl apply -f "$DIR/manifests/testkube"
    kubectl testkube run testworkflow k6-load-test -n "$NS" --watch
    ;;
  *)
    echo "Usage: $0 [k6|testkube]" >&2
    exit 1
    ;;
esac
