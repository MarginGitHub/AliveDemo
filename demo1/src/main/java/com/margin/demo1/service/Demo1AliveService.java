package com.margin.demo1.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.margin.demo1.IDemo1Message;


/**
 * Created by Margin on 2018/1/17.
 * 相当于App Alive的监听器
 */

public class Demo1AliveService extends Service implements ServiceConnection {
    private boolean isBinded = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bindRemoteService();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Stub();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        isBinded = true;
        Toast.makeText(getApplicationContext(), "绑定上了测试应用2 Service", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Toast.makeText(getApplicationContext(), "断开了与测试应用2 Service的链接", Toast.LENGTH_LONG).show();
        isBinded = false;
        bindRemoteService();
    }

    private void bindRemoteService() {
        if (!isBinded) {
            Intent intent = new Intent();
            intent.setClassName("com.margin.demo2", "com.margin.demo2.service.Demo2AliveService");
            bindService(intent, this, Context.BIND_AUTO_CREATE);
        }
    }

    private static class Stub extends IDemo1Message.Stub {

        @Override
        public boolean isAlive() throws RemoteException {
            return true;
        }
    }
}
