package com.nitish.gamershub.view.base;

import static com.nitish.gamershub.utils.AppHelper.getAdViewedStatsGlobal;
import static com.nitish.gamershub.utils.AppHelper.getUserProfileGlobalData;
import static com.nitish.gamershub.utils.AppHelper.saveAdViewedStatsGlobal;
import static com.nitish.gamershub.utils.AppHelper.saveUserProfileGlobal;
import static com.nitish.gamershub.utils.AppHelper.setStatusBarColor;
import static com.nitish.gamershub.utils.AppConstants.From;
import static com.nitish.gamershub.utils.AppConstants.GamersHubDataGlobal;
import static com.nitish.gamershub.utils.AppConstants.GamersHub_DATA;
import static com.nitish.gamershub.utils.AppConstants.GamersHub_ParentCollection;
import static com.nitish.gamershub.utils.AppConstants.GoogleSignInAccountUser;
import static com.nitish.gamershub.utils.AppConstants.UserMail;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.utils.PreferenceHelper;
import com.nitish.gamershub.view.dialogs.ConfirmationDialog;
import com.nitish.gamershub.view.dialogs.DialogGamerReward;
import com.nitish.gamershub.view.dialogs.DialogListener;
import com.nitish.gamershub.view.dialogs.SuspensionDialog;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.view.dialogs.BottomSheetDialog;
import com.nitish.gamershub.utils.interfaces.AdmobInterstitialAdListener;
import com.nitish.gamershub.utils.interfaces.ConfirmationDialogListener2;
import com.nitish.gamershub.databinding.BlockUserDialogBinding;
import com.nitish.gamershub.databinding.ConfirmationDialogLayoutBinding;
import com.nitish.gamershub.databinding.ShowWebviewDialogBinding;
import com.nitish.gamershub.model.local.DialogHelperPojo;
import com.nitish.gamershub.model.firebase.AdViewedStats;
import com.nitish.gamershub.model.firebase.GamersHubData;
import com.nitish.gamershub.model.firebase.TimerStatus;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.Connectivity;
import com.nitish.gamershub.utils.AppConstants;
import com.nitish.gamershub.utils.ProgressBarHelper;
import com.nitish.gamershub.utils.firebaseUtils.UserOperations;
import com.nitish.gamershub.view.loginSingup.activity.LoginActivity;
import com.nitish.gamershub.view.loginSingup.activity.SplashScreen;

import java.util.List;

import io.paperdb.Paper;

public abstract class BaseActivity extends AppCompatActivity {


    // google ads
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    static boolean isLoading;


    static boolean isHomeActivityDestroyed = false;
    BottomSheetDialog bottomSheetDialog;
    public static ProgressDialog progressDialog;
    GoogleSignInOptions gso;
    AlertDialog logOutDialog;
    AlertDialog confirmationDialog;
    private UserProfile userProfileGlobal;
    private GamersHubData gamersHubDataGlobal;
    PreferenceHelper preferenceHelper;
    static boolean bottomSheetDialogShown = false;

    @Nullable
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

//        setStatusBarColor();

        setActivityStatusBarColor();


        Paper.init(this);
        progressDialog = ProgressBarHelper.setProgressBarDialog(BaseActivity.this);
        preferenceHelper = new PreferenceHelper(this);

