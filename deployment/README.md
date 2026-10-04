# Load-testing environment (Docker Desktop Kubernetes)

Prerequisites: Docker Desktop with Kubernetes enabled, `kubectl`, `helm`, and optionally the
[Testkube CLI](https://docs.testkube.io/cli/testkube).

The `load-testing` namespace runs the REST and gRPC applications, each with its own PostgreSQL
Deployment, Service, Secret, and persistent volume claim:

| Application | Application service | Database service | Database | User | Local password |
| --- | --- | --- | --- | --- | --- |
| REST | `rest-service:8080` | `rest-postgres:5432` | `restdb` | `restapp` | `rest-local-db-password` |
| gRPC | `grpc-service:9090` | `grpc-postgres:5432` | `grpcdb` | `grpcapp` | `grpc-local-db-password` |

The database credentials are defined in `manifests/postgres/postgres.yaml` as Kubernetes Secrets
and injected into their respective database and application Pods. The passwords in this repository
are for local testing only; replace them and manage Secrets outside source control for shared or
production environments. Each database stores its data in a separate 1 GiB PVC.

## Build the application images

Run these commands from the repository root. Docker Desktop Kubernetes can use these locally built
images without pulling from a registry.

```bash
docker build -f docker/Dockerfile.rest -t poc-springboot-rest:local .
docker build -f docker/Dockerfile.grpc -t poc-springboot-grpc:local .
```

## Start and inspect

`run-tests.sh` applies the namespace, database and application resources, and k6 ConfigMap. It waits
until every PostgreSQL and application Pod is Ready before starting either load-test runner. The
application Pods wait for their respective database using an init container; database and
application health probes keep unready Pods out of Service traffic.

```bash
cd deployment
kubectl config use-context docker-desktop
./run-tests.sh k6
```

The k6 test targets the REST search API at
`http://rest-service:8080/api/students/search`. Change `TARGET_URL` in
`manifests/jobs/k6-job.yaml` and `manifests/testkube/testworkflow-k6.yaml` to choose another target.
The gRPC service listens at `grpc-service:9090`.

```bash
kubectl get pods,services,pvc -n load-testing
kubectl -n load-testing exec -it deployment/rest-postgres -- psql -U restapp -d restdb
kubectl -n load-testing exec -it deployment/grpc-postgres -- psql -U grpcapp -d grpcdb
```

## Run with Testkube

```bash
helm repo add kubeshop https://kubeshop.github.io/helm-charts
helm repo update
helm upgrade --install testkube kubeshop/testkube -n load-testing --create-namespace -f testkube-values.yaml
./run-tests.sh testkube
```

## Cleanup

Deleting the namespace removes the database PVCs and their data.

```bash
helm uninstall testkube -n load-testing
kubectl delete namespace load-testing
```
