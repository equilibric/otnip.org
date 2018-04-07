package org.otnip.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class Ratpack implements Plugin<Project> {

    void apply(Project project) {
        // load properties
        def properties = new Properties()
        properties.load(new File(project.rootDir, "gradle.properties").newReader())

        // apply plugin
        project.plugins.apply('io.ratpack.ratpack-java')

        // add dependencies
        project.dependencies.add("compile", "io.netty:netty-all:${properties['otnip_version_netty_all']}")
    }
}