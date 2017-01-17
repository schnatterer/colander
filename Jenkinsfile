#!groovy

node { // No specific label

    properties([
        // Keep only the last 10 build to preserve space
        //buildDiscarder(logRotator(numToKeepStr: '10')),
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

        mvnHome = tool 'M3.3'
        javaHome = tool 'JDK8'

        stage('Checkout') {
            checkout scm
            gitClean()
        }

        stage('Build') {
            // Run the maven build
            mvn 'clean install -DskipTests'
            archive '**/target/*.jar'
        }

        stage('Test') {
            // Archive JUnit results using special step for pipeline plugin
            mvn 'surefire:test'
            // Archive JUnit results, if any
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
        }

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

    // email on fail
    step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: emailRecipients, sendToIndividuals: true])
}

def mvnHome
def javaHome

def mvn(def args) {
    // Apache Maven related side notes:
    // --batch-mode : recommended in CI to inform maven to not run in interactive mode (less logs)
    // -V : strongly recommended in CI, will display the JDK and Maven versions in use.
    //      Very useful to be quickly sure the selected versions were the ones you think.
    // -U : force maven to update snapshots each time (default : once an hour, makes no sense in CI).
    // -Dsurefire.useFile=false : useful in CI. Displays test errors in the logs directly (instead of
    //                            having to crawl the workspace files to see the cause).

    // Advice: don't define M2_HOME in general. Maven will autodetect its root fine.
    withEnv(["JAVA_HOME=${javaHome}", "PATH+MAVEN=${mvnHome}/bin:${env.JAVA_HOME}/bin"]) {
        sh "${mvnHome}/bin/mvn ${args} --batch-mode -V -U -e -Dsurefire.useFile=false --settings ${env.HOME}/.m2/settings.xml ${args}"
    }
}

void gitClean() {
    // Remove all untracked files
    sh "git clean -df"
    //Clear all unstaged changes
    sh 'git checkout -- .'
}
