pipeline {
    agent any

    environment {
        IMAGE_NAME = "localhost:5000/backend"
        RELEASE_NAME = "backend"
        CHART_PATH = "helm/backend"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Read Version') {
            steps {
                sh "chmod +x mvnw"

                script {
                    VERSION = sh(
                        script: "./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout",
                        returnStdout: true
                    ).trim()

                    echo "Project version: ${VERSION}"
                }
            }
        }

        stage('Build JAR') {
            steps {
                sh "chmod +x mvnw"
                sh "./mvnw clean package -DskipTests"
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${VERSION} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                sh "docker push ${IMAGE_NAME}:${VERSION}"
            }
        }

        stage('Deploy to K3s') {
            steps {
                sh """
                helm upgrade --install ${RELEASE_NAME} ${CHART_PATH} \
                  --set image.tag=${VERSION}
                """
            }
        }

        stage('Rollout Status') {
            steps {
                sh "kubectl rollout status deployment/backend-backend --timeout=120s"
            }
        }
    }
}