package com.xmlcalabash

import com.xmlcalabash.XMLCalabashExec

import java.io.File;
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

    @Test
    public void canUseFile() {
        Project project = ProjectBuilder.builder().build()
        def task = project.task('calabash', type: XMLCalabashExec)
        task.pipeline = new File("pipe.xpl")
        task.input("source", new File("source.xml"))
        task.output("result", new File("result.xml"))
        def args = task.getArgs()
        assertTrue(String.join(" ", args), args.contains("-isource=" + new File("source.xml").absolutePath))
        assertTrue(String.join(" ", args), args.contains("-oresult=" + new File("result.xml").absolutePath))
    }
}
