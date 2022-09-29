package com.nitish.gamershub.Utils;

import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHub_ParentCollection;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nitish.gamershub.Pojo.FireBase.UserProfile;

import io.paperdb.Paper;

public class UserOperations {


    public static  DocumentReference getFirestoreUser()
    {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

       return firebaseFirestore.collection(GamersHub_ParentCollection).document(Paper.book().read(ConstantsHelper.UserMail)+"");



    }


    public static UserProfile.ProfileData addCoinsToWallet(UserProfile userProfile, int amount)
    {



        UserProfile.ProfileData profileData = userProfile.getProfileData();

        int totalCoins = profileData.getGameCoins()+amount;


            profileData.setGameCoins(totalCoins);

        userProfile.setProfileData(profileData);

        return profileData;
    }
}
