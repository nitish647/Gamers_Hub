package com.nitish.gamershub;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Helper_class {
    public static GradientDrawable gd;
    String github_test;
    public static void show_toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    public static GradientDrawable setBackgroundWithGradient(String color1, String color2, Float radius, GradientDrawable.Orientation orientation) {
        int[] colors = {Color.parseColor(color1), Color.parseColor(color2)};
        gd = new GradientDrawable(
                orientation, colors);
        gd.setCornerRadius(radius);
        return gd;
    }

    public static GradientDrawable setSingleColorRoundBackground(String color1, Float radius) {
        int[] colors = {Color.parseColor(color1)};

        gd = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP, colors);

        gd.setCornerRadius(radius);
        gd.setColor(Color.parseColor(color1));
        return gd;
    }
}
