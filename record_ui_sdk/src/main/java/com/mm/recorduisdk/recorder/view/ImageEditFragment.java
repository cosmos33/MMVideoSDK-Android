package com.mm.recorduisdk.recorder.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mm.base_business.base.BaseFragment;
import com.mm.base_business.glide.ImageLoaderX;
import com.mm.mediasdk.IImageProcess;
import com.mm.mediasdk.MoMediaManager;
import com.mm.mediasdk.utils.ImageUtil;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.StringUtils;
import com.mm.mmutil.app.AppContext;
import com.mm.mmutil.log.Log4Android;
import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.mmutil.toast.Toaster;
import com.mm.recorduisdk.IRecordResourceConfig;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.RecordUISDK;
import com.mm.recorduisdk.bean.MMImageEditParams;
import com.mm.recorduisdk.bean.MomentSticker;
import com.mm.recorduisdk.config.Configs;
import com.mm.recorduisdk.imagecrop.ImageCropActivity;
import com.mm.recorduisdk.recorder.MediaConstants;
import com.mm.recorduisdk.recorder.helper.VideoPanelFaceAndSkinManager;
import com.mm.recorduisdk.recorder.listener.FilterSelectListener;
import com.mm.recorduisdk.recorder.listener.OnFilterDensityChangeListener;
import com.mm.recorduisdk.recorder.model.Photo;
import com.mm.recorduisdk.utils.AnimUtils;
import com.mm.recorduisdk.utils.album.AlbumConstant;
import com.mm.recorduisdk.utils.filter.FiltersManager;
import com.mm.recorduisdk.widget.MomentEdittextPannel;
import com.mm.recorduisdk.widget.MomentFilterPanelLayout;
import com.mm.recorduisdk.widget.MomentFilterPanelTabLayout;
import com.mm.recorduisdk.widget.MomentSkinAndFacePanelLayout;
import com.mm.recorduisdk.widget.MomentStickerPanel;
import com.mm.recorduisdk.widget.paint.PaintPanelView;
import com.mm.recorduisdk.widget.sticker.StickerContainerView;
import com.mm.recorduisdk.widget.sticker.StickerView;
import com.momo.mcamera.filtermanager.MMPresetFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.android.imageprocessing.FastImageGLTextureView;

/**
 * Created by huang.liangjie on 2017/6/22.
 * <p>
 * Momo Tech 2011-2017 © All Rights Reserved.
 */

public class ImageEditFragment extends BaseFragment implements View.OnClickListener {

    private static int TextPosY = 0;
    private int stickerMarginLeft = 0;
    private int stickerMarginTop = 0;
    private int stickerWidth = 0;
    private int stickerHeight = 0;

    private int mCurrentFilterSelectPosition;
    private int mCurrentBeautySelectPosition;
    private int mCurrentBigEyeSelectPosition;
    private int mCurrentSlimmingSelectPosition;
    private int mCurrentLongLegsSelectPosition;

    /**
     * 表示是否是默认的滤镜，美颜，大眼瘦脸设置
     */
    private boolean isDefaultFilter = true;
    private boolean isFromCrop = false;

    private String originPath;
    private String finishText;

    private FastImageGLTextureView fastImage;
    private StickerContainerView stickerContainer;
    private ImageView addDrawBgImage;
    private ImageView closeBtn;
    private TextView sendBtn;
    private LinearLayout toolsLayout;
    private ImageView deleteStickerIV;
    private View editProgressLayout;
    private ProgressBar editingProgressbar;
    private TextView filterTv;
    private View allStickerContainer;
    private View editTextTv, editFilterTv, editStickerTv, editPaintTv;
    private TextView editSlimmingTv;
    private Bitmap mPreviewBitmap;

    /**
     * 贴纸的View索引
     */
    private HashMap<StickerView, MomentSticker> stickerViewMap;
    /**
     * 文字贴纸view
     */
    private ArrayList<StickerView> textStickerList;

    private StickerView editingStickerView;
    private PaintPanelView paintPanel;
    private MomentStickerPanel stickerPanel;
    private MomentEdittextPannel editTextPanel;
    private MomentFilterPanelLayout filterPanel;
    private ViewGroup.MarginLayoutParams imageParams;

