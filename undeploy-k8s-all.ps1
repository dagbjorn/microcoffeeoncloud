# Service gateway-lb (LoadBalancer) is currently only used by Amazon EKS.
kubectl delete "service,deployment" creditrating
kubectl delete "service,deployment" location
kubectl delete "service,deployment" order
kubectl delete "service,deployment" gateway
kubectl delete service gateway-lb
kubectl delete "service,deployment" authserver
kubectl delete "service,deployment" configserver
kubectl delete "service,deployment" discovery
kubectl delete "service,deployment" database
kubectl delete secret order-client-secret
kubectl wait --for=delete pod -l "app in (authserver,configserver,mongodb,discovery,gateway,location,order,creditrating)" --timeout=120s
kubectl get services
kubectl get pods
