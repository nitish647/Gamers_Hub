package com.nitish.gamershub.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nitish.gamershub.R;

public class ToastHelper {

    public static void customToast(Context context , String message)
    {
        Activity activity = (Activity)context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //Getting the View object as defined in the customtoast.xml file
        View layout = layoutInflater.inflate(R.layout.custom_toast_layout,null);

        RelativeLayout backgroundRelative = layout.findViewById(R.id.backgroundRelative);
        TextView messageTextview = layout.findViewById(R.id.messageTextview);


        Toast toast = new Toast(context);
        toast.setView(layout);

        messageTextview.setText(message);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

    }
}
