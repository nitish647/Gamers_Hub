package com.nitish.gamershub.model.gamersHubMaterData;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AllGamesResponseItem implements Serializable {

    @SerializedName("new")
    private List<GamesItems> newGamesList;

    @SerializedName("main")
    private List<GamesItems> mainGamesList;

    @SerializedName("best")
    private List<GamesItems> bestGamesList;

    public void setNewGamesList(List<GamesItems> newGamesList) {
        this.newGamesList = newGamesList;
    }

    public List<GamesItems> getNewGamesList() {
        return newGamesList;
    }

    public void setMainGamesList(List<GamesItems> mainGamesList) {
        this.mainGamesList = mainGamesList;
    }

    public List<GamesItems> getMainGamesList() {
        return mainGamesList;
    }

    public void setBestGamesList(List<GamesItems> bestGamesList) {
        this.bestGamesList = bestGamesList;
    }

    public List<GamesItems> getBestGamesList() {
        return bestGamesList;
    }
}