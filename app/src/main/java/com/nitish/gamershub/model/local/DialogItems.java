package com.nitish.gamershub.model.local;

public class DialogItems {
    private String message;
    private String title;
    private String yesTitle;
    private String noTitle;

    private Integer rawAnimation;

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
}
