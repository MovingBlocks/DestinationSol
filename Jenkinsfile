node ("default-java") {
    stage('Checkout') {
        echo "Going to check out the things !"
        checkout scm
        sh 'chmod +x gradlew'
    }
    stage('Build') {
        // Jenkins sometimes doesn't run Gradle automatically in plain console mode, so make it explicit
        sh './gradlew --console=plain clean distZipBundleJres'
        archiveArtifacts 'desktop/build/distributions/DestinationSol.zip'
    }
    stage('Analytics') {
        sh "./gradlew --console=plain check javadoc"
    }
    stage('Record') {
        junit testResults: '**/build/test-results/test/*.xml',  allowEmptyResults: true
        recordIssues tool: javaDoc()
        step([$class: 'JavadocArchiver', javadocDir: 'engine/build/docs/javadoc', keepAll: false])
        recordIssues tool: checkStyle(pattern: '**/build/reports/checkstyle/*.xml')
        recordIssues tool: findBugs(pattern: '**/build/reports/findbugs/*.xml', useRankAsPriority: true)
        recordIssues tool: pmdParser(pattern: '**/build/reports/pmd/*.xml')
        recordIssues tool: taskScanner(includePattern: '**/*.java,**/*.groovy,**/*.gradle', lowTags: 'WIBNIF', normalTags: 'TODO', highTags: 'ASAP')
    }
    stage('Notify') {
        withCredentials([string(credentialsId: 'destsolDiscordWebhook', variable: 'webhook')]) {
            discordSend link: env.BUILD_URL, result: currentBuild.currentResult, webhookURL: ${webhook}
        }
    }
}
