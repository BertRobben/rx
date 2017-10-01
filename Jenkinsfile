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
        dir(path: 'oauth') {
          bat 'mvn clean install'
        }
        
        dir(path: 'oauth2.test') {
          bat 'mvn clean install'
          junit(allowEmptyResults: true, testResults: 'target/surefire-reports/**/*.xml')
        }
        
      }
      post {
        success {
          junit 'target/surefire-reports/**/*.xml'
          
        }
        
      }
    }
  }
  tools {
    maven 'Maven 3.5.0'
    jdk 'JDK 8u144'
  }
}