pipeline {
    agent any

    environment {
            // --- LOCAL CONFIGURATION ---
            REGISTRY_URL   = "localhost:50409"
            SERVICE_NAME   = "yt-auth-service"
            MANIFEST_FILE  = "apps/auth-service.yaml"

            // Local image mapping string
            LOCAL_IMAGE    = "${REGISTRY_URL}/${SERVICE_NAME}"
            IMAGE_TAG      = "v${env.BUILD_NUMBER}"
        }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Docker Build & Tag') {
            steps {
                script {
                    // Build the local image using your specific build version
                    sh "docker build -t ${ECR_REPO_URL}:${IMAGE_TAG} ."
                }
            }
        }

        stage('Push to Minikube Registry') {
            steps {
                script {
                    // No AWS authentication required! Just push directly through the open port
                    sh "docker push ${LOCAL_IMAGE}:${IMAGE_TAG}"
                }
            }
        }

        stage('Update Git Manifest') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'github-gitops-token', usernameVariable: 'GH_USER', passwordVariable: 'GH_TOKEN')]) {

                        sh "rm -rf yt-clone-gitops-manifests"
                        sh "git clone https://${GH_TOKEN}@github.com/Porush09/yt-clone-gitops-manifests.git"

                        dir('yt-clone-gitops-manifests') {
                            sh "sed -i '' 's|${SERVICE_NAME}:.*|${SERVICE_NAME}:${IMAGE_TAG}|g' ${MANIFEST_FILE}"

                            sh """
                                git config user.name "Jenkins-CI"
                                git config user.email "jenkins@mountblue.com"
                                git add ${MANIFEST_FILE}
                                git commit -m "chore: automated image tag update for ${SERVICE_NAME} to ${IMAGE_TAG} [skip ci]"
                                git push origin main
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Successfully pushed ${SERVICE_NAME}:${IMAGE_TAG} to ECR!"
            // Clean up the local worker space using the exact version tag
            sh "docker rmi ${ECR_REPO_URL}:${IMAGE_TAG} || true"
        }
    }
}
