pipeline {
    agent {
        kubernetes {
            label 'android'
            defaultContainer 'builder' // Use actual container with Android SDK present
        }
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
                sh './gradlew clean distZipBundleJres'
                archiveArtifacts 'desktop/build/distributions/DestinationSol.zip'
            }
        }
        stage('Analytics') {
            steps {
                sh "./gradlew check javadoc"
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
                // Set the ANDROID_HOME environment variable
                withEnv(['ANDROID_HOME=/opt/android-sdk']) {

                    writeFile file: 'local.properties', text: 'sdk.dir=/opt/android-sdk'

                    sh 'echo "ANDROID_HOME is: $ANDROID_HOME"'
                    sh 'pwd'
                    sh 'ls -la'
                    sh 'cat local.properties'
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
                            // Figure out a suitable target branch in the Android repo, default is the develop branch
                            def androidBranch = "develop"
                            // Check to see if Jenkins is building a tag, branch, or other (including PRs)
                            if (env.TAG_NAME != null && env.TAG_NAME ==~ /v\d+\.\d+\.\d+.*/) {
                                println "Going to use target Android tag " + env.TAG_NAME
                                androidBranch = "refs/tags/" + env.TAG_NAME
                            } else if (env.BRANCH_NAME.equalsIgnoreCase("master") || env.BRANCH_NAME.startsWith("android/")) {
                                println "Going to use target unusual Android branch " + env.BRANCH_NAME
                                androidBranch = env.BRANCH_NAME
                            } else {
                                println "Going to use target Android branch 'develop' - not building 'master' nor anything starting with 'android/'"
                            }
                            checkout scm: [$class: 'GitSCM', branches: [[name: androidBranch]], extensions: [], userRemoteConfigs: [[credentialsId: 'GooeyHub', url: androidGitPath]]]
                        }
                    }
                    sh './gradlew :android:assembleDebug'
                }
                archiveArtifacts 'android/build/outputs/apk/debug/android-debug.apk'
            }
        }
        stage('Record Android') {
            steps {
               sh './gradlew :android:lint'
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
        stage('Build Steam') {
            when {
                // Example: v2.1.0
                tag pattern: 'v\\d+\\.\\d+\\.\\d+.*', comparator: "REGEXP"
                branch pattern: 'steam/*'
            }
            steps {
                dir('steam') {
                    script {
                        // Allow varying from the default Steam repo path for easier development. Assume same Steam branch as engine branch.
                        def steamGitPath = "https://github.com/MovingBlocks/DestSolSteam.git"
                        if (env.PUBLISH_ORG) {
                            steamGitPath = steamGitPath.replace("MovingBlocks", env.PUBLISH_ORG)
                            println "Updated target Steam Git path to: " + steamGitPath
                        } else {
                            println "Not varying the Steam path from default " + steamGitPath
                        }
                        // Figure out a suitable target branch in the Steam repo, default is the develop branch
                        def steamBranch = "develop"
                        // Check to see if Jenkins is building a tag, branch, or other (including PRs)
                        if (env.TAG_NAME != null && env.TAG_NAME ==~ /v\d+\.\d+\.\d+.*/) {
                            println "Going to use target Steam tag " + env.TAG_NAME
                            steamBranch = "refs/tags/" + env.TAG_NAME
                        } else if (env.BRANCH_NAME.equalsIgnoreCase("master") || env.BRANCH_NAME.startsWith("steam/")) {
                            println "Going to use target unusual Steam branch " + env.BRANCH_NAME
                            steamBranch = env.BRANCH_NAME
                        } else {
                            println "Going to use target Steam branch 'develop' - not building 'master' nor anything starting with 'android/'"
                        }
                        checkout scm: [$class: 'GitSCM', branches: [[name: steamBranch]], extensions: [], userRemoteConfigs: [[credentialsId: 'GooeyHub', url: steamGitPath]]]
                    }
                }
                sh './gradlew :steam:distZip'
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
                    steps {
                        dir('android') {
                            sh 'fastlane buildRelease'
                            archiveArtifacts 'build/outputs/apk/release/android-release.apk'
                        }
                    }
                }
                // Leaving this stage inside a parallel as only one can ever be true at once (pipeline visualization aesthetics!)
                stage('Target release channel') {
                    parallel {
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
                        stage('Publish Live To Play Store') {
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
    }
}
