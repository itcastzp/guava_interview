package jvm.gc.example;

/**<note>
 *          一：对象优先在Eden区进行分配！
 *          当eden区域没有足够空间时，虚拟机将发起一次MinorGC；
 *          -XX:+PrintGCDetails：
 *          1.虚拟机在进行垃圾回收时，打印日志，
 *          2.虚拟机退出后输出当前内存各区域情况!
 *          二：大对象则直接分配在老年代！
 *          1.何为大对象？：大量连续内存空间的对象，->数组，很长的字符串（底层还是char数组）
 *          2.坏消息，jvm遇到短命的大对象！我们的代码应尽量避免！
 * </note>
 *
 *
 * @program: guava_interview
 * @description: 年轻代的GC测试基于Serial垃圾收集器
 * @author: AlphaGO
 * @create: 2019-08-15 22:02
 **/
public class MinorGC {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
//        testAllocation();
        testPretenureSizeThreshold();

    }


    //    -XX:+UseSerialGC -XX:+PrintGCDetails -Xms20M -Xmx20M -Xmn10M
    //10M分配给新生代，10M给老年代。
//   新生代总可用空间为Eden区+1个Surivivor区总容量=9216KB
    public static void testAllocation() {
        byte[] a1, a2, a3, a4;
        a1 = new byte[2 * _1MB];
        a2 = new byte[2 * _1MB];
        a3 = new byte[2 * _1MB];
        a4 = new byte[4 * _1MB];//预期出现一次MinorGc 年轻代的GC;
        /*
         * <p>
         *     当为A4分配内存是发现eden区的大小8M-6M，不够存放4M的a4，发生minorGC，
         *     将eden区的6M通过复制算法复制到Survivor区，可是survivor区只有1M大小，不够，通过分配担保机制
         *     将原Eden区的6M对象标记-清除或标记-整理算法，移动到老年代
         *     最后：A4成功分配到Eden区，原Eden区的对象分配在老年代。所以程序运行结果为
         *     Eden去占用为4M,survivor区空闲，老年代占用6M.
         * </p>
         *
         * */
//        System.gc();
    }

    public static void testPretenureSizeThreshold() {
        byte[] allocation;
        allocation = new byte[4 * _1MB];
        allocation = null;
    }


}
