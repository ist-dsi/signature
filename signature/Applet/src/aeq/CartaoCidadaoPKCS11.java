/**
 * Instituto Superior Técnico - 2009
 * Cartão de Cidadão PKCS11
 * @author  Daniel Almeida - daniel.almeida@ist.utl.pt
 */
package aeq;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Set;

public class CartaoCidadaoPKCS11 {
  
    public static String PKCS11LibFilePath = "LIBPKCS11";
    private static Provider PKCS11CartaoCidadaoProv;

    public static KeyStore getPKCS11Store() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        String tokenName = "AssinaturaElectronicaQualificada";
        File PKCS11LibFile;


            // determina qual o sistema operativo
            String OSName = System.getProperty("os.name").toLowerCase();

            if (OSName.startsWith("linux")) // linux
            {
                PKCS11LibFilePath = "/usr/local/lib/libpteidpkcs11.so";
            } else if (OSName.startsWith("windows")) // ms windows
            {
                PKCS11LibFilePath = System.getenv("WINDIR") + "\\System32\\pteidpkcs11.dll";
            } else if (OSName.startsWith("mac")) // mac os x
            {
                PKCS11LibFilePath = "/usr/local/lib/pteidpkcs11.dylib";
            }

            PKCS11LibFile = new File(PKCS11LibFilePath);

            if (!PKCS11LibFile.canRead()) {
                throw new java.security.KeyStoreException("Erro a ler o ficheiro: " + PKCS11LibFile.getAbsolutePath());
            }

            // cria um ficheiro temporário com a configuração pkcs11
            File PKCS11ConfigFile = File.createTempFile("pkcs11_config", "conf");
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                    new FileOutputStream(PKCS11ConfigFile)));
            writer.write(String.format("name = %s\n", tokenName));
            writer.write(String.format("library = %s\n", PKCS11LibFile.getPath()));
            writer.close();

            PKCS11CartaoCidadaoProv = new sun.security.pkcs11.SunPKCS11(PKCS11ConfigFile.getPath());
            System.out.println("(*) PKCS11 Provider Name: " + PKCS11CartaoCidadaoProv.getName());
            Set<Provider.Service> servicesSet = PKCS11CartaoCidadaoProv.getServices();
         

            if (Security.getProvider(PKCS11CartaoCidadaoProv.getName()) != null) {
                Security.removeProvider(PKCS11CartaoCidadaoProv.getName());
                System.out.println("(*) A remover PKCS11 Provider....");
            }

            Security.addProvider(PKCS11CartaoCidadaoProv);
              Applet.debugln("PKCS11CartaoCidadoProv algorithms - start of list - :");
            for(Provider.Service providerService : servicesSet)
            {
                Applet.debugln("Algorithm: " + providerService.getAlgorithm());
            }
           
            KeyStore ks = KeyStore.getInstance("PKCS11", PKCS11CartaoCidadaoProv);

         
            ks.load(null, null);
             //if in debug let's print the keystore aliases content
            Applet.debugln("Printing all of the aliases of the keystore");;
            for (Enumeration<String> alias = ks.aliases(); alias.hasMoreElements();)
            {
             Applet.debugln(alias.nextElement());
            }
            // apaga o ficheiro temporário
            PKCS11ConfigFile.delete();

            return ks;
    }

}
