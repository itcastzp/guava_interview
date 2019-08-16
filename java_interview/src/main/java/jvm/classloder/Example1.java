package jvm.classloder;


class SingleTon {
    public static int count1;
    public static SingleTon singleTon = new SingleTon();
    public static int count2 = 0;

    /*
    * 1:SingleTon singleTon = SingleTon.getInstance();调用了类的SingleTon调用了类的静态方法，触发类的初始化
    2:类加载的时候在准备过程中为类的静态变量分配内存并初始化默认值 singleton=null count1=0,count2=0
    3:类初始化化，为类的静态变量赋值和执行静态代码快。singleton赋值为new SingleTon()调用类的构造方法
    4:调用类的构造方法后count=1;count2=1
    5:然后   继续为count1与count2赋值,此时count1的静态没有赋值操作,所有count1为1,但是count2执行赋值操作就变为0
    如果换位置，保证count1count2都在 singleton之前，那么就可以全部都赋值为1
    *
    * */
    private SingleTon() {
        count1++;
        count2++;
        System.out.println("构造后count1:" + count1);
        System.out.println("构造后count2:" + count2);
    }

    public static SingleTon getInstance() {
        return null;
    }
}

class Test {
    public static void main(String[] args) {
        /*SingleTon singleTon = SingleTon.singleTon;
        System.out.println("count1=" + singleTon.count1);
        System.out.println("count2=" + singleTon.count2);*/
        System.out.println("count1=" + SingleTon.count1);
        System.out.println("count2=" + SingleTon.count2);

    }
}

