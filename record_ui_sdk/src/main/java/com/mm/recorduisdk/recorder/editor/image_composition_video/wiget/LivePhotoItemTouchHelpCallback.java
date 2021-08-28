package com.mm.recorduisdk.recorder.editor.image_composition_video.wiget;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class LivePhotoItemTouchHelpCallback extends ItemTouchHelper.Callback {

    public static final int NO_MOVE = -100;

    private boolean canDrag = true;

    /**
     * Item操作的回调
     */
    private OnItemTouchCallbackListener onItemTouchCallbackListener;

    public LivePhotoItemTouchHelpCallback(@NonNull OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    public void canDrag(boolean canDrag){
        this.canDrag = canDrag;
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int dragFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (target.itemView.getTag(NO_MOVE)!=null) {
            return false;
        }
        onItemTouchCallbackListener.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }


    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return canDrag;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }


    public interface OnItemTouchCallbackListener {

        /**
         * 当两个Item位置互换的时候被回调
         *
         * @param srcPosition    拖拽的item的position
         * @param targetPosition 目的地的Item的position
         * @return 开发者处理了操作应该返回true，开发者没有处理就返回false
         */
        boolean onMove(int srcPosition, int targetPosition);
    }
}
