pipeline {
    agent any
    parameters {
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip tests')
        booleanParam(name: 'FORCE_DEPLOY', defaultValue: false, description: 'Force deploy on feature branches')
        string(name: 'ALT_DEPLOYMENT_REPOSITORY', defaultValue: '', description: 'Alternative deployment repo')
        string(name: 'MVN_ARGS', defaultValue: '', description: 'Additional maven args')
        string(name: 'GPG_KEY_CREDENTIAL_ID', defaultValue: 'jenkins-jenkins-valuya-maven-deploy-gpg-key',
         description: 'Credential containing the private gpg key (pem)')
        string(name: 'GPG_KEY_FINGERPRINT', defaultValue: '98547E57F1690E2901E74CAB04EBAAA5BAB4A4DF',
         description: 'The fingerprint of this key to add to trust root')
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
                withCredentials([file(credentialsId: "${params.GPG_KEY_CREDENTIAL_ID}", variable: 'GPGKEY')]) {
                    sh 'gpg --allow-secret-key-import --import $GPGKEY'
                    sh "echo \"${params.GPG_KEY_FINGERPRINT}:6:\" | gpg --import-ownertrust"
                }
                withMaven(maven: 'maven', mavenSettingsConfig: 'ossrh-settings-xml') {
                    lock ('download-maven-plugin') {
                        sh "rm -rf ~/.m2/repository/.cache/download-maven-plugin"
                        sh "mvn $MVN_ARGS -Pwinbooks-parfilux-test -DskipTests=${params.SKIP_TESTS} clean compile install"
                    }
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
                    if (env.BRANCH_NAME == 'master' || params.FORCE_DEPLOY == true) {
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
