<?php
  //error_reporting(0);
 chdir("bin");

  function XAdESValidator($filename){
    $cmd = "java -cp xmlsec-1.4.3.jar:commons-logging-1.1.1.jar:. XAdESValidator " . $filename . " -sig -xsd -val"; //-rev
    return shell_exec($cmd);
  }

  function XAdESCounterSign($filename){
    $cmd = "java XAdESCounterSigner " . $filename . " istcs.p12 bundlepassword privatekeypassword";
    return shell_exec($cmd);
  }

  function XAdESSignatureTimeStamper($filename){
    $cmd = "java XAdESSignatureTimeStamper " . $filename . " http://tsp.iaik.tugraz.at/tsp/TspRequest";
    return shell_exec($cmd);
  }

  $con = mysql_connect('localhost','aeq','aeqdocs');
  if (!$con) die('Erro a ligar à base de dados: ' . mysql_error());
  mysql_select_db("aeq", $con);

  if(isset($_GET['do'])){

    $do = $_GET['do'];
    if(isset($_GET['id']))
      $id = mysql_real_escape_string($_GET['id']);

    //SUBMIT
    if($do == 'submit'){
      //validate
      $tmpfilename = $_FILES['userfile']['tmp_name'];
      $res = XAdESValidator($tmpfilename);
      $alert = "XAdESValidator: " . $res;

      if(preg_match('/^SUCCESS$/',$res)){
        $signature = "";
        $file = fopen($tmpfilename, "r");
        while(!feof($file)) {
          $signature = $signature . fgets($file, 4096);
        }
        mysql_query("INSERT INTO Assinaturas VALUES (null,'$signature',null)",$con) or die ("Erro a inserir na BD: " . mysql_error());
      }
    }elseif($do == 'countersign' && isset($id)){  //COUNTERSIGN
      //echo 'CounterSign :' . $id;
      $dbsignature = mysql_query("SELECT signature FROM Assinaturas WHERE id='$id'",$con);
      if($row = mysql_fetch_array($dbsignature)){

        $tmpfilename = tempnam  ("/tmp", "XAdES-");
        $file = fopen($tmpfilename,"w");
        fwrite($file,$row['signature']);
        fclose($file);
        $res = XAdESCounterSign($tmpfilename);
        $alert = "XAdESCounterSigner: " . $res;

        if(preg_match('/^SUCCESS$/',$res)){
          $file = fopen($tmpfilename,"r");
          while(!feof($file)){
            $countersignature = $countersignature . fgets($file,4096);
          }
          fclose($file);
          mysql_query("UPDATE Assinaturas SET countersignature='$countersignature' WHERE id ='$id'",$con) or die ("Erro a inserir na BD: " . mysql_error());
         }
       }
      }elseif($do == 'timestamp' && isset($id)){  //TIMESTAMP
        //echo 'SignatureTimeStamp :' . $id;
        $dbsignature = mysql_query("SELECT countersignature FROM Assinaturas WHERE id='$id'",$con);
        if($row = mysql_fetch_array($dbsignature)){
          $tmpfilename = tempnam  ("/tmp", "XAdES-TS-");
          $file = fopen($tmpfilename,"w");
          fwrite($file,$row['countersignature']);
          fclose($file);
          $res = XAdESSignatureTimeStamper($tmpfilename);
          $alert = "XAdESSignatureTimeStamper: " . $res;

          if(preg_match('/^SUCCESS$/',$res)){
            $file = fopen($tmpfilename,"r");
            while(!feof($file)){
              $countersignature = $countersignature . fgets($file,4096);
            }
            fclose($file);
            mysql_query("UPDATE Assinaturas SET countersignature='$countersignature' WHERE id ='$id'",$con);
          }
        }
    }elseif($do == 'getsignature' && isset($id)){ // GETSIGNATURE
      $signature = mysql_query('SELECT signature FROM Assinaturas WHERE id=\'' . $id . '\'',$con);
      if($row = mysql_fetch_array($signature)){
        preg_match('/<FileContent Id="(.*?)">/',$row['signature'],$filename);
        Header("Content-type: text/xml");
        Header("Content-Disposition: attachment; filename=" . $filename[1] .".sig.xml");
        print_r($row['signature']);
        exit(0);
     }
    }elseif($do == 'getcountersignature' && isset($id)){ // GETCOUNTERSIGNATURE
      $csignature = mysql_query('SELECT countersignature FROM Assinaturas WHERE id=\'' . $id . '\'',$con);
      if($row = mysql_fetch_array($csignature)){
        preg_match('/<FileContent Id="(.*?)">/',$row['countersignature'],$filename);
        Header("Content-type: text/xml");
        Header("Content-Disposition: attachment; filename=" . $filename[1] .".csig.xml");
        print_r($row['countersignature']);
        exit(0);
      }
    }elseif($do == 'getfile' && isset($id)){ // GETFILE
      $signature = mysql_query('SELECT signature FROM Assinaturas WHERE id=\'' . $id . '\'',$con);
      if($row = mysql_fetch_array($signature)){
        preg_match('/<FileContent Id="(.*?)">/',$row['signature'],$filename);
        preg_match('/<MimeType>(.*?)<\/MimeType>/',$row['signature'],$mimetype);
        preg_match('/<FileContent Id=".*">(.*?)<\/FileContent>/',$row['signature'],$filecontent);

        Header("Content-type: " . $mimetype[1]);
        Header("Content-Disposition: attachment; filename=" . $filename[1]);
        print_r(base64_decode($filecontent[1]));
        exit(0);
      }
    }elseif($do == 'getsignercertificate' && isset($id)){ // GETSIGNERCERTIFICATE
      $signature = mysql_query("SELECT signature FROM Assinaturas WHERE id='$id'" ,$con);
      if($row = mysql_fetch_array($signature)){
        preg_match('/<ds:X509Certificate>(.*)<\/ds:X509Certificate>/',$row['signature'],$filecontent);
        Header("Content-type: application/x-x509-ca-cert");
        Header("Content-Disposition: attachment; filename=cert.der");
        //print_r(base64_decode($filecontent[1]));
        print_r($filecontent[1]);
        exit(0);
      }
    }
  }

  $signatures = mysql_query("SELECT * FROM Assinaturas");

  mysql_close($con);

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

  <script LANGUAGE="JavaScript">
    function MsgAlerta (text) {
      alert (text)
    }