        loadInterstitialAdNew();
        loadRewardedAd2();
        getGoogleSignInOptions();

    }


    protected abstract int getLayoutResourceId();


    //----------------- Intent------------///

    public void startActivityIntent(Activity fromActivity, Class toActivity) {
        Intent intent = new Intent(this, toActivity);
        intent.putExtra(From, fromActivity.getClass().getSimpleName());
        startActivity(intent);
    }


    public void startIntentWithFlags(Intent intent, Activity fromActivity) {
        intent.putExtra(From, fromActivity.getClass().getSimpleName());
        startActivity(intent);
    }

    public void openPlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.play_store_link)));
        startActivity(intent);
    }
    //////------------------------------////////


    protected void getGoogleSignInOptions() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.googleAccountWebClientID)) // This line did the magic for me

                .build();
    }

    public PreferenceHelper getPreferencesMain() {
        return preferenceHelper;
    }


    public void setGamersHubRedeemCoinsList(OnFirestoreDataCompleteListener onFirestoreDataCompleteListener) {


        setGamersHubRedeemCoinsList().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dismissProgressBar();

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()) {
                        onFirestoreDataCompleteListener.OnComplete(documentSnapshot);


                    } else {
                        Toast.makeText(BaseActivity.this, "document does not exist 112", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dismissProgressBar();
                Toast.makeText(BaseActivity.this, "couldn't get the documents ", Toast.LENGTH_SHORT).show();
            }
        });


    }


    public interface OnFirestoreDataCompleteListener {
        public void OnComplete(DocumentSnapshot documentSnapshot);
    }


    public void setUserProfile(UserProfile userProfile, SetUserDataOnCompleteListener setUserDataOnCompleteListener) {
        if (!showNoInternetDialog())
            return;



        getFirebaseUser().set(userProfile, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dismissProgressBar();
                if (task.isSuccessful() && task.isComplete()) {


                    saveUserProfileGlobal(userProfile);

                    setUserDataOnCompleteListener.onTaskSuccessful();


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissProgressBar();

                DialogItems dialogItems = new DialogItems();
                Log.d("pError", "error1123 in the update profile " + e);
                dialogItems.setMessage("A server error occurred");


                showConfirmationDialog2(dialogItems, new DialogListener() {
                    @Override
                    public void onYesClick() {

                    }

                    @Override
                    public void onNoClick() {

                    }
                });
            }
        });
    }


    public interface SetUserDataOnCompleteListener {
        public void onTaskSuccessful();
    }


    public static DocumentReference getFirebaseUser() {
        return FirebaseFirestore.getInstance().collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail) + "");
    }


    public DocumentReference setGamersHubRedeemCoinsList() {
        return FirebaseFirestore.getInstance().collection(GamersHub_DATA).document("redeemCoinsList");
    }


    //------------------------- dialog boxes -------------------------------//


    public Boolean showNoInternetDialog() {


        if (!isInternetAvailable()) {

            DialogItems dialogItems = new DialogItems();
            dialogItems.setTitle("");
            dialogItems.setSingleButton(true);
            dialogItems.setDialogIcon(R.drawable.no_internet_icon);
            dialogItems.setMessage("Connection problem, please check your internet connection and try again");
            showConfirmationDialog2(dialogItems, null);


            return false;
        } else
            return true;
    }


    public void showRewardDialog(String message, DialogListener dialogListener) {
        DialogItems dialogItems = new DialogItems();
        dialogItems.setMessage(message);

        DialogGamerReward dialogGamerReward = DialogGamerReward.newInstance(dialogItems, dialogListener);


        showDialogfragment(dialogGamerReward, null);


    }

    public void showRewardDialog(DialogItems dialogItems, DialogListener dialogListener) {


        DialogGamerReward dialogGamerReward = DialogGamerReward.newInstance(dialogItems, dialogListener);

        if (!BaseActivity.this.isFinishing()) {
            dialogGamerReward.show(getSupportFragmentManager(), null);
        }

    }


    public void showWebviewDialog() {
        ShowWebviewDialogBinding showWebviewDialogBinding;
        LayoutInflater factory = LayoutInflater.from(BaseActivity.this);

        showWebviewDialogBinding = DataBindingUtil.inflate(factory, R.layout.show_webview_dialog, null, false);

        final AlertDialog showWebviewDialog = new AlertDialog.Builder(BaseActivity.this).create();


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


    public void showSuspendDialog(DialogItems dialogItems, DialogListener dialogListener) {

        SuspensionDialog suspensionDialog = SuspensionDialog.newInstance(dialogItems, dialogListener);

        suspensionDialog.show(getSupportFragmentManager(), null);
    }



    public void showConfirmationDialogSingleButton2(DialogItems dialogItems, DialogListener dialogListener) {
        dialogItems.setSingleButton(true);

        showConfirmationDialog2(dialogItems, dialogListener);


    }






    public void showConfirmationDialog2(DialogItems dialogItems, DialogListener dialogListener) {


        ConfirmationDialog confirmationDialog1 = ConfirmationDialog.newInstance(dialogItems, dialogListener);

        confirmationDialog1.show(getSupportFragmentManager(), null);
    }


    public static interface ConfirmationDialogListener {
        public void onDismissListener();

        public void onYesClick();

        public void onNoClick();

        public void onRewardGrantedListener();


    }


    //------------------------- dialog boxes -------------------------------//


    public TimerStatus.DailyBonus getDailyBonusFromProfile(UserProfile userProfile) {
        TimerStatus.DailyBonus dailyBonus;
        if (userProfile != null) {
            TimerStatus timerStatus = userProfile.getTimerStatus();

            // TestCode: Anuraag
//            timerStatus = null;
            dailyBonus = timerStatus.getDailyBonus();


        } else {
            return null;
        }

        return dailyBonus;


    }


    public void showInterstitialAdNew(AdmobInterstitialAdListener interstitialAdListener) {

        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            BaseActivity.this.interstitialAd = null;
                            interstitialAdListener.onAdDismissed();


                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when fullscreen content failed to show.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            BaseActivity.this.interstitialAd = null;

                            interstitialAdListener.onAdFailed();
                            Log.d("gInterstitialAd", "The ad failed to show.");

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            Log.d("gInterstitialAd", "The ad was shown.");
                            incrementInterstitialAdAdCount();
                            interstitialAdListener.onAdShown();
                        }
                    });

            interstitialAd.show(BaseActivity.this);

        } else {
            interstitialAdListener.onAdLoading();
        }
    }

    public InterstitialAd loadInterstitialAdNew() {


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
                        BaseActivity.this.interstitialAd = interstitialAd;
                        Log.i("gInterstitialAd", "onAdLoaded");


                    }


                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("gInterstitialAd", "ad loading failed : " + loadAdError.getMessage());
                        interstitialAd = null;

                        String error = String.format(
                                "domain: %s, code: %d, message: %s",
                                loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());

                        Log.d("gInterstitialAd", "Ad loading failed : " + error);
                    }
                });

        return interstitialAd;
    }

    public void incrementInterstitialAdAdCount() {

        AdViewedStats adViewedStats;

        if (getAdViewedStatsGlobal() == null) {
            UserProfile userProfile = getUserProfileGlobalData();
            adViewedStats = userProfile.getAdViewedStats();
        } else {
            adViewedStats = getAdViewedStatsGlobal();
        }
        int interstitialAdCount = adViewedStats.getAdMobInterstitialAdViewed();
        interstitialAdCount++;

        adViewedStats.setAdMobInterstitialAdViewed(interstitialAdCount);
        saveAdViewedStatsGlobal(adViewedStats);

//        userProfile.setAdViewedStats(adViewedStats);
//
//        setUserProfile(userProfile, new SetUserDataOnCompleteListener() {
//            @Override
//            public void onTaskSuccessful() {
//
//            }
//        });

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
                            Log.d("rewardedAd", "onAdFailedToLoad " + loadAdError.getMessage());
                            rewardedAd = null;
                            BaseActivity.this.isLoading = false;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            BaseActivity.this.rewardedAd = rewardedAd;
                            Log.d("rewardedAd", "onAdLoaded");
                            BaseActivity.this.isLoading = false;
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
                        Toast.makeText(BaseActivity.this, "Ad loading failed please try again", Toast.LENGTH_SHORT)
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
        Activity activityContext = BaseActivity.this;
        rewardedAd.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        incrementRewardAdCount();
                        rewardedAdListener.onRewardGrantedListener();


                        AppConstants.ShowAds = false;
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                    }
                });
    }

    public interface RewardedAdListener {
        public void onDismissListener();

        public void onRewardGrantedListener();


    }

    public void incrementRewardAdCount() {

        AdViewedStats adViewedStats;

        if (getAdViewedStatsGlobal() == null) {
            UserProfile userProfile = getUserProfileGlobalData();
            adViewedStats = userProfile.getAdViewedStats();
        } else {
            adViewedStats = getAdViewedStatsGlobal();
        }
        int rewardAdCount = adViewedStats.getAdMobRewardedAdViewed();
        rewardAdCount++;

        adViewedStats.setAdMobRewardedAdViewed(rewardAdCount);
        saveAdViewedStatsGlobal(adViewedStats);


    }


    public ProgressDialog showProgressBar() {
        try {
            if (!BaseActivity.this.isFinishing())
                progressDialog.show();

        } catch (Exception e) {

        }

        return progressDialog;
    }

    public void dismissProgressBar() {
        progressDialog.dismiss();
    }


    // google signin

    public GoogleSignInAccount getGoogleSignInAccount() {

        return (GoogleSignInAccount) Paper.book().read(GoogleSignInAccountUser);
    }

    public GoogleSignInClient getGoogleSignInClient() {

        return GoogleSignIn.getClient(this, gso);
    }

    public void googleSignOut() {

        if (!showNoInternetDialog()) {
            return;
        }
        showProgressBar();
        getGoogleSignInClient().signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dismissProgressBar();
                        AppHelper.destroyAllPaperData();
                        startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();


        Log.d("onResume: Activity ", this.getClass().getName());
        if (getSupportFragmentManager().findFragmentById(R.id.frameLayout) != null) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment f : fragments) {
                if (f.isVisible()) {
                    Log.d("onResume: Fragment ", f.getClass().getName());
                }
            }
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onStart() {
        super.onStart();

        UserOperations.getFirestoreUser().addSnapshotListener(BaseActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(BaseActivity.this, "error while getting doc 211", Toast.LENGTH_SHORT).show();
                }
                if (value != null && value.exists()) {

                    try {
                        UserProfile userProfile = null;
                        userProfile = value.toObject(UserProfile.class);

                        if (userProfile != null) {
                            saveUserProfileGlobal(userProfile);


                        }


                    } catch (Exception e) {
                        Log.d("pError", "User profile error " + e);

                    }


                }
            }
        });

    }


    public void saveGamersHubDataGlobal(GamersHubData gamersHubData) {

        gamersHubDataGlobal = gamersHubData;
        Paper.book().write(GamersHubDataGlobal, gamersHubDataGlobal);


    }

    public boolean isInternetAvailable() {
        String connectivityStatus = Connectivity.getConnectionStatus(BaseActivity.this);
//
//        if (connectivityStatus.equals(AppConstants.ConnectionSignalStatus.NO_CONNECTIVITY + "") ||
//                connectivityStatus.equals(AppConstants.ConnectionSignalStatus.POOR_STRENGTH + ""))
        return !connectivityStatus.equals(AppConstants.ConnectionSignalStatus.NO_CONNECTIVITY + "");
    }


    public void showBonusBottomSheet() {

        // will show the bottom sheet only once the app is started

        if (isHomeActivityDestroyed || bottomSheetDialogShown)
            return;

        if (bottomSheetDialog == null) {

//                    String timerDataString = DataPassingHelper.ConvertObjectToString(getUserProfileGlobalData().getTimerStatus());

            bottomSheetDialog = BottomSheetDialog.newInstance("");
        }


        try {
            if (bottomSheetDialogShown) {
                if (bottomSheetDialog.isAdded())
                    bottomSheetDialog.dismiss();
            }

            showDialogfragment(bottomSheetDialog, "");
//            bottomSheetDialog.show(getSupportFragmentManager(), "");

            bottomSheetDialogShown = true;
        } catch (Exception e) {
            Log.d("pError", "error in showing botton nav 1123 " + e);
        }


    }

    void showDialogfragment(DialogFragment dialog, String tag) {
        if (!this.isFinishing()) {
            dialog.show(getSupportFragmentManager(), tag);
        }
    }


    // will check if the ad view is changed
    public boolean adStatsChanged() {
        if (getUserProfileGlobalData() == null)
            return false;
        AdViewedStats adViewedStats = getUserProfileGlobalData().getAdViewedStats();

        if (getAdViewedStatsGlobal() == null)
            return false;
        else {
            if (getAdViewedStatsGlobal().getAdMobRewardedAdViewed() > adViewedStats.getAdMobRewardedAdViewed()
                    || getAdViewedStatsGlobal().getAdMobInterstitialAdViewed() > adViewedStats.getAdMobInterstitialAdViewed()
                    || getAdViewedStatsGlobal().getFaceBookInterstitialAdViewed() > adViewedStats.getFaceBookInterstitialAdViewed()
                    || getAdViewedStatsGlobal().getFaceBookRewardedAdViewed() > adViewedStats.getFaceBookRewardedAdViewed()) {
                return true;
            } else
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        if (this.getClass().getSimpleName().equals(HomeActivity.class.getSimpleName())) {
            isHomeActivityDestroyed = true;
            UserProfile userProfile = getUserProfileGlobalData();
            // upload the ad viewed status when exiting from the app
            if (adStatsChanged()) {

                userProfile.setAdViewedStats(getAdViewedStatsGlobal());
                setUserProfile(userProfile, new SetUserDataOnCompleteListener() {
                    @Override
                    public void onTaskSuccessful() {
                        saveAdViewedStatsGlobal(getAdViewedStatsGlobal());
                    }
                });

            }

        }
        super.onDestroy();
    }


    public void setActivityStatusBarColor() {
        if (this.getClass().getName().equals(SplashScreen.class.getName())) {
            setStatusBarColor(this, R.color.splash_screen);
        } else if (this.getClass().getName().equals(LoginActivity.class.getName())) {
            setStatusBarColor(this, R.color.login_page);
        } else {
            setStatusBarColor(this, R.color.gamers_hub_theme);
        }

    }


}
