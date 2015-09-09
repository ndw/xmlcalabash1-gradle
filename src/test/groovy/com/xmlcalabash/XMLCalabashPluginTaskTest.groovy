package com.xmlcalabash.gradle

import com.xmlcalabash.XMLCalabashTask
import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class XMLCalabashPluginTest {
    @Test
    public void testPluginAddsXMLCalabashTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.xmlcalabash.task'

        assertTrue(project.tasks.comXmlCalabashTask instanceof XMLCalabashTask)
    }
}
