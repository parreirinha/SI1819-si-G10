package Trabalho1;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

public class DigitalSignature {



    private static byte[] bufbyte=new byte[1024];

    public static final String signatureSHA1="SHA1WithRSA" ;
    public static final String signatureSHA256="SHA256WithRSA" ;
    private static String path= System.getProperty("user.dir") + "\\src\\" ;
    private static Signature sng;

    static {
        try {
            sng = Signature.getInstance(signatureSHA1);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, SignatureException, InvalidKeyException, NoSuchProviderException {
        /*Signature sng;
        if(args.length>0){
            if(args[1]== "SHA1")
                sng = Signature.getInstance(signatureSHA1);
            if(args[1]== "SHA256")
                sng = Signature.getInstance(signatureSHA256);
*/
            //ler ficheiro
            updateFile(args[2]);

            if(args[0].equals( "-sign")){
                try {
                    SignFile(args[3], args[4]);
                } catch (UnrecoverableEntryException e) {
                    e.printStackTrace();
                }

            }else if(args[0].equals( "-verify")){
                VerifySign(args[3], args[4]);
            }else{
                System.out.println("Not suported function");
            }

    }


    private  static void updateFile(String filePath) throws IOException {

        File fileName = new File(path+filePath);
        FileInputStream fis = new FileInputStream(fileName);

        try {
            while (fis.read() == -1)
            {
                sng.update(bufbyte);
            }

        } catch (SignatureException e) {
            e.printStackTrace();
        }finally {
            fis.close();
        }

    }



    public static void SignFile(String pfxFile, String pass) throws NoSuchAlgorithmException, SignatureException, IOException, CertificateException, KeyStoreException, UnrecoverableEntryException, InvalidKeyException {
        //recebe keystore
        KeyStore keySt = KeyStore.getInstance("JKS");
        InputStream in = new FileInputStream(pfxFile);
        //ficheiro pfx mais a pass //password para aceder keytore
        keySt.load(in, pass.toCharArray());
        KeyStore.ProtectionParameter proParam = new KeyStore.PasswordProtection(pass.toCharArray());
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

        // get my private key
        String ali="" ;
        Enumeration<String> e = ks.aliases();
        while(e.hasMoreElements()) {
            String entry = e.nextElement();
            if (ks.isKeyEntry(entry)) {
                ali=entry;
            }
        }

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(ali, proParam);
        PrivateKey myPrivateKey = pkEntry.getPrivateKey();

        sng.initSign(myPrivateKey);
        sng= Signature.getInstance(signatureSHA1);
        byte [] theSign = sng.sign();

        //cria novo ficheiro so com a assinatura

        File file = new File(path);
        FileOutputStream writer = new FileOutputStream(file, true);
        writer.write(theSign);
        writer.close();

    }

    public static boolean VerifySign( String fileToVerify, String cert) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException, CertificateException, NoSuchProviderException {
        //recebe um ficheiro

        InputStream certificateInputStream = new FileInputStream(cert);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Certificate certificate = certificateFactory.generateCertificate(certificateInputStream);
        PublicKey certificatePublicKey = certificate.getPublicKey();

        //certificado de quem assinou
        InputStream certInStr = new FileInputStream(fileToVerify);
        CertificateFactory certbuff = CertificateFactory.getInstance("X.509");
        Certificate certFileVerif = certbuff.generateCertificate(certInStr);
        PublicKey expectedPublicKey =certFileVerif.getPublicKey();

        certificate.verify(expectedPublicKey);


        sng = Signature.getInstance(signatureSHA1);
        sng.initVerify(certificatePublicKey);
        sng.update(bufbyte);
        //get sign from certificate
        byte[] signBytes = Base64.decode(bufbyte.toString());
        //retorna verdadeiro ou falso, valido ou nao
        return sng.verify(signBytes);



    }
}
