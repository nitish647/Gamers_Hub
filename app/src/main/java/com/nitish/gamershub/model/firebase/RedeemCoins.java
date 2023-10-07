package com.nitish.gamershub.model.firebase;

import java.util.ArrayList;

public class RedeemCoins {
    ArrayList<RedeemListItem> redeemListItemList;


    public RedeemCoins() {
    }

    public ArrayList<RedeemListItem> getRedeemListItemList() {
        return redeemListItemList;
    }

    public void setRedeemListItemList(ArrayList<RedeemListItem> redeemListItemList) {
        this.redeemListItemList = redeemListItemList;
    }
}
