package com.nitish.gamershub.model.local;

import com.nitish.gamershub.R;

public class DialogHelperPojo
{
    String YesButton = "Confirm";
    String NoButton = "Cancel";
    String title = "";
    String message = "Are you sure ?";
    int dialogIcon= R.drawable.gamers_hub_icon15;

    public DialogHelperPojo() {
    }

    public String getYesButton() {
        return YesButton;
    }

    public void setYesButton(String yesButton) {
        YesButton = yesButton;
    }

    public String getNoButton() {
        return NoButton;
    }

    public void setNoButton(String noButton) {
        NoButton = noButton;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDialogIcon() {
        return dialogIcon;
    }

    public void setDialogIcon(int dialogIcon) {
        this.dialogIcon = dialogIcon;
    }
}