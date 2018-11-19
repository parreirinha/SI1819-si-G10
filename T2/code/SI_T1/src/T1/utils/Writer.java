package T1.utils;

import java.io.*;

/**
 * Created by fabio on 11-Oct-18.
 */
public class Writer {

    public Writer(){}

    public void writeToFileFromArray(String path, byte[] bytes) {
        try {
            File file = new File(path);
            FileOutputStream writer = new FileOutputStream(file, true);
            writer.write(bytes);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFileWithNoAppend(String path, byte[] bytes) {
        try {
            File file = new File(path);
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(bytes);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void writeWhithoutAppend(String path, int offset, byte[] newByte) throws IOException {

            File file = new File(path);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            try {
                raf.seek(0); // Go to byte at offset position 5.
                raf.write(newByte, offset, newByte.length); // Write byte 70 (overwrites original byte at this offset).
            } finally {
                raf.close(); // Flush/save changes and close resource.
            }
    }*/
}