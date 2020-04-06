package com.medi.androidxdevelop.network.request;


import com.medi.androidxdevelop.network.callback.ProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Description:重新RequestBody  支持byte写入进度
 *
 * @author: guoyongping
 * @date: 2016/8/4 17:58
 */
public class ByteRequestBody extends RequestBody {

    private static final String TAG="ByteRequestBody";
    private  byte [] bytes;
    private  int bytesLength;
    public static final int CHUNK_SIZE = 1*1024;
    private BufferedSink bufferedSink;
    private ProgressListener mProgressListener;

    public ByteRequestBody(byte [] bytes , ProgressListener progressListener) {
        this.bytes=bytes;
        if(bytes!=null){
            bytesLength=bytes.length;
        }
        this.mProgressListener = progressListener ;
    }

    /**
     * 返回了本RequestBody的长度，也就是上传的totalLength
     */
    @Override
    public long contentLength() throws IOException {
        return bytesLength;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/json;charset=utf-8");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            //包装
            bufferedSink = Okio.buffer(sink(sink));
        }
        int offset=0;
        while(offset<bytesLength){
            int chunkSize=calcPutSize(bytesLength,offset);
            bufferedSink.write(bytes,offset,chunkSize);
            offset+=chunkSize;
        }
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;
                //回调上传接口
                mProgressListener.onProgress(bytesWritten, contentLength, bytesWritten == contentLength);
            }
        };
    }


    private int calcPutSize(int bytesLength,int offset) {
//        Log.i("content infocalcPutSize","---------offset:"+offset);
        int left = bytesLength - offset;//待提交的字节位置
        return left < CHUNK_SIZE ? left : CHUNK_SIZE;
    }
}
