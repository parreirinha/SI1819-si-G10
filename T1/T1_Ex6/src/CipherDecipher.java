import javax.crypto.*;
import java.lang.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * Created by fabio on 13-Oct-18.
 * Exercicio 6
 */
public class CipherDecipher {

    private String path, mode, inputFilePath, keyFilePath, resFileName, fileKey;
    private boolean decipherSucess = false;
    FileReaderWriter fileReaderWriter;
    byte[] bytes;
    private final int bytesDimForCipher = 56;


    public  CipherDecipher(String[] args)
    {
        path = System.getProperty("user.dir") + "\\src\\" ;
        mode = args[0];
        inputFilePath = path + args[1];
        keyFilePath = path + args[2];
        resFileName = path + mode.substring(1, mode.length()) + "Result";

        if (!(mode.equals("-cipher") || mode.equals("-decipher")))
        {
            System.out.println("invalid mode: choose '-cipher' or '-dicipher'");
            System.exit(1);
        }

        fileReaderWriter = FileReaderWriter.getInstance();
    }

    private static void printByteArray(byte[] fileBytes) {
        for (int i = 0; i<fileBytes.length; i++) {
            System.out.println(fileBytes[i]);
        }
    }



    private void decipher() {

    }

    private void cipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        bytes = fileReaderWriter.readBytesFromFile(inputFilePath);

        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        SecretKey key = keyGen.generateKey();

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        int index = 0;

        byte[] toCipher = getPartialArrayForUpdate(index);
        byte[][] confidentialresult = initResultArrya(bytes.length);

        for ( ; index < confidentialresult.length-1; index++)
        {
            confidentialresult[index] = cipher.update(toCipher);
            toCipher = getPartialArrayForUpdate(index * bytesDimForCipher);
        }
        confidentialresult[index] = cipher.doFinal(toCipher);

        printTest(confidentialresult);
    }

    private void printTest(byte[][] confidentialresult) {

        System.out.println("confidentialresult: " + confidentialresult.length);
        int count = 0;
        for (int i = 0; i < confidentialresult.length ; i++) {
            for (int j = 0; j < bytesDimForCipher; j++) {
                System.out.print(confidentialresult[i][j] + " | ");
                count ++;
            }
            System.out.println();
        }
        System.out.println("numero de bytes" + count);
    }

    private byte[][] initResultArrya(int length) {

        int lines = bytes.length / bytesDimForCipher + 1 ; // necessita sempre de mais um bloco ou padding ou restantes bytes
        //if(bytes.length / bytesDimForCipher > 0) lines++;
        int cols = bytesDimForCipher;

        return new byte[lines][cols];
    }

    private byte[] getPartialArrayForUpdate(int initial) {

        byte[] res = new byte[bytesDimForCipher];
        int last = initial + bytesDimForCipher;

        for (int i = initial, idx = 0; i < last && i < bytes.length; )
        {
            res[idx++] = bytes[i++];
        }
        return res;
    }

    private static void verify(String[] args) {

        if (args.length != 3)
        {
            System.out.println("expection 3 arguments");
            return;
        }
    }

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        verify(args);
        CipherDecipher cd = new CipherDecipher(args);

        if (cd.mode.equals("-cipher"))
            cd.cipher();
        else
            cd.decipher();
    }


}
