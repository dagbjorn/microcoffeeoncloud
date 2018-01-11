@echo.
@echo ### Some useful commands:
@echo kubectl logs microcoffee -f
@echo kubectl logs microcoffee creditrating -f

kubectl create -f microcoffee-pod.yml
kubectl get pods -w
