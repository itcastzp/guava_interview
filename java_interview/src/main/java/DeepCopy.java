import java.io.*;

public class DeepCopy {


    public static Object deepCopy(Object orgin) throws IOException, ClassNotFoundException {
        //写入对象
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(orgin);
        //读取对象
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        return (oi.readObject());
    }

}
