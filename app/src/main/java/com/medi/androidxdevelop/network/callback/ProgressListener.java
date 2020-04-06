package com.medi.androidxdevelop.network.callback;

/**
 * Description:监听进度
 *
 * @author: guoyongping
 * @date: 2016/8/4 18:04
 */
public interface ProgressListener {
    void onProgress(long hasWrittenLen, long totalLen, boolean hasFinish);
}
