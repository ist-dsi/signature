<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>Configuração</h2>

<h3>Lista de utilizadores com Queue</h3>

<logic:present name="queues">
	<fr:view name="queues">
		<fr:layout name="tabular">
			
			<fr:property name="classes" value="tstyle3 mvert1 width100pc tdmiddle punits"/>
			
			<fr:property name="link(view)" value="/signature.do?method=createQueue2" />
			<fr:property name="key(view)" value="link.signature.deleteQueue" />
			<fr:property name="param(view)" value="OID" />
			<fr:property name="bundle(view)" value="SIGNATURE_RESOURCES" />
			<fr:property name="order(view)" value="1" />

		</fr:layout>
		<fr:schema bundle="SIGNATURE_RESOURCES" type="module.signature.domain.SignatureQueue">
			<fr:slot name="oid" key="label.signature.oid" />
			<fr:slot name="user.username" key="label.signature.username" />
		</fr:schema>
	</fr:view>
</logic:present>


<html:link action="/signature.do?method=createQueue2">
Create Queue?
</html:link>
