package com.nitish.gamershub.view.loginSingup.viewmodelRepo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.utils.NetworkResponse;

public class LoginSignUpViewModel extends ViewModel {

    LoginSignupRepository loginSignupRepository = new LoginSignupRepository();




    public LiveData<NetworkResponse<UserProfile>> loginUserLD = loginSignupRepository.getLoginUserLD;


    public void callLoginUser() {

        loginSignupRepository.callLoginUser();

    }

    public LiveData<NetworkResponse<Object>> registerUserProfileLD = loginSignupRepository.registerUserProfileLD;

    public void callRegisterUserProfile(UserProfile userProfile) {

        loginSignupRepository.callRegisterUserProfile(userProfile);

    }
    public LiveData<NetworkResponse<Object>> updateUserProfileLD = loginSignupRepository.updateUserProfileLD;

    public void callUpdateUserProfile(UserProfile userProfile) {

        loginSignupRepository.callUpdateUserProfile(userProfile);

    }



}
