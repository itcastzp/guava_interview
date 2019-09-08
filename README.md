原文出处：http://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html
# JSR 133（Java内存模型）常见问题解答Jeremy Manson和Brian Goetz，2004年2月
## 目录
##### [什么是内存模型？](#1)
##### [像C这样的其他语言是否有内存模型？](#2)
##### [什么是JSR 133？](#3)
##### [重新排序是什么意思？](#4)
##### [旧的记忆模型出了什么问题？](#5) 
##### [错误同步是什么意思？](#6)
##### [同步有什么作用？](#7)
##### [Final字段如何显示更改其值？](#8)
##### [最终字段如何在新的JMM下工作？](#9) 
##### [Volatile有什么作用？](#10)
##### [新内存模型是否修复了“双重检查锁定”问题？](#11)
##### [如果我正在编写VM怎么办？ 我为什么要在乎？](#12) 

#### <span id="1">1. 什么是内存模型?</span>

>>在多处理器系统中，处理器通常具有一层或多层内存高速缓存，这通过加快对数据的访问（因为数据更接近处理器）和
减少共享内存总线上的流量来提高性能（因为可以满足许多内存操作）通过本地缓存。内存缓存可以极大地提高性能，
但它们带来了许多新的挑战。例如，当两个处理器同时检查相同的内存位置时会发生什么？在什么条件下他们会看到相同的值?

>>在处理器级别，存储器模型定义了必要且充分的条件，用于知道当前处理器对其他处理器的存储器写入是可见的，并且当前处理器的写入对于其他处理器是可见的。某些处理器表现出强大的内存模型，其中所有处理器始终看到任何给定内存位置的完全相同的值。其他处理器表现出较弱的内存模型，其中需要特殊指令（称为内存屏障）来刷新或使本地处理器高速缓存无效，以便查看其他处理器进行的写入或使该处理器的写入对其他处理器可见。这些内存屏障通常在执行锁定和解锁操作时执行;对于高级语言的程序员来说，它们是不可见的。

>>由于减少了对内存屏障的需求，有时可以更容易地为强内存模型编写程序。然而，即使在一些最强大的内存模型上，通常也需要内存屏障;很多时候他们的位置是违反直觉的。处理器设计的最新趋势鼓励了较弱的内存模型，因为它们为缓存一致性所做的放松允许跨多个处理器和更大内存量的更大可扩展性。

>>编译器重新排序代码时，写入对另一个线程可见的问题更加复杂。例如，编译器可能会决定稍后在程序中移动写操作更有效;只要此代码动作不会改变程序的语义，就可以自由地执行此操作。如果编译器推迟操作，则另一个线程在执行之前不会看到它;这反映了缓存的效果。

>>此外，可以在程序中更早地移动对存储器的写入;在这种情况下，其他线程可能会在程序实际“发生”之前看到写入。所有这些灵活性都是通过设计 - 通过为编译器，运行时或硬件提供以最佳顺序执行操作的灵活性，在内存模型的范围内，我们可以实现更高的性能。

以下代码中可以看到一个简单的示例：

```java
class Reordering {
	  int x = 0, y = 0;
	  public void writer() {
		x = 1;
		y = 2;
	  }

	  public void reader() {
		int r1 = y;
		int r2 = x;
	  }
	}
```
>>假设这个代码同时在两个线程中执行，并且y的读取看到值2.因为这个写入在写入x之后，程序员可能会认为x的读取必须看到值1.但是，写入可能已被重新排序。如果发生这种情况，则可能会发生对y的写入，可能会读取两个变量，然后可能会发生对x的写入。结果是r1的值为2，但r2的值为0
 
>>Java内存模型描述了多线程代码中哪些行为是合法的，以及线程如何通过内存进行交互。它描述了程序中变量之间的关系，以及在真实计算机系统中存储和检索它们与存储器或寄存器之间的低级细节。它以一种可以使用各种硬件和各种编译器优化正确实现的方式实现。

>>Java包括几种语言结构，包括volatile，final和synchronized，它们旨在帮助程序员描述程序对编译器的并发性要求。 Java内存模型定义了volatile和synchronized的行为，更重要的是，确保正确同步的Java程序在所有处理器体系结构上正确运行。

------------------------

