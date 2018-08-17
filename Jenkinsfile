node {
    stage ('checkout') {
        git credentialsId: 'github-ssh', url: 'git@github.com:renarj/robo-max.git'
    }

    stage ('build') {
        withMaven(maven: 'M3', jdk: 'JDK10') {
            sh "mvn clean install"
        }
    }

    stage 'archive'
    step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
}