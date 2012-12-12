<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>

<%@page import="pt.ist.bennu.core.presentationTier.actions.ContextBaseAction"%>
<%@page import="pt.ist.bennu.core.domain.contents.Node"%>
<%@page import="module.workflow.presentationTier.ProcessNodeSelectionMapper"%>
<%@page import="pt.ist.bennu.core.presentationTier.servlets.filters.contentRewrite.ContentContextInjectionRewriter"%>
<%@page import="java.util.List"%>


<logic:present name="signaturesCount">
	<p><html:link action="/signature.do?method=history"><bean:write name="signaturesCount" /> assinaturas </html:link></p>
</logic:present>
