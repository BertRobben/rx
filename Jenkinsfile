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
        tool(name: 'jdk', type: 'jdk8')
      }
    }
  }
}