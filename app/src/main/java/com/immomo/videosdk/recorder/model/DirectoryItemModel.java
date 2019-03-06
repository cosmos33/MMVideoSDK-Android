package com.immomo.videosdk.recorder.model;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.immomo.videosdk.base.cement.CementAdapter;
import com.immomo.videosdk.base.cement.CementModel;
import com.immomo.videosdk.base.cement.CementViewHolder;
import com.immomo.videosdk.R;
import com.immomo.videosdk.config.Configs;
import com.immomo.videosdk.glide.ImageLoaderX;
import com.immomo.videosdk.utils.UIUtils;
import com.immomo.videosdk.utils.album.AlbumConstant;

import androidx.annotation.NonNull;

public class DirectoryItemModel extends CementModel<DirectoryItemModel.ViewHodler> {


    private int position;
    private AlbumDirectory directory;

    public DirectoryItemModel(int position, @NonNull AlbumDirectory albumDirectory) {
        this.directory = albumDirectory;
        this.position = position;
    }


    public AlbumDirectory getDirectory() {
        return directory;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.pop_album_directory_item;
    }

    @Override
    public void bindData(@NonNull ViewHodler holder) {
        super.bindData(holder);
        ImageLoaderX.load(directory.getCoverPath()).height(UIUtils.getPixels(60)).width(UIUtils.getPixels(60)).into(holder.imageView);
        holder.name.setText(directory.getName());
        final String desc;
        if (TextUtils.equals(directory.getId(), AlbumConstant.DIRECTORY_ID_VIDEO)) {
            desc = directory.getMedias().size() + "";
        } else {
            desc = holder.count.getResources().getString(R.string.multpic_directory_count, directory.getMedias().size());
        }
        holder.count.setText(desc);


    }

    @NonNull
    @Override
    public CementAdapter.IViewHolderCreator<ViewHodler> getViewHolderCreator() {
        return new CementAdapter.IViewHolderCreator<ViewHodler>() {
            @NonNull
            @Override
            public ViewHodler create(@NonNull View view) {
                return new ViewHodler(view);
            }
        };
    }

    public static class ViewHodler extends CementViewHolder {


        private ImageView imageView;
        private TextView name;
        private TextView count;


        public ViewHodler(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.diretory_cover);
            name = itemView.findViewById(R.id.diretory_name);
            count = itemView.findViewById(R.id.image_count);
        }
    }
}