#### <span id="2">2. 像C这样的其他语言是否有内存模型？</span>
>>大多数其他编程语言（如C和C）的设计并未直接支持多线程。这些语言针对编译器和体系结构中发生的各种重新排序提供的保护很大程度上取决于所使用的线程库（例如pthread），所使用的编译器以及运行代码的平台所提供的保证。

------------------

#### <span id="3">3. 什么是JSR 133？</span>
>>自1997年以来，在Java语言规范的第17章中定义的Java内存模型中发现了几个严重的缺陷。这些缺陷允许混淆行为（例如观察到最终字段改变其值）并破坏编译器执行常见优化的能力。

>>Java内存模型是一项雄心勃勃的事业;这是编程语言规范第一次尝试合并一个内存模型，该内存模型可以为各种体系结构的并发提供一致的语义。不幸的是，定义一致且直观的内存模型证明比预期困难得多。 JSR 133为Java语言定义了一个新的内存模型，它修复了早期内存模型的缺陷。为了做到这一点，需要改变final和volatile的语义。

>>完整的语义可以在http://www.cs.umd.edu/users/pugh/java/memoryModel
上找到，但正式的语义不是胆小的。令人惊讶和发人深省的是，发现像同步一样复杂的看似简单的概念。幸运的是，您无需了解形式语义的细节 - JSR 133的目标是创建一组形式语义，为易变，同步和最终工作提供直观的框架。

	JSR 133的目标包括: 
>>保持现有的安全保障，如类型安全，并加强其他安全保障。例如，变量值可能不会“凭空”创建：某			 个线程观察到的变量的每个值必须是某个线程可以合理放置的值。

>>正确同步程序的语义应尽可能简单直观。

>>应定义不完全或不正确同步程序的语义，以便最大限度地减少潜在的安全隐患。

>>程序员应该能够自信地理解多线程程序如何与内存交互。

>>应该可以在广泛的流行硬件架构中设计正确的，高性能的JVM实现。

>>应提供初始化安全性的新保证。如果一个对象被正确构造（这意味着对它的引用在构造期间不会被转义），那么看到对该对象的引用的所有线程也将看到在构造函数中设置的最终字段的值，而不需要同步。
对现有代码的影响应该是最小的。

---------------------------------

#### <span id="4">4. 重新排序是什么意思？</span>
>><kbd>在许多情况下</kbd>，对程序变量（对象实例字段，类静态字段和数组元素）的访问可能看起来以与程序指定的顺序不同的顺序执行。编译器可以自由地使用优化名称中的指令顺序。处理器可能在某些情况下不按顺序执行指令。可以以不同于程序指定的顺序在寄存器，处理器高速缓存和主存储器之间移动数据。

>>例如，如果一个线程写入字段a然后写入字段b，并且b的值不依赖于a的值，则编译器可以自由地重新排序这些操作，并且缓存可以自由地将b刷新到main记忆之前的。有许多潜在的重新排序源，例如编译器，JIT和缓存。

>>编译器，运行时和硬件应该合谋创建as-if-serial语义的假象，这意味着在单线程程序中，程序不应该能够观察重新排序的影响。但是，重新排序可能会在错误同步的多线程程序中发挥作用，其中一个线程能够观察其他线程的影响，并且可能能够检测到变量访问对于其他线程可见，其顺序与执行或指定的顺序不同程序

>>大多数时候，一个线程并不关心对方在做什么。但是当它发生时，这就是同步的目的。

-----------------------

#### <span id="5">5. 旧的记忆模型出了什么问题？</span>
>> 旧内存模型存在几个严重问题。它很难理解，因此被广泛侵犯。例如，在许多情况下，旧模型不允许在每个JVM中进行各种重新排序。关于旧模型含义的混淆是迫使JSR-133形成的原因

>> 例如，一个广泛持有的信念是，如果使用最终字段，则线程之间的同步是不必要的，以保证另一个线程将看到该字段的值。虽然这是一个合理的假设和合理的行为，实际上我们希望事情如何运作，但在旧的记忆模型下，却根本不是真的。旧内存模型中的任何内容都没有处理最终字段与任何其他字段不同 - 这意味着同步是确保所有线程都能看到构造函数写入的最终字段值的唯一方法。因此，线程可以看到该字段的默认值，然后稍后查看其构造值。这意味着，例如，像String这样的不可变对象可能会改变它们的价值 - 这确实是一个令人不安的前景。

