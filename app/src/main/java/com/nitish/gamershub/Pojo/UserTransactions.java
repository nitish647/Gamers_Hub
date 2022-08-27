package com.nitish.gamershub.Pojo;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UserTransactions implements Serializable {





    TransactionRequest transactionRequest;

    public UserTransactions() {

    }
    public UserTransactions(TransactionRequest transactionRequest) {
        this.transactionRequest = transactionRequest;
    }

    public TransactionRequest getTransactionRequest() {
        return transactionRequest;
    }

    public void setTransactionRequest(TransactionRequest transactionRequest) {
        this.transactionRequest = transactionRequest;
    }

    public static class TransactionRequest  {


        public String redeemType;
        public boolean isPaid;
        public String paidDate;
        public int remainingCoins;
        public String requestDate;
        public int coins;
        public int amount;

        public TransactionRequest() {
        }

        public TransactionRequest(String redeemType, boolean isPaid, String paidDate, int remainingCoins, String requestDate, int coins, int amount) {
            this.redeemType = redeemType;
            this.isPaid = isPaid;
            this.paidDate = paidDate;
            this.remainingCoins = remainingCoins;
            this.requestDate = requestDate;
            this.coins = coins;
            this.amount = amount;
        }



        public String getRedeemType() {
            return redeemType;
        }


        public void setRedeemType(String redeemType) {
            this.redeemType = redeemType;
        }

        public boolean isPaid() {
            return isPaid;
        }

        public void setPaid(boolean paid) {
            isPaid = paid;
        }

        public String getPaidDate() {
            return paidDate;
        }

        public void setPaidDate(String paidDate) {
            this.paidDate = paidDate;
        }

        public int getRemainingCoins() {
            return remainingCoins;
        }

        public void setRemainingCoins(int remainingCoins) {
            this.remainingCoins = remainingCoins;
        }

        public String getRequestDate() {
            return requestDate;
        }

        public void setRequestDate(String requestDate) {
            this.requestDate = requestDate;
        }

        public int getCoins() {
            return coins;
        }

        public void setCoins(int coins) {
            this.coins = coins;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

}

