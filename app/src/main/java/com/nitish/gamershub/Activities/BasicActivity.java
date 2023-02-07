package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.AppHelper.getAdViewedStatsGlobal;
import static com.nitish.gamershub.Utils.AppHelper.getUserProfileGlobalData;
import static com.nitish.gamershub.Utils.AppHelper.saveAdViewedStatsGlobal;
import static com.nitish.gamershub.Utils.AppHelper.saveUserProfileGlobal;
import static com.nitish.gamershub.Utils.ConstantsHelper.From;
import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHubDataGlobal;
import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHub_DATA;
import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHub_ParentCollection;
import static com.nitish.gamershub.Utils.ConstantsHelper.GoogleSignInAccountUser;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserMail;
import static com.nitish.gamershub.Utils.ConstantsHelper.populateCategoryList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
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
import com.google.gson.Gson;
import com.nitish.gamershub.Fragments.BottomSheetDialog;
import com.nitish.gamershub.Interface.AdmobInterstitialAdListener;
import com.nitish.gamershub.Interface.ConfirmationDialogListener2;
import com.nitish.gamershub.Pojo.DialogHelperPojo;
import com.nitish.gamershub.Pojo.FireBase.AdViewedStats;
import com.nitish.gamershub.Pojo.FireBase.GamersHubData;
import com.nitish.gamershub.Pojo.FireBase.TimerStatus;
import com.nitish.gamershub.Pojo.FireBase.UserProfile;
import com.nitish.gamershub.Pojo.FireBase.UserTransactions;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.AppHelper;
import com.nitish.gamershub.Utils.Connectivity;
import com.nitish.gamershub.Utils.ConstantsHelper;
import com.nitish.gamershub.Utils.DataPassingHelper;
import com.nitish.gamershub.Utils.ProgressBarHelper;
import com.nitish.gamershub.Utils.UserOperations;
import com.nitish.gamershub.databinding.BlockUserDialogBinding;
import com.nitish.gamershub.databinding.ConfirmationDialogLayoutBinding;
import com.nitish.gamershub.databinding.GameRewardDialogBinding;
import com.nitish.gamershub.databinding.ShowWebviewDialogBinding;

import org.json.JSONObject;

import java.util.List;

import io.paperdb.Paper;

abstract class BasicActivity extends AppCompatActivity {


    // google ads
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
   static boolean isLoading;



  static boolean isHomeActivityDestroyed =false;
   BottomSheetDialog bottomSheetDialog;
   static ProgressDialog progressDialog;
    GoogleSignInOptions gso;
    AlertDialog logOutDialog;
    AlertDialog confirmationDialog;
 private    UserProfile userProfileGlobal;
 private GamersHubData gamersHubDataGlobal;
  TimeChangedReceiver2 timeChangedReceiver2;
   static   boolean bottomSheetDialogShown=false;
    @Nullable
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        setStatusBarColor();

        Paper.init(this);
         progressDialog = ProgressBarHelper.setProgressBarDialog(BasicActivity.this);
         timeChangedReceiver2 = new TimeChangedReceiver2();

