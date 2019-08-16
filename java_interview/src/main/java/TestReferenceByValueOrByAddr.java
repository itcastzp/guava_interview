public class TestReferenceByValueOrByAddr {


    public static void main(String[] args) {


        Person ps = new Person();
        //穿的是引用的拷贝，地址相同但是却不失同一个对象。
        TestReferenceByValueOrByAddr.change(ps);
        try {
            Person clone = (Person) ps.clone();
            clone.setName("wangwang");
            System.out.println(clone);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        System.out.println(ps + "hashcode===" + ps.hashCode());
        System.out.println(ps.getName());

    }

    private static void change(Person p) {
        Person local = null;
        try {
            local = (Person) p.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        //形参传递的是对象的地址引用的复制！
        System.out.println("before change" + p + "name" + p.hashCode());
        local.setName("lixiong");
        p = null;
        System.out.println("after change" + p + "name");

    }

}

class Person implements Cloneable {
    private String name = "lili";

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getName() {
        return name;
    }
}