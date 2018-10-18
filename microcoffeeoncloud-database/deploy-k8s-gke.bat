@echo.
@echo ### Some useful commands:
@echo kubectl get pods
@echo kubectl logs PODNAME -f
@echo kubectl exec -it PODNAME -- mongo microcoffee

kubectl create -f k8s-gke-service.yml
kubectl get services
kubectl get pods -w
