package com.xmlcalabash

import com.xmlcalabash.XMLCalabashExec

import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class XMLCalabashExecTest {
    @Test
    public void canAddTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        def task = project.task('calabash', type: XMLCalabashExec)
        assertTrue(task instanceof XMLCalabashExec)
    }
}
