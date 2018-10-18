import utils.FileReaderWriter;
import utils.Reader;
import utils.Writer;

import javax.crypto.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * Created by fabio on 13-Oct-18.
 * Exercicio 6
 */
public class CipherDecipher {

    private String path, mode, inputFilePath, keyFilePath, resFileName, fileKey;
    private boolean decipherSucess = false;
    private final int readDimensionBlock = 32, headerSize = 2;
   // private FileReaderWriter readerWriter;
    private int currIndex = 0;
    private int authStart;
    Reader reader;
    Writer writer;

    public  CipherDecipher(String[] cmdLineArgs) throws FileNotFoundException {
        path = System.getProperty("user.dir") + "\\src\\" ;
        mode = cmdLineArgs[0];
        inputFilePath = path + cmdLineArgs[1];
        keyFilePath = path + cmdLineArgs[2];
        resFileName = path + mode.substring(1, mode.length()) + "Result";

        if (!(mode.equals("-cipher") || mode.equals("-decipher")))
        {
            System.out.println("invalid mode: choose '-cipher' or '-dicipher'");
            System.exit(1);
        }
    }

    private static void printByteArray(byte[] fileBytes) {
        for (int i = 0; i<fileBytes.length; i++) {
            System.out.println(fileBytes[i]);
        }
    }



    private void decipher() {

    }

    private void cipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {

        setHeaderFileResult();

        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        SecretKey key = keyGen.generateKey();                   //TODO alterar para ler um ficheiro com 8 bytes

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        int index = 0;
        byte[] toCipher = new byte[readDimensionBlock];
        byte[] bytesCiphered;

        Reader reader = new Reader();

        int bytesReaded = reader.readBytesFromFile(inputFilePath,toCipher, index);

        while(bytesReaded == readDimensionBlock)
        {
            bytesCiphered = cipher.update(toCipher);
            toCipher = new byte[readDimensionBlock];
            writer = new Writer();
            writer.writeToFileFromArray(resFileName, bytesCiphered);
            index += readDimensionBlock;
            currIndex += readDimensionBlock;
            reader = new Reader();
            bytesReaded = reader.readBytesFromFile(inputFilePath, toCipher, index);
        }

        bytesCiphered = cipher.doFinal(toCipher);
        writer = new Writer();
        writer.writeToFileFromArray(resFileName, bytesCiphered);

        currIndex += readDimensionBlock;;


        // AND NOW AUTHENTICATION!!!
    }

    private void setHeaderFileResult() throws IOException {

        File file = new File(resFileName);
        boolean result = Files.deleteIfExists(file.toPath());
        byte[] aux = new byte[headerSize];
        for (int i = 0; i < headerSize; i++) {
            aux[i] = (byte) 0x41;
        }
        Writer writer = new Writer();
        writer.writeToFileFromArray(resFileName,aux);
        currIndex += headerSize;
    }


    private static void verifyArgsLength(String[] args) {

        if (args.length != 3)
        {
            System.out.println("expection 3 arguments");
            return;
        }
    }

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {

        verifyArgsLength(args);
        CipherDecipher cd = new CipherDecipher(args);

        if (cd.mode.equals("-cipher"))
            cd.cipher();
        else
            cd.decipher();

        byte[] key = new byte[cd.headerSize];
        key[0] = (byte)0x41;
        key[1] = (byte)0x42;
        Writer writer = new Writer();
        writer.writeWhithoutAppend(cd.resFileName, 0, key);
    }


}
