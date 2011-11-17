<%@page import="module.signature.domain.data.SignatureData"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>


<bean:define id="signatureData" type="module.signature.domain.data.SignatureData" name="signData"/>


<script type="text/javascript">
function getContent() {
	return "<%=signatureData.getBase64EncodedContentToSign().replace("\n","\\n")%>";
}

function writeContentToInput(value)
{
	$("textarea[id$='auxiliarySignatureContentString']").get(0).value = value;
}

function submitForm() {
 	$('form[action="<%= request.getContextPath() + "/signature.do?method=submitSignature"%>"]').submit();
}

$(function() {
	$("textarea[id$='auxiliarySignatureContentString']").css('display','none');
	$('label[title="auxiliarySignatureContentString"]').css('display','none');
});

</script>

<logic:messagesPresent property="message" message="true">
	<div class="error1">
		<html:messages id="errorMessage" property="message" message="true"> 
			<span><fr:view name="errorMessage"/></span>
		</html:messages>
	</div>
</logic:messagesPresent>

<applet code="aeq.Applet.class" archive="public-jars/aeq30.jar,public-jars/bcprov-jdk16-146.jar.debug.jar,public-jars/plugin.jar,public-jars/httpmime-4.1.2.jar,public-jars/httpcore-4.1.2.jar,public-jars/httpclient-cache-4.1.2.jar,public-jars/core-renderer.jar,public-jars/httpclient-4.1.2.jar,public-jars/commons-codec-1.4.jar,public-jars/bctsp-jdk16-146.jar.debug.jar,public-jars/httpclient-4.1.2.jar,public-jars/commons-logging-1.1.1.jar,public-jars/core-renderer.jar,public-jars/xmlsec-1.4.3.jar,public-jars/bcmail-jdk16-146.jar.debug.jar"
width="910" height="800" MAYSCRIPT>
<param name="signContentURL" value="<%=request.getScheme() +"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath() +"/INVALID_TODO.do?method=getSignatureContent&contentId=" %>">
<param name="serverURL" value="<%=request.getScheme() +"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath() +"/INVALID_TODO.do?method=uploadSignature&contentId="  %>">
<param name="redirectURL" value="<%=request.getScheme() +"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath() +"/INVALID_TODO.do?method=viewProcess&processId="%>">
</applet>

<fr:form   action='<%= "/signature.do?method=submitSignature"%>' encoding="multipart/form-data">
	<fr:edit id="signatureData" 
		name="signData" >
			<fr:schema bundle="SIGNATURE_RESOURCES" type="module.signature.domain.data.SignatureData">
				<fr:slot name="auxiliarySignatureContentString" layout="longText" />
			</fr:schema>
		</fr:edit>
	<%--<html:submit styleClass="inputbutton"><bean:message key="renderers.form.submit.name" bundle="RENDERER_RESOURCES"/></html:submit> --%>
</fr:form>
