package com.harris.androidMedia.camera2.album;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.harris.androidMedia.R;
import com.harris.androidMedia.databinding.ActivityAlbumScanBinding;
import com.harris.androidMedia.recyclerView.itemDecoration.AlbumItemDecoration;
import com.harris.androidMedia.util.GenericCallBack;
import com.harris.androidMedia.util.ToastUtil;
import com.harris.androidMedia.util.UtilImage;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Harris on 2017/4/7.
 */

public class AlbumActivity extends AppCompatActivity implements GenericCallBack<UtilImage.ImageInfo> {

    ActivityAlbumScanBinding binding;

    AlbumBindAdapter mAdapter;
    List<UtilImage.ImageInfo> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_album_scan);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mList = new ArrayList<>();
        mAdapter = new AlbumBindAdapter(R.layout.item_image_card);
        mAdapter.mList = mList;
        mAdapter.callBack = this;
        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.addItemDecoration(new AlbumItemDecoration(this));
        Observable.create(new ObservableOnSubscribe<List<UtilImage.ImageInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<UtilImage.ImageInfo>> e) throws Exception {
                if (ActivityCompat.checkSelfPermission(AlbumActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                e.onNext(UtilImage.getAllImageOnDevice(AlbumActivity.this,mList));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<UtilImage.ImageInfo>>() {
                    @Override
                    public void accept(List<UtilImage.ImageInfo> videoInfos) throws Exception {
                        mAdapter.notifyDataSetChanged();
                    }
                });

    }

    @Override
    public void onClick(View view, UtilImage.ImageInfo imageInfo) {
        // TODO: 2017/4/7 ImageClicked do stuff
        ToastUtil.showTextShort(this, imageInfo.name);
    }
}
