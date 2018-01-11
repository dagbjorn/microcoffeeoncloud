@echo.
@echo ### Some useful commands:
@echo kubectl logs microcoffee mongodb -f
@echo kubectl exec -it microcoffee -c mongodb -- mongo microcoffee

kubectl create -f microcoffee-pod.yml
kubectl get pods -w
