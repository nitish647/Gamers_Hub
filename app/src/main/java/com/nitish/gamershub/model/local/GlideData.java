package com.nitish.gamershub.model.local;

import com.nitish.gamershub.R;

public class GlideData {

    String imageUrl;
    int placeHolder = R.drawable.loading;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(int placeHolder) {
        this.placeHolder = placeHolder;
    }
}
