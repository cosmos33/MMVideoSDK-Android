package com.mm.player_business;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.mm.base_business.base.BaseFullScreenActivity;
import com.mm.base_business.utils.DeviceUtils;
import com.mm.mmutil.toast.Toaster;
import com.mm.player.PlayerManager;
import com.mm.player.scale.ScalableType;

public class PrePlayActivity extends BaseFullScreenActivity {
    private EditText editText;
    public static int scaleType = ScalableType.CENTER_CROP;
    public static boolean mediaCodec = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preplay);
        editText = findViewById(R.id.edittext);
    }

    public void onClick(View view) {

        if (view == findViewById(R.id.choose_local_file)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), 101);
        } else if (view == findViewById(R.id.go_play)) {
            Intent intent = new Intent(this, PlayerActivity.class);
            String url = editText.getText().toString().trim();
            if (TextUtils.isEmpty(url)) {
                Toaster.show("播放地址不能为空");
                return;
            }
            intent.putExtra(PlayerActivity.KEY_URL, url);
            startActivity(intent);
        } else if (view == findViewById(R.id.go_preload)) {
            String url = editText.getText().toString().trim();
            Uri currentUri = Uri.parse(url);
            if (TextUtils.isEmpty(url)) {
                Toaster.show("地址不能为空");
                return;
            }
            if ("http".equalsIgnoreCase(currentUri.getScheme()) || "https".equalsIgnoreCase(currentUri.getScheme())) {
                PlayerManager.getMediaPreLoader().addTask(url);
            } else {
                Toaster.show("该协议视频不支持预加载");
            }
        } else if (view == findViewById(R.id.go_random_play)) {
            startActivity(new Intent(this, PlayListActivity.class));

        } else if (view == findViewById(R.id.playlist_CENTER_CROP)) {
            scaleType = ScalableType.CENTER_CROP;
            Toaster.show("当前为CENTER_CROP模式");
        } else if (view == findViewById(R.id.playlist_FIT_CENTER)) {
            scaleType = ScalableType.FIT_CENTER;
            Toaster.show("当前为FIT_CENTER模式");
        } else if (view == findViewById(R.id.playlist_FIT_XY)) {
            scaleType = ScalableType.FIT_XY;
            Toaster.show("当前为FIT_XY模式");
        } else if (view == findViewById(R.id.playlist_codec)) {
            mediaCodec = !mediaCodec;
            if (mediaCodec) {
                Toaster.show("已经切换到硬解码");
            } else {
                Toaster.show("已经切换到软解码");
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                String selectedImagePath = DeviceUtils.uri2Path(getApplicationContext(), data.getData());
                editText.setText(selectedImagePath);
            }
        }
    }

}
