package com.mm.sdkdemo.recorder.model;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.cement.CementAdapter;
import com.mm.sdkdemo.base.cement.CementModel;
import com.mm.sdkdemo.base.cement.CementViewHolder;

/**
 * Created by liuhuan on 2017/6/6.
 * 滤镜美颜，大眼瘦脸
 */
public class MomentFilterEditFaceItemModel extends CementModel<MomentFilterEditFaceItemModel.ViewHolder> {

    private int type;

    public MomentFilterEditFaceItemModel(int type) {
        this.type = type;

        id(this.hashCode());
    }

    public int getType() {
        return this.type;
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
        if (getType() != 0) {
            holder.editFaceText.setText(String.valueOf(getType()));
            holder.editFaceText.setBackgroundResource(R.drawable.moment_filter_edit_face_selector);
        } else {
            holder.editFaceText.setText("");
            holder.editFaceText.setBackgroundResource(R.drawable.filter_no_select);
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
            editFaceText = (TextView) itemView.findViewById(R.id.filter_edit_face_text);
        }
    }
}
