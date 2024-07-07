# Kubernetes Commands Cheat Sheet

## Cluster Management

### Cluster Info
- `kubectl cluster-info`: Display addresses of the master and services.

### Node Management
- `kubectl get nodes`: List all nodes in the cluster.
- `kubectl describe node <node-name>`: Show detailed information about a node.

### Namespace Management
- `kubectl get namespaces`: List all namespaces.
- `kubectl get namespace <namespace-name>`: Show detailed information about a namespace.
- `kubectl create namespace <namespace-name>`: Create a new namespace.
- `kubectl delete namespace <namespace-name>`: Delete a namespace and all resources within it.

## Workload Management

### Pods
- `kubectl get pods`: List all pods in the current namespace.
- `kubectl get pods -n <namespace-name>`: List all pods in a specific namespace.
- `kubectl describe pod <pod-name>`: Show details of a pod.
- `kubectl logs <pod-name>`: Print the logs from a pod.

### Deployments
- `kubectl get deployments`: List all deployments.
- `kubectl describe deployment <deployment-name>`: Show details of a deployment.
- `kubectl scale deployment <deployment-name> --replicas=<num>`: Scale a deployment to a specified number of replicas.
- `kubectl rollout status deployment <deployment-name>`: Check the rollout status of a deployment.
- `kubectl rollout history deployment <deployment-name>`: Show rollout history of a deployment.
- `kubectl rollout undo deployment <deployment-name>`: Undo a deployment to previous revision.

### ReplicaSets
- `kubectl get replicasets`: List all ReplicaSets.
- `kubectl describe replicaset <replicaset-name>`: Show details of a ReplicaSet.

### StatefulSets
- `kubectl get statefulsets`: List all StatefulSets.
- `kubectl describe statefulset <statefulset-name>`: Show details of a StatefulSet.

### DaemonSets
- `kubectl get daemonsets`: List all DaemonSets.
- `kubectl describe daemonset <daemonset-name>`: Show details of a DaemonSet.

## Service Management

### Services
- `kubectl get services`: List all services.
- `kubectl describe service <service-name>`: Show details of a service.

### Port Forwarding
- `kubectl port-forward <pod-name> <local-port>:<pod-port>`: Forward one or more local ports to a pod.

### Exposing Services
- `kubectl expose deployment <deployment-name> --type=NodePort --port=<port>`: Expose a deployment via a NodePort service.

## Configuration Management

### ConfigMaps
- `kubectl get configmaps`: List all ConfigMaps.
- `kubectl describe configmap <configmap-name>`: Show details of a ConfigMap.

### Secrets
- `kubectl get secrets`: List all Secrets.
- `kubectl describe secret <secret-name>`: Show details of a Secret.

## Resource Management

### Resource Usage
- `kubectl top node`: Show metrics for nodes.
- `kubectl top pod`: Show metrics for pods.

### Resource Limits
- `kubectl describe pod <pod-name>`: Show resource requests and limits set on a pod.
- `kubectl edit pod <pod-name>`: Edit resource requests and limits directly.

## Troubleshooting and Debugging

### Pod Health
- `kubectl get events`: List all events in the cluster.
- `kubectl describe pod <pod-name>`: Check events and describe a pod for troubleshooting.

### Executing Commands
- `kubectl exec -it <pod-name> -- <command>`: Execute a command in a running pod.

### Log Collection
- `kubectl logs <pod-name>`: Retrieve logs from a pod.

## Advanced Commands

### Raw Manifests
- `kubectl apply -f <filename.yaml>`: Apply a configuration file to the cluster.
- `kubectl create -f <filename.yaml>`: Create resources from a configuration file.
- `kubectl delete -f <filename.yaml>`: Delete resources defined in a configuration file.

### Namespace-wide Operations
- `kubectl api-resources`: List all API resources supported by the server.
- `kubectl api-versions`: List all API versions supported by the server.

### Customizing Output
- `kubectl get pods -o wide`: Get additional information with wide output format.

### To delete all resources in the default namespace and ensure that no new pods are created

- `kubectl delete all --all -n default`:delete all resources in the default namespace

### Ensure there are no resources left after delete

- `kubectl get all -n default`: List all resources

### Check for any resource definitions
- `kubectl get deployments,replicasets,statefulsets,daemonsets -n default`: like Deployment, ReplicaSet, StatefulSet, DaemonSet) that might recreate the pods.

### Delete any remaining resource definitions
-`kubectl delete deployment <deployment-name> -n default`
-`kubectl delete replicaset <replicaset-name> -n default`
-`kubectl delete statefulset <statefulset-name> -n default`
-`kubectl delete daemonset <daemonset-name> -n default`
---

This cheat sheet covers essential Kubernetes commands for managing clusters, workloads, services, configurations, and troubleshooting. Customize commands and options as per your specific use case and environment.

For more details on each command, refer to the [Kubernetes Documentation](https://kubernetes.io/docs/).
