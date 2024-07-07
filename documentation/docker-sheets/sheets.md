# Docker Commands Cheat Sheet

## Image Management

### List Images
- `docker images`: List all images on the local machine.

### Pull Images
- `docker pull <image-name>:<tag>`: Pull an image from a registry.

### Build Images
- `docker build -t <image-name> <path-to-Dockerfile>`: Build an image from a Dockerfile.

### Remove Images
- `docker rmi <image-name>`: Remove an image from the local machine.

## Container Management

### List Containers
- `docker ps`: List all running containers.
- `docker ps -a`: List all containers (including stopped ones).
- `docker ps -q`:Lists all running containers and outputs only their container IDs (-q flag).

### Run Containers
- `docker run <image-name>`: Run a container from an image.
- `docker run -d <image-name>`: Run a container in detached mode.
- `docker run -it <image-name> <command>`: Run a container interactively.

### Start/Stop Containers
- `docker start <container-id>`: Start a stopped container.
- `docker stop <container-id>`: Stop a running container.

### Remove Containers
- `docker rm <container-id>`: Remove a stopped container.
- `docker rm -f <container-id>`: Force remove a running container.

### Inspect Containers
- `docker inspect <container-id>`: Display detailed information about a container.
- `docker logs <container-id>`: Fetch the logs of a container.

### Execute Commands- `docker exec -it <container-id> <command>`: Execute a command in a running container.

## Volume Management

### List Volumes
- `docker volume ls`: List all volumes.

### Create/Remove Volumes
- `docker volume create <volume-name>`: Create a new volume.
- `docker volume rm <volume-name>`: Remove a volume.
- `docker-compose down -v `: This command not only stops the containers but also removes them and their associated networks.

### Inspect Volumes
- `docker volume inspect <volume-name>`: Display detailed information about a volume.

## Network Management

### List Networks
- `docker network ls`: List all networks.

### Create/Remove Networks
- `docker network create <network-name>`: Create a new network.
- `docker network rm <network-name>`: Remove a network.

### Inspect Networks
- `docker network inspect <network-name>`: Display detailed information about a network.

## Docker Compose

### Run Docker Compose
- `docker-compose up`: Start Docker Compose services defined in `docker-compose.yml`.
- `docker-compose down`: Stop Docker Compose services and remove containers.

### Docker Compose Commands
- `docker-compose build`: Build or rebuild services.
- `docker-compose logs`: View output from services.
- `docker-compose ps`: List containers for services.

## Docker Registry

### Login/Logout
- `docker login`: Log in to a Docker registry.
- `docker logout`: Log out from a Docker registry.

### Push/Pull Images
- `docker push <image-name>:<tag>`: Push an image to a registry.
- `docker pull <image-name>:<tag>`: Pull an image from a registry.

### Remove All Volumes
- `docker volume prune`: Remove all unused Docker volumes.


### Remove All Containers
- `docker container prune`: Remove all stopped container.

### Remove All Unused Images
- `docker image prune`: Prune Docker images (remove all dangling and unused images.

### Stops all containers.
- `docker stop $(docker ps -q)`:Stops all containers that are currently running.

### Start a Container
- Start a container with a specified name:
  ```bash
  docker run --name mycontainer -d myimage
  ```
### Login to Docker Hub
- `docker login `: Login to Docker Hub (enter username and password as prompted)

### Tag Docker Image
- `docker tag myimage yourusername/myrepository:tag`: Tag a Docker image with your Docker Hub username and repository name
- 
### Push Docker Image to Docker Hub
- `docker push yourusername/myrepository:tag `: Push a tagged Docker image to Docker Hub

### To stop all running Docker containers without specifying their IDs individually
- `docker stop $(docker ps -q) `: Remove all stopped container.
1. docker ps -q: Lists the IDs of all running containers.
2. docker stop $(docker ps -q): Stops all containers whose IDs are listed by docker ps -q.
---

This cheat sheet covers essential Docker commands for image management, container management, volume management, network management, Docker Compose, and Docker registry operations. Customize commands and options based on your specific Docker environment and requirements.

For more details on each command, refer to the [Docker Documentation](https://docs.docker.com/).
