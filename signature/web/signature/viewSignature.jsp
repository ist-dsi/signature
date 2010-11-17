<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>


<h1>Assinatura</h1>

<fr:view name="signIntention" schema="viewSignature">
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2"/>
		<fr:property name="columnClasses" value="smalltxt, smalltxt aleft,smalltxt"/>
		<fr:property name="sortBy" value="whenOperationWasRan"/>
	</fr:layout>
</fr:view>

<br />

<code>
<bean:write name="signIntention" property="content" />
</code> 
