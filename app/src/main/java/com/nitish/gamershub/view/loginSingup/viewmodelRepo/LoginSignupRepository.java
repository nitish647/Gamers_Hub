package com.nitish.gamershub.view.loginSingup.viewmodelRepo;

import static com.nitish.gamershub.utils.AppHelper.saveUserProfileGlobal;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.utils.DataPassingHelper;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.firebaseUtils.FireBaseService;
import com.nitish.gamershub.view.base.BaseActivity;
import com.nitish.gamershub.view.base.BaseRepository;

import org.json.JSONObject;

public class LoginSignupRepository extends BaseRepository {


    private MutableLiveData<NetworkResponse<UserProfile>> getUserProfileMLD = new MutableLiveData<>();
    public LiveData<NetworkResponse<UserProfile>> getUserProfileLD = getUserProfileMLD;



    public void callGetUserProfileGlobal() {

        getFireBaseDocumentReference(FireBaseService.getFirebaseUser(), getUserProfileMLD,UserProfile.class);


    }


}
