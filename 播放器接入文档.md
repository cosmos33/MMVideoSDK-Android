# 播放器SDK接入文档

## 接入前准备

### 注册
* 注册应用
* 开通短视频服务

### 工程配置

* 权限配置
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```
* 最小支持版本
```    
Android4.2 API17
```

* 添加工程依赖
```
implementation 'com.cosmos.mediax:playersdk:1.1.2'
```

* so架构
    
SDK目前只提供了armeabi-v7a架构，请在app/build.gradle文件中配置如下代码。在android/defaultConfig/结构下添加
```
ndk {
    abiFilters "armeabi-v7a"
}
```

* 混淆配置
```
-keepclasseswithmembernames class * {
native <methods>;
}

-keep class tv.danmaku.ijk.media.** {*;}
```

## 功能接入
### 基础设置
* 初始化
```
PlayerManager.init(this, "your app id");
```

* 预加载操作（其他接口参考接口文档 IMediaPreloader）
```
//判断视频是否已经预加载
PlayerManager.getMediaPreLoader().isTakCachedByUrl(videoUrl);

//添加预加载任务
PlayerManager.getMediaPreLoader().addTask(videoUrl);

//取消预加载任务
PlayerManager.getMediaPreLoader().clearTaskByUrl(videoUrl);
```

### 使用VideoView播放视频

* 构建VideoView
```
//布局添加，或直接构建
<com.mm.player.VideoView
    android:id="@+id/player_videoview"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />


videoView = findViewById(R.id.player_videoview);
//videoView = new VideoView(getContext());
```

* 设置
```
//设置裁切模式
videoView.setScaleType(ScalableType.FIT_CENTER);

//设置状态监听
videoView.setOnStateChangedListener(new ICosPlayer.OnStateChangedListener() {
    @Override
    public void onStateChanged(final int state) {
        if (state == ICosPlayer.STATE_BUFFERING) {
            //do something，eg 显示loading
        } else if (state == ICosPlayer.STATE_READY) {
            //do something
        }
    }
});

```

* 播放控制
```
//开始播放
videoView.playVideo(videoUrl);
//跳转
videoView.seekTo(3000);

playPauseBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (videoView.isPlaying()) {
            videoView.pause();
            playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
        } else {
            videoView.resume();
            playPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
        }
    }
});

//记录当前播放位置
lastPos = videoView.getCurrentPosition();
//释放
videoView.releaseVideo();
```