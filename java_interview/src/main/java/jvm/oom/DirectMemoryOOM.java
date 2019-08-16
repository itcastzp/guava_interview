package jvm.oom;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @program: guava_interview
 * @description: 本机直接内存溢出-XX:MaxDirectMemorySize
 * @author: AlphaGO
 * @create: 2019-08-15 17:08
 **/
public class DirectMemoryOOM {

    public static void main(String[] args) {
//  直接调用肯定不行，     Exception in thread "main" java.lang.SecurityException: Unsafe
//        暴力反射调用！
        try {
            Field unsafe = Unsafe.class.getDeclaredField("theUnsafe");
            unsafe.setAccessible(true);

            Unsafe o = (Unsafe) unsafe.get(null);
            for (; ; ) {
//Exception in thread "main" java.lang.OutOfMemoryError
//               此处最好加上最大直接内存参数，否则会让计算机死机，内存一直暴增！
                o.allocateMemory(1024 * 1024);
                System.gc();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }


}