    private Photo image;
    private Bitmap masicBitmap;
    private IImageProcess imageProcess;
    private float bodyWrapWidth = 0f;
    private float bodyWrapLegWidth = 0f;
    private static final int sCropImageRequestCode = 991;

    private final Runnable hideFilterNameRunnable = new Runnable() {
        @Override
        public void run() {
            if (filterTv != null) {
                filterTv.setVisibility(View.INVISIBLE);
            }
        }
    };
    private TextView mTvCropImage;
    private MMImageEditParams mImageEditParams;

    @Override
    protected int getLayout() {
        return R.layout.fragment_image_edit;
    }

    @Override
    protected void initViews(View contentView) {
        initArguments();
        loadMediaInfo();
        initImageParam();
        initView();
        initLayoutParam();
        boolean isSuccess = loadData();
        if (isSuccess) {
            initEvent();
        } else {
            Toaster.show("图片加载失败");
            getActivity().finish();
        }
    }

    @Override
    protected void onLoad() {

    }

    private void initArguments() {
        Bundle args = getArguments();
        mImageEditParams = args.getParcelable(MediaConstants.KEY_IMAGE_EDIT_PARAMS);
        if (mImageEditParams == null || mImageEditParams.getPhoto() == null) {
            return;
        }
        image = mImageEditParams.getPhoto();
        originPath = StringUtils.notEmpty(image.tempPath) ? image.tempPath : image.path;
        finishText = args.getString(AlbumConstant.KEY_FINISH_TEXT);
    }

    /**
     * 根据图片尺寸计算编辑边界
     */
    private void initImageParam() {
        int width = image.width;
        int height = image.height;
        int bgWidth = UIUtils.getScreenWidth();
        int bgHeight = UIUtils.getScreenHeight();
        TextPosY = bgHeight >> 1;
        if (width / (float) height >= bgWidth / (float) bgHeight) {
            stickerWidth = bgWidth;
            float scale = bgWidth / (float) width;
            stickerHeight = (int) (height * scale);
        } else {
            stickerHeight = bgHeight;
            float scale = bgHeight / (float) height;
            stickerWidth = (int) (width * scale);
        }
        stickerMarginTop = (bgHeight - stickerHeight) / 2;
        stickerMarginLeft = (bgWidth - stickerWidth) / 2;
    }

    /**
     * 获取图片信息
     */
    private void loadMediaInfo() {
        boolean needRotate = false;

        mPreviewBitmap = com.core.glcore.util.BitmapPrivateProtocolUtil.getBitmap(originPath);
        if (mPreviewBitmap != null) {
            image.width = mPreviewBitmap.getWidth();
            image.height = mPreviewBitmap.getHeight();
            return;
        }

        try {
            ExifInterface exifInfo = new ExifInterface(originPath);
            int width = exifInfo.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
            int height = exifInfo.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
            int orientation = exifInfo.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

            if (width != 0 && height != 0) {

                needRotate = (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270);
                if (needRotate) {
                    image.width = height;
                    image.height = width;
                } else {
                    image.width = width;
                    image.height = height;
                }
            }

        } catch (Exception ex) {
            Log4Android.getInstance().e(ex);
        }

        if (image.width == 0 || image.height == 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(originPath, options);

            if (needRotate) {
                image.height = options.outWidth;
                image.width = options.outHeight;
            } else {
                image.height = options.outHeight;
                image.width = options.outWidth;
            }

        }
    }

