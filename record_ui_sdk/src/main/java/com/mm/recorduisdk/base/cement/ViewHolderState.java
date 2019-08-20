package com.mm.recorduisdk.base.cement;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.util.LongSparseArray;
import android.util.SparseArray;
import android.view.View;

import com.mm.recorduisdk.R;

/**
 * @author xudong
 * @since 2017/2/8
 */

class ViewHolderState extends LongSparseArray<ViewHolderState.ViewState> implements Parcelable {
    public ViewHolderState() {
    }

    public ViewHolderState(int initialCapacity) {
        super(initialCapacity);
    }

    public void save(CementViewHolder holder) {
        if (!holder.shouldSaveViewState()) return;

        ViewState state = get(holder.getItemId());
        if (state == null) {
            state = new ViewState();
        }
        state.save(holder.itemView);
        put(holder.getItemId(), state);
    }

    public void restore(CementViewHolder holder) {
        if (!holder.shouldSaveViewState()) return;

        ViewState state = get(holder.getItemId());
        if (state != null) {
            state.restore(holder.itemView);
        }
    }

    //<editor-fold desc="Parcelable">
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final int size = size();
        dest.writeInt(size);
        for (int i = 0; i < size; i++) {
            dest.writeLong(keyAt(i));
            dest.writeParcelable(valueAt(i), 0);
        }
    }

    public static final Creator<ViewHolderState> CREATOR =
            new Creator<ViewHolderState>() {
                @Override
                public ViewHolderState createFromParcel(Parcel source) {
                    int size = source.readInt();
                    ViewHolderState state = new ViewHolderState(size);

                    for (int i = 0; i < size; i++) {
                        long key = source.readLong();
                        ViewState value = source.readParcelable(ViewState.class.getClassLoader());
                        state.put(key, value);
                    }

                    return state;
                }

                @Override
                public ViewHolderState[] newArray(int size) {
                    return new ViewHolderState[size];
                }
            };
    //</editor-fold>

    /**
     * A wrapper around a sparse array as a helper to save the state of a view. This also adds
     * parcelable support.
     */
    public static class ViewState extends SparseArray<Parcelable> implements Parcelable {

        public ViewState() {
        }

        private ViewState(int size, int[] keys, Parcelable[] values) {
            super(size);
            for (int i = 0; i < size; ++i) {
                put(keys[i], values[i]);
            }
        }

        public void save(View view) {
            int originalId = view.getId();
            setIdIfNoneExists(view);

            view.saveHierarchyState(this);
            view.setId(originalId);
        }

        public void restore(View view) {
            int originalId = view.getId();
            setIdIfNoneExists(view);

            view.restoreHierarchyState(this);
            view.setId(originalId);
        }

        /**
         * If a view hasn't had an id set we need to set a temporary one in order to save state, since a
         * view won't save its state unless it has an id. The view's id is also the key into the sparse
         * array for its saved state, so the temporary one we choose just needs to be consistent between
         * saving and restoring state.
         */
        private void setIdIfNoneExists(View view) {
            if (view.getId() == View.NO_ID) {
                view.setId(R.id.view_model_state_saving_id);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            int size = size();
            int[] keys = new int[size];
            Parcelable[] values = new Parcelable[size];
            for (int i = 0; i < size; ++i) {
                keys[i] = keyAt(i);
                values[i] = valueAt(i);
            }
            parcel.writeInt(size);
            parcel.writeIntArray(keys);
            parcel.writeParcelableArray(values, flags);
        }

        public static final Creator<ViewState> CREATOR =
                ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<ViewState>() {
                    @Override
                    public ViewState createFromParcel(Parcel source, ClassLoader loader) {
                        int size = source.readInt();
                        int[] keys = new int[size];
                        source.readIntArray(keys);
                        Parcelable[] values = source.readParcelableArray(loader);
                        return new ViewState(size, keys, values);
                    }

                    @Override
                    public ViewState[] newArray(int size) {
                        return new ViewState[size];
                    }
                });
    }
}
