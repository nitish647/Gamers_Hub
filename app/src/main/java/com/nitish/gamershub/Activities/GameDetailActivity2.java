package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.AppHelper.getGamersHubDataGlobal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter;
import com.nitish.gamershub.Fragments.GameDetailsFragment;
import com.nitish.gamershub.Fragments.GamePlayFragment;
import com.nitish.gamershub.Interface.AdmobInterstitialAdListener;
import com.nitish.gamershub.Pojo.AllGamesItems;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        Paper.init(GameDetailActivity2.this);

         allGamesItems = NewAndPopularGamesAdapter.SelectedGameObject;

         fragmentManager = getSupportFragmentManager();
         loadInterstitialAdNew();


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
                showInterstitialAdNew(interstitialAdListener());
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


                int amount = getGamersHubDataGlobal().getGamesData().getGamePlayReward();

                updateUserWallet(amount);

                Toast.makeText(this, "you are rewarded "+amount+" coins", Toast.LENGTH_SHORT).show();

            }
            gamePlayFragment = null;
        }

    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();

            loadInterstitialAdNew();
    }
    public void updateUserWallet(int amount)
    {

        UserOperations.getFirestoreUser().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                    userProfile.setProfileData(UserOperations.addCoinsToWallet(userProfile, amount));


                        UserOperations.getFirestoreUser().set(userProfile, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    showRewardDialog(amount,R.raw.rupee_reward_box);
                                }
                            }
                        });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GameDetailActivity2.this, "can't add the reward as user document doesn't exists ", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public AdmobInterstitialAdListener interstitialAdListener()
    {

        return new AdmobInterstitialAdListener() {

            @Override
            public void onAdDismissed() {

                super.onAdDismissed();
                showGameDetailsFrag();
            }

            @Override
            public void onAdShown() {
                super.onAdShown();
            }

            @Override
            public void onAdFailed() {
                super.onAdFailed();
            }

            @Override
            public void onAdLoading() {
                super.onAdLoading();

                    showGameDetailsFrag();

            }
        };

    }
}