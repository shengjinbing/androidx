Android 官方开发者文档中 “What Triggers ANR?” 有介绍 如下：
常见的有如下两种情况会产生 ANR：
输入事件（例如按键或屏幕轻触事件等）在 5 秒内没有响应；
BroadcastReceiver 在 10 秒内没有执行完成。

1、理解Android ANR的信息收集过程
http://gityuan.com/2016/12/02/app-not-response/
2、反思系列：深入探索ANR机制的设计与实现
https://mp.weixin.qq.com/s/Qnser4SMoRtgEPd74oDJGQ
1、彻底理解安卓应用无响应机制 @Gityuan
http://gityuan.com/2019/04/06/android-anr/
2、Input系统—ANR原理分析 @Gityuan
http://gityuan.com/2017/01/01/input-anr/
3、理解Android ANR的触发原理 @Gityuan  
http://gityuan.com/2016/07/02/android-anr/

那么哪些场景会造成ANR呢？
Service Timeout:比如前台服务在20s内未执行完成；
对于前台服务，则超时为SERVICE_TIMEOUT = 20s；
对于后台服务，则超时为SERVICE_BACKGROUND_TIMEOUT = 200s
BroadcastQueue Timeout：比如前台广播在10s内未执行完成
对于前台广播，则超时为BROADCAST_FG_TIMEOUT = 10s；
对于后台广播，则超时为BROADCAST_BG_TIMEOUT = 60s
ContentProvider Timeout：内容提供者,在publish过超时10s;
InputDispatching Timeout: 输入事件分发超时5s，包括按键和触摸事件。

重点总结：system_server 就是AMS具体实现在ActivityService里面，ActivityService是辅助AMS进行Service管理的类、
        包含Service的启动、绑定和停止。
1.埋炸弹 [-> ActiveServices.java]realStartServiceLocked方法中，发送delay消息(SERVICE_TIMEOUT_MSG). 炸弹已埋下,
 我们并不希望炸弹被引爆, 那么就需要在炸弹爆炸之前拆除炸弹.
以Service的启动过程分析：
private final void realStartServiceLocked(ServiceRecord r, ProcessRecord app, boolean execInFg) throws RemoteException {
    ...
    //发送delay消息(SERVICE_TIMEOUT_MSG)，【见小节2.1.2】
    bumpServiceExecutingLocked(r, execInFg, "create");
    try {
        ...
        //最终执行服务的onCreate()方法
        app.thread.scheduleCreateService(r, r.serviceInfo,
                mAm.compatibilityInfoForPackageLocked(r.serviceInfo.applicationInfo),
                app.repProcState);
    } catch (DeadObjectException e) {
        mAm.appDiedLocked(app);
        throw e;
    } finally {
        ...
    }
}

private final void bumpServiceExecutingLocked(ServiceRecord r, boolean fg, String why) {
    ... 
    //埋炸弹是在onCreate()之前埋下的
    scheduleServiceTimeoutLocked(r.app);
}

