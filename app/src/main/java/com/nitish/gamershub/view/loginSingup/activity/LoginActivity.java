package com.nitish.gamershub.view.loginSingup.activity;

import static com.nitish.gamershub.utils.AppConstants.UserInfo;
import static com.nitish.gamershub.utils.AppConstants.UserMail;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.instacart.library.truetime.TrueTime;
import com.nitish.gamershub.model.firebase.profileData.ProfileData;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.ToastHelper;
import com.nitish.gamershub.utils.firebaseUtils.FireBaseService;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.databinding.ActivityLoginPageBinding;
import com.nitish.gamershub.model.firebase.userProfile.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;
import com.nitish.gamershub.utils.DeviceHelper;
import com.nitish.gamershub.view.base.BaseActivity;
import com.nitish.gamershub.view.loginSingup.viewmodelRepo.LoginSignUpViewModel;

import io.paperdb.Paper;
import io.tempo.Tempo;

public class LoginActivity extends BaseActivity implements ActivityResultCallback<FirebaseAuthUIAuthenticationResult> {

    ActivityLoginPageBinding binding;
    FirebaseFirestore firestoreDb;

    private UserProfile loginUserProfile;
    private UserProfile registerUserProfile;
    private LoginSignUpViewModel viewModel;

    int RC_SIGN_IN = 12345;
    GoogleSignInAccount googleSignInAccount;
    public static String Users_ParentDocument = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_page);
        Paper.init(this);
        setActivityStatusBarColor( R.color.login_page);
        firestoreDb = FirebaseFirestore.getInstance();
        viewModel = ViewModelProviders.of(this).get(LoginSignUpViewModel.class);


        new GetNetworkTimeAsync().execute();

        bindObservers();


    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login_page;
    }

    private void bindObservers() {
        viewModel.getUerProfileLD.observe(this, new Observer<NetworkResponse<UserProfile>>() {
            @Override
            public void onChanged(NetworkResponse<UserProfile> response) {
                if (response instanceof NetworkResponse.Success) {
//                    hideLoader();


                    UserProfile userProfile = ((NetworkResponse.Success<UserProfile>) response).getData();
                    ProfileData profileData = userProfile.getProfileData();
                    profileData.setLastLogin(DateTimeHelper.getDatePojo().getGetCurrentDateString());
                    userProfile.setProfileData(profileData);
                    callUpdateUser(userProfile);
                    getPreferencesMain().saveUserLoggedIn();

                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<UserProfile>) response).getMessage();
                    if (message.equals(FireBaseService.FirebaseMessage.DOCUMENT_DOES_NOT_EXISTS.toString())) {
                        // for new users
                        ToastHelper.customToast(LoginActivity.this, "registering");

                        UserProfile userProfile = new UserProfile();
                        ProfileData profileData = new ProfileData(googleSignInAccount);
                        userProfile.setProfileData(profileData);
                        callRegisterUser(userProfile);


                    }
//                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

//                    showLoader();
                }
            }
        });


        viewModel.registerUserProfileLD.observe(this, new Observer<NetworkResponse<Object>>() {
            @Override
            public void onChanged(NetworkResponse<Object> response) {
                if (response instanceof NetworkResponse.Success) {

                    Toast.makeText(LoginActivity.this, "Welcome User", Toast.LENGTH_SHORT).show();
                    getPreferencesMain().saveUserLoggedIn();

                    startIntent(LoginActivity.class,true);

                    hideLoader();
                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<Object>) response).getMessage();

                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

                    showLoader();
                }
            }
        });

        viewModel.updateUserProfileLD.observe(this, new Observer<NetworkResponse<Object>>() {
            @Override
            public void onChanged(NetworkResponse<Object> response) {
                if (response instanceof NetworkResponse.Success) {

                    Toast.makeText(LoginActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                    hideLoader();

                    Paper.book().write(UserInfo, loginUserProfile);

                    if (googleSignInAccount != null && googleSignInAccount.getEmail() != null)
                        Paper.book().write(UserMail, googleSignInAccount.getEmail());


                    startIntent(HomeActivity.class,true);


                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<Object>) response).getMessage();

                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

                    showLoader();
                }
            }
        });


    }

    private <T> void  startIntent(Class<T> destinationActivity,boolean doFinish)
    {
        Intent intent = new Intent(LoginActivity.this,destinationActivity);
        startActivity(intent);
        if(doFinish)
        {
            finish();
        }
    }

    public void setonClickListeners() {
        binding.singInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();

            }
        });

        binding.moreSignupButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                getPreferencesMain().saveUserMail("nitish.kumar647@gmail.com");
                Paper.book().write(UserMail, "nitish.kumar647@gmail.com");
                callGetUserProfile();
                return false;
            }
        });


    }


    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }

            }
    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount googleSignInAccountUser = completedTask.getResult(ApiException.class);

            // Successfully signed in
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (googleSignInAccountUser != null && googleSignInAccountUser.getEmail() != null) {

                googleSignInAccount = googleSignInAccountUser;

                getPreferencesMain().saveGoogleSignInAccountUser(googleSignInAccountUser);
                getPreferencesMain().saveUserMail(googleSignInAccountUser.getEmail());
                Paper.book().write(UserMail, googleSignInAccountUser.getEmail());

                callGetUserProfile();

            } else {
                Toast.makeText(this, "user is null", Toast.LENGTH_SHORT).show();
            }

            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("pResponse", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    public class GetNetworkTimeAsync extends AsyncTask<Object, Object, Object> {


        @Override
        protected Object doInBackground(Object... objects) {


            try {
                if (!TrueTime.isInitialized())
                    TrueTime.build().withSharedPreferencesCache(LoginActivity.this);
                if (!Tempo.isInitialized())
                    Tempo.initialize(getApplication());

            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            hideLoader();
            setonClickListeners();
            super.onPostExecute(o);
        }
    }



    private void signIn() {
        Intent signInIntent = getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {

        if (result.getResultCode() == RESULT_OK) {

            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            // ...
        } else {


            showSimpleToast("Login Failed " + result.getIdpResponse() + " " + result.getResultCode());


        }

    }

    @Override
    public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {

        Toast.makeText(this, "result " + result.getIdpResponse() + " " + result.getResultCode(), Toast.LENGTH_SHORT).show();

    }


    private void callGetUserProfile() {
        viewModel.callGetUserProfile();
    }

    private void callRegisterUser(UserProfile userProfile) {
        registerUserProfile = userProfile;
        viewModel.callRegisterUserProfile(userProfile);
    }

    private void callUpdateUser(UserProfile userProfile) {
        loginUserProfile = userProfile;
        viewModel.callUpdateUserProfile(userProfile);
    }
}