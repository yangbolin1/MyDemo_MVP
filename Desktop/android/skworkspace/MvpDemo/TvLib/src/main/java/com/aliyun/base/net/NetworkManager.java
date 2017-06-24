
package com.aliyun.base.net;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.aliyun.base.exception.NoNetworkException;
import com.aliyun.base.exception.NoNetworkException.NoNetworkHanler;
import java.util.HashSet;
import java.util.Iterator;

public class NetworkManager {
    public static final String TAG = "NetworkManager";
    private static NetworkManager networkManager = null;
    private Context applicationContext;
    private boolean isConnected = true;
    private boolean mLastIsConnected = false;
    private HashSet<NetworkManager.INetworkListener> listenerSet = new HashSet();
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
                NetworkManager.this.mLastIsConnected = NetworkManager.this.isConnected;
                NetworkManager.this.isConnected = NetworkManager.isNetworkAvailable(context);
                Iterator var5 = NetworkManager.this.listenerSet.iterator();

                while(var5.hasNext()) {
                    NetworkManager.INetworkListener l = (NetworkManager.INetworkListener)var5.next();
                    l.onNetworkChanged(NetworkManager.this.isConnected, NetworkManager.this.mLastIsConnected);
                }
            }

        }
    };
    public static final int UNCONNECTED = -9999;

    private NetworkManager() {
    }

    public static NetworkManager instance() {
        if(networkManager == null) {
            networkManager = new NetworkManager();
        }

        return networkManager;
    }

    public void init(Context context) {
        this.init(context, (NoNetworkHanler)null);
    }

    public void init(Context context, NoNetworkHanler noNetworkHanler) {
        this.applicationContext = context;
        if(context instanceof Activity) {
            Context applicationContext = context.getApplicationContext();
            if(applicationContext != null) {
                this.applicationContext = applicationContext;
            }
        }

        this.applicationContext.registerReceiver(this.mBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        NoNetworkException.setNoNetworkHanler(noNetworkHanler);
        this.isConnected = isNetworkAvailable(context);
        this.mLastIsConnected = this.isConnected;
    }

    public void release() {
        this.applicationContext.unregisterReceiver(this.mBroadcastReceiver);
    }

    public void registerStateChangedListener(NetworkManager.INetworkListener l) {
        this.listenerSet.add(l);
        Log.i("NetworkManager", "registerStateChangedListener, size:" + this.listenerSet.size());
    }

    public void unregisterStateChangedListener(NetworkManager.INetworkListener l) {
        this.listenerSet.remove(l);
        Log.i("NetworkManager", "unregisterStateChangedListener, size:" + this.listenerSet.size());
    }

    public boolean isNetworkConnected() {
        return this.isConnected;
    }

    public Context getApplicationContext() {
        return this.applicationContext;
    }

    public static int getNetworkType(Context context) {
        if(context instanceof Activity) {
            Context connectivityManager = context.getApplicationContext();
            if(connectivityManager != null) {
                context = connectivityManager;
            }
        }

        ConnectivityManager connectivityManager1 = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager1.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.isAvailable()?info.getType():-9999;
    }

    public static boolean isNetworkAvailable(Context context) {
        return -9999 != getNetworkType(context);
    }

    public interface INetworkListener {
        void onNetworkChanged(boolean var1, boolean var2);
    }
}
