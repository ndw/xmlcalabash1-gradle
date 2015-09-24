package com.xmlcalabash

import com.xmlcalabash.XMLCalabashTask

import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class XMLCalabashTaskTest {
    @Test
    public void canAddTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        def task = project.task('calabash', type: XMLCalabashTask)
        assertTrue(task instanceof XMLCalabashTask)
    }
}
