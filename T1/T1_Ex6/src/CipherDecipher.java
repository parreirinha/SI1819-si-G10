import java.lang.*;

/**
 * Created by fabio on 13-Oct-18.
 * Exercicio 6
 */
public class CipherDecipher {



    private static void printByteArray(byte[] fileBytes) {
        for (int i = 0; i<fileBytes.length; i++) {
            System.out.println(fileBytes[i]);
        }
    }


    public static void main(String[] args) {

        final String path = System.getProperty("user.dir") + "\\src\\" ;
        final String mode = args[0];
        final String inputFilePath = path + args[1];
        final String keyFilePath = path + args[2];
        final String resFileName = path + mode.substring(1, mode.length()) + "Result";

        FileReaderWriter reader = FileReaderWriter.getInstance();

        String s = reader.readKeyFromFile(inputFilePath);
        System.out.println(s);
        System.out.println("$$$$$");

        byte[] fileBytes = reader.readBytesFromFile(inputFilePath);

        printByteArray(fileBytes);
        System.out.println("$$$$$");
        reader.createFileFromByteArray(resFileName, fileBytes);

        String k = reader.readKeyFromFile(keyFilePath);
        System.out.println("key = " + k);
    }

}
