package jvm.gc.example;

/**
 * @program: guava_interview
 * @description: 年轻代的GC测试基于Serial垃圾收集器
 * @author: AlphaGO
 * @create: 2019-08-15 22:02
 **/
public class MinorGC {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        testAllocation();
    }

    //    -XX:+UseSerialGC -XX:+PrintGCDetails -Xms20M -Xmx20M -Xmn10M
    public static void testAllocation() {
        byte[] a1, a2, a3, a4;
        a1 = new byte[2 * _1MB];
        a2 = new byte[2 * _1MB];
        a3 = new byte[2 * _1MB];
        a4 = new byte[4 * _1MB];//预期出现一次MinorGc 年轻代的GC;
        System.gc();
    }

}
