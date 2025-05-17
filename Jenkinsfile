pipeline{
    agent{
        label "Default"
    }
    tools {
        jdk 'Java17'
        maven 'Maven3'
    }

    environment{
        APP_NAME= "car-rental-orchestrator"
        RELEASE = "1.0.0"
        DOCKER_USER = "gukami98"
        DOCKER_PASS = "DOCKERHUB_LOGIN"
        IMAGE_NAME =  "${DOCKER_USER}" + "/" + "${APP_NAME}"
        IMAGE_TAG = "${RELEASE}-${BUILD_NUMBER}" 
    }


    stages{
        stage("Cleanup Workspace"){
            steps{
                cleanWs()
            }
        }

        stage("Checkout from SCM"){
            steps{
                git branch: 'main', credentialsId: 'GITHUB_LOGIN', url: 'https://github.com/myOrganization-GV/car-rental-orchestrator'
            }
        }
        stage("Build Application"){
            steps{
                sh "mvn clean package"
            }
        }
            
    }
}
