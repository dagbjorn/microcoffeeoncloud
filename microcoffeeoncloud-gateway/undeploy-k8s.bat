:: Service gateway-lb (LoadBalancer) is currently only used by Amazon EKS.
kubectl delete service,deployment gateway
kubectl delete service gateway-lb
kubectl get services
kubectl get pods -w
