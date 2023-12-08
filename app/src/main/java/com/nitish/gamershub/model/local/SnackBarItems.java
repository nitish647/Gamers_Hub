package com.nitish.gamershub.model.local;

import com.nitish.gamershub.R;

public class SnackBarItems {

  private int backgroundColor= R.color.light_green_material;
  private String titleText;
  private int duration;

    public SnackBarItems(String titleText) {
        this.titleText = titleText;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
