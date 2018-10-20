import utils.Reader;
import utils.Writer;
import utils.FileJoin;

import javax.crypto.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * Created by fabio on 13-Oct-18.
 * Exercicio 6
 */
public class CipherDecipher {

    private static final String HEADER_FILE_PATH = System.getProperty("user.dir") + "\\src\\headerTemp.txt";
    private static final String CHIPHER_FILE_PATH = System.getProperty("user.dir") + "\\src\\chipherTemp.txt";
    private static final String AUTH_FILE_PATH = System.getProperty("user.dir") + "\\src\\authTemp.txt";
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String DES_CBC_PKCS5 = "DES/CBC/PKCS5Padding";


    private static String RESULT_FILE_PATH = System.getProperty("user.dir") + "\\src\\FINAL_RESULT.txt";
    private static String INPUT_FILE_PATH = System.getProperty("user.dir") + "\\src\\";
    private static String KEY_FILE_PATH = System.getProperty("user.dir") + "\\src\\";

    private String mode;
    private final int readDimensionBlock = 32, headerSize = 2;

    private Reader reader;
    private Writer writer;

    private Cipher cipher;
    private Mac mac;


    private  CipherDecipher(String[] cmdLineArgs) throws FileNotFoundException, InterruptedException {

        mode = cmdLineArgs[0];

        if (!(mode.toLowerCase().equals("-cipher") || mode.toLowerCase().equals("-decipher")))
        {
            System.out.println("invalid mode: choose '-cipher' or '-dicipher'");
            Thread.sleep(1000);
            System.exit(1);
        }

        INPUT_FILE_PATH += cmdLineArgs[1];
        KEY_FILE_PATH += cmdLineArgs[2];
    }

    private void decipher() {

    }

    private void cipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {

        init();

        byte[] toCipher = new byte[readDimensionBlock];
        byte[] bytesCiphered, bytesAuthentication;
        int index = 0;

        int bytesReaded = reader.readBytesFromFile(INPUT_FILE_PATH,toCipher, index);

        while(bytesReaded == readDimensionBlock)
        {
            bytesCiphered = cipher.update(toCipher);                        //Cipher file
            mac.update(bytesCiphered);

            toCipher = new byte[readDimensionBlock];                        //clear byte[]

            writer.writeToFileFromArray(CHIPHER_FILE_PATH, bytesCiphered);  //write to chipher temp file

            index += readDimensionBlock;                                    //Update index to read input file
            bytesReaded = reader.readBytesFromFile(INPUT_FILE_PATH, toCipher, index);   //Read with skip of index length
        }

        bytesCiphered = cipher.doFinal(toCipher);                           //doFinal
        byte[] macAuth = mac.doFinal(bytesCiphered);

        writer.writeToFileFromArray(CHIPHER_FILE_PATH, bytesCiphered);      //last write to cipher file
        writer.writeToFileFromArray(AUTH_FILE_PATH, macAuth);



        concatFiles();
    }

    private void init() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        deleteUnnecessaryFiles();   // TODO Remove

        reader = new Reader();
        writer = new Writer();

        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        SecretKey key = keyGen.generateKey();                   //TODO alterar para ler um ficheiro com 8 bytes

        cipher = Cipher.getInstance(DES_CBC_PKCS5);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        mac = Mac.getInstance(HMAC_SHA1);
        mac.init(key);
    }



    private void deleteUnnecessaryFiles() throws IOException {

        deleteIfExists(new File(HEADER_FILE_PATH));
        deleteIfExists(new File(CHIPHER_FILE_PATH));
        deleteIfExists(new File(AUTH_FILE_PATH));
    }

    //Delete File if exists
    private void deleteIfExists(File file) throws IOException {
        Files.deleteIfExists(file.toPath());
    }


    private static void verifyArgsLength(String[] args) {

        if (args.length != 3)
        {
            System.out.println("expect 3 arguments");
            return;
        }
    }

    private void concatFiles() throws IOException {

        File chipherFile = new File(CHIPHER_FILE_PATH);
        File authfile = new File(AUTH_FILE_PATH);
        File resultFile = new File(RESULT_FILE_PATH);

        FileJoin fj = new FileJoin();
        fj.joinFiles(resultFile, new File[] {chipherFile, authfile});

        deleteIfExists(chipherFile);
        deleteIfExists(authfile);
    }

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, InterruptedException {

        verifyArgsLength(args);
        CipherDecipher cd = new CipherDecipher(args);

        if (cd.mode.equals("-cipher"))
            cd.cipher();
        else
            cd.decipher();


    }


}
