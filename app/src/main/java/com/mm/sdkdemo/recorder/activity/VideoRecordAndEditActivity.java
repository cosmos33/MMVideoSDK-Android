package com.mm.sdkdemo.recorder.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.immomo.mdlog.MDLog;
import com.immomo.mmutil.task.MomoTaskExecutor;
import com.immomo.mmutil.toast.Toaster;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.BaseFragment;
import com.mm.sdkdemo.bean.VideoInfoTransBean;
import com.mm.sdkdemo.bean.VideoRecordDefs;
import com.mm.sdkdemo.recorder.MediaConstants;
import com.mm.sdkdemo.recorder.listener.FragmentChangeListener;
import com.mm.sdkdemo.recorder.model.Video;
import com.mm.sdkdemo.recorder.view.AlbumHomeFragment;
import com.mm.sdkdemo.recorder.view.VideoCutFragment;
import com.mm.sdkdemo.recorder.view.VideoEditFragment;
import com.mm.sdkdemo.recorder.view.VideoRecordFragment;
import com.mm.sdkdemo.utils.MediaSourceHelper;
import com.mm.sdkdemo.utils.MomentUtils;
import com.mm.sdkdemo.utils.SDCardUtils;
import com.mm.sdkdemo.utils.VideoCompressUtil;
import com.mm.sdkdemo.utils.VideoPickerCompressListener;
import com.mm.sdkdemo.utils.VideoUtils;
import com.mm.sdkdemo.utils.WakeManager;

import java.util.Stack;

public class VideoRecordAndEditActivity extends BaseFullScreenActivity {

    /**
     * 相册、拍摄、视频编辑等切换时的目的fragment
     */
    public static final String GOTO_WHERE = "gotoWhere";

    /**
     * 回退到backStack栈顶的fragment，若无则finish
     */
    public static final String BACK_TO_OLD = "backToOld";

    /**
     * 自定义栈管理fragment间的切换/回退
     */
    public Stack<String> backStack;

    private String mGotoWhere;

    private static boolean startRecordForResult(@NonNull Context context, VideoInfoTransBean info, int requestCode) {
        if (context == null || info == null)
            return false;
        MomentUtils.isOnlyAlbum(info);
        Intent intent = new Intent(context, VideoRecordAndEditActivity.class);
        //        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MediaConstants.EXTRA_KEY_VIDEO_TRANS_INFO, info);
        bundle.putInt(MediaConstants.EXTRA_KEY_VIDEO_STATE, info.state);
        intent.putExtras(bundle);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
        //        addAnimationInFromBottom(context);
        return true;
    }

    /**
     * 传入Video，直接开始预览和编辑视频
     * 返回的编辑结果存储在Intent中
     * key为{@link MediaConstants#EXTRA_KEY_VIDEO_DATA}
     */
    private static boolean startEditForResult(@NonNull Context context, VideoInfoTransBean info, int requestCode) {
        if (context == null || info == null)
            return false;
        if (!MomentUtils.isSupportRecord()) {
            Toaster.showInvalidate("你的手机系统版本暂时不支持视频录制");
            //Fabric打点 统计不支持时刻录制的手机
            return false;
        }
        final Video video = info.video;
        if (VideoUtils.getVideoFixMetaInfo(video)) {
            if (VideoUtils.isLocalVideoSizeTooLarge(video)) {
                VideoCompressUtil.compressVideo(video, VideoUtils.getMaxVideoSize(),
                                                new VideoPickerCompressListener(context, info, onCompressListener, requestCode));
                return false;
            }
        } else {
            Toaster.show("视频格式不正确");
            return false;
        }
        Intent intent = new Intent(context, VideoRecordAndEditActivity.class);
        //        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MediaConstants.EXTRA_KEY_VIDEO_TRANS_INFO, info);
        if (info.video != null) {
            info.video.isChosenFromLocal = true;
            bundle.putParcelable(MediaConstants.EXTRA_KEY_VIDEO_DATA, info.video);
        }
        intent.putExtras(bundle);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
        addAnimationInFromBottom(context);
        return true;
    }

