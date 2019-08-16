package jvm.oom;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestBug {

    @Test
    public void test() throws Exception {

        String s2 = new StringBuilder("A").append("D").toString();
        System.out.println(s2.intern() == s2);
        String s3 = new StringBuilder("ja").append("va").toString();
        System.out.println(s3.intern() == s3);
        Thread.sleep(100000);
    }

    @Test
    public void test111() throws Exception {

        String s2 = new StringBuilder("java").toString();
        System.out.println(s2.intern() == s2);
    }

    @Test
    public void test2() throws Exception {

        String s2 = new StringBuilder("UTF").append("-8").toString();
        System.out.println(s2.intern() == s2);
        Thread.sleep(100000);
    }

    @Test
    public void test3() throws Exception {

        String s2 = new StringBuilder("D").append("A").toString();
        System.out.println(s2.intern() == s2);
    }

    @Test
    public void test4() throws Exception {
        String s2 = "abc";
        String s1 = "a" + "bc";
        System.out.println(s1 == s2.intern());
    }

    public static void main(String[] args) throws InterruptedException {
        String s2 = new StringBuilder("D").append("A").toString();
        System.out.println(s2.intern() == s2);

        TimeUnit.SECONDS.sleep(10);
    }

}
