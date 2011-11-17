package aeq;

import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.security.KeyStoreException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import netscape.javascript.JSObject;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.swing.ScalableXHTMLPanel;
import org.xhtmlrenderer.util.XRRuntimeException;


/*
 * Created on Apr 19, 2010, 6:23:40 PM
 *
 * @author Diogo Figueiredo
 */
public class MainWindow extends JApplet {

    private XAdESSigner signer;
    private final String tsaURL = "http://tsp.iaik.tugraz.at/tsp/TspRequest";

    private boolean canSign = false;
    private boolean hasSigned = false;

    private boolean ableToUseJavaScript = false;

    //    private final String signContentURL;
    //    final private String serverURL;
    final private String redirectURL;

    //    private File contentFile;
    private String contentString;
    private String xhtmlContentString;
    private String xmlContentString;

    private final JFileChooser fileChooser = new JFileChooser();

    private byte[] signedContent;
    //    private File signatureFile;

    /**
     * Field that controls if the application is running in 'debug' mode or not
     */
    boolean debugMode = true;
    private static MainWindow mainWindowInstance;

    private final AppletContext appContext;
    private String intentionString;
    private String descriptionString;
    private String signatureId;
    private String roleString;
    private JSObject window;

    public MainWindow(AppletContext appContext, String redirectURL) {
	this.appContext = appContext;

	//	this.serverURL = serverURL;
	this.redirectURL = redirectURL;

	init();
    }

    public static MainWindow getInstance() {
	return mainWindowInstance;
    }

