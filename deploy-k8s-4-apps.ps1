#
# Deploy application to specified Kubernetes platform.
#

$ValidPlatforms = @("gke", "eks", "aks", "mkube")

Function Print-Usage {
    $output = $ValidPlatforms -join ", "
    Write-Output "Usage: deploy-k8s-4-apps.ps1 platform"
    Write-Output "       platform   $output"
}

if ($args.Count -eq 0) {
    Print-Usage
    return
}

$Platform = $args[0]

if ($ValidPlatforms -notcontains $Platform) {
    Write-Output "ERROR: Unsupported Kubernetes platform`n"
    Print-Usage
    return
}

Write-Output "### Some useful commands:"
Write-Output "kubectl get pods"
Write-Output "kubectl logs PODNAME -f"
Write-Output ""

kubectl apply -f microcoffeeoncloud-discovery/k8s-service-$Platform.yml
kubectl apply -f microcoffeeoncloud-gateway/k8s-service-$Platform.yml
kubectl apply -f microcoffeeoncloud-location/k8s-service-$Platform.yml
kubectl apply -f microcoffeeoncloud-order/k8s-service-$Platform.yml
kubectl apply -f microcoffeeoncloud-creditrating/k8s-service-$Platform.yml
kubectl get services
kubectl get pods -w
