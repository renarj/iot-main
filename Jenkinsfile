pipeline {
    agent any

    tools {
        maven "MVN3"
    }

    stages {
        stage('Build') {
            steps {
                git 'git@github.com:renarj/iot-main.git'

                sh "mvn clean install -Dmaven.test.skip=true"
            }

            post {
                success {
                    archiveArtifacts '**/target/*.jar'
                }
            }
        }
        stage("Docker") {
            steps {
                sh "docker build backend/command-svc/ -t command-svc:latest"
                sh "docker build backend/edge-svc/ -t edge-svc:latest"
                sh "docker build backend/state-svc/ -t state-svc:latest"
                sh "docker build backend/thing-svc/ -t thing-svc:latest"
                sh "docker build iot-base/iot-ui/ -t iot-ui:latest"
            }
        }
        stage("Publish Docker") {
            steps {
                sh "aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws/e1s4e3s4"
                sh "docker tag command-svc:latest public.ecr.aws/e1s4e3s4/command-svc:latest"
                sh "docker push public.ecr.aws/e1s4e3s4/command-svc:latest"

                sh "docker tag edge-svc:latest public.ecr.aws/e1s4e3s4/edge-svc:latest"
                sh "docker push public.ecr.aws/e1s4e3s4/edge-svc:latest"

                sh "docker tag state-svc:latest public.ecr.aws/e1s4e3s4/state-svc:latest"
                sh "docker push public.ecr.aws/e1s4e3s4/state-svc:latest"

                sh "docker tag thing-svc:latest public.ecr.aws/e1s4e3s4/thing-svc:latest"
                sh "docker push public.ecr.aws/e1s4e3s4/thing-svc:latest"

                sh "docker tag iot-ui:latest public.ecr.aws/e1s4e3s4/iot-ui:latest"
                sh "docker push public.ecr.aws/e1s4e3s4/iot-ui:latest"
            }
        }
    }
}
