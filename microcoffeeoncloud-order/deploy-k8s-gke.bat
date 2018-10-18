@echo.
@echo ### Some useful commands:
@echo kubectl get pods
@echo kubectl logs PODNAME -f

kubectl create -f k8s-gke-service.yml
kubectl get services
kubectl get pods -w
