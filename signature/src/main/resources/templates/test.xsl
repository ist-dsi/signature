<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="signature">
<div class="container">

<div class="row">
	<div class="column grid_10">
	<h1>Assinatura: Processo de Aquisição (<xsl:value-of select="simplifiedProcedureProcess/processNumber"/>)</h1>
	</div>
	<div class="column grid_2">
	<div class="vevent"><abbr title="2010-11-24T20:0500" class="dtstart"><xsl:value-of select="date"/></abbr></div>
	</div>
</div>

<div class="row">
<ul class="navigation">
	<li><a href="#payingUnits">Unidades Pagadoras</a></li>
	<li><a href="#items">Items</a></li>
	<li><a href="#logs">Logs</a></li>
</ul>
</div>

<div class="row">
	<h2>Informação</h2>
	<div class="column grid_6">
		<table class="generic-list data-list">
			<tr><th>Criado por:</th><td><xsl:value-of select="simplifiedProcedureProcess/creationPerson"/></td></tr>
			<tr><th>Criado em:</th><td><xsl:value-of select="simplifiedProcedureProcess/creationDate"/></td></tr>
			<tr><th>Descrição:</th><td><xsl:value-of select="simplifiedProcedureProcess/description"/></td></tr>
			<tr><th>Classificação Processo:</th><td><xsl:value-of select="simplifiedProcedureProcess/processClassification"/></td></tr>
			<tr><th>Documento Requisitado:</th><td><xsl:value-of select="simplifiedProcedureProcess/acquisitionRequestDocumentID"/></td></tr>
			<tr><th>Unidade Requisitante:</th><td><xsl:value-of select="simplifiedProcedureProcess/requestingUnitPresentationName"/></td></tr>
			<tr><th>Requisitante:</th><td><xsl:value-of select="simplifiedProcedureProcess/requestorName"/></td></tr>
		</table>
	</div>
	<div class="column grid_6">
		<table class="generic-list data-list">
			<tr><th>Pessoa a Reembolsar:</th><td><xsl:value-of select="simplifiedProcedureProcess/refundeeName"/></td></tr>
			<tr><th>Valor total (s/IVA):</th><td><xsl:value-of select="simplifiedProcedureProcess/currentValue"/></td></tr>
			<tr><th>IVA:</th><td><xsl:value-of select="simplifiedProcedureProcess/currentTotalVatValue"/></td></tr>
			<tr><th>Custos adicionais:</th><td><xsl:value-of select="simplifiedProcedureProcess/currentTotalAdditionalCostsValue"/></td></tr>
			<tr><th>Valor final (c/IVA):</th><td><xsl:value-of select="simplifiedProcedureProcess/currentTotalItemValueWithAdditionalCostsAndVat"/></td></tr>
			<tr><th>Validade do pré-cabimento:</th><td><xsl:value-of select="simplifiedProcedureProcess/fundAllocationExpirationDate"/></td></tr>
			<tr><th>Referência do pagamento:</th><td><xsl:value-of select="simplifiedProcedureProcess/paymentReference"/></td></tr>
			<tr><th>Objecto de Contracto:</th><td><xsl:value-of select="simplifiedProcedureProcess/contractSimpleDescription"/></td></tr>
		</table>
	</div>
</div>

<a name="payingUnits" />
<div class="row">
	<h2>Unidades Pagadoras</h2>
	<div class="column grid_12">
		<table class="generic-list items-list">
			<thead>
			<tr><th>Unidade</th>
				<th>Unidade Exploração</th>
				<th>Nº de cabimento</th>
				<th>Nº de cabimento efectivo</th>
				<th>Valor (c/IVA)</th>
			</tr>
			</thead>
			<xsl:for-each select="simplifiedProcedureProcess/payingUnitTotals">
			<tr>
				<td><xsl:value-of select="presentationName"/></td>
				<td><xsl:value-of select="accountingUnitName"/></td>
				<td><xsl:value-of select="fundAllocationIds"/></td>
				<td><xsl:value-of select="effectiveFundAllocationIds"/></td>
				<td><xsl:value-of select="amount"/></td>
			</tr>
			</xsl:for-each>
		</table>
	</div>
</div>

<a name="items" />
<div class="row">
	<h2>Items</h2>
	<div class="column grid_12">
		<table class="generic-list items-list">
			<xsl:for-each select="simplifiedProcedureProcess/items">
			<tr><th colspan="2"><xsl:value-of select="description"/></th>
			</tr>
			<tr>
				<td>
					<ul>
						<li>Referência: <xsl:value-of select="proposalReference"/></li>
						<li>CPV: <xsl:value-of select="CPVReference"/></li>
						<li>Endereço: <xsl:value-of select="address"/></li>
						<li>Unidades Pagadoras: <xsl:value-of select="sortedUnitItems"/></li>
					</ul>
				</td>
				<td style="width: 35%">
					<table>
						<thead>
						<tr>
							<th></th>
							<th></th>
							<th>Efectivo</th>
						</tr>
						</thead>
						<tr>
							<td>Quantidade</td>
							<td><xsl:value-of select="quantity"/></td>
							<td><xsl:value-of select="realQuantity"/></td>
						</tr>
						<tr>
							<td>Valor unitário (s/IVA)</td>
							<td><xsl:value-of select="unitValue"/></td>
							<td><xsl:value-of select="realUnitValue"/></td>
						</tr>
						<tr>
							<td>Valor total (s/IVA)</td>
							<td><xsl:value-of select="totalItemValue"/></td>
							<td><xsl:value-of select="totalRealValue"/></td>
						</tr>
						<tr>
							<td>Taxa do IVA (%)</td>
							<td><xsl:value-of select="vatValue"/></td>
							<td><xsl:value-of select="realVatValue"/></td>
						</tr>
						<tr>
							<td>IVA</td>
							<td><xsl:value-of select="totalVatValue"/></td>
							<td><xsl:value-of select="totalRealVatValue"/></td>
						</tr>
						<tr>
							<td>Custo Adicional</td>
							<td><xsl:value-of select="additionalCostValue"/></td>
							<td><xsl:value-of select="realAdditionalCostValue"/></td>
						</tr>
						<tr>
							<td>Valor final (c/IVA)</td>
							<td><xsl:value-of select="totalItemValueWithAdditionalCostsAndVat"/></td>
							<td><xsl:value-of select="totalRealValueWithAdditionalCostsAndVat"/></td>
						</tr>
					</table>
				</td>
			</tr>
			</xsl:for-each>
		</table>
	</div>
</div>

<a name="logs" />
<div class="row">
	<h2>Logs</h2>
	<div class="column grid_12">
		<table class="generic-list logs-list">
			<thead>
			  <tr>
				<th>Data</th>
				<th>Descrição</th>
				<th>Utilizador</th>
			  </tr>
			</thead>			
			<xsl:for-each select="simplifiedProcedureProcess/logs">
			<tr><th class="date"><xsl:value-of select="whenOperationWasRan"/></th>
				<td><xsl:value-of select="description"/></td>
				<td><xsl:value-of select="activityExecutor"/></td>
			</tr>
			</xsl:for-each>
		</table>
	</div>
</div>
</div>

</xsl:template>
</xsl:stylesheet>