    private void initView() {
        fastImage = findViewById(R.id.media_cover_image);
        /*if (image != null && image.width > 0 && image.height > 0
                && fastImage != null && fastImage.getHolder() != null) {
            fastImage.getHolder().setFixedSize(stickerWidth, stickerHeight);
        }*/
        allStickerContainer = findViewById(R.id.media_edit_all_sticker_container);
        stickerContainer = findViewById(R.id.media_edit_sticker_container);
        addDrawBgImage = findViewById(R.id.media_edit_draw_bg);
        closeBtn = findViewById(R.id.media_edit_btn_close);
        sendBtn = findViewById(R.id.media_edit_btn_send);
        toolsLayout = findViewById(R.id.media_edit_tools_layout);
        deleteStickerIV = findViewById(R.id.media_edit_delete_sticker);
        editProgressLayout = findViewById(R.id.media_edit_progress_layout);
        editingProgressbar = findViewById(R.id.media_edit_progresssbar);
        editTextTv = findViewById(R.id.media_edit_text_tv);
        editFilterTv = findViewById(R.id.media_edit_filter_tv);
        editStickerTv = findViewById(R.id.media_edit_sticker_tv);
        editPaintTv = findViewById(R.id.media_edit_paint_tv);
        editSlimmingTv = findViewById(R.id.media_edit_slimming_tv);
        filterTv = findViewById(R.id.filter_name_tv);
        mTvCropImage = findViewById(R.id.tv_crop_image);

        stickerContainer.deleteBtn = deleteStickerIV;

        if (!StringUtils.isEmpty(finishText)) {
            sendBtn.setText(finishText);
        }

        setupHideIfNeed();

    }

    private void setupHideIfNeed() {
        IRecordResourceConfig<List<MomentSticker>> staticStickerListConfig = RecordUISDK.getResourceGetter().getStaticStickerListConfig();
        IRecordResourceConfig<File> filtersImgHomeDirConfig = RecordUISDK.getResourceGetter().getFiltersImgHomeDirConfig();


        editStickerTv.setVisibility((staticStickerListConfig != null && staticStickerListConfig.isOpen()) ? View.VISIBLE : View.GONE);
        editFilterTv.setVisibility((filtersImgHomeDirConfig != null && filtersImgHomeDirConfig.isOpen()) ? View.VISIBLE : View.GONE);

    }

    private void initLayoutParam() {
        imageParams = new ViewGroup.MarginLayoutParams(stickerWidth, stickerHeight);
        imageParams.setMargins(stickerMarginLeft, stickerMarginTop, 0, 0);

        fastImage.setLayoutParams(new RelativeLayout.LayoutParams(imageParams));
        addDrawBgImage.setLayoutParams(new FrameLayout.LayoutParams(imageParams));

        stickerContainer.setParams(stickerWidth, stickerHeight, stickerMarginLeft, stickerMarginTop);
    }

