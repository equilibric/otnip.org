package org.otnip.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class Kotlin implements Plugin<Project> {

    void apply(Project project) {
        // load properties
        def properties = new Properties()
        properties.load(new File(project.rootDir, "gradle.properties").newReader())

        // apply plugin
        project.plugins.apply('kotlin')

        // add core dependencies
        project.dependencies.add("compile", "org.jetbrains.kotlin:kotlin-stdlib:${properties['otnip_version_kotlin']}")
        project.dependencies.add("compile", "org.jetbrains.kotlin:kotlin-reflect:${properties['otnip_version_kotlin']}")

        // add aux dependencies
        project.dependencies.add("compile", "io.github.microutils:kotlin-logging:${properties['otnip_version_kotlin_logging']}")
        project.dependencies.add("compile", "com.fasterxml.jackson.module:jackson-module-kotlin:${properties['otnip_version_kotlin_jackson']}")
    }
}