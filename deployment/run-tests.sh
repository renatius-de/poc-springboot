#!/usr/bin/env bash
# Usage: ./run-tests.sh [k6|testkube]   (default: k6)
set -euo pipefail

MODE="${1:-k6}"
NS="load-testing"
DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(cd "$DIR/.." && pwd)"

REST_DOCKERFILE="docker/Dockerfile.rest"
GRPC_DOCKERFILE="docker/Dockerfile.grpc"
REST_IMAGE="poc-springboot-rest:local"
GRPC_IMAGE="poc-springboot-grpc:local"
REST_POSTGRES_DEPLOYMENT="rest-postgres"
GRPC_POSTGRES_DEPLOYMENT="grpc-postgres"
REST_DEPLOYMENT="rest"
GRPC_DEPLOYMENT="grpc"

case "$MODE" in
  k6|testkube) ;;
  *)
    echo "Usage: $0 [k6|testkube]" >&2
    exit 1
    ;;
esac

docker build --file "$ROOT_DIR/$REST_DOCKERFILE" --tag "$REST_IMAGE" "$ROOT_DIR"
docker build --file "$ROOT_DIR/$GRPC_DOCKERFILE" --tag "$GRPC_IMAGE" "$ROOT_DIR"

kubectl apply -f "$DIR/manifests/namespace"
kubectl apply -f "$DIR/manifests/apps/rest-postgres.yaml"
kubectl apply -f "$DIR/manifests/apps/grpc-postgres.yaml"
kubectl -n "$NS" rollout status "deployment/$REST_POSTGRES_DEPLOYMENT" --timeout=180s
kubectl -n "$NS" rollout status "deployment/$GRPC_POSTGRES_DEPLOYMENT" --timeout=180s
kubectl apply -f "$DIR/manifests/apps/rest.yaml"
kubectl apply -f "$DIR/manifests/apps/grpc.yaml"
kubectl -n "$NS" rollout status "deployment/$REST_DEPLOYMENT" --timeout=180s
kubectl -n "$NS" rollout status "deployment/$GRPC_DEPLOYMENT" --timeout=180s
kubectl -n "$NS" wait --for=condition=Ready pod --all \
  -l app.kubernetes.io/part-of=poc-springboot --timeout=180s

case "$MODE" in
  k6)
    kubectl apply -f "$DIR/manifests/configmaps"
    kubectl -n "$NS" delete job k6-load-test --ignore-not-found
    kubectl apply -f "$DIR/manifests/jobs"
    kubectl -n "$NS" wait --for=condition=complete --timeout=300s job/k6-load-test \
      || { kubectl -n "$NS" logs job/k6-load-test || true; exit 1; }
    kubectl -n "$NS" logs job/k6-load-test
    ;;
  testkube)
    kubectl apply -f "$DIR/manifests/configmaps"
    kubectl apply -f "$DIR/manifests/testkube"
    kubectl testkube run testworkflow k6-load-test -n "$NS" --watch
    ;;
esac
