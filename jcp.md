# [Java并发结构](#1)
# [任务取消(Cancellation)](#2)
# [同步和Java内存模型](#3)


# <span id="1">Java并发结构</span>
原文链接：http://gee.cs.oswego.edu/dl/cpj/mechanics.html

<kbd>**Doug Lea
Last modified: Sun Oct 17 14:21:45 EDT 1999**</kbd>
## 线程
线程是一个独立执行的调用序列，同一个进程的线程在同一时刻共享一些系统资源（比如文件句柄等）也能访问同一个进程所创建的对象资源（内存资源）。java.lang.Thread对象负责统计和控制这种行为。

每个程序都至少拥有一个线程-即作为Java虚拟机(JVM)启动参数运行在主类main方法的线程。在Java虚拟机初始化过程中也可能启动其他的后台线程。这种线程的数目和种类因JVM的实现而异。然而所有用户级线程都是显式被构造并在主线程或者是其他用户线程中被启动。

这里对Thread类中的主要方法和属性以及一些使用注意事项作出总结。这些内容会在这本书(《Java Concurrency Constructs》)上进行进一步的讨论阐述。Java语言规范以及已发布的API文档中都会有更详细权威的描述。

## 构造方法
Thread类中不同的构造方法接受如下参数的不同组合：

一个Runnable对象，这种情况下，Thread.start方法将会调用对应Runnable对象的run方法。如果没有提供Runnable对象，那么就会立即得到一个Thread.run的默认实现。
一个作为线程标识名的String字符串，该标识在跟踪和调试过程中会非常有用，除此别无它用。
线程组（ThreadGroup），用来放置新创建的线程，如果提供的ThreadGroup不允许被访问，那么就会抛出一个SecurityException 。
Thread类本身就已经实现了Runnable接口，因此，除了提供一个用于执行的Runnable对象作为构造参数的办法之外，也可以创建一个Thread的子类，通过重写其run方法来达到同样的效果。然而，比较好的实践方法却是分开定义一个Runnable对象并用来作为构造方法的参数。将代码分散在不同的类中使得开发人员无需纠结于Runnable和Thread对象中使用的同步方法或同步块之间的内部交互。更普遍的是，这种分隔使得对操作的本身与其运行的上下文有着独立的控制。更好的是，同一个Runnable对象可以同时用来初始化其他的线程，也可以用于构造一些轻量化的执行框架(Executors)。另外需要提到的是通过继承Thread类实现线程的方式有一个缺点：使得该类无法再继承其他的类。

Thread对象拥有一个守护(daemon)标识属性，这个属性无法在构造方法中被赋值，但是可以在线程启动之前设置该属性(通过setDaemon方法)。当程序中所有的非守护线程都已经终止，调用setDaemon方法可能会导致虚拟机粗暴的终止线程并退出。isDaemon方法能够返回该属性的值。守护状态的作用非常有限，即使是后台线程在程序退出的时候也经常需要做一些清理工作。（daemon的发音为”day-mon”,这是系统编程传统的遗留，系统守护进程是一个持续运行的进程，比如打印机队列管理，它总是在系统中运行。）

## 启动线程
调用start方法会触发Thread实例以一个新的线程启动其run方法。新线程不会持有调用线程的任何同步锁。

当一个线程正常地运行结束或者抛出某种未检测的异常（比如，运行时异常(RuntimeException)，错误(ERROR) 或者其子类）线程就会终止。当线程终止之后，是不能被重新启动的。在同一个Thread上调用多次start方法会抛出InvalidThreadStateException异常。

如果线程已经启动但是还没有终止，那么调用isAlive方法就会返回true.即使线程由于某些原因处于阻塞(Blocked)状态该方法依然返回true。如果线程已经被取消(cancelled),那么调用其isAlive在什么时候返回false就因各Java虚拟机的实现而异了。没有方法可以得知一个处于非活动状态的线程是否已经被启动过了(译者注：即线程在开始运行前和结束运行后都会返回false，你无法得知处于false的线程具体的状态)。另一点，虽然一个线程能够得知同一个线程组的其他线程的标识，但是却无法得知自己是由哪个线程调用启动的。

## 优先级
Java虚拟机为了实现跨平台(不同的硬件平台和各种操作系统)的特性，Java语言在线程调度与调度公平性上未作出任何的承诺，甚至都不会严格保证线程会被执行。但是Java线程却支持优先级的方法，这些方法会影响线程的调度：

每个线程都有一个优先级，分布在Thread.MIN_PRIORITY和Thread.MAX_PRIORITY之间（分别为1和10）
默认情况下，新创建的线程都拥有和创建它的线程相同的优先级。main方法所关联的初始化线程拥有一个默认的优先级，这个优先级是Thread.NORM_PRIORITY (5).
线程的当前优先级可以通过getPriority方法获得。
线程的优先级可以通过setPriority方法来动态的修改，一个线程的最高优先级由其所在的线程组限定。

当可运行的线程数超过了可用的CPU数目的时候，线程调度器更偏向于去执行那些拥有更高优先级的线程。具体的策略因平台而异。比如有些Java虚拟机实现总是选择当前优先级最高的线程执行。有些虚拟机实现将Java中的十个优先级映射到系统所支持的更小范围的优先级上，因此，拥有不同优先级的线程可能最终被同等对待。还有些虚拟机会使用老化策略（随着时间的增长，线程的优先级逐渐升高）动态调整线程优先级，另一些虚拟机实现的调度策略会确保低优先级的线程最终还是能够有机会运行。设置线程优先级可以影响在同一台机器上运行的程序之间的调度结果，但是这不是必须的。


 
线程优先级对语义和正确性没有任何的影响。特别是，优先级管理不能用来代替锁机制。优先级仅仅是用来表明哪些线程是重要紧急的，当存在很多线程在激励进行CPU资源竞争的情况下，线程的优先级标识将会显得非常有用。比如，在ParticleApplet中将particle animation线程的优先级设置的比创建它们的applet线程低，在某些系统上能够提高对鼠标点击的响应，而且不会对其他功能造成影响。但是即使setPriority方法被定义为空实现，程序在设计上也应该保证能够正确执行（尽管可能会没有响应）。

下面这个表格列出不同类型任务在线程优先级设定上的通常约定。在很多并发应用中，在任一指定的时间点上，只有相对较少的线程处于可执行的状态（另外的线程可能由于各种原因处于阻塞状态），在这种情况下，没有什么理由需要去管理线程的优先级。另一些情况下，在线程优先级上的调整可能会对并发系统的调优起到一些作用。

