import org.junit.Test;

import java.util.Arrays;

public class TestArraySCopy {

    @Test
    public void testDecorateType() {
        Integer[] orgin = new Integer[2];
        orgin[0] = new Integer(1);
        orgin[1] = new Integer(2);
        Integer[] copyof = orgin.clone();
        orgin[0] = 55;
        System.out.println("原始数组" + Arrays.toString(orgin));
        System.out.println("浅拷贝数组" + Arrays.toString(copyof));
    }

    @Test
    public void testPrimitive() {
        int[] orgin = new int[2];
        orgin[0] = 1;
        orgin[1] = 2;
        int[] copyof = orgin.clone();
        orgin[0] = 0;
        System.out.println("原始数组" + Arrays.toString(orgin));
        System.out.println("浅拷贝数组" + Arrays.toString(copyof));
    }

    @Test
    public void testManyReference() {
        int[] orgin = new int[2];
        orgin[0] = 1;
        orgin[1] = 2;
        int[] copyof = orgin.clone();
        orgin[0] = 0;
        System.out.println("原始数组" + Arrays.toString(orgin));
        System.out.println("浅拷贝数组" + Arrays.toString(copyof));
    }


    public static void main(String[] args) {
        A[] orgin = new A[2];
        orgin[0] = new A(1);
        orgin[1] = new A(2);
/*     OBJECT.clone()方法   注意，所有的数组都被视为实现接口 Cloneable。否则，此方法会创建此对象的类的一个新实例，
            并像通过分配那样，严格使用此对象相应字段的内容初始化该对象的所有字段；
        这些字段的内容没有被自我复制。所以，此方法执行的是该对象的“浅表复制”，而不“深层复制”操作。*/
        A[] copyOf = Arrays.copyOf(orgin, 2);
        A[] byClone = orgin.clone();
        A[] aa = orgin;

        System.out.println("原始对象" + Arrays.toString(orgin));
        System.out.println("原始第一个对象" + orgin[0] + "原始第二个对象" + orgin[1]);
        System.out.println("拷贝的第一个对象" + copyOf[0] + "拷贝第二个对象" + copyOf[1]);
        A a1 = orgin[0];
        a1.setA(11);
        // copyOf[0].setA(100);
        System.out.println("进行了");
        System.out.println("原始对象" + Arrays.toString(orgin));
        System.out.println("数组复制" + Arrays.toString(copyOf));
        System.out.println("克隆复制" + Arrays.toString(byClone));
        System.out.println("==复制对象" + Arrays.toString(aa));
        String[] a = new String[2];
        a[0] = "1";
        a[1] = "1";
        String[] b = Arrays.copyOf(a, 2);
        a[1] = "3";


        System.out.println(Arrays.toString(a));
        System.out.println(Arrays.toString(b));

    }
}

class A {
    private int a;

    public A(int a) {
        this.a = a;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    @Override
    public String toString() {
        return a + "";
    }
}