<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<%@page import="myorg.presentationTier.actions.ContextBaseAction"%>
<%@page import="myorg.domain.contents.Node"%>
<%@page import="module.workflow.presentationTier.ProcessNodeSelectionMapper"%>
<%@page import="myorg.presentationTier.servlets.filters.contentRewrite.ContentContextInjectionRewriter"%>
<%@page import="java.util.List"%>


<logic:present name="queue">
	<logic:notEmpty name="queue" property="expire">
		<p><html:link action="/signatureAction.do?method=showQueue"><bean:write name="queue" property="signatureIntentionsCount" /> assinaturas pendentes</html:link></p>
		<p>Expira: <bean:write name="queue" property="expire.hourOfDay" />:<bean:write name="queue" property="expire.minuteOfHour" /></p>
	</logic:notEmpty>
	<logic:empty name="queue" property="expire">
		<p>Não tem assinaturas pendentes</p>
	</logic:empty>
</logic:present>
<logic:notPresent name="queue">
	<b>Não tem queue..</b>
</logic:notPresent>
