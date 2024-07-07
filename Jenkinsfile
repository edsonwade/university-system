pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'your-docker-registry'
        DOCKER_CREDENTIALS_ID = 'docker-credentials-id'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                script {
                    sh 'mvn clean install'
                }
            }
        }
        stage('Test with Maven') {
                    steps {
                        script {
                            sh 'mvn test'
                        }
                    }
                }

        stage('Build Docker Image') {
            steps {
                script {
                    def image = docker.build("${DOCKER_REGISTRY}/student-management-system:${env.BUILD_ID}")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('', "${DOCKER_CREDENTIALS_ID}") {
                        def image = docker.image("${DOCKER_REGISTRY}/student-management-system:${env.BUILD_ID}")
                        image.push()
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Here you can deploy your Docker container, e.g., using Docker Compose
                    sh 'docker-compose down'
                    sh 'docker-compose up -d'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
