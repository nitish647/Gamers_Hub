package com.nitish.gamershub.view.homePage.activity;

import static com.nitish.gamershub.utils.AppConstants.From;
import static com.nitish.gamershub.utils.AppConstants.IntentData;
import static com.nitish.gamershub.utils.AppHelper.getAdViewedStatsGlobal;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
//import androidx.drawerlayout.widget.DrawerLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nitish.gamershub.databinding.ActivityHomeBinding;
import com.nitish.gamershub.model.firebase.adviewStatus.AdViewStatusHelper;
import com.nitish.gamershub.model.firebase.gamePlayed.GamePlayedHelper;
import com.nitish.gamershub.model.firebase.profileData.ProfileDataHelper;
import com.nitish.gamershub.model.firebase.timerStatus.DailyBonus;
import com.nitish.gamershub.model.firebase.timerStatus.TimerStatusHelper;
import com.nitish.gamershub.model.firebase.userProfile.UserProfileHelper;
import com.nitish.gamershub.model.firebase.userTransaction.UserTransactionHelper;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.adsUtils.AdmobInterstitialAdListener;
import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;
import com.nitish.gamershub.model.firebase.adviewStatus.AdViewedStats;
import com.nitish.gamershub.model.firebase.GamersHubData;
import com.nitish.gamershub.model.firebase.timerStatus.TimerStatus;
import com.nitish.gamershub.model.firebase.userProfile.UserAccountStatus;
import com.nitish.gamershub.model.firebase.userProfile.UserProfile;
import com.nitish.gamershub.model.local.NetWorkTimerResult;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.AppConstants;
import com.nitish.gamershub.utils.NotificationHelper;
import com.nitish.gamershub.view.base.BaseActivity;
import com.nitish.gamershub.view.dialogs.DialogListener;
import com.nitish.gamershub.view.gamePlay.GameDetailActivity2;
import com.nitish.gamershub.view.homePage.fragment.CategoryGamesFragment;
import com.nitish.gamershub.view.homePage.fragment.HomeFragment;
import com.nitish.gamershub.view.homePage.fragment.ProfileFragment;
import com.nitish.gamershub.view.loginSingup.viewmodelRepo.LoginSignUpViewModel;

