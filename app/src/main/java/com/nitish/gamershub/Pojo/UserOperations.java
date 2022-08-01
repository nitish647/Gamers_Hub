package com.nitish.gamershub.Pojo;

public class UserOperations {




    public UserProfile updateWallet(int amount)
    {
        UserProfile userProfile = new UserProfile();

        UserProfile.ProfileData profileData = new UserProfile.ProfileData();

        profileData.setGamePoints(0);

        userProfile.setProfileData(profileData);

        return userProfile;
    }
}
