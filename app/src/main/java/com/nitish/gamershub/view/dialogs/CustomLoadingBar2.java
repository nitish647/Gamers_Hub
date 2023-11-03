package com.nitish.gamershub.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nitish.gamershub.R;

public class CustomLoadingBar2 {

    public static CustomLoadingBar2 instance;

    public static CustomLoadingBar2 getInstance() {
        if (instance == null) {
            synchronized (CustomLoadingBar2.class) {
                if (instance == null) {
                    instance = new CustomLoadingBar2();
                }
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

//            ProgressBar progressBar = ((ProgressBar) dialog.findViewById(R.id.avi)); // Ensure the progress bar is visible
//            progressBar.setVisibility(View.VISIBLE);
//
//            ViewTreeObserver observer = progressBar.getViewTreeObserver();
//            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    progressBar.setVisibility(View.VISIBLE); // Ensure the progress bar is visible
//                }
//            });

        } else {
//            com.wang.avi.AVLoadingIndicatorView CustomLoadingBar2 = dialog.findViewById ( R.id.avi );
//            CustomLoadingBar2.hide();
//            dialog.cancel();
        }

        com.wang.avi.AVLoadingIndicatorView avLoadingIndicatorView = dialog.findViewById(R.id.avi);
        avLoadingIndicatorView.show();

//        TextView CustomLoadingBar2text = (TextView) dialog.findViewById ( R.id.message );
//        CustomLoadingBar2text.setVisibility(View.GONE);
//        CustomLoadingBar2text.setText (title);

        dialog.show();
    }

    public void cancelCustomDialog() {
        try {

            if (null != dialog && dialog.isShowing()) {

                com.wang.avi.AVLoadingIndicatorView CustomLoadingBar2 = (com.wang.avi.AVLoadingIndicatorView) dialog.findViewById(R.id.avi);
                CustomLoadingBar2.hide();
                dialog.cancel();
                dialog = null;
                instance = null;
            }
        } catch (Exception e) {
            dialog = null;
            instance = null;
            e.printStackTrace();
        }
    }
}
