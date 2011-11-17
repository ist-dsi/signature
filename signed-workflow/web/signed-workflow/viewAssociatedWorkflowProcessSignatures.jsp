<%@page import="module.workflow.domain.WorkflowProcess"%>
<%@page import="pt.ist.fenixframework.pstm.AbstractDomainObject"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
    <%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
	<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
	<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>
	
	
<%
WorkflowProcess process = AbstractDomainObject.fromExternalId(request.getParameter("processId"));
%>

<logic:notEmpty name="process" property="pendingOrFinishedAssociatedSignatureDatas">
	<%-- TODO localize!!: --%>
		<h3>Assinaturas</h3>
		<style>
			.pendente {
			background: #eee;
			background: #fdeaa5;
			padding: 2px 3px;
			color: #555;
			}
		</style>
		<div id="signatureTable">
		<table class="tview1" style="width: 100%">
			<tbody>
				<tr>
					<th>Data do documento gerado</th>
					<th>Descrição</th>
					<th>Assinante</th>
					<th>Estado</th>
					<th></th>
				</tr>
				<logic:iterate id="processSignatureData" name="process" property="pendingOrFinishedAssociatedSignatureDatas" type="module.signed_workflow.domain.WorkflowProcessSignatureData">
					<tr>
						<td><fr:view name="processSignatureData" property="creationDateTime"/></td>
						<td><bean:write name="processSignatureData" property="signatureDescription"/></td>
						<logic:notEmpty name="processSignatureData" property="signature">
							<td><bean:write name="processSignatureData" property="signature.signedUser"/></td>
							<td>Efectuada</td>
							<td><a class="secondaryLink" href="<%= request.getContextPath() + "/signature.do?method=viewSignatureContent&amp;signatureDataOID=" + processSignatureData.getExternalId() %>">Ver documento original</a> | <a class="secondaryLink" href="#">Descarregar</a></td>
						</logic:notEmpty>
						<logic:empty name="processSignatureData" property="signature">
							<td><bean:write name="processSignatureData" property="userToSignPendingSignature"/></td>
							<td><span class="pendente">Pendente</span></td>
							<td><a class="secondaryLink" href="<%= request.getContextPath() + "/signature.do?method=viewSignatureContent&amp;signatureDataOID=" + processSignatureData.getExternalId() %>">Ver documento original</a> | <a class="secondaryLink" href="<%= request.getContextPath() + "/signature.do?method=createSignature&amp;signatureDataOID=" + processSignatureData.getExternalId() %>">Assinar</a></td>
						</logic:empty>
					</tr>
				</logic:iterate>
			</tbody>
		</table>
		</div>
		<div style="display: none;" id="example" title="Leitor de documentos de assinaturas">
			<iframe src ="" style="margin:0;padding:0;border:none;width:100%;height:100%">
			  <p>Your browser does not support iframes.</p>
			</iframe>
		</div>
		
		<script type="text/javascript">
$(document).ready(function(){
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
	</logic:notEmpty>