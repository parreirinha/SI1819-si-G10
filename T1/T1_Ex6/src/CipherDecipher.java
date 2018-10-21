import utils.FileJoin;
import utils.KeyReader;
import utils.Reader;
import utils.Writer;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.util.Arrays;


/**
 * Created by fabio on 13-Oct-18.
 * Exercicio 6
 */
public class CipherDecipher {

    //paths to cipher
    private static final String CHIPHER_FILE_PATH = System.getProperty("user.dir") + "\\src\\chipherTemp.txt";
    private static final String AUTH_FILE_PATH = System.getProperty("user.dir") + "\\src\\authTemp.txt";
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String DES_CBC_PKCS5 = "DES/CBC/PKCS5Padding";

    //paths to decipher
    private static final String MAC_FROM_CIPHER_FILE_PATH = System.getProperty("user.dir") + "\\src\\mac_file_from_cipher.txt";
    private static final String DECIPHERED_FILE_PATH = System.getProperty("user.dir") + "\\src\\DECIPHERED_FILE.pdf";
    private static final String MAC_FROM_DECIPHER_FILE_PATH = System.getProperty("user.dir") + "\\src\\mac_file_from_decipher.txt";

    private static String RESULT_FILE_PATH = System.getProperty("user.dir") + "\\src\\CIPHER_RESULT.txt";
    private static String INPUT_FILE_PATH = System.getProperty("user.dir") + "\\src\\";
    private static String KEY_FILE_PATH = System.getProperty("user.dir") + "\\src\\";

    private String mode;
    private final int readDimensionBlock = 32;

    private Reader reader;
    private Writer writer;

    private Cipher cipher;
    private Mac mac;

    private SecureRandom rnd = new SecureRandom();
    private IvParameterSpec iv = new IvParameterSpec(rnd.generateSeed(8));


    private  CipherDecipher(String[] cmdLineArgs) throws InterruptedException {

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

    private void cipher() throws BadPaddingException, IllegalBlockSizeException {

        initChipher();

        byte[] toCipher = new byte[readDimensionBlock];
        byte[] bytesCiphered;
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
        deleteUnnecessaryFiles();
    }


    private void decipher() throws BadPaddingException, IllegalBlockSizeException {
        
        initDecipher();

        int macLength = mac.getMacLength();
        byte[] macArray = new byte[macLength];
        int fileLength = reader.getFileLength(RESULT_FILE_PATH);

        // Get MAC from ciphered file
        reader.readBytesFromFile(RESULT_FILE_PATH, macArray, fileLength - macLength -1 );
        writer.writeToFileFromArray(MAC_FROM_CIPHER_FILE_PATH, macArray);

        // Get Ciphered file and Dicipher it
        byte[] toDecipher = new byte[readDimensionBlock];
        byte[] bytesDiciphered;
        int index = 0;

        int bytesReaded = reader.readBytesFromFile(RESULT_FILE_PATH, toDecipher, index);

        while(bytesReaded == readDimensionBlock && (index + readDimensionBlock < fileLength - macLength -1))
        {
            bytesDiciphered = cipher.update(toDecipher);                      //Decipher file
            mac.update(bytesDiciphered);

            toDecipher = new byte[readDimensionBlock];                        //clear byte[]

            writer.writeToFileFromArray(DECIPHERED_FILE_PATH, bytesDiciphered);  //write to deciphered file

            index += readDimensionBlock;                                    //Update index to read input file
            if(index + readDimensionBlock > fileLength) break;
            bytesReaded = reader.readBytesFromFile(RESULT_FILE_PATH, toDecipher, index);   //Read with skip of index length
        }

        toDecipher = new byte[fileLength - macLength - index];      // last bytes to read
        reader.readBytesFromFile(RESULT_FILE_PATH, toDecipher, index);

        bytesDiciphered = cipher.doFinal(toDecipher);               // decipher doFinal
        byte[] macDecipheredFile = mac.doFinal(bytesDiciphered);    // mac doFinal

        writer.writeToFileFromArray(DECIPHERED_FILE_PATH, bytesDiciphered);             // last write to decipher file
        writer.writeToFileFromArray(MAC_FROM_DECIPHER_FILE_PATH, macDecipheredFile);    // write mac file

        int cipherMacsize = reader.getFileLength(MAC_FROM_CIPHER_FILE_PATH);        // get MAC files
        int deciperMacsize = reader.getFileLength(MAC_FROM_DECIPHER_FILE_PATH);

        byte[] cipherMac = reader.getAllBytes(MAC_FROM_CIPHER_FILE_PATH, cipherMacsize);        // Compare MACs
        byte[] decipherMac = reader.getAllBytes(MAC_FROM_DECIPHER_FILE_PATH, deciperMacsize);
        boolean isSameMac = Arrays.equals(cipherMac, decipherMac);





        
        if (isSameMac)
        {
            System.out.println("Decipher OK");
        }
        else
        {
            System.out.println("Decipher NOT OK!!!");
        }
    }

    private void initDecipher() {

        deleteIfExists(new File(MAC_FROM_CIPHER_FILE_PATH));
        deleteIfExists(new File(DECIPHERED_FILE_PATH));
        deleteIfExists(new File(MAC_FROM_DECIPHER_FILE_PATH));

        reader = new Reader();
        writer = new Writer();
        SecretKey key = getSecretKey();
        initCipherAndMac(Cipher.DECRYPT_MODE, key);
    }

    private void initChipher(){

        deleteUnnecessaryFiles();
        deleteIfExists(new File(RESULT_FILE_PATH));
        reader = new Reader();
        writer = new Writer();
        SecretKey key = getSecretKey();
        initCipherAndMac(Cipher.ENCRYPT_MODE, key);
    }

    private SecretKey getSecretKey() {
        /*
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyGen.generateKey();
        */
        KeyReader kr = new KeyReader(KEY_FILE_PATH);
        return kr.getKey();
    }

    private void initCipherAndMac(int mode, SecretKey key) {

        try {
            cipher = Cipher.getInstance(DES_CBC_PKCS5);
            cipher.init(mode, key,iv);
            mac = Mac.getInstance(HMAC_SHA1);
            mac.init(key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    private void deleteUnnecessaryFiles() {

        deleteIfExists(new File(CHIPHER_FILE_PATH));
        deleteIfExists(new File(AUTH_FILE_PATH));
    }
    
    private void deleteIfExists(File file){
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void verifyArgsLength(String[] args) {

        if (args.length != 3)
        {
            System.out.println("expect 3 arguments");
            return;
        }
    }

    private void concatFiles() {

        File chipherFile = new File(CHIPHER_FILE_PATH);
        File authfile = new File(AUTH_FILE_PATH);
        File resultFile = new File(RESULT_FILE_PATH);

        FileJoin fj = new FileJoin();
        try {
            fj.joinFiles(resultFile, new File[] {chipherFile, authfile});
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            deleteIfExists(chipherFile);
            deleteIfExists(authfile);
        }
    }

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, InterruptedException {

        verifyArgsLength(args);
        CipherDecipher cd = new CipherDecipher(args);

        if (cd.mode.equals("-cipher")) {
            System.out.println("Ciphering...");
            cd.cipher();
        }
        else {
            System.out.println("Deciphering...");
            cd.decipher();
        }
    }
}