void scheduleServiceTimeoutLocked(ProcessRecord proc) {
    if (proc.executingServices.size() == 0 || proc.thread == null) {
        return;
    }
    long now = SystemClock.uptimeMillis();
    Message msg = mAm.mHandler.obtainMessage(
            ActivityManagerService.SERVICE_TIMEOUT_MSG);
    msg.obj = proc;
    //当超时后仍没有remove该SERVICE_TIMEOUT_MSG消息，则执行service Timeout流程【见2.3.1】
    mAm.mHandler.sendMessageAtTime(msg,
        proc.execServicesFg ? (now+SERVICE_TIMEOUT) : (now+ SERVICE_BACKGROUND_TIMEOUT));

2.拆炸弹[-> ActivityThread.java]
 该方法的主要工作是当service启动完成，则移除服务超时消息SERVICE_TIMEOUT_MSG。
 private void serviceDoneExecutingLocked(ServiceRecord r, boolean inDestroying, boolean finishing) {
     ...
     if (r.executeNesting <= 0) {
         if (r.app != null) {
             r.app.execServicesFg = false;
             r.app.executingServices.remove(r);
             if (r.app.executingServices.size() == 0) {
                 //当前服务所在进程中没有正在执行的service
                 mAm.mHandler.removeMessages(ActivityManagerService.SERVICE_TIMEOUT_MSG, r.app);
         ...
     }
     ...
 }
3.引爆炸弹
在system_server进程中有一个Handler线程, 名叫”ActivityManager”.当倒计时结束便会向该Handler线程发送 一条信息SERVICE_TIMEOUT_MSG,
                              
ANR机制的设计与实现
1.通常来讲，ANR的来源分为Service、Broadcast、Provider以及Input两种。
2.「组件类ANR发生原因通常是由于 主线程中做了耗时处理」这种说法实际上是笼统的，更准确的讲，其本质的原因是 组件任务调度超时，
   而在设备资源紧凑的情况下，ANR的发生更多是综合性的原因。
3.第一类原理概述
  具体不同在哪里呢，对于Service、Broadcast、Provider组件类的ANR而言，Gityuan 在 这篇文章 中做了一个非常精妙的解释：
  ANR是一套监控Android应用响应是否及时的机制，可以把发生ANR比作是 引爆炸弹，那么整个流程包含三部分组成：
  埋定时炸弹：中控系统(system_server进程)启动倒计时，在规定时间内如果目标(应用进程)没有干完所有的活，则中控系统会定向炸毁(杀进程)目标。 
  拆炸弹：在规定的时间内干完工地的所有活，并及时向中控系统报告完成，请求解除定时炸弹，则幸免于难。
  引爆炸弹：中控系统立即封装现场，抓取快照，搜集目标执行慢的罪证(traces)，便于后续的案件侦破(调试分析)，最后是炸毁目标。
  http://gityuan.com/2019/04/06/android-anr/
4.第二类原理概述
与组件类ANR不同的是，Input类型的超时机制并非时间到了一定就会爆炸，而是处理后续上报事件的过程才会去检测是否该爆炸，所以更像是 扫雷 的过程。
什么叫做扫雷呢，对于输入系统而言，即使某次事件执行时间超过预期的时长，只要用户后续没有再生成输入事件，那么也不需要ANR。
而只有当新一轮的输入事件到来，此时正在分发事件的窗口（即App应用本身）迟迟无法释放资源给新的事件去分发，这时InputDispatcher才会根据超时时间，
动态的判断是否需要向对应的窗口提示ANR信息。
这也正是用户在第一次点击屏幕，即使事件处理超时，也没有弹出ANR窗口，而当用户下意识再次点击屏幕时，屏幕上才提示出了ANR信息的原因。
5.由此可见，组件类ANR和Input ANR原理上确实有所不同
除此之外，前者是在ActivityManager线程中处理的ANR信息，后者则是在InputDispatcher线程中处理的ANR，这里通过一张图简单了
解一下后者的整体流程：anr输入事件.png
5.InputDispatcher的源码实现中，整体的事件分发流程共使用到3个事件队列：
  mInBoundQueue：用于记录InputReader发送过来的输入事件；
  outBoundQueue：用于记录即将分发给目标应用窗口的输入事件；
  waitQueue：用于记录已分发给目标应用，且应用尚未处理完成的输入事件。
  以下几种情况会导致进入ANR检测状态：
  1、目标应用不会空，而目标窗口为空。说明应用程序在启动过程中出现了问题；
  2、目标Activity的状态是Pause，即不再是Focused的应用；
  3、目标窗口还在处理上一个事件。
  并非所有「目标窗口还在处理上一个事件」都会抛出ANR，而是需要通过检测时间，如果未超时，那么直接中止本轮事件分发，反之，
  如果事件分发超时，那么才会确定ANR的发生。(重点)
  

1.Android自身的 输入系统 又是什么？
一言以蔽之，任何与Android设备的交互——我们称之为 输入事件，都需要通过 输入系统 进行管理和分发；
这其中最靠近上层，并且最典型的一个小环节就是View的 事件分发 流程。
思维导图如下：images/性能y优化/anr输入系统.png
2.Android系统在启动的时候,会初始化zygote进程和由zygote进程fork出来的SystemServer进程；作为 系统进程 之一，
  SystemServer进程会提供一系列的系统服务，而接下来要讲到的InputManagerService也正是由 SystemServer 提供的。
3.在 输入系统 中，WMS非常重要，其负责管理IMS、Window与ActivityManager之间的通信，这里点到为止，后文再进行补充，我们先来看IMS。
4.EventHub中使用epoll的恰到好处——多个物理输入设备对应了多个不同的输入流，通过epoll机制，在EventHub初始化时，分别创
  建mEpollFd和mINotifyFd；前者用于监听设备节点是否有设备文件的增删，后者用于监听是否有可读事件，创建管道，让InputReader来读取事件：
  知乎：epoll或者kqueue的原理是什么?
  https://www.zhihu.com/question/20122137/answer/14049112
5.InputDispatcher进入了本文最关键的一个环节——调用 findFocusedWindowTargetLocked()获取当前的 焦点窗口 ，同时检测目标应用是否有ANR发生。
6.InputReader和InputDispatcher运行在system_server 系统进程 中，而用户操作的应用都运行在自己的 应用进程 中；这里就
  涉及到跨进程通信，那么 应用进程 是如何与 系统进程 建立通信的呢？ 
  答案：这里为什么选择Socket而不是选择Binder呢，关于这个问题的解释，笔者找到了一个很好的版本：
  Socket可以实现异步的通知，且只需要两个线程参与（Pipe两端各一个），假设系统有N个应用程序，跟输入处理相关的线程数目是 N+1
   (1是Input Dispatcher线程）。然而，如果用Binder实现的话，为了实现异步接收，每个应用程序需要两个线程，一个Binder线程，
   一个后台处理线程（不能在Binder线程里处理输入，因为这样太耗时，将会堵塞住发送端的调用线程）。在发送端，同样需要两个线程，
   一个发送线程，一个接收线程来接收应用的完成通知，所以，N个应用程序需要 2（N+1)个线程。相比之下，Socket还是高效多了。
   

        