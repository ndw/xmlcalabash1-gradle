package com.xmlcalabash

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskInstantiationException
import org.gradle.process.JavaExecSpec

class XMLCalabashExec extends JavaExec {
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

    XMLCalabashExec() {
        super()
        super.setMain 'com.xmlcalabash.drivers.Main'
    }

    String getPipeline() {
        return pipeline
    }

    JavaExecSpec setPipeline(String pipeline) {
        this.pipeline = pipeline
        return this
    }

    boolean getDebugPipeline() {
        return debug
    }

    JavaExecSpec setDebugPipeline(boolean debug) {
        this.debug = debug
        return this
    }

    boolean getSafeMode() {
        return safeMode
    }

    JavaExecSpec setSafeMode(boolean safe) {
        this.safeMode = safe
        return this
    }

    String getProfilePipeline() {
        return profile
    }

    JavaExecSpec setProfilePipeline(String profile) {
        this.profile = profile
        return this
    }

    String getSaxonEdition() {
        return edition
    }

    JavaExecSpec setSaxonEdition(String edition) {
        this.edition = edition
        return this
    }

    boolean getSchemaAware() {
        return schemaAware
    }

    JavaExecSpec setSchemaAware(boolean aware) {
        schemaAware = aware
        return this
    }

    String getEntityResolver() {
        return entityResolver
    }

    JavaExecSpec setEntityResolver(String resolverClass) {
        entityResolver = resolverClass
        return this
    }

    String getUriResolver() {
        return uriResolver
    }

    JavaExecSpec setUriResolver(String resolverClass) {
        uriResolver = resolverClass
        return this
    }

    void namespaceBinding(String prefix, String uri) {
        nsBindings.put(prefix,uri)
    }

    String getConfigFile() {
        return configFile
    }

    JavaExecSpec setConfigFile(String config) {
        configFile = config
        return this
    }

    String getSaxonConfigFile() {
        return saxonConfigFile
    }

    JavaExecSpec setSaxonConfigFile(String config) {
        saxonConfigFile = config
        return this
    }

    String getLibrary() {
        return library
    }

    JavaExecSpec setLibrary(String library) {
        this.library = library
        return this
    }

    String getStep() {
        return step
    }

    JavaExecSpec setStep(String qname) {
        step = qname
        return this
    }

    String input(String port, String filename) {
        inputs.add("-i" + port + "=" + filename)
    }

    String dataInput(String port, String filename) {
        dataInput(port, filename, null)
    }

    String dataInput(String port, String filename, String contentType) {
        if (contentType == null) {
            inputs.add("-d" + port + "=" + filename)
        } else {
            inputs.add("-d" + port + "=" + contentType + "@" + filename)
        }
    }

    String output(String port, String filename) {
        outputs.add("-o" + port + "=" + filename)
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

    JavaExec setCalabashExtensions(Iterable<String> extensions) {
        this.extensions.clear()
        for (String s : extensions) {
            this.extensions.add(s)
        }
        return this
    }

    JavaExec setCalabashExtensions(String... extensions) {
        this.extensions.clear()
        for (String s : extensions) {
            this.extensions.add(s)
        }
        return this
    }

    @Override
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
            args.add("-b " + ns + "=" + nsBindings.get(ns))
        }

        if (getConfigFile() != null) {
            args.add("-c " + getConfigFile())
        }

        if (getSaxonConfigFile() != null) {
            args.add("--saxon-configuration " + getSaxonConfigFile())
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
            args.add("-E " + getEntityResolver())
        }

        if (getUriResolver() != null) {
            args.add("-U " + getUriResolver())
        }

        if (getLibrary() != null) {
            args.add("-l " + getLibrary())
        }

        if (getStep() != null) {
            args.add("-s " + getStep())
        }

        if (getProfilePipeline() != null) {
            args.add("--profile " + getProfilePipeline())
        }

        if (getSaxonEdition() != null) {
            args.add("-P " + getSaxonEdition())
        }

        if (getPipeline() == null) {
            throw notAllowed("You must specify a pipeline.")
        }

        args.add(getPipeline())

        for (String s : options) {
            args.add(s)
        }

        return args
    }

    @Override
    JavaExec setMain(final String main) {
        if (main == 'com.xmlcalabash.drivers.Main') {
            super.setMain(main)
        } else {
            throw notAllowed("Cannot change XMLCalabashExec main class to ${main}")
        }
    }

    @Override
    JavaExec setArgs(Iterable<?> applicationArgs) {
        throw notAllowed('Do not set args; use XMLCalabashExec methods instead')
    }

    @Override
    JavaExec args(Object... args) {
        throw notAllowed('Do not set args; use XMLCalabashExec methods instead')
    }

    @Override
    JavaExecSpec args(Iterable<?> args) {
        throw notAllowed('Do not set args; use XMLCalabashExec methods instead')
    }

    @Override
    void exec() {
        super.setArgs(getArgs())
        super.exec()
    }

    private static UnsupportedOperationException notAllowed(final String msg) {
        return new UnsupportedOperationException (msg)
    }
}