    @Override
    public final void init() {
	mainWindowInstance = this;
	/*
	 * try { contentFile = File.createTempFile("content", "_mixed.cntnt"); }
	 * catch (IOException e) { JOptionPane.showMessageDialog(this,
	 * "Could not init the temporary content file", "Error - creating file",
	 * JOptionPane.ERROR_MESSAGE); e.printStackTrace(); } signatureFile =
	 * new File(contentFile.getName() + ".sig.xml");
	 * 
	 * try { UIManager.setLookAndFeel(
	 * "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); } catch
	 * (Exception ex) { Logger.getLogger(this.getName()).log(Level.SEVERE,
	 * null, ex); }
	 */
	try {
	    java.awt.EventQueue.invokeAndWait(new Runnable() {
		@Override
		public void run() {
		    initComponents();
		    getSignContent();
		    setupComponents();
		    initSigner(false);

		    //   pack();
		    //		    setSize(930, 600);
		    //   setExtendedState(JFrame.MAXIMIZED_BOTH);
		    //  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	    });
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    protected void setupComponents() {
	System.out.println("Creating xhtml panel..");
	ScalableXHTMLPanel view = new ScalableXHTMLPanel();
	view.setCenteredPagedView(true);
	view.setBackground(Color.WHITE);

	int text_width = 200;
	view.setPreferredSize(new Dimension(text_width, text_width));

	try {
	    System.out.println("Loading document..");
	    //	    ByteArrayInputStream inputStream = new ByteArrayInputStream(contentFileString.getBytes());
	    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    //by default the contentString is exactly the xhtmlContentString! in the future, this might change
	    xhtmlContentString = contentString;

	    Document document = XMLResource.load(new StringReader(xhtmlContentString)).getDocument();
	    System.out.println("Parsed, now loading document..");

	    try {
		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		view.setDocument(document);

		System.out.println("Document loaded..");

	    } catch (XRRuntimeException ex) {
		System.out.println("Cant load document..");
		ex.printStackTrace();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }

	    FSScrollPane scroll = new FSScrollPane(view);

	    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
	    jPanel2.setLayout(jPanel2Layout);
	    jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		    .addGroup(
			    jPanel2Layout
				    .createSequentialGroup()
				    .addContainerGap()
				    .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE,
					    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
	    jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
		    jPanel2Layout
			    .createSequentialGroup()
			    .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
				    Short.MAX_VALUE).addContainerGap()));

	} catch (IndexOutOfBoundsException iex) {
	    debugln("Error, probably reading the XHTML content");
	    JOptionPane.showMessageDialog(this, "Could not read the XHTML content", "Error reading XHTML content",
		    JOptionPane.ERROR_MESSAGE);

	} catch (Exception ex) {
	    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
	    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error - generic exception", JOptionPane.ERROR_MESSAGE);
	}
    }

    protected void cleanUI() {
	identityField.setText(null);
	identityEmissionField.setText(null);
	identityValidadeField.setText(null);
    }

    private void debugln(String string) {
	if (debugMode)
	    System.out.println("**DEBUG**: " + string);
    }

    protected void getSignContent() {
	try {
	    debugln("Going to fetch the content");
	    //let's try to use JavaScript first
	    window = JSObject.getWindow((java.applet.Applet) this.getParent());
	    String content64BaseEncoded = (String) window.eval("getContent()");
	    if (content64BaseEncoded != null && content64BaseEncoded.length() > 2) {

		ableToUseJavaScript = true;

		byte[] byteContent = Base64.decode(content64BaseEncoded);
		contentString = new String(byteContent, Charset.forName("UTF-8"));
		//		debugln("Got the content through JS call, result: " + contentString);
	    }
	} catch (Exception ex) {
	    ableToUseJavaScript = false;
	    if (debugMode)
		ex.printStackTrace();
	}
	try {

	    if (!ableToUseJavaScript) {
		//TODO make the authentication, etc. 
		//TODO implement this!
		throw new Error("Erro de sistema! Impossivel fazer chamada de JavaScript, por favor contacte o suporte");

		//		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(signContentURL).openStream()));

		//	    BufferedWriter contentFileWriter = new BufferedWriter(new FileWriter(contentFile));

		//		StringBuilder sb = new StringBuilder();
		//		String inputLine;
		//		while ((inputLine = in.readLine()) != null) {
		//		    sb.append(inputLine + "\n");
		    //		contentFileWriter.write(inputLine + "\n");
		//		}

		//		contentString = sb.toString();

		//	    contentFileWriter.close();
		//		in.close();
		//		debugln("Got the content through HTTP request, couldn't make it through JS, result: " + contentString);

	    }
	    //let's get the original XML content to get the: signatureId; intentionString; descriptionString; roleString;
	    //we can do that, because we will always have the first span of the document as a non displayed one with the start
	    //of the id with the name AllContent_

	    //now let's interpret it
	    this.xhtmlContentString = this.contentString;
	    Document document = XMLResource.load(new StringReader(xhtmlContentString)).getDocument();
	    NodeList allSpans = document.getElementsByTagName("span");
				Node nodeWithNeededElements =  null;
	    
	    for (int i=0;i<allSpans.getLength();i++)
	    {
		//iterating through all spans and getting the first one whose id starts with AllContent
		Node nodeBeingInspected = allSpans.item(i);
		if (nodeBeingInspected.getAttributes() != null)
		{
		    Node id = nodeBeingInspected.getAttributes().getNamedItem("id");
		    if (id != null)
		    {
			if(id.getNodeValue() == null || id.getNodeValue().isEmpty())
			{
			    continue;
			}
			else {
			    //let's inspect it and see if we have what we came for
			    if (id.getNodeValue().startsWith("AllContent"))
			    {
				debugln("Supposedly found the superNode! content: " + nodeBeingInspected.getTextContent());
				//this is it, let's extract the needed things from here
				//we can assume that the next 'node' is actually gonna be the node with the attributes we need!
				//so we don't have to parse things again
				nodeWithNeededElements = nodeBeingInspected.getFirstChild();
				NamedNodeMap neededAttributesNodeMap = nodeWithNeededElements.getAttributes();
				this.signatureId = neededAttributesNodeMap.getNamedItem("signatureId") != null ? neededAttributesNodeMap
					.getNamedItem("signatureId").getNodeValue() : "";
				this.intentionString = neededAttributesNodeMap.getNamedItem("intention") != null ? neededAttributesNodeMap
					.getNamedItem("intention").getNodeValue() : "";
				this.descriptionString = neededAttributesNodeMap.getNamedItem("description") != null ? neededAttributesNodeMap
					.getNamedItem("description").getNodeValue() : "";
				this.roleString = neededAttributesNodeMap.getNamedItem("roleOfSigner") != null ? neededAttributesNodeMap
					.getNamedItem("roleOfSigner").getNodeValue() : "";
				
			    }
			}
		    }
		}
	    }
	    debugln("Document element signatureId attribute: " + signatureId);
	    debugln("Document element intention content: " + intentionString);
	    debugln("Document element description content: " + descriptionString);
	    debugln("Document element roleString content: " + roleString);
	    if (signatureId == null || intentionString == null || descriptionString == null || signatureId.isEmpty()
		    || intentionString.isEmpty() || descriptionString.isEmpty())
		throw new RuntimeException("Conteudo da assinatura mal formado, id, intenção ou descrição não foram encontrados");
	    debugln("Parsed the XML");

	    canSign = true;

	    //	} catch (MalformedURLException ex) {
	    //	    Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
	    //	    JOptionPane.showMessageDialog(this, ex, "Erro", JOptionPane.ERROR_MESSAGE);
	    //	} catch (IOException ex) {
	    //	    Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
	    //	    JOptionPane.showMessageDialog(this, ex, "Erro", JOptionPane.ERROR_MESSAGE);
	} catch (RuntimeException ex) {
	    Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
	    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);

	}
    }

    protected void sign() {

	if (ableToUseJavaScript) {

	    try {
		performSign();

		//		JOptionPane.showMessageDialog(this, "Javascript envocado, o formulario ja deve ter os dados certos", "Teste",
		//			JOptionPane.INFORMATION_MESSAGE);
	    } catch (Exception ex) {

		JOptionPane.showMessageDialog(this,
			"Erro técnico, por favor entre em contacto com o suporte. Não foi possível assinar o conteudo", "Erro",
			JOptionPane.ERROR_MESSAGE);
		debugln(ex.getMessage());
	    }

	} else {
	    JOptionPane.showMessageDialog(this,
		    "Erro técnico, por favor entre em contacto com o suporte. Não foi possível invocar JavaScript", "Erro",
		    JOptionPane.ERROR_MESSAGE);

	}
	// delete the temporary file
	//	contentFile.delete();

	//	JOptionPane.showMessageDialog(this, "Assinatura concluída e enviada, Obrigado!", "Assinatura Digital",
	//		JOptionPane.INFORMATION_MESSAGE);

	// redirect page
	//	System.out.println("Redirecting page..");
	//	try {
	//	    appContext.showDocument(new URL(redirectURL));
	//	} catch (MalformedURLException ex) {
	//	    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
	//	}
	//	System.out.println("Whooatt?");
    }

    protected void performSign() {

	try {

	    desactivateSign();

	    if (!canSign) {
		return;
	    }

	    if (!initSigner(true)) {
		return;
	    }

	    //let's make sure the signer understands what and why he is signing this document
	    String message = "Assinar este documento corresponde a: \"" + intentionString
		    + "\" tem a certeza que pretende continuar?";
	    int response = JOptionPane.showConfirmDialog(this, message, "Assinatura", JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE);
	    if (response == JOptionPane.NO_OPTION)
		return;

	    //	    BufferedWriter signatureFileWriter = new BufferedWriter(new FileWriter(signatureFile));

	    // do the signature
	    signedContent = signer.sign(contentString.getBytes(Charset.forName("UTF-8")), signatureId, roleString,
		    descriptionString, intentionString,
		    new MimeType("application", "signature"));



	    // add the timestamp
	    XAdESSignatureTimeStamper ts = new XAdESSignatureTimeStamper();
	    Object[] timeStamperReturn = ts.signatureTimeStamp(signedContent, tsaURL);
	    if (timeStamperReturn != null) {
		signedContent = (byte[]) timeStamperReturn[0];
		timestampMarkerTextField.setText(((Date) timeStamperReturn[1]).toLocaleString());
	    }
	    else {
		JOptionPane.showMessageDialog(this, "Falhou a criação da assinatura", "Erro", JOptionPane.ERROR_MESSAGE);
		activateSign();
		return;
	    }

		JOptionPane.showMessageDialog(this, "Assinatura realizada com sucesso", "Sucesso",
			JOptionPane.INFORMATION_MESSAGE);

		hasSigned = true;

	} catch (KeyStoreException ex) {
	    JOptionPane.showMessageDialog(this, "Erro a aceder ao Cartão de Cidadão", "Erro", JOptionPane.ERROR_MESSAGE);
	    Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
	} catch (Exception ex) {
	    Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
	    JOptionPane.showMessageDialog(this, ex, "Erro", JOptionPane.ERROR_MESSAGE);
	} finally {
	    activateSign();
	    if (hasSigned) {
		submitButton.setEnabled(true);
		saveSignatureButton.setEnabled(true);
	    }
	}
    }

    //TODO reconstruct this to use javascript calls if possible
    protected void commitSign() {
	if (!hasSigned) {
	    return;
	}

	//encode the content in Base64
	String encodedSignature = Base64.encodeBytes(signedContent);

	//let's put it in the textArea

	window.eval("writeContentToInput(\"" + encodedSignature + "\")");
	window.eval("submitForm()");

	//	try {
	//	    ClientHttpRequest client = new ClientHttpRequest(serverURL);
	//	    client.
	//	    client.setParameter("originalFile", );
	//	    client.setParameter("signedFile", contentFile);

	//	    InputStream stream = client.post();

	//	    byte[] response = new byte[1024];
	//	    stream.read(response);
	//	} catch (MalformedURLException ex) {
	//	    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
	//	} catch (IOException ex) {
	//	    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
	//	} catch (Exception ex) {
	//	    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
	//	}
    }

    protected boolean initSigner(boolean alert) {
	cleanUI();

	try {
	    signer = new XAdESSigner();

	} catch (Exception ex) {
	    if (alert) {
		JOptionPane
			.showMessageDialog(this, "Não foi possível ler o Cartão de Cidadão", "Erro", JOptionPane.ERROR_MESSAGE);
		if (Applet.debugMode) {
		    JOptionPane.showMessageDialog(this, "Excepção: " + ex.getLocalizedMessage(), "Informação de Erro",
			    JOptionPane.INFORMATION_MESSAGE);
		}
	    }
	    Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);

	    desactivateSign();
	    return false;
	}

	identityField.setText(signer.getCertificado().getSubjectCN());
	identityEmissionField.setText(signer.getCertificado().getIssuerCN());
	identityValidadeField.setText(signer.getCertificado().getX509Cert().getNotAfter().toString());

	activateSign();

	return true;
    }

    protected void activateSign() {
	toggleSign(true);
    }

    protected void desactivateSign() {
	toggleSign(false);
    }

    private void toggleSign(boolean b) {
	signButton.setEnabled(b);
    }

    /**
     * This method is called from within the init() method to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        identityField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        identityEmissionField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        identityValidadeField = new javax.swing.JTextField();
        readCardButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        signButton = new javax.swing.JButton();
        submitButton = new javax.swing.JButton();
        saveSignatureButton = new javax.swing.JButton();
        timestampLabel = new javax.swing.JLabel();
        timestampMarkerTextField = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(910, 800));

	jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Conteúdo a Assinar"));
        jPanel2.setMinimumSize(new java.awt.Dimension(700, 500));
        jPanel2.setSize(new java.awt.Dimension(890, 714));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 898, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 598, Short.MAX_VALUE)
        );

        jLabel2.setText("Identidade:");

        identityField.setEditable(false);
        identityField.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        identityField.addActionListener(new java.awt.event.ActionListener() {
            @Override
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
                identityFieldActionPerformed(evt);
            }
        });

        jLabel3.setText("Emissor:");

        identityEmissionField.setEditable(false);
        identityEmissionField.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        identityEmissionField.addActionListener(new java.awt.event.ActionListener() {
            @Override
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
                identityEmissionFieldActionPerformed(evt);
            }
        });

        jLabel4.setText("Validade:");

        identityValidadeField.setEditable(false);
        identityValidadeField.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        identityValidadeField.addActionListener(new java.awt.event.ActionListener() {
            @Override
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
                identityValidadeFieldActionPerformed(evt);
            }
        });

	readCardButton.setText("Ler Cartão");
        readCardButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
	    public void mouseClicked(java.awt.event.MouseEvent evt) {
                readCardButtonMouseClicked(evt);
            }
        });
        readCardButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
                readCardButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addComponent(identityField, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(identityValidadeField, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(identityEmissionField, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(readCardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(identityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(identityValidadeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(identityEmissionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(readCardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        signButton.setText("Assinar");
        signButton.setEnabled(false);
        signButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
	    public void mouseClicked(java.awt.event.MouseEvent evt) {
                signButtonMouseClicked(evt);
            }
        });
        signButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
                signButtonActionPerformed(evt);
            }
        });

        submitButton.setText("Submeter assinatura");
        submitButton.setEnabled(false);
        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
	    public void mouseClicked(java.awt.event.MouseEvent evt) {
                submitButtonMouseClicked(evt);
            }
        });
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        saveSignatureButton.setText("Guardar assinatura");
        saveSignatureButton.setEnabled(false);
        saveSignatureButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
	    public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveSignatureButtonMouseClicked(evt);
            }
        });
        saveSignatureButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSignatureButtonActionPerformed(evt);
            }
        });

        timestampLabel.setText("Carimbo temporal:");
        timestampLabel.setName("timestampLabel"); // NOI18N

        timestampMarkerTextField.setText("Inexistente");
        timestampMarkerTextField.setEnabled(false);
        timestampMarkerTextField.addActionListener(new java.awt.event.ActionListener() {
            @Override
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
                timestampMarkerTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(timestampLabel)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addComponent(saveSignatureButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(signButton, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(submitButton, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                        .addGap(210, 210, 210))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(timestampMarkerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(signButton, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(saveSignatureButton, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(submitButton, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(timestampMarkerTextField, 0, 0, Short.MAX_VALUE)
                    .addComponent(timestampLabel)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.getAccessibleContext().setAccessibleName("PainelAssinar");
    }// </editor-fold>//GEN-END:initComponents

    private void identityFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_identityFieldActionPerformed
	// TODO add your handling code here:
    }//GEN-LAST:event_identityFieldActionPerformed

    private void identityEmissionFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_identityEmissionFieldActionPerformed
	// TODO add your handling code here:
    }//GEN-LAST:event_identityEmissionFieldActionPerformed

    private void identityValidadeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_identityValidadeFieldActionPerformed
	// TODO add your handling code here:
    }//GEN-LAST:event_identityValidadeFieldActionPerformed

    private void signButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signButtonActionPerformed
	sign();
    }//GEN-LAST:event_signButtonActionPerformed

    private void readCardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readCardButtonActionPerformed
	// TODO add your handling code here:
    }//GEN-LAST:event_readCardButtonActionPerformed

    private void signButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_signButtonMouseClicked
    }//GEN-LAST:event_signButtonMouseClicked

    private void readCardButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readCardButtonMouseClicked
	initSigner(true);
    }//GEN-LAST:event_readCardButtonMouseClicked

    private void submitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitButtonMouseClicked
    }//GEN-LAST:event_submitButtonMouseClicked

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
	commitSign();
    }//GEN-LAST:event_submitButtonActionPerformed

    private void saveSignatureButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveSignatureButtonMouseClicked
	// TODO add your handling code here:
    }//GEN-LAST:event_saveSignatureButtonMouseClicked

    private void saveSignatureButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSignatureButtonActionPerformed
	fileChooser.setSelectedFile(new File(signatureId + ".sig"));
	int returnVal = fileChooser.showSaveDialog(this);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    File file = fileChooser.getSelectedFile();
	    debugln("Saving: " + file.getName() + ".");

	    FileOutputStream fos = null;
	    try {
		fos = new FileOutputStream(file);
		fos.write(signedContent);
		    fos.flush();
		    fos.close();
	    } catch (FileNotFoundException e) {
		JOptionPane.showMessageDialog(this, "Não foi possivel guardar o ficheiro. Causa: " + e.getMessage(),
			"Erro ao guardar", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	    } catch (IOException e) {
		JOptionPane.showMessageDialog(this, "Não foi possivel guardar o ficheiro. Causa: " + e.getMessage(),
			"Erro ao guardar", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
		return;

	    }

	    JOptionPane.showMessageDialog(this, "Ficheiro guardado! (não se esqueça de submeter a assinatura para o sistema)",
		    "Ficheiro gravado", JOptionPane.INFORMATION_MESSAGE);

	} else {
	    debugln("Save command cancelled by user.");
	}

    }//GEN-LAST:event_saveSignatureButtonActionPerformed

    private void timestampMarkerTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timestampMarkerTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_timestampMarkerTextFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField identityEmissionField;
    private javax.swing.JTextField identityField;
    private javax.swing.JTextField identityValidadeField;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JButton readCardButton;
    private javax.swing.JButton saveSignatureButton;
    private javax.swing.JButton signButton;
    private javax.swing.JButton submitButton;
    private javax.swing.JLabel timestampLabel;
    private javax.swing.JTextField timestampMarkerTextField;
    // End of variables declaration//GEN-END:variables

    private void setStub(Object object) {

    }
}
