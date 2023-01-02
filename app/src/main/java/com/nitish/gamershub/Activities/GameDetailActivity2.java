package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.AppHelper.getGamersHubDataGlobal;
import static com.nitish.gamershub.Utils.AppHelper.getUserProfileGlobalData;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter;
import com.nitish.gamershub.Fragments.GameDetailsFragment;
import com.nitish.gamershub.Fragments.GamePlayFragment;
import com.nitish.gamershub.Interface.AdmobInterstitialAdListener;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.Pojo.FireBase.GamePlayedStatus;
import com.nitish.gamershub.Pojo.FireBase.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.DateTimeHelper;
import com.nitish.gamershub.Utils.UserOperations;

import java.time.LocalTime;

import io.paperdb.Paper;

public class GameDetailActivity2 extends BasicActivity {




    Fragment previousFragment;

    static  String DetailFrag = "GameDetailFragment";
    GameDetailsFragment gameDetailsFragment ;

    GamePlayFragment gamePlayFragment ;
    boolean gamePlayVisibility= false;
    AllGamesItems allGamesItems;
    FragmentManager fragmentManager;
    UserProfile userProfile;
    GamePlayedStatus gamePlayedStatus ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        Paper.init(GameDetailActivity2.this);

         allGamesItems = NewAndPopularGamesAdapter.SelectedGameObject;

         fragmentManager = getSupportFragmentManager();
         loadInterstitialAdNew();
         userProfile = getUserProfileGlobalData();
        gamePlayedStatus = getUserProfileGlobalData().getGamePlayedStatus();


        if(gameDetailsFragment==null)
        {
            gameDetailsFragment = GameDetailsFragment.newInstance();
        }
         showHideFragment(gameDetailsFragment,gameDetailsFragment.getTag());



    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_game_detail;
    }

    public void playButtonClick()
    {
        if(!showNoInternetDialog())
            return;

        showInterstitialAdNew(interstitialAdListener(GamePlayFragment.class.getSimpleName()));


    }
    private void showGamePlayFragment()
    {
        if(gamePlayFragment==null)
        {
            gamePlayFragment = GamePlayFragment.newInstance();
        }
        showHideFragment(gamePlayFragment,gamePlayFragment.getTag());

        if (allGamesItems.getOrientation().toLowerCase().contains("hori")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        gamePlayVisibility = true;
        gamePlayFragment.startTimer();
        // game play fragment is visible
    }
    public void showHideFragment(Fragment fragment , String tag)
    {
        if(fragment==previousFragment)
        {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        if(!fragment.isAdded())
        {
            fragmentTransaction.add(R.id.frameLayout,fragment,tag);
            if(previousFragment!=null)
            {
                fragmentTransaction.hide(previousFragment);
            }
        }
        else {
            fragmentTransaction.show(fragment);
            fragmentTransaction.hide(previousFragment);
        }
        fragmentTransaction.commit();
        previousFragment = fragment;
    }



    public void exitGameDialog()
    {

        showConfirmationDialog( "Confirmation","Do you want to exit the game?", new ConfirmationDialogListener() {
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
        if (previousFragment==gamePlayFragment) {


            exitGameDialog();
        }

        else
            super.onBackPressed();


    }
    public void showGameDetailsFrag()
    {

     //   gamePlayFragment.resetTimer();// reset the timer

        if(gameDetailsFragment==null)
        {
            gameDetailsFragment = GameDetailsFragment.newInstance();
        }
        showHideFragment(gameDetailsFragment,gameDetailsFragment.getTag());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              //"00:30"


            int gamePlaySeconds =  getGamersHubDataGlobal().getGamesData().getGamePlaySecs();

            if(LocalTime.parse( gamePlayFragment.timerMinuteSecond).compareTo(LocalTime.parse(DateTimeHelper.formatTimeToMMSS(gamePlaySeconds))) > 0 ) {


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
    private void updateGamePlayedStatus()
    {


        int totalGamePlayed = gamePlayedStatus.getTotalGamePlayed();
        totalGamePlayed = totalGamePlayed+1;
        gamePlayedStatus.setTotalGamePlayed(totalGamePlayed);

      // if current date is equal to the last played date
        if(DateTimeHelper.compareDate(DateTimeHelper.getDatePojo().getGetCurrentDateString(),gamePlayedStatus.getLastGamePlayedDate())==0)
        {
            int gamePlayedToday = gamePlayedStatus.getGamePlayedToday();
            gamePlayedToday = gamePlayedToday+1;
            gamePlayedStatus.setGamePlayedToday(gamePlayedToday);

            if(gamePlayedStatus.getGamePlayedToday()>getGamersHubDataGlobal().gamesData.getDailyGamePlayLimit())
            {
                // do something when the game play limit is over
                updateGamePlayedStatus(gamePlayedStatus);
            }
            else {
                // increment the today game point

                // reward the user
                int amount = getGamersHubDataGlobal().getGamesData().getGamePlayReward();
                updateUserWallet(amount);
                Toast.makeText(this, "you are rewarded "+amount+" coins", Toast.LENGTH_SHORT).show();


            }

        }
        // if current date is greater to the last played date
        else   if(DateTimeHelper.compareDate(DateTimeHelper.getDatePojo().getGetCurrentDateString(),gamePlayedStatus.getLastGamePlayedDate())>0)
        {
            gamePlayedStatus.setLastGamePlayedDate(DateTimeHelper.getDatePojo().getGetCurrentDateString());
            gamePlayedStatus.setGamePlayedToday(0);
            updateGamePlayedStatus(gamePlayedStatus);
        }












    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();

            loadInterstitialAdNew();
    }
    public void updateGamePlayedStatus(GamePlayedStatus gamePlayedStatus)
    {
        userProfile.setGamePlayedStatus(gamePlayedStatus);

        setUserProfile(userProfile, new SetUserDataOnCompleteListener() {
            @Override
            public void onTaskSuccessful() {

                if(gameDetailsFragment!=null)
                {
                    gameDetailsFragment.onResume();
                }

            }

        });
    }
    public void updateUserWallet(int amount)
    {

                userProfile.setProfileData(UserOperations.addCoinsToWallet(userProfile, amount));
                userProfile.setGamePlayedStatus(gamePlayedStatus);
                setUserProfile(userProfile, new SetUserDataOnCompleteListener() {
                    @Override
                    public void onTaskSuccessful() {
                        showRewardDialog(amount,R.raw.rupee_reward_box);
                        if(gameDetailsFragment!=null)
                        {
                            gameDetailsFragment.onResume();
                        }
                    }
                });



    }


    public AdmobInterstitialAdListener interstitialAdListener(String fragmentTag_ToShow)
    {

        return new AdmobInterstitialAdListener() {

            @Override
            public void onAdDismissed() {

                super.onAdDismissed();
                if(fragmentTag_ToShow.equals(GameDetailsFragment.class.getSimpleName()))
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
                if(fragmentTag_ToShow.equals(GameDetailsFragment.class.getSimpleName()))
                    showGameDetailsFrag();
                else {
                    showGamePlayFragment();
                }
                super.onAdFailed();
            }

            @Override
            public void onAdLoading() {
                super.onAdLoading();

                if(fragmentTag_ToShow.equals(GameDetailsFragment.class.getSimpleName()))
                    showGameDetailsFrag();
                else {
                    showGamePlayFragment();
                }

            }
        };

    }
}