package com.mm.sdkdemo.base.cement;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.cement.eventhook.EventHook;
import com.mm.sdkdemo.base.cement.eventhook.EventHookHelper;
import com.mm.sdkdemo.base.cement.eventhook.OnClickEventHook;
import com.mm.sdkdemo.base.cement.eventhook.OnLongClickEventHook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static android.view.View.NO_ID;

/**
 * @author xudong
 * @since 2017/2/6
 */

public class CementAdapter extends RecyclerView.Adapter<CementViewHolder> {
    private static final String LOG_TAG = CementAdapter.class.getSimpleName();
    private static final String SAVED_STATE_ARG_VIEW_HOLDERS = "saved_state_view_holders";

    private final ModelList models = new ModelList();

    private final EventHookHelper eventHookHelper = new EventHookHelper(this);
    private boolean isAttached = false;

    private final LongSparseArray<CementViewHolder> boundViewHolders = new LongSparseArray<>();
    private ViewHolderState viewHolderState = new ViewHolderState();

    //<editor-fold desc="GridLayout support">
    private final GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager
            .SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            CementModel<?> model = getModel(position);
            return model != null ? model.getSpanSize(spanCount, position, getItemCount()) : 1;
        }
    };

    private int spanCount = 1;

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return spanSizeLookup;
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }
    //</editor-fold>

    protected CementAdapter() {
        setHasStableIds(true);
        spanSizeLookup.setSpanIndexCacheEnabled(true);
    }

    //<editor-fold desc="CRUD Method">
    @Nullable
    public CementModel<?> getModel(int position) {
        return position >= 0 && position < models.size() ? models.get(position) : null;
    }

    @Deprecated
    public List<CementModel<?>> getModels() {
        return models;
    }

    public boolean containsModel(CementModel<?> model) {
        return models.indexOf(model) >= 0;
    }

    protected int indexOfModel(CementModel<?> model) {
        return models.indexOf(model);
    }

    @NonNull
    protected List<CementModel<?>> getAllModelSubListAfter(@Nullable CementModel<?> model) {
        int index = models.indexOf(model);
        if (index == -1) return Collections.emptyList();
        return models.subList(index + 1, models.size());
    }

    @NonNull
    public List<CementModel<?>> getAllModelListAfter(@Nullable CementModel<?> model) {
        int index = models.indexOf(model);
        if (index == -1) return Collections.emptyList();

        return new ArrayList<>(models.subList(index + 1, models.size()));
    }

    @NonNull
    public List<CementModel<?>> getAllModelListBetween(@Nullable CementModel<?> start,
                                                       @Nullable CementModel<?> end) {
        int startIdx = models.indexOf(start),
                endIdx = models.indexOf(end);
        startIdx = startIdx == -1 ? 0 : startIdx + 1;
        endIdx = endIdx == -1 ? models.size() : endIdx;
        if (startIdx > endIdx) return Collections.emptyList();

        return new ArrayList<>(models.subList(startIdx, endIdx));
    }

    public void addModel(@NonNull CementModel<?> modelToAdd) {
        final int initialSize = models.size();

        models.add(modelToAdd);
        notifyItemInserted(initialSize);
    }

    public void addModel(int index, @NonNull CementModel<?> modelToAdd) {
        if (index > models.size() || index < 0) return;

        models.add(index, modelToAdd);
        notifyItemInserted(index);
    }

    public void addModels(@NonNull CementModel<?>... modelsToAdd) {
        addModels(Arrays.asList(modelsToAdd));
    }

    public void addModels(@NonNull Collection<? extends CementModel<?>> modelsToAdd) {
        final int initialSize = models.size();

        models.addAll(modelsToAdd);
        notifyItemRangeInserted(initialSize, modelsToAdd.size());
    }

    public void insertModelBefore(@NonNull CementModel<?> modelToInsert,
                                  @Nullable CementModel<?> modelToInsertBefore) {
        int targetIndex = models.indexOf(modelToInsertBefore);
        if (targetIndex == -1) return;

        models.add(targetIndex, modelToInsert);
        notifyItemInserted(targetIndex);
    }

    public void insertModelsBefore(@NonNull Collection<? extends CementModel<?>> modelsToInsert,
                                   @Nullable CementModel<?> modelToInsertBefore) {
        int targetIndex = models.indexOf(modelToInsertBefore);
        if (targetIndex == -1) return;

        models.addAll(targetIndex, modelsToInsert);
        notifyItemRangeInserted(targetIndex, modelsToInsert.size());
    }

    public void insertModelAfter(@NonNull CementModel<?> modelToInsert,
                                 @Nullable CementModel<?> modelToInsertAfter) {
        int modelIndex = models.indexOf(modelToInsertAfter);
        if (modelIndex == -1) return;

        int targetIndex = modelIndex + 1;

        models.add(targetIndex, modelToInsert);
        notifyItemInserted(targetIndex);
    }

    public void insertModelsAfter(@NonNull Collection<? extends CementModel<?>> modelsToInsert,
                                  @Nullable CementModel<?> modelToInsertAfter) {
        int modelIndex = models.indexOf(modelToInsertAfter);
        if (modelIndex == -1) return;

        int targetIndex = modelIndex + 1;

        models.addAll(targetIndex, modelsToInsert);
        notifyItemRangeInserted(targetIndex, modelsToInsert.size());
    }

    public void notifyModelChanged(@NonNull CementModel<?> model) {
        notifyModelChanged(model, null);
    }

    public void notifyModelChanged(@NonNull CementModel<?> model, @Nullable Object payload) {
        int index = models.indexOf(model);
        if (index != -1) {
            notifyItemChanged(index, payload);
        }
    }

    public void removeModel(@Nullable CementModel<?> modelToRemove) {
        int index = models.indexOf(modelToRemove);
        if (index >= 0 && index < models.size()) {
            models.remove(index);

            notifyItemRemoved(index);
        }
    }

    public void removeAllModels() {
        final int initialSize = models.size();

        models.clear();
        notifyItemRangeRemoved(0, initialSize);
    }

    public void removeAllAfterModel(@Nullable CementModel<?> model) {
        final int initialSize = models.size();

        List<CementModel<?>> modelsToRemove = getAllModelSubListAfter(model);
        int numModelsRemoved = modelsToRemove.size();
        if (numModelsRemoved == 0) return;

        //clear the sublist
        modelsToRemove.clear();
        notifyItemRangeRemoved(initialSize - numModelsRemoved, numModelsRemoved);
    }

    public void replaceAllModels(@NonNull final List<? extends CementModel<?>> modelsToReplace) {
        if (models.size() == 0) {
            addModels(modelsToReplace);
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                private <T> T getOrNull(@Nullable List<T> list, int index) {
                    return (list != null && index >= 0 && index < list.size())
                            ? list.get(index) : null;
                }

                @Override
                public int getOldListSize() {
                    return models.size();
                }

                @Override
                public int getNewListSize() {
                    return modelsToReplace.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    CementModel<?> oldModel = getOrNull(models, oldItemPosition),
                            newModel = getOrNull(modelsToReplace, newItemPosition);
                    return oldModel != null && newModel != null
                            && oldModel.getClass().equals(newModel.getClass())
                            && oldModel.isItemTheSame(newModel);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    CementModel<?> oldModel = getOrNull(models, oldItemPosition),
                            newModel = getOrNull(modelsToReplace, newItemPosition);
                    return oldModel != null && newModel != null
                            && oldModel.getClass().equals(newModel.getClass())
                            && oldModel.isContentTheSame(newModel);
                }
            });
            models.clear();
            models.addAll(modelsToReplace);
            result.dispatchUpdatesTo(this);
        }
    }

    public void replaceAllModels(@NonNull final List<? extends CementModel<?>> modelsToReplace, boolean detectMove) {
        if (models.size() == 0) {
            addModels(modelsToReplace);
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                private <T> T getOrNull(@Nullable List<T> list, int index) {
                    return (list != null && index >= 0 && index < list.size())
                            ? list.get(index) : null;
                }

                @Override
                public int getOldListSize() {
                    return models.size();
                }

                @Override
                public int getNewListSize() {
                    return modelsToReplace.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    CementModel<?> oldModel = getOrNull(models, oldItemPosition),
                            newModel = getOrNull(modelsToReplace, newItemPosition);
                    return oldModel != null && newModel != null
                            && oldModel.getClass().equals(newModel.getClass())
                            && oldModel.isItemTheSame(newModel);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    CementModel<?> oldModel = getOrNull(models, oldItemPosition),
                            newModel = getOrNull(modelsToReplace, newItemPosition);
                    return oldModel != null && newModel != null
                            && oldModel.getClass().equals(newModel.getClass())
                            && oldModel.isContentTheSame(newModel);
                }
            }, detectMove);
            models.clear();
            models.addAll(modelsToReplace);
            result.dispatchUpdatesTo(this);
        }
    }

    public void replaceModel(@NonNull CementModel<?> modelToReplace,
                             @NonNull CementModel<?> modelOrigin) {
        int targetIndex = models.indexOf(modelOrigin);
        if (targetIndex == -1) return;

        models.add(targetIndex, modelToReplace);
        models.remove(modelOrigin);
        notifyItemChanged(targetIndex);
    }
    //</editor-fold>

    //<editor-fold desc="Core">
    @Override
    public CementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CementViewHolder viewHolder = models.viewHolderFactory.create(viewType, parent);

        eventHookHelper.bind(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@Nullable CementViewHolder holder, int position) {
        onBindViewHolder(holder, position, Collections.emptyList());
    }

    @Override
    public void onBindViewHolder(@Nullable CementViewHolder holder, int position,
                                 @Nullable List<Object> payloads) {
        final CementModel model = getModel(position);
        if (holder == null || model == null) return;

        // A ViewHolder can be bound again even it is already bound and showing, like when it is on
        // screen and is changed. In this case we need
        // to carry the state of the previous view over to the new view. This may not be necessary if
        // the viewholder is reused (see RecyclerView.ItemAnimator#canReuseUpdatedViewHolder)
        // but we don't rely on that to be safe and to simplify
        // ??????????
        if (boundViewHolders.get(holder.getItemId()) != null) {
            viewHolderState.save(boundViewHolders.get(holder.getItemId()));
        }

        holder.bind(model, payloads);

        viewHolderState.restore(holder);
        boundViewHolders.put(holder.getItemId(), holder);
    }

    @Override
    public void onViewRecycled(@Nullable CementViewHolder holder) {
        if (holder == null) return;

        viewHolderState.save(holder);
        boundViewHolders.remove(holder.getItemId());

        holder.unbind();
    }

    @Override
    public void onViewAttachedToWindow(CementViewHolder holder) {
        final CementModel model = holder.model;
        if (model == null) return;
        //noinspection unchecked,rawtypes
        model.attachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(CementViewHolder holder) {
        final CementModel model = holder.model;
        if (model == null) return;
        //noinspection unchecked,rawtypes
        model.detachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public int getItemViewType(int position) {
        CementModel model = getModel(position);
        return model == null ? NO_ID : model.getViewType();
    }

    @Override
    public long getItemId(int position) {
        CementModel model = getModel(position);
        return model == null ? NO_ID : model.id();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        isAttached = true;
    }

    public void onSaveInstanceState(Bundle outState) {
        for (int i = 0; i < boundViewHolders.size(); i++) {
            long key = boundViewHolders.keyAt(i);
            viewHolderState.save(boundViewHolders.get(key));
        }

        if (viewHolderState.size() > 0 && !hasStableIds()) {
            throw new IllegalStateException("Must have stable ids when saving view holder state");
        }

        outState.putParcelable(SAVED_STATE_ARG_VIEW_HOLDERS, viewHolderState);
    }

    public void onRestoreInstanceState(@Nullable Bundle inState) {
        // To simplify things we enforce that state is restored before views are bound, otherwise it
        // is more difficult to update view state once they are bound
        if (boundViewHolders.size() > 0) {
            throw new IllegalStateException(
                    "State cannot be restored once views have been bound. It should be done before adding "
                            + "the adapter to the recycler view.");
        }

        if (inState != null) {
            ViewHolderState savedState = inState.getParcelable(SAVED_STATE_ARG_VIEW_HOLDERS);
            if (savedState != null) {
                viewHolderState = savedState;
            } else {
                Log.w(LOG_TAG, "can not get save viewholder state");
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="ViewHolderFactory">
    private static class ViewHolderFactory {
        private final SparseArray<Pair<Integer, IViewHolderCreator>> creatorSparseArray
                = new SparseArray<>();

        void register(@NonNull CementModel model) {
            int viewType = model.getViewType();
            if (viewType == NO_ID) {
                throw new RuntimeException("illegal viewType=" + viewType);
            }
            if (creatorSparseArray.get(viewType) == null) {
                creatorSparseArray.put(viewType,
                        Pair.create(model.getLayoutRes(), model.getViewHolderCreator()));
            }
        }

        void register(@NonNull Collection<? extends CementModel> models) {
            for (final CementModel model : models) {
                if (model == null) continue;
                register(model);
            }
        }

        CementViewHolder create(@LayoutRes int viewType, @NonNull ViewGroup parent) {
            Pair<Integer, IViewHolderCreator> info = creatorSparseArray.get(viewType);
            if (info == null) {
                throw new RuntimeException("cannot find viewHolderCreator for viewType="
                        + viewType);
            }
            try {
                return info.second.create(
                        LayoutInflater.from(parent.getContext()).inflate(info.first, parent, false));
            } catch (Exception e) {
                throw new RuntimeException("cannot inflate view="
                        + parent.getContext().getResources().getResourceName(info.first)
                        + "\nreason:" + e.getMessage(), e);
            }
        }
    }

    private static class ModelList extends ArrayList<CementModel<?>> {
        private final ViewHolderFactory viewHolderFactory = new ViewHolderFactory();

        @Override
        public boolean add(@NonNull CementModel<?> model) {
            viewHolderFactory.register(model);
            return super.add(model);
        }

        @Override
        public void add(int index, @NonNull CementModel<?> element) {
            viewHolderFactory.register(element);
            super.add(index, element);
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends CementModel<?>> c) {
            viewHolderFactory.register(c);
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, @NonNull Collection<? extends CementModel<?>> c) {
            viewHolderFactory.register(c);
            return super.addAll(index, c);
        }
    }

    /**
     * Interface definition for a callback to be invoked when creating a viewHolder
     */
    public interface IViewHolderCreator<VH extends CementViewHolder> {
        /**
         * @param view inflated view of {@link CementModel#getLayoutRes()}
         */
        @NonNull
        VH create(@NonNull View view);
    }

    public static abstract class WrapperViewHolderCreator<VH extends CementWrapperViewHolder<MVH>,
            MVH extends CementViewHolder> implements IViewHolderCreator<VH> {
        @LayoutRes
        private final int childLayoutRes;
        @NonNull
        private final IViewHolderCreator<MVH> childViewHolderCreator;

        public WrapperViewHolderCreator(@LayoutRes int childLayoutRes,
                                        @NonNull IViewHolderCreator<MVH> childViewHolderCreator) {
            this.childLayoutRes = childLayoutRes;
            this.childViewHolderCreator = childViewHolderCreator;
        }

        @NonNull
        @Override
        public VH create(@NonNull View view) {
            ViewStub viewStub = (ViewStub) view.findViewById(R.id.view_model_child_stub);
            if (viewStub == null) {
                throw new IllegalStateException("layout must have ViewStub{id=view_model_child_stub}");
            }
            viewStub.setLayoutResource(childLayoutRes);
            return create(view, childViewHolderCreator.create(viewStub.inflate()));
        }

        public abstract VH create(@NonNull View view, MVH childViewHolder);
    }
    //</editor-fold>

    //<editor-fold desc="Event Hook">

    /**
     * Register an eventHook to {@link #models}
     *
     * @throws IllegalStateException this method MUST be called before the ViewHolder is created
     */
    public <VH extends CementViewHolder> void addEventHook(
            @NonNull EventHook<VH> eventHook) {
        if (isAttached) {
            Log.w(LOG_TAG, "addEventHook is called after adapter attached");
        }
        // noinspection unchecked
        eventHookHelper.add(eventHook);
    }
    //</editor-fold>

    //<editor-fold desc="OnClickListener">
    @Nullable
    private OnItemClickListener onItemClickListener;
    @Nullable
    private EventHook<CementViewHolder> onItemClickEventHook;

    private void addOnItemClickEventHook() {
        onItemClickEventHook = new OnClickEventHook<CementViewHolder>(
                CementViewHolder.class) {
            @Override
            public void onClick(@NonNull View view, @NonNull CementViewHolder viewHolder,
                                int position, @NonNull CementModel rawModel) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(view, viewHolder, position, rawModel);
                }
            }

            @Nullable
            @Override
            public View onBind(@NonNull CementViewHolder viewHolder) {
                return viewHolder.itemView.isClickable() ? viewHolder.itemView : null;
            }
        };
        addEventHook(onItemClickEventHook);
    }

    /**
     * Register a callback to be invoked when {@link #models} are clicked.
     * If the view of this model is not clickable, it will not trigger callback.
     *
     * @throws IllegalStateException this method must be called before
     *                               {@link RecyclerView#setAdapter(RecyclerView.Adapter)}
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener) {
        if (isAttached && onItemClickEventHook == null && onItemClickListener != null) {
            throw new IllegalStateException("setOnItemClickListener " +
                    "must be called before the RecyclerView#setAdapter");
        } else if (!isAttached && onItemClickEventHook == null) {
            addOnItemClickEventHook();
        }
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Interface definition for a callback to be invoked when a model is clicked.
     */
    public interface OnItemClickListener {
        /**
         * Called when a model has been clicked.
         *
         * @param itemView   The view that was clicked.
         * @param viewHolder The viewHolder that was clicked.
         * @param position   Adapter position of the viewHolder,
         *                   see {@link RecyclerView.ViewHolder#getAdapterPosition()}.
         * @param model      The model that was clicked.
         */
        void onClick(@NonNull View itemView, @NonNull CementViewHolder viewHolder,
                     int position, @NonNull CementModel<?> model);
    }
    //</editor-fold>

    //<editor-fold desc="OnLongClickListener">
    @Nullable
    private OnItemLongClickListener onItemLongClickListener;
    @Nullable
    private EventHook<CementViewHolder> onItemLongClickEventHook;

    private void addOnItemLongClickEventHook() {
        onItemLongClickEventHook = new OnLongClickEventHook<CementViewHolder>(CementViewHolder.class) {
            @Override
            public boolean onLongClick(@NonNull View view, @NonNull CementViewHolder viewHolder,
                                       int position, @NonNull CementModel rawModel) {
                return onItemLongClickListener != null && onItemLongClickListener.onLongClick(
                        view, viewHolder, position, rawModel);
            }

            @Nullable
            @Override
            public View onBind(@NonNull CementViewHolder viewHolder) {
                return viewHolder.itemView.isClickable() ? viewHolder.itemView : null;
            }
        };
        addEventHook(onItemLongClickEventHook);
    }

    /**
     * Register a callback to be invoked when {@link #models} are clicked and held.
     * If the view of this model is not long clickable, it will not trigger callback.
     *
     * @throws IllegalStateException this method must be called before
     *                               {@link RecyclerView#setAdapter(RecyclerView.Adapter)}
     */
    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener
                                                   onItemLongClickListener) {
        if (isAttached && onItemLongClickEventHook == null && onItemLongClickListener != null) {
            throw new IllegalStateException("setOnItemLongClickListener " +
                    "must be called before the RecyclerView#setAdapter");
        } else if (!isAttached && onItemLongClickEventHook == null) {
            addOnItemLongClickEventHook();
        }
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * Interface definition for a callback to be invoked when a model has been clicked and held.
     */
    public interface OnItemLongClickListener {
        /**
         * Called when a model has been clicked and held.
         *
         * @param itemView   The view that was clicked and held.
         * @param viewHolder The viewHolder that was clicked and held.
         * @param position   Adapter position of the viewHolder,
         *                   see {@link RecyclerView.ViewHolder#getAdapterPosition()}.
         * @param model      The model that was clicked and held.
         * @return true if the callback consumed the long click, false otherwise.
         */
        boolean onLongClick(@NonNull View itemView, @NonNull CementViewHolder viewHolder,
                            int position, @NonNull CementModel<?> model);
    }
    //</editor-fold>
}
