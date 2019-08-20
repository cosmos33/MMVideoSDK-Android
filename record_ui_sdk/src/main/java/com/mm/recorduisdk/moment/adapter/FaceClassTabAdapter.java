package com.mm.recorduisdk.moment.adapter;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mm.base_business.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.bean.FaceClass;
import com.mm.recorduisdk.moment.utils.MyFaceClassHelper;
import com.mm.recorduisdk.recorder.listener.OnRecyclerItemClickListener;
import com.mm.recorduisdk.utils.BounceInAnimator;

import java.util.List;


/**
 * Created by chenxin on 2019/4/19.
 */
public class FaceClassTabAdapter extends RecyclerView.Adapter<FaceClassTabAdapter.FaceClassTabHolder> {

    //默认选中第一个
    private int selectTab = -1;
    private List<FaceClass> datas;

    private OnRecyclerItemClickListener itemClickListener;

    public FaceClassTabAdapter(List<FaceClass> classes) {
        this.datas = classes;
    }

    @Override
    public FaceClassTabAdapter.FaceClassTabHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FaceClassTabAdapter.FaceClassTabHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_face_tab, parent, false));
    }

    @Override
    public void onBindViewHolder(final FaceClassTabAdapter.FaceClassTabHolder holder, int position) {
        FaceClass faceClass = datas.get(position);
        holder.itemView.setSelected(position == selectTab);
        setTab(holder, faceClass);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                playBoundAnim(v);
                if (setSelectTab(pos) && itemClickListener != null) {
                    itemClickListener.onItemClick(v, selectTab);
                }
            }
        });
    }

    private void setTab(FaceClassTabHolder holder, FaceClass faceClass) {
        // 我的分类
        if (TextUtils.equals(faceClass.getId(), MyFaceClassHelper.MY_CATE_ID)) {
            holder.tabName.setText("我的");
            if (holder.itemView.isSelected()) {
                holder.tabName.setTextColor(UIUtils.getColor(R.color.white));
                holder.tabName.setBackground(UIUtils.getDrawable(R.drawable.bg_face_item));
            } else {
                holder.tabName.setTextColor(UIUtils.getColor(R.color.whitewith30tran));
                holder.tabName.setBackground(null);
            }
        } else {
            String tabName = faceClass.getTabName();
            if (!TextUtils.isEmpty(tabName)) {
                holder.tabName.setText(tabName);
                if (holder.itemView.isSelected()) {
                    holder.tabName.setBackground(UIUtils.getDrawable(R.drawable.bg_face_item));
                    holder.tabName.setTextColor(UIUtils.getColor(R.color.white));
                } else {
                    holder.tabName.setTextColor(UIUtils.getColor(R.color.whitewith30tran));
                    holder.tabName.setBackground(null);
                }
            }
        }
    }

    private void playBoundAnim(View view) {
        BounceInAnimator bounce = new BounceInAnimator();
        bounce.setDuration(300);
        bounce.getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f, 1.1f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f, 1.1f, 1f)
        );
        bounce.start();
    }

    public int getSelectTab() {
        return selectTab;
    }

    public boolean setSelectTab(int selectTab) {
        if (selectTab >= 0 && selectTab == this.selectTab) {
            return false;
        }
        notifyItemChanged(this.selectTab);
        notifyItemChanged(selectTab);
        this.selectTab = selectTab;
        return true;
    }

    public void setDatas(List<FaceClass> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    public void setItemClickListener(OnRecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class FaceClassTabHolder extends RecyclerView.ViewHolder {
        TextView tabName;

        public FaceClassTabHolder(View itemView) {
            super(itemView);
            tabName = (TextView) itemView.findViewById(R.id.tv_tab_name);
        }
    }
}
