pipeline {
  agent any
  stages {
    stage('First') {
      steps {
        echo 'Hello from the blue ocean!'
      }
    }
    stage('Build') {
      steps {
        tool 'Maven 3.5.0'
        tool 'JDK 8u144'
        bat(script: 'mvn clean install', returnStatus: true, returnStdout: true)
      }
    }
  }
}