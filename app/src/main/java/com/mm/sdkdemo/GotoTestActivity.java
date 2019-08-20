package com.mm.sdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.mm.recorduisdk.recorder.MediaConstants;
import com.mm.recorduisdk.recorder.model.Photo;
import com.mm.recorduisdk.recorder.model.Video;

import java.util.List;

public class GotoTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goto_test);
        TextView content = findViewById(R.id.tv_content);
        Intent intent = getIntent();
        if (intent.hasExtra(MediaConstants.EXTRA_KEY_IMAGE_DATA)) {
            List<Photo> photos = (List<Photo>) intent.getSerializableExtra(MediaConstants.EXTRA_KEY_IMAGE_DATA);
            if(photos!=null&&photos.size()>0){
                content.setText(TextUtils.isEmpty(photos.get(0).tempPath)?photos.get(0).path:photos.get(0).tempPath);
            }
        }else if(intent.hasExtra(MediaConstants.EXTRA_KEY_VIDEO_DATA)){
            Video video = intent.getParcelableExtra(MediaConstants.EXTRA_KEY_VIDEO_DATA);
            content.setText(video.path);
        }
    }
}
