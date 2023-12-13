package com.nitish.gamershub.model.firebase.userTransaction;

import com.nitish.gamershub.model.firebase.userProfile.UserAccountStatus;
import com.nitish.gamershub.model.firebase.userProfile.UserProfile;

public class UserTransactionHelper {


    public static void updateTransactionStatus(UserProfile userProfile) {
        UserTransactions userTransactions = userProfile.getUserTransactions();
        int isTransactionPending = 0;
        if (userTransactions != null && userTransactions.getTransactionRequestArrayList() != null) {
            for (UserTransactions.TransactionRequest transactionRequest : userTransactions.getTransactionRequestArrayList()) {


                // todo
//                 if((transactionRequest.transactionStatus == null) )
//                     continue;
                // add  a condition with transactionRequest.isTransactionComplete
//                 if ( !transactionRequest.isTransactionComplete() ||
//                         transactionRequest.transactionStatus.equals(ConstantsHelper.TransactionStatusPending))
//
                if (!transactionRequest.isTransactionComplete()) {
                    isTransactionPending = 1;
                    break;
                }
            }
        }
        UserAccountStatus userAccountStatus = userProfile.getUserAccountStatus();
        userAccountStatus.setAnyTransactionsPending(isTransactionPending);
    }

}
