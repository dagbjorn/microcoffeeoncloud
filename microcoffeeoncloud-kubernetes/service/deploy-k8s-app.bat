kubectl create -f microcoffee-mongodb.yml
kubectl create -f microcoffee-creditrating.yml
kubectl create -f microcoffee-location.yml
kubectl create -f microcoffee-order.yml
kubectl create -f microcoffee-gui.yml
kubectl get services
kubectl get pods -w
