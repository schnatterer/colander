#!groovy
/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Johannes Schnatterer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

node { // No specific label

    properties([
        // Keep only the last 10 build to preserve space
        //buildDiscarder(logRotator(numToKeepStr: '10')),
        [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '10']],
        // Configure GitHub project in order to start builds on push
        [$class: 'GithubProjectProperty', projectUrlStr: 'https://github.com/schnatterer/colander'],
        pipelineTriggers([[$class: 'GitHubPushTrigger']]),
        // Don't run concurrent builds for a branch, because they use the same workspace directory
        disableConcurrentBuilds()
    ])

    def CREDENTIALS = [
        $class       : 'StringBinding',
        credentialsId: 'sonarqube',
        variable     : 'authToken'
    ]

    //def sonarQubeVersion = 'sonar5-6'
    // TODO once withSonarQubeEnv closure works, use sonarQubeVersion and remove other SQ properties bellow
    String SONAR_MAVEN_PLUGIN_VERSION = '3.2'
    String SONAR_HOST_URL = env.SONAR_HOST

    String emailRecipients = env.EMAIL_RECIPIENTS

    catchError {

        def mvnHome = tool 'M3.3'
        def javaHome = tool 'JDK8'

        Maven mvn = new Maven(this, mvnHome, javaHome)
        if ("master".equals(env.BRANCH_NAME)) {
            mvn.additionalArgs = "-DperformRelease"
            currentBuild.description = mvn.getVersion()
        }

        stage('Checkout') {
            checkout scm
            gitClean()
        }

        stage('Build') {
            // Run the maven build
            mvn 'clean install -DskipTests'
            archive '**/target/*.jar,**/target/*.zip'
        }

        //parallel unitTests: {
        stage('Unit Test') {
            mvn 'test'
        }
        //}, integrationTests: {
        stage('Integration Test') {
            mvn 'verify -DskipUnitTests'
        }
        //}, failFast: true

        stage('SonarQube') {
            //withSonarQubeEnv(sonarQubeVersion) {
            // Results in this error https://issues.jenkins-ci.org/browse/JENKINS-39346
            // mvn "$SONAR_MAVEN_GOAL -Dsonar.host.url=$SONAR_HOST_URL",
            // // exclude generated code in target folder
            //  "-Dsonar.exclusions=target/**"
            //}

            withCredentials([CREDENTIALS]) {
                //noinspection GroovyAssignabilityCheck
                mvn "org.codehaus.mojo:sonar-maven-plugin:${SONAR_MAVEN_PLUGIN_VERSION}:sonar -Dsonar.host.url=${SONAR_HOST_URL} " +
                    "-Dsonar.login=$authToken " +
                    // Exclude generated code in target folder
                    "-Dsonar.exclusions=target/** "
                //+ sonarBranchProperty
            }
        }
    }

    // Archive Unit and integration test results, if any
    junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml,**/target/surefire-reports/TEST-*.xml'

    // email on fail
    step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: emailRecipients, sendToIndividuals: true])
}

class Maven implements Serializable {
    def mvnHome
    def javaHome
    def script

    // Args added to each mvn call
    String additionalArgs = ""

    Maven(script, mvnHome, javaHome) {
        this.script = script
        this.mvnHome = mvnHome
        this.javaHome = javaHome
    }

    def call(args) {
        mvn(args)
    }

    def mvn(String args) {
        // Apache Maven related side notes:
        // --batch-mode : recommended in CI to inform maven to not run in interactive mode (less logs)
        // -V : strongly recommended in CI, will display the JDK and Maven versions in use.
        //      Very useful to be quickly sure the selected versions were the ones you think.
        // -U : force maven to update snapshots each time (default : once an hour, makes no sense in CI).
        // -Dsurefire.useFile=false : useful in CI. Displays test errors in the logs directly (instead of
        //                            having to crawl the workspace files to see the cause).

        // Advice: don't define M2_HOME in general. Maven will autodetect its root fine.
        script.withEnv(["JAVA_HOME=${javaHome}", "PATH+MAVEN=${mvnHome}/bin:${script.env.JAVA_HOME}/bin"]) {
            script.sh "${mvnHome}/bin/mvn --batch-mode -V -U -e -Dsurefire.useFile=false --settings ${script.env.HOME}/.m2/settings.xml ${args + " " + additionalArgs}"
        }
    }

    String getVersion() {
        def matcher = script.readFile('pom.xml') =~ '<version>(.+)</version>'
        matcher ? matcher[0][1] : null
    }
}

void gitClean() {
    // Remove all untracked files
    sh "git clean -df"
    //Clear all unstaged changes
    sh 'git checkout -- .'
}