        logOutDialog2();
        loadInterstitialAdNew();
        loadRewardedAd2();
        getGoogleSignInOptions();

    }

    protected abstract int getLayoutResourceId();

   
    //----------------- Intent------------///

    public void startActivityIntent(Activity fromActivity , Class toActivity)
    {
        Intent intent = new Intent(this,toActivity);
        intent.putExtra(From, fromActivity.getClass().getSimpleName());
        startActivity(intent);
    }


    public void startIntentWithFlags(Intent intent ,Activity fromActivity)
    {
        intent.putExtra(From,fromActivity.getClass().getSimpleName());
        startActivity(intent);
    }

    public void openPlayStore()
    {
        Intent intent  = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.play_store_link)));
        startActivity(intent);
    }
              //////------------------------------////////




    protected  void getGoogleSignInOptions(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.googleAccountWebClientID)) // This line did the magic for me

                .build();
    }





     void getGamersHubData(GetGamersHubDataListener gamersHubDataListener)
    {


        getFirebaseGamersHubData().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dismissProgressBar();

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {




                        if(progressDialog.isShowing())
                            dismissProgressBar();


                        GamersHubData gamersHubData =   documentSnapshot.toObject(GamersHubData.class);
                        saveGamersHubDataGlobal(gamersHubData);
                        gamersHubDataListener.onTaskSuccessful(gamersHubData);




                    }
                    else {
                        Toast.makeText(BasicActivity.this, "document does not exist 112", Toast.LENGTH_SHORT).show();
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

    void setGamersHubRedeemCoinsList(OnFirestoreDataCompleteListener onFirestoreDataCompleteListener)
    {


        setGamersHubRedeemCoinsList().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dismissProgressBar();

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {
                        onFirestoreDataCompleteListener.OnComplete(documentSnapshot);


                    }
                    else {
                        Toast.makeText(BasicActivity.this, "document does not exist 112", Toast.LENGTH_SHORT).show();
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

    void getGamersHubRedeemCoinsList(OnFirestoreDataCompleteListener onFirestoreDataCompleteListener)
    {


        setGamersHubRedeemCoinsList().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dismissProgressBar();

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {

                        onFirestoreDataCompleteListener.OnComplete(documentSnapshot);


                    }
                    else {
                        Toast.makeText(BasicActivity.this, "document does not exist 112", Toast.LENGTH_SHORT).show();
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

    public interface OnFirestoreDataCompleteListener
    {
        public void OnComplete(DocumentSnapshot documentSnapshot);
    }

     void getUserProfileGlobal(GetUserProfileDataListener getUserDataListener)
    {


        showProgressBar();
        // if intent not available then return
        if( !showNoInternetDialog())
            return;

        getFirebaseUser().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dismissProgressBar();

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();


                    if(documentSnapshot.exists())
                    {

                        if(progressDialog.isShowing())
                            dismissProgressBar();


                        UserProfile userProfile=null;
                        try {
                            JSONObject jsonObject =  new JSONObject(documentSnapshot.getData());
                            userProfile = new Gson().fromJson(jsonObject.toString(),UserProfile.class);

//                             userProfile =   documentSnapshot.toObject(UserProfile.class);
                            Log.d("pResponse","userProfile jsonobject : "+jsonObject);

                           if(userProfile.getProfileData()!=null)
                            Log.d("pResponse","user profile class data "+userProfile.getProfileData().getGameCoins()+" "+userProfile.getProfileData().getDeviceInfo());
                            Log.d("pResponse","userProfile class object : "+DataPassingHelper.ConvertObjectToString(userProfile));


                        }catch (Exception e)
                        {

                            Log.d("pError","Error in converting data into objects  : "+e);

//                            JSONObject jsonObject =  new JSONObject(documentSnapshot.getData());
//                            userProfile = new Gson().fromJson(jsonObject.toString(),UserProfile.class);
//                            Log.d("pResponse","userProfile : "+userProfile);
                        }



                       saveUserProfileGlobal(userProfile);
                        getUserDataListener.onTaskSuccessful(userProfile);




                    }
                    else {
                        Log.d("pError","document does not exist 113  : ");

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
    public interface GetUserProfileDataListener
    {
        public void onTaskSuccessful(UserProfile userProfile);
    }
    public interface GetGamersHubDataListener
    {
        public void onTaskSuccessful(GamersHubData gamersHubData);
    }

    public UserProfile updateUserWaller2(int amount, SetUserDataOnCompleteListener setUserDataOnCompleteListener)
    {

        UserProfile userProfile = getUserProfileGlobalData();
        UserProfile.ProfileData profileData = userProfile.getProfileData();
        int gameCoins =   profileData.getGameCoins();
        int totalCoins = gameCoins+amount;
        profileData.setGameCoins(totalCoins);
        userProfile.setProfileData(profileData);




        showProgressBar();
        setUserProfile(userProfile, setUserDataOnCompleteListener);

        return  userProfile;
    }

     UserProfile updateUserWalletForTransaction(int amount, SetUserDataOnCompleteListener setUserDataOnCompleteListener , UserTransactions userTransactions)
    {

        UserProfile userProfile = getUserProfileGlobalData();
        UserProfile.ProfileData profileData = userProfile.getProfileData();
        int gameCoins =   profileData.getGameCoins();
        int totalCoins = gameCoins+amount;
        profileData.setGameCoins(totalCoins);
        userProfile.setProfileData(profileData);

        userProfile.setUserTransactions(userTransactions);



        showProgressBar();
        setUserProfile(userProfile, setUserDataOnCompleteListener);

        return  userProfile;
    }

     void setUserProfile(UserProfile userProfile, SetUserDataOnCompleteListener setUserDataOnCompleteListener)
    {
        if( !showNoInternetDialog())
            return;

        getFirebaseUser().set(userProfile, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dismissProgressBar();
                if(task.isSuccessful() && task.isComplete() )
                {


                    saveUserProfileGlobal(userProfile);

                    setUserDataOnCompleteListener.onTaskSuccessful();


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissProgressBar();
               DialogHelperPojo dialogHelperPojo = new DialogHelperPojo();
               Log.d("pError","error1123 in the update profile "+e);
                dialogHelperPojo.setMessage("A server error occurred");

                getConfirmationDialog(dialogHelperPojo, new ConfirmationDialogListener2() {
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




    public interface SetUserDataOnCompleteListener
    {
        public void onTaskSuccessful();
    }





    public static DocumentReference getFirebaseUser()
    {
       return FirebaseFirestore.getInstance().collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail)+"");
    }

    public DocumentReference getFirebaseGamersHubData()
    {
        return FirebaseFirestore.getInstance().collection(GamersHub_DATA).document("gamersHubData");
    }
    public DocumentReference setGamersHubRedeemCoinsList()
    {
        return FirebaseFirestore.getInstance().collection(GamersHub_DATA).document("redeemCoinsList");
    }











    //------------------------- dialog boxes -------------------------------//


     Boolean showNoInternetDialog()
    {
        if(!isInternetAvailable())
        {
            dismissProgressBar();
            DialogHelperPojo dialogHelperPojo = new DialogHelperPojo();
            dialogHelperPojo.setTitle("");
            dialogHelperPojo.setDialogIcon(R.drawable.no_internet_icon);
            dialogHelperPojo.setMessage("Connection problem, please check your internet connection and try again");

            getConfirmationDialogSingleButton(dialogHelperPojo, new ConfirmationDialogListener2() {
                @Override
                public void onYesClick() {


                }

                @Override
                public void onNoClick() {

                }
            });

            return false;
        }else

        return true;
    }
    public void showRewardDialog(String message)
    {
        GameRewardDialogBinding gameRewardDialogBinding;
        LayoutInflater factory = LayoutInflater.from(BasicActivity.this);

        gameRewardDialogBinding =  DataBindingUtil.inflate(factory,R.layout.game_reward_dialog,null,false);

        final AlertDialog addRewardDialog = new AlertDialog.Builder(BasicActivity.this).create();


        addRewardDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        addRewardDialog.setView(gameRewardDialogBinding.getRoot());

        addRewardDialog.show();

        gameRewardDialogBinding.earnedCoinsTextview.setText(message);

        gameRewardDialogBinding.gameCoinsImage.setAnimation(R.raw.redeem_pocket_money);

        gameRewardDialogBinding.cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRewardDialog.dismiss();
            }
        });


    }
    public void showRewardDialog(int amount,int rawAnimation)
    {
        GameRewardDialogBinding gameRewardDialogBinding;
        LayoutInflater factory = LayoutInflater.from(BasicActivity.this);

        gameRewardDialogBinding =  DataBindingUtil.inflate(factory,R.layout.game_reward_dialog,null,false);

        final AlertDialog addRewardDialog = new AlertDialog.Builder(BasicActivity.this).create();


        addRewardDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        addRewardDialog.setView(gameRewardDialogBinding.getRoot());

        addRewardDialog.show();

        gameRewardDialogBinding.earnedCoinsTextview.setText(getString(R.string.earned_reward_message)+ " "+amount+" coins");

        gameRewardDialogBinding.gameCoinsImage.setAnimation(rawAnimation);

        gameRewardDialogBinding.gameCoinsImage.setAnimation(R.raw.redeem_pocket_money);

        gameRewardDialogBinding.cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRewardDialog.dismiss();
            }
        });


    }
    public void logOutDialog2()
    {
        LayoutInflater factory = LayoutInflater.from(BasicActivity.this);

        View logOutView = factory.inflate(R.layout.activity_fav,null);
        logOutDialog = new AlertDialog.Builder(BasicActivity.this).create();

        logOutDialog.setView(logOutView);

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
    public void showRewardDialog(String message ,int rawAnimation ,OnDialogLister onDialogLister)
    {
        GameRewardDialogBinding gameRewardDialogBinding;
        LayoutInflater factory = LayoutInflater.from(BasicActivity.this);

        gameRewardDialogBinding =  DataBindingUtil.inflate(factory,R.layout.game_reward_dialog,null,false);

        final AlertDialog addRewardDialog = new AlertDialog.Builder(BasicActivity.this).create();

        addRewardDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        addRewardDialog.setView(gameRewardDialogBinding.getRoot());


        if(!BasicActivity.this.isFinishing())
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
    public void showSuspendDialog(String message)
    {
        BlockUserDialogBinding blockUserDialogBinding;
        LayoutInflater factory = LayoutInflater.from(BasicActivity.this);

        blockUserDialogBinding =  DataBindingUtil.inflate(factory,R.layout.block_user_dialog,null,false);

        final AlertDialog addRewardDialog = new AlertDialog.Builder(BasicActivity.this).create();

        addRewardDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        addRewardDialog.setView(blockUserDialogBinding.getRoot());

        addRewardDialog.show();

        addRewardDialog.setMessage(message);
        blockUserDialogBinding.understandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addRewardDialog.setCancelable(false);


    }

    public void showConfirmationDialog(String title,String message,ConfirmationDialogListener confirmationDialogListener)
    {

        setConfirmationDialog(title,message,confirmationDialogListener);

        confirmationDialog.show();

    }

    public void showConfirmationDialogSingleButton(String buttonTitle,String title,String message,ConfirmationDialogListener confirmationDialogListener)
    {

        ConfirmationDialogLayoutBinding confirmationDialogLayoutBinding=    setConfirmationDialog(title,message,confirmationDialogListener);

        confirmationDialogLayoutBinding.NoButton.setVisibility(View.GONE);


            confirmationDialogLayoutBinding.yesButton.setText(buttonTitle);
            // for update disable the dismiss
        if(buttonTitle.toLowerCase().contains("update")) {
            confirmationDialogLayoutBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmationDialogListener.onYesClick();
                }
            });
        }
        confirmationDialog.show();

    }

    public ConfirmationDialogLayoutBinding setConfirmationDialog(String title,String message,ConfirmationDialogListener confirmationDialogListener)
    {
        ConfirmationDialogLayoutBinding confirmationDialogLayoutBinding;
        LayoutInflater factory = LayoutInflater.from(BasicActivity.this);

        confirmationDialogLayoutBinding =  DataBindingUtil.inflate(factory,R.layout.confirmation_dialog_layout,null,false);

          confirmationDialog = new AlertDialog.Builder(BasicActivity.this).create();

        confirmationDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        confirmationDialog.setView(confirmationDialogLayoutBinding.getRoot());
        confirmationDialog.setCancelable(false);
        confirmationDialog.show();
        confirmationDialogLayoutBinding.titleTextview.setText(title);
        confirmationDialogLayoutBinding.messageTextview.setText(message);


        confirmationDialogLayoutBinding.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog.dismiss();
                confirmationDialogListener.onYesClick();
            }
        });
        confirmationDialogLayoutBinding.NoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog.dismiss();
                confirmationDialogListener.onNoClick();
            }
        });

        return confirmationDialogLayoutBinding;


    }

    public ConfirmationDialogLayoutBinding getConfirmationDialogSingleButton(DialogHelperPojo dialogHelperPojo, ConfirmationDialogListener2 confirmationDialogListener2)
    {
        ConfirmationDialogLayoutBinding confirmationDialogLayoutBinding;
        LayoutInflater factory = LayoutInflater.from(BasicActivity.this);

        confirmationDialogLayoutBinding =  DataBindingUtil.inflate(factory,R.layout.confirmation_dialog_layout,null,false);

        confirmationDialog = new AlertDialog.Builder(BasicActivity.this).create();

        confirmationDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        confirmationDialog.setView(confirmationDialogLayoutBinding.getRoot());
        confirmationDialog.setCancelable(false);
        confirmationDialog.show();
        confirmationDialogLayoutBinding.titleTextview.setText(dialogHelperPojo.getTitle());
        confirmationDialogLayoutBinding.messageTextview.setText(Html.fromHtml(dialogHelperPojo.getMessage()));
        confirmationDialogLayoutBinding.dialogIcon.setImageResource(dialogHelperPojo.getDialogIcon());
        // by default set yes when the


        if(dialogHelperPojo.getYesButton().equals(new DialogHelperPojo().getYesButton()))
        confirmationDialogLayoutBinding.yesButton.setText("Ok");

        confirmationDialogLayoutBinding.NoButton.setVisibility(View.GONE);

        confirmationDialogLayoutBinding.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmationDialog.dismiss();
                confirmationDialogListener2.onYesClick();


            }
        });
        confirmationDialogLayoutBinding.NoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog.dismiss();
                confirmationDialogListener2.onNoClick();

            }
        });

        return confirmationDialogLayoutBinding;


    }

    public ConfirmationDialogLayoutBinding getConfirmationDialog(DialogHelperPojo dialogHelperPojo, ConfirmationDialogListener2 confirmationDialogListener2)
    {
        ConfirmationDialogLayoutBinding confirmationDialogLayoutBinding;
        LayoutInflater factory = LayoutInflater.from(BasicActivity.this);

        confirmationDialogLayoutBinding =  DataBindingUtil.inflate(factory,R.layout.confirmation_dialog_layout,null,false);

        confirmationDialog = new AlertDialog.Builder(BasicActivity.this).create();

        confirmationDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        confirmationDialog.setView(confirmationDialogLayoutBinding.getRoot());
        confirmationDialog.setCancelable(false);
        confirmationDialog.show();
        confirmationDialogLayoutBinding.titleTextview.setText(dialogHelperPojo.getTitle());
        confirmationDialogLayoutBinding.messageTextview.setText(Html.fromHtml(dialogHelperPojo.getMessage()));
        confirmationDialogLayoutBinding.yesButton.setText(dialogHelperPojo.getYesButton());
        confirmationDialogLayoutBinding.NoButton.setText(dialogHelperPojo.getNoButton());

        confirmationDialogLayoutBinding.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmationDialog.dismiss();
                confirmationDialogListener2.onYesClick();


            }
        });
        confirmationDialogLayoutBinding.NoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog.dismiss();
                confirmationDialogListener2.onNoClick();

            }
        });

        return confirmationDialogLayoutBinding;


    }
    public static interface ConfirmationDialogListener
    {
        public void onDismissListener();

        public void onYesClick();
        public void onNoClick();

        public void onRewardGrantedListener();


    }


    //------------------------- dialog boxes -------------------------------//


    public abstract class OnDialogLister
    {

        public abstract void onDialogDismissLister();
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
                            incrementInterstitialAdAdCount();
                            interstitialAdListener.onAdShown();
                        }
                    });

            interstitialAd.show(BasicActivity.this);

        }else {
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

        return interstitialAd;
    }
    public void incrementInterstitialAdAdCount()
    {

        AdViewedStats adViewedStats;

        if(getAdViewedStatsGlobal()==null)
        {
            UserProfile userProfile =getUserProfileGlobalData();
            adViewedStats = userProfile.getAdViewedStats()   ;
        }
        else {
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
                        incrementRewardAdCount();
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

    public void incrementRewardAdCount()
    {

        AdViewedStats adViewedStats;

        if(getAdViewedStatsGlobal()==null)
        {
            UserProfile userProfile =getUserProfileGlobalData();
            adViewedStats = userProfile.getAdViewedStats()   ;
        }
        else {
            adViewedStats = getAdViewedStatsGlobal();
        }
   int rewardAdCount = adViewedStats.getAdMobRewardedAdViewed();
       rewardAdCount++;

      adViewedStats.setAdMobRewardedAdViewed(rewardAdCount);
        saveAdViewedStatsGlobal(adViewedStats);



    }
    public interface PaytmUpiDialogListener{
        public void redeemClick();
    }



    public ProgressDialog showProgressBar()
    {
        try {
            if(!BasicActivity.this.isFinishing())
                progressDialog.show();

        }catch (Exception e)
        {

        }

        return progressDialog;
    }

    public  void dismissProgressBar()
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

        if(!showNoInternetDialog())
        {
            return;
        }
        showProgressBar();
        getGoogleSignInClient().signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       dismissProgressBar();
                        AppHelper.destroyAllPaperData();
                        startActivity(new Intent(BasicActivity.this,LoginPage.class));
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();



        Log.d("onResume: Activity ", this.getClass().getName());
        if(getSupportFragmentManager().findFragmentById(R.id.frameLayout) != null){
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for(Fragment f: fragments) {
                if(f.isVisible())
                    Log.d("onResume: Fragment ", f.getClass().getName());
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

        UserOperations.getFirestoreUser().addSnapshotListener(BasicActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Toast.makeText(BasicActivity.this, "error while getting doc 211", Toast.LENGTH_SHORT).show();
                }
                if(value!=null && value.exists())
                {

                    try {
                        UserProfile userProfile=null;
                        userProfile=   value.toObject(UserProfile.class);

                        if(userProfile!=null) {
                            saveUserProfileGlobal(userProfile);


                        }


                    }catch (Exception e)
                    {
                        Log.d("pError","User profile error "+e);

                    }



                }
            }
        });

    }

    public static class TimeChangedReceiver2 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //   Toast.makeText(context,"time changed",Toast.LENGTH_LONG).show();
            StringBuilder sb = new StringBuilder();
            sb.append("Action: " + intent.getAction() + "\n");
            sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
            String log = sb.toString();
            Log.d("pResponse", "time changed broadcast "+log);
            Toast.makeText(context, log, Toast.LENGTH_LONG).show();
        }

    }
    public void saveGamersHubDataGlobal(GamersHubData gamersHubData)
    {

          gamersHubDataGlobal=gamersHubData;
        Paper.book().write(GamersHubDataGlobal,gamersHubDataGlobal);



    }

     boolean isInternetAvailable()
    {
        String connectivityStatus = Connectivity.getConnectionStatus(BasicActivity.this);

        if(connectivityStatus.equals(ConstantsHelper.ConnectionSignalStatus.NO_CONNECTIVITY+"")||
                connectivityStatus.equals(ConstantsHelper.ConnectionSignalStatus.POOR_STRENGTH+"" ))
        {
            return false;
        }
        else
            return true;
    }



    public void showBottomSheet()
    {

        // will show the bottom sheet only once the app is started

        if(isHomeActivityDestroyed||bottomSheetDialogShown)
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

             bottomSheetDialog.show(getSupportFragmentManager(), "");

             bottomSheetDialogShown = true;
         }
         catch (Exception e)
         {
             Log.d("pError","error in showing botton nav 1123 "+e);
         }




            }



    public void hideBottomSheet()
    {
        if(bottomSheetDialog!=null)
          bottomSheetDialog.dismiss();
        bottomSheetDialogShown = false;
    }
    public BottomSheetDialog getBottomSheetDialog()
    {
        return bottomSheetDialog;
    }

    // will check if the ad view is changed
   public boolean adStatsChanged()
   {
       if(getUserProfileGlobalData()==null)
           return false;
    AdViewedStats adViewedStats =   getUserProfileGlobalData().getAdViewedStats()  ;

    if(getAdViewedStatsGlobal()==null)
        return false;
    else {
        if(getAdViewedStatsGlobal().getAdMobRewardedAdViewed()>adViewedStats.getAdMobRewardedAdViewed()
         ||getAdViewedStatsGlobal().getAdMobInterstitialAdViewed()>adViewedStats.getAdMobInterstitialAdViewed()
            ||getAdViewedStatsGlobal().getFaceBookInterstitialAdViewed()>adViewedStats.getFaceBookInterstitialAdViewed()
            ||getAdViewedStatsGlobal().getFaceBookRewardedAdViewed()>adViewedStats.getFaceBookRewardedAdViewed())
        {
            return true;
        }
        else
            return false;
    }
   }
    @Override
    protected void onDestroy() {
        if(this.getClass().getSimpleName().equals(HomeActivity.class.getSimpleName()))
        {  isHomeActivityDestroyed = true;
               UserProfile userProfile =   getUserProfileGlobalData() ;
            // upload the ad viewed status when exiting from the app
            if(adStatsChanged())
            {

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


    void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if(this.getClass().getName().equals(Splash_Screen.class.getName())) {
                window.setStatusBarColor(this.getResources().getColor(R.color.splash_screen));
            } else if(this.getClass().getName().equals(LoginPage.class.getName())) {
                window.setStatusBarColor(this.getResources().getColor(R.color.login_page));
            }else {
                window.setStatusBarColor(this.getResources().getColor(R.color.gamers_hub_theme));
            }
        }
    }
}
