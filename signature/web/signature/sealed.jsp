<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>Assinaturas Terminadas</h2>

<div id="signatureTable">
<fr:view name="sealed">
	<fr:layout name="tabular">
		
		<fr:property name="classes" value="tstyle3 mvert1 width100pc punits"/>
		
		<fr:property name="link(viewContent)" value="/signature.do?method=viewSignatureContent" />
		<fr:property name="key(viewContent)" value="link.signature.view" />
		<fr:property name="param(viewContent)" value="OID" />
		<fr:property name="bundle(viewContent)" value="SIGNATURE_RESOURCES" />
		<fr:property name="order(viewContent)" value="2" />


	</fr:layout>
	<fr:schema bundle="SIGNATURE_RESOURCES" type="module.signature.domain.SignatureIntention">
		<fr:slot name="createdDateTime" key="label.signature.createdDateTime" />
		<fr:slot name="description" key="label.signature.description" />
	</fr:schema>
</fr:view>
</div>


<div style="display: none;" id="example" title="Leitor de Assinaturas">
<iframe src ="http://www.google.com" style="margin:0;padding:0;border:none;width:100%;height:100%">
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