import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {

    // the whole game data
   ActivityHomeBinding binding;



    HomeFragment homeFragment;
    ProfileFragment profileFragment;

    CategoryGamesFragment categoryGamesFragment;
    Fragment previousFragment;
    boolean interstitialAdDismissed = false;
    private LoginSignUpViewModel viewModel;

    private RewardedAd rewardedAd;
    boolean isLoading;
    ArrayList<GamesItems> mainGamesArrayList;
    DailyBonus dailyBonusToUpdate;


    // firebase auth

    FirebaseFirestore firestoreDb;


    UserProfile updatedUserProfile;

    GamesItems mGamesItems;
    String USAGE_UPDATE_TIMER_STATUS = "USAGE_UPDATE_TIMER_STATUS";
    String USAGE_UPDATE_USER_PROFILE = "USAGE_UPDATE_USER_PROFILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        viewModel = ViewModelProviders.of(this).get(LoginSignUpViewModel.class);
        firestoreDb = FirebaseFirestore.getInstance();
        NotificationHelper.generateFcmToken();

        setViews();

        callGetUserProfile();

        bindObservers();


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });


        // just writing an empty favourite list to avoid null pointer when reading the data

        setBottomNavigationView();


    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    private void bindObservers() {

        viewModel.getUerProfileLD.observe(this, new Observer<NetworkResponse<UserProfile>>() {
            @Override
            public void onChanged(NetworkResponse<UserProfile> response) {
                if (response instanceof NetworkResponse.Success) {
//                    hideLoader();

                    updateUserProfileData(getPreferencesMain().getUserProfile());

                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<UserProfile>) response).getMessage();

                } else if (response instanceof NetworkResponse.Loading) {

                    showLoader();
                }
            }
        });

        viewModel.updateUserProfileLD.observe(this, new Observer<NetworkResponse<Object>>() {
            @Override
            public void onChanged(NetworkResponse<Object> response) {
                if (response instanceof NetworkResponse.Success) {


                    hideLoader();

                    String usage = ((NetworkResponse.Success<Object>) response).getMessage();
                    if (usage.equals(USAGE_UPDATE_USER_PROFILE)) {
                        callGetGamersHubData();
                        checkAndShowRewardBottomDialog();
                    }

                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<Object>) response).getMessage();

//                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

//                    showLoader();
                }
            }
        });

        viewModel.getGamersHubDataLD.observe(this, new Observer<NetworkResponse<GamersHubData>>() {

            @Override
            public void onChanged(NetworkResponse<GamersHubData> response) {
                if (response instanceof NetworkResponse.Success) {
//                    hideLoader();

                    GamersHubData gamersHubData = ((NetworkResponse.Success<GamersHubData>) response).getData();


                    if (!AppHelper.isAppUpdated(getPreferencesMain().getUserProfile())) {
                        showUpdateDialog(gamersHubData);
                    }


                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<GamersHubData>) response).getMessage();

//                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

//                    showLoader();
                }
            }
        });

        viewModel.getNetworkTime.observe(this, new Observer<NetworkResponse<NetWorkTimerResult>>() {

            @Override
            public void onChanged(NetworkResponse<NetWorkTimerResult> response) {
                if (response instanceof NetworkResponse.Success) {
//                    hideLoader();

                    NetWorkTimerResult netWorkTimerResult = ((NetworkResponse.Success<NetWorkTimerResult>) response).getData();


//                    checkDailyBonus(netWorkTimerResult.toString(), dailyBonusToUpdate);


                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<NetWorkTimerResult>) response).getMessage();

//                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

//                    showLoader();
                }
            }
        });

    }


    private void checkAndShowRewardBottomDialog() {
        TimerStatus timerStatus = getPreferencesMain().getUserProfile().getTimerStatus();
        if (!timerStatus.getDailyBonus().getClaimed() || !timerStatus.getWatchViewReward().isClaimed()) {
            showBonusBottomSheet();
        }
    }

    public AdmobInterstitialAdListener interstitialAdListener() {
        return new AdmobInterstitialAdListener() {
            @Override
            public void onAdDismissed() {

                openGameDetailsActivity();
            }

            @Override
            public void onAdShown() {

            }

            @Override
            public void onAdFailed() {

            }

            @Override
            public void onAdLoading() {

                openGameDetailsActivity();
            }
        };
    }


    public void openGameDetailsActivity() {
//        startActivityIntent(HomeActivity.this,GameDetailActivity2.class);
        Intent intent = new Intent(HomeActivity.this, GameDetailActivity2.class);
        intent.putExtra(From, HomeActivity.class.getSimpleName());
        intent.putExtra(IntentData, mGamesItems);
        startActivity(intent);

    }

    public void setBottomNavigationView() {

        homeFragment = HomeFragment.newInstance("", "");
        showHideFragment(homeFragment, homeFragment.getTag());
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.homeMenu:
                        if (homeFragment == null) {
                            homeFragment = HomeFragment.newInstance("", "");

                        }
                        showHideFragment(homeFragment, homeFragment.getTag());


                        break;

                    case R.id.profileMenu:

                        if (profileFragment == null) {
                            profileFragment = profileFragment.newInstance("", "");

                        }
                        showHideFragment(profileFragment, profileFragment.getTag());
                        break;
                    case R.id.categoryGamesMenu:
                        if (categoryGamesFragment == null) {
                            categoryGamesFragment = categoryGamesFragment.newInstance("", "");

                        }
                        showHideFragment(categoryGamesFragment, categoryGamesFragment.getTag());

                }
                return false;
            }
        });
    }

    public void showHideFragment(Fragment fragment, String tag) {
        if (fragment == previousFragment) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (!fragment.isAdded()) {
            fragmentTransaction.add(R.id.frameLayout, fragment, tag);
            if (previousFragment != null) {
                fragmentTransaction.hide(previousFragment);
            }
        } else {
            fragmentTransaction.show(fragment);
            fragmentTransaction.hide(previousFragment);
        }
        fragmentTransaction.commit();
        previousFragment = fragment;
    }

    private void updateUserProfileData(UserProfile userProfile) {

        TimerStatusHelper.checkAndUpdateTimerStatus(userProfile);

        GamePlayedHelper.setGamePlayedStatus(userProfile);

        UserProfileHelper.checkAndUpdateUserProfile(userProfile, new UserProfileHelper.UserProfileListener() {
            @Override
            public void showSuspendedDialog(DialogItems dialogItems) {
                showSuspendDialog(dialogItems, null);
            }
        });


        AdViewStatusHelper.checkAndUpdateAdViewStatus(userProfile);

        // update profile data

        ProfileDataHelper.updateProfileData(userProfile);

        callUpdateUser(userProfile, USAGE_UPDATE_USER_PROFILE);
    }


    public void startIntent(GamesItems gamesItems) {
        mGamesItems = gamesItems;
        showInterstitialAdNew(interstitialAdListener());

    }

    public void setUpBannerAd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.googleBannerAdView.loadAd(adRequest);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        UserProfile userProfile = getPreferencesMain().getUserProfile();
        // upload the ad viewed status when exiting from the app
        if (adStatsChanged()) {


            userProfile.setAdViewedStats(getAdViewedStatsGlobal());
            callUpdateUser(userProfile, "");

        }

    }


    @Override
    protected void onStop() {
        super.onStop();

        interstitialAdDismissed = false;


    }


    public void setHeader() {


//        if (getGoogleSignInAccount() != null) {
//
//            Log.d("pResonse", "google id name" + getGoogleSignInAccount().getDisplayName() + " image " + getGoogleSignInAccount().getPhotoUrl());
//
//        } else {
//            showSimpleToast("current user is null");
//        }


    }

    public void setViews() {


        setHeader();
        loadInterstitialAdNew();
        setUpBannerAd();

    }

    public void showLogOutDialog() {


        DialogItems dialogItems = new DialogItems();
        dialogItems.setTitle(getString(R.string.confirmation));
        dialogItems.setMessage(getString(R.string.do_you_want_to_log_out));
        showConfirmationDialog2(dialogItems, new DialogListener() {
            @Override
            public void onYesClick() {
                performLogOut();
            }

            @Override
            public void onNoClick() {

            }
        });

    }

    public void showUpdateDialog(GamersHubData gamersHubData) {

        DialogItems dialogItems = new DialogItems();
        dialogItems.setTitle(getString(R.string.pending_update));
        dialogItems.setYesTitle(getString(R.string.update));
        dialogItems.setMessage(getString(R.string.a_new_update_of_the_app_has_been_released_please_update));
        if (gamersHubData.getGamesData().isForceUpdate()) {


            showConfirmationDialogSingleButton2(dialogItems, dialogListener);

        } else {

            showConfirmationDialog2(dialogItems, dialogListener);

        }
    }

    private DialogListener dialogListener = new DialogListener() {
        @Override
        public void onYesClick() {
            openPlayStore();
        }

        @Override
        public void onNoClick() {

        }
    };

    private void callGetGamersHubData() {
        viewModel.callGetGamersHub();
    }

    private void callUpdateUser(UserProfile userProfile, String usage) {
        updatedUserProfile = userProfile;
        viewModel.callUpdateUserProfile(userProfile, usage);
    }

    private void callNetworkTime(DailyBonus dailyBonus) {
        dailyBonusToUpdate = dailyBonus;

        viewModel.callNetworkTime(HomeActivity.this);
    }


    private void callGetUserProfile() {
        viewModel.callGetUserProfile();
    }


}


