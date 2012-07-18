<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>


<h1>Queue List</h1>

<logic:notPresent name="signIntention">
Não existem assinaturas na Queue ;)
</logic:notPresent>

<logic:present name="signIntention">
	<bean:write name="signIntention" property="signatureIntentionsCount" /> items
	
	<fr:view name="signIntention" layout="signatureBox" />
</logic:present>

