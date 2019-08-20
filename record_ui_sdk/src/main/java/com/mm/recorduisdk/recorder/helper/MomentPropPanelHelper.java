package com.mm.recorduisdk.recorder.helper;

import android.view.View;
import android.view.ViewStub;

import com.mm.recorduisdk.R;
import com.mm.recorduisdk.moment.MomentFacePanelElement;
import com.mm.recorduisdk.utils.AnimUtils;


/**
 * Created by chenxin on 2019/4/16.
 */
public class MomentPropPanelHelper {

    private MomentFacePanelElement mFacePanelElement;
    // 变脸面板ViewStub对象
    private ViewStub mFacePannelViewStub;

    private View contentView;

    private OnClickListener listener;

    public void initView(ViewStub viewStub) {
        View view = viewStub.inflate();
        contentView = view.findViewById(R.id.contentRoot);
        contentView.setVisibility(View.GONE);
        mFacePannelViewStub = view.findViewById(R.id.record_face_viewstub);
        mFacePanelElement = new MomentFacePanelElement(mFacePannelViewStub);
        view.findViewById(R.id.video_mini_face_record_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRecordClick();
                }
            }
        });
        view.findViewById(R.id.prop_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onResetClick();
                }
            }
        });

    }

    public void show() {
        contentView.setVisibility(View.VISIBLE);
        mFacePanelElement.show(false);
        AnimUtils.Default.showFromBottom(contentView, 400);
    }

    public void hide() {
        contentView.setVisibility(View.GONE);
        AnimUtils.Default.hideToBottom(contentView, true, 400);

    }

    public boolean isShown() {
        return contentView.getVisibility() == View.VISIBLE;
    }

    public MomentFacePanelElement getFacePanelElement() {
        return mFacePanelElement;
    }

    public void setOnResetClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void onResetClick();

        void onRecordClick();
    }

}
