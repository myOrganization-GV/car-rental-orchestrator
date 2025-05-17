pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        APP_NAME = "car-rental-orchestrator"
        RELEASE = "1.0.0"
        DOCKER_USER = "gukami98"
        DOCKER_PASS = "DOCKERHUB_LOGIN"
        IMAGE_NAME = "${DOCKER_USER}/${APP_NAME}"
        IMAGE_TAG = "${RELEASE}-${BUILD_NUMBER}"
    }

    stages {
        stage("Cleanup Workspace") {
            steps {
                cleanWs()
            }
        }

        stage("Checkout Common") {
            steps {
                git branch: 'main', credentialsId: 'GITHUB_LOGIN', url: 'https://github.com/myOrganization-GV/car-rental-common'
            }
        }
        stage("Build Common Application") {
            steps {
                sh "mvn clean install -DskipTests" 
            }
        }

        stage("Checkout Orchestrator") {
            steps {
                git branch: 'main', credentialsId: 'GITHUB_LOGIN', url: 'https://github.com/myOrganization-GV/car-rental-orchestrator'
            }
        }
        stage("Build Orchestrator Application") {
            steps {
                sh "mvn clean package"
            }
        }

    }
}