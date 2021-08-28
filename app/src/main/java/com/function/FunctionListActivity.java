package com.function;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.function.imageprocess.ImageProcessTestActivity;
import com.function.recordvideo.VideoRecordTestActivity;
import com.function.takephoto.TakePhotoTestActivity;
import com.function.videoprocess.VideoProcessTestActivity;
import com.mm.base_business.base.BaseActivity;
import com.mm.sdkdemo.R;

public class FunctionListActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_list);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_photo:
                startActivity(new Intent(this, TakePhotoTestActivity.class));
                break;

            case R.id.record_video:
                startActivity(new Intent(this, VideoRecordTestActivity.class));
                break;
            case R.id.video_process:
                startActivity(new Intent(this, VideoProcessTestActivity.class));
                break;
            case R.id.photo_process:
                startActivity(new Intent(this, ImageProcessTestActivity.class));
                break;
        }
    }
}
