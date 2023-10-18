package com.nitish.gamershub.view.gamePlay;


import static com.nitish.gamershub.utils.AppConstants.UserInfo;
import static com.nitish.gamershub.utils.AppConstants.UserMail;
import static com.nitish.gamershub.utils.AppHelper.getGamersHubDataGlobal;
import static com.nitish.gamershub.utils.AppHelper.getUserProfileGlobalData;
import static com.nitish.gamershub.utils.AppHelper.setStatusBarColor;
import static com.nitish.gamershub.utils.AppConstants.IntentData;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.nitish.gamershub.model.local.AllGamesItems;
import com.nitish.gamershub.model.firebase.GamePlayedStatus;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;
import com.nitish.gamershub.utils.firebaseUtils.UserOperations;
import com.nitish.gamershub.utils.interfaces.AdmobInterstitialAdListener;
import com.nitish.gamershub.view.base.BaseActivity;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.view.loginSingup.activity.LoginActivity;
import com.nitish.gamershub.view.loginSingup.viewmodelRepo.LoginSignUpViewModel;

import java.time.LocalTime;

import io.paperdb.Paper;

public class GameDetailActivity2 extends BaseActivity {


    Fragment previousFragment;

    static String DetailFrag = "GameDetailFragment";
    GameDetailsFragment gameDetailsFragment;

    GamePlayFragment gamePlayFragment;
    boolean gamePlayVisibility = false;
    AllGamesItems allGamesItems;
    FragmentManager fragmentManager;
    UserProfile userProfile;
    GamePlayedStatus gamePlayedStatus;
    private LoginSignUpViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        Paper.init(GameDetailActivity2.this);

//         allGamesItems = NewAndPopularGamesAdapter.SelectedGameObject;
        allGamesItems = (AllGamesItems) getIntent().getSerializableExtra(IntentData);
        viewModel = ViewModelProviders.of(this).get(LoginSignUpViewModel.class);

        bindObservers();
        fragmentManager = getSupportFragmentManager();
        loadInterstitialAdNew();
        userProfile = getUserProfileGlobalData();
        gamePlayedStatus = getUserProfileGlobalData().getGamePlayedStatus();


