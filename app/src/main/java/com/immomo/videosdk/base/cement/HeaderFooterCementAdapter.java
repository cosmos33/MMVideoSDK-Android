package com.immomo.videosdk.base.cement;

import android.view.View;
import android.widget.TextView;

import com.immomo.videosdk.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * including header, data, loadMore, footer, emptyView part
 * <p>
 * normal order [header, data, loadMore, footer]
 * empty data set [header, emptyView, footer]
 *
 * @author xudong
 * @since 2017/2/9
 */
public abstract class HeaderFooterCementAdapter<T> extends CementAdapter {
    @NonNull
    private final OrderedMap<Long, CementModel<?>> headers = new OrderedMap<>(),
            footers = new OrderedMap<>();
    @NonNull
    protected final List<T> dataList = new ArrayList<>();

    private boolean hasMore = false;
    @NonNull
    private CementLoadMoreModel<?> loadMoreModel = new LoadMoreModel();

    @Nullable
    private CementModel<?> emptyViewModel = null;

    @Nullable
    private CementModel<?> getLastHeader() {
        return headers.getLastOrNull();
    }

    @Nullable
    private CementModel<?> getLoadMoreOrFirstFooter() {
        return hasMore ? loadMoreModel : footers.getFirstOrNull();
    }

    //<editor-fold desc="Headers">
    @NonNull
    public final Collection<? extends CementModel<?>> getHeaders() {
        return headers.values();
    }

    /**
     * @throws IllegalStateException if headers is corrupted
     */
    public final <M extends CementModel> boolean addHeader(@NonNull M model) {
        if (headers.checkExistAndConsistency(model.id())) {
            return false;
        }

        addModel(headers.size(), model);
        headers.put(model.id(), model);
        return true;
    }

    public final <M extends CementModel> boolean removeHeader(@NonNull M model) {
        if (!headers.checkExistAndConsistency(model.id())) {
            return false;
        }

        removeModel(model);
        headers.remove(model.id());
        return true;
    }

