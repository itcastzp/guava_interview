package jvm.oom;

import org.junit.Test;

public class RuntimeConstantPoolOOM {

    @Test
    public void test() throws Exception {

        String a3 = "我自己么？";
        String a = new String("我自己么？");
        String a1 = new String("我自己么？");

        System.out.println(a.intern() != a);
        System.out.println(a.intern() == a1.intern());
        System.out.println(a.intern() == a3.intern());
        System.out.println(a.intern() == a3);
        System.out.println(a3.intern() == a.intern());
        System.out.println(a3.intern() == a3);
    }

    @Test
    public void testSB() throws Exception {
        String a;
        String s1 = new String(new StringBuilder().append("J" + "A").toString());
        System.out.println(s1.intern() == s1);
    }

    @Test
    public void testSB2() throws Exception {
        String s2 = new StringBuilder("A").append("A").toString();
        System.out.println(s2.intern() == s2);
    }

    @Test
    public void testSBB() throws Exception {
        String s2 = new StringBuilder("B").append("C").toString();
        System.out.println(s2.intern() == s2);
    }

    @Test
    public void testSB3() throws Exception {
        String s2 = new StringBuilder("B").append("C").toString();
        System.out.println(s2.intern() == s2);
    }

    @Test
    public void testSB1() throws Exception {
        String s1 = new StringBuilder("4").append("Z").toString();

        System.out.println(s1.intern() == s1);
    }

    @Test
    public void testSt() throws Exception {
        String s1 = new String("a");
        String s2 = new String("a");
        System.out.println(s2.intern() == s1.intern());
    }

    @Test
    public void testAddrSS() throws Exception {
        String s1 = new String(new String("aaabbba"));
        System.out.println(s1.intern() == s1);
    }

    public static void main(String[] args) {
        String s200 = new StringBuilder("B").append("C").toString();
        System.out.println(s200.intern() == s200);
        /**
         *
         * JDK1.6 全部为false;1.7以后s2为true;
         * 1.6:intern将首次遇到的字符串复制到永久代中，返会永久代的引用 而由stringbuilder建立的
         * 字符串是存在堆内存上的。所以必然不是一个引用。
         * <p>
         *     s1          -> HeapSpace 引用
         *     s1.intern() ->PermSpace 引用
         *</p>
         * 1.7:intern不会再执行复制。只记录首次出现在常量池中的##实例##引用。
         * 如果常量池已经存在则直接返会该引用
         * 因此intern返会的引用于StringBuilder建立的字符串实例是一个引用！
         * <p>
         *     s1           ->Heap 引用
         *     s1.intern()  ->Heap 引用
         * </p>
         *
         *
         */

        String s1 = new StringBuilder("计算机").append("世界").toString();
        System.out.println(s1.intern() == s1);//1.6->false  1.7->true
        String s3 = "JAVA";
        String s2 = new StringBuilder("JA").append("VA").toString();
        String s4 = new StringBuilder("JA").append("VA").toString();

        System.out.println(s2.intern() == s2);//1.6->false 1.7->false

        System.out.println(s3.intern() == s3);//1.6->true; 1.7->true
        System.out.println(s3.intern() == s2);//1.6->true; 1.7->false
        System.out.println(s3 == s2.intern());//1.6->true; 1.7->false
        System.out.println(s3.intern() == s2.intern());//1.6->true; 1.7->false
        System.out.println(s4.intern() == s3.intern());//1.6->true; 1.7->false

        System.out.println(s4.intern() == s2);//1.6->true; 1.7->true

    }
}
