package com.medi.androidxdevelop.network.request;



import com.medi.androidxdevelop.network.callback.ProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Description:监听下载进度
 *
 * @author: guoyongping
 * @date: 2016/8/5 09:18
 */
public class ProgressResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private final ProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytes = 0L;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long readBytes = super.read(sink, byteCount);
                totalBytes += readBytes != -1 ? readBytes : 0;
                progressListener.onProgress(totalBytes, responseBody.contentLength(), readBytes == -1);
                return readBytes;
            }
        };
    }
}
