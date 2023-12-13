package com.nitish.gamershub.view.loginSingup.viewmodelRepo;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.google.firebase.firestore.SetOptions;
import com.nitish.gamershub.R;
import com.nitish.gamershub.apiUtils.ApiService;
import com.nitish.gamershub.model.firebase.FirebaseDataPassingHelper;
import com.nitish.gamershub.model.firebase.GamersHubData;
import com.nitish.gamershub.model.firebase.RedeemCoins;
import com.nitish.gamershub.model.firebase.userProfile.UserProfile;
import com.nitish.gamershub.model.bannerImagesData.ResponseBannerImages;
import com.nitish.gamershub.model.gamersHubMaterData.AllGamesResponseItem;
import com.nitish.gamershub.model.local.NetWorkTimerResult;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.firebaseUtils.FireBaseService;
import com.nitish.gamershub.view.base.BaseRepository;

public class LoginSignupRepository extends BaseRepository {




    private MutableLiveData<NetworkResponse<UserProfile>> getUserProfileMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<UserProfile>> getUserProfileLD = getUserProfileMLD;


    public void callGetUserProfile(String userMail) {

        getFireBaseDocumentReference(FireBaseService.getFirebaseUser(userMail), getUserProfileMLD, UserProfile.class);

    }


    private MutableLiveData<NetworkResponse<Object>> registerUserProfileMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<Object>> registerUserProfileLD = registerUserProfileMLD;

    public void callRegisterUserProfile(UserProfile userProfile) {

        FirebaseDataPassingHelper<Object> firebaseDataPassing = new FirebaseDataPassingHelper<>();
        firebaseDataPassing.setMutableLiveData(registerUserProfileMLD);
        firebaseDataPassing.setDocumentReference(FireBaseService.getFirebaseUser(userProfile.getProfileData().getEmail()));
        firebaseDataPassing.setDataObject(userProfile);
        firebaseDataPassing.setSetOptions(SetOptions.merge());

        setFirebaseDocumentReference(firebaseDataPassing);

    }

    private MutableLiveData<NetworkResponse<Object>> updateUserProfileMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<Object>> updateUserProfileLD = updateUserProfileMLD;


    public void callUpdateUserProfile(UserProfile userProfile, String message) {

        FirebaseDataPassingHelper<Object> firebaseDataPassing = new FirebaseDataPassingHelper<>();
        firebaseDataPassing.setMutableLiveData(updateUserProfileMLD);
        firebaseDataPassing.setDocumentReference(FireBaseService.getFirebaseUser(userProfile.getProfileData().getEmail()));
        firebaseDataPassing.setDataObject(userProfile);
        firebaseDataPassing.setMessage(message);
        firebaseDataPassing.setSetOptions(null);

        setFirebaseDocumentReference(firebaseDataPassing);

    }

    private MutableLiveData<NetworkResponse<GamersHubData>> getGamersHubDataMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<GamersHubData>> getGamersHubDataLD = getGamersHubDataMLD;

    public void callGetGamersHubData() {

        getFireBaseDocumentReference(FireBaseService.getFirebaseGamersHubData(), getGamersHubDataMLD, GamersHubData.class);

    }


    public MutableLiveData<NetworkResponse<RedeemCoins>> getRedeemCoinsMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<RedeemCoins>> getRedeemCoinsLD = getRedeemCoinsMLD;

    public void getGamersHubRedeemCoinsList() {

        getFireBaseDocumentReference(FireBaseService.getGamersHubRedeemCoinsList(), getRedeemCoinsMLD, RedeemCoins.class);

    }

    public MutableLiveData<NetworkResponse<Object>> setRedeemCoinsMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<Object>> setRedeemCoinsLD = setRedeemCoinsMLD;

    public void callSetGamersHubRedeemCoinsList(RedeemCoins redeemCoins) {


        FirebaseDataPassingHelper<Object> firebaseDataPassing = new FirebaseDataPassingHelper<>();
        firebaseDataPassing.setMutableLiveData(setRedeemCoinsMLD);
        firebaseDataPassing.setDocumentReference(FireBaseService.getGamersHubRedeemCoinsList());
        firebaseDataPassing.setDataObject(redeemCoins);
        firebaseDataPassing.setSetOptions(null);
        setFirebaseDocumentReference(firebaseDataPassing);
    }

    private MutableLiveData<NetworkResponse<NetWorkTimerResult>> getNetworkTimeMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<NetWorkTimerResult>> getNetworkTimeLD = getNetworkTimeMLD;

    public void callGetNetworkTime(Context context) {


        callVolleyRequest(context,Request.Method.GET, getNetworkTimeMLD, context.getString(R.string.getCurrentTimeAsiaKolkata), NetWorkTimerResult.class);

    }

    private MutableLiveData<NetworkResponse<AllGamesResponseItem>> getGamersHubMaterDataMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<AllGamesResponseItem>> getGamersHubMaterDataLD = getGamersHubMaterDataMLD;

    public void callGetGamersHubMaterData(Context context) {


        callVolleyRequest(context,Request.Method.GET, getGamersHubMaterDataMLD, ApiService.materJsonUrl, AllGamesResponseItem.class);

    }


    private MutableLiveData<NetworkResponse<ResponseBannerImages>> getBannerDataMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<ResponseBannerImages>> getBannerDataLD = getBannerDataMLD;

    public void callGetBannerData(Context context) {


        callVolleyRequest(context,Request.Method.GET, getBannerDataMLD, ApiService.bannerJsonUrl, ResponseBannerImages.class);

    }


}
