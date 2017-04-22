package com.harris.androidMedia.album;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.harris.androidMedia.R;

/**
 * 异步加载的两种方式:AsyncTask与ThreadPool
 *
 * @author carrey
 */
public class AlbumMainActivity extends Activity {

    /**
     * 服务器地址
     */
//	public static String webServerStr;
    private static final String TAG = "MainActivity";


    private Button btnAsync;

    private Button btnPool;

    private RecyclerView mRecyclerView;
    private AlbumAdapter mAdapter;

    /**
     * 加载方式
     */
    public static final int LOAD_ASYNC = 1;
    public static final int LOAD_POOL = 2;

    private ThreadPoolManager poolManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        btnAsync = (Button) findViewById(R.id.btn_async);
        btnAsync.setOnClickListener(new AsyncButtonClick());
        btnPool = (Button) findViewById(R.id.btn_pool);
        btnPool.setOnClickListener(new PoolButtonClick());
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        poolManager = new ThreadPoolManager(ThreadPoolManager.TYPE_FIFO, 5);
        mAdapter = new AlbumAdapter(ImageHelper.getDummyStringList(1000), LOAD_POOL, poolManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    }

    private class AsyncButtonClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            mAdapter.loadFashion = LOAD_ASYNC;
            mAdapter.notifyDataSetChanged();
        }
    }

    private class PoolButtonClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            mAdapter.loadFashion = LOAD_POOL;
            mAdapter.notifyDataSetChanged();
        }
    }
}
