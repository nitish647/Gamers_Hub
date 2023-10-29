package com.nitish.gamershub.view.rewards.activity;

import static com.nitish.gamershub.utils.AppHelper.getGamersHubDataGlobal;
import static com.nitish.gamershub.utils.AppHelper.getUserProfileGlobalData;
import static com.nitish.gamershub.utils.AppConstants.timerHourMinuteSecond;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.adsUtils.AdmobInterstitialAdListener;
import com.nitish.gamershub.model.firebase.TimerStatus;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.model.firebase.WatchViewReward;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;
import com.nitish.gamershub.databinding.ActivityRewardsBinding;
import com.nitish.gamershub.utils.adsUtils.AdmobAdsListener;
import com.nitish.gamershub.view.base.BaseActivity;
import com.nitish.gamershub.view.dialogs.DialogListener;
import com.nitish.gamershub.view.loginSingup.viewmodelRepo.LoginSignUpViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RewardsActivity extends BaseActivity {

    ActivityRewardsBinding binding;
    UserProfile userProfile;

    boolean isLoading;
    public int seconds = 0;

    private LoginSignUpViewModel viewModel;

    String Usage_Update_Daily_Reward = "Usage_Update_Daily_Reward";
    String Usage_Update_watchViewReward = "Usage_Update_watchViewReward";
    String successDailyCheckRewardBonusMessage;
    String successUpdateWatchViewRewardMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rewards);
        viewModel = ViewModelProviders.of(this).get(LoginSignUpViewModel.class);

        setOnclickListeners();

        loadInterstitialAdNew();
        loadRewardedVideoAd();

        getProfileData();
        bindObservers();
    }


    public void setOnclickListeners() {

        binding.watchVideoRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WatchViewReward watchViewReward = userProfile.getTimerStatus().getWatchViewReward();

                if (!watchViewReward.isClaimed()) {

                    showRewardedVideo3(setRewardedAdListener);
                    // show Video Ad
                } else {
                    Toast.makeText(RewardsActivity.this, "already claimed", Toast.LENGTH_SHORT).show();
                    // show already claimed
                }

            }
        });


        binding.transactionHistoryRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RewardsActivity.this, TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });

        binding.redeemCoinsRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RewardsActivity.this, RedeemActivity.class);
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


    public void bindObservers() {
        viewModel.updateUserProfileLD.observe(this, new Observer<NetworkResponse<Object>>() {
            @Override
            public void onChanged(NetworkResponse<Object> response) {
                if (response instanceof NetworkResponse.Success) {
                    dismissProgressBar();
                    String usage = ((NetworkResponse.Success<Object>) response).getMessage();


                    if (usage.equals(Usage_Update_Daily_Reward)) {

                        mShowRewardDialog(successDailyCheckRewardBonusMessage, usage);
//                        showRewardDialog(successDailyCheckRewardBonusMessage, R.raw.rupee_piggy_bank_award, new OnDialogLister() {
//                            @Override
//                            public void onDialogDismissLister() {
//                                showInterstitialAdNew(setInterstitialAdListener());
//
//                            }
//                        });
                    } else if (usage.equals(Usage_Update_watchViewReward)) {
                        mShowRewardDialog(successUpdateWatchViewRewardMessage, usage);

//                        showRewardDialog(successUpdateWatchViewRewardMessage, R.raw.rupee_piggy_bank_award, new OnDialogLister() {
//                            @Override
//                            public void onDialogDismissLister() {
//
//                            }
//                        });

                        timerForRewardVideo();
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
        return R.layout.activity_rewards;
    }

    public void getProfileData() {

        RewardsActivity.this.userProfile = getUserProfileGlobalData();
        updateViews(userProfile);


    }


    public void updateViews(UserProfile userProfile) {
        UserProfile.ProfileData profileData = userProfile.getProfileData();
        TimerStatus timerStatus = userProfile.getTimerStatus();
        TimerStatus.DailyBonus dailyBonus = timerStatus.getDailyBonus();


        binding.coinsTextview.setText(profileData.getGameCoins() + "");

        if (dailyBonus.getClaimed()) {
            binding.claimTextview.setText("claimed");

        } else {
            binding.claimTextview.setText("claim");
            //    binding.timerTextview.setVisibility(View.GONE);

        }
        WatchViewReward watchViewReward = timerStatus.getWatchViewReward();
        if (!watchViewReward.isClaimed()) {
            binding.watchVideoTextview.setText("Watch");
            binding.timerTextview.setVisibility(View.GONE);
        } else {
            timerForRewardVideo();
        }

    }


    public void setDailyBonus() {
        if (userProfile != null) {
            TimerStatus.DailyBonus dailyBonus = getDailyBonusFromProfile(userProfile);
            if (dailyBonus.getClaimed()) {
                Toast.makeText(RewardsActivity.this, "You have already claimed the daily bonus , please come tomorrow", Toast.LENGTH_LONG).show();

            } else {
                dailyBonus.setClaimed(true);
                String currentDate = DateTimeHelper.getDatePojo().getGetCurrentDateString();
                dailyBonus.setLastResetDateTime(DateTimeHelper.resetDateToATime(currentDate, DateTimeHelper.time_7_am));
                dailyBonus.setClaimedDate(currentDate);
                userProfile.getTimerStatus().setDailyBonus(dailyBonus);
                int getDailyCheckReward = getGamersHubDataGlobal().getGamesData().getDailyCheckReward();
                userProfile.getProfileData().setGameCoins(userProfile.getProfileData().getGameCoins() + getDailyCheckReward);


                successDailyCheckRewardBonusMessage = "Successfully credited " + getDailyCheckReward + " coins for Daily bonus";
                callUpdateUser(userProfile, Usage_Update_Daily_Reward);
//                setUserProfile(userProfile, new SetUserDataOnCompleteListener() {
//                    @Override
//                    public void onTaskSuccessful() {
//
//                        showRewardDialog("Successfully credited " + getDailyCheckReward + " coins for Daily bonus", R.raw.rupee_piggy_bank_award, new OnDialogLister() {
//                            @Override
//                            public void onDialogDismissLister() {
//                                showInterstitialAdNew(setInterstitialAdListener());
//                            }
//                        });
//
//                        getUserProfileGlobal(getUserProfileDataListener());
//                    }
//                });
            }


        }
    }


    public AdmobAdsListener.RewardedAdListener setRewardedAdListener =
            new AdmobAdsListener.RewardedAdListener() {
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


    public AdmobInterstitialAdListener setInterstitialAdListener() {
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

    public void setWatchVideoRewardAfterVideo() {
        WatchViewReward watchViewReward = getUserProfileGlobalData().getTimerStatus().getWatchViewReward();

        // increment 1 hour in the current date
        Calendar cal = Calendar.getInstance();// creates calendar
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(DateTimeHelper.getDatePojo().getGetCurrentDate());               // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1);      // adds one hour

        watchViewReward.setClaimedTime(DateTimeHelper.convertDateToString(cal.getTime()));
        watchViewReward.setClaimed(true);

        binding.watchVideoTextview.setText("watched");
        int coins = userProfile.getProfileData().getGameCoins();

        int getWatchVideoReward = getGamersHubDataGlobal().getGamesData().getWatchVideoReward();
        int totalCoins = coins + getWatchVideoReward;
        userProfile.getProfileData().setGameCoins(totalCoins);
        userProfile.getTimerStatus().setWatchViewReward(watchViewReward);


        successUpdateWatchViewRewardMessage = "Successfully credited " + getWatchVideoReward + " coins for Watching video";
        callUpdateUser(userProfile, Usage_Update_watchViewReward);
//        setUserProfile(userProfile, new SetUserDataOnCompleteListener() {
//            @Override
//            public void onTaskSuccessful() {
//                showRewardDialog("Successfully credited " + getWatchVideoReward + " coins for Watching video", R.raw.rupee_piggy_bank_award, new OnDialogLister() {
//                    @Override
//                    public void onDialogDismissLister() {
//
//                    }
//                });
//
//                timerForRewardVideo();
//                getUserProfileGlobal(getUserProfileDataListener());
//            }
//        });

    }

    private DialogListener rewardDialogListener(String usage) {
        return new DialogListener() {
            @Override
            public void onYesClick() {

            }

            @Override
            public void onNoClick() {

            }

            @Override
            public void onDialogDismissed() {
                if (usage.equals(Usage_Update_Daily_Reward)) {
                    showInterstitialAdNew(setInterstitialAdListener());
                }

            }
        };
    }

    public void mShowRewardDialog(String message, String usage) {
        DialogItems dialogItems = new DialogItems();
        dialogItems.setRawAnimation(R.raw.rupee_piggy_bank_award);
        dialogItems.setMessage(message);

        showRewardDialog(dialogItems, rewardDialogListener(usage));

    }

    public void timerForRewardVideo() {

        // Creates a new Handler
        final Handler handler
                = new Handler();
        handler.post(new Runnable() {
                         @Override
                         public void run() {


                             WatchViewReward watchViewReward = userProfile.getTimerStatus().getWatchViewReward();


                             String lastModifiedTime = watchViewReward.getClaimedTime();

                             String currentTime = DateTimeHelper.getDatePojo().getGetCurrentDateString();

                             try {
                                 Date date1CurrentDate1 = DateTimeHelper.convertStringIntoDate(currentTime);
                                 Date date2lastModifiedTime = DateTimeHelper.convertStringIntoDate(lastModifiedTime);
                                 long mills = date1CurrentDate1.getTime() - date2lastModifiedTime.getTime();


                                 Log.v("Data1", "" + date1CurrentDate1.toString());
                                 Log.v("Data2", "" + date2lastModifiedTime.toString());


                                 int hours = (int) (mills / (1000 * 60 * 60));
                                 int mins = (int) (mills / (1000 * 60)) % 60;

                                 int seconds = (int) (mills / 1000) % 60;


                                 String diff = hours + ":" + mins; // u
                                 Log.d("pTimer", "time difference " + diff);


                                 timerHourMinuteSecond
                                         = String
                                         .format(Locale.getDefault(),
                                                 "%02d:%02d:%02d",
                                                 hours, mins, seconds);
                                 String claimText = "Next claim in " + timerHourMinuteSecond.replace("-", "");


                                 if (hours >= 1) {
                                     if (!userProfile.getTimerStatus().getWatchViewReward().isClaimed())
                                         return;
                                     binding.watchVideoTextview.setText("Watch");
                                     binding.timerTextview.setVisibility(View.GONE);
                                     userProfile.getTimerStatus().getWatchViewReward().setClaimed(false);

//                                     setUserProfile(userProfile, new SetUserDataOnCompleteListener() {
//                                         @Override
//                                         public void onTaskSuccessful() {
//
//                                         }
//                                     });
                                     callUpdateUser(userProfile, "");
                                     return;

                                 } else {
                                     binding.watchVideoTextview.setText("Watched");
                                     binding.timerTextview.setVisibility(View.VISIBLE);
                                     binding.timerTextview.setText(claimText);

                                 }

                             } catch (Exception e) {
                                 Log.d("pError", "Hello, error 112221 ! " + e);

                                 e.printStackTrace();
                             }


                             handler.postDelayed(this, 1100);
                         }

                     }
        );
    }


    @Override
    protected void onResume() {
        super.onResume();
        userProfile = getUserProfileGlobalData();
        if (userProfile.getTimerStatus() != null)
            updateViews(userProfile);
    }


    private void callUpdateUser(UserProfile userProfile, String usage) {

        viewModel.callUpdateUserProfile(userProfile, usage);
    }
}