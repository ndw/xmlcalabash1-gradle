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
        task.pipeline = new File("pipe.xpl")
        task.input("source", new File("source.xml"))
        task.output("result", new File("result.xml"))
        def args = task.getArgs()
        //assertTrue(String.join(" ", args), args.contains("-isource=" + new File("source.xml").absolutePath))
        //assertTrue(String.join(" ", args), args.contains("-oresult=" + new File("result.xml").absolutePath))
        // Reworked tests to avoid Java 8 String.join() method...
        assertTrue(args.get(0).equals("-isource=" + new File("source.xml").absolutePath)
                || args.get(0).equals("-oresult=" + new File("result.xml").absolutePath))
        assertTrue(args.get(1).equals("-isource=" + new File("source.xml").absolutePath)
                || args.get(1).equals("-oresult=" + new File("result.xml").absolutePath))
        assertTrue(!args.get(0).equals(args.get(1)))
    }

}
