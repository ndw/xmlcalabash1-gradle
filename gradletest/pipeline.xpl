<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                version="1.0" name="main">
  <p:input port="source"/>
  <p:input port="parameters" kind="parameter"/>
  <p:output port="result">
    <p:pipe step="params" port="result"/>
  </p:output>

  <p:parameters name="params">
    <p:input port="parameters">
      <p:pipe step="main" port="parameters"/>
    </p:input>
  </p:parameters>
</p:declare-step>
