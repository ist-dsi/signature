<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>


<h1>Queue List</h1>

<logic:notPresent name="signIntention">
Não existem assinaturas na Queue ;)
</logic:notPresent>

<logic:present name="signIntention">
	<bean:write name="signIntention" property="signatureIntentionsCount" /> items
	
	<fr:view name="signIntention" layout="signatureBox" />
</logic:present>

