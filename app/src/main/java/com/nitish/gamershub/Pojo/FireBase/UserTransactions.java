package com.nitish.gamershub.Pojo.FireBase;

import java.io.Serializable;
import java.util.ArrayList;

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
        public boolean transactionComplete;
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

        public String getRedeemType() {
            return redeemType;
        }

        public void setRedeemType(String redeemType) {
            this.redeemType = redeemType;
        }

        public boolean isTransactionComplete() {
            return transactionComplete;
        }

        public void setTransactionComplete(boolean transactionComplete) {
            this.transactionComplete = transactionComplete;
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

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }
    }


}

