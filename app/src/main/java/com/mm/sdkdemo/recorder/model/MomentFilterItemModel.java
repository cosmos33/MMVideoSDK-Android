package com.mm.sdkdemo.recorder.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.cement.CementAdapter;
import com.mm.sdkdemo.base.cement.CementModel;
import com.mm.sdkdemo.base.cement.CementViewHolder;
import com.momo.mcamera.filtermanager.MMPresetFilter;

/**
 * Created by liuhuan on 2017/6/6.
 */
public class MomentFilterItemModel extends CementModel<MomentFilterItemModel.ViewHolder> {

    //    private MomentFilterBean momentFilter;
    boolean isShowBg = false;

    private final MMPresetFilter momentFilter;

    public MomentFilterItemModel(MMPresetFilter momentFilterBean) {
        this.momentFilter = momentFilterBean;
        id(momentFilter.hashCode());
    }

    public MMPresetFilter getMomentFilter() {
        return this.momentFilter;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.moment_filter_list_item;
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
    public void bindData(@NonNull ViewHolder holder) {
        super.bindData(holder);
        //标签
        String tag = momentFilter.getTag();
        if (!TextUtils.isEmpty(tag)) {
            holder.filterTag.setVisibility(View.VISIBLE);
            holder.filterTag.setText(tag);
        } else {
            holder.filterTag.setVisibility(View.GONE);
        }

        if (isShowBg) {
            holder.filterBg.setVisibility(View.VISIBLE);
        } else {
            holder.filterBg.setVisibility(View.INVISIBLE);
        }
        //加载icon
//        if (momentFilter.isLocal()) {
//            ImageLoaderX.loadWithReset(momentFilter.getIconUrl()).into(holder.filterItemImg);
//        } else {
//            ImageLoaderX.loadWithReset(momentFilter.getIconUrl()).into(holder.filterItemImg);
//        }
        if (!TextUtils.isEmpty(momentFilter.getName())) {
            holder.filteeItemText.setText(momentFilter.getName());
        }
    }

    @Override
    public boolean isItemTheSame(@NonNull CementModel<?> item) {
        return super.isItemTheSame(item);
    }

    public static class ViewHolder extends CementViewHolder {
        private ImageView filterItemImg;
        private TextView filteeItemText, filterTag;
        private View filterBg;

        public ViewHolder(View itemView) {
            super(itemView);
            filterItemImg = (ImageView) itemView.findViewById(R.id.moment_filter_item_img);
            filteeItemText = (TextView) itemView.findViewById(R.id.moment_filter_item_text);
            filterTag = (TextView) itemView.findViewById(R.id.moment_filter_tag);
            filterBg = itemView.findViewById(R.id.moment_filter_item_bg);
        }
    }

    public void showFilterBg(boolean isShowBg) {
        this.isShowBg = isShowBg;
    }

}
