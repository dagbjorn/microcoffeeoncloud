#
# Deploy servers part 2 to specified Kubernetes platform.
#

$ValidPlatforms = @("gke", "eks", "aks", "mkube")

Function Print-Usage {
    $output = $ValidPlatforms -join ", "
    Write-Output "Usage: deploy-k8s-2-servers.ps1 platform"
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
Write-Output "kubectl exec -it PODNAME -- mongo microcoffee"
Write-Output ""

kubectl apply -f microcoffeeoncloud-configserver/k8s-service-$Platform.yml
kubectl apply -f microcoffeeoncloud-database/k8s-service-$Platform.yml
kubectl get services
kubectl get pods -w
