package com.example.signtech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

public class Connect  {
    public static boolean isConnectedToInternet(Context context) {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(context.CONNECTIVITY_SERVICE);



            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0 ; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                            return true;
                        }
                    }
                }
            }


        } catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }

        return false;
    }
}
