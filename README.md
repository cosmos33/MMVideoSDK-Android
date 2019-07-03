## 最新更新：

#### 拍摄器

[拍摄器SDK 2.1.1](https://github.com/cosmos33/MMVideoSDK-Android/tree/recorder_2.1.1)

[拍摄器SDK 2.0.0](https://github.com/cosmos33/MMVideoSDK-Android/tree/recorder_2.0.0)

[拍摄器SDK 1.0.6](https://github.com/cosmos33/MMVideoSDK-Android/tree/recorder_1.0.6)



#### 播放器

[播放器SDK 1.1.8](https://github.com/cosmos33/MMVideoSDK-Android/tree/player_1.1.8)

[播放器SDK 1.1.7](https://github.com/cosmos33/MMVideoSDK-Android/tree/player_1.1.7)

[播放器SDK 1.1.6](https://github.com/cosmos33/MMVideoSDK-Android/tree/player_1.1.6)



-------

## 接入前准备

#### 注册
- 注册应用

    
        

- 开通短视频服务

#### 工程配置
    
- 权限配置

接入SDK需要如下权限，将如下代码copy到主app的AndroidManifest.xml对应位置
    
    
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
        
- 最小支持版本

    5.0  API21


- 添加工程依赖


    //SDK依赖库  
    implementation 'com.cosmos.mediax:recorder:1.0.2'
    


- so架构

SDK目前只提供了armeabi-v7a架构，请在app/build.gradle文件中配置如下代码。在android/defaultConfig/结构下添加

    
    ndk {
        abiFilters "armeabi-v7a"
    }

- 混淆配置

    
    -keepclasseswithmembernames class * {
        native <methods>;
    }
    
    -keep class com.core.glcore.util.** {*;}
    -keep class com.momocv.** {*;}
    -keep class com.imomo.momo.mediaencoder.** {*;}
    -keep class com.imomo.momo.mediamuxer.** {*;}

## 功能接入

#### 授权
授权流程是所有流程中基础流程，需要先进行初始化，才可以使用其他功能，否则可能会出现一些异常状态。

初始化    
    
    com.mm.mediasdk.MoMediaManager#init(Application application, String appid);
    
获取录制器
    
    IMultiRecorder com.mm.mediasdk.MoMediaManager#createRecorder();

获取视频处理器
    
    IVideoProcessor com.mm.mediasdk.MoMediaManager#createVideoProcessor();
    
获取图片处理器
    
    ImageProcess com.mm.mediasdk.MoMediaManager#createImageProcessor();
    
#### 拍照
一、构造IMultiRecorder
    
    IMultiRecorder recorder = MoMediaManager.createRecorder();
    
二、初始化
    
    MRConfig mrConfig = MRConfig.obtain();
    mrConfig.setDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    Size size = new Size(1280, 720);
    mrConfig.setEncodeSize(size);
    // 设置camera 的采集分辨率
    mrConfig.setTargetVideoSize(size);
    recorder.prepare(this, mrConfig);   
    
    //设置预览画面的宽高
    recorder.setVisualSize(width, height);
    //设置预览画布
    recorder.setPreviewDisplay(holder);

三、开始预览
    
    recorder.startPreview();
    
四、拍照
    
    File file = new File(Environment.getExternalStorageDirectory(), "take_photo.jpg");
    recorder.takePhoto(file.getAbsolutePath(), new MRecorderActions.OnTakePhotoListener() {
        @Override
        public void onTakePhotoComplete(int status, Exception e) {
            //0表示完成， -1表示失败
            MDLog.e(LogTag.RECORD, "onTakePhotoComplete %d", status);
        }
    });

五、结束预览
    
    recorder.stopPreview();
    
六、释放资源
    
    recorder.release();
    
其他操作，参考接口文档进行操作    


#### 录制
一、构造IMultiRecorder
    
    IMultiRecorder recorder = MoMediaManager.createRecorder();
    
二、初始化
    
    MRConfig mrConfig = MRConfig.obtain();
    mrConfig.setDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    //设置码率 8M
    mrConfig.setVideoEncodeBitRate(8<<20);

    //设置音频声道数
    mrConfig.setAudioChannels(1);
    //以下两项选一种即可
    //mrConfig.setUseDefaultEncodeSize(true);
    mrConfig.setEncodeSize(new Size(640, 960));
    //设置视频编码帧率
    mrConfig.setVideoFPS(20);
    // 设置camera 的采集分辨率
    mrConfig.setTargetVideoSize(new Size(720, 1280));
    recorder.prepare(this, mrConfig);   
    
    //设置预览画面的宽高
    recorder.setVisualSize(width, height);
    //设置预览画布
    recorder.setPreviewDisplay(holder);

    //设置视频输出路径
    File file = new File(Environment.getExternalStorageDirectory(), "video_test.mp4");
    recorder.setMediaOutPath(file.getAbsolutePath());

三、开始预览

    recorder.startPreview();
    
四、开始录制
    
    recorder.startRecording();

五、停止录制
    
    recorder.stopRecording();

六、完成录制

    recorder.finishRecord(new MRecorderActions.OnRecordFinishedListener() {
        @Override
        public void onFinishingProgress(int progress) {
            MDLog.e(TAG, "onFinishingProgress %d", progress);
        }

        @Override
        public void onRecordFinished() {
            MDLog.e(TAG, "onRecordFinished");
        }

        @Override
        public void onFinishError(String errMsg) {
            MDLog.e(TAG, "onFinishError %s", errMsg);
        }
    });
其他功能参考接口文档和demo中的使用    

#### 视频处理
参考 #VideoProcessTestActivity 测试代码

一、构造IVideoProcessor

    IVideoProcessor videoProcessor = MoMediaManager.createVideoProcessor();
    
二、基本设置
    
    videoProcessor.setLoopBack(true);
    videoProcessor.setOutVideoInfo(720, 1280, 30, 4<<20, false);
    videoProcessor.setOnProcessErrorListener(new MRecorderActions.OnProcessErrorListener() {
        @Override
        public void onErrorCallback(int what, int errorCode, String msg) {
            MDLog.e(TAG, "onErrorCallback %d", what);
        }
    });

    videoProcessor.setOnStatusListener(new MRecorderActions.OnProcessProgressListener() {
        @Override
        public void onProcessProgress(float progress) {
            MDLog.e(TAG, "onProcessProgress %f", progress);
        }

        @Override
        public void onProcessFinished() {
            MDLog.e(TAG, "onProcessFinished");
            Toaster.show("导出完成");
        }
    });
    
    //选择视频源，放入到videoPath中
    MoVideo moVideo = new MoVideo();
    moVideo.path = videoPath;
    moVideo.osPercent = 100;
    moVideo.psPercent = 0;
    videoProcessor.prepareVideo(moVideo);

三、预览
    
    videoProcessor.addScreenSurface(holder);
    videoProcessor.startPreview();

四、添加滤镜
    
    List<MMPresetFilter> filters = FiltersManager.getAllFilters();
    lastFilter = FilterUtils.getFilterGroupByIndex(2, filters);
    videoProcessor.addFilters(lastFilter);

五、删除滤镜
    
    videoProcessor.deleteFilter(lastFilter);
    
六、添加动态贴纸
    
    //进行此操作前提是，已经下载了狗头动态贴纸，第三个参数为ID，用于删除时使用
    MaskModel model = MaskStore.getInstance().getMask(AppContext.getContext(), "/storage/emulated/0/MomoVideoSDK/moment/dynamic_sticker/59ccd466f38ef/3");
    videoProcessor.addMaskModel(model, 1, 0.5f, 0.5f);

七、删除动态贴纸
    
    videoProcessor.removeMaskModel(1);

八、暂停视频播放
    
    videoProcessor.pause();
    
九、恢复视频播放
    
    videoProcessor.resume();
    
其他功能参考接口文档和demo中的使用

#### 图片处理

一、构建ImageProcess
    
    ImageProcess imageProcess = MoMediaManager.createImageProcessor();

二、基本设置
    
    //给处理器设置滤镜
    imageProcess.initFilters(FiltersManager.getAllFilters());
    
    FastImageProcessingView processingView = findViewById(R.id.media_cover_image);
    
    //设置处理后保存的路径
    File file = new File(Configs.getDir("ProcessImage"), System.currentTimeMillis() + "_process.jpg");
    imageProcess.init(this, processFilePath, processingView, file.getAbsolutePath());

三、切换滤镜

    //具体替换滤镜的index，要根据初始化时的滤镜组决定，不可越界
    imageProcess.switchFilterPreview(2);
    
四、磨皮、美白、大眼、瘦脸
    
    imageProcess.updateSkinLevel(1f);
    imageProcess.updateSkinLightingLevel(1f);
    
五、开始处理

    imageProcess.setImageProcessListener(new ImageProcess.ImageProcessListener() {
        @Override
        public void onProcessCompleted(final String path) {
            MDLog.e(TAG, "onProcessCompleted %s", path);
        }

        @Override
        public void onProcessFailed() {
            MDLog.e(TAG, "onProcessFailed");
        }
    });
    imageProcess.startImageProcess(null, null, 0, 0);

六、释放
    
    imageProcess.release();
    
## 其他功能

#### 滤镜

#### 变脸