>>旧的内存模型允许使用非易失性读写重新排序易失性写入，这与大多数开发人员对volatile的直觉不一致，因此引起混淆

>>最后，正如我们将要看到的，程序员对程序错误同步时可能发生的事情的直觉常常是错误的。 JSR-133的目标之一是引起人们对这一事实的关注。

#### <span id="6">6. 错误的同步</span>
>>错误地同步代码对不同的人来说意味着不同的东西。当我们在Java内存模型的上下文中讨论错误同步的代码时，我们指的是任何代码，其中一个线程写入一个变量，另一个线程读取相同的变量，而写入和读取不是通过同步排序当违反这些规则时，我们说我们对该变量进行了数据竞争。具有数据争用的程序是错误同步的程序。

#### <span id="7">7. Synchronization做了什么？</span>
>>同步有几个方面。最常见的是互斥 - 只有一个线程可以同时保存一个监视器，因此在监视器上进行同步意味着一旦一个线程进入受监视器保护的同步块，其他线程就无法进入受该监视器保护的块直到第一个线程退出同步块

>>但是，除了相互排斥之外，还有更多的同步。同步确保线程在同步块之前或期间的内存写入以可预测的方式显示给在同一监视器上同步的其他线程。在我们退出synchronized块之后，我们释放了监视器，它具有将缓存刷新到主内存的效果，因此该线程所做的写操作对其他线程是可见的。在我们进入同步块之前，我们获取监视器，它具有使本地处理器高速缓存无效的效果，以便从主存储器重新加载变量。然后，我们将能够看到前一版本中显示的所有写入。

>>在缓存方面讨论这个问题，听起来好像这些问题只影响多处理器机器。但是，可以在单个处理器上轻松看到重新排序效果。例如，编译器无法在获取之前或发布之后移动代码。当我们说获取和释放对缓存起作用时，我们使用速记来表示许多可能的影响。

>>新的内存模型语义在内存操作（读取字段，写入字段，锁定，解锁）和其他线程操作（启动和加入）上创建部分排序，其中一些操作据说在其他操作之前发生。当一个动作发生在另一个动作之前时，第一个动作保证在第二个动作之前被命令并且可见。这种排序的规则如下：

>>+ 线程中的每个操作都发生在该线程中的每个操作之前，该操作在程序的顺序中稍后出现。 
+ 监视器上的解锁发生在同一监视器上的每个后续锁定之前。 
+ 对易失性字段的写入在每次后续读取相同的易失性之前发生。 
+ 在线程上调用start（）发生在启动线程中的任何操作之前。 
+ 线程中的所有操作都发生在任何其他线程从该线程上的join（）成功返回之前。 这意味着在退出同步块之前对线程可见的任何内存操作在进入由同一监视器保护的同步块之后对任何线程都是可见的，因为所有内存操作都在发布之前发生，并且释放发生在获得。

>>另一个含义是，一些人用来强制内存屏障的以下模式不起作用：
```
synchronized (new Object()) {
}
```

>>这实际上是一个无操作，并且您的编译器可以完全删除它，因为编译器知道没有其他线程将在同一监视器上同步。您必须为一个线程设置一个before-before关系，以查看另一个线程的结果。

>>重要说明：请注意，两个线程在同一监视器上同步以便正确设置before-before关系非常重要。情况并非如此，在对象X上同步时，线程A可见的所有内容在对象Y上同步后变为对线程B可见。释放和获取必须“匹配”（即，在同一监视器上执行）以使正确的语义。否则，代码会有数据竞争。

--------------
#### <span id="8">8.最终字段如何显示更改其值</span>
>>可以看到最终字段值的变化的最佳示例之一涉及String类的一个特定实现。 String可以实现为具有三个字段的对象 - 字符数组，该数组的偏移量和长度。以这种方式实现String的基本原理是，它允许多个String和StringBuffer对象共享相同的字符数组，并避免额外的对象分配和复制，而不是只有字符数组。因此，例如，方法String.substring（）可以通过创建一个新的字符串来实现，该字符串与原始字符串共享相同的字符数组，并且只是长度和偏移字段不同。对于String，这些字段都是最终字段。
```
String s1 = "/usr/tmp";
String s2 = s1.substring(4); 
```
>>字符串s2的偏移量为4，长度为4.但是，在旧模型下，另一个线程可能会将偏移量视为默认值为0，然后再看到正确的值4，它看起来好像字符串“/ usr”变为“/ tmp”

