package com.mm.recorduisdk.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;
import com.mm.recorduisdk.base.cement.SimpleCementAdapter;
import com.mm.recorduisdk.recorder.model.AlbumDirectory;
import com.mm.recorduisdk.recorder.model.DirectoryItemModel;
import com.mm.recorduisdk.widget.decoration.LinearPaddingItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class DirectoriesPopWindow extends PopupWindow {

    private View anchorView;
    private View contentView;
    private CementAdapter cementAdapter;
    private RecyclerView recyclerView;
    private OnDirectorySelectListener onDirectorySelect;
    private View maskLayout;
    private View maskView;
    private Context context;
    private WindowManager windowManager;

    public DirectoriesPopWindow(Context context, View anchorView) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.context = context;
        init();
        this.anchorView = anchorView;
        contentView = LayoutInflater.from(context).inflate(R.layout.pop_album_directory, null);

        setContentView(contentView);

        recyclerView = contentView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new LinearPaddingItemDecoration(UIUtils.getPixels(11), UIUtils.getPixels(15), UIUtils.getPixels(11)));
        cementAdapter = new SimpleCementAdapter();
        cementAdapter.setOnItemClickListener(new CementAdapter.OnItemClickListener() {
            @Override
            public void onClick(@NonNull View itemView, @NonNull CementViewHolder viewHolder, int position, @NonNull CementModel<?> model) {
                if (DirectoryItemModel.class.isInstance(model)) {
                    DirectoryItemModel directoryItemModel = ((DirectoryItemModel) model);
                    if (onDirectorySelect != null) {
                        onDirectorySelect.onSelect(directoryItemModel.getPosition(), directoryItemModel.getDirectory());
                    }
                }
            }
        });
        recyclerView.setAdapter(cementAdapter);

        addMask();
    }

    private void init() {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        setFocusable(true);
    }

    /**
     * 添加一层蒙层.
     */
    private void addMask() {
        maskLayout = LayoutInflater.from(context).inflate(R.layout.pop_album_directory_mask, null);
        maskView = maskLayout.findViewById(R.id.mask);

        WindowManager.LayoutParams wl = new WindowManager.LayoutParams();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.MATCH_PARENT;

        wl.format = PixelFormat.TRANSLUCENT;//不设置这个弹出框的透明遮罩显示为黑色
        wl.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;//该Type描述的是形成的窗口的层级关系


        maskView.setBackgroundColor(0x18000000);
        maskView.setFitsSystemWindows(false);
        maskView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    removeMask();
                    return true;
                }
                return false;
            }
        });
        windowManager.addView(maskLayout, wl);
    }


    private void removeMask() {
        if (null != maskView) {
            windowManager.removeViewImmediate(maskLayout);
        }
    }

    @Override
    public void dismiss() {
        removeMask();
        super.dismiss();
    }


    public void setOnDirectorySelect(OnDirectorySelectListener onDirectorySelect) {
        this.onDirectorySelect = onDirectorySelect;
    }

    public void show() {
        PopupWindowCompat.showAsDropDown(this, anchorView, anchorView.getMeasuredWidth(), 0, Gravity.RIGHT);
    }

    public void setDirectories(List<AlbumDirectory> directories) {
        if (directories == null) {
            return;
        }
        List<CementModel<?>> list = new ArrayList<>(directories.size());
        for (int i = 0; i < directories.size(); i++) {
            list.add(new DirectoryItemModel(i, directories.get(i)));
        }
        cementAdapter.removeAllModels();
        cementAdapter.addModels(list);
    }


    public interface OnDirectorySelectListener {
        void onSelect(int position, AlbumDirectory albumDirectory);
    }
}
