package com.nitish.gamershub.Pojo.FireBase;

public class RedeemListItem {
    String name;
    int coins;
    int money;
    int imageUrl;
    String paytmNumber="";
    String upiID="";

    public RedeemListItem(String name, int coins, int money, int imageUrl) {
        this.name = name;
        this.coins = coins;
        this.money = money;
        this.imageUrl = imageUrl;
    }

    public String getPaytmNumber() {
        return paytmNumber;
    }

    public void setPaytmNumber(String paytmNumber) {
        this.paytmNumber = paytmNumber;
    }

    public String getUpiID() {
        return upiID;
    }

    public void setUpiID(String upiID) {
        this.upiID = upiID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
