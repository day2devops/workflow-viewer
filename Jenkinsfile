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
    stage('Build') {
      steps {
        script {
          sh("./gradlew build -x test")
        {
      }
    }
  }
}
