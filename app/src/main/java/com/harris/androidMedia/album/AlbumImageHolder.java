package com.harris.androidMedia.album;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harris.androidMedia.R;

/**
 * Created by Harris on 2017/4/22.
 */

public class AlbumImageHolder extends RecyclerView.ViewHolder implements CallBack{


   public boolean isDetachedFromWindow;

    AlbumAsyncTask albumAsyncTask;

    private CardView cardView;
    private LinearLayout linearLayout;
    private ImageView imageView;
    private TextView textView;

    public AlbumImageHolder(View itemView) {
        super(itemView);
        cardView = (CardView) itemView.findViewById(R.id.cardView);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
        textView = (TextView) itemView.findViewById(R.id.textView);
    }

    /** kick off a background thread maybe cache them later
     * @param position
     */
    public void beginTask(int position) {
        albumAsyncTask = new AlbumAsyncTask(this, position);
        albumAsyncTask.execute(ImageHelper.getImageUrl(position));
    }

    public void cancelTask() {
        if (albumAsyncTask != null) {
            albumAsyncTask.cancel(false);
        }
    }



    @Override
    public void onReady(final Bitmap bitmap, int position) {
        if (!isDetachedFromWindow) {
            if (Thread.currentThread().getId() != 1) {
                Activity activity = (Activity) imageView.getContext();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }


}
