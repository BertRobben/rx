pipeline {
  agent any
  tools {
    maven 'Maven 3.5.0'
    jdk 'JDK 8u144'
  }
  stages {
    stage('First') {
      steps {
        echo 'Hello from the blue ocean!'
        path
        set
      }
    }
    stage('Build') {
      steps {
        bat(script: 'mvn clean install', returnStatus: true, returnStdout: true)
      }
    }
  }
}
