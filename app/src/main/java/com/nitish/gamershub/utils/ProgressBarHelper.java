package com.nitish.gamershub.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressBarHelper {

  static   ProgressDialog progressDialog;


  public static ProgressDialog setProgressBarDialog(Context context)
  {
    ProgressDialog progressBarDialog = new ProgressDialog(context);

    progressBarDialog.setMessage("Loading...");
    progressBarDialog.setCancelable(false);
    progressBarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressBarDialog.setIndeterminate(true);
    return  progressBarDialog;

  }
}
