package about.rerurn;

public class ReturnAndFinallyCheck {

    public static void main(String[] args) {
        System.out.println(CheckPrimate());
        System.out.println(CheckReference());
        System.out.println(CheckReference2());
    }

    private static int CheckPrimate() {
        int i = 5;
        try {
            i = 100;
            System.out.println(i);
            return i;
            //return的使用，意味着结束对函数的调用或者跳出函数体。一般所有语句都只能在return前执行！！！
            // return返会的值，其实并不是当前方法体内的变量，i变量其实是存在与栈中的当函数结束后，
            //当函数结束后，其对应的栈就会被回收，此时栈内的变量也将不复存在。所以返回的其实是
            //变量的复制，对于基本类型复制的是值，而对于引用类型，复制的是引用地址。finally中对引用地址指向的对象的修改，将直接
            //改变该对象
        } finally {
            System.out.println("finally executor");
//            System.exit(0);
            i = 8888;

        }


    }


    private static String CheckReference() {
        String a = "5";
        try {
            a = "aba";
            return a;//所有的方法都要在return之前执行，包括finally所以此处会执行return 888; 但是只针对基本数据类型
        } finally {
            System.out.println("finally executor");
            a = "finally";
            return a;
        }

    }

    private static Integer CheckReference2() {
        Integer a = Integer.valueOf("100");
        try {
            a = 200;
//            return a;//所有的方法都要在return之前执行，包括finally所以此处会执行return 888; 但是只针对基本数据类型
        } catch (Exception e) {
            a = 888888;
            return a;
        } finally {
            System.out.println("finally executor");
            a = new Integer("1");
//            return a;
        }
        return a;
    }
}
