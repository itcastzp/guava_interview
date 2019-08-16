package jvm.oom;

public class StringTest {
    public static void main(String[] args) throws InterruptedException {
        String s2 = new StringBuilder("ja").append("va").toString();
        System.out.println(s2.intern() == s2);
//        Thread.sleep(1000000);
    }
}
