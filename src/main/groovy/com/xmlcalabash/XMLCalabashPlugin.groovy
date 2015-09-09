package com.xmlcalabash

import org.gradle.api.Project
import org.gradle.api.Plugin

class XMLCalabashPlugin implements Plugin<Project> {
    void apply(Project target) {
        target.task('comXmlCalabashTask', type: XMLCalabashTask)
    }
}
