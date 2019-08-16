public class TestPersonCopy {
    public static void main(String[] args) {


        B orgin = new B(1);
        orgin.name = "xiaoming";

        try {
            B byclone = (B) orgin.clone();
            orgin.a = 2;
            orgin.name = "xiaoming2";

            System.out.println("克隆而来的:" + byclone);
            System.out.println("原始:" + orgin);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

    }
}

class B implements Cloneable {
    int a;
    public String name;

    public B(int a) {
        this.a = a;
    }

    @Override
    public String toString() {
        return a + "  " + name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}