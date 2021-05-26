:: Service gateway-lb (LoadBalancer) is currently only used by Amazon EKS.
kubectl delete service,deployment creditrating
kubectl delete service,deployment location
kubectl delete service,deployment order
kubectl delete service,deployment gateway
kubectl delete service gateway-lb
kubectl delete service,deployment authserver
kubectl delete service,deployment configserver
kubectl delete service,deployment discovery
kubectl delete service,deployment database
kubectl get services
kubectl get pods -w
