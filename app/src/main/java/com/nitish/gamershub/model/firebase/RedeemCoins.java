package com.nitish.gamershub.model.firebase;

import java.io.Serializable;
import java.util.ArrayList;

public class RedeemCoins implements Serializable {
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
