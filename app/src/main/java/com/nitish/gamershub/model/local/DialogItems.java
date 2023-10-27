package com.nitish.gamershub.model.local;

import com.nitish.gamershub.R;

public class DialogItems {
    private String message;
    private String title;
    private String yesTitle="Yes";
    private String noTitle="No";

    private Boolean isSingleButton=false;
    private Integer rawAnimation;
    int dialogIcon= R.drawable.gamers_hub_icon15;
    public DialogItems() {
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYesTitle(String yesTitle) {
        this.yesTitle = yesTitle;
    }

    public void setNoTitle(String noTitle) {
        this.noTitle = noTitle;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getYesTitle() {
        return yesTitle;
    }

    public String getNoTitle() {
        return noTitle;
    }

    public Integer getRawAnimation() {
        return rawAnimation;
    }


    public void setRawAnimation(Integer rawAnimation) {
        this.rawAnimation = rawAnimation;
    }

    public Boolean getSingleButton() {

        return isSingleButton;
    }

    public void setSingleButton(Boolean singleButton) {
        isSingleButton = singleButton;
    }

    public int getDialogIcon() {
        return dialogIcon;
    }

    public void setDialogIcon(int dialogIcon) {
        this.dialogIcon = dialogIcon;
    }
}
