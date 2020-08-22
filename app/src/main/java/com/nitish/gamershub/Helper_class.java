package com.nitish.gamershub;

import android.content.Context;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Helper_class  {

    public static void show_toast(Context context,String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
