package com.harris.androidMedia.camera2.album;

import android.support.annotation.LayoutRes;

import com.harris.androidMedia.databinding.ItemImageCardBinding;
import com.harris.androidMedia.recyclerView.adapter.DataBoundAdapter;
import com.harris.androidMedia.recyclerView.viewholer.DataBoundViewHolder;
import com.harris.androidMedia.util.GenericCallBack;
import com.harris.androidMedia.util.UtilImage;

import java.util.List;

/**
 * Created by Harris on 2017/4/7.
 */

public class AlbumBindAdapter extends DataBoundAdapter<ItemImageCardBinding> {

    List<UtilImage.ImageInfo> mList;

    GenericCallBack<UtilImage.ImageInfo> callBack;

    /**
     * Creates a DataBoundAdapter with the given item layout
     *
     * @param layoutId The layout to be used for items. It must use data binding.
     */
    public AlbumBindAdapter(@LayoutRes int layoutId) {
        super(layoutId);
    }

    @Override
    protected void bindItem(DataBoundViewHolder<ItemImageCardBinding> holder, int position, List<Object> payloads) {
        holder.binding.setCallback(callBack);
        holder.binding.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }
}