    private void initEvent() {
        editFilterTv.setOnClickListener(this);
        editTextTv.setOnClickListener(this);
        editPaintTv.setOnClickListener(this);
        editStickerTv.setOnClickListener(this);
        editSlimmingTv.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        mTvCropImage.setOnClickListener(this);
        stickerContainer.stickerEditListener = new StickerContainerView.StickerEditListener() {
            boolean isEditing = false;
            long touchTime = 0;

            private void weakView(final View view) {
                AnimationSet set = new AnimationSet(false);
                AlphaAnimation alpha = new AlphaAnimation(1f, 0f);
                alpha.setDuration(150);
                view.startAnimation(alpha);
                set.addAnimation(alpha);
                ScaleAnimation scale = new ScaleAnimation(1f, 0f, 1f, 0f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                scale.setDuration(150);
                scale.setInterpolator(new AccelerateInterpolator());
                set.addAnimation(scale);
                view.startAnimation(set);

                view.setVisibility(View.INVISIBLE);
            }

            private void strongView(final View view) {
                AnimationSet set = new AnimationSet(false);
                AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
                alpha.setDuration(150);
                view.startAnimation(alpha);
                set.addAnimation(alpha);
                ScaleAnimation scale = new ScaleAnimation(0f, 1f, 0f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                scale.setDuration(150);
                scale.setInterpolator(new AccelerateInterpolator());
                set.addAnimation(scale);
                view.startAnimation(set);

                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTouch() {
                if (System.currentTimeMillis() - touchTime > 500) {
                    if (isFilterShow()) {
                        hideFilterPanel();
                    }
                    touchTime = System.currentTimeMillis();
                }
            }

            @Override
            public void beginEdit() {
                if (!isEditing) {
                    weakView(toolsLayout);
                }
                isEditing = true;
            }

            @Override
            public void editingStickerView(StickerView stickerView) {

            }

            @Override
            public void endEdit() {
                if (isEditing) {
                    strongView(toolsLayout);
                }
                isEditing = false;
            }

            @Override
            public void onDeleteSticker(StickerView stickerView) {
                if (stickerView.isText()) {
                    removeTextSticker(stickerView);
                } else {
                    removeSticker(stickerView);
                }
            }

            @Override
            public void onStickerClick(StickerView view) {
                if (view.isText()) {
                    editingStickerView = view;
                    String text = view.getText();
                    int index = view.getChosenTextColorIndex();
                    showEditPanel(text, index);
                }
                stickerContainer.endEditIndeed();
            }
        };
    }

    private List<MMPresetFilter> filters;

    private boolean loadData() {
        imageProcess = MoMediaManager.createImageProcessor();
        filters = FiltersManager.getAllFilters();
        imageProcess.initFilters(filters);
        boolean isSuccess;
        File file = new File(mImageEditParams.getOutputPath());
        if (mPreviewBitmap != null) {
            isSuccess = imageProcess.init(getActivity(), mPreviewBitmap, fastImage, file.getAbsolutePath());
        } else {
            isSuccess = imageProcess.init(getActivity(), originPath, fastImage, file.getAbsolutePath());
        }
        return isSuccess;
    }

    private boolean isClicking = false;

    @Override
    public void onClick(View v) {
        if (isClicking) {
            return;
        }
        isClicking = true;


        if (v == editTextTv) {
            showEditPanel(null, 0);
        } else if (v == editPaintTv) {
            showPaintPanel();
        } else if (v == editStickerTv) {
            showStickerPanel();
        } else if (v == sendBtn) {
            if (image.isTakePhoto || isEdited()) {
                send();
            } else {
                finishEdit(isFromCrop ? originPath : null);
            }
        } else if (v == closeBtn) {
            if (image.isTakePhoto || isEdited()) {
                showCloseDialog();
            } else {
                getActivity().finish();
                deleteTempFile();
            }
        } else if (v == editFilterTv) {
            showFilterPanel(MomentFilterPanelTabLayout.ON_CLICK_FILTER);
        } else if (v == editSlimmingTv) {
            showFilterPanel(MomentFilterPanelTabLayout.ON_CLICK_FACE);
        } else if (v == mTvCropImage) {
            File targetFile = new File(Configs.getDir("ProcessImage"), System.currentTimeMillis() + ".jpg");
            if (!TextUtils.isEmpty(originPath)) {
                ImageCropActivity.startImageCrop(this, originPath, targetFile.toString(), sCropImageRequestCode);
            }
        }
        MomoMainThreadExecutor.postDelayed(hashCode(), new Runnable() {
            @Override
            public void run() {
                isClicking = false;
            }
        }, 500);
    }

    private void showFilterPanel(@MomentFilterPanelTabLayout.TabSelectedPosition final int tabPanelPosition) {
        initFilterPanel();
        filterPanel.switchTabPanel(tabPanelPosition, filters, mCurrentFilterSelectPosition, mCurrentBeautySelectPosition,
                mCurrentBigEyeSelectPosition, mCurrentSlimmingSelectPosition, mCurrentLongLegsSelectPosition);

        if (filterPanel.getVisibility() != View.VISIBLE) {
            AnimUtils.Default.showFromBottom(filterPanel, 400);
        }
        AnimUtils.Default.hideToBottom(toolsLayout, false, 400);
    }

    private void initFilterPanel() {
        if (filterPanel == null) {
            ViewStub filterStub = findViewById(R.id.media_filter_stub);
            if (filterStub == null) {
                return;
            }
            filterPanel = (MomentFilterPanelLayout) filterStub.inflate();
            filterPanel.setFilterDensityChangeListener(new OnFilterDensityChangeListener() {
                @Override
                public void onFilterDensityChange(int density) {
                    imageProcess.setFilterIntensity(density / 100.0f);
                }
            });
            filterPanel.setFilterSelectListener(new FilterSelectListener() {
                @Override
                public void onFilterTabSelect(int selectPosition) {
                    imageProcess.changeToFilter(selectPosition, false, 0);
                    mCurrentFilterSelectPosition = selectPosition;
                    showFilterName(selectPosition);
                    isDefaultFilter = (isDefaultFilter && (selectPosition == 0));
                }

                @Override
                public void onBeautyTabSelect(int selectPosition, int type) {
                    if (imageProcess == null) {
                        return;
                    }
                    float[] value;
                    switch (type) {
                        case MomentFilterPanelLayout.TYPE_BEAUTY:   // 美肤 美白
                            value = VideoPanelFaceAndSkinManager.getInstance().getFaceSkinLevel(selectPosition, type);
                            imageProcess.setSkinAndLightingLevel(value[0], value[1]);
//                            imageProcess.setFaceDetectLoopCount(0);
                            mCurrentBeautySelectPosition = selectPosition;
                            break;
                        case MomentFilterPanelLayout.TYPE_EYE_AND_THIN: //  大眼 瘦脸
                            value = VideoPanelFaceAndSkinManager.getInstance().getFaceSkinLevel(selectPosition, type);
                            imageProcess.updateBigEyeAndThin(value[0], value[1]);
//                            imageProcess.setFaceDetectLoopCount(0);
                            mCurrentBigEyeSelectPosition = selectPosition;
                            break;
                        case MomentFilterPanelLayout.TYPE_SLIMMING:    // 瘦身
//                            imageProcess.setFaceDetectLoopCount(0);
                            bodyWrapWidth = VideoPanelFaceAndSkinManager.getInstance().getSlimmingAndLongLegsLevel(selectPosition, type);
                            imageProcess.updateBodyWarpAndLegLen(bodyWrapWidth, bodyWrapLegWidth);
                            mCurrentSlimmingSelectPosition = selectPosition;
                            break;
                        case MomentSkinAndFacePanelLayout.TYPE_LONG_LEGS:  // 长腿                            imageProcess.setFaceDetectLoopCount(0);
//                            imageProcess.setFaceDetectLoopCount(0);
                            bodyWrapLegWidth = VideoPanelFaceAndSkinManager.getInstance().getSlimmingAndLongLegsLevel(selectPosition, type);
                            imageProcess.updateBodyWarpAndLegLen(bodyWrapWidth, bodyWrapLegWidth);
                            mCurrentLongLegsSelectPosition = selectPosition;
                            break;
                        default:
                            break;
                    }
                    isDefaultFilter = (isDefaultFilter && (selectPosition == 0));
                }

                @Override
                public void onBeautyMoreChanged(float[] value, int type) {
                    if (imageProcess == null) {
                        return;
                    }
                    switch (type) {
                        case MomentFilterPanelLayout.TYPE_BEAUTY:   // 美肤 美白
                            imageProcess.setSkinAndLightingLevel(value[0], value[1]);
                            break;
                        case MomentFilterPanelLayout.TYPE_EYE_AND_THIN: //  大眼 瘦脸
                            imageProcess.updateBigEyeAndThin(value[0], value[1]);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isFilterShow()) {
            hideFilterPanel();
            return true;
        }

        if (editTextPanel != null && editTextPanel.getVisibility() == View.VISIBLE) {
            editTextPanel.onBackPressed();
            return true;
        }
        if (paintPanel != null && paintPanel.getVisibility() == View.VISIBLE) {
            hidePaintPanel();
            return true;
        }
        if (stickerPanel != null && stickerPanel.getVisibility() == View.VISIBLE) {
            hideStickerPanel();
            return true;
        }

        if (image.isTakePhoto || isEdited()) {
            showCloseDialog();
            return true;
        }
        deleteTempFile();
        return super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (imageProcess != null) {
            imageProcess.onResume();
        }
    }

    @Override
    public void onPause() {
        UIUtils.hideInputMethod(getActivity());
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (imageProcess != null) {
            imageProcess.release();
        }

        if (editTextPanel != null) {
            editTextPanel.release();
        }

        if (stickerViewMap != null) {
            stickerViewMap.clear();
            stickerViewMap = null;
        }

        if (textStickerList != null) {
            textStickerList.clear();
            textStickerList = null;
        }

        if (mPreviewBitmap != null) {
            mPreviewBitmap.recycle();
        }
        MomoMainThreadExecutor.cancelAllRunnables(getTaskTag());

//        deleteTempFile();

        super.onDestroy();
    }

    private void send() {
        switchEditMode(2, false);
        startImageProcess();
    }

    /**
     * path未空表示未做编辑。
     */
    private void finishEdit(String path) {
        if (StringUtils.notEmpty(path)) {
            image.tempPath = path;
            image.isCheck = true;
        }
        Intent intent = new Intent();
        intent.putExtra(MediaConstants.KEY_IMAGE_EDIT_PARAMS, mImageEditParams);
        //通知图库更新
        AppContext.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));

        getActivity().setResult(RESULT_OK, intent);
        getActivity().finish();
    }

    private void showStickerPanel() {
        if (paintPanel != null) {
            paintPanel.setVisibility(View.GONE);
        }
        switchEditMode(1, true);
        if (stickerPanel == null) {
            ViewStub stickerPanelStub = findViewById(R.id.media_edit_sticker_panel_stub);
            stickerPanel = (MomentStickerPanel) stickerPanelStub.inflate();
            stickerPanel.setOnStickerPanelListener(new MomentStickerPanel.OnStickerPanelListener() {
                @Override
                public void onCloseClicked() {
                    hideStickerPanel();
                }

                @Override
                public void onChooseSticker(final int x, final int y, final MomentSticker sticker) {
                    if (getAllStickerCount() >= MediaConstants.MAX_STICKER_COUNT) {
                        Toaster.showInvalidate("最多只能添加 " + MediaConstants.MAX_STICKER_COUNT + " 个贴纸");
                        hideStickerPanel();
                        return;
                    }

                    Bitmap bmp = ImageLoaderX.load(sticker.getPic()).loadAsync();
                    if (bmp == null || bmp.isRecycled()) {
                        return;
                    }
                    Rect viewRect = new Rect();
                    fastImage.getGlobalVisibleRect(viewRect);
                    stickerContainer.showRect = viewRect;
                    StickerView stickerView = stickerContainer.addSticker(bmp, x, y);
                    hideStickerPanel();
                    //纪录下使用的贴纸的View
                    recordSticker(stickerView, sticker);
                }
            });
        }

        //增加动画
        if (stickerPanel.getVisibility() != View.VISIBLE) {
            stickerPanel.clearAnimation();
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom);
            anim.setDuration(200);
            anim.setInterpolator(new AccelerateInterpolator());
            stickerPanel.startAnimation(anim);
            stickerPanel.setVisibility(View.VISIBLE);
        }
        stickerPanel.loadStickerData();
    }

    private void hideStickerPanel() {
        if (stickerPanel.getVisibility() != View.GONE) {
            stickerPanel.clearAnimation();
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_bottom);
            anim.setDuration(200);
            anim.setInterpolator(new AccelerateInterpolator());
            stickerPanel.startAnimation(anim);
            stickerPanel.setVisibility(View.GONE);
        }
        switchEditMode(0, false);
    }

    private void showEditPanel(String text, int index) {
        if (editTextPanel == null) {
            ViewStub mediaEditTextLayoutStub = findViewById(R.id.media_edit_text_layout_stub);
            editTextPanel = (MomentEdittextPannel) mediaEditTextLayoutStub.inflate();

            editTextPanel.setChangeTextListener(new MomentEdittextPannel.ChangeTextListener() {
                @Override
                public void onChange(Bitmap textBitmap, String text, int checkedIndex) {
                    if (textBitmap != null && StringUtils.notEmpty(text)) {
                        if (editingStickerView != null) {
                            editingStickerView.changeBitmap(textBitmap);
                            editingStickerView.setTextAndColorIndex(text, checkedIndex);
                            editingStickerView = null;
                        } else if (getAllStickerCount() >= MediaConstants.MAX_STICKER_COUNT) {
                            Toaster.showInvalidate("最多只能添加 " + MediaConstants.MAX_STICKER_COUNT + " 个贴纸");
                        } else {
                            Rect viewRect = new Rect();
                            fastImage.getGlobalVisibleRect(viewRect);
                            stickerContainer.showRect = viewRect;
                            StickerView textSticker = stickerContainer.addSticker(textBitmap, text,
                                    checkedIndex,
                                    getTextStickerXPos(textBitmap),
                                    TextPosY);
                            recordTextSticker(textSticker);
                        }
                    }
                    switchEditMode(0, true);
                }
            });
        }

        editTextPanel.setText(text);
        editTextPanel.setCheckedIndex(index);
        editTextPanel.setVisibility(View.VISIBLE);
        editTextPanel.beginEdit(getActivity());
        switchEditMode(1, true);
    }

    private void showPaintPanel() {
        switchEditMode(1, true);
        if (paintPanel == null) {
            ViewStub paintViewStub = findViewById(R.id.media_edit_paint_layout_stub);
            paintPanel = (PaintPanelView) paintViewStub.inflate();
            paintPanel.setHasMosaic(false);
            paintPanel.init();

            paintPanel.setPaintActionListener(new PaintPanelView.PaintActionListener() {
                @Override
                public void onUndo(Bitmap paintBmp, Bitmap easeBitmap) {
                    setBlendBitmap(paintBmp, easeBitmap);
                }

                @Override
                public void onFinished(Bitmap paintBmp, Bitmap easeBitmap) {
                    setBlendBitmap(paintBmp, easeBitmap);
                    switchEditMode(0, true);
                }

                @Override
                public void onDraw(Bitmap paintBmp, Bitmap easeBitmap) {
                    setBlendBitmap(paintBmp, easeBitmap);
                }
            });
        }

        paintPanel.setImageParams(new RelativeLayout.LayoutParams(imageParams));
        paintPanel.setVisibility(View.VISIBLE);
        paintPanel.bringToFront();
    }

    private void hidePaintPanel() {
        if (paintPanel != null && paintPanel.getVisibility() != View.GONE) {
            paintPanel.finishPaint();
        }
    }

    /**
     * 更新涂鸦
     */
    private void setBlendBitmap(Bitmap bitmap, Bitmap easeBitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            addDrawBgImage.setImageBitmap(bitmap);
        }

        masicBitmap = easeBitmap;
    }

    private void hideFilterPanel() {
        AnimUtils.Default.showFromBottom(toolsLayout, 400);

        if (filterPanel != null) {
            AnimUtils.Default.hideToBottom(filterPanel, true, 400);
        }
    }

    private boolean isFilterShow() {
        return filterPanel != null && filterPanel.getVisibility() == View.VISIBLE;
    }

    /**
     * 切换默认模式和各种编辑状态
     */
    private void switchEditMode(int stickMode, boolean anim) {
        switch (stickMode) {
            case 0:
                toolsLayout.clearAnimation();
                toolsLayout.startAnimation(getShowAnim());
                toolsLayout.setVisibility(View.VISIBLE);

                if (paintPanel != null) {
                    paintPanel.setVisibility(View.GONE);
                }

                closeBtn.setVisibility(View.VISIBLE);
                sendBtn.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (anim) {
                    toolsLayout.clearAnimation();
                    toolsLayout.startAnimation(getHideAnim());
                    toolsLayout.setVisibility(View.INVISIBLE);
                }
                closeBtn.setVisibility(View.GONE);
                sendBtn.setVisibility(View.GONE);
                break;
            case 2:
                toolsLayout.clearAnimation();
                toolsLayout.setVisibility(View.GONE);
                closeBtn.setVisibility(View.GONE);
                sendBtn.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private Animation getShowAnim() {
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator());
        return anim;
    }

    private Animation getHideAnim() {
        AlphaAnimation anim = new AlphaAnimation(1f, 0f);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator());
        return anim;
    }

    /**
     * 记录一次文字贴纸
     */
    private void recordTextSticker(StickerView view) {
        if (textStickerList == null) {
            textStickerList = new ArrayList<>();
        }
        if (!textStickerList.contains(view)) {
            textStickerList.add(view);
        }
    }

    /**
     * 回退一次文字贴纸
     */
    private void removeTextSticker(StickerView view) {
        if (textStickerList != null) {
            textStickerList.remove(view);
        }
    }

    private void recordSticker(StickerView view, MomentSticker sticker) {
        if (stickerViewMap == null) {
            stickerViewMap = new HashMap<>();
        }
        if (!stickerViewMap.containsKey(view)) {
            stickerViewMap.put(view, sticker);
        }
    }

    private void removeSticker(StickerView view) {
        if (view == null) {
            return;
        }

        if (stickerViewMap != null) {
            stickerViewMap.remove(view);
        }
    }

    private int getAllStickerCount() {
        final int tc = textStickerList != null ? textStickerList.size() : 0;
        final int tv = stickerViewMap != null ? stickerViewMap.size() : 0;
        return tc + tv;
    }

    /**
     * 计算贴纸水平位移
     */
    private int getTextStickerXPos(Bitmap bitmap) {
        final int w = bitmap.getWidth();
        final int sw = UIUtils.getScreenWidth();
        return (sw - w) >> 1;
    }

    private void showFilterName(int filterIndex) {
        String filterName = "";
        if (filterIndex > -1) {
            filterName = filters.get(filterIndex).getFilterName();
        } else if (filterIndex == -1) {
            filterName = "原图";
        }
        if (StringUtils.notEmpty(filterName)) {
            filterTv.setText(filterName);
            MomoMainThreadExecutor.cancelSpecificRunnable(getTaskTag(), hideFilterNameRunnable);
            MomoMainThreadExecutor.post(getTaskTag(), new Runnable() {
                @Override
                public void run() {
                    filterTv.setVisibility(View.VISIBLE);
                }
            });
            MomoMainThreadExecutor.postDelayed(getTaskTag(), hideFilterNameRunnable, 1500);
        }
    }

    private void startImageProcess() {
        if (imageProcess.isProcessing()) {
            return;
        }
        imageProcess.setImageProcessListener(new IImageProcess.ImageProcessListener() {
            @Override
            public void onProcessCompleted(final String path) {
                MomoMainThreadExecutor.post(getTaskTag(), new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        finishEdit(path);
                    }
                });
            }

            @Override
            public void onProcessFailed() {
                MomoMainThreadExecutor.post(getTaskTag(), new Runnable() {
                    @Override
                    public void run() {
                        Toaster.show("合成失败");
                    }
                });

            }
        });

        showProgress();

        Bitmap blendBitmap = null;
        if (allStickerContainer != null && stickerContainer != null) {
            blendBitmap = ImageUtil.createBitmapByView(allStickerContainer,
                    stickerWidth,
                    stickerHeight,
                    stickerMarginLeft,
                    stickerMarginTop);
        }

        imageProcess.startImageProcess(blendBitmap, masicBitmap, stickerWidth, stickerHeight);
    }

