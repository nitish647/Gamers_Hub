package com.nitish.gamershub.Pojo;

import androidx.annotation.NonNull;

public class Categories {
    int imageSrc;
    String imageUrl;
    String name;

    public Categories(int imageSrc, String imageUrl, String name) {
        this.imageSrc = imageSrc;
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public int getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(int imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
