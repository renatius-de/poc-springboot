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
├── testkube-values.yaml  # Helm values for Testkube
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

## Cleanup

```bash
helm uninstall testkube -n load-testing
kubectl delete namespace load-testing
```
