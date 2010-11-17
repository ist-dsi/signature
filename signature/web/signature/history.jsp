<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>Assinaturas Pendentes</h2>

<fr:view name="pending">
	<fr:layout name="tabular">
		
		<fr:property name="classes" value="tstyle3 mvert1 width100pc tdmiddle punits"/>
		
		<fr:property name="link(sign)" value="/signature.do?method=createSignature" />
		<fr:property name="key(sign)" value="link.signature.sign" />
		<fr:property name="param(sign)" value="OID" />
		<fr:property name="bundle(sign)" value="SIGNATURE_RESOURCES" />
		<fr:property name="order(sign)" value="1" />

		<fr:property name="link(view)" value="/signature.do?method=viewSignature" />
		<fr:property name="key(view)" value="link.signature.view" />
		<fr:property name="param(view)" value="OID" />
		<fr:property name="bundle(view)" value="SIGNATURE_RESOURCES" />
		<fr:property name="order(view)" value="2" />

	</fr:layout>
	<fr:schema bundle="SIGNATURE_RESOURCES" type="module.signature.domain.SignatureIntention">
		<fr:slot name="oid" key="label.signature.oid" />
		<fr:slot name="createdDateTime" key="label.signature.sealedDateTime" />
	</fr:schema>
</fr:view>

<h2>Assinaturas Concluidas</h2>

<fr:view name="sealed">
	<fr:layout name="tabular">
		
		<fr:property name="classes" value="tstyle3 mvert1 width100pc tdmiddle punits"/>
		
		<fr:property name="link(view)" value="/signature.do?method=viewSignature" />
		<fr:property name="key(view)" value="link.signature.view" />
		<fr:property name="param(view)" value="OID" />
		<fr:property name="bundle(view)" value="SIGNATURE_RESOURCES" />
		<fr:property name="order(view)" value="2" />

	</fr:layout>
	<fr:schema bundle="SIGNATURE_RESOURCES" type="module.signature.domain.SignatureIntention">
		<fr:slot name="oid" key="label.signature.oid" />
		<fr:slot name="sealedDateTime" key="label.signature.sealedDateTime" />
	</fr:schema>
</fr:view>
