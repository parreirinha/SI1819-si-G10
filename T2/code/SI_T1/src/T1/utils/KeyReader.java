package T1.utils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by fabio on 21-Oct-18.
 */
public class KeyReader {

    private  KeyReader kr;
    private String path;

    public KeyReader(String path)
    {
        this.path = path;
    }

    public SecretKey getKey(){

        byte []keybyte = new byte[8];
        FileInputStream fin = null;
        SecretKey skey = null;

        try {
            fin = new FileInputStream(path);
            fin.read(keybyte);
            skey = new SecretKeySpec(keybyte, "DES");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return skey;
    }

}
