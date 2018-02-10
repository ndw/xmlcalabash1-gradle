package com.xmlcalabash

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

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

        File pipeFile = new File("pipe.xpl")
        File srcFile = new File("source.xml")
        File resFile = new File ("result.xml")

        task.pipeline = pipeFile
        task.input("source", srcFile)
        task.output("result", resFile)
        def args = task.getArgs()

        URI baseURI = URI.create("file:/root")

        String srcOpt = "-isource=" + baseURI.resolve(srcFile.absolutePath).toASCIIString()
        String resOpt = "-oresult=" + baseURI.resolve(resFile.absolutePath).toASCIIString()

        // Reworked tests to avoid Java 8 String.join() method...
        assertTrue(args.get(0).equals(srcOpt) || args.get(0).equals(resOpt))
        assertTrue(args.get(1).equals(srcOpt) || args.get(1).equals(resOpt))
        assertTrue(!args.get(0).equals(args.get(1)))
    }
}