范围  用途
10      Crisis management（应急处理）
7-9    Interactive, event-driven（交互相关，事件驱动）
4-6    IO-bound（IO限制类）
2-3    Background computation（后台计算）
1        Run only if nothing else can（仅在没有任何线程运行时运行的）

## 控制方法
只有很少几个方法可以用于跨线程交流：

每个线程都有一个相关的Boolean类型的中断标识。在线程t上调用t.interrupt会将该线程的中断标识设为true，除非线程t正处于Object.wait,Thread.sleep,或者Thread.join,这些情况下interrupt调用会导致t上的这些操作抛出InterruptedException异常，但是t的中断标识会被设为false。
任何一个线程的中断状态都可以通过调用isInterrupted方法来得到。如果线程已经通过interrupt方法被中断，这个方法将会返回true。
但是如果调用了Thread.interrupted方法且中断标识还没有被重置，或者是线程处于wait，sleep，join过程中，调用isInterrupted方法将会抛出InterruptedException异常。调用t.join()方法将会暂停执行调用线程，直到线程t执行完毕：当t.isAlive()方法返回false的时候调用t.join()将会直接返回(return)。另一个带参数毫秒(millisecond)的join方法在被调用时，如果线程没能够在指定的时间内完成，调用线程将重新得到控制权。因为isAlive方法的实现原理，所以在一个还没有启动的线程上调用join方法是没有任何意义的。同样的，试图在一个还没有创建的线程上调用join方法也是不明智的。
起初，Thread类还支持一些另外一些控制方法：suspend,resume,stop以及destroy。这几个方法已经被声明过期。其中destroy方法从来没有被实现，估计以后也不会。而通过使用等待/唤醒机制增加suspend和resume方法在安全性和可靠性的效果有所欠缺，将在3.2章节进行具体讨论。而stop方法所带来的问题也将在3.1.2.3进行探讨。

## 静态方法
Thread类中的部分方法被设计为只适用于当前正在运行的线程（即调用Thread方法的线程）。为强调这点，这些方法都被声明为静态的。

Thread.currentThread方法会返回当前线程的引用，得到这个引用可以用来调用其他的非静态方法，比如Thread.currentThread().getPriority()会返回调用线程的优先级。
Thread.interrupted方法会清除当前线程的中断状态并返回前一个状态。（一个线程的中断状态是不允许被其他线程清除的）
Thread.sleep(long msecs)方法会使得当前线程暂停执行至少msecs毫秒。
Thread.yield方法纯粹只是建议Java虚拟机对其他已经处于就绪状态的线程（如果有的话）调度执行，而不是当前线程。最终Java虚拟机如何去实现这种行为就完全看其喜好了。

尽管缺乏保障，但在不支持分时间片/可抢占式的线程调度方式的单CPU的Java虚拟机实现上，yield方法依然能够起到切实的作用。在这种情况下，线程只在被阻塞的情况下（比如等待IO，或是调用了sleep等）才会进行重新调度。在这些系统上，那些执行非阻塞的耗时的计算任务的线程就会占用CPU很长的时间，最终导致应用的响应能力降低。如果一个非阻塞的耗时计算线程会导致时间处理线程或者其他交互线程超出可容忍的限度的话，就可以在其中插入yield操作(或者是sleep)，使得具有较低线程优先级的线程也可以执行。为了避免不必要的影响，你可以只在偶然间调用yield方法，比如，可以在一个循环中插入如下代码:if (Math.random() < 0.01) Thread.yield();


 
在支持可抢占式调度的Java虚拟机实现上，线程调度器忽略yield操作可能是最完美的策略，特别是在多核处理器上。

## 线程组
每一个线程都是一个线程组中的成员。默认情况下，新建线程和创建它的线程属于同一个线程组。线程组是以树状分布的。当创建一个新的线程组，这个线程组成为当前线程组的子组。getThreadGroup方法会返回当前线程所属的线程组，对应地，ThreadGroup类也有方法可以得到哪些线程目前属于这个线程组，比如enumerate方法。

ThreadGroup类存在的一个目的是支持安全策略来动态的限制对该组的线程操作。比如对不属于同一组的线程调用interrupt是不合法的。这是为避免某些问题(比如，一个applet线程尝试杀掉主屏幕的刷新线程)所采取的措施。ThreadGroup也可以为该组所有线程设置一个最大的线程优先级。

线程组往往不会直接在程序中被使用。在大多数的应用中，如果仅仅是为在程序中跟踪线程对象的分组，那么普通的集合类（比如java.util.Vector）应是更好的选择。

在ThreadGroup类为数不多的几个方法中，uncaughtException方法却是非常有用的，当线程组中的某个线程因抛出未检测的异常（比如空指针异常NullPointerException）而中断的时候，调用这个方法可以打印出线程的调用栈信息。

## 同步
#### 对象与锁
每一个Object类及其子类的实例都拥有一个锁。其中，标量类型int，float等不是对象类型，但是标量类型可以通过其包装类来作为锁。单独的成员变量是不能被标明为同步的。锁只能用在使用了这些变量的方法上。然而正如在2.2.7.4上描述的，成员变量可以被声明为volatile，这种方式会影响该变量的原子性，可见性以及排序性。

类似的，持有标量变量元素的数组对象拥有锁，但是其中的标量元素却不拥有锁。（也就是说，没有办法将数组成员声明为volatile类型的）。如果锁住了一个数组并不代表其数组成员都可以被原子的锁定。也没有能在一个原子操作中锁住多个对象的方法。

Class实例本质上是个对象。正如下所述，在静态同步方法中用的就是类对象的锁。

#### 同步方法和同步块
使用synchronized关键字，有两种语法结构：同步代码块和同步方法。同步代码块需要提供一个作为锁的对象参数。这就允许了任意方法可以去锁任一一个对象。但在同步代码块中使用的最普通的参数却是this。

同步代码块被认为比同步方法更加的基础。如下两种声明方式是等同的：

1
synchronized void f() { /* body */ }
2
void f() { synchronized(this) { /* body */ } }
synchronized关键字并不是方法签名的一部分。所以当子类覆写父类中的同步方法或是接口中声明的同步方法的时候，synchronized修饰符是不会被自动继承的，另外，构造方法不可能是真正同步的（尽管可以在构造方法中使用同步块）。

同步实例方法在其子类和父类中使用同样的锁。但是内部类方法的同步却独立于其外部类， 然而一个非静态的内部类方法可以通过下面这种方式锁住其外部类：

