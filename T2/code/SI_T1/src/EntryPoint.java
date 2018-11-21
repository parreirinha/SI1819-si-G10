import T1.CipherDecipher;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fabio on 22-Oct-18.
 */
public class EntryPoint {

    public static void main(String[] args) throws BadPaddingException, InterruptedException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, IOException {


        // ex5:
        //GetInfoFromHostName ex5 = new GetInfoFromHostName("www.isel.pt");
        //ex5.doWork();

        //String[] params = {"-cipher", "serie1.pdf", "key.txt"};
        String[] params = {"-decipher", "CIPHER_RESULT.txt", "key.txt"};
        CipherDecipher.doWork(params);


        // T2 Ex6 => https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html#PBEEx

    }
}
