# Load-testing environment (Docker Desktop Kubernetes)

Prerequisites: Docker Desktop with Kubernetes enabled, `kubectl`, `helm`, and optionally the
[Testkube CLI](https://docs.testkube.io/cli/testkube).

```
deployment/
├── manifests/
│   ├── namespace/   # load-testing namespace
│   ├── apps/        # REST and gRPC Deployments/Services and dedicated PostgreSQL resources
│   ├── configmaps/  # k6 script (test.js)
│   ├── jobs/        # k6 Job
│   └── testkube/    # Testkube TestWorkflow
├── k6/                   # REST vs. gRPC benchmark scripts and proto/
├── testkube-values.yaml  # Helm values for Testkube
├── prometheus-values.yaml # Helm values pointing Prometheus at Alertmanager
├── run-benchmark.sh      # REST vs. gRPC benchmark runner
└── run-tests.sh
```

## Build and start the applications

```bash
kubectl config use-context docker-desktop
cd deployment
./run-tests.sh k6
```

The script builds `docker/Dockerfile.rest` as `poc-springboot-rest:local` and
`docker/Dockerfile.grpc` as `poc-springboot-grpc:local`, using the repository
root as the Docker build context. Docker Desktop Kubernetes uses these local
images with `imagePullPolicy: IfNotPresent`. It then creates the `load-testing`
namespace, starts a dedicated PostgreSQL Deployment and Service for each
application, then deploys the REST and gRPC services. Database credentials are
provided to PostgreSQL and the matching application from separate Kubernetes
Secrets. The script waits for both databases, both applications, and all
project-labeled Pods to become Ready before starting the load test.

The REST application uses database `restdb` on
`rest-postgres-service.load-testing.svc.cluster.local:5432` with username
`rest_app` and password `rest-local-password`. The gRPC application uses
database `grpcdb` on
`grpc-postgres-service.load-testing.svc.cluster.local:5432` with username
`grpc_app` and password `grpc-local-password`. These credentials are local
load-testing defaults only; replace them before using this configuration in a
shared or production cluster. Kubernetes Secrets are base64-encoded by default,
not encrypted unless the cluster is configured for Secret encryption at rest.
Database data is stored in `emptyDir` volumes and is discarded when the Pods
are replaced.

The REST API is reachable inside the cluster at
`http://rest-service.load-testing.svc.cluster.local:8080`; the gRPC service is
at `grpc-service.load-testing.svc.cluster.local:9090`.

## Run k6 directly

```bash
./run-tests.sh k6
```

## Run with Testkube

```bash
cd deployment
kubectl apply -f manifests/namespace
helm repo add kubeshop https://kubeshop.github.io/helm-charts
helm repo update
helm upgrade --install testkube kubeshop/testkube -n load-testing -f testkube-values.yaml
./run-tests.sh testkube
```

Both test runners load `TARGET_URL` from the k6 script's service-DNS default.
To change the target, edit `BASE_URL` in `manifests/configmaps/k6-script.yaml`.

## REST vs. gRPC Benchmark

Both applications expose the same student search (`GET /api/students/search` and
`StudentService/SearchStudents`, page size 20, filter `first_name=Bench`). Each
test seeds 100 students in `setup()`.

- `k6/test-rest.js` uses `k6/http`; `k6/test-grpc.js` uses `k6/net/grpc` with
  `k6/proto/academic.proto` (copy of `grpc/src/main/proto/academic.proto`; keep in sync).
- `k6/common.js` holds the identical `ramping-arrival-rate` profile: low load
  (10-50 RPS), medium load (100-500 RPS) and peak load (1000-1500 RPS), plus
  thresholds on p90/p95/p99 latency, error rate and iteration throughput.

Prerequisites: applications deployed (`./run-tests.sh k6` once), Testkube installed
(see above), Testkube CLI, `jq`, and optionally metrics-server for resource usage.

```bash
cd deployment
COOLDOWN_SECONDS=60 ./run-benchmark.sh
```

The script checks readiness, runs the REST workflow, cools down, runs the gRPC
workflow, and prints a comparison table (p50/p90/p95/p99, throughput, error rate,
dropped iterations, peak CPU/memory, data received) with a recommendation. Raw
logs, per-protocol JSON and `summary.txt` are saved in `deployment/results/<timestamp>/`.

Interpreting results: a negative `gRPC vs REST` delta is better for latency, errors
and resources; positive is better for throughput. A non-zero dropped-iterations count
or crossed thresholds mark the saturation point. REST tends to win for small payloads
and simple clients; gRPC for high request rates and larger structured payloads.
Results from a single local cluster are indicative only; repeat runs before drawing conclusions.

## Cleanup

```bash
helm uninstall testkube -n load-testing
kubectl delete namespace load-testing
```

## Alertmanager

```bash
kubectl apply -f manifests/observability/alertmanager.yaml
helm upgrade --install prometheus prometheus-community/prometheus -n observability -f prometheus-values.yaml
```

Alertmanager is exposed in-cluster at `alertmanager.observability:9093`. Replace the placeholder webhook receiver in
`manifests/observability/alertmanager.yaml` with a real email/Slack integration.
