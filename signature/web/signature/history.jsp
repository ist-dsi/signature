<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>Minhas Assinaturas</h2>

<fr:view name="signatures">
	<fr:layout name="tabular">
		
		<fr:property name="classes" value="tstyle3 mvert1 width100pc tdmiddle punits"/>
		
		<fr:property name="link(view)" value="/signature.do?method=viewSignature" />
		<fr:property name="key(view)" value="link.signature.viewSignature" />
		<fr:property name="param(view)" value="OID" />
		<fr:property name="bundle(view)" value="SIGNATURE_RESOURCES" />
		<fr:property name="order(view)" value="1" />

	</fr:layout>
	<fr:schema bundle="SIGNATURE_RESOURCES" type="module.signature.domain.SignatureIntention">
		<fr:slot name="oid" key="label.signature.oid" />
		<fr:slot name="sealedDateTime" key="label.signature.sealedDateTime" />
	</fr:schema>
</fr:view>
