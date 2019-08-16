package jvm.classloder;

public class ConstClass {
    static {
        System.out.println("ConstClass init");
    }
//    public static final String HELLOWORLD = "555555";可以不初始化
//    public static final String HELLOWORLD = new String ("555555");会初始化类的信息。
//    public static final Integer HELLOWORLD = 555555;会初始化类的信息。

    //  public static final int HELLOWORLD = Integer.valueOf("1000"); 会初始化类信息；
    public static final int HELLOWORLD = 9999;//不会

}

class Test1 {
    public static void main(String[] args) {

//        访问类的常量，不会初始化类
//        基本数据类型可以，以及指向性的String 可以初始化类
        System.out.println(ConstClass.HELLOWORLD);// 调用类常量
    }
}