pipeline {
    agent {
        label 'android'
    }
    stages {
        stage('Setup') {
            steps {
                echo "Repository checkout complete!"
                sh 'chmod +x gradlew'
            }
        }
        stage('Build') {
            steps {
                // Jenkins sometimes doesn't run Gradle automatically in plain console mode, so make it explicit
                sh './gradlew --console=plain clean distZipBundleJres'
                archiveArtifacts 'desktop/build/distributions/DestinationSol.zip'
            }
        }
        stage('Analytics') {
            steps {
                sh "./gradlew --console=plain check javadoc"
            }
        }
        stage('Record') {
            steps {
                junit testResults: '**/build/test-results/test/*.xml',  allowEmptyResults: true
                recordIssues tool: javaDoc()
                step([$class: 'JavadocArchiver', javadocDir: 'engine/build/docs/javadoc', keepAll: false])
                recordIssues tool: checkStyle(pattern: '**/build/reports/checkstyle/*.xml')
                recordIssues tool: findBugs(pattern: '**/build/reports/findbugs/*.xml', useRankAsPriority: true)
                recordIssues tool: pmdParser(pattern: '**/build/reports/pmd/*.xml')
                recordIssues tool: taskScanner(includePattern: '**/*.java,**/*.groovy,**/*.gradle', lowTags: 'WIBNIF', normalTags: 'TODO', highTags: 'ASAP')
            }
        }
        stage('Build Android') {
            steps {
                sh 'echo sdk.dir=/opt/android-sdk > local.properties'
                dir('android') {
                    script {
                        // Allow varying from the default Android repo path for easier development. Assume same Android branch as engine branch.
                        def androidGitPath = "https://github.com/MovingBlocks/DestSolAndroid.git"
                        if (env.PUBLISH_ORG) {
                            androidGitPath = androidGitPath.replace("MovingBlocks", env.PUBLISH_ORG)
                            println "Updated target Android Git path to: " + androidGitPath
                        } else {
                            println "Not varying the Android path from default " + androidGitPath
                        }
                        // Figure out a suitable target brand in the Android repo
                        def androidBranch = "develop"
                        if (env.BRANCH_NAME.equalsIgnoreCase("master") || env.BRANCH_NAME.startsWith("android/")) {
                            println "Going to use target unusual Android branch " + env.BRANCH_NAME
                            androidBranch = env.BRANCH_NAME
                        } else {
                            println "Going to use target Android branch 'develop' - not building 'master' nor anything starting with 'android/'"
                        }
                        checkout scm: [$class: 'GitSCM', branches: [[name: androidBranch]], extensions: [], userRemoteConfigs: [[credentialsId: 'GooeyHub', url: androidGitPath]]]
                    }
                }
                sh './gradlew --console=plain :android:assembleDebug'
                archiveArtifacts 'android/build/outputs/apk/debug/android-debug.apk'
            }
        }
        stage('Record Android') {
            steps {
               sh './gradlew --console=plain :android:lint'
               recordIssues tool: androidLintParser(pattern: 'android/build/reports/lint-results.xml')
           }
        }
        stage('Notify') {
            environment {
                WEBHOOK = credentials('destsolDiscordWebhook')
            }
            steps {
                discordSend title: env.BRANCH_NAME, link: env.BUILD_URL, result: currentBuild.currentResult, webhookURL: env.WEBHOOK
            }
        }
        stage('Publish to Play Store') {
            when {
                // Example: v2.1.0
                tag pattern: 'v\\d+\\.\\d+\\.\\d+.*', comparator: "REGEXP"
            }
            environment {
                DESTSOL_ANDROID_SIGNING_KEYSTORE=credentials('destsol-signing-keystore')
                DESTSOL_ANDROID_SIGNING_STORE_PASS=credentials('destsol-keystore-pass')
                DESTSOL_ANDROID_SIGNING_KEY_ALIAS=credentials('destsol-signing-alias')
                DESTSOL_ANDROID_SINGING_KEY_PASS=credentials('destsol-signing-pass')
                DESTSOL_PLAYSTORE_SECRET=credentials('destsol-playstore-secret')
                WEBHOOK = credentials('destsolDiscordWebhook')
            }
            stages {
                stage('Prepare Fastlane') {
                    steps {
                        dir('android') {
                            sh 'ln -s $DESTSOL_PLAYSTORE_SECRET playstore_secret.json'
                        }
                    }
                }
                stage('Create Release APK') {
                    sh 'fastlane buildRelease'
                    archiveArtifacts 'android/build/outputs/apk/release/android-release.apk'
                }
                stage('Publish Alpha To Play Store') {
                    when {
                        // Example: v2.1.0-alpha
                        tag pattern: 'v\\d+\\.\\d+\\.\\d+-alpha$', comparator: "REGEXP"
                    }
                    steps {
                        dir('android') {
                            sh 'fastlane deployAlpha'
                        }
                    }
                }
                stage('Publish Beta To Play Store') {
                    when {
                        // Example: v2.1.0-beta
                        tag pattern: 'v\\d+\\.\\d+\\.\\d+-beta$', comparator: "REGEXP"
                    }
                    steps {
                        dir('android') {
                            sh 'fastlane deployBeta'
                        }
                        discordSend title: "Beta ${env.GIT_TAG} published to Play Store", result: currentBuild.currentResult, webhookURL: env.WEBHOOK
                    }
                }
                stage('Publish Release To Play Store') {
                    when {
                        // Example: v2.1.0
                        // This intentionally does not include things like v2.1.0-beta
                        tag pattern: 'v\\d+\\.\\d+\\.\\d+$', comparator: "REGEXP"
                    }
                    steps {
                        dir('android') {
                            sh 'fastlane deployProduction'
                        }
                        discordSend title: "Release ${env.GIT_TAG} published to Play Store", result: currentBuild.currentResult, webhookURL: env.WEBHOOK
                    }
                }
            }
        }
    }
}
