package serializable_example;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class Box1Deser {
    public static void main(String[] args) {
        try {
            FileInputStream fileInput = new FileInputStream("foo.ser");
            ObjectInputStream os = new ObjectInputStream(fileInput);
            Object one = os.readObject();
            Box1 one1 = (Box1) one;
            os.close();
            System.out.println(one1.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
