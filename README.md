# XML Calabash 1.0 Gradle Plugin

This project provides a Gradle plugin to run XML Calabash.

Version 1.3 has been rewritten to use the underlying XML Calabash APIs
to run the pipeline directly, rather than running them through the
hack of calling the XML Calabash driver. This makes it easier to
subclass them task (see the `DocBookTask` in
https://github.com/docbook/xslt20-stylesheets). I’ve removed the “Exec” flavor
plugin.

Version 1.2 introduced a backwards incompatible change: all input,
output, and pipeline filenames are now resolved against the location
of the task. This means that the filenames are relative to the
location of the `build.gradle` file, not the current working
directory.

To use the plugin, simply include the dependencies in the buildscript:

```
buildscript {
  repositories {
    mavenCentral()
    maven { url "http://maven.restlet.org" }
  }

  dependencies {
    classpath group: 'com.xmlcalabash', name: 'gradle', version: '1.3.4'
  }
}
```

Apply the plugin and import the task type:

```
apply plugin: 'com.xmlcalabash.task'

import com.xmlcalabash.XMLCalabashTask
```

Then create tasks of the appropriate type:

```
task myTask(type: XMLCalabashTask) {
    … properties and methods here …
}
```

## Task Properties

The following properties are supported:

<dl>
<dt>pipeline</dt>
<dd>The filename (URI) of the pipeline to run (string, required).</dd>
<dt>debugPipeline</dt>
<dd>Enable debug mode (boolean)</dd>
<dt>safeMode</dt>
<dd>Enable safe mode (boolean)</dd>
<dt>profilePipeline</dt>
<dd>Name of the file to use for writing profile information (string)</dd>
<dt>saxonEdition</dt>
<dd>Saxon edition (string: “he”, “pe”, or “ee”; defaults to “he”)</dd>
<dt>schemaAware</dt>
<dd>Schema aware processing (boolean, requires “pe” or “ee” edition)</dd>
<dt>entityResolver</dt>
<dd>Name of entity resolver class (string)</dd>
<dt>uriResolver</dt>
<dd>Name of URI resolver class (string)</dd>
<dt>configFile</dt>
<dd>Filename of the XML Calabash configuration file (string;
    defaults to <code>$HOME/.calabash</code>)</dd>
<dt>saxonConfigFile</dt>
<dd>Filename of the Saxon Configuration file (string)</dd>
<dt>library</dt>
<dd>Library to load (string)</dd>
<dt>step</dt>
<dd>Step to run (QName). _N.B._ This option is currently unavailable.</dd>
<dt>extensionValues</dt>
<dd>Enable the “general-values” extension (boolean)</dd>
<dt>xpointerOnText</dt>
<dd>Enable the “xpointer-on-text” extension (boolean)</dd>
<dt>transparentJson</dt>
<dd>Enable the “transparent-json” extension (boolean)</dd>
<dt>jsonFlavor</dt>
<dd>Identify the preferred flavor of JSON-to-XML conversion (string)</dd>
<dt>allowTextResults</dt>
<dd>Allow steps to produce documents that consist of a single text node (boolean)</dd>
<dt>useXslt10</dt>
<dd>Enable the “use-xslt-1.0” extension (boolean)</dd>
<dt>htmlSerializer</dt>
<dd>Set the preferred HTML serializer (string)</dd>
</dl>

## Task Methods

In addition, the following methods may be called:

<dl>
<dt>namespaceBinding(String prefix, String uri)</dt>
<dd>Establish the namespace binding between <code>prefix</code> and <code>uri</code>
for evaluating QNames specified in other properties and methods.</dd>
<dt>input(String port, String filename)</dt>
<dd>Send the XML document identified by <code>filename</code> (URI) to the port named <code>port</code>.</dd>
<dt>dataInput(String port, String filename)</dt>
<dd>Send the non-XML document identified by <code>filename</code> (URI) to the port named <code>port</code>.</dd>
<dt>dataInput(String port, String filename, String contentType)</dt>
<dd>Send the non-XML document identified by <code>filename</code> (URI) to the port named <code>port</code>
with the explicit content type specified in <code>contentType</code>.</dd>
<dt>output(String port, String filename)</dt>
<dd>Send the output from the port named <code>port</code> to the filename identified by <code>filename</code>.</dd>
<dt>param(String qname, String<sup><a href="#types">†</a></sup> value)</dt>
<dd>Send the <code>qname</code> parameter with the value <code>value</code> to the primary
parameter input port on the pipeline.</dd>
<dt>param(String qname, String<sup><a href="#types">†</a></sup> value, String port)</dt>
<dd>Send the <code>qname</code> parameter with the value <code>value</code> to the <code>port</code> parameter
input port on the pipeline.</dd>
<dt>option(String qname, String<sup><a href="#types">†</a></sup> value)</dt>
<dd>Send the option <code>qname</code> to the pipeline with the value <code>value</code>.</dd>
</dl>

The `input`, `dataInput`, and `output` methods automatically register
their filename argument as an input (or output) to the task. Gradle
will cache this information for the purpose of working out what other
tasks may need to be run.

<sup id="types"><b>†</b></sup> The parameter and option methods
take `String`, `Boolean`, `Integer`,
`Float`, and `File` values. For clarity, only the string signatures are shown above.
If a `File` value is given, the file is registered as an input to the task
and the absolute path of the file is sent to the pipeline as the value of
the parameter or option.

If you need the file parameter or option to have a relative path, simply
send it as a string and use the standard Gradle `inputs.file` property
to register the input:

```
task myTask(type: XMLCalabashTask) {
  inputs.file "path/to/file.xml"
  option("relpath", "path/to/file.xml")
  …
}
```

## Example

There’s a simple example of a build script that uses the `XMLCalabashTask`
in the [`gradletest/`](https://github.com/ndw/xmlcalabash1-gradle/tree/master/gradletest)
directory of this repository.
