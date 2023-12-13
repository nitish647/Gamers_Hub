package com.nitish.gamershub.view.loginSingup.activity;

import static com.nitish.gamershub.utils.AppConstants.IntentData;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import com.nitish.gamershub.model.gamersHubMaterData.AllGamesResponseItem;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.databinding.ActivitySplashScreenBinding;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.NotificationHelper;
import com.nitish.gamershub.view.base.BaseActivity;
import com.nitish.gamershub.view.loginSingup.viewmodelRepo.LoginSignUpViewModel;

import io.paperdb.Paper;

public class SplashScreen extends BaseActivity {
    ActivitySplashScreenBinding binding;

    SharedPreferences sh;
    SharedPreferences.Editor editor;
    String data;
    String gameData;
    RequestQueue requestQueue;

    private Context context;
    public static String MaterData = "MasterData";
    private FirebaseAuth mAuth;
    private LoginSignUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(LoginSignUpViewModel.class);
        setActivityStatusBarColor( R.color.splash_screen);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash__screen);
        requestQueue = Volley.newRequestQueue(this);
        mAuth = FirebaseAuth.getInstance();
        context = SplashScreen.this;
        Paper.init(this);


        handlerFunctions();
        bindObservers();

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_splash__screen;
    }

    public void bindObservers() {

        viewModel.getGamersHubMaterData.observe(this, new Observer<NetworkResponse<AllGamesResponseItem>>() {
            @Override
            public void onChanged(NetworkResponse<AllGamesResponseItem> response) {
                if (response instanceof NetworkResponse.Success) {


                    hideLoader();

                    AllGamesResponseItem response1 = ((NetworkResponse.Success<AllGamesResponseItem>) response).getData();


                    saveGamersMastersHubData();


                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<AllGamesResponseItem>) response).getMessage();

                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

                    showLoader();
                }
            }
        });


    }


    private void handlerFunctions() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
//                get_data();
                callGetGamersHubMaterData();
//                SplashScreen.this.getMaterData();

            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.warning.setText(R.string.internet_is_slow);
            }
        }, 7000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.lottieAnimationLoading.pauseAnimation();
                binding.warning.setText(R.string.please_check_your_internet_connection_and_try_again);
            }
        }, 11000);
    }


    private void saveGamersMastersHubData() {


        //      FirebaseUser currentUser = mAuth.getCurrentUser();


        // when not singed in
        Intent intent;
        if(!isUserLoggedIn())
        {
            intent = new Intent(SplashScreen.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);


        }
        else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey("type")) {
                intent = NotificationHelper.createNotificationIntent(bundle.getString("type"), context);
                intent.putExtra(IntentData, bundle);

            } else {
                intent = new Intent(getApplicationContext(), HomeActivity.class);
            }
        }

//        if (getGoogleSignInAccount() == null) {
//            intent = new Intent(SplashScreen.this, LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//
//        } else
//        // when already  singed in
//        {
//            Toast.makeText(SplashScreen.this, "hello " + getGoogleSignInAccount().getEmail(), Toast.LENGTH_SHORT).show();
//
//
//        }
        startIntentWithFlags(intent, SplashScreen.this);
        finish();


    }

    private boolean isUserLoggedIn()
    {
        return  getPreferencesMain().isUserLoggedIn();
    }

    private void callGetGamersHubMaterData() {


        viewModel.callGetGamersHubJson(this);
    }

}