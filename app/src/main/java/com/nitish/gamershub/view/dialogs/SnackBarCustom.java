package com.nitish.gamershub.view.dialogs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.LayoutCustomSnackbarBinding;
import com.nitish.gamershub.model.local.SnackBarItems;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.SnackBarHelper;

public class SnackBarCustom {


    public static void showCustomSnackBar(Activity activity, View rootLayout, SnackBarItems snackBarItems) {

        if (rootLayout == null)
            rootLayout = activity.getWindow().getDecorView();

        LayoutCustomSnackbarBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.layout_custom_snackbar, null, false);


        Snackbar snackbar = Snackbar.make(rootLayout, "", snackBarItems.getDuration());
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        // set padding of the all corners as 0
        snackbarLayout.setPadding(10, 0, 10, 30);

        snackbarLayout.addView(binding.getRoot(), 0);

        binding.parentLinear.setBackgroundTintList(AppCompatResources.getColorStateList(activity, snackBarItems.getBackgroundColor()));
        binding.messageTextview.setText(snackBarItems.getTitleText());
        binding.crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        snackbar.show();
    }


}

