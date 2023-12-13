package com.nitish.gamershub.view.gamePlay;


import static com.nitish.gamershub.utils.AppConstants.IntentData;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.nitish.gamershub.databinding.ActivityGameDetailBinding;
import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;
import com.nitish.gamershub.model.firebase.gamePlayed.GamePlayedStatus;
import com.nitish.gamershub.model.firebase.userProfile.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;
import com.nitish.gamershub.utils.firebaseUtils.UserOperations;
import com.nitish.gamershub.utils.adsUtils.AdmobInterstitialAdListener;
import com.nitish.gamershub.view.base.BaseActivity;
import com.nitish.gamershub.view.dialogs.DialogListener;
import com.nitish.gamershub.view.loginSingup.viewmodelRepo.LoginSignUpViewModel;

import io.paperdb.Paper;

public class GameDetailActivity2 extends BaseActivity {


    Fragment previousFragment;

    static String DetailFrag = "GameDetailFragment";
    GameDetailsFragment gameDetailsFragment;

    GamePlayFragment gamePlayFragment;
    boolean gamePlayVisibility = false;
    GamesItems gamesItems;
    FragmentManager fragmentManager;
    UserProfile userProfile;
    GamePlayedStatus gamePlayedStatus;
    private LoginSignUpViewModel viewModel;


    String USAGE_UPDATE_TIMER_STATUS = "USAGE_UPDATE_TIMER_STATUS";

    String USAGE_UPDATE_GamePlayed_Status = "USAGE_UPDATE_GamePlayed_Status";
    String USAGE_UPDATE_UserWallet = "USAGE_UPDATE_UserWallet";


    ActivityGameDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_detail);
        Paper.init(GameDetailActivity2.this);

//         allGamesItems = NewAndPopularGamesAdapter.SelectedGameObject;
        gamesItems = (GamesItems) getIntent().getSerializableExtra(IntentData);
        viewModel = ViewModelProviders.of(this).get(LoginSignUpViewModel.class);


        bindObservers();
        fragmentManager = getSupportFragmentManager();
        loadInterstitialAdNew();
        userProfile = getPreferencesMain().getUserProfile();
        gamePlayedStatus = getPreferencesMain().getUserProfile().getGamePlayedStatus();

//        showSnackBar("Sample snackbar",binding.frameLayout);

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

                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

                    hideLoader();
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


        if (AppHelper.checkIfHorizontal(gamesItems)) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        getPreferencesMain().saveRecentlyPlayedItemInList(gamesItems);

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
            setActivityStatusBarColor(R.color.black);

        } else {
            setActivityStatusBarColor(R.color.gameDetails_screen_color);
        }
    }


    public void exitGameDialog() {


        DialogItems dialogItems = new DialogItems();
        dialogItems.setMessage(getString(R.string.do_you_want_to_exit_the_game));
        dialogItems.setTitle(getString(R.string.confirmation));
        showConfirmationDialog2(dialogItems, new DialogListener() {
            @Override
            public void onYesClick() {

                if (gameDetailsFragment != null) {
                    gameDetailsFragment.onDestroy();

                }

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
                }

                showInterstitialAdNew(interstitialAdListener(GameDetailsFragment.class.getSimpleName()));
            }

            @Override
            public void onNoClick() {

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


        //"00:30"


        int gamePlaySeconds = getPreferencesMain().getGamersHubData().getGamesData().getGamePlaySecs();

//            if (LocalTime.parse(gamePlayFragment.timerMinuteSecond).compareTo(LocalTime.parse(DateTimeHelper.formatTimeToMMSS(gamePlaySeconds))) > 0) {
//
//                updateGamePlayedStatus();
//
//            }
        if (DateTimeHelper.compareLocaleSeconds(gamePlayFragment.timerMinuteSecond, gamePlaySeconds) > 0) {
            updateGamePlayedStatus();
        }

        gamePlayFragment = null;


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

            if (gamePlayedStatus.getGamePlayedToday() > getPreferencesMain().getGamersHubData().getGamesData().getDailyGamePlayLimit()) {
                // do something when the game play limit is over

                updateGamePlayedStatus(gamePlayedStatus);
            } else {
                // increment the today game point

                // reward the user
                int amount = getPreferencesMain().getGamersHubData().getGamesData().getGamePlayReward();
                updateUserWallet(amount);
                showSimpleToast("you are rewarded " + amount + " coins");


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

        callUpdateUser(userProfile, USAGE_UPDATE_GamePlayed_Status);

    }

    public void updateUserWallet(int amount) {

        userProfile.setProfileData(UserOperations.addCoinsToWallet(userProfile, amount));
        userProfile.setGamePlayedStatus(gamePlayedStatus);
        callUpdateUser(userProfile, USAGE_UPDATE_UserWallet);


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

    private void callUpdateUser(UserProfile userProfile, String usage) {

        viewModel.callUpdateUserProfile(userProfile, usage);
    }
}