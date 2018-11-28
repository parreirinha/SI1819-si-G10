package ex6.utils;

import java.io.*;

/**
 * Created by fabio on 11-Oct-18.
 */
public class FileReaderWriter {


    private FileReaderWriter(){}

    public static FileReaderWriter getInstance(){

        return new FileReaderWriter();
    }

    public int readBytesFromFile(String path, byte[] buffer, int idx, int len) {

        byte[] getBytes = new byte[buffer.length];
        int res = -1;
        try {
            File file = new File(path);
            InputStream is = new FileInputStream(file);
            res = is.read(getBytes,idx,len);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void writeToFileFromArray(String path, byte[] bytes, int idx, int len) {
        try {
            FileOutputStream writer = new FileOutputStream(path, true);
            writer.write(bytes, idx, len);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readAllFile(String path)
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