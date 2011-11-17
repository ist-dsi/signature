<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
				<title>Assinatura</title>
				<style type="text/css">
					* {
					margin: 0;
					padding: 0;
					}

					body {
					font: 12px Helvetica, Arial, sans-serif;
					
					
					}

					.grid_1 {
					width: 60px;
					}

					.grid_2 {
					width: 130px;
					}

					.grid_3 {
					width: 200px;
					}

					.grid_4 {
					width: 270px;
					}

					.grid_5 {
					width: 340px;
					}

					.grid_6 {
					width: 410px;
					}

					.grid_7 {
					width: 480px;
					}

					.grid_8 {
					width: 550px;
					}

					.grid_9 {
					width: 620px;
					}

					.grid_10 {
					width: 690px;
					}

					.grid_11 {
					width: 760px;
					}

					.grid_12 {
					width: 830px;
					}

					.column {
					margin: 0 5px;
					overflow: hidden;
					float: left;
					display: inline;
					}

					.row {
					width: 840px;
					margin: 0 auto;
					overflow: hidden;
					}

					.row .row {
					margin: 0 -5px;
					width: auto;
					display: inline-block;
					}

					.nodisplay {
					display: none;
					}

					.container {
					border: 1px solid #666;
					width: 840px;
					margin: 1em auto;
					}

					.text-center {
					text-align: center
					}

					h1 {
					font-size: 1.5em;
					padding: 0.5em 0.5em 0.5em 38px;
					background-position: left center;
					background-repeat: no-repeat;
					background-image:
					url('data:image/png;base64,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');
					}

					h2 {
					font-size: 1.2em;
					background: #DDD;
					padding: 5px 10px;
					margin-top: 0.5em
					}

					.vevent {
					padding: 1em 0 0 0;
					}

					.vevent abbr {
					font-size: 1.4em;
					}

					ul.navigation {
					float: right;
					}

					ul.navigation li {
					float: left;
					display: block;
					margin-right: 0.5em;
					padding: 0.4em 0.5em;
					background: #A4D3EE
					}

					ul.navigation li a {
					text-decoration: none;
					font-weight: bold
					}

					table {
					padding: 0;
					width: 100%;
					border-collapse: collapse;
					}

					table td,table th {
					padding: 4px 8px;
					text-align: left
					}

					table thead th {
					background: #EEE;
					}

					.generic-list td,.generic-list th {
					border-bottom: 1px solid #CCCCCC;
					}

					.data-list th {
					width: 45%
					}

					.logs-list tbody th {
					width: 15%
					}

					.logs-list tbody td {
					font-size: 1em;
					}

					.logs-list tbody th.date {
					width: 100px
					}

					.items-list tbody td {
					font-size: 1em;
					}
</style>
			</head>

			<body>


				<div class="container">
					<span style="display:none"><xsl:attribute name="id">AllContent_<xsl:value-of select="position()" />_<xsl:value-of select="//@signatureId" /></xsl:attribute><xsl:copy-of select="//*"/></span>
					<a name="top"/>
						<h1>Múltiplas Assinaturas</h1>
						<h2>Número de assinaturas pendentes:<xsl:value-of select="count(/*/aggregatedSignature)"/></h2>
						<ul style="margin: 10px; padding:0px">
						<xsl:for-each select="/*/aggregatedSignature">
							<li style="margin-left: 10px; padding:0px"><a style="margin: 10px; padding:0"><xsl:attribute name="href">#<xsl:value-of select="@signatureId"/></xsl:attribute><xsl:value-of select="@description"/></a></li>
						</xsl:for-each>
						</ul>
					#CONTENT

				</div>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>