package com.xmlcalabash

import com.xmlcalabash.core.XProcConfiguration
import com.xmlcalabash.core.XProcException
import com.xmlcalabash.core.XProcRuntime
import com.xmlcalabash.io.ReadableData
import com.xmlcalabash.io.ReadablePipe
import com.xmlcalabash.io.WritableDocument
import com.xmlcalabash.model.RuntimeValue
import com.xmlcalabash.model.Serialization
import com.xmlcalabash.runtime.XPipeline
import com.xmlcalabash.util.Closer
import com.xmlcalabash.util.Input
import com.xmlcalabash.util.Output
import com.xmlcalabash.util.UserArgs
import net.sf.saxon.s9api.QName
import net.sf.saxon.s9api.XdmNode
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.TaskAction
import org.xml.sax.InputSource

import static com.xmlcalabash.core.XProcConstants.c_data
import static java.lang.String.format

class XMLCalabashTask extends ConventionTask {
    protected boolean schemaAware = false
    protected boolean safeMode = false
    protected boolean debug = false
    protected String pipelineURI = null
    protected Hashtable<String,String> nsBindings = new Hashtable<String,String> ()
    protected String configFile = null
    protected String saxonConfigFile = null
    protected String entityResolver = null
    protected String uriResolver = null
    protected String library = null          // Should be a list...
    protected String step = null
    protected String profile = null
    protected String edition = null
    protected boolean extensionValues = false
    protected boolean xpointerOnText = false
    protected boolean transparentJson = false
    protected String jsonFlavor = null
    protected boolean allowTextResults = false
    protected boolean useXslt10 = false
    protected boolean htmlSerializer = false;
    protected UserArgs userArgs = null
    protected Hashtable<String,String> seenOptions = new Hashtable<String, String> ();
    protected URI baseURI = project.getProjectDir().toURI();

    protected XProcConfiguration xprocConfiguration = null
    protected XProcRuntime runtime = null
    protected XPipeline pipeline = null
    protected Map<String, Output> portOutputs = null

    XMLCalabashTask() {
        userArgs = new UserArgs()
    }

    String getPipeline() {
        return pipelineURI
    }

    def setPipeline(String pipeline) {
        pipelineURI = baseURI.resolve(pipeline).toASCIIString()
        getInputs().file(pipelineURI)
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
        if (edition == null) {
            return "he"
        } else {
            return edition
        }
    }

    def setSaxonEdition(String edition) {
        this.edition = edition
        return this
    }

    String getProcessor() {
        return proctype
    }

