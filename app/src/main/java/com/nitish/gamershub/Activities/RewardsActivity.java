package com.nitish.gamershub.Activities;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nitish.gamershub.Interface.AdmobInterstitialAdListener;
import com.nitish.gamershub.Pojo.FireBase.TimerStatus;
import com.nitish.gamershub.Pojo.FireBase.UserProfile;
import com.nitish.gamershub.Pojo.FireBase.WatchViewReward;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.DateTimeHelper;
import com.nitish.gamershub.databinding.ActivityRewardsBinding;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RewardsActivity extends BasicActivity {

    ActivityRewardsBinding binding;
    UserProfile userProfile;

    boolean isLoading;
    public int seconds = 0;
    public     String timerHourMinuteSecond ="00:00:00";

    boolean running ,  wasRunning ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_rewards);
        setOnclickListeners();

        loadInterstitialAdNew();
        getProfileData();
    }

    public void setOnclickListeners()
    {





        binding.watchVideoRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WatchViewReward watchViewReward = userProfile.getTimerStatus().getWatchViewReward();

                if(!watchViewReward.isClaimed())
                {

                    showRewardedVideo2(setRewardedAdListener());
                    // show Video Ad
                }
                else {
                    Toast.makeText(RewardsActivity.this, "already claimed", Toast.LENGTH_SHORT).show();
                    // show already claimed
                }

            }
        });



        binding.transactionHistoryRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RewardsActivity.this,TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });

        binding.redeemCoinsRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RewardsActivity.this,RedeemActivity.class);
                startActivity(intent);
            }
        });

        binding.dailyBonusRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setDailyBonus();

            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }



    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_rewards;
    }
    public void getProfileData()
    {


        showProgressBar();

        getUserProfile(getUserProfileDataListener());




    }


    public void updateViews(UserProfile userProfile )
    {
        UserProfile.ProfileData profileData = userProfile.profileData;
       TimerStatus timerStatus = userProfile.getTimerStatus();
     TimerStatus.DailyBonus dailyBonus = timerStatus.getDailyBonus();

        binding.coinsTextview.setText( profileData.getGameCoins()+"");

      if(dailyBonus.getClaimed())
        {
            binding.claimTextview.setText("claimed");
        }
      else {
          binding.claimTextview.setText("claim");

      }






    }


    public void setDailyBonus()
    {
        if(userProfile!=null)
        {
            TimerStatus.DailyBonus dailyBonus = getDailyBonusFromProfile(userProfile);
            if(dailyBonus.getClaimed())
            {
                Toast.makeText(RewardsActivity.this, "You have already claimed the daily bonus , please come tomorrow", Toast.LENGTH_LONG).show();

            }
            else {




                dailyBonus.setClaimed(true);
                String currentDate = DateTimeHelper.datePojo().getGetCurrentDateString();
                dailyBonus.setLastResetDateTime(DateTimeHelper.resetDateToATime(currentDate,DateTimeHelper.time_7_am));
                dailyBonus.setClaimedDate(currentDate);
                userProfile.getTimerStatus().setDailyBonus(dailyBonus);
                userProfile.getProfileData().setGameCoins(userProfile.getProfileData().getGameCoins()+200);
                setUserProfile(userProfile, new SetUserDataListener() {
                    @Override
                    public void onTaskSuccessful() {
                        showRewardDialog("Successfully credited " + 200 + " coins for Daily bonus", R.raw.rupee_piggy_bank_award, new OnDialogLister() {
                            @Override
                            public void onDialogDismissLister() {
                                showInterstitialAdNew(setInterstitialAdListener());
                            }
                        });
                        getUserProfile(getUserProfileDataListener());
                    }
                });
            }


        }
    }

    public GetUserProfileDataListener getUserProfileDataListener()
    {
      return   new GetUserProfileDataListener() {
            @Override
            public void onTaskSuccessful(UserProfile userProfile) {

                RewardsActivity.this.userProfile = userProfile;

                timer();
                updateViews(userProfile);

            }
        };
    }
    public RewardedAdListener setRewardedAdListener()
    {
        return new RewardedAdListener() {
            @Override
            public void onDismissListener() {

            }

            @Override
            public void onRewardGrantedListener() {

               // start the timer
                Toast.makeText(RewardsActivity.this, "reward is granted", Toast.LENGTH_SHORT).show();
                setWatchVideoRewardAfterVideo();

            }





        };
    }




    public AdmobInterstitialAdListener setInterstitialAdListener()
    {
        return new AdmobInterstitialAdListener() {


            @Override
            public void onAdShown() {

            }

            @Override
            public void onAdFailed() {

            }

            @Override
            public void onAdLoading() {

            }
        };
    }

    public void setWatchVideoRewardAfterVideo()
    {
        WatchViewReward watchViewReward =    userProfile.getTimerStatus().watchViewReward;

        // increment 1 hour in the current date
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date());               // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1);      // adds one hour


        watchViewReward.setClaimedTime(DateTimeHelper.convertDateToString(cal.getTime()));
        watchViewReward.setClaimed(true);

        binding.watchVideoTextview.setText("watched");
        userProfile.getTimerStatus().setWatchViewReward(watchViewReward);

        setUserProfile( userProfile ,  new SetUserDataListener() {
           @Override
           public void onTaskSuccessful() {
               showRewardDialog("Successfully credited " + 50 + " coins for Watching video", R.raw.rupee_piggy_bank_award, new OnDialogLister() {
                   @Override
                   public void onDialogDismissLister() {

                   }
               });

               timer();
               getUserProfile(getUserProfileDataListener());
           }
       });

    }

    public void setWatchVideoReward()
    {
        WatchViewReward watchViewReward =    userProfile.getTimerStatus().getWatchViewReward();

        watchViewReward.setClaimed(false);


    }
    public void timer()
    {
        // Creates a new Handler
        final Handler handler
                = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {



                WatchViewReward watchViewReward = userProfile.getTimerStatus().getWatchViewReward();


                String lastModifiedTime = watchViewReward.getClaimedTime();

                String currentTime = DateTimeHelper.datePojo().getGetCurrentDateString();

                try {
                Date       date1CurrentDate1 = DateTimeHelper.convertStringIntoDate(currentTime);
                    Date date2lastModifiedTime = DateTimeHelper.convertStringIntoDate(lastModifiedTime);
                    long mills =  date1CurrentDate1.getTime()-date2lastModifiedTime.getTime() ;


                    Log.v("Data1", "" + date1CurrentDate1.toString());
                    Log.v("Data2", "" + date2lastModifiedTime.toString());


                    int hours = (int) (mills / (1000 * 60 * 60));
                    int mins = (int) (mills / (1000 * 60)) % 60;

                    int seconds = (int) (mills/1000)%60;


                    String diff = hours + ":" + mins; // u
                    Log.d("pTimer","time difference " + diff);





                    timerHourMinuteSecond
                            = String
                            .format(Locale.getDefault(),
                                    "%02d:%02d:%02d",
                                    hours, mins,seconds);
                    String claimText  ="Next claim in "+timerHourMinuteSecond.replace("-","");


                    if(Math.abs(mins)<=55)
                    {
                       binding.watchVideoTextview.setText("Watch");
                       binding.timerTextview.setVisibility(View.GONE);

                        setWatchVideoReward();

                        return;
                    }
                    else {
                        binding.watchVideoTextview.setText("Watched");
                        binding.timerTextview.setVisibility(View.VISIBLE);
                        binding.timerTextview.setText(claimText);

                    }

                } catch ( Exception e) {
                    Log.d("pTimer","Hello, error ! " + e);

                    e.printStackTrace();
                }




                handler.postDelayed(this, 1100);
            }

        }
        );
    }



}