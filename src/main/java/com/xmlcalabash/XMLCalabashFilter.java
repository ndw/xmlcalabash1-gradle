package com.xmlcalabash;

import com.xmlcalabash.core.XProcConfiguration;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.io.ReadablePipe;
import com.xmlcalabash.io.WritableDocument;
import com.xmlcalabash.model.RuntimeValue;
import com.xmlcalabash.model.Serialization;
import com.xmlcalabash.runtime.XInput;
import com.xmlcalabash.runtime.XPipeline;
import com.xmlcalabash.util.Output;
import com.xmlcalabash.util.UserArgs;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class XMLCalabashFilter extends BaseParamFilterReader implements ChainableReader {
    private ByteArrayInputStream bais = null;
    private boolean transformed = false;

    protected HashMap<String,String> filterParams = null;
    protected HashMap<String,String> filterOptions = null;
    protected boolean schemaAware = false;
    protected boolean safeMode = false;
    protected boolean debug = false;
    protected String pipelineURI = null;
    protected HashMap<String,String> nsBindings = new HashMap<String,String>();
    protected String configFile = null;
    protected String saxonConfigFile = null;
    protected String entityResolver = null;
    protected String uriResolver = null;
    protected String library = null;          // Should be a list...
    protected String step = null;
    protected String profile = null;
    protected String edition = "he";
    protected boolean extensionValues = false;
    protected boolean xpointerOnText = false;
    protected boolean transparentJson = false;
    protected boolean ignoreInvalidXmlBase = false;
    protected String jsonFlavor = null;
    protected boolean allowTextResults = false;
    protected boolean useXslt10 = false;
    protected boolean htmlSerializer = false;

    protected UserArgs userArgs = null;
    protected Hashtable<String,String> seenOptions = new Hashtable<String, String> ();
    protected URI baseURI = null;

    protected XProcConfiguration xprocConfiguration = null;
    protected XProcRuntime runtime = null;
    protected XPipeline pipeline = null;

    public XMLCalabashFilter() {
        // I have no idea what happens if this constructor is called.
    }

    public XMLCalabashFilter(Reader in) {
        super(in);
    }

    public void setTransformed(Boolean xformed) {
        transformed = xformed;
    }

    public boolean getTransformed() {
        return transformed;
    }

    public void setParams(HashMap<String,Object> value) {
        filterParams = new HashMap<String,String>();
        for (String key : value.keySet()) {
            filterParams.put(key, value.get(key).toString());
        }
    }

    public HashMap<String,String> getParams() {
        return filterParams;
    }

    public void setOptions(HashMap<String,Object> value) {
        filterOptions = new HashMap<String,String>();
        for (String key : value.keySet()) {
            filterOptions.put(key, value.get(key).toString());
        }
    }

    public HashMap<String,String> getOptions() {
        return filterOptions;
    }

    public void setSchemaAware(boolean value) {
        schemaAware = value;
    }

    public boolean getSchemaAware() {
        return schemaAware;
    }

    public void setSafeMode(boolean value) {
        safeMode = value;
    }

    public boolean getSafeMode() {
        return safeMode;
    }

    public void setDebug(boolean value) {
        debug = value;
    }

    public boolean getDebug() {
        return debug;
    }

    public void setPipeline(String value) {
        pipelineURI = value;
    }

    public String getPipeline() {
        return pipelineURI;
    }

    public void setNamespaces(HashMap<String,String> value) {
        nsBindings = value;
    }

    public HashMap<String,String> getNamespaces() {
        return nsBindings;
    }

    public void setConfigFile(String value) {
        configFile = value;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setSaxonConfigFile(String value) {
        saxonConfigFile = value;
    }

    public String getSaxonConfigFile() {
        return saxonConfigFile;
    }

    public void setEntityResolver(String value) {
        entityResolver = value;
    }

    public String getEntityResolver() {
        return entityResolver;
    }

    public void setUriResolver(String value) {
        uriResolver = value;
    }

    public String getUriResolver() {
        return uriResolver;
    }

    public void setLibrary(String value) {
        library = value;
    }

    public String getLibrary() {
        return library;
    }

    public void setStep(String value) {
        step = value;
    }

    public String getStep() {
        return step;
    }

    public void setProfilePipeline(String value) {
        profile = value;
    }

    public String getProfilePipeline() {
        return profile;
    }

    public void setSaxonEdition(String value) {
        edition = value;
    }

    public String getSaxonEdition() {
        return edition;
    }

    public void setExtensionValues(boolean value) {
        extensionValues = value;
    }

    public boolean getExtensionValues() {
        return extensionValues;
    }

    public void setXpointerOnText(boolean value) {
        xpointerOnText = value;
    }

    public boolean getXpointerOnText() {
        return xpointerOnText;
    }

    public void setTransparentJson(boolean value) {
        transparentJson = value;
    }

    public boolean getTransparentJson() {
        return transparentJson;
    }

    public void setIgnoreInvalidXmlBase(boolean value) {
        ignoreInvalidXmlBase = value;
    }

    public boolean getIgnoreInvalidXmlBase() {
        return ignoreInvalidXmlBase;
    }

    public void setJsonFlavor(String value) {
        jsonFlavor = value;
    }

    public String getJsonFlavor() {
        return jsonFlavor;
    }

    public void setAllowTextResults(boolean value) {
        allowTextResults = value;
    }

    public boolean getAllowTextResults() {
        return allowTextResults;
    }

    public void setUseXslt10(boolean value) {
        useXslt10 = value;
    }

    public boolean getUseXslt10() {
        return useXslt10;
    }

    public void setHtmlSerializer(boolean value) {
        htmlSerializer = value;
    }

    public boolean getHtmlSerializer() {
        return htmlSerializer;
    }

    public int read() throws IOException {
        if (!this.getInitialized()) {
            if (this.getProject() != null) {
                baseURI = this.getProject().getBaseDir().toURI();
            } else {
                try {
                    baseURI = new URI(System.getProperty("user.dir"));
                } catch (URISyntaxException use) {
                    throw new RuntimeException(use.getMessage());
                }
            }
            // On first read, get all the input and transform it
            this.setInitialized(true);
        }

        if (!transformed) {
            transformed = true;
            transform();
        }

        return bais.read();
    }

    protected void setupRuntime() throws SaxonApiException {
        if (getPipeline() == null && getStep() == null) {
            throw new IllegalArgumentException("You must specify a pipeline or a step.");
        }

        if (getPipeline() != null && getStep() != null) {
            throw new IllegalArgumentException("You must specify either a pipeline or a step.");
        }

        // On reflection, I'm not sure XML Calabash is well designed for this kind of embedding
        userArgs = new UserArgs();
        userArgs.setSaxonProcessor(getSaxonEdition());
        userArgs.setSchemaAware(getSchemaAware());
        userArgs.setSafeMode(getSafeMode());
        userArgs.setDebug(getDebug());
        userArgs.setExtensionValues(getExtensionValues());
        userArgs.setIgnoreInvalidXmlBase(getIgnoreInvalidXmlBase());
        userArgs.setAllowXPointerOnText(getXpointerOnText());
        userArgs.setTransparentJSON(getTransparentJson());
        userArgs.setAllowTextResults(getAllowTextResults());
        userArgs.setUseXslt10(getUseXslt10());
        userArgs.setHtmlSerializer(getHtmlSerializer());

        for (String pfx : nsBindings.keySet()) {
            userArgs.addBinding(pfx, nsBindings.get(pfx));
        }

        for (String key : filterParams.keySet()) {
            userArgs.addParam(key, filterParams.get(key));
        }

        for (String key : filterOptions.keySet()) {
            userArgs.addOption(key, filterOptions.get(key));
        }

        if (getLibrary() != null) {
            userArgs.addLibrary(getLibrary());
        }

        if (getPipeline() != null) {
            userArgs.setPipeline(getPipeline());
        }

        if (getStep() != null) {
            userArgs.setCurStepName(getStep());
        }

        if (getConfigFile() != null) {
            userArgs.setConfig(getConfigFile());
        }

        if (getSaxonConfigFile() != null) {
            userArgs.setSaxonConfig(getSaxonConfigFile());
        }

        if (getEntityResolver() != null) {
            userArgs.setEntityResolverClass(getEntityResolver());
        }

        if (getUriResolver() != null) {
            userArgs.setUriResolverClass(getUriResolver());
        }

        if (getProfilePipeline() != null) {
            userArgs.setProfile(profile);
        }

        if (getJsonFlavor() != null) {
            userArgs.setJsonFlavor(getJsonFlavor());
        }

        xprocConfiguration = userArgs.createConfiguration();
        runtime = new XProcRuntime(xprocConfiguration);
    }

    protected void transform() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int next = this.in.read();
        while (next >= 0) {
            baos.write(next);
            next = this.in.read();
        }

        try {
            setupRuntime();
            pipeline = runtime.load(userArgs.getPipeline());

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

            Set<String> inputPorts = pipeline.getInputs();
            String inputPort = null;
            for (String port : inputPorts) {
                XInput input = pipeline.getInput(port);
                if (!input.getParameters()) {
                    if (inputPort != null) {
                        throw new IllegalArgumentException("A filter pipeline must have exactly one (non-parameter) input");
                    }
                    inputPort = port;
                }
            }

            Set<String> outputPorts = pipeline.getOutputs();
            if (outputPorts.size() != 1) {
                throw new IllegalArgumentException("A filter pipeline must have exactly one output");
            }

            String outputPort = outputPorts.toArray(new String[]{})[0];

            pipeline.clearInputs(inputPort);
            XdmNode doc = runtime.parse(new InputSource(new ByteArrayInputStream(baos.toByteArray())));
            pipeline.writeTo(inputPort, doc);

            for (QName optname : xprocConfiguration.options.keySet()) {
                RuntimeValue value = new RuntimeValue(xprocConfiguration.options.get(optname), null, null);
                pipeline.passOption(optname, value);
            }

            for (QName optname : userArgs.getOptionNames()) {
                RuntimeValue value = new RuntimeValue(userArgs.getOption(optname), null, null);
                pipeline.passOption(optname, value);
            }

            pipeline.run();

            Serialization serial = pipeline.getSerialization(outputPort);

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
            for (String name : new String[]{"byte-order-mark", "escape-uri-attributes", "include-content-type",
                    "indent", "omit-xml-declaration", "undeclare-prefixes", "method",
                    "doctype-public", "doctype-system", "encoding", "media-type",
                    "normalization-form", "standalone", "version" }) {
                String value = userArgs.getSerializationParameter(outputPort, name);
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

            // We'll just reuse it.
            baos = new ByteArrayOutputStream();

            // I wonder if there's a better way...
            Output output =  userArgs.getOutputs().get(null);
            WritableDocument wd = new WritableDocument(runtime, null, serial, baos);

            try {
                ReadablePipe rpipe = pipeline.readFrom(outputPort);
                if (rpipe.moreDocuments()) {
                    wd.write(rpipe.read());
                    if (rpipe.moreDocuments()) {
                        throw new RuntimeException("Pipeline output a sequence of documents");
                    }
                }
            } finally {
                if (output != null) {
                    wd.close();
                }
            }
        } catch(SaxonApiException sae) {
            throw new RuntimeException(sae.getMessage());
        }

        bais = new ByteArrayInputStream(baos.toByteArray());
    }

    private static void setParametersOnPipeline(XPipeline pipeline, String port, Map<QName, String> parameters) {
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

    public Reader chain(Reader rdr) {
        XMLCalabashFilter newFilter = new XMLCalabashFilter(rdr);
        newFilter.setInitialized(true);
        newFilter.setTransformed(false);
        return newFilter;
    }
}
