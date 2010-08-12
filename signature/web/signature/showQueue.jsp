<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>


<h1>Queue List</h1>

<bean:write name="signIntention" property="signatureIntentionsCount" /> items

<logic:iterate id="queueItem" name="signIntention" property="signatureIntentions">
	<li><bean:write name="queueItem" property="identification" /> (<bean:write name="queueItem" property="signObject.class" />)</li>
</logic:iterate>

<fr:view name="signIntention" layout="signatureBox" />

