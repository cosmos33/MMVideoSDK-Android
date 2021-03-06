# MomoVideoSDK接口文档

Momo短视频模块SDK，支持短视频录制、视频编辑与后期特效处理等功能。MomoVideoSDK的四大核心模块为基本配置模块、录制模块、视频编辑以及图像编辑模块。功能涵盖短视频拍摄和编辑涉及到的工具类，提供滤镜、人像美化以及视频格式导出组件等相对丰富的技术支持。其中录制模块包含视频拍摄的相关操作以及拍后编辑的特效处理。视频与图像编辑模块包含对于视频和图片素材的编辑和特效添加。

# 目录  
* [授权模块](#授权模块)
    - [配置设置](#配置设置)
    - [获取模块接口](#获取模块接口)
* [录制模块](#录制模块)
    - [基本设置](#基本设置)
    - [录制控制](#录制控制)
    - [回调](#回调)
    - [高级效果](#高级效果)    
        - [添加音乐](#添加音乐)
        - [变速录制](#变速录制)
        - [滤镜相关](#滤镜相关)
        - [人脸识别相关](#人脸识别相关)
* [视频编辑](#视频编辑)
    - [基本设置](#视频编辑基本设置)
    - [播放控制](#视频编辑播放控制)
    - [回调](#视频编辑回调)
    - [特效处理](#视频编辑特效处理)
    - [视频导出](#视频导出)
* [图片编辑](#图片编辑)
    - [基本设置](#图片编辑基本设置)
    - [特效](#图片编辑特效)    
    - [处理操作](#处理操作)

## 授权模块
#### 配置设置
- 初始化


      void init(Application context, String appId);

#### 获取模块接口
- 获取录制器
    

      public static IMultiRecorder createRecorder();

- 获取视频处理器


      public static IVideoProcessor createVideoProcessor();
      
- 获取图片处理器


      public static IImageProcess createImageProcessor();

## 录制模块

#### 基本设置

- 设置配置参数并初始化


    /**
     * 录制器资源准备
     * @param activity  录制器持有的上下文信息
     * @param mrConfig  视频录制器需要的配置信息
     * @return 准备结果，true：表示准备完成，false：表示准备出现异常
     */
    boolean prepare(Activity activity, MRConfig mrConfig);
      
- 设置预览窗口
    

    /**
     * 设置预览窗口
     * @param holder 必须是可承载视频画面的surface
     */
    void setPreviewDisplay(SurfaceHolder holder);
      
- 设置预览窗口大小
    
    
    /**
     * 设置预览窗口宽高
     * @param w 宽度
     * @param h 高度
     */
    void setVisualSize(int w, int h);
      
- 设置视频输出路径
    
    
    /**
     * 设置视频输出路径
     * @param outPath 视频输出路径
     */
    void setMediaOutPath(String outPath);
      
- 设置闪光灯的模式
        
    
    /**
     * 设置闪光灯模式
     * @param mode 参考{@link IMultiRecorder#FLASH_MODE_ON}、{@link IMultiRecorder#FLASH_MODE_OFF}、{@link IMultiRecorder#FLASH_MODE_AUTO}
     */
    void setFlashMode(int mode);

- 切换摄像头
    
    
    /**
     * 切换摄像头
     */
    void switchCamera(); 


- 当前是否为前置摄像头
    
    
    /**
     * 当前是否为前置摄像头
     * @return true：表示是，false：表示不是
     */
    boolean isFrontCamera();
          
- 摄像头调焦
    
    
    /**
     * 摄像头调焦
     * @param x             焦点坐标X
     * @param y             焦点坐标X
     * @param viewWidth     调焦区域宽度
     * @param viewHeight    调焦区域高度
     */
    void focusOnTouch(double x, double y, int viewWidth, int viewHeight);


- 是否支持闪光灯打开
    
      
    boolean supportFlash();

- 是否支持闪光灯自动模式
      
    
    boolean supportAutoFlash();

- 重置Camera
      
    
    void resetCamera();                     

- 获取旋转的角度

    
    float getRotateDegree();
      
      
#### 录制控制

- 开始预览
    
    
    /**
     * 开始预览，确保在调用{@link IMultiRecorder#prepare(Activity, MRConfig)}和{@link IMultiRecorder#setPreviewDisplay(SurfaceHolder)} 之后调用
     */
    void startPreview();

- 停止预览
    
    
    /**
     * 结束预览，如果当前正在录制，内部则会调用{@link IMultiRecorder#pauseRecording()}，结束当前段的录制，并将分段视频加入到分段管理中
     */
    void stopPreview();
      
- 拍照
      
    
    /**
     * 拍照
     * @param path 保存路径
     * @param listener 回调监听 注意：回调函数在异步线程，注意相关处理
     */
    void takePhoto(String path, MRecorderActions.OnTakePhotoListener listener);

            
- 开始录制
    
    
    /**
     * 开始录制
     */
    void startRecording();
      
- 暂停录制
    
    
    /**
     * 结束片段视频录制，与开始录制相对应，结束后会自动将片段视频交于分段管理
     */
    void pauseRecording();
      
- 取消录制
    
    
    /**
     * 取消录制，删除累积的所有分段视频
     */
    void cancelRecording();

- 完成录制
    
    
    /**
     * 完成整个录制，并合成视频
     * @param onRecordFinishedListener 视频处理回调
     * @return 接口调用是否成功
     */
    boolean finishRecord(final MRecorderActions.OnRecordFinishedListener onRecordFinishedListener);
            
- 获取所有视频分段
    
    
    LinkedList<VideoFragment> getVideoFragments();            

- 删除最后一个分段
    
    
    void removeLast();
                  
- 获取分段录制的总时长
    
    
    long getTotalLength();   
                     
- 获取分段录制的数量
    
    
    int getFragmentCount();
                               
- 退出

    
    /**
     * 释放资源，确保退出录制业务时调用该函数
     */
    void release();                                  

#### 回调
- 错误回调

    
    void setOnErrorListener(MRecorderActions.OnErrorListener onErrorListener);
      
- 前后摄像头切换的回调

    
    void setOnCameraSetListener(ICamera.onCameraSetListener onCameraSetListener);

- 第一帧渲染完成的回调
    
    
    void setOnFirstFrameRenderedListener(MRecorderActions.OnFirstFrameRenderedListener listener)

- 变脸贴纸状态变化回调

    
    void setStickerStateChangeListener(StickerBlendFilter.StickerStateChangeListener stickerStateChangeListener);
      

#### 高级效果

###### 添加音乐
- 设置背景音乐

    
    /**
     * 设置录制的背景音乐，背景音乐会在startTime与endTime之间循环播放。注意，开始录制后音乐设置将变得无效，请确保在录制开始前进行音乐的设置
     * @param musicPath 音乐文件的绝对路径，不可为空
     * @param startTime 音乐的开始时间，单位ms
     * @param endTime   音乐的结束时间，单位ms
     * @param showTipWhenNotSupport 当不支持背景音乐设置时是否出现toast轻提醒
     */
    boolean setMusic(String musicPath, int startTime, int endTime, boolean showTipWhenNotSupport);
      
- 取消背景音乐      
        
    
    /**
     * 取消音乐设置
     */
    boolean cancelMusic();

###### 变速录制      
- 变速录制

    
    /**
     * 变速录制 比如[2f, 1.5f, 1f, 0.5f, 0.25f] 对应 ["极慢", "慢", "标准", "快", "极快"]
     * @param speed 范围：(无穷大，0.25f]
     */
    void setRecorderSpeed(float speed);   
       
###### 滤镜相关       
- 设置滤镜
       
    
    /**
     * 初始化滤镜列表
     * @param filters 滤镜
     */
    void initFilters(List<MMPresetFilter> filters);
        
    /**
     * 切换滤镜
     * @param index 切换位置，这里的索引是对应{@link IMultiRecorder#initFilters(List)} 这里设置进来的顺序。
     * @param up 是否为向上滑动
     * @param offset 向上滑动比例 [0, 1f]
     */
    void changeToFilter(int index, boolean up, float offset);
      
- 添加变脸/贴纸
      
    
    /**
     * 添加变脸特效，默认添加新的变脸特效时会移除上一个，所以不用担心重复添加问题，也不必在调用{@link IMultiRecorder#addMaskModel(MaskModel)} 之前调用 {@link IMultiRecorder#clearMaskModel()}
     * @param maskModel 变脸或贴纸素材 可以通过{@link com.mm.mediasdk.utils.VideoFaceUtils#readMaskModel(Context, File)} 生成素材对象。素材文件可以通过提供的编辑器进行制作生成（需按照陌陌拍摄器素材标准制作）
     * @return 添加结果
     */
    boolean addMaskModel(MaskModel maskModel);

- 清除变脸/贴纸

    
    /**
     * 清除变脸特效
     */
    void clearMaskModel();
       
###### 人脸识别相关
- 磨皮

    
    /**
     * 设置磨皮参数
     * @param value [0,1f]
     */
    void setSkinLevel(float value);
      
- 美白


    /**
     * 设置美白参数
     * @param value [0,1f]
     */
    void setSkinLightingLevel(float value);

- 磨皮和美白

    
    /**
     * 设置磨皮和美白参数
     * @param skinLevel 磨皮参数，参考{@link IMultiRecorder#setSkinLevel(float)}
     * @param skinLightingScale  美白参数，参考{@link IMultiRecorder#setSkinLightingLevel(float)}
     */
    void setSkinAndLightingLevel(float skinLevel, float skinLightingScale);
      

- 大眼
       
    
    /**
     * 设置大眼参数
     * @param value [0,1f]
     */
    void setFaceEyeScale(float value);
       
- 瘦脸
       
    
    /**
     * 设置瘦脸参数
     * @param value [0,1f]
     */
    void setFaceThinScale(float value);

- 大眼和瘦脸

    
    /**
     * 设置大眼和瘦脸参数
     * @param mFaceEyeScale 大眼 取值范围[0,1f] 参考：{@link IMultiRecorder#setFaceEyeScale(float)}
     * @param mFaceThinScale 瘦脸 取值范围[0,1f] 参考：{@link IMultiRecorder#setFaceThinScale(float)}
     */
    void setFaceEyeAndThinScale(float mFaceEyeScale, float mFaceThinScale);

- 瘦身
       
    
    /**
     * 设置瘦身参数
     * @param value [0,1f]
     */
    void setSlimmingScale(float value);

- 长腿
       
    
    /**
     * 设置长腿参数
     * @param value [0,1f]
     */
    void setLongLegScale(float value);
                      
## 视频编辑

#### 视频编辑基本设置
        
- 资源初始化

    
    void prepareVideo(MoVideo video);

- 设置变音相关资源

    
    void setPitchShiftProcessMode(String tmpDataFolder, String videoPath, int pitchMode, MRecorderActions.DataProcessListener listener);

- 设置视频输出信息

    
    void setOutVideoInfo(int width, int height, int fps, int bitrate);
        
- 设置是否开启音频混音       
        
    
    /**
     * 是否开启音频混音模式，当视频没有发生变化只是音频发生变化时可以开启，这样可以让编码后的事情具有更高的清晰度
     * @param openAudioMix
     */
    void setAudioMixMode(boolean openAudioMix);

- 设置音频输出相关信息        

    
    void setOutAudioInfo(int sampleRate, int channels, int bits, int bitrate);

#### 视频编辑播放控制

- 设置是否循环播放        

    
    void setLoopBack(boolean isLoopBack);

- 设置原视频中语音声音大小

    
    /**
     * 设置原视频中语音声音大小
     * @param ratio    [0-1f]
     */
    void setPlayingSrcAudioRatio(float ratio);

- 设置音乐声音大小

    
    /**
     * 设置音乐声音大小
     * @param ratio    [0-1f]
     */
    void setPlayingMusicAudioRatio(float ratio);
- 设置预览窗口

    
    void addScreenSurface(final SurfaceHolder holder);
    
    void addSurfaceTexture(final SurfaceTexture surfaceTexture);

- 开始预览    

    
    void startPreview();
        
- 指定播放跳转位置        
        
    
    void seek(long ptsMs, boolean isPause);

- 暂停播放    
        
    
    void pause();

- 恢复播放    
        
    
    void resume();
    

- 停止预览功能    

    
    void stopPreview();


- 从合成模式切换到预览模式

    
    void changeToPreviewMode();

- 释放资源    

    
    void release();     

- 获取当前处理状态


    /**
     * 参考 {@link com.mm.moment.recorder.MomoProcess#MODE_PREVIEW}、{@link com.mm.moment.recorder.MomoProcess#MODE_PROCESS}
     * @return
     */
    int getProcessorMode();

- 查询当前播放状态 
    
    
    boolean isPlaying();

- 查询当前视频总长度

    
    int getDuration();
        
#### 视频编辑回调

- 设置错误回调        

    
    void setOnProcessErrorListener(MRecorderActions.OnProcessErrorListener listener);

- 设置播放相关回调
        
    
    void setPlayingStatusListener(MRecorderActions.OnPlayingStatusListener listener);

- 设置合成视频回调        

    
    void setOnStatusListener(MRecorderActions.OnProcessProgressListener listener);
        

#### 视频编辑特效处理
    
- 设置基本滤镜
       
       
    //初始化所有滤镜 
    void initFilters(List<MMPresetFilter> filters);
        
    //根据初始化的滤镜组，设置滤镜切换，方向上下   
    void changeToFilter(final int index, boolean up, float positionReal);
    
- 添加特效滤镜
    
    
        void addSpecialFilter(List<BasicFilter> filters);

- 添加文字贴纸或水印
        
    
    public void addFilter(BasicFilter basicFilter);

- 更新当前的视频特效配置

    
    /**
    * 更新视频编辑后效果 seekTime 更新后播放位置，timeRangeScales：视频变速特效
    */
    void updateEffect(List<TimeRangeScale> timeRangeScales, long seekTime);

    /**
    * 更新视频编辑后效果
    */
    void updateEffect(long seekTime);    
    
#### 视频导出    
        
- 合成视频        

    
    void makeVideo(String path);


## 图片编辑    
#### 图片编辑基本设置
- 初始化
    
      
    /**
     * 初始化
     * @param context   图片处理当前的上下文
     * @param imagePath 图片路径（可以是标准图片JPG/PNG，也可以是陌陌拍摄器拍摄的私有协议图片）
     * @param fastImage 图片处理的GLSurfaceView，需要业务层自己在XML或代码中构建 参考{@link project.android.imageprocessing.FastImageProcessingView}
     * @param savePath  图片处理后保存的路径
     * @return 初始化结果，true表示成功
     */
    boolean init(Context context, String imagePath, FastImageProcessingView fastImage, String savePath);
    
    /**
     * 初始化
     * @param context   图片处理当前的上下文
     * @param bitmap    图片位图
     * @param fastImage 图片处理的GLSurfaceView，需要业务层自己在XML或代码中构建 参考{@link project.android.imageprocessing.FastImageProcessingView}
     * @param savePath  图片处理后保存的路径
     * @return 初始化结果，true表示成功
     */
    boolean init(Context context, Bitmap bitmap, FastImageProcessingView fastImage, String savePath);

- 释放资源

    
    /**
     * 释放资源，确保在上下文退出时进行资源是否，否则会出现内存泄漏等情况
     */
    void release();

#### 图片编辑特效
- 磨皮
        
    
    /**
     * 设置磨皮参数
     * @param value [0,1f]
     */
    void setSkinLevel(float value);
- 美白

    
    /**
     * 设置美白参数
     * @param value [0,1f]
     */
    void setSkinLightingLevel(float value);
    
- 磨皮和美白

    
    /**
     * 设置磨皮和美白参数
     * @param skinLevel 磨皮参数，参考{@link IImageProcess#setSkinLevel(float)}
     * @param skinLightingScale  美白参数，参考{@link IImageProcess#setSkinLightingLevel(float)}
     */
    void setSkinAndLightingLevel(float skinLevel, float skinLightingScale);

- 大眼和瘦脸
       
    
    /**
     * 设置大眼和瘦脸参数
     * @param mFaceEyeScale 大眼 取值范围[0,1f]
     * @param mFaceThinScale 瘦脸 取值范围[0,1f]
     */
    void updateBigEyeAndThin(float mFaceEyeScale, float mFaceThinScale);
       

- 瘦身和长腿
       
       
    /**
     * 设置瘦身和长腿参数
     * @param bodyWarpWidth 瘦身 取值范围[0,1f]
     * @param bodyWarpLegsLength 长腿 取值范围[0,1f]
     */
    void updateBodyWarpAndLegLen(float bodyWarpWidth, float bodyWarpLegsLength);

- 初始化滤镜
       
       
    /**
     * 初始化滤镜列表
     * @param filters 滤镜
     */
    void initFilters(List<MMPresetFilter> filters);

- 切换滤镜


    /**
     * 切换滤镜
     * @param index 切换位置，这里的索引是对应{@link IImageProcess#initFilters(List)} 这里设置进来的顺序。
     * @param up 是否为向上滑动
     * @param offset 向上滑动比例 [0, 1f]
     */
    void changeToFilter(int index, boolean up, float offset);
    


#### 图片处理
- 是否正在处理中

    
    /**
     * 是否正在处理中状态
     * @return true：表示正在处理中
     */
    boolean isProcessing();
    
- 设置处理监听


    /**
     * 设置图片处理监听
     * @param listener 监听器
     */
    void setImageProcessListener(ImageProcessListener listener);

- 开始处理


    /**
     * 开始处理
     * @param blendBitmap   贴纸bitmap  把贴纸操作的View转成bitmap，在处理时传入即可；如何转换可以参考 {@link com.mm.mediasdk.utils.ImageUtil#createBitmapByView(View, int, int, int, int)}
     * @param maskBitmap    涂鸦bitmap
     * @param width         宽度
     * @param height        高度
     */
    void startImageProcess(Bitmap blendBitmap, Bitmap maskBitmap, int width, int height);