</script>
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
        <a href="validador.php"><span>Validador</span><div class="lic1"></div></a>
      </li>

      <li class="selected">
        <a href="notariado.php"><span>Notariado</span><div class="lic1"></div></a>
      </li>
    </ul>

  <div class="c1"></div>
  <div class="c2"></div>
</div>

  <div id="container2">
    <div id="secnav">
      <ul>
        <li class="navsublist"><a href="notariado.php"><span>Serviço de Notariado</span></a></li>
      </ul>
  </div>

  <div id="container3">
    <div id="content">
      <h2>Notariado</h2>

	<table class="form">
          <tr><td><b>ID</b></td><td><b>Ficheiros</b></td> <td><b>Informação</b></td> <td><b>Acções</b></td></tr>
<?php
while($row = mysql_fetch_array($signatures))
  {
  preg_match('/\<FileContent Id="(.*?)"\>/',$row['signature'],$filename);
  preg_match('/\<SigningTime\>(.*?)\<\/SigningTime\>/',$row['signature'], $signingtime);
  $signingtime = preg_replace('/T/',' ',$signingtime);
  preg_match('/\<ds:X509SubjectName\>CN=([^,]*).*\<\/ds:X509SubjectName\>/',$row['signature'], $subjectCN);
  if(empty($subjectCN[1]))
    preg_match('/\<ds:X509SubjectName\>.*CN=(.*)\<\/ds:X509SubjectName\>/',$row['signature'], $subjectCN);
  preg_match('/\<ds:X509IssuerName\>CN=([^,]*).*\<\/ds:X509IssuerName\>/',$row['signature'], $issuerCN);
  if(empty($issuerCN[1]))
    preg_match('/\<ds:X509IssuerName\>.*CN=(.*)\<\/ds:X509IssuerName\>/',$row['signature'], $issuerCN);
  preg_match('/\<ClaimedRole\>(.*?)\<\/ClaimedRole\>/',$row['signature'], $claimedrole);
  preg_match('/\<Description\>(.*?)\<\/Description\>/',$row['signature'], $filedescription);
  preg_match('/\<CommitmentTypeQualifier\>(.*)\<\/CommitmentTypeQualifier\>/',$row['signature'], $commitment);

  echo "<tr>";
  echo "<td>" . $row['id'] . "</td>";
  echo "<td>";
  echo "<a href=\"notariado.php?do=getfile&id=" . $row['id'] . "\">" . $filename[1] . "</a><br/>";
  echo "<a href=\"notariado.php?do=getsignature&id=" . $row['id'] . "\">" . $filename[1] . ".sig.xml" . "</a><br/>";
  if(!empty($row['countersignature']))
    echo "<a href=\"notariado.php?do=getcountersignature&id=" . $row['id'] . "\">" . $filename[1] . ".csig.xml" . "</a>";
  echo "</td>";
  echo "<td>";
  echo "<b>Assinante</b><br/>";
  echo "<b>Cidadão: </b>" . $subjectCN[1] . "<br/><b>Emissor: </b>" . $issuerCN[1] . "<br/><br/>";
  echo "<b>Propriedades Qualificantes Reclamadas</b><br/>";
  echo "<b>Data: </b>" . $signingtime[1] . "<br/>";
  echo "<b>Descrição: </b>" . $filedescription[1] . "<br/>";
  echo "<b>Papel: </b>" . $claimedrole[1] . "<br/>";
  echo "<b>Compromisso: </b>" . $commitment[1] . "<br/>";
  echo "</td>";
  echo "<td>";
//   echo "<a href=\"notariado.php?do=getsignercertificate&id=" . $row['id'] . "\"><img width=\"48px\" src=\"images/certificate.jpg\"/></a>";
//   echo "<a href=\"notariado.php?do=getsignercertificate&id=" . $row['id'] . "\"><br/>Certificado</a>";
  echo "<a href=\"notariado.php?do=countersign&id=" . $row['id'] . "\"><img src=\"images/signature-48x48.png\"/></a>";
  echo "<a href=\"notariado.php?do=countersign&id=" . $row['id'] . "\"><br/>Contra Assinar</a><br/>";
  //Enable SignatureTimeStamps after CounterSignature
  if(!empty($row['countersignature'])){
    echo "<a href=\"notariado.php?do=timestamp&id=" . $row['id'] . "\"><img src=\"images/timestamp.jpg\"/></a>";
    echo "<a href=\"notariado.php?do=timestamp&id=" . $row['id'] . "\"><br/>Carimbo Temporal</a>";
  }
  echo "</td>";
  echo "</tr>";
  }
?>
        </table>
  <h3>Submeter Assinatura</h3>
        <form enctype="multipart/form-data" method="POST" action="notariado.php?do=submit">
          <table><tr><td><input type="file" name="userfile" size="60"></td><td><input type="submit" value="Enviar"></td></table>
        </form>
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