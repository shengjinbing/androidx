1.生产者消费者模式（手撕代码）
2.wait 和 sleep的区别
3.String、StringBuffer 、StringBuilder的区别
4.String a = "abc" 和 String str = new String(“abc”) 的区别
5.谈谈四种引用
6.HashSet和HashMap的关系
7.HashMap与HashTable的区别
8.ConcurrentHashMap知道吗，ConcurrentHashMap在jdk1.8之后的优化
9.重写和重载的区别
  一、定义上的区别：
  1、重载是指不bai同的函数使用相同的函数名，但是函数的参数个du数或类型不同。调用的时候根据函数的参数来区别不同的函数。
  2、覆盖（也叫重写）是指在派生类中重新对基类中的虚函数（注意是虚函数，private,final等关键词不能被重写的）重新实现。即函数名和参数都一样，
     只是函数的实现体不一样。
  二、规则上的不同：
  1、重载的规则：
  ①必须具有不同的参数列表。
  ②可以有不同的访问修饰符。
  ③可以抛出不同的异常。
10.谈谈Activity的四种启动模式，SingleTop和SingleTask启动模式的应用场景
11.图片的三级缓存是怎么做的？
12.Service的两种启动方式以及有什么区别
13.如何在Activity和Service进行通信
14.谈谈本地广播
15.binder机制
16.Handler消息机制，子线程可以创建handler吗
   不可以，会抛出异常（Can't create handler inside thread that has not called Looper.prepare()）
   创建Handler之前必须先Looper.prepare()来创建Looper，如果想让handler能收到消息要Looper.loop();
17.View的绘制流程，MeasureSpec(MS)知道吗？如何确定一个View的MS？那DecorView呢？
18.OkHttp的源码讲解，它涉及哪些设计模式
19.为什么TCP要三次握手，两次不行吗？
20.TCP与UDP的区别，OSI五层结构
21.死锁的四个条件
22.操作系统中页面置换算法
23.线程的所有状态
24.synchronized关键字（选择题，问题关键在于synchronized锁住的是哪个对象，针对普通同步方法和静态同步方法）

二：
1.数据库的范式
   1、第一范式 属性的原子性
   所谓的第一范式就是数据库中的每一列都是不可分割的基本数据项，同一列中不能有多个值，即实体中的某个属性不能有
   多个值或者不能有重复的属性，如果出现重复的属性则需要重新构建实体，新的实体由重复的属性构成。
   第二范式 属性完全依赖于主键
   2、第二范式是在第一范式的基础上建立起来的，即满足第二范式必须先满足第一范式，第二范式要求数据库的每个实例或行
   必须可以被唯一的区分，即表中要有一列属性可以将实体完全区分，这个属性就是主键，即每一个属性完全依赖于主键，
   在员工管理中，员工可以通过员工编号进行唯一区分,
   完全依赖概念：即非主属性不能依赖于主键的部分属性，必须依赖于主键的所有属性
   3、第三范式
   满足第三范式必须先满足第二范式，第三范式要求一个数据库表中不包含已在其他表中已包含的非主关键字信息， 例如 
   存在一个课程表，课程表中有课程号(Cno),课程名(Cname),学分(Ccredit)，那么在学生信息表中就没必要再把课
   程名，学分再存储到学生表中，这样会造成数据的冗余， 第三范式就是属性不依赖与其他非主属性，也就是说，如果存
   在非主属性对于码的传递函数依赖，则不符合第三范式
   https://blog.csdn.net/qq_43079376/article/details/93647335
