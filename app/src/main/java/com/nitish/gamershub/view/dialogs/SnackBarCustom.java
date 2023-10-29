package com.nitish.gamershub.view.dialogs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.LayoutCustomSnackbarBinding;
import com.nitish.gamershub.utils.SnackBarHelper;

public class SnackBarCustom {


    public static void showCustomSnackBar(Activity activity, String message, int duration) {
        View rootView = activity.getWindow().getDecorView();

        LayoutCustomSnackbarBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.layout_custom_snackbar, null, false);

        Snackbar snackbar = Snackbar.make(rootView, message, duration);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

        // set padding of the all corners as 0
        snackbarLayout.setPadding(0, 0, 0, 0);

        snackbarLayout.addView(binding.getRoot(), 0);

        binding.crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        snackbar.show();
    }


}