>>最初的Java内存模型允许这种行为;几个JVM已经表现出这种行为。新的Java内存模型使这非法

#### <span id="9">9.最终字段如何在新的JMM下工作?</span> 

对象的最终字段的值在其构造函数中设置。假设对象是“正确”构造的，一旦构造了一个对象，分配给构造函数中最终字段的值对于所有其他线程都是可见的，而不进行同步。此外，这些最终字段引用的任何其他对象或数组的可见值将至少与最终字段一样是最新的。

>>对象的正确构造意味着什么？它只是意味着在构造过程中不允许对正在构造的对象的引用“逃逸”。 （有关示例，请参阅安全构造技术。）换句话说，不要在另一个线程可能看到它的任何地方放置对正在构造的对象的引用;不要将它分配给静态字段，不要将其注册为任何其他对象的侦听器，依此类推。这些任务应在构造函数完成后完成，而不是在构造函数中完成。

```java
class FinalFieldExample {
  final int x;
  int y;
  static FinalFieldExample f;
  public FinalFieldExample() {
    x = 3;
    y = 4;
  }

  static void writer() {
    f = new FinalFieldExample();
  }

  static void reader() {
    if (f != null) {
      int i = f.x;
      int j = f.y;
    }
  }
}
```

>>**上面的类是如何使用最终字段的示例。线程执行读取器保证看到f.x的值3，因为它是最终的。不能保证看到y的值为4，因为它不是最终的。如果FinalFieldExample的构造函数如下所示：**


```java
public FinalFieldExample() { // bad!
  x = 3;
  y = 4;
  // bad construction - allowing this to escape
  global.obj = this;
}
```
>>+ 然后从global.obj读取对此的引用的线程不保证看到3表示x。

>>能够看到字段的正确构造值是很好的，但如果字段本身是引用，那么您还希望代码查看它指向的对象（或数组）的最新值。如果您的字段是最终字段，则也可以保证。因此，您可以拥有一个指向数组的最终指针，而不必担心其他线程看到数组引用的正确值，但是数组内容的值不正确。同样，在这里“正确”，我们的意思是“对象的构造函数结束时的最新”，而不是“最新的可用值”。

>>现在，说完所有这些，如果在一个线程构造一个不可变对象（即一个只包含最终字段的对象）之后，你想确保所有其他线程都能正确看到它，你通常还需要使用同步。例如，没有其他方法可以确保第二个线程可以看到对不可变对象的引用。程序从最终字段获得的保证应该仔细调整，仔细了解如何在代码中管理并发。

>>如果要使用JNI更改最终字段，则没有已定义的行为。

#### <span id="10">10. Volatile的作用</span>
>>易失性字段是用于在线程之间传递状态的特殊字段。每次读取volatile都会看到任何线程对该volatile的最后一次写入;实际上，它们被程序员指定为字段，由于缓存或重新排序而无法接受看到“陈旧”值的字段。禁止编译器和运行时将它们分配到寄存器中。他们还必须确保在写入之后，将它们从缓存中刷新到主内存，这样它们就可以立即对其他线程可见。类似地，在读取volatile字段之前，必须使高速缓存无效，以便主存储器中的值（而不是本地处理器高速缓存）是所见的值。重新排序对volatile变量的访问还有其他限制。

>>在旧的内存模型下，对volatile变量的访问不能相互重新排序，但可以使用非易失性变量访问重新排序。这破坏了易失性字段作为从一个线程到另一个线程发信号通知的手段的有用性。

>>在新的内存模型下，仍然可以确定volatile变量不能相互重新排序。不同之处在于现在不再那么容易重新排序它们周围的正常字段访问。写入易失性字段与监视器释放具有相同的记忆效应，从易失性字段读取具有与监视器获取相同的记忆效应。实际上，因为新的存储器模型对具有其他字段访问（易失性或非易失性）的易失性字段访问的重新排序施加了更严格的约束，所以当线程A写入易失性字段f时线程A可见的任何内容在读取f时对线程B可见。

