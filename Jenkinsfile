#!groovy

pipeline {
    agent any
    parameters {
        booleanParam(name: 'SKIP_TESTS', defaultValue: true, description: 'Skip tests')
        string(name: 'ALT_DEPLOYMENT_REPOSITORY', defaultValue: '', description: 'Alternative deployment repo')
    }
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    stages {
        stage ('Build') {
            steps {
                sh "mvn -DskipTests=${params.SKIP_TESTS} -s /var/run/secrets/nexus-mvn-settings clean compile install"
            }
        }
        stage ('Deploy') {
            steps {
                sh  "mvn -DskipTests=${params.SKIP_TESTS} -s /var/run/secrets/nexus-mvn-settings  deploy"
            }
        }
    }
}