package com.xmlcalabash

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

class XMLCalabashTaskTest {
    @Test
    public void canAddTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        def task = project.task('calabash', type: XMLCalabashTask)
        assertTrue(task instanceof XMLCalabashTask)
    }

    @Test
    public void canUseFile() {
        Project project = ProjectBuilder.builder().build()
        def task = project.task('calabash', type: XMLCalabashTask)

        String cwd = System.getProperty("user.dir")
        File pipeFile = new File(cwd + "/src/test/resources/pipe.xpl")
        File srcFile = new File(cwd + "/src/test/resources/source.xml")
        File resFile = new File (cwd + "/build/test-canUseFile-output.xml")

        task.pipeline = pipeFile
        task.input("source", srcFile)
        task.output("result", resFile)

        // FIXME: this test is now vacuous.
    }
}
