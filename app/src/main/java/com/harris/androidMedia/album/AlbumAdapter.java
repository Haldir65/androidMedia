package com.harris.androidMedia.album;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harris.androidMedia.R;

import java.util.List;

import static com.harris.androidMedia.album.AlbumMainActivity.LOAD_ASYNC;
import static com.harris.androidMedia.album.AlbumMainActivity.LOAD_POOL;

/**
 * Created by Harris on 2017/4/22.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumImageHolder> {

    List<String> mDatas;

    int loadFashion = LOAD_ASYNC;

    private ThreadPoolManager poolManager;




    public AlbumAdapter(List<String> mDatas, int loadFashion, ThreadPoolManager poolManager) {
        this.mDatas = mDatas;
        this.loadFashion = loadFashion;
        this.poolManager = poolManager;
    }

    @Override
    public AlbumImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_thumbnail, parent, false);
        return new AlbumImageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AlbumImageHolder holder, int position) {
        String url = mDatas.get(position);
        if (!TextUtils.isEmpty(url)) {
            if (loadFashion == LOAD_ASYNC) { //using AsyncTask
                holder.beginTask(position);
            } else if (loadFashion == LOAD_POOL) {
                poolManager.start();
                poolManager.addAsyncTask(new ThreadPoolTaskBitmap(ImageHelper.getImageUrl(position),holder,position));
            }

        }
    }

    @Override
    public int getItemCount() {
        return mDatas==null?0:mDatas.size();
    }

    @Override
    public void onViewRecycled(AlbumImageHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(AlbumImageHolder holder) {
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(AlbumImageHolder holder) {
        holder.isDetachedFromWindow = false;
        holder.cancelTask();
    }

    @Override
    public void onViewDetachedFromWindow(AlbumImageHolder holder) {
        holder.isDetachedFromWindow = true;
    }


}
