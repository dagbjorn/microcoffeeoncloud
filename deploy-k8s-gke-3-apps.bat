@echo.
@echo ### Some useful commands:
@echo kubectl get pods
@echo kubectl logs PODNAME -f

kubectl create -f microcoffeeoncloud-gateway/k8s-gke-service.yml
kubectl create -f microcoffeeoncloud-location/k8s-gke-service.yml
kubectl create -f microcoffeeoncloud-order/k8s-gke-service.yml
kubectl create -f microcoffeeoncloud-creditrating/k8s-gke-service.yml
kubectl get services
kubectl get pods -w
