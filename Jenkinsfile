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
    }
}
