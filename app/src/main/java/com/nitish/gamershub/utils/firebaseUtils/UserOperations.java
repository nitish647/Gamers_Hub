package com.nitish.gamershub.utils.firebaseUtils;

import static com.nitish.gamershub.utils.AppConstants.GamersHub_ParentCollection;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nitish.gamershub.model.firebase.UserProfile;

public class UserOperations {


    public static DocumentReference getFireStoreUser(String userMail) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        return firebaseFirestore.collection(GamersHub_ParentCollection).document(userMail);


    }


    public static UserProfile.ProfileData addCoinsToWallet(UserProfile userProfile, int amount) {


        UserProfile.ProfileData profileData = userProfile.getProfileData();

        int totalCoins = profileData.getGameCoins() + amount;


        profileData.setGameCoins(totalCoins);

        userProfile.setProfileData(profileData);

        return profileData;
    }
}
