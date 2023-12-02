node {
    stage ('checkout') {
        git credentialsId: 'github-ssh', url: 'git@github.com:renarj/iot-main.git'
    }

    stage ('build') {
        withMaven(maven: 'M3', jdk: 'JDK21') {
            sh "mvn clean install"
        }
    }

    stage 'archive'
    step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
}