1
synchronized(OuterClass.this) { /* body */ }
#### 等待锁与释放锁
使用synchronized关键字须遵循一套内置的锁等待-释放机制。所有的锁都是块结构的。当进入一个同步方法或同步块的时候必须获得该锁，而退出的时候（即使是异常退出）必须释放这个锁。你不能忘记释放锁。

锁操作是建立在独立的线程上的而不是独立的调用基础上。一个线程能够进入一个同步代码的条件是当前锁未被占用或者是当前线程已经占用了这个锁，否则线程就会阻塞住。（这种可重入锁或是递归锁不同于POSIX线程）。这就允许一个同步方法可以去直接调用同一个锁管理的另一个同步方法，而不需要被冻结（注：即不需要再经历释放锁-阻塞-申请锁的过程）。


 
同步方法或同步块遵循这种锁获取/锁释放的机制有一个前提，那就是所有的同步方法或同步块都是在同一个锁对象上。如果一个同步方法正在执行中，其他的非同步方法也可以在任何时候执行。也就是说，同步不等于原子性，但是同步机制可以用来实现原子性。

当一个线程释放锁的时候，另一个线程可能正等待这个锁（也可能是同一个线程，因为这个线程可能需要进入另一个同步方法）。但是关于哪一个线程能够紧接着获得这个锁以及什么时候，这是没有任何保证的。（也就是，没有任何的公平性保证-见3.4.1.5）另外，没有什么办法能够得到一个给定的锁正被哪个线程拥有着。

正如2.2.7讨论的，除了锁控制之外，同步也会对底层的内存系统带来副作用。

#### 静态变量/方法
锁住一个对象并不会原子性的保护该对象类或其父类的静态成员变量。而应该通过同步的静态方法或代码块来保证访问一个静态的成员变量。静态同步使用的是静态方法锁声明的类对象所拥有的锁。类C的静态锁可以通过内置的实例方法获取到：
synchronized(C.class) { /* body */ }

每个类所对应的静态锁和其他的类（包括其父类）没有任何的关系。通过在子类中增加一个静态同步方法来试图保护父类中的静态成员变量是无效的。应使用显式的代码块来代替。

如下这种方式也是一种不好的实践：
synchronized(getClass()) { /* body */ } // Do not use
这种方式，可能锁住的实际中的类，并不是需要保护的静态成员变量所对应的类（有可能是其子类）


 
Java虚拟机在类加载和类初始化阶段，内部获得并释放类锁。除非你要去写一个特殊的类加载器或者需要使用多个锁来控制静态初始顺序，这些内部机制不应该干扰普通类对象的同步方法和同步块的使用。Java虚拟机没有什么内部操作可以独立的获取你创建和使用的类对象的锁。然而当你继承java.*的类的时候，你需要特别小心这些类中使用的锁机制。

#### 监视器
正如每个对象都有一个锁一样，每一个对象同时拥有一个由这些方法(wait,notify,notifyAll,Thread,interrupt)管理的一个等待集合。拥有锁和等待集合的实体通常被称为监视器（虽然每种语言定义的细节略有不同），任何一个对象都可以作为一个监视器。

对象的等待集合是由Java虚拟机来管理的。每个等待集合上都持有在当前对象上等待但尚未被唤醒或是释放的阻塞线程。

因为与等待集合交互的方法（wait，notify，notifyAll）只在拥有目标对象的锁的情况下才被调用，因此无法在编译阶段验证其正确性，但在运行阶段错误的操作会导致抛出IllegalMonitorStateException异常。

这些方法的操作描述如下：

##### Wait
调用wait方法会产生如下操作：

如果当前线程已经终止，那么这个方法会立即退出并抛出一个InterruptedException异常。否则当前线程就进入阻塞状态。
Java虚拟机将该线程放置在目标对象的等待集合中。
释放目标对象的同步锁，但是除此之外的其他锁依然由该线程持有。即使是在目标对象上多次嵌套的同步调用，所持有的可重入锁也会完整的释放。这样，后面恢复的时候，当前的锁状态能够完全地恢复。
##### Notify
调用Notify会产生如下操作：

Java虚拟机从目标对象的等待集合中随意选择一个线程(称为T，前提是等待集合中还存在一个或多个线程)并从等待集合中移出T。当等待集合中存在多个线程时，并没有机制保证哪个线程会被选择到。
线程T必须重新获得目标对象的锁，直到有线程调用notify释放该锁，否则线程会一直阻塞下去。如果其他线程先一步获得了该锁，那么线程T将继续进入阻塞状态。
线程T从之前wait的点开始继续执行。
##### NotifyAll

notifyAll方法与notify方法的运行机制是一样的，只是这些过程是在对象等待集合中的所有线程上发生（事实上，是同时发生）的。但是因为这些线程都需要获得同一个锁，最终也只能有一个线程继续执行下去。

##### Interrupt（中断）
如果在一个因wait而中断的线程上调用Thread.interrupt方法，之后的处理机制和notify机制相同，只是在重新获取这个锁之后，该方法将会抛出一个InterruptedException异常并且线程的中断标识将被设为false。如果interrupt操作和一个notify操作在同一时间发生，那么不能保证那个操作先被执行，因此任何一个结果都是可能的。（JLS的未来版本可能会对这些操作结果提供确定性保证）


 
##### Timed Wait（定时等待）
定时版本的wait方法，wait(long mesecs)和wait(long msecs,int nanosecs),参数指定了需要在等待集合中等待的最大时间值。如果在时间限制之内没有被唤醒，它将自动释放，除此之外，其他的操作都和无参数的wait方法一样。并没有状态能够表明线程正常唤醒与超时唤醒之间的不同。需要注意的是，wait(0)与wait(0,0)方法其实都具有特殊的意义，其相当于不限时的wait()方法，这可能与你的直觉相反。

由于线程竞争，调度策略以及定时器粒度等方面的原因，定时等待方法可能会消耗任意的时间。（注：关于定时器粒度并没有任何的保证，目前大多数的Java虚拟机实现当参数设置小于1毫秒的时候，观察的结果基本上在1～20毫秒之间）

Thread.sleep(long msecs)方法使用了定时等待的wait方法，但是使用的并不是当前对象的同步锁。它的效果如下描述：
if (msecs != 0)  {
Object s = new Object();
synchronized(s) { s.wait(msecs); }
}
当然，系统不需要使用这种方式去实现sleep方法。需要注意的，sleep(0)方法的含义是中断线程至少零时间，随便怎么解释都行。（译者注：该方法有着特殊的作用，从原理上它可以促使系统重新进行一次CPU竞争）。

# <span id="2">任务取消(Cancellation)</span>
原文链接：http://gee.cs.oswego.edu/dl/cpj/cancel.html

