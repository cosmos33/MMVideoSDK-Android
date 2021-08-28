package com.mm.recorduisdk.recorder.model;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;
import com.mm.recorduisdk.recorder.helper.VideoPanelFaceAndSkinManager;
import com.mm.recorduisdk.widget.BeautyAdapterData;

/**
 * Created by liuhuan on 2017/6/6.
 * 滤镜美颜，大眼瘦脸
 */
public class MomentFilterEditFaceItemModel extends CementModel<MomentFilterEditFaceItemModel.ViewHolder> {

    private BeautyAdapterData data;

    public MomentFilterEditFaceItemModel(BeautyAdapterData data) {
        this.data = data;

        id(this.hashCode());
    }

    public BeautyAdapterData getData() {
        return this.data;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.moment_filter_edit_face_list_item;
    }

    @NonNull
    @Override
    public CementAdapter.IViewHolderCreator getViewHolderCreator() {
        return new CementAdapter.IViewHolderCreator<ViewHolder>() {
            @NonNull
            @Override
            public ViewHolder create(@NonNull View view) {
                return new ViewHolder(view);
            }
        };
    }

    @Override
    public void bindData(@NonNull final ViewHolder holder) {
        super.bindData(holder);
        if (getData().beautyType == null) {
            if (!"0".equals(getData())) {
                holder.editFaceText.setText(getData().content);
                holder.editFaceText.setBackgroundResource(R.drawable.moment_filter_edit_face_selector);
            } else {
                holder.editFaceText.setText("");
                holder.editFaceText.setBackgroundResource(R.drawable.filter_no_select);
            }
        } else {
            switch (getData().beautyType) {
                case VideoPanelFaceAndSkinManager.TYPE_MICRO:
                    holder.editFaceText.setText(getData().content);
                    holder.editFaceText.setBackgroundResource(R.drawable.moment_filter_edit_face_selector);
                    break;
                case VideoPanelFaceAndSkinManager.TYPE_MAKEUP:
                    holder.editFaceText.setText(getData().content);
                    break;
                default:
            }
        }
        if (isShowBg) {
            holder.editFaceText.setSelected(true);
        } else {
            holder.editFaceText.setSelected(false);
        }
    }

    boolean isShowBg = false;

    public void showFilterBg(boolean isShowBg) {
        this.isShowBg = isShowBg;
    }

    @Override
    public boolean isItemTheSame(@NonNull CementModel<?> item) {
        return super.isItemTheSame(item);
    }

    public class ViewHolder extends CementViewHolder {
        private TextView editFaceText;

        public ViewHolder(View itemView) {
            super(itemView);
            editFaceText = itemView.findViewById(R.id.filter_edit_face_text);
        }
    }
}
