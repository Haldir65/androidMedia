package com.harris.androidMedia.exoPlayer.customize;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.harris.androidMedia.R;
import com.harris.androidMedia.databinding.ActivityChooseLocalVideoBinding;
import com.harris.androidMedia.recyclerView.itemDecoration.VideoItemDecoration;
import com.harris.androidMedia.util.OnItemClickListener;
import com.harris.androidMedia.util.ToastUtil;
import com.harris.androidMedia.util.UtilVideosKt;
import com.harris.androidMedia.util.VideoInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.harris.androidMedia.exoPlayer.customize.CustomPlayerViewActivity.CUSTOM_PLAYER_VIEW_URL_STRING;

/**
 * Created by Fermi on 2017/3/16.
 */

public class ChooseLocalVideoActivity extends AppCompatActivity implements OnItemClickListener {


    public static final String FLAG_CHOOSE_AND_RETURN_URL = "choose_and_return";
    ActivityChooseLocalVideoBinding binding;
    List<VideoInfo> list;
    VideoAdapter mAdapter;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_local_video);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        Disposable subscribe = Observable.fromCallable(new Callable<List<VideoInfo>>() {
            @Override
            public List<VideoInfo> call() throws Exception {
                return UtilVideosKt.
                        getAllVideoUnderCertainDirNonRecrusive
                                (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Environment.DIRECTORY_MOVIES, list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<VideoInfo>>() {
                    @Override
                    public void accept(List<VideoInfo> videoInfos) throws Exception {
                        mAdapter.notifyDataSetChanged();
                    }
                });
        compositeDisposable.add(subscribe);
        mAdapter = new VideoAdapter(list, this);
        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.addItemDecoration(new VideoItemDecoration(this));
//        binding.image.setImageBitmap(ThumbnailUtils.createVideoThumbnail(list.get(0).path,MINI_KIND));
    }

    @Override
    public void onItemClicked(View view, int position) {
        VideoInfo info = list.get(position);
        ToastUtil.showTextShort(this, info.getName());
        Intent intent;
        if (getIntent().getBooleanExtra(FLAG_CHOOSE_AND_RETURN_URL, false)) {
            intent = new Intent();
            intent.putExtra(CUSTOM_PLAYER_VIEW_URL_STRING, info.getPath());
            setResult(RESULT_OK, intent);
            finish();
        } else {
//            intent = new Intent(this, CustomPlayerViewActivity.class);
//            intent.putExtra(CUSTOM_PLAYER_VIEW_URL_STRING, info.getPath());
//            startActivityForResult(intent, -1);
            PlayerActivity.Companion.go(this, info.getPath());
            finish();
        }
    }

    static class VideoAdapter extends RecyclerView.Adapter<VideoHolder> {

        List<VideoInfo> mList;
        OnItemClickListener mListener;

        public VideoAdapter(List<VideoInfo> mList, OnItemClickListener mListener) {
            this.mList = mList;
            this.mListener = mListener;
        }

        @Override
        public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_info, parent, false);
            return new VideoHolder(itemView, mListener);
        }

        @Override
        public void onBindViewHolder(VideoHolder holder, int position) {
            holder.bindData(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

    }

    static class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnItemClickListener mListener;
        CardView mCardView;
        LinearLayout mLinearLayout;
        ImageView mImageView;
        TextView mTextView;

        public VideoHolder(View itemView, OnItemClickListener mListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.mListener = mListener;
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            mTextView = (TextView) itemView.findViewById(R.id.textView);

        }


        public void bindData(@NonNull VideoInfo videoInfo) {
//            mImageView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoInfo.path,MINI_KIND));
            Glide.with(itemView.getContext()).load(videoInfo.getPath()).asBitmap().centerCrop().into(mImageView);
            mTextView.setText(videoInfo.getName());

        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClicked(v, getAdapterPosition());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
