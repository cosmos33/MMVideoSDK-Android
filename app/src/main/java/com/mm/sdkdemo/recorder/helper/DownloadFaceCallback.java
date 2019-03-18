package com.mm.sdkdemo.recorder.helper;

import com.mm.sdkdemo.bean.MomentFace;

/**
 * Created by chenwangwang on 2018/3/27.
 * 下载变脸资源的回调接口
 */
public interface DownloadFaceCallback {
    /**
     * 下载成功
     * @param face 加载的变脸资源信息
     * @param isLocalExit 是否本地已经存在可使用的资源，true 代表本次任务未从服务器下载资源，只是做了一下本地资源的校验，并且校验结果是本地资源可用
     */
    void onFaceDownloadSuccess(MomentFace face, boolean isLocalExit);

    /**
     * 下载失败
     * @param face 加载的变脸资源信息
     */
    void onFaceDownloadFailed(MomentFace face);

    /**
     * 该方法在下列情况下会回调                         <br/>
     * ：想要下载的变脸已经在本地存在                    <br/>
     * ：异步线程监测已经存在的文件，居然TM的损坏了不能用    <br/>
     * ：如果上面两个都满足，该放回就会调用               <br/>
     * @param momentFace
     */
    void onIntegrityDetectionFailed(MomentFace momentFace);
}
