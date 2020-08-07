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
17.View的绘制流程，MeasureSpec(MS)知道吗？如何确定一个View的MS？那DecorView呢？
18.OkHttp的源码讲解，它涉及哪些设计模式
19.为什么TCP要三次握手，两次不行吗？
20.TCP与UDP的区别，OSI五层结构
21.死锁的四个条件
22.操作系统中页面置换算法
23.线程的所有状态
24.synchronized关键字（选择题，问题关键在于synchronized锁住的是哪个对象，针对普通同步方法和静态同步方法）

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

20.TCP与UDP的区别，OSI五层结构
https://www.cnblogs.com/wxd0108/p/7597216.html
一、OSI七层模型
  OSI七层协议模型主要是：应用层（Application）、表示层（Presentation）、会话层（Session）、传输层（Transport）、网络层（Network）、
数据链路层（Data Link）、物理层（Physical）。
二、TCP/IP四层模型
  TCP/IP是一个四层的体系结构，主要包括：应用层、运输层、网际层和网络接口层。从实质上讲，只有上边三层，网络接口层没有什么具体的内容。
三、五层体系结构
  五层体系结构包括：应用层、运输层、网络层、数据链路层和物理层。

23.线程的所有状态