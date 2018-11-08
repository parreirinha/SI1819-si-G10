package ex5;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.X509Certificate;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by fabio on 02-Nov-18.
 */
public class GetInfoFromHostName {

    private final String HOST_NAME;
    private final int PORT = 443;
    X509Certificate[] certChain;
    Date smallerExpirationDate;
    ArrayList supportedProtocols;


    public GetInfoFromHostName(String hostName)
    {
        this.HOST_NAME = hostName;
    }

    public void doWork()
    {
        SSLSocket socket = getSocket();
        certChain = getCertificateChain(socket);                        //ponto 1
        smallerExpirationDate = getSmallerExpirationDate(certChain);    //ponto 2

        String[] sp = socket.getSupportedProtocols();
        supportedProtocols = testProtocols(sp);         // ponto 3

        printResult();
        closeServer(socket);
    }

    private ArrayList<String> testProtocols(String[] supportedProtocols) {

        ArrayList<String> res = new ArrayList<String>();

        SSLSocketFactory factory;
        SSLSocket socket = null;
        SSLContext context = null;

        for (int i = 0; i < supportedProtocols.length; i++) {

            //https://gist.github.com/fkrauthan/ac8624466a4dee4fd02f
            try {
                context = SSLContext.getInstance(supportedProtocols[i]);
                context.init(null, null, null);
                factory = context.getSocketFactory();
                socket = (SSLSocket)factory.createSocket(HOST_NAME, PORT);
                socket.startHandshake();
                res.add(supportedProtocols[i]);
                //System.out.println("LOG protocolo => " + supportedProtocols[i]);
            } catch (NoSuchAlgorithmException e) {
                //e.printStackTrace();
                System.out.println("LOG => protocolo não suportado: " + supportedProtocols[i]);
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    private Date getSmallerExpirationDate(X509Certificate[] certChain)
    {
        Date res = null;

        for (int i = 0; i < certChain.length; i++)
        {
           if(res == null || certChain[i].getNotBefore().after(res))
           {
               res = certChain[i].getNotAfter();
           }
        }
        return res;
    }

    private X509Certificate[] getCertificateChain(SSLSocket socket)
    {
        X509Certificate[] res = null;
        try {
            res = socket.getSession().getPeerCertificateChain();
        } catch (SSLPeerUnverifiedException e) {
            e.printStackTrace();
        }
        return res;
    }

    private void closeServer(SSLSocket socket)
    {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SSLSocket getSocket()
    {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = null;
        try {
            socket = (SSLSocket) factory.createSocket(HOST_NAME, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    private void printResult()
    {
        System.out.println("1. Certificate chain:");
        for (int i = 0; i < certChain.length; i++)
        {
            System.out.println("\tCertificate nº" + (i+1) + " serial number: " + certChain[i].getSerialNumber());
        }

        System.out.println("\n2. SMALLER EXPIRATION DATE:\n\t" + smallerExpirationDate);

        System.out.println("\n3. SUPPORTED PROTOCOLS:");

        for (int i = 0; i < supportedProtocols.size(); i++) {

            System.out.println("\t" + supportedProtocols.get(i));
        }
    }

}