2.数据库中事务的特性
  1、原子性(Atomicity)：事务中的全部操作在数据库中是不可分割的，要么全部完成，要么全部不执行。 
  2、一致性(Consistency)：几个并行执行的事务，其执行结果必须与按某一顺序 串行执行的结果相一致。 
  3、隔离性(Isolation)：事务的执行不受其他事务的干扰，事务执行的中间结果对其他事务必须是透明的。
  4、持久性(Durability):对于任意已提交事务，系统必须保证该事务对数据库的改变不被丢失，即使数据库出现故障。
  事务的ACID特性是由关系数据库系统(DBMS)来实现的，DBMS采用日志来保证事务的原子性、一致性和持久性。日志记录了
  事务对数据库所作的更新，如果某个事务在执行过程中发生错误，就可以根据日志撤销事务对数据库已做的更新，使得数据库
  回滚到执行事务前的初始状态。对于事务的隔离性，DBMS是采用锁机制来实现的。当多个事务同时更新数据库中相同的数据
  时，只允许持有锁的事务能更新该数据，其他事务必须等待，直到前一个事务释放了锁，其他事务才有机会更新该数据。

3.用过的设计模式或者是安卓中遇到的设计模式
4.责任链模式有哪些优势
5.实现一个单例模式（写的是DCL）
6.DCL为什么要两次判断null呢？为什么使用volatile？
7.在浏览器输入一个网址到网页显示出来有哪些经过
8.DNS如何解析域名
9.HTTPS与HTTP的区别，HTTPS为什么安全
10.GC过程（判断对象是否存活，GC算法）
11.知道哪些数据结构，红黑树的特点
12.做过的安卓性能优化
13.内存泄露检测工具leakcanary的原理
14.RN开发和原生开发的区别
15.线程池的参数和工作流程
16.IntentService能用bind方式启动吗？IntentService的原理
17.MVP MVVM架构
18.算法题：两个队列实现一个栈
19.HashMap的hash算法和扩容机制的原因


3.理解 Java 的字符串，String、StringBuffer、StringBuilder 有什么区别？
  String 是 Java 语言非常基础和重要的类，提供了构造和管理字符串的各种基本逻辑。它是典型的 Immutable 类，
  被声明成为 final class，所有属性也都是 final 的。也由于它的不可变性，类似拼接、裁剪字符串等动作，都会
  产生新的 String 对象。由于字符串操作的普遍性，所以相关操作的效率往往对应用性能有明显影响
  
  StringBuffer 是为解决上面提到拼接产生太多中间对象的问题而提供的一个类，我们可以用 append 或者 add 方法，
  把字符串添加到已有序列的末尾或者指定位置。StringBuffer 本质是一个线程安全的可修改字符序列，它保证了线程安全
  ，也随之带来了额外的性能开销，所以除非有线程安全的需要，不然还是推荐使用它的后继者，也就是 StringBuilder。
  
  StringBuilder 是 Java 1.5 中新增的，在能力上和 StringBuffer 没有本质区别，但是它去掉了线程安全的部分，
  有效减小了开销，是绝大部分情况下进行字符串拼接的首选。
  
4.String a = "abc" 和 String str = new String(“abc”) 的区别

5.谈谈四种引用

22.操作系统中页面置换算法
   最佳置换算法OPT（根据未来使用情况将未来的近期里不用的页替换出去。）
   先进先出算法FIFO
   近期最久未用过算法LRU
   CLOCK置换算法NRU
   
   页面缓冲算法PBA
   近期最少使用算法LFU
   
   
19.为什么TCP要三次握手，两次不行吗？
  https://blog.csdn.net/lengxiao1993/article/details/82771768
  https://juejin.cn/post/6844904158655414286#heading-27
  1.什么叫连接？
  2.TCP连接的建立和关闭
  3.长连接，什么叫长连接？长连接的实现方式：心跳（推送）

20.TCP与UDP的区别，OSI五层结构
https://www.cnblogs.com/wxd0108/p/7597216.html
一、OSI七层模型
  OSI七层协议模型主要是：应用层（Application）、表示层（Presentation）、会话层（Session）、传输层（Transport）、
  网络层（Network）、数据链路层（Data Link）、物理层（Physical）。
二、TCP/IP四层模型
  TCP/IP是一个四层的体系结构，主要包括：应用层、传输层、网络层和数据链路层。（因为网络不稳定，所以要分层传输）
三、五层体系结构
  五层体系结构包括：应用层、传输层、网络层、数据链路层和物理层。
23.线程的所有状态


