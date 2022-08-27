package com.nitish.gamershub.Pojo;

public class RedeemListItem {
    String name;
    int coins;
    int money;
    int imageUrl;

    public RedeemListItem(String name, int coins, int money, int imageUrl) {
        this.name = name;
        this.coins = coins;
        this.money = money;
        this.imageUrl = imageUrl;
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
