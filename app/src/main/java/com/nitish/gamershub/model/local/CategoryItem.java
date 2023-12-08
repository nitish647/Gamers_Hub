package com.nitish.gamershub.model.local;

import androidx.annotation.NonNull;

import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryItem implements Serializable {
  private int imageSrc;
  private String imageUrl;
  private Integer imageRes;
  private String name;
  private String noGameBannerTitle;
  private ArrayList<GamesItems> categoryGameList;

    public CategoryItem(String name, Integer imageRes) {
        this.imageRes = imageRes;
        this.name = name;
    }

    public String getNoGameBannerTitle() {
        return noGameBannerTitle;
    }

    public void setNoGameBannerTitle(String noGameBannerTitle) {
        this.noGameBannerTitle = noGameBannerTitle;
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

    public Integer getImageRes() {
        return imageRes;
    }

    public void setImageRes(Integer imageRes) {
        this.imageRes = imageRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<GamesItems> getCategoryGameList() {
        return categoryGameList;
    }

    public void setCategoryGameList(ArrayList<GamesItems> categoryGameList) {
        this.categoryGameList = categoryGameList;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