    public final boolean clearHeaders() {
        for (CementModel<?> model : getHeaders()) {
            removeHeader(model);
        }
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="Footers">
    @NonNull
    public final Collection<? extends CementModel<?>> getFooters() {
        return footers.values();
    }

    /**
     * @throws IllegalStateException if footers is corrupted
     */
    public final <M extends CementModel> boolean addFooter(@NonNull M model) {
        if (footers.checkExistAndConsistency(model.id())) {
            return false;
        }

        addModel(getItemCount(), model);
        footers.put(model.id(), model);
        return true;
    }

    public final <M extends CementModel> boolean removeFooter(@NonNull M model) {
        if (!footers.checkExistAndConsistency(model.id())) {
            return false;
        }

        removeModel(model);
        footers.remove(model.id());
        return true;
    }

    public final boolean clearFooters() {
        for (CementModel<?> model : getFooters()) {
            removeFooter(model);
        }
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="EmptyView">

    /**
     * get data models size
     * derived class can override this logic
     */
    protected boolean isDataListEmpty() {
        return dataList.isEmpty();
    }

    /**
     * set empty view model and remove previous model if exists
     */
    public final void setEmptyViewModel(@Nullable CementModel<?> emptyViewModel) {
        if (this.emptyViewModel == emptyViewModel) return;

        if (this.emptyViewModel != null) {
            removeModel(this.emptyViewModel);
        }
        this.emptyViewModel = emptyViewModel;
    }

    /**
     * when dataList.isEmpty(), show empty view model; otherwise, remove it
     */
    public void checkEmptyView() {
        if (isDataListEmpty()) {
            if (emptyViewModel != null && !containsModel(emptyViewModel)) {
                addModel(headers.size(), emptyViewModel);
            }
        } else {
            removeModel(emptyViewModel);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Data Models">

    /**
     * convert {@link T} data to
     * {@link CementModel}
     */
    @NonNull
    abstract Collection<? extends CementModel<?>> transData(@NonNull T data);

    @NonNull
    protected Collection<? extends CementModel<?>> transDataList(
            @NonNull Collection<T> dataList) {
        List<CementModel<?>> dataModels = new ArrayList<>();
        for (T data : dataList) {
            dataModels.addAll(transData(data));
        }
        return dataModels;
    }

    @NonNull
    public final List<T> getDataList() {
        return dataList;
    }

    /**
     * get all models excluding header,footer,loadMore,emptyView
     */
    @NonNull
    public final List<? extends CementModel<?>> getDataModels() {
        if (isDataListEmpty()) {
            return Collections.emptyList();
        }
        return getAllModelListBetween(getLastHeader(), getLoadMoreOrFirstFooter());
    }
    //</editor-fold>

    //<editor-fold desc="Data Models[Add]">
    private void addDataModels(@NonNull Collection<? extends CementModel<?>> dataModels) {
        CementModel lastModel = getLoadMoreOrFirstFooter();
        if (lastModel == null) {
            addModels(dataModels);
        } else {
            insertModelsBefore(dataModels, lastModel);
        }
    }

    public final void addData(@NonNull T data) {
        addDataModels(transData(data));
        dataList.add(data);

        checkEmptyView();
    }

    public final void addDataList(@NonNull T... dataListToAdd) {
        addDataList(Arrays.asList(dataListToAdd), hasMore);
    }

    public final void addDataList(@NonNull Collection<T> dataListToAdd) {
        addDataList(dataListToAdd, hasMore);
    }

    public final void addDataList(@NonNull Collection<T> dataListToAdd, boolean hasMore) {
        setHasMore(hasMore);
        addDataModels(transDataList(dataListToAdd));
        dataList.addAll(dataListToAdd);

        checkEmptyView();
    }
    //</editor-fold>

    //<editor-fold desc="Data Models[Update]">

    /**
     * derived class can override this logic
     */
    protected void replaceAllDataModels() {
        List<CementModel<?>> newModels = new ArrayList<>();
        newModels.addAll(headers.values());
        if (isDataListEmpty() && emptyViewModel != null) {
            newModels.add(emptyViewModel);
        } else {
            newModels.addAll(transDataList(dataList));
            if (hasMore) newModels.add(loadMoreModel);
        }
        newModels.addAll(footers.values());

        replaceAllModels(newModels);
    }

    /**
     * derived class can override this logic for optimization
     */
    public abstract void notifyDataChanged(@NonNull T data);

    public final void updateDataList(@NonNull Collection<T> newDataList) {
        updateDataList(newDataList, hasMore);
    }

    public final void updateDataList(@NonNull Collection<T> newDataList, boolean hasMore) {
        this.hasMore = hasMore;
        if (!hasMore) {
            loadMoreModel.setState(CementLoadMoreModel.COMPLETE);
        }

        dataList.clear();
        dataList.addAll(newDataList);

        replaceAllDataModels();
    }
    //</editor-fold>

    //<editor-fold desc="Data Models[Delete]">
    public void clearData(boolean hasMore) {
        updateDataList(Collections.<T>emptyList(), hasMore);
    }

    public void clearData() {
        clearData(hasMore);
    }

    /**
     * derived class can override this logic for optimization
     */
    public void removeData(@NonNull T data) {
        if (dataList.remove(data)) {
            replaceAllDataModels();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Load More">
    public boolean hasMore() {
        return hasMore;
    }

    public final void setHasMore(boolean hasMore) {
        if (this.hasMore == hasMore) return;

        this.hasMore = hasMore;
        if (hasMore) {
            //show
            if (footers.size() == 0) {
                addModels(loadMoreModel);
            } else {
                insertModelBefore(loadMoreModel, footers.getFirstOrNull());
            }
        } else {
            //hide
            loadMoreModel.setState(CementLoadMoreModel.COMPLETE);
            removeModel(loadMoreModel);
        }
    }

    public final void setLoadMoreModel(@NonNull CementLoadMoreModel<?> loadMoreModel) {
        this.loadMoreModel = loadMoreModel;
    }

    public final void setLoadMoreState(@CementLoadMoreModel.LoadMoreState int state) {
        if (!hasMore) return;

        loadMoreModel.setState(state);
        notifyModelChanged(loadMoreModel);
    }

    public final int getLoadMorePosition() {
        return indexOfModel(loadMoreModel);
    }
    //</editor-fold>

    private static class LoadMoreModel extends CementLoadMoreModel<LoadMoreModel.ViewHolder> {
        @Override
        public int getLayoutRes() {
            return R.layout.layout_empty_view_model;
        }

        @Override
        public void onLoadMoreStart(@NonNull ViewHolder holder) {
            holder.title.setText("loading...");
        }

        @Override
        public void onLoadMoreComplete(@NonNull ViewHolder holder) {
            holder.title.setText("click to load");
        }

        @Override
        public void onLoadMoreFailed(@NonNull ViewHolder holder) {
            holder.title.setText("click to retry");
        }

        @NonNull
        @Override
        public IViewHolderCreator<ViewHolder> getViewHolderCreator() {
            return new IViewHolderCreator<ViewHolder>() {
                @NonNull
                @Override
                public ViewHolder create(@NonNull View view) {
                    return new ViewHolder(view);
                }
            };
        }

        public class ViewHolder extends CementViewHolder {
            private TextView title;

            public ViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.section_title);
            }
        }
    }

    private class OrderedMap<K, V> implements Iterable<V> {
        private HashMap<K, V> map = new HashMap<>();
        private List<K> orderList = new ArrayList<>();

        /**
         * @throws IllegalStateException if data is corrupted
         */
        boolean checkExistAndConsistency(@Nullable K key) {
            boolean inMap = map.containsKey(key),
                    inOrder = orderList.contains(key);
            if (inMap ^ inOrder) {
                throw new IllegalStateException("inconsistent key=" + key);
            }
            return inMap & inOrder;
        }

        @Nullable
        public synchronized V get(@NonNull K key) {
            return checkExistAndConsistency(key) ? map.get(key) : null;
        }

        @Nullable
        public synchronized V put(@NonNull K key, @NonNull V value) {
            if (!checkExistAndConsistency(key)) {
                map.put(key, value);
                orderList.add(key);
            }
            return null;
        }

        @Nullable
        public synchronized V remove(@NonNull K key) {
            if (checkExistAndConsistency(key)) {
                map.remove(key);
                orderList.remove(key);
            }
            return null;
        }

        public int size() {
            return orderList.size();
        }

        public Collection<V> values() {
            Collection<V> values = new ArrayList<>();
            for (K key : orderList) {
                values.add(map.get(key));
            }
            return values;
        }

        public V getFirstOrNull() {
            return orderList.size() == 0 ? null : map.get(orderList.get(0));
        }

        public V getLastOrNull() {
            return orderList.size() == 0 ? null : map.get(orderList.get(orderList.size() - 1));
        }

        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < orderList.size();
                }

                @Override
                public V next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return map.get(orderList.get(index++));
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}
