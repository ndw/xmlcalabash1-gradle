package com.xmlcalabash

import org.gradle.api.Project
import org.gradle.api.Plugin

class XMLCalabashExecPlugin implements Plugin<Project> {
    void apply(Project target) {
        target.task('comXmlCalabashExec', type: XMLCalabashExec)
    }
}
