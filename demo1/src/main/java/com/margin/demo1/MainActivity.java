package com.margin.demo1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.margin.demo1.service.Demo1AliveService;
import com.margin.search.SearchManager;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mDataTv;
    private ProgressDialog progressDialog;
    private LoadDataHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDataTv = findViewById(R.id.data_view);
        mDataTv.setOnClickListener(this);
        findViewById(R.id.load_data_btn).setOnClickListener(this);
        handler = new LoadDataHandler(this);
        startService(new Intent(this, Demo1AliveService.class));
    }

    public void updateView(Spanned data) {
        mDataTv.setText(data);
    }

    public void updateView(String data) {
        mDataTv.setText(data);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.data_view:
                showLoading("加载原数据并删除重复字符，请稍后~");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data = SearchManager.replace(getApplication(), "test");
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        message.obj = data;
                        handler.sendMessage(message);
                    }
                }).start();
                break;
            case R.id.load_data_btn:
                findViewById(R.id.load_data_btn).setVisibility(View.GONE);
                Dialog dialog = new AlertDialog.Builder(this)
                        .setTitle("加载测试数据")
                        .setMessage("加载数据会高亮显示重复的字符,\n您可以在数据加载完毕后点击屏幕来删除重复的字符,\n现在开始加载数据吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("加载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showLoading("正在加载数据中，请稍后~");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Spanned data = SearchManager.search(getApplication(), "test");
                                        Message message = handler.obtainMessage();
                                        message.what = 0;
                                        message.obj = data;
                                        handler.sendMessage(message);
                                    }
                                }).start();
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
        }
    }

    public void showLoading(String msg) {
        cancelLoading();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void cancelLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private static class LoadDataHandler extends Handler {
        private WeakReference<MainActivity> activityWeakReference;

        public LoadDataHandler(MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            if (msg.what == 0) {
                Spanned data = (Spanned) msg.obj;
                activity.updateView(data);
            } else if (msg.what == 1) {
                String data = (String) msg.obj;
                activity.updateView(data);
            }
            activity.cancelLoading();

        }
    }
}
