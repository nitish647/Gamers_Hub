package com.nitish.gamershub.utils.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public  class TimeChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
     //   Toast.makeText(context,"time changed",Toast.LENGTH_LONG).show();
        StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = sb.toString();
        Log.d("pResponse", "time changed broadcast "+log);
        Toast.makeText(context, log, Toast.LENGTH_LONG).show();
    }

}