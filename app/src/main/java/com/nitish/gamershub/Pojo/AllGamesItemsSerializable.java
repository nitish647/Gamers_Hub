package com.nitish.gamershub.Pojo;

import java.io.Serializable;

public class AllGamesItemsSerializable implements Serializable {
    String img_file;
    String  orientation;
    String category;
    String name;
    String description;
    String gameUrl;

    public AllGamesItemsSerializable() {
    }

    public AllGamesItemsSerializable(String img_file, String orientation, String category, String name, String description, String gameUrl) {
        this.img_file = img_file;
        this.orientation = orientation;
        this.category = category;
        this.name = name;
        this.description = description;
        this.gameUrl = gameUrl;
    }

    public String getImg_file() {
        return img_file;
    }

    public void setImg_file(String img_file) {
        this.img_file = img_file;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }
}