<kbd>**Doug Lea
Last modified: Sun Oct 17 14:21:45 EDT 1999**</kbd>

<p>当某个线程中的活动执行失败或想改变运行意图，也许就有必要或想要在其它线程中取消这个线程的活动，而不管这个线程正在做什么。取消会给运行中的线程带来一些无法预料的失败情况。取消操作异步特性相关的设计技巧，让人想起了因系统崩溃和连接断开任何时候都有可能失败的分布式系统的那些技巧。并发程序还要确保多线程共享的对象的状态一致性。
在大多数多线程程序中，取消任务（Cancellation）是普遍存在的，常见于：
几乎所有与GUI中取消按钮相关的活动。
多媒体演示（如动画循环）中的正常终止活动。
线程中生成的结果不再需要。例如使用多个线程搜索数据库，只要某个线程返回了结果，其它的都可以取消掉。
由于一组活动中的一或多个遇到意外错误或异常导致整组活动无法继续。
脚注：在并发编程中两个l的cancellation最常见。译者注：英语”取消”有两种写法cancelation和cancellation</p>
 

## 中断（Interruption）

实现取消任务的最佳技术是使用线程的中断状态，这个状态由Thread.interrupt设置，可被Thread.isInterrupted检测到，通过Thread.interrupted清除，有时候抛出InterruptedException异常来响应。

脚注：JDK1.0不支持中断机制。版本间政策与机制（policies and mechanisms）的更新说明对任务取消支持中的不规则行为做出了说明

线程中断起着请求取消活动的作用。但无法阻止有人将其用作它途，而用来作取消操作是预期的用途。基于中断的任务取消依赖于取消者和被取消者间的一个协议以确保跨多线程使用的对象在被取消线程终止的时候不被损坏。大部分（理想情况下是所有的）java.*包中的类都遵守这个协议。

几乎在所有的情况下，取消一个与线程有关系的活动都应当终止对应的线程。但中断机制不会强制线程立马终止。这就给任何被中断的线程一个在终止前做些清理操作的机会，但也给代码强加了及时检查中断状态以及采取合适操作的职责。

延迟甚至忽略任务取消的请求给写出良好响应性且非常健壮的代码提供了途径。因为不会直接将线程中断掉，所以很难或不可能撤销的动作的前面可以作为一个安全点，然后在此安全点检查中断状态。响应中断大部分可选的方式在§3.1.1中有讨论：

继续执行（忽略或清除了中断）可能适用于那些不打算终止的线程；例如，那些对于程序基本功能不可或缺的数据库管理服务。一旦遇到中断，可中止这些特殊的任务，然后允许线程继续执行其它任务。然而，即使在这里，将中断的线程替换成一个处于初始状态的新启动的线程会更易于管理。

突然终止（比如抛出错误）一般适用于提供独立服务、除了run方法中finally子句外无需其它清理操作的线程。但是，当线程执行的服务被其它线程依赖时（见§4.3），就应当以某种形式通知这些依赖的线程或设置状态指示。（异常本身不会自动在线程间传播）

线程中使用的对象被其它线程依赖时必须使用回滚或前滚技术。

在某种程度上，可以通过决定多久用Thread.currentThread().isInterrupted()来检查中断状态以控制代码对中断的响应灵敏性。中断状态检查不需要太频繁以免影响程序效率。例如，如果需要取消的活动包含大约10000条指令，每10000条指令做一次取消检查，那么从取消请求到关闭平均会耗费15000条指令。只要活动继续运行没有什么实际的危害，这个数量级可以满足大部分应用的需要。通常，这个理由可以让你将中断检测代码仅放到既方便检测又是重要的程序点。在性能关键型应用中，也许值得构建一个分析模型或收集经验值来准确地决定响应性与吞吐量间的最佳权衡（参见§4.4.1.7）。

Object.wait、Thread.join、Thread.sleep以及它们衍生出的方法都会自动检测中断。这些方法一旦中断就会抛出InterruptedException来中止，然后让线程苏醒并执行与活动取消相关的代码。

按照惯例，应当在抛出InterruptedException时清除中断状态。有时候有必要这样做来支持一些清理工作，但这也可能是错误与混乱之源。当处理完InterruptedException后想要传播中断状态，必须要么重新抛出捕获的InterruptedException，要么通过Thread.currentThread().interrupt()重新设置中断状态。如果你的代码调用了其它未正确维持中断状态的代码（例如，忽略InterruptedException又不重设状态），可以能通过维持一个字段来规避问题，这个字段用于保存活动取消的标识，在调用interrupt的时候设置该字段，从有问题的调用中返回时检查该字段。

有两种情况线程会保持休眠而无法检测中断状态或接收InterruptedException：在同步块中和在IO中阻塞时。线程在等待同步方法或同步块的锁时不会对中断有响应。但是，如§2.5中讨论的，当需要大幅降低在活动取消期间被卡在锁等待中的几率，可以使用lock工具类。使用lock类的代码阻塞仅是为了访问锁对象本身，而不是这些锁所保护的代码。这些阻塞的耗时天生就很短（尽管时间不能严格保证）。

## IO和资源撤销（IO and resource revocation）

一些IO支持类（尤其是java.net.Socket及其相关类）提供了在读操作阻塞的时候能够超时的可选途径，在这种情况下就可以在超时后检测中断。java.io中的其它类采用了另一种方式——一种特殊形式的资源撤销。如果某个线程在一个IO对象s（如InputStream）上执行s.close()，那么任何其它尝试使用s的线程将收到一个IOException。IO关闭会影响所有使用关闭了的IO对象的线程，会导致IO对象不可用。如有必要，可以创建一个新IO对象来替代关闭了的IO对象。

这与其它资源撤销的用途密切相关（如为了安全目的）。该策略也会保护应用免让共享的IO对象因其它使用了此IO对象的线程被取消而自动变得不可用。大部分java.io中的类不会也不能在出现IO异常时清除失败状态。例如，如果在StreamTokenizer或ObjectInputStream操作中间出现了一个底层IO异常，没有一个实用的恢复动作能继续保持预期的保障。所以，作为一种策略，JVM不会自动中断IO操作。

