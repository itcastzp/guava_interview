package jvm.gc;

/**
 * @program: guava_interview
 * @description: 引用计数算法的循环引用问题
 * @author: AlphaGO
 * @create: 2019-08-15 18:54
 **/
public class ReferenceCountingTest {
    public Object instance = null;

    //-XX:+TraceClassLoading 追踪类的加载堆栈，-XX:+TraceClassUnloading 追踪类的卸载信息
    public static void main(String[] args) {

        ReferenceCountingTest test1 = new ReferenceCountingTest();
        ReferenceCountingTest test2 = new ReferenceCountingTest();
//        相互循环 引用!!!
        test1.instance = test2;
        test2.instance = test1;
        test1 = null;
        test2 = null;
        System.gc();
/**由此看出虚拟机并没有因为循环引用而不进行回收，所以jvm使用的不是引用计数算法来判断对象的存活！
 * jvm通过可达性分析的算法，某些对象作为GC ROOTS起始点.如果对象不在GC ROOTS引用链中，则为不可达，即为
 * 可回收对对象！！！
 * 可作为GCROOTS的对象：
 * 虚拟机栈中引用的对象！
 * 方法区中静态属性引用的对象
 * 方法区中常量引用的对象
 * 本地方法栈（native）引用的对象
 *
 * <p>
 *      [GC [PSYoungGen: 5247K->728K(76288K)] 5247K->728K(249344K), 0.0013776 secs]
 *      [Times: user=0.00 sys=0.00, real=0.00 secs]
 *      [Full GC [PSYoungGen: 728K->0K(76288K)] [ParOldGen: 0K->587K(173056K)] 728K->587K(249344K)
 *      [PSPermGen: 2965K->2964K(21504K)], 0.0102087 secs]
 *      [Times: user=0.02 sys=0.00, real=0.01 secs]
 * </p>
 */

    }


}
