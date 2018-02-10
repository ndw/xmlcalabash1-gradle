package com.xmlcalabash

import com.xmlcalabash.drivers.Main
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.TaskAction

class XMLCalabashTask extends ConventionTask {
    private boolean schemaAware = false
    private boolean safeMode = false
    private boolean debug = false
    private String pipeline = null
    private Hashtable<String,String> nsBindings = new Hashtable<String,String> ()
    private String configFile = null
    private String saxonConfigFile = null
    private Vector<String> inputs = new Vector<String> ()
    private Vector<String> outputs = new Vector<String> ()
    private Vector<String> options = new Vector<String> ()
    private Vector<String> params = new Vector<String> ()
    private Vector<String> extensions = new Vector<String> ()
    private String entityResolver = null
    private String uriResolver = null
    private String library = null
    private String step = null
    private String profile = null
    private String edition = null

    private URI baseURI = project.getProjectDir().toURI();

    XMLCalabashTask() {
    }

    String getPipeline() {
        return pipeline
    }

    def setPipeline(String pipeline) {
        this.pipeline = baseURI.resolve(pipeline).toASCIIString()
        return this
    }

    boolean getDebugPipeline() {
        return debug
    }

    def setDebugPipeline(boolean debug) {
        this.debug = debug
        return this
    }

    boolean getSafeMode() {
        return safeMode
    }

    def setSafeMode(boolean safe) {
        this.safeMode = safe
        return this
    }

    String getProfilePipeline() {
        return profile
    }

    def setProfilePipeline(String profile) {
        this.profile = profile
        return this
    }

    String getSaxonEdition() {
        return edition
    }

    def setSaxonEdition(String edition) {
        this.edition = edition
        return this
    }

    boolean getSchemaAware() {
        return schemaAware
    }

    def setSchemaAware(boolean aware) {
        schemaAware = aware
        return this
    }

    String getEntityResolver() {
        return entityResolver
    }

    def setEntityResolver(String resolverClass) {
        entityResolver = resolverClass
        return this
    }

    String getUriResolver() {
        return uriResolver
    }

    def setUriResolver(String resolverClass) {
        uriResolver = resolverClass
        return this
    }

    void namespaceBinding(String prefix, String uri) {
        nsBindings.put(prefix,uri)
    }

    String getConfigFile() {
        return configFile
    }

    def setConfigFile(String config) {
        configFile = config
        return this
    }

    String getSaxonConfigFile() {
        return saxonConfigFile
    }

    def setSaxonConfigFile(String config) {
        saxonConfigFile = config
        return this
    }

    String getLibrary() {
        return library
    }

    def setLibrary(String library) {
        this.library = baseURI.resolve(library).toASCIIString()
        return this
    }

    String getStep() {
        return step
    }

    def setStep(String qname) {
        step = qname
        return this
    }

    String input(String port, File file) {
        input(port, file.absolutePath)
    }

    String input(String port, String filename) {
        inputs.add("-i" + port + "=" + baseURI.resolve(filename).toASCIIString())
    }

    String dataInput(String port, File file) {
        dataInput(port, file.absolutePath)
    }

    String dataInput(String port, String filename) {
        dataInput(port, baseURI.resolve(filename).toASCIIString(), null)
    }

    String dataInput(String port, String filename, String contentType) {
        String fn = baseURI.resolve(filename).toASCIIString()

        if (contentType == null) {
            inputs.add("-d" + port + "=" + fn)
        } else {
            inputs.add("-d" + port + "=" + contentType + "@" + fn)
        }
    }

    String output(String port, File file) {
        output(port, file.absolutePath)
    }

    String output(String port, String filename) {
        outputs.add("-o" + port + "=" + baseURI.resolve(filename).toASCIIString())
    }

    String param(String qname, String value) {
        param(qname, value, null)
    }

    String param(String qname, String value, String port) {
        if (port == null) {
            params.add("-p" + qname + "=" + value)
        } else {
            params.add("-p" + qname + "=" + port + "@" + value)
        }
    }

    String option(String qname, String value) {
        options.add(qname + "=" + value)
    }

    List getCalabashExtensions() {
        return extensions
    }

    def setCalabashExtensions(Iterable<String> extensions) {
        this.extensions.clear()
        for (String s : extensions) {
            this.extensions.add(s)
        }
        return this
    }

    def setCalabashExtensions(String... extensions) {
        this.extensions.clear()
        for (String s : extensions) {
            this.extensions.add(s)
        }
        return this
    }

    List<String> getArgs() {
        Vector<String> args = new Vector<String> ()

        if (getSchemaAware()) {
            args.add("-a")
        }

        if (getSafeMode()) {
            args.add("-S")
        }

        if (getDebugPipeline()) {
            args.add("-D")
        }

        for (String ns : nsBindings.keySet()) {
            args.add("-b")
            args.add(ns + "=" + nsBindings.get(ns))
        }

        if (getConfigFile() != null) {
            args.add("-c")
            args.add(getConfigFile())
        }

        if (getSaxonConfigFile() != null) {
            args.add("--saxon-configuration")
            args.add(getSaxonConfigFile())
        }

        for (String s : inputs) {
            args.add(s)
        }

        for (String s : outputs) {
            args.add(s)
        }

        for (String s : params) {
            args.add(s)
        }

        for (String s : extensions) {
            args.add("-X" + s)
        }

        if (getEntityResolver() != null) {
            args.add("-E")
            args.add(getEntityResolver())
        }

        if (getUriResolver() != null) {
            args.add("-U")
            args.add(getUriResolver())
        }

        if (getLibrary() != null) {
            args.add("-l")
            args.add(getLibrary())
        }

        if (getStep() != null) {
            args.add("-s" + getStep())
        }

        if (getProfilePipeline() != null) {
            args.add("--profile")
            args.add(getProfilePipeline())
        }

        if (getSaxonEdition() != null) {
            args.add("-P")
            args.add(getSaxonEdition())
        }

        if (getPipeline() == null && getStep() == null) {
            throw notAllowed("You must specify a pipeline or a step.")
        }

        if (getPipeline() != null && getStep() != null) {
            throw notAllowed("You must specify either a pipeline or a step.")
        }

        if (getPipeline() != null) {
            args.add(getPipeline())
        }

        for (String s : options) {
            args.add(s)
        }

        return args
    }

    @TaskAction
    void exec() {
        // This is a complete hack; I have code (XMLCalabashExec) that handles making an arguments
        // list for the command line, so let's just reuse that. And since we're doing a complete
        // hack, we'll just instantiate the driver, shall we?
        Main main = new Main()
        main.runMethod(getArgs().toArray(new String[0]))
    }

    private static UnsupportedOperationException notAllowed(final String msg) {
        return new UnsupportedOperationException (msg)
    }

}
