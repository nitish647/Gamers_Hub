package com.nitish.gamershub.view.base;

import static com.nitish.gamershub.utils.AppHelper.getAdViewedStatsGlobal;

import static com.nitish.gamershub.utils.AppHelper.saveAdViewedStatsGlobal;
import static com.nitish.gamershub.utils.AppHelper.setStatusBarColor;
import static com.nitish.gamershub.utils.AppConstants.From;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.nitish.gamershub.model.firebase.timerStatus.DailyBonus;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.model.local.DialogLoadingItems;
import com.nitish.gamershub.model.local.SnackBarItems;
import com.nitish.gamershub.utils.PreferenceHelper;
import com.nitish.gamershub.utils.adsUtils.AdmobAdsListener;
import com.nitish.gamershub.utils.adsUtils.InterstitialUtilsAdmobAdUtil;
import com.nitish.gamershub.utils.adsUtils.RewardedAdAdmobUtilUtils;
import com.nitish.gamershub.view.dialogs.ConfirmationDialog;
import com.nitish.gamershub.view.dialogs.DialogGamerReward;
import com.nitish.gamershub.view.dialogs.DialogListener;
import com.nitish.gamershub.view.dialogs.LoadingBarDialog;
import com.nitish.gamershub.view.dialogs.SnackBarCustom;
import com.nitish.gamershub.view.dialogs.SuspensionDialog;
import com.nitish.gamershub.view.dialogs.WebViewDialog;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.view.dialogs.RewardsBottomSheetDialog;
import com.nitish.gamershub.utils.adsUtils.AdmobInterstitialAdListener;
import com.nitish.gamershub.databinding.ShowWebviewDialogBinding;
import com.nitish.gamershub.model.firebase.adviewStatus.AdViewedStats;
import com.nitish.gamershub.model.firebase.timerStatus.TimerStatus;
import com.nitish.gamershub.model.firebase.userProfile.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.Connectivity;
import com.nitish.gamershub.utils.AppConstants;
import com.nitish.gamershub.utils.ProgressBarHelper;
import com.nitish.gamershub.utils.firebaseUtils.UserOperations;
import com.nitish.gamershub.view.loginSingup.activity.LoginActivity;

import io.paperdb.Paper;

public abstract class BaseActivity extends AppCompatActivity {


    // google ads
    private InterstitialAd interstitialAd;
    private InterstitialUtilsAdmobAdUtil interstitialAdmobAdUtil;
    private RewardedAd rewardedAd;
    private RewardedAdAdmobUtilUtils rewardedAdAdmobUtil;
    static boolean isLoading;


    static boolean isHomeActivityDestroyed = false;
    RewardsBottomSheetDialog rewardsBottomSheetDialog;
    GoogleSignInOptions gso;
    PreferenceHelper preferenceHelper;
    static boolean bottomSheetDialogShown = false;

    @Nullable
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

//        setStatusBarColor();


        Paper.init(this);
        preferenceHelper = new PreferenceHelper(this);

