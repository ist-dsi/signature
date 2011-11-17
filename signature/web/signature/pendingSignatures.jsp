<%@page import="module.signed_workflow.domain.WorkflowProcessSignatureData"%>

<%@page import="module.signature.domain.data.SignatureData"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<div style="float:right">
<form action="<%= request.getContextPath() + "/signature.do" %>" method="post">
	<html:hidden property="method" value="pendingSignatures"/>
<% String filterBy = (String) request.getAttribute("filterBy"); %>
<select name="filterBy">
	<option value=""><bean:message bundle="SIGNATURE_RESOURCES" key="label.module.signature.all" /></option>
	<logic:iterate name="filters" id="entry">
		<bean:define name="entry" property="key" id="entryKey" />
		<bean:define name="entry" property="value" id="entryValue" />
		<logic:equal name="entryKey" value="<%= filterBy %>">
			<option selected="selected" value="<%= entryKey %>"><%= entryValue %></option>
		</logic:equal>
		<logic:notEqual name="entryKey" value="<%= filterBy %>">
			<option value="<%= entryKey %>"><%= entryValue %></option>
		</logic:notEqual>
	</logic:iterate>
</select>
<button>Filtrar</button>
</form>
</div>


<h2><bean:message bundle="SIGNATURE_RESOURCES" key="label.module.signature.pendingSignatures" /></h2>

<logic:messagesPresent property="message" message="true">
	<div class="error1">
		<html:messages id="errorMessage" property="message" message="true"> 
			<span><fr:view name="errorMessage"/></span>
		</html:messages>
	</div>
</logic:messagesPresent>

<logic:messagesPresent property="messageSuccess" message="true">
	<div class="success1">
		<html:messages id="successMessage" property="messageSuccess" message="true"> 
			<span><fr:view name="successMessage"/></span>
		</html:messages>
	</div>
</logic:messagesPresent>
<logic:notEmpty name="pending">
<form action="<%= request.getContextPath() + "/signature.do" %>" method="post">
	<html:hidden property="method" value="multiSignature"/>

	<div id="signatureTable">
	<table class="tstyle3 thleft tdleft mbottom2" width="100%">
		<tr>
			<th><input type="checkbox" id="selectAllSignatures" /></th> 
			<th><bean:message bundle="SIGNATURE_RESOURCES" key="label.module.signature.addedDateTime"/></th>
			<th><bean:message bundle="SIGNATURE_RESOURCES" key="label.module.signature.description"/></th>
			<th><%--<bean:message bundle="SIGNATURE_RESOURCES" key="label.module.signature.actions"/> --%></th>
		</tr>
		<bean:define name="pending" id="pending" toScope="request"  type="java.util.List<SignatureData>" />
		<%
			for (final SignatureData signature : pending) {
		%>
		<tr>
			<td><input type="checkbox" name="signatureIds" value="<%= signature.getExternalId() %>" /></td>
			<% String datetime = (signature.getCreationDateTime() != null) ? signature.getCreationDateTime().toString("dd/MM/yyyy hh:mm") : ""; %>
			<td><%= datetime %></td>
			<td><%= signature.getSignatureDescription() %></td>
			<td><a href="<%= request.getContextPath() + "/signature.do?method=createSignature&amp;signatureDataOID=" + signature.getExternalId() %>" class="secondaryLink">
				<bean:message bundle="SIGNATURE_RESOURCES" key="link.signature.sign"/>
				</a> |
				<a href="<%= request.getContextPath() + "/signature.do?method=viewSignatureContent&amp;signatureDataOID=" + signature.getExternalId() %>" class="secondaryLink">
				<bean:message bundle="SIGNATURE_RESOURCES" key="link.signature.view"/>
				</a>
				<% //if this is a kind of Signature associated to a process, let's put a link to the process here
					if (signature instanceof WorkflowProcessSignatureData)
					{
					    WorkflowProcessSignatureData workflowSignatureData = (WorkflowProcessSignatureData) signature;
				%>
				 | <html:link action="<%= "/workflowProcessManagement.do?method=viewProcess&processId=" + workflowSignatureData.getWorkflowProcess().getExternalId()%>" styleClass="secondaryLink">
				 <bean:message bundle="SIGNATURE_RESOURCES" key="link.signature.viewProcess"/>
				 </html:link>
				<% }%>
			</td>
		</tr>		
		<%
			}
		%>
	</table>
	</div>
		
	<button><bean:message bundle="SIGNATURE_RESOURCES" key="label.module.signature.signSelected" /></button>
</form>
</logic:notEmpty>
<logic:empty name="pending">
<p><i> - Sem assinaturas pendentes - </i></p>
</logic:empty>
<div style="display: none;" id="example" title="Leitor de Assinaturas">
<iframe src ="" style="margin:0;padding:0;border:none;width:100%;height:100%">
  <p>Your browser does not support iframes.</p>
</iframe>
</div>

<script type="text/javascript">
$(document).ready(function(){
	$("#selectAllSignatures").click(function(){
		$('input[name="signatureIds"]').attr("checked", $(this).attr("checked"));
	});
	
	$('#signatureTable a[href*="viewSignatureContent"]').each(function(){
		var href = $(this).attr("href");
		var dialogOpts = {
			modal: true,
			bgiframe: true,
			autoOpen: false,
			height: 550,
			width: 900,
			draggable: true,
			resizeable: true,
			open: function() {
				//$("#example iframe").src(href);
			}
		};

		$("#example").dialog(dialogOpts);	//end dialog

		$(this).click(function(){
			$("#example iframe").attr('src', href);			
			$("#example").dialog("open");

			return false;
		});
	});
});	
</script>