>>这是一个如何使用volatile字段的简单示例:

```java
class VolatileExample {
  int x = 0;
  volatile boolean v = false;
  public void writer() {
    x = 42;
    v = true;
  }

  public void reader() {
    if (v == true) {
      //uses x - guaranteed to see 42.
    }
  }
}
```

>>假设一个线程正在调用writer，另一个线程正在调用reader。写入v中的写入将x的写入释放到内存，而v的读取从内存中获取该值。因此，如果读者看到v的值为true，那么也可以保证看到在它之前发生的写入42。在旧的内存模型下，这不可能是真的。如果v不是volatile，那么编译器可以重新排序writer中的写入，读者对x的读取可能会看到0。

>>实际上，易失性的语义已经大大加强，几乎达到了同步的水平。出于可见性的目的，每次读取或写入易失性字段的行为类似于“半”同步。

>>重要说明：请注意，两个线程都必须访问相同的volatile变量才能正确设置before-before关系。情况并非如此，线程A在写入易失性字段f时可见的所有内容在读取易失性字段g后变为线程B可见。释放和获取必须“匹配”（即，在相同的易失性字段上执行）以具有正确的语义。


#### <span id="11">11. 新内存模型是否修复了“双重检查锁定”问题？ </span>
>>（臭名昭着的）双重检查锁定习惯用法（也称为多线程单例模式）是一种旨在支持延迟初始化同时避免同步开销的技巧。在早期的JVM中，同步很慢，开发人员急于将其删除 - 可能过于急切。双重检查的锁定习语如下所示：

```java
// double-checked-locking - don't do this!

private static Something instance = null;

public Something getInstance() {
  if (instance == null) {
    synchronized (this) {
      if (instance == null)
        instance = new Something();
    }
  }
  return instance;
}
```

>>这看起来非常聪明 - 在公共代码路径上避免了同步。它只有一个问题 - 它不起作用。为什么不？最明显的原因是初始化实例和写入实例字段的写入可以由编译器或高速缓存重新排序，这将产生返回看似部分构造的Something的效果。结果是我们读取了一个未初始化的对象。还有很多其他原因导致错误，以及为什么对它进行算法修正是错误的。使用旧的Java内存模型无法修复它。更多深入的信息可以在双重检查锁定中找到：聪明，但破损和“双重检查锁定已损坏”声明

>>许多人认为使用volatile关键字可以消除尝试使用双重检查锁定模式时出现的问题。在1.5之前的JVM中，volatile不能确保它有效（你的里程可能会有所不同）。在新的内存模型下，使实例字段volatile将“修复”双重检查锁定的问题，因为在构造线程的Something初始化和它的值返回之间会有一个先发生的关系。读取它的线程

>>然而，对于双重检查锁定的粉丝（我们真的希望没有留下），新闻仍然不好。双重检查锁定的重点是避免同步的性能开销。自Java 1.0起，不仅简短的同步得到了更低的成本，而且在新的内存模型下，使用volatile的性能成本几乎上升到了同步成本的水平。所以仍然没有充分的理由使用双重检查锁定。编辑 - 在大多数平台上挥发物都很便宜

>>相反，使用Initialization On Demand Holder惯用法，这是一个线程安全的，更容易理解：

```java
private static class LazySomethingHolder {
  public static Something something = new Something();
}

public static Something getInstance() {
  return LazySomethingHolder.something;
}
```

>>由于静态字段的初始化保证，此代码保证是正确的。如果在静态初始化程序中设置了一个字段，则可以保证它可以正确地显示给访问该类的任何线程。 

-------------------------
#### <span id="12">12. 如果我正在编写VM怎么办？ </span>
>>你应该看看http://gee.cs.oswego.edu/dl/jmm/cookbook.html。 我为什么要在乎？ 你为什么要关心？并发错误很难调试。它们通常不会出现在测试中，等待您的程序在高负载下运行，并且难以重现和陷阱。您最好提前花费额外的精力来确保您的程序正确同步;虽然这并不容易，但它比尝试调试严重同步的应用程序要容易得多。









