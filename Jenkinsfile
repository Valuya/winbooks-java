pipeline {
    agent any
    parameters {
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip tests')
        booleanParam(name: 'FORCE_DEPLOY', defaultValue: false, description: 'Force deploy on feature branches')
        string(name: 'ALT_DEPLOYMENT_REPOSITORY', defaultValue: '', description: 'Alternative deployment repo')
        string(name: 'MVN_ARGS', defaultValue: '', description: 'Additional maven args')
    }
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    stages {
        stage ('Build') {
            steps {
                script {
                    env.MVN_ARGS=params.MVN_ARGS
                    if (params.ALT_DEPLOYMENT_REPOSITORY != '') {
                        env.MVN_ARGS="${env.MVN_ARGS} -DaltDeploymentRepository=${params.ALT_DEPLOYMENT_REPOSITORY}"
                    }
                }
                withMaven(maven: 'maven', mavenSettingsConfig: 'ossrh-settings-xml') {
                    sh "mvn -DskipTests=${params.SKIP_TESTS} clean compile install"
                }
            }
        }
        stage ('Publish') {
            when { anyOf {
                      environment name: 'BRANCH_NAME', value: 'master'
                      environment name: 'BRANCH_NAME', value: 'rc'
                      expression { return params.FORCE_DEPLOY == true }
            } }
            steps {
                script {
                    env.MVN_ARGS=params.MVN_ARGS
                    if (params.ALT_DEPLOYMENT_REPOSITORY != '') {
                        env.MVN_ARGS="${env.MVN_ARGS} -DaltDeploymentRepository=${params.ALT_DEPLOYMENT_REPOSITORY}"
                    }
                    if (env.BRANCH_NAME == 'master') {
                        env.MVN_ARGS="${env.MVN_ARGS} -Possrh-deploy"
                    }
                }
                withMaven(maven: 'maven', mavenSettingsConfig: 'ossrh-settings-xml',
                          mavenOpts: '-DskipTests=true') {
                    sh "mvn deploy $MVN_ARGS"
                }
            }
        }
    }
}
