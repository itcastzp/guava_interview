package jvm.classloder;

public class SubClassTest extends SuperClass {
    private int i = 10;

    public SubClassTest() {
        System.out.println("SubClass of constructor");
        super.m();
        m();
    }

    public void m() {
        System.out.println("SubClass.m(): i = " + i);
    }

    public static void main(String[] args) {
        SuperClass t = new SubClassTest();
        // SuperClass of constructor ->SubClass.m(): i = 0->SubClass of
        // constructor->SuperClass.m()->SubClass.m(): i = 10

        /*
         * <p>SuperClass of constructor SubClass.m(): i = 0 SubClass of
         * constructor SuperClass.m() SubClass.m(): i = 10</p>
         */
        /**
         * 可见当父类，和子类有Static时，先初始化Static，再初始化子类的Static，再初始化父类的其他成员变量－>父类构造方法－>
         * 子类其他成员变量－>子类的构造方法。
         * 父类上层还有父类时，总是先执行最顶层父类的Static－－>派生类Static－－>派生类Static
         * －－>.......－－>子类Static－－>顶层父类的其他成员变量－－>父类构造方法－－> 派生类的其他成员变量 －－>
         * 派生类构造方法－－> ...............－－>子类其他成员变量－－>子类构造方法
         */

    }
}

class SuperClass {
    public SuperClass() {
        System.out.println("SuperClass of constructor");
        m();
    }

    public void m() {
        System.out.println("SuperClass.m()");
    }
}