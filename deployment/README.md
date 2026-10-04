# Load-testing environment (Docker Desktop Kubernetes)

Prerequisites: Docker Desktop with Kubernetes enabled, `kubectl`, `helm`, and optionally the
[Testkube CLI](https://docs.testkube.io/cli/testkube).

```
deployment/
├── manifests/
│   ├── namespace/   # load-testing namespace
│   ├── configmaps/  # k6 script (test.js)
│   ├── jobs/        # k6 Job
│   └── testkube/    # Testkube TestWorkflow
├── testkube-values.yaml  # Helm values for Testkube
└── run-tests.sh
```

## Start

```bash
kubectl config use-context docker-desktop
kubectl apply -f manifests/namespace
kubectl apply -f manifests/configmaps
```

## Run k6 directly

```bash
./run-tests.sh k6
```

## Run with Testkube

```bash
helm repo add kubeshop https://kubeshop.github.io/helm-charts
helm repo update
helm upgrade --install testkube kubeshop/testkube -n load-testing -f testkube-values.yaml
./run-tests.sh testkube
```

Change the target by editing `TARGET_URL` in `manifests/jobs/k6-job.yaml` or the TestWorkflow.

## Cleanup

```bash
helm uninstall testkube -n load-testing
kubectl delete namespace load-testing
```
