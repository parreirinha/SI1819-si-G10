import java.io.*;

/**
 * Created by fabio on 11-Oct-18.
 */
public class FileReaderWriter {


    private FileReaderWriter(){}
    private static FileReaderWriter frw = null;

    public static FileReaderWriter getInstance(){

        if (frw == null)
           frw = new FileReaderWriter();
        return frw;
    }

    public byte[] readBytesFromFile(String path) {
        byte[] getBytes = null;
        try {
            File file = new File(path);
            getBytes = new byte[(int) file.length()];
            InputStream is = new FileInputStream(file);
            is.read(getBytes);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getBytes;
    }

    public void createFileFromByteArray(String path, byte[] bytes) {
        try {
            FileOutputStream writer = new FileOutputStream(path);
            writer.write(bytes);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readKeyFromFile(String path)
    {
        String res = null;
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            res = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
