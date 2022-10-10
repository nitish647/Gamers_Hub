package com.nitish.gamershub.Pojo.FireBase;

public class UserAccountStatus {


    int accountStatus =0;
    String suspendedDate="";
    String suspensionMessage;
    String suspensionInfo ="";

    public int getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getSuspendedDate() {
        return suspendedDate;
    }

    public void setSuspendedDate(String suspendedDate) {
        this.suspendedDate = suspendedDate;
    }

    public String getSuspensionMessage() {
        return suspensionMessage;
    }

    public void setSuspensionMessage(String suspensionMessage) {
        this.suspensionMessage = suspensionMessage;
    }

    public String getSuspensionInfo() {
        return suspensionInfo;
    }

    public void setSuspensionInfo(String suspensionInfo) {
        this.suspensionInfo = suspensionInfo;
    }
}
