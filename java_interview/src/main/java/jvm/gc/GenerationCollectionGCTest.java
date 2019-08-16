package jvm.gc;

/**
 * <note>
 * 根据对象存货周期将内存划分为几块，把堆分为新生代，年老代.然后根据年代进行各自合适的垃圾回收算法！
 * 新生代中：大批对象死去，那么选用复制算法合适。
 * 年老代中：对象的存活周期长，存活率高，没有额外空间担保内存的分配，所以必须使用标记整理或标记清除算法进行回收
 * </note>
 *
 * @program: guava_interview
 * @description: 分代收集算法
 * @author: AlphaGO
 * @create: 2019-08-15 20:34
 **/
public class GenerationCollectionGCTest {
}
