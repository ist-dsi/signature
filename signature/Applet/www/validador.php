<?php
  //error_reporting(0);
 session_start();
 chdir("bin");

  function XAdESValidator($filename){
    $cmd = "java -cp xmlsec-1.4.3.jar:commons-logging-1.1.1.jar:. XAdESValidator " . $filename . " -sig -xsd -val"; //-rev
    return shell_exec($cmd);
  }

  if(isset($_GET['do'])){

    $do = $_GET['do'];

    //SUBMIT
    if($do == 'submit'){
      $tmpfilename = tempnam("/tmp","XAdES-V-");
      $filename = $_FILES['userfile']['tmp_name'];
      $_SESSION['filename'] = $tmpfilename;

      copy($filename,$tmpfilename);

      $res = XAdESValidator($tmpfilename);

      //if(preg_match('/^SUCCESS$/',$res)){
        $signature = "";
        $file = fopen($filename, "r");
        while(!feof($file)) {
          $signature = $signature . fgets($file, 4096);
        }
      //}
    }elseif($do == 'getfile'){ // GETFILE
        $signature = "";
        $file = fopen($_SESSION['filename'], "r");
        while(!feof($file)) {
          $signature = $signature . fgets($file, 4096);
        }
        preg_match('/<FileContent Id="(.*?)">/',$signature,$filename);
        preg_match('/<MimeType>(.*?)<\/MimeType>/',$signature,$mimetype);
        preg_match('/<FileContent Id=".*">(.*?)<\/FileContent>/',$signature,$filecontent);

        Header("Content-type: " . $mimetype[1]);
        Header("Content-Disposition: attachment; filename=" . $filename[1]);
        print_r(base64_decode($filecontent[1]));
        exit(0);
    }
  }

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="pt-PT" xml:lang="pt-PT">
<head>
  <title>Assinatura Electrónica Qualificada</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <link rel="shortcut icon" type="image/ico" href="./images/favicon.ico">
  <link rel="stylesheet" type="text/css" href="./css/screen.css" media="screen"/>
  <link rel="stylesheet" type="text/css" href="./css/print.css" media="print"/>

</head>

<body <?php if(isset($alert)){
                         $alert = trim($alert);
                         echo "onLoad=\"MsgAlerta('$alert')\"";  } ?>>

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
        <a href="assinatura.php"><span>Assinatura</span><div class="lic1"></div></a>
      </li>

      <li class="selected">
        <a href="validador.php"><span>Validador</span><div class="lic1"></div></a>
      </li>

      <li>
        <a href="notariado.php"><span>Notariado</span><div class="lic1"></div></a>
      </li>
    </ul>

  <div class="c1"></div>
  <div class="c2"></div>
</div>

  <div id="container2">
    <div id="secnav">
      <ul>
        <li class="navsublist"><a href="validador.php"><span>Validador de Assinaturas</span></a></li>
      </ul>
  </div>

  <div id="container3">
    <div id="content">
      <h2>Validador</h2>

<?php if(!isset($_GET['do'])){

  echo '<table class=\"form\">';
  echo '<p>Para utilizar o validador bastará submeter o ficheiro (extensão .sig.xml) com o documento e a assinatura que deseja validar.</p>';
  echo '<h3>Ficheiro</h3>';
  echo '<form enctype="multipart/form-data" method="POST" action="validador.php?do=submit">';
  echo '<table><tr><td><input type="file" name="userfile" size="60"></td><td><input type="submit" value="Enviar"></td></table>';
  echo '</form></table>';

}else{

   preg_match('/\<FileContent Id="(.*?)"\>/',$signature,$filename);
   preg_match('/\<SigningTime\>(.*?)\<\/SigningTime\>/',$signature, $signingtime);
   $signingtime = preg_replace('/T/',' ',$signingtime);
   preg_match('/\<ds:X509SubjectName\>CN=([^,]*).*\<\/ds:X509SubjectName\>/',$signature, $subjectCN);
   if(empty($subjectCN[1]))
     preg_match('/\<ds:X509SubjectName\>.*CN=(.*)\<\/ds:X509SubjectName\>/',$signature, $subjectCN);
   preg_match('/\<ds:X509IssuerName\>CN=([^,]*).*\<\/ds:X509IssuerName\>/',$signature, $issuerCN);
   if(empty($issuerCN[1]))
     preg_match('/\<ds:X509IssuerName\>.*CN=(.*)\<\/ds:X509IssuerName\>/',$signature, $issuerCN);
   preg_match('/\<ClaimedRole\>(.*?)\<\/ClaimedRole\>/',$signature, $claimedrole);
   preg_match('/\<Description\>(.*?)\<\/Description\>/',$signature, $filedescription);
   preg_match('/\<CommitmentTypeQualifier\>(.*)\<\/CommitmentTypeQualifier\>/',$signature, $commitment);

echo '<p>';

     if(preg_match('/^SUCCESS$/',$res))
       echo '<img src="/images/ok.png" /><b> Assinatura Válida </b> <br/><br/>';
     else
       echo '<img src="/images/error.png" /><b> Assinatura Inválida </b> <br/><br/>';

   echo '<b>Ficheiro: </b><a href="validador.php?do=getfile"> ' . htmlentities($filename[1],ENT_COMPAT,"UTF-8") . "</a> <br/><br/>";
   echo "<b>Assinante</b><br/>";
   echo "<b>Cidadão: </b>" . htmlentities($subjectCN[1],ENT_COMPAT,"UTF-8") . "<br/><b>Emissor: </b>" . htmlentities($issuerCN[1],ENT_COMPAT,"UTF-8") . "<br/><br/>";
   echo "<b>Propriedades Qualificantes Reclamadas</b><br/>";
   echo "<b>Data: </b>" . htmlentities($signingtime[1],ENT_COMPAT,"UTF-8") . "<br/>";
   echo "<b>Descrição: </b>" . htmlentities($filedescription[1],ENT_COMPAT,"UTF-8") . "<br/>";
   echo "<b>Papel: </b>" . htmlentities($claimedrole[1],ENT_COMPAT,"UTF-8") . "<br/>";
   echo "<b>Compromisso: </b>" . htmlentities($commitment[1],ENT_COMPAT,"UTF-8") . "<br/>";
   echo '</p>';
  }

?>

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