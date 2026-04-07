import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class StatusFile {
     public static void updateStatus(File file, int index, byte status){
         try (RandomAccessFile rnd = new RandomAccessFile(file, "rw");
              FileChannel ch = rnd.getChannel()){
             ch.position(index);
             ByteBuffer buffer = ByteBuffer.allocate(1);
             buffer.put(status);
             buffer.flip();
             ch.write(buffer);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }

     }
     public static byte readBack(File file, int index){
         ByteBuffer buffer;
         try (RandomAccessFile rnd = new RandomAccessFile(file, "rw");
              FileChannel ch = rnd.getChannel()){
             ch.position(index);
             buffer = ByteBuffer.allocate(1);
             ch.read(buffer);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
         buffer.flip();
         return buffer.get();
     }
public static final long N=20;
    static void main() throws FileNotFoundException {
        File binFile= new File("status.bin");
        try (RandomAccessFile rnd = new RandomAccessFile(binFile, "rw")){
           rnd.setLength(N);
           System.out.println("File is created with length "+N);
           updateStatus(binFile, 3, (byte) 7);
           System.out.println(readBack(binFile, 3));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
