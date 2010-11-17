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


<logic:present name="signaturesCount">
	<p><html:link action="/signature.do?method=history"><bean:write name="signaturesCount" /> assinaturas </html:link></p>
</logic:present>
