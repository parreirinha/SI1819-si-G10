
/*
package ex5;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Example {
    public static void main(String[] args) throws Exception {

        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        String[] supportedCS = factory.getSupportedCipherSuites();
        for (String cs : supportedCS) {
            System.out.println(cs);
        }

        SSLSocket socket = (SSLSocket) factory.createSocket("www.isel.pt", 443);
        //TODO
        socket.getSupportedProtocols();//estabelecer um novo socket com cada uma destas versões e fazer uma ligação para todos e ver em quais tenho sucesso ou nao.
        socket.startHandshake();

        SSLSession session = socket.getSession();
        System.out.println("Selected protocol: "+ session.getProtocol());
        System.out.println("Seected cipher suite: " + session.getCipherSuite());

        socket.close();
    }
}

*/