package com.harris.androidMedia.exoPlayer.customize;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harris.androidMedia.R;
import com.harris.androidMedia.databinding.ActivityChooseLocalVideoBinding;
import com.harris.androidMedia.util.UtilVideo;

import java.util.List;

/**
 * Created by Fermi on 2017/3/16.
 */

public class ChooseLocalVideoActivity extends AppCompatActivity {

    ActivityChooseLocalVideoBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_local_video);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    static class VideoAdapter extends RecyclerView.Adapter<VideoHolder> {

        List<UtilVideo.VideoInfo> mList;

        @Override
        public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_info, parent, false);
            return new VideoHolder(itemView);
        }

        @Override
        public void onBindViewHolder(VideoHolder holder, int position) {
            holder.bindData(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    static class VideoHolder extends RecyclerView.ViewHolder {


        public VideoHolder(View itemView) {
            super(itemView);
        }

        public void bindData(UtilVideo.VideoInfo videoInfo) {

        }
    }

}
