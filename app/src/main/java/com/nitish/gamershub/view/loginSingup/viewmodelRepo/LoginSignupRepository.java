package com.nitish.gamershub.view.loginSingup.viewmodelRepo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.SetOptions;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.firebaseUtils.FireBaseService;
import com.nitish.gamershub.view.base.BaseRepository;

public class LoginSignupRepository extends BaseRepository {


    private MutableLiveData<NetworkResponse<UserProfile>> getLoginUserMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<UserProfile>> getLoginUserLD = getLoginUserMLD;


    public void callLoginUser() {

        getFireBaseDocumentReference(FireBaseService.getFirebaseUser(), getLoginUserMLD,UserProfile.class);

    }


    private MutableLiveData<NetworkResponse<Object>> registerUserProfileMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<Object>> registerUserProfileLD = registerUserProfileMLD;

    public void callRegisterUserProfile(UserProfile userProfile) {

        setFirebaseDocumentReference(FireBaseService.getFirebaseUser(), registerUserProfileMLD,userProfile, SetOptions.merge());

    }
    private MutableLiveData<NetworkResponse<Object>> updateUserProfileMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<Object>> updateUserProfileLD = updateUserProfileMLD;


    public void callUpdateUserProfile(UserProfile userProfile) {

        setFirebaseDocumentReference(FireBaseService.getFirebaseUser(), updateUserProfileMLD,userProfile, null);

    }


}
