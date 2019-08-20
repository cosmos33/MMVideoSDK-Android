package com.mm.recorduisdk.base.cement;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import static android.view.View.NO_ID;

/**
 * The fundamental unit of the adapter. It provides the view layout, the viewHolder creator,
 * the binding rules from data to viewHolder, and the diff logic.
 * <p>
 * Each model should have its own identity, defaultValue {@link #id}
 * otherwise may cause the {@link DiffUtil#calculateDiff(DiffUtil.Callback)} failed,
 * even "Inconsistency detected" exception
 */
public abstract class CementModel<T extends CementViewHolder>
        implements IDiffUtilHelper<CementModel<?>> {
    private static long idCounter = NO_ID - 1;
    /**
     * used to unique identify a model in the RecyclerView,
     * see {@link CementAdapter#getItemId(int)}
     * <p>
     * can be written by {@link #CementModel(long)} or override by {@link #id()}
     * TODO: not allow change once set
     */
    private long id;

    protected CementModel(long id) {
        this.id = id;
    }

    public CementModel() {
        this(idCounter--);
    }

    /**
     * model's own identity.
     * use {@link #id(long)} and similar methods to customize
     */
    public final long id() {
        return id;
    }

    /**
     * get the number of spans occupied by this model in the adapter
     * <p>
     * only used for {@link GridLayoutManager}
     *
     * @param totalSpanCount max span count the model can occupied
     * @param position       position in the {@link CementAdapter#models}
     * @param itemCount      total model count
     */
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return 1;
    }

    /**
     * Whether the adapter should save the state of the view bound to this model.
     */
    public boolean shouldSaveViewState() {
        return false;
    }

    @LayoutRes
    public abstract int getLayoutRes();

    int getViewType() {
        return hashInt(getLayoutRes());
    }

    /**
     * Binds the data to the viewHolder.
     */
    public void bindData(@NonNull T holder) {
    }

    /**
     * Binds the data to the viewHolder with payloads.
     *
     * @param payloads A non-null list of merged payloads. Can be empty list if requires full
     *                 update.
     * @see RecyclerView.Adapter#notifyItemChanged(int, Object)
     */
    public void bindData(@NonNull T holder, @Nullable List<Object> payloads) {
        bindData(holder);
    }

    /**
     * Subclasses can override this if their view needs to release resources when it's recycled.
     */
    public void unbind(@NonNull T holder) {
    }

    /**
     * Called when the itemView has been attached to a window.
     * <p>
     * <p>This can be used as a reasonable signal that the view is about to be seen
     * by the user. If the adapter previously freed any resources in
     * {@link CementAdapter#onViewDetachedFromWindow(RecyclerView.ViewHolder)}
     * those resources should be restored here.</p>
     *
     * @param holder Holder of the view being attached
     */
    public void attachedToWindow(@NonNull T holder) {
    }

    /**
     * Called when the itemView has been detached from its window.
     * <p>
     * <p>Becoming detached from the window is not necessarily a permanent condition;
     * the consumer of an Adapter's views may choose to cache views offscreen while they
     * are not visible, attaching and detaching them as appropriate.</p>
     *
     * @param holder Holder of the view being detached
     */
    public void detachedFromWindow(@NonNull T holder) {
    }

    /**
     * Used to create viewHolder corresponding to the {@link #getLayoutRes()}.
     */
    @NonNull
    public abstract CementAdapter.IViewHolderCreator<T> getViewHolderCreator();

    /**
     * Called to compare this model with another model, normally just compare the {@link #id()}.
     *
     * @param item another model to be compared.
     *             can be safely cast to the same class of this model
     * @return True if the two models represent the same object or false if they are different.
     * @see DiffUtil.Callback#areItemsTheSame(int, int)
     */
    @Override
    public boolean isItemTheSame(@NonNull CementModel<?> item) {
        return id() == item.id();
    }

    /**
     * Called to compare this model with another model.
     *
     * @param item another model to be compared.
     *             can be safely cast to the same class of this model
     * @return True if the contents of the models are the same or false if they are different.
     * @see DiffUtil.Callback#areContentsTheSame(int, int)
     */
    @Override
    public boolean isContentTheSame(@NonNull CementModel<?> item) {
        return true;
    }

    protected void id(long id) {
        if (id == -1) return;
        this.id = id;
    }

    /**
     * Use multiple numbers as the id for this model.
     *
     * @param ids not allow null child, if found, use old id
     */
    protected void id(Number... ids) {
        long result = 0;
        for (Number id : ids) {
            if (id == null) return;
            result = 31 * result + hashLong64Bit(id.hashCode());
        }
        id(result);
    }

    /**
     * Use two numbers as model id.
     */
    protected void id(long id1, long id2) {
        long result = hashLong64Bit(id1);
        result = 31 * result + hashLong64Bit(id2);
        id(result);
    }

    /**
     * Use a string as the model id.
     *
     * @param key not allow null
     */
    protected void id(@Nullable CharSequence key) {
        if (key == null) return;

        id(hashString64Bit(key));
    }

    /**
     * Use several strings to define the id of the model.
     *
     * @param key       not allow null
     * @param otherKeys not allow null child, if found, use old id
     */
    protected void id(@Nullable CharSequence key, CharSequence... otherKeys) {
        if (key == null) return;

        long result = hashString64Bit(key);
        for (CharSequence otherKey : otherKeys) {
            if (otherKey == null) return;
            result = 31 * result + hashString64Bit(otherKey);
        }
        id(result);
    }

    /**
     * Use a string and a number as model id.
     *
     * @param key not allow null, if found, use old id
     */
    protected void id(@Nullable CharSequence key, long id) {
        if (key == null) return;

        long result = hashString64Bit(key);
        result = 31 * result + hashLong64Bit(id);
        id(result);
    }

    /**
     * Hash a long into 64 bits instead of the normal 32. This uses a xor shift implementation to
     * attempt psuedo randomness so object ids have an even spread for less chance of collisions.
     * <p>
     * From http://stackoverflow.com/a/11554034
     * <p>
     * http://www.javamex.com/tutorials/random_numbers/xorshift.shtml
     */
    private static long hashLong64Bit(long value) {
        value ^= (value << 21);
        value ^= (value >>> 35);
        value ^= (value << 4);
        return value;
    }

    private static int hashInt(int value) {
        value ^= (value << 13);
        value ^= (value >>> 17);
        value ^= (value << 5);
        return value;
    }

    /**
     * Hash a string into 64 bits instead of the normal 32. This allows us to better use strings as a
     * model id with less chance of collisions. This uses the FNV-1a algorithm for a good mix of speed
     * and distribution.
     * <p>
     * Performance comparisons found at http://stackoverflow.com/a/1660613
     * <p>
     * Hash implementation from http://www.isthe.com/chongo/tech/comp/fnv/index.html#FNV-1a
     */
    private static long hashString64Bit(@NonNull CharSequence str) {
        long result = 0xcbf29ce484222325L;
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            result ^= str.charAt(i);
            result *= 0x100000001b3L;
        }
        return result;
    }
}
