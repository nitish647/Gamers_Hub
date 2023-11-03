package com.nitish.gamershub.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.nitish.gamershub.R;

public class Loader {

    public static Loader instance;

    public static Loader getInstance() {
        if (instance == null) {
            synchronized (Loader.class) {

                instance = new Loader();

            }
        }
        return instance;
    }


    Dialog dialog;

    public void showCustomDialogBox(Context context, String title) {

        if (dialog == null) {
            dialog = new Dialog(context
                    //, android.R.style.Theme_NoTitleBar
            );
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.loader);
            dialog.setCancelable(false);


        } else {
            com.wang.avi.AVLoadingIndicatorView loader = dialog.findViewById(R.id.avi);
            loader.hide();
            dialog.cancel();
        }

        com.wang.avi.AVLoadingIndicatorView loader = dialog.findViewById(R.id.avi);
        loader.show();

        TextView loadertext = (TextView) dialog.findViewById(R.id.message);
        loadertext.setVisibility(View.GONE);
        loadertext.setText(title);

        dialog.show();
    }

    public void cancelCustomDialog() {
        try {

            if (null != dialog && dialog.isShowing()) {

                com.wang.avi.AVLoadingIndicatorView loader = (com.wang.avi.AVLoadingIndicatorView) dialog.findViewById(R.id.avi);
                loader.hide();
                dialog.cancel();
                dialog = null;
                instance =null;
            }
        } catch (Exception e) {
            dialog = null;
            instance = null;
            e.printStackTrace();
        }
    }
}