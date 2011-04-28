<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="pt-PT" xml:lang="pt-PT">
<head>
  <title>Assinatura Electrónica Qualificada</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <link rel="shortcut icon" type="image/ico" href="./images/favicon.ico">
  <link rel="stylesheet" type="text/css" href="./css/screen.css" media="screen"/>
  <link rel="stylesheet" type="text/css" href="./css/print.css" media="print"/>

  <script type="text/javascript">
   onload = function(){
	document.getElementsByTagName( "applet" )[0].focus();
	}				
  </script>
</head>

<body>

<div id="container">
  <div id="header">
    <table width="100%">
      <tr>
        <td rowspan="2" width="60px" valign="middle"><img src="./images/ist.gif"></td>
        <td rowspan="2" valign="top" align="left" style="padding-left: 5px; vertical-align: middle;">
          <h1 style="margin-top: 0;">AEQ Docs</h1>
          <p>sistema de assinatura electrónica qualificada de documentos</p>
        </td>
        <td align="right" nowrap="nowrap" style="vertical-align: top;">
          <div class="login">
            <a href="ajuda.php" target="_blank">Ajuda</a> | Utilizador: istxxxxxx | <a href="https://id.ist.utl.pt/cas/logout">Sair</a>
          </div>
        </td>
      </tr>
      <tr>
        <td align="right" nowrap="nowrap" width="40%"> Versão 0.1 </td>
      </tr>
</table>

</div>

  <div id="mainnav">
    <ul>
      <li>
        <a href="index.php"><span>Início</span><div class="lic1"></div></a>
      </li>

      <li class="selected">
        <a href="assinatura.php"><span>Assinatura</span><div class="lic1"></div></a>
      </li>
      <li>
        <a href="validador.php"><span>Validador</span><div class="lic1"></div></a>
        <a href="notariado.php"><span>Notariado</span><div class="lic1"></div></a>
      </li>
    </ul>

  <div class="c1"></div>
  <div class="c2"></div>
</div>

  <div id="container2">
    <div id="secnav">
      <ul>
        <li class="navsublist"><a href=""><span>Assinatura de Documentos</span></a></li>
      </ul>
  </div>

  <div id="container3">
    <div id="content">
      <h2>Assinatura</h2>
      <applet code=AssinaturaElectronicaQualificada.class archive="aeq.jar" width="800px" height="600px">
      <param name=foo value="bar">
    </applet>
   <h4> Requisitos </h4>
   <p>Para utilizar esta aplicação é necessário ter instalado o <b>Java Runtime Environment 6</b> e a <b>Aplicação do Cartão de Cidadão</b> que podem ser obtidos em <a href="http://www.cartaodecidadao.pt">http://www.cartaodecidadao.pt</a> e em <a href="http://www.sun.com/java">http://www.sun.com/java</a>, e dispor de um leitor de cartões SmartCard.</p>
     <p>Certifique-se também que o seu browser tem o suporte de Java activo.</p>
    </div>
  </div>

  <div id="footer">

      <div class="c1"></div>
      <div class="c2"></div>
      <div class="c3"></div>

      <div class="c4"></div>
        <p>&copy;2009 Instituto Superior Técnico</p>
      </div>

      <div class="cont_c1"></div>
      <div class="cont_c2"></div>
    </div>
  </div>

</body>
</html>
