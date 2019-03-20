package com.mm.sdkdemo.log;

/**
 * @author wangduanqing
 */
public interface LogTag {
    String COMMON = "MomoVideoSDK";

    interface RECORDER {
        String MUSIC = "MUSIC";
        String FACE = "VideoFaceUtils";

        String VIDEO_CUT = "VideoCut";

        String RECORD = "VideoRecord";
    }

    String IMAGE = "Image";

    interface PROCESSOR {
        String MUSIC = "MUSIC";
        String FACE = "VideoFaceUtils";

        String VIDEO_CUT = "VideoCut";

        String PROCESS = "Process";
    }
}