        initialiseAds();
        getGoogleSignInOptions();

    }


    protected abstract int getLayoutResourceId();


    //----------------- Intent------------///

    public <K>void startActivityIntent(Activity fromActivity, Class<K> toActivity) {
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


    //------------------------- dialog boxes -------------------------------//


    public Boolean showNoInternetDialog() {


        if (!isInternetAvailable()) {

            DialogItems dialogItems = new DialogItems();
            dialogItems.setTitle("");
            dialogItems.setSingleButton(true);
            dialogItems.setDialogIcon(R.drawable.no_internet_icon);
            dialogItems.setMessage(getString(R.string.connection_problem_please_check_your_internet_connection_and_try_again));
            showConfirmationDialog2(dialogItems, null);


            return false;
        } else
            return true;
    }


    public void showRewardDialog(String message, DialogListener dialogListener) {
        DialogItems dialogItems = new DialogItems();
        dialogItems.setMessage(message);

        DialogGamerReward dialogGamerReward = DialogGamerReward.newInstance(dialogItems, dialogListener);


        showDialogFragment(dialogGamerReward, null);


    }

    public void showRewardDialog(DialogItems dialogItems, DialogListener dialogListener) {


        DialogGamerReward dialogGamerReward = DialogGamerReward.newInstance(dialogItems, dialogListener);

        if (!BaseActivity.this.isFinishing()) {
            dialogGamerReward.show(getSupportFragmentManager(), null);
        }

    }


    public void showPrivacyPolicyDialog()
    {

        showWebViewDialog(WebViewDialog.USAGE_PRIVACY_POLICY);
    }
    public void showAboutUsDialog()
    {

        showWebViewDialog(WebViewDialog.USAGE_ABOUT_US);
    }

    public void showWebViewDialog(String usage) {
        WebViewDialog webViewDialog = new WebViewDialog(BaseActivity.this,usage);
        webViewDialog.showDialog();

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


    //------------------------- dialog boxes -------------------------------//


    public DailyBonus getDailyBonusFromProfile(UserProfile userProfile) {
      DailyBonus dailyBonus;
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


    public void initialiseAds() {
        interstitialAdmobAdUtil = new InterstitialUtilsAdmobAdUtil(BaseActivity.this, interstitialAd);
//        interstitialAdmobAdUtil.loadInterstitialAd();

        rewardedAdAdmobUtil = new RewardedAdAdmobUtilUtils(rewardedAd, BaseActivity.this);
//        rewardedAdAdmobUtil.loadRewardedAdmobAd();

    }


    public void showInterstitialAdNew(AdmobInterstitialAdListener interstitialAdListener) {

        interstitialAdmobAdUtil.showInterstitialAdNew(interstitialAdListener);

    }

    public InterstitialAd loadInterstitialAdNew() {


        return interstitialAdmobAdUtil.loadInterstitialAd();

    }

    public void loadRewardedVideoAd() {
        rewardedAdAdmobUtil.loadRewardedAdmobAd();
    }

    public void incrementInterstitialAdAdCount() {

        AdViewedStats adViewedStats;


            UserProfile userProfile = getPreferencesMain().getUserProfile();
            adViewedStats = userProfile.getAdViewedStats();

        int interstitialAdCount = adViewedStats.getAdMobInterstitialAdViewed();
        interstitialAdCount++;

        adViewedStats.setAdMobInterstitialAdViewed(interstitialAdCount);


        userProfile.setAdViewedStats(adViewedStats);
        getPreferencesMain().saveUserProfile(userProfile);

//        saveAdViewedStatsGlobal(adViewedStats);


    }


    public void showRewardedVideo3(AdmobAdsListener.RewardedAdListener rewardedAdListener) {

        rewardedAdAdmobUtil.showRewardedAd(rewardedAdListener);
    }


    public void incrementRewardAdCount() {

        AdViewedStats adViewedStats;

      UserProfile userProfile = getPreferencesMain().getUserProfile();
      adViewedStats = userProfile.getAdViewedStats();

        int rewardAdCount = adViewedStats.getAdMobRewardedAdViewed();
        rewardAdCount++;

        adViewedStats.setAdMobRewardedAdViewed(rewardAdCount);

        userProfile.setAdViewedStats(adViewedStats);

        getPreferencesMain().saveUserProfile(userProfile);

//        saveAdViewedStatsGlobal(adViewedStats);


    }


    // google signin

    public GoogleSignInAccount getGoogleSignInAccount() {

        return getPreferencesMain().getGoogleSignInAccountUser();
    }

    public GoogleSignInClient getGoogleSignInClient() {

        return GoogleSignIn.getClient(this, gso);
    }

    public void performLogOut() {
        googleSignOut();
        getPreferencesMain().destroyAllPreferences();
        startActivity(new Intent(BaseActivity.this, LoginActivity.class));
        finish();
    }

    public void googleSignOut() {

        if (!showNoInternetDialog()) {
            return;
        }

        getGoogleSignInClient().signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        AppHelper.destroyAllPaperData();
//                        startActivity(new Intent(BaseActivity.this, LoginActivity.class));
//                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();


        printCurrentScreenName(true, this.getClass().getName());

    }

    public void printCurrentScreenName(Boolean isActivity, String activityOrFragment) {
        String screenType = "Fragment";
        if (isActivity) {
            screenType = "Activity ";
        }
        String screenName = screenType + " " + activityOrFragment;

        Log.d("currentScreenName", screenName);


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onStart() {
        super.onStart();

        if (getPreferencesMain().getUserMail() == null)
            return;
        UserOperations.getFireStoreUser(getPreferencesMain().getUserMail()).addSnapshotListener(BaseActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(BaseActivity.this, "error while getting doc 211", Toast.LENGTH_SHORT).show();
                }
                if (value != null && value.exists()) {

                    try {
                        UserProfile userProfile = null;
                        userProfile = value.toObject(UserProfile.class);


                    } catch (Exception e) {
                        Log.d("pError", "User profile error " + e);

                    }


                }
            }
        });

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

        if (rewardsBottomSheetDialog == null) {

//                    String timerDataString = DataPassingHelper.ConvertObjectToString(getPreferencesMain().getUserProfile().getTimerStatus());

            rewardsBottomSheetDialog = RewardsBottomSheetDialog.newInstance("");
        }


        try {
            if (bottomSheetDialogShown) {
                if (rewardsBottomSheetDialog.isAdded())
                    rewardsBottomSheetDialog.dismiss();
            }

            showDialogFragment(rewardsBottomSheetDialog, "");
//            bottomSheetDialog.show(getSupportFragmentManager(), "");

            bottomSheetDialogShown = true;
        } catch (Exception e) {
            Log.d("pError", "error in showing botton nav 1123 " + e);
        }


    }


    public void showLoader() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingBarDialog.newInstance(BaseActivity.this, new DialogLoadingItems()).showDialog();

            }
        });

    }

    public void hideLoader() {

        long startTime = System.currentTimeMillis();

        LoadingBarDialog loadingBarDialog = LoadingBarDialog.getInstance();


//        Loader loadingBarDialog =  Loader.getInstance();
        if (loadingBarDialog != null) {

            long currentTime = System.currentTimeMillis() - startTime;

            long maxTime = 3000;
            if (currentTime < maxTime) {
                try {
                    Thread.sleep(maxTime - currentTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            loadingBarDialog.hideDialog();
//            loadingBarDialog.cancelCustomDialog();
        }

    }

    public void showSimpleToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showSnackBar(String message, View rootLayout) {

        SnackBarCustom.showCustomSnackBar(this, rootLayout, new SnackBarItems(message));
    }
    public void showSnackBar(View rootLayout, SnackBarItems snackBarItems) {

        SnackBarCustom.showCustomSnackBar(this, rootLayout,snackBarItems);
    }


    void showDialogFragment(DialogFragment dialog, String tag) {
        if (!this.isFinishing()) {
            dialog.show(getSupportFragmentManager(), tag);
        }
    }


    // will check if the ad view is changed
    public boolean adStatsChanged() {
        if (getPreferencesMain().getUserProfile() == null)
            return false;
        AdViewedStats adViewedStats = getPreferencesMain().getUserProfile().getAdViewedStats();
        AdViewedStats getAdViewedStatsGlobal = getAdViewedStatsGlobal();

        if (getAdViewedStatsGlobal() == null)
            return false;
        else {
            if (getAdViewedStatsGlobal.getAdMobRewardedAdViewed() > adViewedStats.getAdMobRewardedAdViewed()
                    || getAdViewedStatsGlobal.getAdMobInterstitialAdViewed() > adViewedStats.getAdMobInterstitialAdViewed()
                    || getAdViewedStatsGlobal.getFaceBookInterstitialAdViewed() > adViewedStats.getFaceBookInterstitialAdViewed()
                    || getAdViewedStatsGlobal.getFaceBookRewardedAdViewed() > adViewedStats.getFaceBookRewardedAdViewed()) {
                return true;
            } else
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        if (this.getClass().getSimpleName().equals(HomeActivity.class.getSimpleName())) {
            isHomeActivityDestroyed = true;

        }
        super.onDestroy();
    }


    public void setActivityStatusBarColor(int color) {
        setStatusBarColor(this,color);

    }


}
