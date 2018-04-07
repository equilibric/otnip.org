package org.otnip.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class Webpack implements Plugin<Project> {
    void apply(Project project) {
        project.afterEvaluate {
            // webpack tasks
            project.task('webpackBuild', group: 'WebPack') {
                doLast {
                    // npm build
                    def npmBuildOutput = "npm run build".execute(null, project.projectDir).text
                    println(npmBuildOutput)

                    // files
                    def baseSrc = project.file("src/main/webpack")
                    def baseDest = project.file("build/webpack/assets")
                    baseSrc.eachFileRecurse { srcFile ->
                        def srcName = srcFile.name
                        def copyFile = srcName.endsWith(".html") || srcName.endsWith(".png") || srcName.endsWith(".svg") || srcName.endsWith(".jpg") ||srcName.endsWith(".json") || srcName.endsWith(".ico")
                        if (copyFile) {
                            def relativePath = srcFile.toString().substring(baseSrc.toString().length())
                            def destFile = srcName.endsWith("favicon.ico") ? new File(baseDest, "../$relativePath") : new File(baseDest, relativePath)

                            destFile.parentFile.mkdirs()
                            destFile.bytes = srcFile.bytes

                            println("...copying : " + destFile)
                        }
                    }
                }
            }

            project.task('webpackInstall', group: 'WebPack') {
                doLast {
                    def npmBuildOutput = "npm i".execute(null, project.projectDir).text
                    println(npmBuildOutput)
                }
            }

            project.task('webpackClean', group: 'WebPack') {
                doLast {
                    project.file("node_modules").deleteDir()
                }
            }

            // augment jar task
            project.tasks.jar {
                dependsOn('webpackBuild')

                doFirst {
                    def ratpackFolder = project.file("build/resources/main/.ratpack")
                    def httpBuildFolder = project.file("build/webpack")

                    if ( ratpackFolder.exists() ) {
                        ratpackFolder.deleteDir()
                    }
                    httpBuildFolder.renameTo(ratpackFolder)
                }
            }
        }
    }
}