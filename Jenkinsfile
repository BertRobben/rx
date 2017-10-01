pipeline {
  agent any
  stages {
    stage('First') {
      steps {
        echo 'Hello from the blue ocean!'
        bat '''
          path
          set
        '''
      }
    }
    stage('Build') {
      steps {
        bat(script: 'mvn clean install', returnStatus: true, returnStdout: true)
        bat(script: 'dir', returnStdout: true)
      }
    }
  }
  tools {
    maven 'Maven 3.5.0'
    jdk 'JDK 8u144'
  }
}