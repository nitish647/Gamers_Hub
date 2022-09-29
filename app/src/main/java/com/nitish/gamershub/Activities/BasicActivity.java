package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHub_ParentCollection;
import static com.nitish.gamershub.Utils.ConstantsHelper.GoogleSignInAccountUser;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserMail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.nitish.gamershub.Interface.AdmobInterstitialAdListener;
import com.nitish.gamershub.Pojo.FireBase.TimerStatus;
import com.nitish.gamershub.Pojo.FireBase.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.ConstantsHelper;
import com.nitish.gamershub.Utils.ProgressBarHelper;
import com.nitish.gamershub.databinding.GameRewardDialogBinding;
import com.nitish.gamershub.databinding.ShowWebviewDialogBinding;

import io.paperdb.Paper;

abstract class BasicActivity extends AppCompatActivity {


    // google ads
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    boolean isLoading;



    ProgressDialog progressDialog;
    GoogleSignInOptions gso;
    AlertDialog logOutDialog;
    @Nullable
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        Paper.init(this);
         progressDialog = ProgressBarHelper.setProgressBarDialog(BasicActivity.this);
        logOutDialog2();

        loadRewardedAd2();
        getGoogleSignInOptions();


    }

    protected  void getGoogleSignInOptions(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.googleAccountWebClientID)) // This line did the magic for me

                .build();
    }

    protected abstract int getLayoutResourceId();





    public void getUserProfile(GetUserProfileDataListener getUserDataListener)
    {


        showProgressBar();
        getFirebaseUser().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dismissProgressBar();

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {




                        UserProfile userProfile =   documentSnapshot.toObject(UserProfile.class);

                        getUserDataListener.onTaskSuccessful(userProfile);




                    }
                    else {
                        Toast.makeText(BasicActivity.this, "document does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dismissProgressBar();
                Toast.makeText(BasicActivity.this, "couldn't get the documents ", Toast.LENGTH_SHORT).show();
            }
        });



    }


    public void setUserProfile(UserProfile userProfile, SetUserDataListener setUserDataListener)
    {
        getFirebaseUser().set(userProfile, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {


                    setUserDataListener.onTaskSuccessful();

                }
            }
        });
    }

    public interface SetUserDataListener
    {
        public void onTaskSuccessful();
    }

    public interface GetUserProfileDataListener
    {
        public void onTaskSuccessful(UserProfile userProfile);
    }



    public DocumentReference getFirebaseUser()
    {
       return FirebaseFirestore.getInstance().collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail)+"");
    }







    public void showRewardDialog(int amount)
    {
        GameRewardDialogBinding gameRewardDialogBinding;
        LayoutInflater factory = LayoutInflater.from(BasicActivity.this);

        gameRewardDialogBinding =  DataBindingUtil.inflate(factory,R.layout.game_reward_dialog,null,false);

        final AlertDialog addRewardDialog = new AlertDialog.Builder(BasicActivity.this).create();


        addRewardDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        addRewardDialog.setView(gameRewardDialogBinding.getRoot());

        addRewardDialog.show();

        gameRewardDialogBinding.earnedCoinsTextview.setText("Redeem request generated for  ₹"+amount);

        gameRewardDialogBinding.gameCoinsImage.setAnimation(R.raw.redeem_pocket_money);

        gameRewardDialogBinding.cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRewardDialog.dismiss();
            }
        });


    }
    public abstract class OnDialogLister
    {

        public abstract void onDialogDismissLister();
    }

    public void showRewardDialog(String message ,int rawAnimation ,OnDialogLister onDialogLister)
    {
        GameRewardDialogBinding gameRewardDialogBinding;
        LayoutInflater factory = LayoutInflater.from(BasicActivity.this);

        gameRewardDialogBinding =  DataBindingUtil.inflate(factory,R.layout.game_reward_dialog,null,false);

        final AlertDialog addRewardDialog = new AlertDialog.Builder(BasicActivity.this).create();

        addRewardDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        addRewardDialog.setView(gameRewardDialogBinding.getRoot());

        addRewardDialog.show();

        addRewardDialog.setCancelable(false);
        gameRewardDialogBinding.earnedCoinsTextview.setText(message);


        gameRewardDialogBinding.gameCoinsImage.setAnimation(rawAnimation);


        gameRewardDialogBinding.cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRewardDialog.dismiss();
                onDialogLister.onDialogDismissLister();
            }
        });


    }


    public void showWebviewDialog()
    {
        ShowWebviewDialogBinding showWebviewDialogBinding;
        LayoutInflater factory = LayoutInflater.from(BasicActivity.this);

        showWebviewDialogBinding =  DataBindingUtil.inflate(factory,R.layout.show_webview_dialog,null,false);

        final AlertDialog showWebviewDialog = new AlertDialog.Builder(BasicActivity.this).create();


        showWebviewDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        showWebviewDialog.setView(showWebviewDialogBinding.getRoot());

        showWebviewDialog.show();

        showWebviewDialogBinding.webView.loadUrl("file:///android_asset/policy.html");


        showWebviewDialogBinding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWebviewDialog.dismiss();
            }
        });


    }

    public TimerStatus.DailyBonus getDailyBonusFromProfile(UserProfile userProfile )
    {
        TimerStatus.DailyBonus dailyBonus;
        if(userProfile!=null)
        {
            TimerStatus timerStatus = userProfile.getTimerStatus();
             dailyBonus = timerStatus.getDailyBonus();


        }
        else {
            return null;
        }

        return dailyBonus;



    }
    public void logOutDialog2()
    {
        LayoutInflater factory = LayoutInflater.from(BasicActivity.this);

        View logOutView = factory.inflate(R.layout.activity_fav,null);
        logOutDialog = new AlertDialog.Builder(BasicActivity.this).create();

        logOutDialog.setView(logOutView);

    }

    public void showInterstitial2() {
        // Show the ad if it's ready. Otherwise toast and restart the game.

        if (interstitialAd != null && ConstantsHelper.ShowAds) {
            interstitialAd.show(this);

        }
    }




    public void showInterstitialAdNew(AdmobInterstitialAdListener interstitialAdListener)
    {

        if(interstitialAd!=null) {
            interstitialAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            BasicActivity.this.interstitialAd = null;
                            interstitialAdListener.onAdDismissed();


                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when fullscreen content failed to show.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            BasicActivity.this.interstitialAd = null;

                            interstitialAdListener.onAdFailed();
                            Log.d("gInterstitialAd", "The ad failed to show.");

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            Log.d("gInterstitialAd", "The ad was shown.");

                            interstitialAdListener.onAdShown();
                        }
                    });

            interstitialAd.show(BasicActivity.this);

        }else {
            interstitialAdListener.onAdLoading();
        }
    }
    public void loadInterstitialAdNew() {



        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                getString(R.string.admob_inter),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        BasicActivity.this.interstitialAd = interstitialAd;
                        Log.i("gInterstitialAd", "onAdLoaded");


                    }


                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("gInterstitialAd", "ad loading failed : "+loadAdError.getMessage());
                        interstitialAd = null;

                        String error = String.format(
                                "domain: %s, code: %d, message: %s",
                                loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());

                        Log.d("gInterstitialAd","Ad loading failed : "+error);
                    }
                });
    }

    public void loadInterstitialAd2(AdmobInterstitialAdListener interstitialAdListener) {


        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                getString(R.string.admob_inter),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        BasicActivity.this.interstitialAd = interstitialAd;
                        Log.i("gInterstitialAd", "onAdLoaded");

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        BasicActivity.this.interstitialAd = null;

                                        interstitialAdListener.onAdDismissed();



                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        BasicActivity.this.interstitialAd = null;
                                        Log.d("gInterstitialAd", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        interstitialAdListener.onAdShown();
                                        // Called when fullscreen content is shown.
                                        Log.d("gInterstitialAd", "The ad was shown.");
                                    }
                                });
                    }


                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("gInterstitialAd", "ad loading failed : "+loadAdError.getMessage());
                        interstitialAd = null;

                        String error = String.format(
                                "domain: %s, code: %d, message: %s",
                                loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());

                        Log.d("gInterstitialAd","Ad loading failed : "+error);
                    }
                });
    }


    public void loadRewardedAd2() {
        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    this,
                    getString(R.string.admob_reward),
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d("rewardedAd", "onAdFailedToLoad "+loadAdError.getMessage());
                            rewardedAd = null;
                            BasicActivity.this.isLoading = false;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            BasicActivity.this.rewardedAd = rewardedAd;
                            Log.d("rewardedAd", "onAdLoaded");
                            BasicActivity.this.isLoading = false;
                        }
                    });
        }
    }



    public void showRewardedVideo2(RewardedAdListener rewardedAdListener) {

        if (rewardedAd == null) {
            Toast.makeText(this, "The ad is not loaded yet , try again", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }

        rewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d("rewardedAd", "onAdShowedFullScreenContent");


                    }


                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d("rewardedAd", "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;
                        Toast.makeText(BasicActivity.this, "Ad loading failed please try again", Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAdListener.onDismissListener();
                        rewardedAd = null;


                    }
                });
        Activity activityContext = BasicActivity.this;
        rewardedAd.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.

                        rewardedAdListener.onRewardGrantedListener();


                        ConstantsHelper.ShowAds = false;
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                    }
                });
    }

    public interface RewardedAdListener
    {
        public void onDismissListener();

        public void onRewardGrantedListener();


    }
    public interface PaytmUpiDialogListener{
        public void redeemClick();
    }



    public ProgressDialog showProgressBar()
    {
        progressDialog.show();

        return progressDialog;
    }

    public void dismissProgressBar()
    {
        progressDialog.dismiss();
    }





    // google signin

    public GoogleSignInAccount getGoogleSignInAccount()
    {

        return (GoogleSignInAccount)Paper.book().read(GoogleSignInAccountUser);
    }

    public GoogleSignInClient getGoogleSignInClient()
    {

        return GoogleSignIn.getClient(this, gso);
    }

    public void googleSignOut() {
        progressDialog.show();
        getGoogleSignInClient().signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        startActivity(new Intent(BasicActivity.this,LoginPage.class));
                        finish();
                    }
                });
    }
}
