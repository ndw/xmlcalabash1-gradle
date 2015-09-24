package com.xmlcalabash

import com.xmlcalabash.XMLCalabashExec
import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class XMLCalabashPluginExecTest {
    @Test
    public void testPluginAddsXMLCalabashTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.xmlcalabash.exec'

        assertTrue(project.tasks.comXmlCalabashExec instanceof XMLCalabashExec)
    }
}