这给代码强加了额外的职责来处理取消事件。若一个线程正在执行IO操作，如果在此IO操作期间试图取消该IO操作，必须意识到IO对象正在使用且关闭该IO对象是你想要的行为。如果能接受这种情况，就可以通过关闭IO对象和中断线程来完成活动取消。例如：
```
class CancellableReader {                        // Incomplete
	private Thread readerThread; // only one at a time supported
	private FileInputStream dataFile;

	public synchronized void startReaderThread() 
		throws IllegalStateException, FileNotFoundException {
		if (readerThread != null) throw new IllegalStateException();
			dataFile = new FileInputStream("data");
			readerThread = new Thread(new Runnable() {
				public void run() { doRead(); }
			});
		readerThread.start();
	}

	protected synchronized void closeFile() { // utility method
		if (dataFile != null) {
			try { dataFile.close(); } 
			catch (IOException ignore) {}
			dataFile = null;
		}
	}

	protected void doRead() {
		try {
			while (!Thread.interrupted()) {
				try {
					int c = dataFile.read();
					if (c == -1) break;
					else process(c);
				} catch (IOException ex) {
					break; // perhaps first do other cleanup
				}
			}
		} finally {
			closeFile();
			synchronized(this) { readerThread = null; }
		}
	}

	public synchronized void cancelReaderThread() {
		if (readerThread != null) readerThread.interrupt();
			closeFile();
	}
}
```
很多其它取消IO的场景源于需要中断那些等待输入而输入却不会或不能及时到来的线程。大部分基于套接字的流，可以通过设置套接字的超时参数来处理。其它的，可以依赖InputStream.available，然后手写自己的带时间限制的轮询循环来避免超时之后还阻塞在IO中（见§4.1.5）。这种设计可以使用一种类似于§3.1.1.5中描述的有时间限制的退避重试协议。例如：
```
class ReaderWithTimeout {                // Generic code sketch
	// ...
	void attemptRead(InputStream stream, long timeout) throws... {
		long startTime = System.currentTimeMillis();
		try {
			for (;;) {
				if (stream.available() > 0) {
					int c = stream.read();
					if (c != -1) process(c);
					else break; // eof
				} else {
					try {
						Thread.sleep(100); // arbitrary fixed back-off time
					} catch (InterruptedException ie) {
						/* ... quietly wrap up and return ... */ 
					}
					long now = System.currentTimeMillis();
					if (now - startTime >= timeout) {
						/* ... fail ...*/
					}
				}
			}
		} catch (IOException ex) { /* ... fail ... */ }
	}
}
```
脚注：有些JDK发布版本也支持InterruptedIOException，但只是部分实现了且仅限于某些平台。在本文撰写之时，未来版本打算停止对其支持，部分原因是由于IO对象不可用会引起不良后果。但既然InterruptedIOException定义为IOException的一个子类，这种设计的工作方式与包含InterruptedIOException支持的版本上描述的相似，尽管存在额外的不确定性：中断可能抛出InterruptedIOException或InterruptedException。捕获InterruptedIOException然后将其作为一个InterruptedException重新抛出能部分解决该问题。

## 异步终止（Asynchronous termination）

stop方法起初包含在Thread类中，但是已经不推荐使用了。Thread.stop会导致不管线程正在做什么就突然抛出一个ThreadDeath异常。（与interrupt类似，stop不会中止锁等待或IO等待。但与interrupt不同的是，它不严格保证会中止wait，sleep或join）

这会是个非常危险的操作。因为Thread.stop产生异步信号，某些操作由于程序安全和对象一致性必须回滚或前滚，而活动正在执行这些操作或代码段时可能被终止掉。看下面例子：
```
class C {                                         // Fragments
	private int v;  // invariant: v >= 0

	synchronized void f() {
		v = -1  ;   // temporarily set to illegal value as flag
		compute();  // possible stop point (*)
		v = 1;      // set to legal value
	}

	synchronized void g() { 
		while (v != 0) { 
			--v; 
			something(); 
		} 
	}
}
```
如果Thread.stop碰巧导致(*)行终止，对象就被破坏了：线程一终止，对象将保持在不一致状态，因为变量v被设了一个非法的值。其它线程在该对象上的任何调用会执行不想要的或危险的操作。例如，这里g方法中的循环将自旋2*Integer.MAX_VALUE次。stop让回滚或前滚恢复技术的使用变得极其困难。乍一看，这个问题看起来不太严重 —— 毕竟，调用compute抛出的任何未捕获异常都会破坏状态。但是，Thread.stop的后果更隐蔽，因为在可能忽略了ThreadDeath异常（由Thread.stop抛出）而仍传播取消请求的方法中你什么也做不了。而且，除非在每行代码后都放一个catch(ThreadDeath)，否则就没办法准确恢复当前对象的状态，所以可能碰到未检测到的破坏。相比之下，通常可以将代码写的健壮些，不用大费周章就能消除或处理其它类型的运行时异常。

换而言之，禁用Thread.stop不是为了修复它有缺陷的逻辑，而是纠正对其功能的错误认识。不可能允许所有方法的每条字节码都能出现取消操作导致的异常（底层操作系统代码开发者非常熟悉这个事实。即使程序非常短，很小的异步取消安全的例程也会是个艰巨的任务。）


 
注意，任意正在执行的方法可以捕获并忽略由stop导致的ThreadDeath异常。这样的话，stop就和interrupt一样不能保证线程会被终止，这更危险。任何stop的使用都暗含着开发者评估过试图突然终止某个活动带来的潜在危害比不这样做的潜在危害更大。

## 资源控制（Resource control）

活动取消可能出现在可装载和执行外部代码的任一系统的设计中。试图取消未遵守标准约定的代码面临着难题。外部代码也许完全忽略了中断，甚至是捕获ThreadDeath异常后将其丢弃，在这种情况下调用Thread.interrupt和Thread.stop将不会有什么效果。

你无法精确控制外来代码的行为及其耗时。但能够且应当使用标准的安全措施来限制不良后果。一种方式是创建和使用一个SecurityManager，当某个线程运行的时间太长，就拒绝所有对受检资源的请求。（细节内容超出本书范围，参考推荐读物。）这种形式的资源拒绝同§3.1.2.2中讨论的资源撤销策略一起能够阻止外部代码执行任一与其它应当继续执行的线程竞争资源。副作用就是这些措施经常最终会导致线程因异常而挂掉。

此外，可以调用某个线程的setPriority(Thread.MIN_PRIORITY)将CPU资源的竞争降到最小。可以用一个SecurityManager来阻止该线程将优先级提高。

## 多步取消（Multiphase cancellation）

有时候，即使取消的是普通的代码，损害也比通常的更大。为应付这种可能性，可以建立一个通用的多步取消功能，尽可能尝试以破坏性最小的方式来取消任务，如果稍候还没有终止，再使用一种破坏性较大的方式。

在大多数操作系统进程级，多步取消是一种常见的模式。例如，它用在Unix关闭期间，先尝试使用kill -1终止任务，若有必要随后再使用kill -9.大多数win系统中的任务管理器也使用了类似的策略。

