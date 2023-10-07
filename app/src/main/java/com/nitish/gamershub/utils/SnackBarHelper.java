package com.nitish.gamershub.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.nitish.gamershub.R;

public class SnackBarHelper {

    public static  void showPutatoeSnackBar(Context context ,ViewGroup rootView, String message )
    {

        // create an instance of the snackbar
        // pass the root viewGroup
        final Snackbar snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_LONG);

        // inflate the custom_snackbar_view created previously
        View customSnackView = LayoutInflater.from(context).inflate(R.layout.activity_category, null);

        TextView snackBarTextView = customSnackView.findViewById(R.id.playButton);

        // when the text is not empty then show the input message
        if(!message.trim().isEmpty())
            snackBarTextView.setText(message);
        // set the background of the default snackbar as transparent
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        // now change the layout of the snackbar
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

        // set padding of the all corners as 0
        snackbarLayout.setPadding(0, 0, 0, 0);

        snackbarLayout.addView(customSnackView,0);

        snackbar.show();
    }
}
