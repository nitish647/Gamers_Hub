package com.nitish.gamershub.view.homePage.activity;

import static com.nitish.gamershub.utils.AppConstants.From;
import static com.nitish.gamershub.utils.AppConstants.IntentData;
import static com.nitish.gamershub.utils.AppHelper.getAdViewedStatsGlobal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.ToastHelper;
import com.nitish.gamershub.utils.adsUtils.AdmobInterstitialAdListener;
import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;
import com.nitish.gamershub.model.firebase.AdViewedStats;
import com.nitish.gamershub.model.firebase.GamePlayedStatus;
import com.nitish.gamershub.model.firebase.GamersHubData;
import com.nitish.gamershub.model.firebase.TimerStatus;
import com.nitish.gamershub.model.firebase.UserAccountStatus;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.model.firebase.UserTransactions;
import com.nitish.gamershub.model.firebase.WatchViewReward;
import com.nitish.gamershub.model.local.NetWorkTimerResult;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.AppConstants;
import com.nitish.gamershub.utils.firebaseUtils.FireBaseService;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;
import com.nitish.gamershub.utils.DeviceHelper;
import com.nitish.gamershub.utils.NotificationHelper;
import com.nitish.gamershub.view.base.BaseActivity;
import com.nitish.gamershub.view.dialogs.DialogListener;
import com.nitish.gamershub.view.gamePlay.GameDetailActivity2;
import com.nitish.gamershub.view.homePage.fragment.CategoryGamesFragment;
import com.nitish.gamershub.view.homePage.fragment.HomeFragment;
import com.nitish.gamershub.view.homePage.fragment.ProfileFragment;
import com.nitish.gamershub.view.loginSingup.activity.LoginActivity;
import com.nitish.gamershub.view.loginSingup.viewmodelRepo.LoginSignUpViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends BaseActivity {

    // the whole game data
    JSONObject masterDataJsonObject;

    ActivityHomeBinding homeBinding;


    int currentSelectedFragPosition = 0;


    HomeFragment homeFragment;
    ProfileFragment profileFragment;

    CategoryGamesFragment categoryGamesFragment;
    Fragment previousFragment;
    boolean interstitialAdDismissed = false;
    private LoginSignUpViewModel viewModel;

    private RewardedAd rewardedAd;
    boolean isLoading;
    ArrayList<GamesItems> mainGamesArrayList;
    TimerStatus.DailyBonus dailyBonusToUpdate;


    // firebase auth

    FirebaseFirestore firestoreDb;

    boolean flag_DailyBonusWatched = true, flag_rewardVideoWatched = true;

    UserProfile updatedUserProfile;

    GamesItems mGamesItems;
    String USAGE_UPDATE_TIMER_STATUS = "USAGE_UPDATE_TIMER_STATUS";
    String USAGE_UPDATE_USER_PROFILE = "USAGE_UPDATE_USER_PROFILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

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


//                    Paper.book().write(UserInfo, updatedUserProfile);

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
        if (!timerStatus.dailyBonus.getClaimed() || !timerStatus.watchViewReward.isClaimed()) {
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
        homeBinding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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

        if (userProfile.getTimerStatus() != null) {

            setTimerStatus(userProfile);


        } else {
            userProfile.setTimerStatus(createTimerStatus());
        }


        setGamePlayedStatus(userProfile);


        if (userProfile.getUserAccountStatus() == null) {
            UserAccountStatus userAccountStatus = new UserAccountStatus();
            userAccountStatus.setSuspensionInfo(getString(R.string.suspensionMessage));
            userProfile.setUserAccountStatus(userAccountStatus);
        } else {
            getUserAccountStatus(userProfile);
        }

        if (userProfile.getAdViewedStats() == null) {
            userProfile.setAdViewedStats(new AdViewedStats());
        }

        // update profile data

        UserProfile.ProfileData profileData = userProfile.getProfileData();

        if (profileData != null) {

            if (profileData.getName() == null || profileData.getName().trim().isEmpty()) {
                profileData.setName(UserProfile.ProfileData.getProfileData().getName());
            }
            if (profileData.getEmail() == null || profileData.getEmail().trim().isEmpty()) {
                profileData.setEmail(UserProfile.ProfileData.getProfileData().getEmail());
            }
            profileData.setVersionName(AppHelper.getAppVersionName(HomeActivity.this));
            profileData.setFirebaseFcmToken(AppHelper.getFireBaseFcmToken());
            profileData.setLastOpened(DateTimeHelper.getDatePojo().getGetCurrentDateString());
            profileData.setDeviceInfo(DeviceHelper.getDeviceNameAndVersion());
            if (profileData.getCreatedAt().trim().isEmpty() && DateTimeHelper.isDateCorrect(profileData.getCreatedAt())) {
                profileData.setCreatedAt(DateTimeHelper.getDatePojo().getGetCurrentDateString());
            }
            userProfile.setProfileData(profileData);


        } else {
            UserProfile.ProfileData profileData1 = new UserProfile.ProfileData();
            userProfile.setProfileData(profileData1);
        }

        callUpdateUser(userProfile, USAGE_UPDATE_USER_PROFILE);
    }

    public TimerStatus createTimerStatus() {
        TimerStatus timerStatus = new TimerStatus();


        // DAILY BONUS
        TimerStatus.DailyBonus dailyBonus = new TimerStatus.DailyBonus();

        dailyBonus.setClaimed(false);


        String currentDateTime = DateTimeHelper.getDatePojo().getGetCurrentDateString();

        dailyBonus.setLastResetDateTime(DateTimeHelper.resetDateToATime(currentDateTime, DateTimeHelper.time_7_am));
        dailyBonus.setClaimedDate(currentDateTime);

        timerStatus.setDailyBonus(dailyBonus);

        // watchViewReward
        WatchViewReward watchViewReward = new WatchViewReward();
        watchViewReward.setClaimed(false);
        watchViewReward.setClaimedTime(currentDateTime);
        timerStatus.setWatchViewReward(watchViewReward);

        return timerStatus;
    }

    public void updateTimerStatus(TimerStatus.DailyBonus dailyBonus) {

        UserProfile userProfile = getPreferencesMain().getUserProfile();
        TimerStatus timerStatus = userProfile.getTimerStatus();
        userProfile.getTimerStatus().setDailyBonus(dailyBonus);
        userProfile.setTimerStatus(timerStatus);

        callUpdateUser(userProfile, USAGE_UPDATE_TIMER_STATUS);

    }

    private void setTimerStatus(UserProfile userProfile) {
        TimerStatus.DailyBonus dailyBonus = userProfile.getTimerStatus().getDailyBonus();

        if (dailyBonus != null) {

            // when the daily bonus is claimed then check for the time
            if (dailyBonus.getClaimed()) {
                checkDailyBonus("", userProfile.getTimerStatus().getDailyBonus());

//                callNetworkTime(dailyBonus);
//                getTimeApi(dailyBonus);
            } else {

                flag_DailyBonusWatched = false;

//                showBonusBottomSheet();


            }


        } else {
            dailyBonus = new TimerStatus.DailyBonus();
            userProfile.getTimerStatus().setDailyBonus(dailyBonus);
        }

        // initialize video reward
        if (userProfile.getTimerStatus().getWatchViewReward() == null) {
            WatchViewReward watchViewReward = new WatchViewReward();
            watchViewReward.setClaimed(false);
            watchViewReward.setClaimedTime(DateTimeHelper.getDatePojo().getGetCurrentDateString());

            userProfile.getTimerStatus().setWatchViewReward(watchViewReward);
        } else {

            WatchViewReward watchViewReward = userProfile.getTimerStatus().getWatchViewReward();

            if (!watchViewReward.isClaimed()) {

                flag_rewardVideoWatched = false;
            }

//            if (!watchViewReward.isClaimed()) {
//
//                showBonusBottomSheet();
//            }
        }
    }

    private void setGamePlayedStatus(UserProfile userProfile) {
        if (userProfile.getGamePlayedStatus() == null) {
            GamePlayedStatus gamePlayedStatus = new GamePlayedStatus();
            gamePlayedStatus.setLastGamePlayedDate(DateTimeHelper.getDatePojo().getGetCurrentDateString());
            userProfile.setGamePlayedStatus(gamePlayedStatus);
        } else {
            GamePlayedStatus gamePlayedStatus = userProfile.getGamePlayedStatus();
            if (gamePlayedStatus.getLastGamePlayedDate() == null
                    || gamePlayedStatus.getLastGamePlayedDate().trim().isEmpty()) {
                gamePlayedStatus.setLastGamePlayedDate(DateTimeHelper.getDatePojo().getGetCurrentDateString());
            }
            // reset the game play limit if current date is greater to the last played date
            else if (DateTimeHelper.compareDate(DateTimeHelper.getDatePojo().getGetCurrentDateString(), gamePlayedStatus.getLastGamePlayedDate()) > 0) {
                gamePlayedStatus.setLastGamePlayedDate(DateTimeHelper.getDatePojo().getGetCurrentDateString());
                gamePlayedStatus.setGamePlayedToday(0);

            }
            userProfile.setGamePlayedStatus(gamePlayedStatus);
        }
    }

    private void updateTransactionStatus(UserProfile userProfile) {
        UserTransactions userTransactions = userProfile.getUserTransactions();
        int isTransactionPending = 0;
        if (userTransactions != null && userTransactions.getTransactionRequestArrayList() != null) {
            for (UserTransactions.TransactionRequest transactionRequest : userTransactions.getTransactionRequestArrayList()) {


                // todo
//                 if((transactionRequest.transactionStatus == null) )
//                     continue;
                // add  a condition with transactionRequest.isTransactionComplete
//                 if ( !transactionRequest.isTransactionComplete() ||
//                         transactionRequest.transactionStatus.equals(ConstantsHelper.TransactionStatusPending))
//
                if (!transactionRequest.isTransactionComplete()) {
                    isTransactionPending = 1;
                    break;
                }
            }
        }
        UserAccountStatus userAccountStatus = userProfile.getUserAccountStatus();
        userAccountStatus.setAnyTransactionsPending(isTransactionPending);
    }

    public void startIntent(GamesItems gamesItems) {
        mGamesItems = gamesItems;
        showInterstitialAdNew(interstitialAdListener());

    }

    public void setUpBannerAd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        homeBinding.googleBannerAdView.loadAd(adRequest);

    }

    public void checkDailyBonus(String currentTime, TimerStatus.DailyBonus dailyBonus) {

        String lastModifiedTime = dailyBonus.getLastResetDateTime();
        Log.d("pResponse", "current time " + currentTime);

        Date date1CurrentDate1 = null;

//            date1CurrentDate1 = DateTimeHelper.convertStringIntoDate(currentTime);
        date1CurrentDate1 = DateTimeHelper.getDatePojo().getGetCurrentDate();

        Date date2lastModifiedTime = DateTimeHelper.convertStringIntoDate(lastModifiedTime);
        long mills = date1CurrentDate1.getTime() - date2lastModifiedTime.getTime();


        int hours = (int) (mills / (1000 * 60 * 60));
        int mins = (int) (mills / (1000 * 60)) % 60;

        String diff = hours + ":" + mins; // u
        Log.d("pTimer", "time difference " + diff);


        // reset the time to 8 pm when the time difference is 24 hours
        // enable the daily bonus
        if (hours >= 24) {


//                showBonusBottomSheet();
            flag_DailyBonusWatched = false;

            dailyBonus.setClaimed(false);
            dailyBonus.setClaimedDate(DateTimeHelper.getDatePojo().getSimpleDateFormat().format(date1CurrentDate1) + "");
            dailyBonus.setLastResetDateTime(DateTimeHelper.resetDateToATime(date1CurrentDate1, DateTimeHelper.time_7_am));

            updateTimerStatus(dailyBonus);


            //     dailyBonus.setLastResetDateTime();
        }


    }


    @Override
    public void onPause() {

        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();


    }

    /**
     * Called before the activity is destroyed
     */
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
        //   Toast.makeText(this, "On stop", Toast.LENGTH_SHORT).show();


    }


    public void setHeader() {


        if (getGoogleSignInAccount() != null) {

            Log.d("pResonse", "google id name" + getGoogleSignInAccount().getDisplayName() + " image " + getGoogleSignInAccount().getPhotoUrl());

        } else {
            showSimpleToast("current user is null");
        }


    }

    public void setViews() {


        setHeader();
        loadInterstitialAdNew();
        setUpBannerAd();

    }


    public void getUserAccountStatus(UserProfile userProfile) {
        UserAccountStatus userAccountStatus = userProfile.getUserAccountStatus();

        if (userAccountStatus.getAccountStatus() != AppConstants.AccountActive) {

            DialogItems dialogItems = new DialogItems();
            dialogItems.setMessage(userAccountStatus.getSuspensionMessage());
            showSuspendDialog(dialogItems, null);
//            showSuspendDialog(userAccountStatus.getSuspensionMessage());
        }

        // updating the user transaction
        updateTransactionStatus(userProfile);
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

    private void callNetworkTime(TimerStatus.DailyBonus dailyBonus) {
        dailyBonusToUpdate = dailyBonus;

        viewModel.callNetworkTime(HomeActivity.this);
    }


    private void callGetUserProfile() {
        viewModel.callGetUserProfile();
    }


}


