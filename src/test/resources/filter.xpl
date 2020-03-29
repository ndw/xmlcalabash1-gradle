<p:declare-step xmlns:p="http://www.w3.org/ns/xproc" version="1.0"
                xmlns:p1="http://foo.com/">
  <p:input port="source"/>
  <p:input port="parameters" kind="parameter"/>
  <p:option name="p1:one"/>
  <p:output port="result"/>

  <p:add-attribute attribute-name="test" attribute-value="this" match="/*"/>

</p:declare-step>
