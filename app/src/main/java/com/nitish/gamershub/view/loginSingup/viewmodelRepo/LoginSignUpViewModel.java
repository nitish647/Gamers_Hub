package com.nitish.gamershub.view.loginSingup.viewmodelRepo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.utils.NetworkResponse;

public class LoginSignUpViewModel extends ViewModel {

    LoginSignupRepository loginSignupRepository = new LoginSignupRepository();




    public LiveData<NetworkResponse<UserProfile>> getUserProfileLD = loginSignupRepository.getUserProfileLD;


    private void callGetUserProfile() {

        loginSignupRepository.callGetUserProfileGlobal();

    }



}
