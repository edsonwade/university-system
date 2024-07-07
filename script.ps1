# Script to build Maven project, build Docker image, and start Docker Compose

# Step 1: Maven clean install
Write-Host "Step 1: Running 'mvn clean install'"
mvn clean install

# Check if Maven build was successful
if ($LastExitCode -ne 0) {
    Write-Host "Maven build failed. Exiting script."
    exit 1
}

# Step 2: Docker build
Write-Host "Step 2: Building Docker image 'student-management-system'"
docker build -t student-management-system .

# Check if Docker build was successful
if ($LastExitCode -ne 0) {
    Write-Host "Docker build failed. Exiting script."
    exit 1
}

# Step 3: Docker Compose up
Write-Host "Step 3: Starting Docker Compose"
docker-compose up
