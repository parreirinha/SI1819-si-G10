package utils;

import java.io.*;

/**
 * Created by fabio on 11-Oct-18.
 */
public class Reader {


    public Reader(){}

    public int readBytesFromFile(String path, byte[] buffer, int idx) {

        int res = -1;
        try {
            File file = new File(path);
            InputStream is = new FileInputStream(file);
            is.skip(idx);
            res = is.read(buffer);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
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