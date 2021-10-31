node ("android") {
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
    stage('Build Android') {
        sh 'echo sdk.dir=/opt/android-sdk > local.properties'
        dir('android') {
            // Allow varying from the default Android repo path for easier development. Assume same Android branch as engine branch.
            def androidGitPath = "https://github.com/MovingBlocks/DestSolAndroid.git"
            if (env.PUBLISH_ORG) {
                androidGitPath = androidGitPath.replace("MovingBlocks", env.PUBLISH_ORG)
                println "Updated target Android Git path to: " + androidGitPath
                println "And going to use branch " + env.BRANCH_NAME
            } else {
                println "Not varying the Android path from default " + androidGitPath
            }
            checkout scm: [$class: 'GitSCM', branches: [[name: env.BRANCH_NAME]], extensions: [], userRemoteConfigs: [[url: androidGitPath]]]
        }
        sh './gradlew --console=plain :android:assembleDebug'
        archiveArtifacts 'android/build/outputs/apk/debug/android-debug.apk'
    }
    stage('Record Android') {
       sh './gradlew --console=plain :android:lint'
       recordIssues tool: androidLintParser(pattern: 'android/build/reports/lint-results.xml')
    }
    stage('Notify') {
        withCredentials([string(credentialsId: 'destsolDiscordWebhook', variable: 'WEBHOOK')]) {
            discordSend title: env.BRANCH_NAME, link: env.BUILD_URL, result: currentBuild.currentResult, webhookURL: env.WEBHOOK
        }
    }
}
