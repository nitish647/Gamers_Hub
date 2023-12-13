package com.nitish.gamershub.model.firebase.profileData;

import com.nitish.gamershub.model.firebase.userProfile.UserProfile;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.DeviceHelper;
import com.nitish.gamershub.utils.StringHelper;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;
import com.nitish.gamershub.view.base.AppClass;

public class ProfileDataHelper {


    public static void createNewUser()
    {

    }

    public static void updateProfileData(UserProfile userProfile)
    {  ProfileData profileData = userProfile.getProfileData();
        if (profileData != null) {

            if (profileData.getName() == null || profileData.getName().trim().isEmpty()) {
                profileData.setName(ProfileData.getProfileData().getName());
            }
            if (profileData.getEmail() == null || profileData.getEmail().trim().isEmpty()) {
                profileData.setEmail(ProfileData.getProfileData().getEmail());
            }
            profileData.setVersionName(AppHelper.getAppVersionName(AppClass.getInstance()));
            profileData.setFirebaseFcmToken(AppHelper.getFireBaseFcmToken());
            profileData.setLastOpened(DateTimeHelper.getDatePojo().getGetCurrentDateString());
            profileData.setDeviceInfo(DeviceHelper.getDeviceNameAndVersion());

            if (StringHelper.isStringNullOrEmpty(profileData.getCreatedAt().trim()) && DateTimeHelper.isDateCorrect(profileData.getCreatedAt())) {
                profileData.setCreatedAt(DateTimeHelper.getDatePojo().getGetCurrentDateString());
            }
            userProfile.setProfileData(profileData);


        } else {
            ProfileData profileData1 = new ProfileData();
            userProfile.setProfileData(profileData1);
        }
    }
}
