package ex6;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fabio on 22-Oct-18.
 */
public class EntryPointChiperDecipherPassword {

    public static void main(String[] args) throws BadPaddingException, InterruptedException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, IOException {


        // ex5:
        //GetInfoFromHostName ex5 = new GetInfoFromHostName("www.isel.pt");
        //ex5.doWork();

        //String[] params = {"-cipher", "serie1.pdf"};
        String[] params = {"-decipher", "CIPHER_RESULT.txt"};
        CipherDecipherWithPassword.doWork(params);


        // T2 Ex6 => https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html#PBEEx

    }
}
