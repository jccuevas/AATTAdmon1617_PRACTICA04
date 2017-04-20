
import dnie.FirmarDatos;
import es.gob.jmulticard.jse.provider.DnieProvider;
import es.gob.jmulticard.ui.passwordcallback.PasswordCallbackManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPasswordField;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Programa para realización de firmas con DNIe ATENCIÓN: Para que funcione
 * correctamente se debe tener instalada una versión de java de 32 bits (aunque
 * el SO sea de 64) y la dll de Pkcs11 puede usarse la de 64 bits
 * (C:\Windows\SysWOW64\UsrPkcs11.dll) o la de 32 bits
 * (C:\WINDOWS\system32\UsrPkcs11.dll) La comporbación de la versión del SO
 * depende del Java activo en el proyecto y no del del SO
 *
 * @author toni
 *
 */
public class Main extends Frame implements ActionListener {

    /**
     * @param args the command line arguments
     */
    Main() {
        Button b = new Button("Autenticar");
        b.setBounds(30, 100, 80, 30);// setting button position  
        add(b);//adding button into frame  
        b.addActionListener((ActionListener) this);//passing current instance  
        setSize(300, 300);//frame size 300 width and 300 height  
        setLayout(null);//no layout manager  
        setVisible(true);//now frame will be visible, by default not visible 

    }

    public static void main(String args[]) {
        Main f = new Main();
        PasswordCallbackManager.setDialogOwner(f);
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent evt ) {
                // Concluye la aplicacion cuando el usuario cierra la 
               // ventana
                System.exit( 0 );
                }
            } );
        
        //System.setProperty("es.gob.jmulticard.fastmode", "true");

    }

    public void actionPerformed(ActionEvent e) {
        doAuth();
    }

    private void doAuth() {

        FileInputStream signIn = null;

        String datos = null;
        //Sin PIN
        FirmarDatos od = new FirmarDatos();
        /*
         IMPORTANTE: Introducir aquí el PIN del DNIe que se vaya a utilizar
         */
        String PIN = "";
        int salir = 0;

        try {
            final String alias = "CertFirmaDigital"; //$NON-NLS-1$
            //Se instancia el proveedor y se anade
            final Provider p = new DnieProvider();
            Security.addProvider(p);
// Se obtiene el almacen y se carga
            final KeyStore ks = KeyStore.getInstance("DNI"); //$NON-NLS-1$

            ks.load(null, null);

            final Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                System.out.println(aliases.nextElement());
            }
      
            X509Certificate authCert = (X509Certificate) ks.getCertificate(alias);
            System.out.println("USUARIO" + authCert.getSubjectDN());
            
// Se obtiene el motor de firma y se inicializa
            final Signature signature = Signature.getInstance("SHA-256withRSA"); //$NON-NLS-1$
            PrivateKey pk= (PrivateKey) ks.getKey(alias, null);
            signature.initSign(pk);
// Vamos a firmar el texto 'hola'
            signature.update("hola".getBytes()); //$NON-NLS-1$
// Completamos el proceso y obtenemos la firma PKCS#1
            final byte[] signatureBytes = signature.sign();

//Certificate authCert = ks.getCertificate("CertFirmaDigital");
            
        } catch (KeyStoreException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableEntryException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

}
