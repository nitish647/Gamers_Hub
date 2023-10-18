package com.nitish.gamershub.view.loginSingup.viewmodelRepo;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.google.firebase.firestore.SetOptions;
import com.nitish.gamershub.R;
import com.nitish.gamershub.model.firebase.GamersHubData;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.model.local.NetWorkTimerResult;
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

    private MutableLiveData<NetworkResponse<GamersHubData>> getGamersHubDataMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<GamersHubData>> getGamersHubDataLD = getGamersHubDataMLD;

    public void callGetGamersHubData() {

        getFireBaseDocumentReference(FireBaseService.getFirebaseUser(), getGamersHubDataMLD, GamersHubData.class);

    }

    private MutableLiveData<NetworkResponse<NetWorkTimerResult>> getNetworkTimeMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<NetWorkTimerResult>> getNetworkTimeLD = getNetworkTimeMLD;

    public void callGetNetworkTime(Context context) {


        callVolleyRequest( Request.Method.GET,getNetworkTimeMLD,context.getString(R.string.getCurrentTimeAsiaKolkata),NetWorkTimerResult.class);

    }


}
