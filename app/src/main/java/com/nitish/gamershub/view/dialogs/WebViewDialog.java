package com.nitish.gamershub.view.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.ShowWebviewDialogBinding;
import com.nitish.gamershub.utils.AppConstants;
import com.nitish.gamershub.utils.AppHelper;

public class WebViewDialog {

   private AlertDialog dialog;
   private final String usage;

    public static String USAGE_ABOUT_US="USAGE_ABOUT_US";
    public static String USAGE_PRIVACY_POLICY="USAGE_PRIVACY_POLICY";

    public WebViewDialog(Context context,String mUsage)
    {
        this.usage = mUsage;
        createDialog(context);
    }
    private void createDialog(Context context)
    {
        ShowWebviewDialogBinding binding;
        LayoutInflater factory = LayoutInflater.from(context);

        binding = DataBindingUtil.inflate(factory, R.layout.show_webview_dialog, null, false);

          dialog = new AlertDialog.Builder(context).create();
        AppHelper.setDialogBackgroundTransparent(dialog);
        dialog.setView(binding.getRoot());



        binding.webView.loadUrl(getUrl(usage));


        binding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }


    public void showDialog()
    {
        dialog.show();
    }





    private String getUrl(String usage)
    {
        String url = AppConstants.ASSETS_URL_PRIVACY_POLICY;
        if(usage.equals(USAGE_ABOUT_US))
        {
            url = AppConstants.ASSETS_URL_ABOUT_US;

        }
        return url;
    }


}
