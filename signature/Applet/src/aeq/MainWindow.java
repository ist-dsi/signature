package aeq;

import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStoreException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

    public final static String BEGIN_XML_FILE = "------- XML START -------\n";
    public final static String END_XML_FILE = "------- XML END -------\n";
    public final static String BEGIN_XHTML_FILE = "------- XHTML START -------\n";
    public final static String END_XHTML_FILE = "------- XHTML END -------\n";

    private XAdESSigner signer;
    private final String tsaURL = "http://tsp.iaik.tugraz.at/tsp/TspRequest";

    private boolean canSign = false;
    private boolean hasSigned = false;

    private final String signContentURL;
    final private String serverURL;
    final private String redirectURL;


    //    private File contentFile;
    private String contentString;
    private String xhtmlContentString;

    private String xmlContentString;

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

    public MainWindow(AppletContext appContext, String signContentURL, String serverURL, String redirectURL) {
	this.appContext = appContext;

	this.signContentURL = signContentURL;
	this.serverURL = serverURL;
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
	    //let's remove the content which isn't XHTML:
	    int startIndexOfXHTMLContent = contentString.indexOf(BEGIN_XHTML_FILE) + BEGIN_XHTML_FILE.length();
	    int endIndexOfXHTMLContent = contentString.indexOf(END_XHTML_FILE);
	    xhtmlContentString = contentString.substring(startIndexOfXHTMLContent, endIndexOfXHTMLContent);

	    Document document = XMLResource.load(new StringReader(xhtmlContentString)).getDocument();
	    System.out.println("Parsed, now loading document..");

	    try {
		System.out.println("Loading Page: " + signContentURL);
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
	    BufferedReader in = new BufferedReader(new InputStreamReader(new URL(signContentURL).openStream()));

	    //	    BufferedWriter contentFileWriter = new BufferedWriter(new FileWriter(contentFile));

	    StringBuilder sb = new StringBuilder();
	    String inputLine;
	    while ((inputLine = in.readLine()) != null) {
		sb.append(inputLine + "\n");
		//		contentFileWriter.write(inputLine + "\n");
	    }

	    contentString = sb.toString();

	    //	    contentFileWriter.close();
	    in.close();

	    //let's extract the XML from the content
	    //let's remove the content which isn't pure XML:
	    int startIndexOfXMLContent = contentString.indexOf(BEGIN_XML_FILE) + BEGIN_XML_FILE.length();
	    int endIndexOfXMLContent = contentString.indexOf(END_XML_FILE);
	    xmlContentString = contentString.substring(startIndexOfXMLContent, endIndexOfXMLContent);

	    //now let's interpret it
	    Document document = XMLResource.load(new StringReader(xmlContentString)).getDocument();
	    Element documentElement = document.getDocumentElement();
	    debugln("Document element: " + documentElement.getLocalName());
	    signatureId = documentElement.getAttribute("signatureId");
	    intentionString = documentElement.getAttribute("intention");
	    descriptionString = documentElement.getAttribute("description");
	    roleString = documentElement.getAttribute("roleOfSigner");
	    debugln("Document element signatureId attribute: " + signatureId);
	    debugln("Document element intention content: " + intentionString);
	    debugln("Document element description content: " + descriptionString);
	    debugln("Document element roleString content: " + roleString);
	    if (signatureId.isEmpty() || intentionString.isEmpty() || descriptionString.isEmpty())
		throw new RuntimeException("Conteudo da assinatura mal formado, id, intenção ou descrição não foram encontrados");
	    debugln("Parsed the XML");

	    canSign = true;


	} catch (MalformedURLException ex) {
	    Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
	    JOptionPane.showMessageDialog(this, ex, "Erro", JOptionPane.ERROR_MESSAGE);
	} catch (IOException ex) {
	    Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
	    JOptionPane.showMessageDialog(this, ex, "Erro", JOptionPane.ERROR_MESSAGE);
	} catch (RuntimeException ex)
	{
	    Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
	    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
	    
	}
    }

    protected void signAndCommit() {
	performSign();
	commitSign();

	// delete the temporary file
	//	contentFile.delete();

	JOptionPane.showMessageDialog(this, "Assinatura concluída e enviada, Obrigado!", "Assinatura Digital",
		JOptionPane.INFORMATION_MESSAGE);

	// redirect page
	System.out.println("Redirecting page..");
	try {
	    appContext.showDocument(new URL(redirectURL));
	} catch (MalformedURLException ex) {
	    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
	}
	System.out.println("Whooatt?");
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

	    // remove line breaks
	    compromissoArea.setText(compromissoArea.getText().replaceAll(System.getProperty("line.separator"), " "));
	    descriptionArea.setText(descriptionArea.getText().replaceAll(System.getProperty("line.separator"), " "));

	    //	    BufferedWriter signatureFileWriter = new BufferedWriter(new FileWriter(signatureFile));

	    // do the signature
	    signedContent = signer.sign(contentString.getBytes(), signatureId, cargoField.getText(),
		    descriptionArea.getText(),
		    compromissoArea.getText(), new MimeType("application", "signature"));

	    // add the timestamp
	    XAdESSignatureTimeStamper ts = new XAdESSignatureTimeStamper();
	    signedContent = ts.signatureTimeStamp(signedContent, tsaURL);

	    if (signedContent != null) {
		JOptionPane.showMessageDialog(this, "Assinatura realizada com sucesso", "Sucesso",
			JOptionPane.INFORMATION_MESSAGE);

		hasSigned = true;
	    } else {
		JOptionPane.showMessageDialog(this, "Falhou a criação da assinatura", "Erro", JOptionPane.ERROR_MESSAGE);
		activateSign();
	    }

	} catch (KeyStoreException ex) {
	    JOptionPane.showMessageDialog(this, "Erro a aceder ao Cartão de Cidadão", "Erro", JOptionPane.ERROR_MESSAGE);
	    Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
	} catch (Exception ex) {
	    Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
	    JOptionPane.showMessageDialog(this, ex, "Erro", JOptionPane.ERROR_MESSAGE);
	} finally {
	    activateSign();
	}
    }

    //TODO reconstruct this to use javascript calls if possible
    protected void commitSign() {
	if (!hasSigned) {
	    return;
	}

	try {
	    ClientHttpRequest client = new ClientHttpRequest(serverURL);
	    //	    client.
	    //	    client.setParameter("originalFile", );
	    //	    client.setParameter("signedFile", contentFile);

	    InputStream stream = client.post();

	    byte[] response = new byte[1024];
	    stream.read(response);
	} catch (MalformedURLException ex) {
	    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
	} catch (Exception ex) {
	    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    protected boolean initSigner(boolean alert) {
	cleanUI();

	try {
	    signer = new XAdESSigner();

	} catch (Exception ex) {
	    if (alert) {
		JOptionPane
			.showMessageDialog(this, "Não foi possível ler o Cartão de Cidadão", "Erro",
			JOptionPane.ERROR_MESSAGE);
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
	compromissoArea.setEnabled(b);
	compromissoArea.setEditable(b);
	descriptionArea.setEnabled(b);
	descriptionArea.setEditable(b);
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
	jPanel3 = new javax.swing.JPanel();
	jLabel7 = new javax.swing.JLabel();
	jLabel8 = new javax.swing.JLabel();
	jScrollPane3 = new javax.swing.JScrollPane();
	descriptionArea = new javax.swing.JTextArea();
	jScrollPane2 = new javax.swing.JScrollPane();
	compromissoArea = new javax.swing.JTextArea();
	jPanel5 = new javax.swing.JPanel();
	jLabel2 = new javax.swing.JLabel();
	identityField = new javax.swing.JTextField();
	jLabel3 = new javax.swing.JLabel();
	identityEmissionField = new javax.swing.JTextField();
	jLabel4 = new javax.swing.JLabel();
	identityValidadeField = new javax.swing.JTextField();
	cargoField = new javax.swing.JTextField();
	jLabel9 = new javax.swing.JLabel();
	readCardButton = new javax.swing.JButton();
	signButton = new javax.swing.JButton();
	jButton1 = new javax.swing.JButton();

	jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Conteúdo a Assinar"));

	javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
	jPanel2.setLayout(jPanel2Layout);
	jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0,
		962, Short.MAX_VALUE));
	jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0,
		553, Short.MAX_VALUE));

	jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Opcional"));

	jLabel7.setText("Descrição");

	jLabel8.setText("Compromisso");

	descriptionArea.setColumns(20);
	descriptionArea.setEditable(false);
	descriptionArea.setRows(5);
	descriptionArea.setEnabled(false);
	jScrollPane3.setViewportView(descriptionArea);

	compromissoArea.setColumns(20);
	compromissoArea.setEditable(false);
	compromissoArea.setRows(5);
	compromissoArea.setEnabled(false);
	jScrollPane2.setViewportView(compromissoArea);

	javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
	jPanel3.setLayout(jPanel3Layout);
	jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
		jPanel3Layout
			.createSequentialGroup()
			.addContainerGap()
			.addGroup(
				jPanel3Layout
					.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
						javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel8))
			.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(
				jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
					.addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 822, Short.MAX_VALUE)
					.addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 822, Short.MAX_VALUE))
			.addContainerGap()));
	jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
		jPanel3Layout
			.createSequentialGroup()
			.addGroup(
				jPanel3Layout
					.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
					.addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 22,
						javax.swing.GroupLayout.PREFERRED_SIZE))
			.addGroup(
				jPanel3Layout
					.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
					.addGroup(
						jPanel3Layout
							.createSequentialGroup()
							.addGap(6, 6, 6)
							.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 19,
								javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGroup(
						jPanel3Layout
							.createSequentialGroup()
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 17,
								Short.MAX_VALUE))).addContainerGap()));

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

	cargoField.setEditable(false);
	cargoField.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
	cargoField.setPreferredSize(new java.awt.Dimension(250, 18));

	jLabel9.setText("Cargo:");

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

	jButton1.setText("Envio Teste");
	jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
	    @Override
	    public void mouseClicked(java.awt.event.MouseEvent evt) {
		jButton1MouseClicked(evt);
	    }
	});
	jButton1.addActionListener(new java.awt.event.ActionListener() {
	    @Override
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		jButton1ActionPerformed(evt);
	    }
	});

	javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
	jPanel5.setLayout(jPanel5Layout);
	jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
		jPanel5Layout
			.createSequentialGroup()
			.addContainerGap()
			.addGroup(
				jPanel5Layout
					.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addComponent(jLabel3)
					.addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
					.addComponent(identityEmissionField, javax.swing.GroupLayout.DEFAULT_SIZE, 383,
						Short.MAX_VALUE)
					.addComponent(identityField, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE))
			.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(
				jPanel5Layout
					.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addComponent(jLabel9)
					.addComponent(identityValidadeField, javax.swing.GroupLayout.DEFAULT_SIZE, 303,
						Short.MAX_VALUE).addComponent(jLabel4)
					.addComponent(cargoField, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
			.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(
				jPanel5Layout
					.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addComponent(signButton, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
					.addGroup(
						jPanel5Layout
							.createSequentialGroup()
							.addComponent(readCardButton, javax.swing.GroupLayout.PREFERRED_SIZE,
								107, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 123,
								Short.MAX_VALUE))).addContainerGap()));
	jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
		jPanel5Layout
			.createSequentialGroup()
			.addGap(20, 20, 20)
			.addGroup(
				jPanel5Layout
					.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
					.addGroup(
						jPanel5Layout
							.createSequentialGroup()
							.addComponent(jLabel4)
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(identityValidadeField,
								javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGroup(
						jPanel5Layout
							.createSequentialGroup()
							.addComponent(jLabel2)
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(identityField, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGroup(
						jPanel5Layout
							.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
							.addComponent(readCardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36,
								javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jButton1)))
			.addGroup(
				jPanel5Layout
					.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(
						jPanel5Layout
							.createSequentialGroup()
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
							.addComponent(signButton, javax.swing.GroupLayout.DEFAULT_SIZE, 51,
								Short.MAX_VALUE))
					.addGroup(
						jPanel5Layout
							.createSequentialGroup()
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(jLabel3)
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(
								jPanel5Layout
									.createParallelGroup(
										javax.swing.GroupLayout.Alignment.BASELINE)
									.addComponent(identityEmissionField,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
									.addComponent(cargoField,
										javax.swing.GroupLayout.PREFERRED_SIZE, 23,
										Short.MAX_VALUE)))
					.addGroup(
						jPanel5Layout
							.createSequentialGroup()
							.addGap(2, 2, 2)
							.addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 21,
								javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap()));

	javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	getContentPane().setLayout(layout);
	layout.setHorizontalGroup(layout
		.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		.addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE,
			javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		.addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE,
			javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		.addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
			Short.MAX_VALUE));
	layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		.addGroup(
			javax.swing.GroupLayout.Alignment.TRAILING,
			layout.createSequentialGroup()
				.addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80,
					javax.swing.GroupLayout.PREFERRED_SIZE)));
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
	signAndCommit();
    }//GEN-LAST:event_signButtonActionPerformed

    private void readCardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readCardButtonActionPerformed
	// TODO add your handling code here:
    }//GEN-LAST:event_readCardButtonActionPerformed

    private void signButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_signButtonMouseClicked
    }//GEN-LAST:event_signButtonMouseClicked

    private void readCardButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readCardButtonMouseClicked
	initSigner(true);
    }//GEN-LAST:event_readCardButtonMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
	// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
	signAndCommit();
    }//GEN-LAST:event_jButton1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField cargoField;
    private javax.swing.JTextArea compromissoArea;
    private javax.swing.JTextArea descriptionArea;
    private javax.swing.JTextField identityEmissionField;
    private javax.swing.JTextField identityField;
    private javax.swing.JTextField identityValidadeField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton readCardButton;
    private javax.swing.JButton signButton;

    // End of variables declaration//GEN-END:variables

    private void setStub(Object object) {

    }
}
