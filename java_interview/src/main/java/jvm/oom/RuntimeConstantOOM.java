package jvm.oom;

import java.util.ArrayList;

/**
 * since JDK 1.7
 * vmargs:-XX:PermSize=10M -XX:MaxPermSize=10m
 * 运行常量池中的内存溢出情况
 * JDK1.6之前常量池在方法区里，所以运行会出现PermSpace溢出错误！
 * but 在JDK1.7后，此种情况不会抛出异常，会一直运行
 */
public class RuntimeConstantOOM {

    /***
     * <p>
     *     方法区：
     *     存放class的相关信息，类名，访问修饰符，常量池，字段描述，方法描述等。
     * </p>
     *
     * @param args
     */
    public static void main(String[] args) {

        ArrayList<String> strings = new ArrayList<String>();
        int i = 0;
        for (; ; ) {
            strings.add(String.valueOf(i++).intern());
        }
    }


}