这里有个简单版本的示例。（Thread.join使用方面的更多细节参见§4.3.2.）

```
class Terminator {
	// Try to kill; return true if known to be dead

	static boolean terminate(Thread t, long maxWaitToDie) { 
		
		if (!t.isAlive()) return true;  // already dead
		// phase 1 -- graceful cancellation
		
		t.interrupt();       
		try { t.join(maxWaitToDie); } 
		catch(InterruptedException e){} //  ignore 

		if (!t.isAlive()) return true;  // success

		// phase 2 -- trap all security checks

		theSecurityMgr.denyAllChecksFor(t); // a made-up method
		try { t.join(maxWaitToDie); } 
		catch(InterruptedException ex) {} 

		if (!t.isAlive()) return true; 

		// phase 3 -- minimize damage

		t.setPriority(Thread.MIN_PRIORITY);
		return false;
	}
}
```
注意这里的terminate方法本身忽略了中断。这表明取消操作所做的这种策略选择一旦开始就必须继续。取消正在执行的取消操作，会给处理已经开始的与终止相关的清理带来另外一些问题。

因不同JVM实现中Thread.isAlive的行为不尽相同（参见§1.1.2），当join因线程结束返回后，在线程完全死掉之前isAlive还有可能返回true。


# <span id="3">同步与Java内存模型</span>
原文地址：http://gee.cs.oswego.edu/dl/cpj/jmm.html

<kbd>**Doug Lea Last modified: Sun Oct 17 14:21:45 EDT 1999**</kbd>

先来看如下这个简单的Java类，该类中并没有使用任何的同步。
 ```java
 final class SetCheck {
	private int  a = 0;
	private long b = 0;

    void set() {
    a =  1;
    b = -1;
    }

    boolean check() {
    return ((b ==  0) ||
    (b == -1 && a == 1));
    }
}
 
 ```
如果是在一个串行执行的语言中，执行SetCheck类中的check方法永远不会返回false，即使编译器，运行时和计算机硬件并没有按照你所期望的逻辑来处理这段程序，该方法依然不会返回false。在程序执行过程中，下面这些你所不能预料的行为都是可能发生的：

+ 编译器可能会进行指令重排序，所以b变量的赋值操作可能先于a变量。如果是一个内联方法，编译器可能更甚一步将该方法的指令与其他语句进行重排序。

+ 处理器可能会对语句所对应的机器指令进行重排序之后再执行，甚至并发地去执行。

+ 内存系统（由高速缓存控制单元组成）可能会对变量所对应的内存单元的写操作指令进行重排序。重排之后的写操作可能会对其他的计算/内存操作造成覆盖。

+ 编译器，处理器以及内存系统可能会让两条语句的机器指令交错。比如在32位机器上，b变量的高位字节先被写入，然后是a变量，紧接着才会是b变量的低位字节。
+ 编译器，处理器以及内存系统可能会导致代表两个变量的内存单元在（如果有的话）连续的check调用（如果有的话）之后的某个时刻才更新，而以这种方式保存相应的值（如在CPU寄存器中）仍会得到预期的结果（check永远不会返回false）。

在串行执行的语言中，只要程序执行遵循类似串行的语义，如上几种行为就不会有任何的影响。在一段简单的代码块中，串行执行程序不会依赖于代码的内部执行细节，因此如上的几种行为可以随意控制代码。这样就为编译器和计算机硬件提供了基本的灵活性。基于此，在过去的数十年内很多技术（CPU的流水线操作，多级缓存，读写平衡，寄存器分配等等）应运而生，为计算机处理速度的大幅提升奠定了基础。这些操作的类似串行执行的特性可以让开发人员无须知道其内部发生了什么。对于开发人员来说，如果不创建自己的线程，那么这些行为也不会对其产生任何的影响。

然而这些情况在并发编程中就完全不一样了，上面的代码在并发过程中，当一个线程调用check方法的时候完全有可能另一个线程正在执行set方法，这种情况下check方法就会将上面提到的优化操作过程暴露出来。如果上述任意一个操作发生，那么check方法就有可能返回false。例如，check方法读取long类型的变量b的时候可能得到的既不是0也不是-1.而是一个被写入一半的值。另一种情况，set方法中的语句的乱序执行有可能导致check方法读取变量b的值的时候是-1，然而读取变量a时却依然是0。

换句话说，不仅是并发执行会导致问题，而且在一些优化操作（比如指令重排序）进行之后也会导致代码执行结果和源代码中的逻辑有所出入。由于编译器和运行时技术的日趋成熟以及多处理器的逐渐普及，这种现象就变得越来越普遍。对于那些一直从事串行编程背景的开发人员（其实，基本上所有的程序员）来说，这可能会导致令人诧异的结果，而这些结果可能从没在串行编程中出现过。这可能就是那些微妙难解的并发编程错误的根本源头吧。

在绝大部分的情况下，有一个很简单易行的方法来避免那些在复杂的并发程序中因代码执行优化导致的问题：使用同步。例如，如果SetCheck类中所有的方法都被声明为synchronized,那么你就可以确保那么内部处理细节都不会影响代码预期的结果了。

但是在有些情况下你却不能或者不想去使用同步，抑或着你需要推断别人未使用同步的代码。在这些情况下你只能依赖Java内存模型所阐述的结果语义所提供的最小保证。Java内存模型允许上面提到的所有操作，但是限制了它们在执行语义上潜在的结果，此外还提出了一些技术让程序员可以用来控制这些语义的某些方面。

Java内存模型是Java语言规范的一部分，主要在JLS的第17章节介绍。这里，我们只是讨论一些基本的动机，属性以及模型的程序一致性。这里对JLS第一版中所缺少的部分进行了澄清。

我们假设Java内存模型可以被看作在1.2.4中描述的那种标准的SMP机器的理想化模型。

