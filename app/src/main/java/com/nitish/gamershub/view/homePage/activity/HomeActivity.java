package com.nitish.gamershub.view.homePage.activity;

import static com.nitish.gamershub.utils.AppConstants.UserInfo;
import static com.nitish.gamershub.utils.AppConstants.FavouriteList;
import static com.nitish.gamershub.utils.AppConstants.From;
import static com.nitish.gamershub.utils.AppConstants.GamersHub_ParentCollection;
import static com.nitish.gamershub.utils.AppConstants.GoogleSignInAccountUser;
import static com.nitish.gamershub.utils.AppConstants.IntentData;
import static com.nitish.gamershub.utils.AppConstants.UserMail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
//import androidx.drawerlayout.widget.DrawerLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.nitish.gamershub.databinding.ActivityHomeBinding;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.interfaces.AdmobInterstitialAdListener;
import com.nitish.gamershub.model.local.AllGamesItems;
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
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;
import com.nitish.gamershub.utils.DeviceHelper;
import com.nitish.gamershub.utils.NotificationHelper;
import com.nitish.gamershub.utils.ProgressBarHelper;
import com.nitish.gamershub.view.base.BaseActivity;
import com.nitish.gamershub.view.dialogs.DialogListener;
import com.nitish.gamershub.view.gamePlay.GameDetailActivity2;
import com.nitish.gamershub.view.homePage.fragment.CategoryGamesFragment;
import com.nitish.gamershub.view.homePage.fragment.HomeFragment;
import com.nitish.gamershub.view.homePage.fragment.ProfileFragment;
import com.nitish.gamershub.view.loginSingup.viewmodelRepo.LoginSignUpViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import io.paperdb.Paper;

public class HomeActivity extends BaseActivity {

    // the whole game data
    JSONObject masterDataJsonObject;

    ActivityHomeBinding homeBinding;


    int currentSelectedFragPosition = 0;


    HomeFragment homeFragment;
    ProfileFragment profileFragment;

    CategoryGamesFragment categoryGamesFragment;
    Fragment previousFragment;
    RequestQueue requestQueue;

    boolean interstitialAdDismissed = false;
    private LoginSignUpViewModel viewModel;

    private RewardedAd rewardedAd;
    boolean isLoading;
    ArrayList<AllGamesItems> mainGamesArrayList;
    ProgressDialog progressDialog;
    TimerStatus.DailyBonus dailyBonusToUpdate;


    // firebase auth

    FirebaseFirestore firestoreDb;

    UserProfile updatedUserProfile;

    AllGamesItems mAllGamesItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        viewModel = ViewModelProviders.of(this).get(LoginSignUpViewModel.class);
        firestoreDb = FirebaseFirestore.getInstance();
        requestQueue = Volley.newRequestQueue(HomeActivity.this);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(HomeActivity.this);
        Paper.book().write(GoogleSignInAccountUser, acct);
        setViews();
        updateUserProfileData((UserProfile) Paper.book().read(UserInfo));
        bindObservers();
        String playStoreVersionCode = FirebaseRemoteConfig.getInstance().getString(
                "appVersion");

        Log.d("pResponse", "playStoreVersionCode " + playStoreVersionCode);
//        categoriesList = new ArrayList<>();
//        navigationView.setVisibility(View.VISIBLE);

        NotificationHelper.generateFcmToken();
        setHeader();
//        updateUserInfo();

        loadInterstitialAdNew();
        setUpBannerAd();

        //     AdsHelper.loadInterstitialAd(this);

//        setCategory();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                showLogOutDialog();
//            }
//        });


        // just writing an empty favourite list to avoid null pointer when reading the data

        if (!Paper.book().contains(FavouriteList) || Paper.book().read(FavouriteList) == null) {
            ArrayList<AllGamesItems> favouriteArrayList = new ArrayList<>();
            Paper.book().write(FavouriteList, favouriteArrayList);
        }