    private void showProgress() {
        editProgressLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        editProgressLayout.setVisibility(View.GONE);
    }

    private boolean isEdited() {
        boolean isPainted = false;
        if (paintPanel != null && paintPanel.canUndo()) {
            isPainted = true;
        }
        return (getAllStickerCount() > 0) || !isDefaultFilter || isPainted;
    }

    private void showCloseDialog() {
        AlertDialog mCloseDialog = new AlertDialog.Builder(getContext()).create();
        mCloseDialog.setTitle("提示");
        if (image.isTakePhoto) {
            mCloseDialog.setMessage("要放弃该图片吗？");
        } else {
            mCloseDialog.setMessage("要放弃修改该图片吗？");
        }
        mCloseDialog.setButton(DialogInterface.BUTTON_POSITIVE, "放弃", new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
                deleteTempFile();
            }
        });
        mCloseDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        showDialog(mCloseDialog);
    }

    private void deleteTempFile() {
        if (isFromCrop) {
            try {
                File deleteFile = new File(originPath);
                if (deleteFile.exists()) {
                    deleteFile.delete();
                }
            } catch (Exception ex) {
                Log4Android.getInstance().e(ex);
            }
        }
    }

    @Override
    protected void onActivityResultReceived(int requestCode, int resultCode, Intent data) {
        super.onActivityResultReceived(requestCode, resultCode, data);

        if (requestCode == sCropImageRequestCode && resultCode == RESULT_OK && data != null) {
            String cropImagePath = data.getStringExtra(ImageCropActivity.TargetImagePathKey);
            if (!TextUtils.isEmpty(cropImagePath) && new File(cropImagePath).exists()) {
                isFromCrop = true;
                originPath = cropImagePath;
                Bitmap previewBitmap = BitmapFactory.decodeFile(originPath);
                image.width = previewBitmap.getWidth();
                image.height = previewBitmap.getHeight();
                initImageParam();
//                initView();
                initLayoutParam();
                imageProcess.updateSourceImage(previewBitmap);
            }
        }
    }

    public Object getTaskTag() {
        return this.getClass().getName() + '@' + Integer.toHexString(this.hashCode());
    }

}
