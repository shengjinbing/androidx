字节跳动
一面
1.java泛型，反射
2.进程间通信的方式，安卓中有哪些方式，为什么是基于Binder的，不用传统的操作系统进程间通信方式呢
3.一个app可以开启多个进程嘛，怎么做呢，每个进程都是在独立的虚拟机上嘛
4.异步消息处理流程，如果发送一个延时消息，messagequeue里面怎么个顺序，messagequeue是个什么数据结构
5.广播的种类，注册的方式，以及不同注册方式的生命周期。
6.局部广播和全局广播的区别分别用什么实现的。
7.activity和service的通信方式
8.进程和线程的区别
9.并发和并行分别是什么意思，多线程是并发还是并行
  它们虽然都说是"多个进程同时运行"，但是它们的"同时"不是一个概念。并行的"同时"是同一时刻可以多个进程在运行(处于running)，
  并发的"同时"是经过上下文快速切换，使得看上去多个进程同时都在运行的现象，是一种OS欺骗用户的现象。
10.安卓11有什么新的特性。
11.HTTPS过程。
12.DNS解析过程，如果服务器ip地址改变了，客户端怎么知道呢
13.算法： 二叉树的右视图。

二面
1.介绍一下所有的map，以及他们之间的对比，适用场景。
2.一个按钮，手抖了连续点了两次，会跳转两次页面，怎么让这种情况不发生。
3.一个商品页一个商详页，点击商详页的一个关注按钮，希望回- 到商品页也能够显示关注的状态，怎么做
4.项目中定时为什么用AlarmManager，不用postDelayed
5.项目中后台网络请求为什么用service不用线程
   service其实是跑在主线程中的，如果需要大量的后台费时数据处理操作，最好的方式是在service中开子线程，而不是直接开一个子线
   程，这样是为了提高子线程的优先级，而不会轻易被系统杀掉。

也问了一些安卓的新特性。
内部类会有内存泄漏问题吗 内部类为什么能访问外部类的变量，为什么还能访问外部类的私有变量。
算法: 单链表判断有无环。

三面
介绍项目用到了contentprovider,然后问ContentProvider的生命周期，application,activity，service,contentprovider他们的- - - context有什么区别。
内存溢出和内存泄漏，提到了bitmap
然后问下载一个图片的时候直接下载了一个5g的图片，不压缩一定会产生OOM问题，那么怎么去获取这个图片的长宽呢
，或者说这个图片的大小的大小在你没下载之前如何得到。

