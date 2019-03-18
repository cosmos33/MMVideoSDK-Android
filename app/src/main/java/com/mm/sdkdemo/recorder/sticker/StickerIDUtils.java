package com.mm.sdkdemo.recorder.sticker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于StickerId生成
 * Created by zhu.tao on 2017/6/19.
 */

public class StickerIDUtils {

    private static final AtomicInteger seqId = new AtomicInteger(0);

    /**
     * 下一个seqId
     * @return
     */
    public static int nextSeqId() {
        int id = seqId.incrementAndGet() % 10000000;
        return id;
    }

}
