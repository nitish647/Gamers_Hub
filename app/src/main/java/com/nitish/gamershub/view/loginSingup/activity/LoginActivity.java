package com.nitish.gamershub.view.loginSingup.activity;

import static com.nitish.gamershub.utils.AppConstants.UserInfo;
import static com.nitish.gamershub.utils.AppConstants.UserMail;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.ToastHelper;
import com.nitish.gamershub.utils.firebaseUtils.FireBaseService;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.databinding.ActivityLoginPageBinding;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;
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
        viewModel.loginUserLD.observe(this, new Observer<NetworkResponse<UserProfile>>() {
            @Override
            public void onChanged(NetworkResponse<UserProfile> response) {
                if (response instanceof NetworkResponse.Success) {
                    dismissProgressBar();


                    UserProfile userProfile = ((NetworkResponse.Success<UserProfile>) response).getData();
                    UserProfile.ProfileData profileData = userProfile.getProfileData();
                    profileData.setLastLogin(DateTimeHelper.getDatePojo().getGetCurrentDateString());
                    userProfile.setProfileData(profileData);
                    callUpdateUser(userProfile);


                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<UserProfile>) response).getMessage();
                    if (message.equals(FireBaseService.FirebaseMessage.DOCUMENT_DOES_NOT_EXISTS.toString())) {
                        // for new users
                        ToastHelper.customToast(LoginActivity.this, "registering");

                        UserProfile userProfile = new UserProfile();

                        UserProfile.ProfileData profileData = new UserProfile.ProfileData();

                        // Find todays date
                        profileData.setLastLogin(DateTimeHelper.getDatePojo().getGetCurrentDateString());
                        profileData.setCreatedAt(DateTimeHelper.getDatePojo().getGetCurrentDateString());
                        profileData.setDeviceInfo(DeviceHelper.getDeviceNameAndVersion());
                        profileData.setEmail(googleSignInAccount.getEmail());
                        profileData.setName(googleSignInAccount.getDisplayName());
                        userProfile.setProfileData(profileData);
                        callRegisterUser(userProfile);
                    }
                    dismissProgressBar();
                } else if (response instanceof NetworkResponse.Loading) {

                    showProgressBar();
                }
            }
        });


        viewModel.registerUserProfileLD.observe(this, new Observer<NetworkResponse<Object>>() {
            @Override
            public void onChanged(NetworkResponse<Object> response) {
                if (response instanceof NetworkResponse.Success) {

                    Toast.makeText(LoginActivity.this, "Welcome User", Toast.LENGTH_SHORT).show();
                    Paper.book().write(UserMail, googleSignInAccount.getEmail());
                    Paper.book().write(UserInfo, registerUserProfile);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                    dismissProgressBar();
                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<Object>) response).getMessage();

                    dismissProgressBar();
                } else if (response instanceof NetworkResponse.Loading) {

                    showProgressBar();
                }
            }
        });

        viewModel.updateUserProfileLD.observe(this, new Observer<NetworkResponse<Object>>() {
            @Override
            public void onChanged(NetworkResponse<Object> response) {
                if (response instanceof NetworkResponse.Success) {

                    Toast.makeText(LoginActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                    dismissProgressBar();

                    Paper.book().write(UserInfo, loginUserProfile);
                    Paper.book().write(UserMail, googleSignInAccount.getEmail());
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<Object>) response).getMessage();

                    dismissProgressBar();
                } else if (response instanceof NetworkResponse.Loading) {

                    showProgressBar();
                }
            }
        });


    }

    public void setonClickListeners() {
        binding.singInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();

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
                AppHelper.saveGoogleSignInAccountUser(googleSignInAccountUser);

                Paper.book().write(UserMail, googleSignInAccountUser.getEmail());

                callLoginUser();
//
//                getUserProfileGlobal(new GetUserProfileDataListener() {
//                    @Override
//                    public void onTaskSuccessful(UserProfile userProfile) {
//
//                    }
//                });
//                firestoreDb.collection(GamersHub_ParentCollection).document(googleSignInAccountUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        DocumentSnapshot documentSnapshot = task.getResult();
//                        if (task.isSuccessful()) {
//
//                            if (documentSnapshot.exists()) {
//
//                                // for existing users
//
//
//                                UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
//
//
//                                UserProfile.ProfileData profileData = userProfile.getProfileData();
//
//
//                                profileData.setLastLogin(DateTimeHelper.getDatePojo().getGetCurrentDateString());
//
//
//                                userProfile.setProfileData(profileData);
//
//
//                                firestoreDb.collection(GamersHub_ParentCollection).document(googleSignInAccountUser.getEmail()).set(userProfile, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//
//                                        Toast.makeText(LoginActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
//
//                                        Paper.book().write(UserInfo, userProfile);
//
//
//                                        startIntentForHome();
//
//                                    }
//                                });
//                            } else {
//
//
//                                // for new users
//
//                                UserProfile userProfile = new UserProfile();
//
//                                UserProfile.ProfileData profileData = new UserProfile.ProfileData();
//
//                                // Find todays date
//                                profileData.setLastLogin(DateTimeHelper.getDatePojo().getGetCurrentDateString());
//                                profileData.setCreatedAt(DateTimeHelper.getDatePojo().getGetCurrentDateString());
//                                profileData.setDeviceInfo(DeviceHelper.getDeviceNameAndVersion());
//                                profileData.setEmail(googleSignInAccountUser.getEmail());
//                                profileData.setName(googleSignInAccountUser.getDisplayName());
//
//                                userProfile.setProfileData(profileData);
//                                firestoreDb.collection(GamersHub_ParentCollection).document(googleSignInAccountUser.getEmail()).set(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//
//                                        Toast.makeText(LoginActivity.this, "Welcome User", Toast.LENGTH_SHORT).show();
//                                        Paper.book().write(UserInfo, userProfile);
//
//                                        AppHelper.saveGoogleSignInAccountUser(googleSignInAccountUser);
//                                        startIntentForHome();
//                                    }
//                                });
//
//                            }
//
//                        }
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(LoginActivity.this, "failed " + e, Toast.LENGTH_SHORT).show();
//
//                    }
//                });
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

            dismissProgressBar();
            setonClickListeners();
            super.onPostExecute(o);
        }
    }

    public void startIntentForHome() {


        startActivityIntent(LoginActivity.this, HomeActivity.class);
        finish();
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


            Toast.makeText(this, "Login Failed " + result.getIdpResponse() + " " + result.getResultCode(), Toast.LENGTH_SHORT).show();


        }

    }

    @Override
    public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {

        Toast.makeText(this, "result " + result.getIdpResponse() + " " + result.getResultCode(), Toast.LENGTH_SHORT).show();

    }


    private void callLoginUser() {
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