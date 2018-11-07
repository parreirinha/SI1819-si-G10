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
    String[] supportedProtocols;


    public GetInfoFromHostName(String hostName)
    {
        this.HOST_NAME = hostName;
        System.out.println("LOG: constructor");
    }


    public void doWork()
    {
        SSLSocket socket = getSocket();
        certChain = getCertificateChain(socket);                        //ponto 1
        smallerExpirationDate = getSmallerExpirationDate(certChain);    //ponto 2

        //As versoes dos protocolos SSL e TLS suportadas (de entre as dispon´ıveis na plataforma Java)

        String[] sp = socket.getSupportedProtocols();
        supportedProtocols = testProtocols(sp);
        //estabelecer um novo socket com cada uma destas versões e fazer uma ligação para todos e ver em quais tenho sucesso ou nao.


        //printResult();
        closeServer(socket);
    }

    private String[] testProtocols(String[] supportedProtocols) {

        ArrayList<String> res = new ArrayList<String>();

        //SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
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
                System.out.println("LOG protocolo => " + supportedProtocols[i]);
            } catch (NoSuchAlgorithmException e) {
                //e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res.to;
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
        System.out.println("Certificate chain:\n\n");
        for (int i = 0; i < certChain.length; i++)
        {
            System.out.println(certChain[i].toString() + "\n");
        }

        System.out.println("SMALLER EXPIRATION DATE:\n\t" + smallerExpirationDate);

        System.out.println("SUPPORTED PROTOCOLS:");

        for (int i = 0; i < supportedProtocols.length; i++) {

            System.out.println("\t" + supportedProtocols[i]);
        }
    }

}
