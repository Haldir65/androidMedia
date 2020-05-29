package com.harris.androidMedia.camera2.album;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.harris.androidMedia.R;
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


    AlbumAdapter mAdapter;
    List<UtilImage.ImageInfo> mList;
    RecyclerView recyclerView;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_album_scan);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mList = new ArrayList<>();
        mAdapter = new AlbumAdapter(R.layout.item_image_card);
        mAdapter.mList = mList;
        mAdapter.callBack = this;
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new AlbumItemDecoration(this));
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
            /*    if (mList.size() == 1) {
                    File file = new File(mList.get(0).path);
                    File parentFolder = file.getParentFile();
                    File[] files = parentFolder.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        if (!files[i].getAbsolutePath().endsWith(".jpeg")&&!files[i].isDirectory()) {
                            files[i].renameTo(new File(files[i].getAbsolutePath() + ".jpeg"));
                            LogUtil.d("renameFile " + i + " Thread" + Thread.currentThread());
                        }
                    }

                } else {
                    for (int i = 0; i < mList.size(); i++) {
                        UtilImage.ImageInfo imageInfo = mList.get(i);
                        File file = new File(imageInfo.path);
                        if (file.exists()) {
                            File newFile = new File(file.getAbsolutePath() + ".jpeg");
                            file.renameTo(newFile);
                            LogUtil.d("renameFile " + i + " Thread" + Thread.currentThread());
                        }
                    }
                }*/

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
