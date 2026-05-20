pipeline {
    agent any

    environment {
        AWS_ACCOUNT_ID = "${env.AWS_ACCOUNT_ID}"
        AWS_REGION     = "ap-south-1"

        // --- SERVICE CONFIGURATION ---
        SERVICE_NAME   = "yt-auth-service"
        // This targets the specific file inside your manifests repository
        MANIFEST_FILE  = "apps/auth-service.yaml"

        ECR_REPO_URL   = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${SERVICE_NAME}"

        // This creates a unique identifier for this specific build (e.g., v12, v13)
        IMAGE_TAG      = "v${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Maven Build') {
            steps {
                sh "chmod +x mvnw"
                sh "./mvnw clean package -DskipTests"
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

        stage('Push to ECR') {
            steps {
                script {
                    // Authenticate with ECR
                    sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

                    // Push ONLY the unique version tag to ECR
                    sh "docker push ${ECR_REPO_URL}:${IMAGE_TAG}"
                }
            }
        }

        stage('Update Git Manifest') {
            steps {
                script {
                    // Wrap in a credentials block using your Jenkins GitHub Token ID
                    // Make sure 'github-token' matches the ID of your secret text/credential in Jenkins
                    withCredentials([string(credentialsId: 'github-token', variable: 'GH_TOKEN')]) {

                        // 1. Clean any old directory if it exists and clone your manifests repository
                        sh "rm -rf yt-clone-gitops-manifests"
                        sh "git clone https://${GH_TOKEN}@github.com/porushyadav/yt-clone-gitops-manifests.git"

                        dir('yt-clone-gitops-manifests') {
                            // 2. Use sed to replace the old image tag with our new dynamic IMAGE_TAG
                            // This looks for 'yt-auth-service:anything' and replaces it with 'yt-auth-service:vBuildNum'
                            sh "sed -i 's|${SERVICE_NAME}:.*|${SERVICE_NAME}:${IMAGE_TAG}|g' ${MANIFEST_FILE}"

                            // 3. Configure git and push the updated manifest file back to GitHub
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