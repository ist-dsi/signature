<%@page import="module.signature.domain.Signature"%>
<%@page import="module.signature.domain.data.SignatureData"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>Assinaturas Terminadas</h2>

<div id="signatureTable">
<logic:empty name="signedSignatureDatas">
	<p><i> - Sem assinaturas concluídas - </i></p>
</logic:empty>
<logic:notEmpty name="signedSignatureDatas">

<table class="tstyle3 mvert1 width100pc punits">
	<tbody>
		<tr>
			<th>Criada em</th>
			<th>Descrição</th>
			<th>Tipo</th>
			<th>Acções</th>
		</tr>
		<logic:iterate id="signedSignatureData" name="signedSignatureDatas" type="module.signature.domain.data.ObjectSignatureData">
		<tr>
			<td><fr:view name="signedSignatureData" property="signature.createdDateTime"/></td>
			<td><bean:write name="signedSignatureData" property="signatureDescription"/></td>
			<%
			if (signedSignatureData.hasMultipleObjectSignatureDataObject())
			{
			    
			%>
				<td>Assinatura múltipla</td>
				<%
			} else {
				%>
				<td>Assinatura simples</td>
				<%} %>
			<td><a class="secondaryLink" href="<%= request.getContextPath() + "/signature.do?method=viewSignatureContent&amp;signatureDataOID=" + signedSignatureData.getExternalId() %>">Ver documento original</a> | <a class="secondaryLink" href="<%= request.getContextPath() + "/signature.do?method=downloadSignature&amp;signatureDataOID=" + signedSignatureData.getExternalId() %>">Descarregar</a></td>
		</tr>
		</logic:iterate>
	</tbody>
</table>

</logic:notEmpty>
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

