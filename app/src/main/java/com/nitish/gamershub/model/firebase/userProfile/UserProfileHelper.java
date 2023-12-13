package com.nitish.gamershub.model.firebase.userProfile;

import com.nitish.gamershub.R;
import com.nitish.gamershub.model.firebase.userTransaction.UserTransactionHelper;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.utils.AppConstants;
import com.nitish.gamershub.view.base.AppClass;

public class UserProfileHelper {

    public static void checkAndUpdateUserProfile(UserProfile userProfile, UserProfileListener userProfileListener)
    {
        if (userProfile.getUserAccountStatus() == null) {

            UserAccountStatus userAccountStatus = new UserAccountStatus();
            userAccountStatus.setSuspensionInfo(AppClass.getInstance().getString(R.string.suspensionMessage));
            userProfile.setUserAccountStatus(userAccountStatus);

        }
        else {
            getUserAccountStatus(userProfile,userProfileListener);
        }
    }
    private static void getUserAccountStatus(UserProfile userProfile, UserProfileListener userProfileListener) {
        UserAccountStatus userAccountStatus = userProfile.getUserAccountStatus();

        if (userAccountStatus.getAccountStatus() != AppConstants.AccountActive) {

            DialogItems dialogItems = new DialogItems();
            dialogItems.setMessage(userAccountStatus.getSuspensionMessage());
            userProfileListener.showSuspendedDialog(dialogItems);
//            showSuspendDialog(dialogItems, null);
//            showSuspendDialog(userAccountStatus.getSuspensionMessage());
        }

        // updating the user transaction
        UserTransactionHelper.updateTransactionStatus(userProfile);
    }


    public static interface UserProfileListener{
        void showSuspendedDialog(DialogItems dialogItems);
    }
}
