pipeline {
  agent any
  tools {
    jdk "JDK17"
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '3'))
    disableConcurrentBuilds()
    skipDefaultCheckout()
    timestamps()
  }
  stages {
    stage('Set build trigger') {
      steps {
        script {
          properties([
            pipelineTriggers([[$class: "GitHubPushTrigger"]])
          ])
        }
      }
    }
    stage('Checkout from GitHub') {
      steps {
        checkout scm
      }
    }
    stage('Build') {
      steps {
        script {
          withSonarQubeEnv("EA Sonar") {
            sh("./gradlew build sonarqube jacocoTestReport --no-daemon")
          }
        }
      }
    }
  }
}
