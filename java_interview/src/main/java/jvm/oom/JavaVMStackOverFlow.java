package jvm.oom;

/**
 * vm-args:-Xss128k
 */
public class JavaVMStackOverFlow {
    private int stacklength = 1;

    public void stackLeak() {

        ++stacklength;
        stackLeak();
    }

    public static void main(String[] args) {
        JavaVMStackOverFlow javaVMStackOverFlow = new JavaVMStackOverFlow();
        try {
            javaVMStackOverFlow.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack legth" + javaVMStackOverFlow.stacklength);

            e.printStackTrace();
        }
    }
}
