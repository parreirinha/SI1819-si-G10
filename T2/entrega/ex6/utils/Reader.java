package ex6.utils;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public byte[] getAllBytes(String path, int macSize) {

        byte[] res = new byte[macSize];
        try {
            File file = new File(path);
            InputStream is = new FileInputStream(file);
            is.read(res);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }



    public int getFileLength(String path)
    {
        File file = new File(path);
        return (int)file.length();
    }

    public byte[] getDigest(String path, String algorithm)
    {
        FileInputStream in = null;
        byte[] digest = null;

        try {
            in = new FileInputStream(path);
            MessageDigest sha = MessageDigest.getInstance(algorithm);
            DigestInputStream din = new DigestInputStream(in, sha);
            int b;
            while ((b = din.read()) != -1) ;
            din.close();
            digest = sha.digest();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return digest;
    }

}