        setBottomNavigationView();


    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }


    private void bindObservers() {

        viewModel.updateUserProfileLD.observe(this, new Observer<NetworkResponse<Object>>() {
            @Override
            public void onChanged(NetworkResponse<Object> response) {
                if (response instanceof NetworkResponse.Success) {


                    dismissProgressBar();


                    Paper.book().write(UserInfo, updatedUserProfile);

                    callGetGamersHubData();


                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<Object>) response).getMessage();

                    dismissProgressBar();
                } else if (response instanceof NetworkResponse.Loading) {

                    showProgressBar();
                }
            }
        });

        viewModel.getGamersHubDataLD.observe(this, new Observer<NetworkResponse<GamersHubData>>() {

            @Override
            public void onChanged(NetworkResponse<GamersHubData> response) {
                if (response instanceof NetworkResponse.Success) {
                    dismissProgressBar();

                    GamersHubData gamersHubData = ((NetworkResponse.Success<GamersHubData>) response).getData();


                    if (!AppHelper.isAppUpdated()) {
                        showUpdate(gamersHubData);
                    }


                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<GamersHubData>) response).getMessage();

                    dismissProgressBar();
                } else if (response instanceof NetworkResponse.Loading) {

                    showProgressBar();
                }
            }
        });

        viewModel.getNetworkTime.observe(this, new Observer<NetworkResponse<NetWorkTimerResult>>() {

            @Override
            public void onChanged(NetworkResponse<NetWorkTimerResult> response) {
                if (response instanceof NetworkResponse.Success) {
                    dismissProgressBar();

                    NetWorkTimerResult netWorkTimerResult = ((NetworkResponse.Success<NetWorkTimerResult>) response).getData();


                    checkDailyBonus(netWorkTimerResult.toString(), dailyBonusToUpdate);


                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<NetWorkTimerResult>) response).getMessage();

                    dismissProgressBar();
                } else if (response instanceof NetworkResponse.Loading) {

                    showProgressBar();
                }
            }
        });

    }

    public void showLogOutDialog() {


        DialogItems dialogItems = new DialogItems();
        dialogItems.setTitle("Confirmation");
        dialogItems.setMessage("Do you want to log out?");
        showConfirmationDialog2(dialogItems, new DialogListener() {
            @Override
            public void onYesClick() {
                googleSignOut();
            }

            @Override
            public void onNoClick() {

            }
        });

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
        intent.putExtra(IntentData, mAllGamesItems);
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

        callUpdateUser(userProfile);
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

        firestoreDb.collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail) + "").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()) {

                        UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                        TimerStatus timerStatus = userProfile.getTimerStatus();

                        userProfile.getTimerStatus().setDailyBonus(dailyBonus);
                        userProfile.setTimerStatus(timerStatus);

                        firestoreDb.collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail) + "").set(userProfile, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {


                                    //     Toast.makeText(HomeActivity.this, "daily earn enabled", Toast.LENGTH_LONG).show();


                                }
                            }
                        });

                    } else {
                        Toast.makeText(HomeActivity.this, "document does not exist11221", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this, "couldn't get the documents ", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void setTimerStatus(UserProfile userProfile) {
        TimerStatus.DailyBonus dailyBonus = userProfile.getTimerStatus().getDailyBonus();

        if (dailyBonus != null) {

            // when the daily bonus is claimed then check for the time
            if (dailyBonus.getClaimed()) {
                callNetworkTime(dailyBonus);
//                getTimeApi(dailyBonus);
            } else {
                showBonusBottomSheet();
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
                showBonusBottomSheet();
            }
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


    public void startIntent(AllGamesItems allGamesItems) {
        mAllGamesItems = allGamesItems;
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
        try {
            date1CurrentDate1 = DateTimeHelper.convertStringIntoDate(currentTime);
            Date date2lastModifiedTime = DateTimeHelper.convertStringIntoDate(lastModifiedTime);
            long mills = date1CurrentDate1.getTime() - date2lastModifiedTime.getTime();


            Log.v("Data1", "" + date1CurrentDate1.toString());
            Log.v("Data2", "" + date2lastModifiedTime.toString());


            int hours = (int) (mills / (1000 * 60 * 60));
            int mins = (int) (mills / (1000 * 60)) % 60;

            String diff = hours + ":" + mins; // u
            Log.d("pTimer", "time difference " + diff);


            // reset the time to 8 pm when the time difference is 24 hours
            // enable the daily bonus
            if (hours >= 24) {
                showBonusBottomSheet();
                dailyBonus.setClaimed(false);
                dailyBonus.setClaimedDate(DateTimeHelper.getDatePojo().getSimpleDateFormat().format(date1CurrentDate1) + "");
                dailyBonus.setLastResetDateTime(DateTimeHelper.resetDateToATime(date1CurrentDate1, DateTimeHelper.time_7_am));

                Log.d("pTimer", "Calender timer " + DateTimeHelper.getDatePojo().getSimpleDateFormat().format(date1CurrentDate1));
                updateTimerStatus(dailyBonus);


                //     dailyBonus.setLastResetDateTime();
            }


        } catch (Exception e) {
            Log.d("pTimer", "Hello, error ! " + e);

            e.printStackTrace();
        }


    }


    @Override
    public void onPause() {

        if (homeBinding.googleBannerAdView != null) {
            homeBinding.googleBannerAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();


        if (homeBinding.googleBannerAdView != null) {
            homeBinding.googleBannerAdView.resume();

        }


    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (homeBinding.googleBannerAdView != null) {
            homeBinding.googleBannerAdView.destroy();
        }

        super.onDestroy();
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
            Toast.makeText(this, "current user is null", Toast.LENGTH_SHORT).show();
        }


    }

    public void setViews() {


        progressDialog = ProgressBarHelper.setProgressBarDialog(this);


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


    public void showUpdate(GamersHubData gamersHubData) {
        ConfirmationDialogListener confirmationDialogListener = new ConfirmationDialogListener() {
            @Override
            public void onDismissListener() {

            }

            @Override
            public void onYesClick() {
                openPlayStore();
            }

            @Override
            public void onNoClick() {

            }

            @Override
            public void onRewardGrantedListener() {

            }
        };
        DialogItems dialogItems = new DialogItems();
        dialogItems.setTitle("Pending Update");
        dialogItems.setYesTitle("Update");
        dialogItems.setMessage("A new update of the app has been released , please update ");
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

    private void callUpdateUser(UserProfile userProfile) {
        updatedUserProfile = userProfile;
        viewModel.callUpdateUserProfile(userProfile);
    }

    private void callNetworkTime(TimerStatus.DailyBonus dailyBonus) {
        dailyBonusToUpdate = dailyBonus;

        viewModel.callNetworkTime(HomeActivity.this);
    }

    private void callGetUserProfileData() {
        viewModel.callGetUserProfile();
    }
}