    /**
     * 底部弹出动画
     */
    private static void addAnimationInFromBottom(Context context) {
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.slide_in_from_bottm_300ms, 0);
        }
    }

    public static boolean startActivity(@NonNull final Context context, final VideoInfoTransBean info, final int requestCode) {
        if (context == null || info == null)
            return false;
        if (info.video == null) {
            return startRecordForResult(context, info, requestCode);
        } else {
            return startEditForResult(context, info, requestCode);
        }
    }

    public static boolean startActivityForVChatSelectImage(@NonNull final Context context, final VideoInfoTransBean info, final int requestCode) {
        if (context == null || info == null) {
            return false;
        }
        return startRecordForResult(context, info, requestCode);
    }

    private static final String KEY_STATE = "KEY_STATE";
    private static final String KEY_EXTRA = "KEY_EXTRA";
    private static final int STATE_RECORD = 0;
    private static final int STATE_EDIT = 1;
    private static final int STATE_CUT = 2;
    private static final int STATE_IN_ALBNUM = 3;

    private Bundle args = null;
    private VideoRecordFragment recordFragment;
    private VideoEditFragment editFragment;
    private VideoCutFragment cutFragment;
    private AlbumHomeFragment albumHomeFragment;
    private BaseFragment currentFragment;
    private int state = STATE_RECORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //保持屏幕常亮
        try {
            WakeManager.getInstance().keepScreenOn(this);
        } catch (Exception ex) {
            MDLog.printErrStackTrace("VideoRecordAndEditActivity", ex);
        }

        backStack = new Stack<>();

        Intent intent = getIntent();
        if (intent != null) {
            args = intent.getExtras();
        }
        if (args == null) {
            return;
        }
        //        String from = args.getString(MomentConstants.KEY_RECORD_FROM);
        String from = null;
        VideoInfoTransBean transBean = args.getParcelable(MediaConstants.EXTRA_KEY_VIDEO_TRANS_INFO);
        boolean blockBusiness = true;
        if (transBean != null) {
            blockBusiness = transBean.blockBusiness;
            from = transBean.from;
        }
        if (!SDCardUtils.checkSDCardStatus() || !SDCardUtils.hasStorageUsage(50<<20)) {
            Toaster.show("SD卡空间不足");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        setContentView(R.layout.activity_moment_record);
        if (transBean != null) {
            if (transBean.extraBundle == null) {
                transBean.extraBundle = new Bundle();
                String recordFrom = args.getString(MediaConstants.KEY_RECORD_FROM);
            }

            //onCreate时进入相册页
            if (transBean.state == VideoInfoTransBean.STATE_CHOOSE_MEDIA) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(MediaConstants.EXTRA_KEY_VIDEO_TRANS_INFO, transBean);
                showAlumFragment(bundle);
                acquireWakeLock();
                return;
            }
        }
        Video video = args.getParcelable(MediaConstants.EXTRA_KEY_VIDEO_DATA);
        //有视频，直接进入编辑页
        if (video != null) {
            //检查视频是否合法
            if (VideoUtils.getVideoMetaInfo(video)) {
                args.putBoolean(MediaConstants.KEY_JUST_EDIT, true);
                long len = video.length;
                //视频长度过长，先进裁剪页
                if (len > MediaConstants.MAX_VIDEO_DURATION) {
                    cutVideo(args, video);
                } else {
                    showEditFragment(args);
                }
            } else {
                Toaster.show("视频格式不正确");
                finish();
                return;
            }
        } else {
            showRecordFragment(args);
        }
        acquireWakeLock();
    }

    @Override
    public void onPause() {
        super.onPause();
        UIUtils.hideInputMethod(this);
    }

    private void showRecordFragment(Bundle args) {
        VideoInfoTransBean transBean = args.getParcelable(MediaConstants.EXTRA_KEY_VIDEO_TRANS_INFO);
        if (transBean != null) {
            args.putParcelable(MediaConstants.EXTRA_KEY_VIDEO_TRANS_INFO, transBean);
        }
        recordFragment = new VideoRecordFragment();
        recordFragment.setFragmentChangeListener(fragmentChangeListener);
        recordFragment.setArguments(args);

        if (!TextUtils.isEmpty(mGotoWhere) && AlbumHomeFragment.class.getSimpleName().equals(mGotoWhere)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.anim_fragment_in, R.anim.anim_fragment_out)
                    .replace(R.id.fragment_container, recordFragment, "RecordFragment")
                    .commitAllowingStateLoss();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, recordFragment, "RecordFragment")
                    .commitAllowingStateLoss();
        }
        currentFragment = recordFragment;
        state = STATE_RECORD;
    }

    private void showEditFragment(Bundle args) {
        editFragment = new VideoEditFragment();
        editFragment.setFragmentChangeListener(fragmentChangeListener);
        editFragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, editFragment, "EditFragment")
                .commitAllowingStateLoss();
        currentFragment = editFragment;
        state = STATE_EDIT;
    }

    private void cutVideo(Bundle args, Video video) {
        cutFragment = new VideoCutFragment();
        cutFragment.setFragmentChangeListener(fragmentChangeListener);
        Bundle bundle = new Bundle();
        bundle.putParcelable(VideoRecordDefs.KEY_VIDEO, video);
        cutFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, cutFragment, "CutFragment")
                .commitAllowingStateLoss();
        currentFragment = cutFragment;
        state = STATE_CUT;
    }

    private void showAlumFragment(Bundle args) {
        albumHomeFragment = new AlbumHomeFragment();
        albumHomeFragment.setArguments(args);
        albumHomeFragment.setFragmentChangeListener(fragmentChangeListener);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.anim_fragment_in, R.anim.anim_fragment_out)
                .replace(R.id.fragment_container, albumHomeFragment, "AlbumHomeFragment")
                .commitAllowingStateLoss();
        currentFragment = albumHomeFragment;
        state = STATE_IN_ALBNUM;
    }

    @Override
    public void onBackPressed() {
        if (currentFragment != null && currentFragment.isVisible()) {
            if (currentFragment.onBackPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
        if (recordFragment != null) {
            recordFragment.release();
        }
        recordFragment = null;
        editFragment = null;
        currentFragment = null;
        MomoTaskExecutor.cancleAllTasksByTag(hashCode());
        MediaSourceHelper.resetLatLon();
        VideoRecordFragment.cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_to_bottom_300ms);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (currentFragment != null) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putInt(KEY_STATE, state);
            outState.putParcelable(KEY_EXTRA, args);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            state = savedInstanceState.getInt(KEY_STATE);
            args = savedInstanceState.getParcelable(KEY_EXTRA);
        }
    }

    private PowerManager.WakeLock wakeLock;// 防止锁屏线程休眠导致视频裁剪进度停止

    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }

    }

    private FragmentChangeListener fragmentChangeListener = new FragmentChangeListener() {
        @Override
        public void change(BaseFragment old, Bundle extra) {

            if (isFinishing() || isDestroyed()) {
                return;
            }
            if (extra != null && !TextUtils.isEmpty(extra.getString(GOTO_WHERE))) {
                String gotoTemp = extra.getString(GOTO_WHERE);
                mGotoWhere = gotoTemp;
                extra.putString(GOTO_WHERE, "");
                if (TextUtils.equals(gotoTemp, BACK_TO_OLD) && (backStack == null || backStack.size() == 0)) {
                    finish();
                    return;
                }
                if (backStack != null && backStack.size() > 0 && (TextUtils.equals(gotoTemp, backStack.peek()) || TextUtils.equals(gotoTemp, BACK_TO_OLD))) {
                    gotoTemp = backStack.pop();
                } else {
                    backStack.push(old.getClass().getSimpleName());
                }

                if (TextUtils.equals(gotoTemp, VideoRecordFragment.class.getSimpleName())) {
                    showRecordFragment(extra);
                    return;
                }
                if (TextUtils.equals(gotoTemp, AlbumHomeFragment.class.getSimpleName())) {
                    showAlumFragment(extra);
                    return;
                }
                if (TextUtils.equals(gotoTemp, VideoEditFragment.class.getSimpleName())) {
                    if (old != cutFragment) {
                        showEditFragment(extra);
                        return;
                    } else {
                        int resultCode = extra.getInt(MediaConstants.KEY_RESULT_CODE);
                        if (resultCode == RESULT_OK && extra.getBoolean(MediaConstants.KEY_CUT_VIDEO_RESULT)) {
                            Video video = extra.getParcelable(MediaConstants.KEY_PICKER_VIDEO);
                            args.putParcelable(MediaConstants.EXTRA_KEY_VIDEO_DATA, video);
                            showEditFragment(args);
                            return;
                        }
                        if (resultCode == RESULT_CANCELED) {
                            finish();
                            return;
                        }
                        Toaster.show("视频格式不正确");
                        finish();
                    }
                }
            }
        }
    };

    private static VideoPickerCompressListener.OnCompressListener onCompressListener = new VideoPickerCompressListener.OnCompressListener() {
        @Override
        public void onFinish(Context context, boolean success, VideoInfoTransBean info, int requestCode) {
            if (success && info != null && context != null && info.video != null) {
                startEditForResult(context, info, requestCode);
            }
        }
    };
}