        if (gameDetailsFragment == null) {
            gameDetailsFragment = GameDetailsFragment.newInstance();
        }
        showHideFragment(gameDetailsFragment, gameDetailsFragment.getTag());


    }

    private void bindObservers() {
        viewModel.updateUserProfileLD.observe(this, new Observer<NetworkResponse<Object>>() {
            @Override
            public void onChanged(NetworkResponse<Object> response) {
                if (response instanceof NetworkResponse.Success) {

                    if (gameDetailsFragment != null) {
                        gameDetailsFragment.onResume();
                    }

                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<Object>) response).getMessage();

                    dismissProgressBar();
                } else if (response instanceof NetworkResponse.Loading) {

                    showProgressBar();
                }
            }
        });

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_game_detail;
    }

    public void playButtonClick() {
        if (!showNoInternetDialog())
            return;

        showInterstitialAdNew(interstitialAdListener(GamePlayFragment.class.getSimpleName()));


    }

    private void showGamePlayFragment() {
        if (gamePlayFragment == null) {
            gamePlayFragment = GamePlayFragment.newInstance();
        }
        showHideFragment(gamePlayFragment, gamePlayFragment.getTag());

        if (allGamesItems.getOrientation().toLowerCase().contains("hori")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        gamePlayVisibility = true;
        gamePlayFragment.startTimer();

        // game play fragment is visible
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

        if (fragment instanceof GamePlayFragment) {
            setStatusBarColor(this, R.color.black);
        } else {
            setStatusBarColor(this, R.color.gamers_hub_theme);
        }
    }


    public void exitGameDialog() {

        showConfirmationDialog("Confirmation", "Do you want to exit the game?", new ConfirmationDialogListener() {
            @Override
            public void onDismissListener() {

            }

            @Override
            public void onYesClick() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                showInterstitialAdNew(interstitialAdListener(GameDetailsFragment.class.getSimpleName()));
            }

            @Override
            public void onNoClick() {

            }

            @Override
            public void onRewardGrantedListener() {

            }
        });


    }

    @Override
    public void onBackPressed() {
        if (previousFragment == gamePlayFragment) {


            exitGameDialog();
        } else
            super.onBackPressed();

    }

    public void showGameDetailsFrag() {

        //   gamePlayFragment.resetTimer();// reset the timer

        if (gameDetailsFragment == null) {
            gameDetailsFragment = GameDetailsFragment.newInstance();
        }
        showHideFragment(gameDetailsFragment, gameDetailsFragment.getTag());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //"00:30"


            int gamePlaySeconds = getGamersHubDataGlobal().getGamesData().getGamePlaySecs();

            if (LocalTime.parse(gamePlayFragment.timerMinuteSecond).compareTo(LocalTime.parse(DateTimeHelper.formatTimeToMMSS(gamePlaySeconds))) > 0) {


                updateGamePlayedStatus();
//                int amount = getGamersHubDataGlobal().getGamesData().getGamePlayReward();
//
//                updateUserWallet(amount);
//
//                Toast.makeText(this, "you are rewarded "+amount+" coins", Toast.LENGTH_SHORT).show();

            }
            gamePlayFragment = null;
        }

    }

    private void updateGamePlayedStatus() {

        int totalGamePlayed = gamePlayedStatus.getTotalGamePlayed();
        totalGamePlayed = totalGamePlayed + 1;
        gamePlayedStatus.setTotalGamePlayed(totalGamePlayed);
        gamePlayedStatus.setLastGamePlayedDate(DateTimeHelper.getDatePojo().getGetCurrentDateString());

        // if current date is equal to the last played date
        if (DateTimeHelper.compareDate(DateTimeHelper.getDatePojo().getGetCurrentDateString(), gamePlayedStatus.getLastGamePlayedDate()) == 0) {
            int gamePlayedToday = gamePlayedStatus.getGamePlayedToday();
            gamePlayedToday = gamePlayedToday + 1;
            gamePlayedStatus.setGamePlayedToday(gamePlayedToday);

            if (gamePlayedStatus.getGamePlayedToday() > getGamersHubDataGlobal().gamesData.getDailyGamePlayLimit()) {
                // do something when the game play limit is over

                updateGamePlayedStatus(gamePlayedStatus);
            } else {
                // increment the today game point

                // reward the user
                int amount = getGamersHubDataGlobal().getGamesData().getGamePlayReward();
                updateUserWallet(amount);
                Toast.makeText(this, "you are rewarded " + amount + " coins", Toast.LENGTH_SHORT).show();


            }

        }
        // if current date is greater to the last played date
        else if (DateTimeHelper.compareDate(DateTimeHelper.getDatePojo().getGetCurrentDateString(), gamePlayedStatus.getLastGamePlayedDate()) > 0) {

            gamePlayedStatus.setGamePlayedToday(0);
            updateGamePlayedStatus(gamePlayedStatus);
        }

    }


    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        loadInterstitialAdNew();
    }

    public void updateGamePlayedStatus(GamePlayedStatus gamePlayedStatus) {
        userProfile.setGamePlayedStatus(gamePlayedStatus);

        callUpdateUser(userProfile);
//        setUserProfile(userProfile, new SetUserDataOnCompleteListener() {
//            @Override
//            public void onTaskSuccessful() {
//
//                if (gameDetailsFragment != null) {
//                    gameDetailsFragment.onResume();
//                }
//
//            }
//
//        });
    }

    public void updateUserWallet(int amount) {

        userProfile.setProfileData(UserOperations.addCoinsToWallet(userProfile, amount));
        userProfile.setGamePlayedStatus(gamePlayedStatus);

        callUpdateUser(userProfile);

//        setUserProfile(userProfile, new SetUserDataOnCompleteListener() {
//            @Override
//            public void onTaskSuccessful() {
//                showRewardDialog(amount, R.raw.rupee_reward_box);
//                if (gameDetailsFragment != null) {
//                    gameDetailsFragment.onResume();
//                }
//            }
//        });


    }


    public AdmobInterstitialAdListener interstitialAdListener(String fragmentTag_ToShow) {

        return new AdmobInterstitialAdListener() {

            @Override
            public void onAdDismissed() {

                super.onAdDismissed();
                if (fragmentTag_ToShow.equals(GameDetailsFragment.class.getSimpleName()))
                    showGameDetailsFrag();
                else {
                    showGamePlayFragment();
                }
            }

            @Override
            public void onAdShown() {
                super.onAdShown();
            }

            @Override
            public void onAdFailed() {
                if (fragmentTag_ToShow.equals(GameDetailsFragment.class.getSimpleName()))
                    showGameDetailsFrag();
                else {
                    showGamePlayFragment();
                }
                super.onAdFailed();
            }

            @Override
            public void onAdLoading() {
                super.onAdLoading();

                if (fragmentTag_ToShow.equals(GameDetailsFragment.class.getSimpleName()))
                    showGameDetailsFrag();
                else {
                    showGamePlayFragment();
                }

            }
        };

    }

    private void callUpdateUser(UserProfile userProfile) {

        viewModel.callUpdateUserProfile(userProfile);
    }
}