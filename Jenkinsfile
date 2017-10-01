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
        tool(name: 'maven', type: 'Maven 3.3.9')
        tool(name: 'jdk', type: 'jdk8')
      }
    }
  }
}