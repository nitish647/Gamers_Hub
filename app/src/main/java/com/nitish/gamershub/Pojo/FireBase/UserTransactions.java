package com.nitish.gamershub.Pojo.FireBase;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UserTransactions implements Serializable {





    ArrayList<TransactionRequest> transactionRequestArrayList;

    public UserTransactions() {

    }

    public ArrayList<TransactionRequest> getTransactionRequestArrayList() {

        return (transactionRequestArrayList!=null)?transactionRequestArrayList:new ArrayList<TransactionRequest>();
    }

    public void setTransactionRequestArrayList(ArrayList<TransactionRequest> transactionRequestArrayList) {
        this.transactionRequestArrayList = transactionRequestArrayList;
    }

    public static class TransactionRequest  {


        public String redeemType;
        public boolean isPaid;
        public String paidDate;
        public int remainingCoins;
        public String requestDate;
        public int coins;
        public int amount;
        public String paytmNumber;
        public String upiId;

        public String transactionId;

        public TransactionRequest() {
        }


        public String getPaytmNumber() {
            return paytmNumber;
        }

        public void setPaytmNumber(String paytmNumber) {
            this.paytmNumber = paytmNumber;
        }

        public String getUpiId() {
            return upiId;
        }

        public void setUpiId(String upiId) {
            this.upiId = upiId;
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

        public void setIsPaid(boolean isPaid) {
            this.isPaid = isPaid;
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

