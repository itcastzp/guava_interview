package jvm.gc;

/**
 * <note>
 * 复制收集算法在对象存活率高时，就要进行较多的复制操作。效率降低，如果不想浪费一般的空间，就需要老年代分配担保。
 * 以应对被使用的内存中100%存活对象的例子，所以老年代不能直接选用复制收集算法！
 * 标记整理与标记清除类似，但是后续不进行直接清除处理，而让存活的对象都向一段移动，然后直接清除掉边界以外的内存。
 * 有点压缩的含义！。
 * </note>
 *
 * @program: guava_interview
 * @description: 标记整理算法垃圾回收
 * @author: AlphaGO
 * @create: 2019-08-15 20:27
 **/
public class MarkCompactGCTest {
    public static void main(String[] args) {

    }
}
