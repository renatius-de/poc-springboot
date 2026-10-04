# Load-testing environment (Docker Desktop Kubernetes)

Prerequisites: Docker Desktop with Kubernetes enabled, `kubectl`, `helm`, and optionally the
[Testkube CLI](https://docs.testkube.io/cli/testkube).

```
deployment/
├── manifests/
│   ├── namespace/   # load-testing namespace
│   ├── apps/        # PostgreSQL, REST, and gRPC Deployments and Services
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
namespace, starts PostgreSQL, deploys the REST and gRPC services, and waits for
all three Deployments to become Ready before starting the load test.

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
