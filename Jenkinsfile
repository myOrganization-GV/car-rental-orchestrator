pipeline{
    agent{
        label "Default"
    }
    tools {
        jdk 'Java17'
        maven 'Maven3'
    }

    environment{
        APP_NAME= "vuttr"
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
                git branch: 'master', credentialsId: 'GITHUB_LOGIN', url: 'https://github.com/Guilherme-Vale-98/VUTTR-project'
            }
        }
         stage("Prepare Configuration") {
            steps {
                configFileProvider([configFile(fileId: 'config-1', targetLocation: 'src/main/resources/application.yml')]) {
                   
                }
            }
        }

        stage("Build Application"){
            steps{
                powershell "mvn clean package"
            }
        }

        stage("Test Application"){
            steps{
                powershell "mvn test"
            }
        }

        stage("Sonarqube Analysis"){
            steps{
                script{
                withSonarQubeEnv(credentialsId: 'jenkins-sonaqube-token' ){
                powershell "mvn sonar:sonar"
                }
            }}
        }

        stage("Quality Gate"){
            steps{
                script{
                    waitForQualityGate abortPipeline: false, credentialsId: 'jenkins-sonaqube-token' 
                }
            }}
        stage("Build & Push Docker Image"){
            steps{
                script{
                   powershell 'docker context use default'

                   docker.withRegistry('', DOCKER_PASS) {
                    docker_image = docker.build "${IMAGE_NAME}"
                   }
                   docker.withRegistry('', DOCKER_PASS){
                    docker_image.push("${IMAGE_TAG}")
                    docker_image.push('latest')
                   }

                }
            }}
        }
    }
