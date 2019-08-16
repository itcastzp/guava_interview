package jvm.gc;

/**
 * @program: guava_interview
 * @description: 复制算法的实现垃圾回收
 * @author: AlphaGO
 * @create: 2019-08-15 20:06
 **/
public class CopyGCTest {
    //复制收集算法：
// 将内存划分两个相等的区域，每次使用其中的一块，当这块用完后，将存活的对象复制到另一块上，然后把用完的那块进行回收清理，
// 实现简单，运行高效，牺牲了一半内存  IBM研究 98%新生代的对象稍纵即逝！
//Hotspot JVM   将内存分为一块大的Eden区，和两个小Survivor区  默认比例8：1：1
//    每次新生代可用内存为整个容量的80+10. /100，只浪费了10%；
//    我们不能保证每次回收后只有不足10%的对象存活，所以当survivor不够时，需要依赖老年代进行分配担保！！！进入ParOldGen
//
    public static void main(String[] args) {

    }
}