![理想化模型](data:image/png;base64,R0lGODlhHQLWAPcAAP///wAAAOq+k8ahfPbj0YBoUAAAAMevr6mUlOfd3WteXgAAAI6iwXiKpM3W5ElUZP///9TU2rS0ue3t8HZ2egAAAMevr9TU2j8/P97e3r29vSEhIWNjY0JCQpSUlHNzc1FR+///APoTQADuAP8A/39/f0xMTAAAAAAAPwAAfwAAvwAA/wA/AAA/PwA/fwA/vwA//wB/AAB/PwB/fwB/vwB//wC/AAC/PwC/fwC/vwC//wD/AAD/PwD/fwD/vwD//z8AAD8APz8Afz8Avz8A/z8/AD8/Pz8/fz8/vz8//z9/AD9/Pz9/fz9/vz9//z+/AD+/Pz+/fz+/vz+//z//AD//Pz//fz//vz///38AAH8AP38Af38Av38A/38/AH8/P38/f38/v38//39/AH9/P39/f39/v39//3+/AH+/P3+/f3+/v3+//3//AH//P3//f3//v3///78AAL8AP78Af78Av78A/78/AL8/P78/f78/v78//79/AL9/P79/f79/v79//7+/AL+/P7+/f7+/v7+//7//AL//P7//f7//v7////8AAP8AP/8Af/8Av/8A//8/AP8/P/8/f/8/v/8///9/AP9/P/9/f/9/v/9///+/AP+/P/+/f/+/v/+/////AP//P///f///v////wAAABERESIiIjMzM0RERFVVVWZmZnd3d4iIiJmZmaqqqru7u8zMzN3d3e7u7v///ziMKOUA//+ZAJmZmQD+/urg4HFkZKmVlYyMjP/vAPf/AN//AMf/AK//AJf/AH//AGf/AE//ADf/AB//AAf/AAD/DwD/JwD/PwD/VwD/bwD/hwD/nwD/twD/zwD/5wD//wDn/wDP/wC3/wCf/wCH/wBv/wBX/wA//wAn/wAP/wcA/x8A/zcA/08A/2cA/38A/5cA/68A/8cA/98A//cA//8A7/8A1/8Av/8Ap/8Aj/8Ad/8AX/8AR/8AL/8AFwBdDBKWNVEATIAAgL2Wcp+ipaJFoj+fojZMk/X19URERDTIAJOHbyH5BAAAAKYALAAAAAAdAtYAAAj+AAEIHEiwoMGDCBMqXMiwocOHECNKnEixosWLGDNq3Mixo8ePIEOKHEmypMmTKFOqXMmypcuXMGPKnEmzps2bOHPq3Mmzp8+fQIMKHUq0qNGjSJMqXcq0qdOnUKNKnUq1qtWr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)
 
在这个模型中，每一个线程都可以被看作为运行在不同的CPU上，然而即使是在多处理器上，这种情况也是很罕见的。但是实际上，通过模型所具备的某些特性，这种CPU和线程单一映射能够通过一些合理的方法去实现。例如，因为CPU的寄存器不能被另一个CPU直接访问，这种模型必须考虑到某个线程无法得知被另一个线程操作变量的值的情况。这种情况不仅仅存在于多处理器环境上，在单核CPU环境里，因为编译器和处理器的不可预测的行为也可能导致同样的情况。

Java内存模型没有具体讲述前面讨论的执行策略是由编译器，CPU，缓存控制器还是其它机制促成的。甚至没有用开发人员所熟悉的类，对象及方法来讨论。取而代之，Java内存模型中仅仅定义了线程和内存之间那种抽象的关系。众所周知，每个线程都拥有自己的工作存储单元（缓存和寄存器的抽象）来存储线程当前使用的变量的值。Java内存模型仅仅保证了代码指令与变量操作的有序性，大多数规则都只是指出什么时候变量值应该在内存和线程工作内存之间传输。这些规则主要是为了解决如下三个相互牵连的问题：

### 原子性：
哪些指令必须是不可分割的。在Java内存模型中，这些规则需声明仅适用于-—实例变量和静态变量，也包括数组元素，但不包括方法中的局部变量-—的内存单元的简单读写操作。

### 可见性：
在哪些情况下，一个线程执行的结果对另一个线程是可见的。这里需要关心的结果有，写入的字段以及读取这个字段所看到的值。
### 有序性：
在什么情况下，某个线程的操作结果对其它线程来看是无序的。最主要的乱序执行问题主要表现在读写操作和赋值语句的相互执行顺序上。

当正确的使用了同步，上面属性都会具有一个简单的特性：一个同步方法或者代码块中所做的修改对于使用了同一个锁的同步方法或代码块都具有原子性和可见性。同步方法或代码块之间的执行过程都会和代码指定的执行顺序保持一致。即使代码块内部指令也许是乱序执行的，也不会对使用了同步的其它线程造成任何影响。

当没有使用同步或者使用的不一致的时候，情况就会变得复杂。Java内存模型所提供的保障要比大多数开发人员所期望的弱，也远不及目前业界所实现的任意一款Java虚拟机。这样，开发人员就必须负起额外的义务去保证对象的一致性关系：对象间若有能被多个线程看到的某种恒定关系，所有依赖这种关系的线程就必须一直维持这种关系，而不仅仅由执行状态修改的线程来维持。

### Atomicity:
除了long型字段和double型字段外，java内存模型确保访问任意类型字段所对应的内存单元都是原子的。这包括引用其它对象的引用类型的字段。此外，volatile long 和volatile double也具有原子性 。（虽然java内存模型不保证non-volatile long 和 non-volatile double的原子性，当然它们在某些场合也具有原子性。）（译注：non-volatile long在64位JVM，OS，CPU下具有原子性）

当在一个表达式中使用一个non-long或者non-double型字段时，原子性可以确保你将获得这个字段的初始值或者某个线程对这个字段写入之后的值；但不会是两个或更多线程在同一时间对这个字段写入之后产生混乱的结果值（即原子性可以确保，获取到的结果值所对应的所有bit位，全部都是由单个线程写入的）。但是，如下面（译注：指可见性章节）将要看到的，原子性不能确保你获得的是任意线程写入之后的最新值。 因此，原子性保证通常对并发程序设计的影响很小。

### Visibility
只有在下列情况时，一个线程对字段的修改才能确保对另一个线程可见：

+ 一个写线程释放一个锁之后，另一个读线程随后获取了同一个锁。本质上，线程释放锁时会将强制刷新工作内存中的脏数据到主内存中，获取一个锁将强制线程装载（或重新装载）字段的值。锁提供对一个同步方法或块的互斥性执行，线程执行获取锁和释放锁时，所有对字段的访问的内存效果都是已定义的。

+ 注意同步的双重含义：锁提供高级同步协议，同时在线程执行同步方法或块时，内存系统（有时通过内存屏障指令）保证值的一致性。这说明，与顺序程序设计相比较，并发程序设计与分布式程序设计更加类似。同步的第二个特性可以视为一种机制：一个线程在运行已同步方法时，它将发送和/或接收其他线程在同步方法中对变量所做的修改。从这一点来说，使用锁和发送消息仅仅是语法不同而已。


+ 如果把一个字段声明为volatile型，线程对这个字段写入后，在执行后续的内存访问之前，线程必须刷新这个字段且让这个字段对其他线程可见（即该字段立即刷新）。每次对volatile字段的读访问，都要重新装载字段的值。

+ 一个线程首次访问一个对象的字段，它将读到这个字段的初始值或被某个线程写入后的值。
此外，把还未构造完成的对象的引用暴露给某个线程，这是一个错误的做法 (see 2.1.2)。在构造函数内部开始一个新线程也是危险的，特别是这个类可能被子类化时。Thread.start有如下的内存效果：调用start方法的线程释放了锁，随后开始执行的新线程获取了这个锁。如果在子类构造函数执行之前，可运行的超类调用了new Thread(this).start()，当run方法执行时，对象很可能还没有完全初始化。同样，如果你创建且开始一个新线程T，这个线程使用了在执行start之后才创建的一个对象X。你不能确信X的字段值将能对线程T可见。除非你把所有用到X的引用的方法都同步。如果可行的话，你可以在开始T线程之前创建X。

+ 线程终止时，所有写过的变量值都要刷新到主内存中。比如，一个线程使用Thread.join来终止另一个线程，那么第一个线程肯定能看到第二个线程对变量值得修改。(see §4.3.2)

注意，在同一个线程的不同方法之间传递对象的引用，永远也不会出现内存可见性问题。
内存模型确保上述操作最终会发生，一个线程对一个特定字段的特定更新，最终将会对其他线程可见，但这个“最终”可能是很长一段时间。线程之间没有同步时，很难保证对字段的值能在多线程之间保持一致（指写线程对字段的写入立即能对读线程可见）。特别是，如果字段不是volatile或没有通过同步来访问这个字段，在一个循环中等待其他线程对这个字段的写入，这种情况总是错误的(see 3.2.6)。

在缺乏同步的情况下，模型还允许不一致的可见性。比如，得到一个对象的一个字段的最新值，同时得到这个对象的其他字段的过期的值。同样，可能读到一个引用变量的最新值，但读取到这个引用变量引用的对象的字段的过期值。

不管怎样，线程之间的可见性并不总是失效（指线程即使没有使用同步，仍然有可能读取到字段的最新值），内存模型仅仅是允许这种失效发生而已。因此，即使多个线程之间没有使用同步，也不保证一定会发生内存可见性问题（指线程读取到过期的值），java内存模型仅仅是允许内存可见性问题发生而已。在很多当前的JVM实现和java执行平台中，甚至是在那些使用多处理器的JVM和平台中，也很少出现内存可见性问题。共享同一个CPU的多个线程使用公共的缓存，缺少强大的编译器优化，以及存在强缓存一致性的硬件，这些都会使线程更新后的值能够立即在多线程之间传递。这使得测试基于内存可见性的错误是不切实际的，因为这样的错误极难发生。或者这种错误仅仅在某个你没有使用过的平台上发生，或仅在未来的某个平台上发生。这些类似的解释对于多线程之间的内存可见性问题来说非常普遍。没有同步的并发程序会出现很多问题，包括内存一致性问题。

### Ordering

有序性规则表现在以下两种场景: 线程内和线程间

 从某个线程的角度看方法的执行，指令会按照一种叫“串行”（as-if-serial）的方式执行，此种方式已经应用于顺序编程语言。
 这个线程“观察”到其他线程并发地执行非同步的代码时，任何代码都有可能交叉执行。唯一起作用的约束是：对于同步方法，同步块以及volatile字段的操作仍维持相对有序。
 

再次提醒，这些仅是最小特性的规则。具体到任何一个程序或平台上，可能存在更严格的有序性规则。所以你不能依赖它们，因为即使你的代码遵循了这些更严格的规则，仍可能在不同特性的JVM上运行失败，而且测试非常困难。

需要注意的是，线程内部的观察视角被JLS [1] 中其他的语义的讨论所采用。例如，算术表达式的计算在线程内看来是从左到右地执行操作（JLS 15.6章节），而这种执行效果是没有必要被其他线程观察到的。

仅当某一时刻只有一个线程操作变量时，线程内的执行表现为串行。出现上述情景，可能是因为使用了同步，互斥体[2] 或者纯属巧合。当多线程同时运行在非同步的代码里进行公用字段的读写时，会形成一种执行模式。在这种模式下，代码会任意交叉执行，原子性和可见性会失效，以及产生竞态条件。这时线程执行不再表现为串行。

尽管JLS列出了一些特定的合法和非法的重排序，如果碰到所列范围之外的问题，会降低以下这条实践保证 ：运行结果反映了几乎所有的重排序产生的代码交叉执行的情况。所以，没必要去探究这些代码的有序性。

译注：

【1】JLS：Java Language Specification ，Java语言规范

【2】互斥体：原文为structural exclusion，译者认为意同 mutual exclusion ，详见 互斥体。

### Volatile

从原子性，可见性和有序性的角度分析，声明为volatile字段的作用相当于一个类通过get/set同步方法保护普通字段，如下：

```java
final class VFloat {
    private float value;

    final synchronized void set(float f) { value = f; }
    final synchronized float get()       { return value; }
}
```

与使用synchronized相比，声明一个volatile字段的区别在于没有涉及到锁操作。但特别的是对volatile字段进行“++”这样的读写操作不会被当做原子操作执行。

另外，有序性和可见性仅对volatile字段进行一次读取或更新操作起作用。声明一个引用变量为volatile，不能保证通过该引用变量访问到的非volatile变量的可见性。同理，声明一个数组变量为volatile不能确保数组内元素的可见性。volatile的特性不能在数组内传递，因为数组里的元素不能被声明为volatile。


由于没有涉及到锁操作，声明volatile字段很可能比使用同步的开销更低，至少不会更高。但如果在方法内频繁访问volatile字段，很可能导致更低的性能，这时还不如锁住整个方法。

如果你不需要锁，把字段声明为volatile是不错的选择，但仍需要确保多线程对该字段的正确访问。可以使用volatile的情况包括：

+ 该字段不遵循其他字段的不变式。
+ 对字段的写操作不依赖于当前值。
+ 没有线程违反预期的语义写入非法值。
+ 读取操作不依赖于其它非volatile字段的值。

当只有一个线程可以修改字段的值，其它线程可以随时读取，那么把字段声明为volatile是合理的。例如，一个名叫Thermometer(中文：体温计)的类，可以声明temperature字段为volatile。正如在3.4.2节所讨论，一个volatile字段很适合作为完成某些工作的标志。另一个例子在4.4节有描述，通过使用轻量级的执行框架使某些同步工作自动化，但是仍需把结果字段声明为volatile，使其对各个任务都是可见的。


