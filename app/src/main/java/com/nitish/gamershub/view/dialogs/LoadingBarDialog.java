package com.nitish.gamershub.view.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.ConfirmationDialogLayoutBinding;
import com.nitish.gamershub.databinding.CustomLoadingBarLayoutBinding;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.model.local.DialogLoadingItems;
import com.nitish.gamershub.utils.AppHelper;

public class LoadingBarDialog {
    CustomLoadingBarLayoutBinding binding;
    Context context;


    static Dialog dialog = null;
    private DialogLoadingItems dialogItems;

    private static LoadingBarDialog instance = null;


    public LoadingBarDialog(Context context, DialogLoadingItems dialogItems) {

        this.context = context;
        this.dialogItems = dialogItems;
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.custom_loading_bar_layout, null, false);
        createDialog();
    }


    private void setOnClickListener() {


    }


    public static LoadingBarDialog createInstance(Context context, DialogLoadingItems dialogItems) {
        if (instance == null)
            synchronized (LoadingBarDialog.class) {
                instance = new LoadingBarDialog(context, dialogItems);
            }


        return instance;
    }

    public static LoadingBarDialog newInstance(Context context, DialogLoadingItems dialogItems) {


        if (instance == null)
            synchronized (LoadingBarDialog.class) {
                instance = new LoadingBarDialog(context, dialogItems);
            }


        return instance;


    }

    public static LoadingBarDialog getInstance() {
        return instance;
    }

    private void createDialog() {
        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            AppHelper.setDialogBackgroundTransparent(dialog);
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.custom_loading_bar_layout, null, false);
//            binding.lottieAnimationLoading.setAnimation(R.raw.lottile_gaming_loading_anim);
            // Initialize the animation here

            binding.lottieAnimationLoading.setAnimation(R.raw.lottile_gaming_loading_anim);
            // Start the animation
            binding.lottieAnimationLoading.playAnimation();

            dialog.setContentView(binding.getRoot());
            dialog.setCancelable(false);
        }
    }

    public void showDialog() {
        if (((Activity) context).isFinishing()) {
            return;
        }

        if (dialog != null) {

            if (!dialog.isShowing()) {

                dialog.show();
            }

            // Start the animation after the dialog is shown
            if (binding.lottieAnimationLoading != null) {
                binding.lottieAnimationLoading.playAnimation();
            }
        }

    }

    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            try {

                dialog.dismiss();
            } catch (Exception e) {

                dialog = null;
                instance = null;
            }

            dialog = null;
            instance = null;
        }
    }

}
