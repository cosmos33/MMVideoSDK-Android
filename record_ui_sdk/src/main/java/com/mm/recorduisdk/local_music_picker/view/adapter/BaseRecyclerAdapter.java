package com.mm.recorduisdk.local_music_picker.view.adapter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * Created by XK on 16/5/30.
 * <p>
 * recyclerView adapter基类
 */
public abstract class BaseRecyclerAdapter<T, VH extends BaseRecyclerAdapter.BaseViewHolder> extends RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder> {

    protected List<T> mData;

    protected Context mContext;

    protected OnItemClickListener onItemClickListener;

    protected OnItemLongClickListener onItemLongClickListener;

    protected OnItemButtonClickListener onItemButtonClickListener;

    public BaseRecyclerAdapter(Context context, List<T> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
        if (onItemClickListener != null && needSetItemClickListener(holder, position)) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
        if (onItemLongClickListener != null && needSetItemLongClickListener(holder, position)) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onItemLongClickListener.onLongClick(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
        if (mData != null && mData.size() > position) {
            T data = mData.get(position);
            setViews((VH) holder, data, position);
        }
    }

    protected boolean needSetItemClickListener(BaseViewHolder holder, int position) {
        return true;
    }

    protected boolean needSetItemLongClickListener(BaseViewHolder holder, int position) {
        return true;
    }

    /**
     * {@link #onBindViewHolder(BaseViewHolder, int)}后调用，设置view
     *
     * @param holder   view holder
     * @param data     数据
     * @param position 位置
     */
    protected abstract void setViews(VH holder, T data, int position);

    /**
     * 设置view的点击事件
     *
     * @param listener 点击事件
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    /**
     * 设置view长按事件
     *
     * @param listener 长按事件
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
    }

    /**
     * 设置view内部button的点击监听
     *
     * @param listener 点击事件
     */
    public void setOnItemButtonClickListener(OnItemButtonClickListener listener) {
        onItemButtonClickListener = listener;
    }

    /**
     * 获取整个data
     *
     * @return mData
     */
    public List<T> getData() {
        return mData;
    }

    /**
     * 在结尾处添加数据
     *
     * @param data 数据
     */
    public void add(T data) {
        insert(mData.size(), data);
    }

    /**
     * 在position处插入数据时
     *
     * @param position 位置 0 ~ mData.size()
     * @param data     数据
     */
    public void insert(int position, T data) {
        mData.add(position, data);
        notifyItemInserted(position);
    }

    /**
     * 在结尾处添加数据
     *
     * @param data 数据集
     */
    public void addAll(List<T> data) {
        insertAll(mData.size(), data);
    }

    /**
     * 在position位置添加数据集
     *
     * @param position 位置 0 ~ mData.size()
     * @param data     数据集
     */
    public void insertAll(int position, List<T> data) {
        mData.addAll(position, data);
        notifyItemRangeInserted(position, data.size());
    }

    /**
     * 替换全部数据
     *
     * @param data
     */
    public void replace(List<T> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 删除position位置的数据
     *
     * @param position 位置
     * @return 删除的数据
     */
    public T remove(int position) {
        T d = mData.remove(position);
        notifyItemRemoved(position);
        return d;
    }

    /**
     * 获取item位置
     * @param item
     * @return
     */
    public int getItemPosition(T item) {
        if (mData != null && mData.size() > 0) {
            for (int i = 0, l = mData.size(); i < l; i++) {
                T d = mData.get(i);
                if (d != null && d.equals(item))
                    return i;
            }
        }
        return -1;
    }

    /**
     * 删除data
     * @param data
     * @return
     */
    public boolean remove(T data) {
        final int pos = getItemPosition(data);
        boolean result = mData.remove(data);
        if (result)
            notifyItemRemoved(pos);
        return result;
    }

    /**
     * 获取某个位置的数据
     *
     * @param position 位置
     * @return 数据 没有返回null
     */
    public T getItemInPosition(int position) {
        if (mData != null && mData.size() > position) return mData.get(position);
        return null;
    }

    /**
     * view holder类
     */
    public abstract static class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
            setView();
        }

        /**
         * 查找并赋值
         */
        protected abstract void setView();

        /**
         * 根据id查找view
         *
         * @param id  查找的id
         * @param <T> 类型
         * @return 查找到的view
         */
        protected <T extends View> T findViewById(int id) {
            return (T) itemView.findViewById(id);
        }
    }
}
