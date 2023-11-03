package com.nitish.gamershub.view.base;

import static com.nitish.gamershub.utils.AppHelper.getAdViewedStatsGlobal;
import static com.nitish.gamershub.utils.AppHelper.getUserProfileGlobalData;
import static com.nitish.gamershub.utils.AppHelper.saveAdViewedStatsGlobal;
import static com.nitish.gamershub.utils.AppHelper.saveUserProfileGlobal;
import static com.nitish.gamershub.utils.AppHelper.setStatusBarColor;
import static com.nitish.gamershub.utils.AppConstants.From;
import static com.nitish.gamershub.utils.AppConstants.GamersHubDataGlobal;
import static com.nitish.gamershub.utils.AppConstants.GamersHub_ParentCollection;
import static com.nitish.gamershub.utils.AppConstants.GoogleSignInAccountUser;
import static com.nitish.gamershub.utils.AppConstants.UserMail;

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
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
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
import com.nitish.gamershub.model.local.DialogLoadingItems;
import com.nitish.gamershub.utils.PreferenceHelper;
import com.nitish.gamershub.utils.adsUtils.AdmobAdsListener;
import com.nitish.gamershub.utils.adsUtils.InterstitialUtilsAdmobAdUtil;
import com.nitish.gamershub.utils.adsUtils.RewardedAdAdmobUtilUtils;
import com.nitish.gamershub.view.dialogs.ConfirmationDialog;
import com.nitish.gamershub.view.dialogs.CustomLoadingBar2;
import com.nitish.gamershub.view.dialogs.DialogGamerReward;
import com.nitish.gamershub.view.dialogs.DialogListener;
import com.nitish.gamershub.view.dialogs.Loader;
import com.nitish.gamershub.view.dialogs.LoadingBarDialog;
import com.nitish.gamershub.view.dialogs.SnackBarCustom;
import com.nitish.gamershub.view.dialogs.SuspensionDialog;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.view.dialogs.BottomSheetDialog;
import com.nitish.gamershub.utils.adsUtils.AdmobInterstitialAdListener;
import com.nitish.gamershub.databinding.ShowWebviewDialogBinding;
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
    private InterstitialUtilsAdmobAdUtil interstitialAdmobAdUtil;
    private RewardedAd rewardedAd;
    private RewardedAdAdmobUtilUtils rewardedAdAdmobUtil;
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

        initialiseAds();
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


    public void setUserProfile(UserProfile userProfile, SetUserDataOnCompleteListener setUserDataOnCompleteListener) {
        if (!showNoInternetDialog())
            return;


        getFirebaseUser().set(userProfile, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideLoader();
                if (task.isSuccessful() && task.isComplete()) {


                    saveUserProfileGlobal(userProfile);

                    setUserDataOnCompleteListener.onTaskSuccessful();


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideLoader();

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


    public void showWebViewDialog() {
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


    }


    public void showRewardedVideo3(AdmobAdsListener.RewardedAdListener rewardedAdListener) {

        rewardedAdAdmobUtil.showRewardedAd(rewardedAdListener);
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
        hideLoader();
        getGoogleSignInClient().signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideLoader();
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

    public void showLoader(DialogLoadingItems items) {
        LoadingBarDialog.newInstance(this, items).showDialog();


    }

    public void showLoader() {

        LoadingBarDialog.newInstance(this, new DialogLoadingItems()).showDialog();

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
        SnackBarCustom.showCustomSnackBar(this, rootLayout, message, 3000);
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