    def setProcessor(String ptype) {
        proctype = ptype
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

    /* Remove step property until issue #4 is fixed
    def setStep(String qname) {
        step = qname
        return this
    }
    */

    def input(String port, File file) {
        input(port, file.getAbsolutePath())
    }

    def input(String port, String filename) {
        String fn = baseURI.resolve(filename).toASCIIString()

        getInputs().file(fn)

        userArgs.addInput(port, fn, Input.Type.XML)
        return this
    }

    def dataInput(String port, File file) {
        dataInput(port, file.absolutePath)
    }

    def dataInput(String port, String filename) {
        dataInput(port, filename, null)
    }

    def dataInput(String port, String filename, String contentType) {
        String fn = baseURI.resolve(filename).toASCIIString()

        getInputs().file(fn)

        if (contentType == null) {
            userArgs.addInput(port, fn, Input.Type.DATA)
        } else {
            userArgs.addInput(port, fn, Input.Type.DATA, contentType)
        }

        return this
    }

    def output(String port, File file) {
        output(port, file.absolutePath)
    }

    def output(String port, String filename) {
        String fn = baseURI.resolve(filename).toASCIIString()

        getOutputs().file(fn)

        userArgs.addOutput(port, fn)
        return this
    }

    def param(String qname, String value) {
        param(qname, value, null)
    }

    def param(String qname, String value, String port) {
        if (port == null) {
            userArgs.addParam(qname, value)
        } else {
            userArgs.addParam(port, qname, value)
        }
        return this
    }

    def option(String qname, String value) {
        userArgs.addOption(qname, value)
        seenOptions.put(qname, value)
        return this
    }

    def setExtensionValues(boolean value) {
        extensionValues = value
        return this
    }

    boolean getExtensionValues() {
        return extensionValues
    }

    def setXPointerOnText(boolean value) {
        xpointerOnText = value
        return this
    }

    boolean getXPointerOnText() {
        return xpointerOnText
    }

    def setTransparentJson(boolean value) {
        transparentJson = value
        return this
    }

    boolean getTransparentJson() {
        return transparentJson
    }

    def setJsonFlavor(String value) {
        jsonFlavor = value
        return this
    }

    String getJsonFlavor() {
        return jsonFlavor
    }

    def setAllowTextResults(boolean value) {
        allowTextResults = value
        return this
    }

    boolean getAllowTextResults() {
        return allowTextResults
    }

    def setUseXslt10(boolean value) {
        useXslt10 = value
        return this
    }

    boolean getUseXslt10() {
        return useXslt10
    }

    def setHtmlSerializer(boolean value) {
        htmlSerializer = value
        return this
    }

    boolean getHtmlSerializer() {
        return htmlSerializer
    }

    protected void setupRuntime() {
        if (getPipeline() == null && getStep() == null) {
            throw notAllowed("You must specify a pipeline or a step.")
        }

        if (getPipeline() != null && getStep() != null) {
            throw notAllowed("You must specify either a pipeline or a step.")
        }

        // On reflection, I'm not sure XML Calabash is well designed for this kind of embedding
        userArgs.setSaxonProcessor(getSaxonEdition())
        userArgs.setSchemaAware(getSchemaAware())
        userArgs.setSafeMode(getSafeMode())
        userArgs.setDebug(getDebugPipeline())
        userArgs.setExtensionValues(getExtensionValues())
        userArgs.setAllowXPointerOnText(getXPointerOnText())
        userArgs.setTransparentJSON(getTransparentJson())
        userArgs.setAllowTextResults(getAllowTextResults())
        userArgs.setUseXslt10(getUseXslt10())
        userArgs.setHtmlSerializer(getHtmlSerializer())

        for (pfx in nsBindings.keySet()) {
            userArgs.addBinding(pfx, nsBindings.get(pfx))
        }

        if (getLibrary() != null) {
            userArgs.addLibrary(getLibrary())
        }

        if (getPipeline() != null) {
            userArgs.setPipeline(getPipeline())
        }

        if (getStep() != null) {
            userArgs.setCurStepName(getStep())
        }

        if (getConfigFile() != null) {
            userArgs.setConfig(getConfigFile())
        }

        if (getSaxonConfigFile() != null) {
            userArgs.setSaxonConfig(getSaxonConfigFile())
        }

        if (getEntityResolver() != null) {
            userArgs.setEntityResolverClass(getEntityResolver())
        }

        if (getUriResolver() != null) {
            userArgs.setUriResolverClass(getUriResolver())
        }

        if (getProfilePipeline() != null) {
            userArgs.setProfile(profile)
        }

        if (getJsonFlavor() != null) {
            userArgs.setJsonFlavor(getJsonFlavor())
        }

        xprocConfiguration = userArgs.createConfiguration()
        runtime = new XProcRuntime(xprocConfiguration);
    }

    protected void processInputs() {
        setupRuntime()
        pipeline = runtime.load(userArgs.getPipeline())

        // Process parameters from the configuration...
        for (String port : xprocConfiguration.params.keySet()) {
            Map<QName, String> parameters = xprocConfiguration.params.get(port);
            setParametersOnPipeline(pipeline, port, parameters);
        }

        // Now process parameters from the command line...
        for (String port : userArgs.getParameterPorts()) {
            Map<QName, String> parameters = userArgs.getParameters(port);
            setParametersOnPipeline(pipeline, port, parameters);
        }

        Set<String> ports = pipeline.getInputs();
        Set<String> userArgsInputPorts = userArgs.getInputPorts();
        Set<String> cfgInputPorts = xprocConfiguration.inputs.keySet();
        Set<String> allPorts = new HashSet<String>();
        allPorts.addAll(userArgsInputPorts);
        allPorts.addAll(cfgInputPorts);

        // map a given input without port specification to the primary non-parameter input implicitly
        for (String port : ports) {
            if (!allPorts.contains(port) && allPorts.contains(null)
                    && pipeline.getDeclareStep().getInput(port).getPrimary()
                    && !pipeline.getDeclareStep().getInput(port).getParameterInput()) {

                if (userArgsInputPorts.contains(null)) {
                    userArgs.setDefaultInputPort(port);
                    allPorts.remove(null);
                    allPorts.add(port);
                }
                break;
            }
        }

        for (String port : allPorts) {
            if (!ports.contains(port)) {
                throw new XProcException("There is a binding for the port '" + port + "' but the pipeline declares no such port.");
            }

            pipeline.clearInputs(port);

            if (userArgsInputPorts.contains(port)) {
                XdmNode doc = null;
                for (Input input : userArgs.getInputs(port)) {
                    switch (input.getType()) {
                        case Input.Type.XML:
                            switch (input.getKind()) {
                                case Input.Kind.URI:
                                    String uri = input.getUri();
                                    if ("-".equals(uri)) {
                                        doc = runtime.parse(new InputSource(System.in));
                                    } else {
                                        doc = runtime.parse(new InputSource(uri));
                                    }
                                    break;

                                case Input.Kind.INPUT_STREAM:
                                    InputStream inputStream = input.getInputStream();
                                    try {
                                        doc = runtime.parse(new InputSource(inputStream));
                                    } finally {
                                        Closer.close(inputStream);
                                    }
                                    break;

                                default:
                                    throw new UnsupportedOperationException(format("Unsupported input kind '%s'", input.getKind()));
                            }
                            break;

                        case Input.Type.DATA:
                            ReadableData rd;
                            switch (input.getKind()) {
                                case Input.Kind.URI:
                                    rd = new ReadableData(runtime, c_data, input.getUri(), input.getContentType());
                                    doc = rd.read();
                                    break;

                                case Input.Kind.INPUT_STREAM:
                                    InputStream inputStream = input.getInputStream();
                                    try {
                                        rd = new ReadableData(runtime, c_data, inputStream, input.getContentType());
                                        doc = rd.read();
                                    } finally {
                                        Closer.close(inputStream);
                                    }
                                    break;

                                default:
                                    throw new UnsupportedOperationException(format("Unsupported input kind '%s'", input.getKind()));
                            }
                            break;

                        default:
                            throw new UnsupportedOperationException(format("Unsupported input type '%s'", input.getType()));
                    }

                    pipeline.writeTo(port, doc);
                }
            } else {
                for (ReadablePipe pipe : xprocConfiguration.inputs.get(port)) {
                    XdmNode doc = pipe.read();
                    pipeline.writeTo(port, doc);
                }
            }
        }

        // Implicit binding for stdin?
        String implicitPort = null;
        for (String port : ports) {
            if (!allPorts.contains(port)) {
                if (pipeline.getDeclareStep().getInput(port).getPrimary()
                        && !pipeline.getDeclareStep().getInput(port).getParameterInput()) {
                    implicitPort = port;
                }
            }
        }

        if (implicitPort != null && !pipeline.hasReadablePipes(implicitPort)) {
            XdmNode doc = runtime.parse(new InputSource(System.in));
            pipeline.writeTo(implicitPort, doc);
        }

        portOutputs = new HashMap<String, Output>();

        Map<String, Output> userArgsOutputs = userArgs.getOutputs();
        for (String port : pipeline.getOutputs()) {
            // Bind to "-" implicitly
            Output output = null;

            if (userArgsOutputs.containsKey(port)) {
                output = userArgsOutputs.get(port);
            } else if (xprocConfiguration.outputs.containsKey(port)) {
                output = new Output(xprocConfiguration.outputs.get(port));
            } else if (userArgsOutputs.containsKey(null)
                    && pipeline.getDeclareStep().getOutput(port).getPrimary()) {
                // Bind unnamed port to primary output port
                output = userArgsOutputs.get(null);
            }

            // Look for explicit binding to "-"
            if ((output != null) && (output.getKind() == Output.Kind.URI) && "-".equals(output.getUri())) {
                output = null;
            }

            portOutputs.put(port, output);
        }

        for (QName optname : xprocConfiguration.options.keySet()) {
            RuntimeValue value = new RuntimeValue(xprocConfiguration.options.get(optname), null, null);
            pipeline.passOption(optname, value);
        }

        for (QName optname : userArgs.getOptionNames()) {
            RuntimeValue value = new RuntimeValue(userArgs.getOption(optname), null, null);
            pipeline.passOption(optname, value);
        }
    }

    protected void processOutputs() {
        for (String port : pipeline.getOutputs()) {
            Output output;
            if (portOutputs.containsKey(port)) {
                output = portOutputs.get(port);
            } else {
                // You didn't bind it, and it isn't going to stdout, so it's going into the bit bucket.
                continue;
            }

            if ((output == null) || ((output.getKind() == Output.Kind.OUTPUT_STREAM) && System.out.equals(output.getOutputStream()))) {
                logger.trace("Copy output from " + port + " to stdout");
            } else {
                switch (output.getKind()) {
                    case Output.Kind.URI:
                        logger.trace("Copy output from " + port + " to " + output.getUri());
                        break;

                    case Output.Kind.OUTPUT_STREAM:
                        String outputStreamClassName = output.getOutputStream().getClass().getName();
                        logger.trace("Copy output from " + port + " to " + outputStreamClassName + " stream");
                        break;

                    default:
                        throw new UnsupportedOperationException(format("Unsupported output kind '%s'", output.getKind()));
                }
            }

            Serialization serial = pipeline.getSerialization(port);

            if (serial == null) {
                // Use the configuration options
                serial = new Serialization(runtime, pipeline.getNode()); // The node's a hack
                for (String name : xprocConfiguration.serializationOptions.keySet()) {
                    String value = xprocConfiguration.serializationOptions.get(name);

                    if ("byte-order-mark".equals(name)) serial.setByteOrderMark("true".equals(value));
                    if ("escape-uri-attributes".equals(name)) serial.setEscapeURIAttributes("true".equals(value));
                    if ("include-content-type".equals(name)) serial.setIncludeContentType("true".equals(value));
                    if ("indent".equals(name)) serial.setIndent("true".equals(value));
                    if ("omit-xml-declaration".equals(name)) serial.setOmitXMLDeclaration("true".equals(value));
                    if ("undeclare-prefixes".equals(name)) serial.setUndeclarePrefixes("true".equals(value));
                    if ("method".equals(name)) serial.setMethod(new QName("", value));

                    // FIXME: if ("cdata-section-elements".equals(name)) serial.setCdataSectionElements();
                    if ("doctype-public".equals(name)) serial.setDoctypePublic(value);
                    if ("doctype-system".equals(name)) serial.setDoctypeSystem(value);
                    if ("encoding".equals(name)) serial.setEncoding(value);
                    if ("media-type".equals(name)) serial.setMediaType(value);
                    if ("normalization-form".equals(name)) serial.setNormalizationForm(value);
                    if ("standalone".equals(name)) serial.setStandalone(value);
                    if ("version".equals(name)) serial.setVersion(value);
                }
            }

            // Command line values override pipeline or configuration specified values
            for (String name: ["byte-order-mark", "escape-uri-attributes", "include-content-type",
                               "indent", "omit-xml-declaration", "undeclare-prefixes", "method",
                               "doctype-public", "doctype-system", "encoding", "media-type",
                               "normalization-form", "standalone", "version" ]) {
                String value = userArgs.getSerializationParameter(port, name);
                if (value == null) {
                    value = userArgs.getSerializationParameter(name);
                    if (value == null) {
                        continue;
                    }
                }

                if ("byte-order-mark".equals(name)) serial.setByteOrderMark("true".equals(value));
                if ("escape-uri-attributes".equals(name)) serial.setEscapeURIAttributes("true".equals(value));
                if ("include-content-type".equals(name)) serial.setIncludeContentType("true".equals(value));
                if ("indent".equals(name)) serial.setIndent("true".equals(value));
                if ("omit-xml-declaration".equals(name)) serial.setOmitXMLDeclaration("true".equals(value));
                if ("undeclare-prefixes".equals(name)) serial.setUndeclarePrefixes("true".equals(value));
                if ("method".equals(name)) serial.setMethod(new QName("", value));
                // N.B. cdata-section-elements isn't allowed
                if ("doctype-public".equals(name)) serial.setDoctypePublic(value);
                if ("doctype-system".equals(name)) serial.setDoctypeSystem(value);
                if ("encoding".equals(name)) serial.setEncoding(value);
                if ("media-type".equals(name)) serial.setMediaType(value);
                if ("normalization-form".equals(name)) serial.setNormalizationForm(value);
                if ("standalone".equals(name)) serial.setStandalone(value);
                if ("version".equals(name)) serial.setVersion(value);
            }

            // I wonder if there's a better way...
            WritableDocument wd = null;
            if (output == null) {
                wd = new WritableDocument(runtime, null, serial);
            } else {
                switch (output.getKind()) {
                    case Output.Kind.URI:
                        URI furi = new URI(output.getUri());
                        String filename = furi.getPath();
                        FileOutputStream outfile = new FileOutputStream(filename);
                        wd = new WritableDocument(runtime, filename, serial, outfile);
                        break;

                    case Output.Kind.OUTPUT_STREAM:
                        OutputStream outputStream = output.getOutputStream();
                        wd = new WritableDocument(runtime, null, serial, outputStream);
                        break;

                    default:
                        throw new UnsupportedOperationException(format("Unsupported output kind '%s'", output.getKind()));
                }
            }

            try {
                ReadablePipe rpipe = pipeline.readFrom(port);
                while (rpipe.moreDocuments()) {
                    wd.write(rpipe.read());
                }
            } finally {
                if (output != null) {
                    wd.close();
                }
            }
        }
    }

    @TaskAction
    void exec() {
        processInputs()
        pipeline.run()
        processOutputs()
    }

    private void setParametersOnPipeline(XPipeline pipeline, String port, Map<QName, String> parameters) {
        if ("*".equals(port)) {
            for (QName name : parameters.keySet()) {
                pipeline.setParameter(name, new RuntimeValue(parameters.get(name)));
            }
        } else {
            for (QName name : parameters.keySet()) {
                pipeline.setParameter(port, name, new RuntimeValue(parameters.get(name)));
            }
        }
    }

    private static UnsupportedOperationException notAllowed(final String msg) {
        return new UnsupportedOperationException (msg)
    }

}
