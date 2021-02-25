glide源码分析
面试官：简历上最好不要写Glide，不是问源码那么简单 https://www.jianshu.com/p/330fa6422938
首先，当下流行的图片加载框架有那么几个，可以拿 Glide 跟Fresco对比，例如这些：

对比优缺点
Glide：
1.多种图片格式的缓存，适用于更多的内容表现形式（如Gif、WebP、缩略图、Video）
2.生命周期集成（根据Activity或者Fragment的生命周期管理图片加载请求）
3.高效处理Bitmap（bitmap的复用和主动回收，减少系统回收压力）
4.高效的缓存策略，灵活（Picasso只会缓存原始尺寸的图片，Glide缓存的是多种规格），加载速度快且内存开销小（默认Bitmap格式的不同，使得内存开销是Picasso的一半）
Fresco：
1.最大的优势在于5.0以下(最低2.3)的bitmap加载。在5.0以下系统，Fresco将图片放到一个特别的内存区域(Ashmem区) ==> 
  这个Ashmem区是一块匿名共享内存，Fresco 将Bitmap像素放到共享内存去了，共享内存是属于native堆内存。
2.大大减少OOM（在更底层的Native层对OOM进行处理，图片将不再占用App的内存）
3.适用于需要高性能加载大量图片的场景

二、假如让你自己写个图片加载框架，你会考虑哪些问题？
首先，梳理一下必要的图片加载框架的需求：
1.异步加载：线程池
2.切换线程：Handler，没有争议吧
3.缓存：LruCache、DiskLruCache
4.防止OOM：软引用、LruCache、图片压缩、Bitmap像素存储位置 
   方法1：软引用 
   方法2：onLowMemory 当内存不足的时候，Activity、Fragment会调用onLowMemory方法，可以在这个方法里去清除缓存，Glide使用的就是这一种方式来防止OOM。
   方法3：从Bitmap 像素存储位置考虑 
   8.0 的Bitmap创建就两个点：                     
                        创建native层Bitmap，在native堆申请内存。
                        通过JNI创建java层Bitmap对象，这个对象在java堆中分配内存。
                        像素数据是存在native层Bitmap，也就是证明8.0的Bitmap像素数据存在native堆中。
5.内存泄露：注意ImageView的正确引用，生命周期管理
   当然，修改也比较简单粗暴，将ImageView用WeakReference修饰就完事了。 
   事实上，这种方式虽然解决了内存泄露问题，但是并不完美，例如在界面退出的时候，我们除了希望ImageView被回收，同时希望加载图片的任务可以取消，队未执行的任务可以移除。
   Glide的做法是监听生命周期回调，看 RequestManager 这个类在Activity/fragment 销毁的时候，取消图片加载任务，细节大家可以自己去看源码。                                     
6.列表滑动加载的问题：加载错乱、队满任务过多问题
  图片错乱：由于RecyclerView或者LIstView的复用机制，网络加载图片开始的时候ImageView是第一个item的，加载成功之后ImageView由于复用可能跑到第10个item去了，在第10个item显示第一个item的图片肯定是错的。
  常规的做法是给ImageView设置tag，tag一般是图片地址，更新ImageView之前判断tag是否跟url一致。
  当然，可以在item从列表消失的时候，取消对应的图片加载任务。要考虑放在图片加载框架做还是放在UI做比较合适
  队满任务过多问题：列表滑动，会有很多图片请求，如果是第一次进入，没有缓存，那么队列会有很多任务在等待。所以在请求网络图片之前，需要判断队列中是否已经存在该任务，存在则不加到队列去。
7.当然，还有一些不是必要的需求，例如加载动画等。

2.1 异步加载：
线程池，多少个？
缓存一般有三级，内存缓存、硬盘、网络。
由于网络会阻塞，所以读内存和硬盘可以放在一个线程池，网络需要另外一个线程池，网络也可以采用Okhttp内置的线程池。
读硬盘和读网络需要放在不同的线程池中处理，所以用两个线程池比较合适。
Glide 必然也需要多个线程池，看下源码是不是这样
public final class GlideBuilder {
  ...
  private GlideExecutor sourceExecutor; //加载源文件的线程池，包括网络加载
  private GlideExecutor diskCacheExecutor; //加载硬盘缓存的线程池
  ...
  private GlideExecutor animationExecutor; //动画线程池
  ....
  }
Glide使用了三个线程池，不考虑动画的话就是两个

1.自定义moulde
@GlideModule
public class CustomGlideModule extends AppGlideModule {
    /**
     *全局配置
     */
    public void applyOptions(Context context, GlideBuilder builder) {
    }

    /**
     *替换网络模块
     */
    public void registerComponents(Context context, Glide glide, Registry registry) {
        Log.d("CustomGlide_log","registerComponents");
        //registry.append(GlideUrl.class, InputStream.class,new OkHttpUriLoader.Factory());
        //替换掉之前的网络框架
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new ProgressInterceptor())
                .build();
        registry.replace(GlideUrl.class, InputStream.class,new OkHttpUriLoader.Factory(okHttpClient));
    }
}
2.扩展方法
@GlideExtension
public class CustomGlideExtension {
    /*GlideExtension
    为了添加新的方法，修改已有的方法或者添加对其他类型格式的支持，
    你需要在扩展中使用加了注解的静态方法。
    GlideOption用来添加自定义的方法，GlideType用来支持新的格式*/


    //缩略图的最小尺寸，单位：px
    private static final int MINI_THUMB_SIZE = 100;

    private static final RequestOptions DECODE_TYPE_GIF = GlideOptions.decodeTypeOf(GifDrawable.class).lock();

    private CustomGlideExtension() {
    }

    /**
     * 1.自己新增的方法的第一个参数必须是RequestOptions options
     * 2.方法必须是静态的
     * 3.你可以为方法任意添加参数，但要保证第一个参数为 RequestOptions。
     * 4.这些生成的方法在标准的 Glide 和 RequestOptions 类里不可用，只存在于生成的等效类中。
     *
     * @param options
     */
    @SuppressLint("CheckResult")
    @GlideOption
    public static void miniThumb(RequestOptions options) {
        options.fitCenter()
                .override(MINI_THUMB_SIZE);

    }

    /**
     * 1.注解的方法允许你添加对新的资源类型的支持，包括指定默认选项。
     * @param requestBuilder
     */
    @SuppressLint("CheckResult")
    @GlideType(GifDrawable.class)
    public static void asGIF(RequestBuilder<GifDrawable> requestBuilder) {
        requestBuilder
                .transition(new DrawableTransitionOptions())
                .apply(DECODE_TYPE_GIF);
    }
