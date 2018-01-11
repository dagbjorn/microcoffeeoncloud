@echo.
@echo ### Some useful commands:
@echo kubectl logs microcoffee-configserver -f
@echo kubectl logs microcoffee-configserver configserver -f

kubectl create -f microcoffee-pod.yml
kubectl get pods -w
