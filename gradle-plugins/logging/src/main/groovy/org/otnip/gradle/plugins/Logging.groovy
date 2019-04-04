package org.otnip.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class Logging implements Plugin<Project> {

    void apply(Project project) {
        // load properties
        def properties = new Properties()
        properties.load(new File(project.rootDir, "gradle.properties").newReader())

        // add dependencies
        // project.dependencies.add("compile", "org.slf4j:slf4j-simple:${properties['otnip_version_slf4j']}")
        project.dependencies.add("compile", "org.slf4j:slf4j-jdk14:${properties['otnip_version_slf4j']}")
    }
}