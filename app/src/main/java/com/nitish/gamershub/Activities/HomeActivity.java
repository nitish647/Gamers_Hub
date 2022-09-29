package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Adapters.CategoriesAdapter.context;
import static com.nitish.gamershub.Utils.ConstantsHelper.FavouriteList;
import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHub_ParentCollection;
import static com.nitish.gamershub.Utils.ConstantsHelper.GoogleSignInAccountUser;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserInfo;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserMail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.nitish.gamershub.Adapters.CategoriesAdapter;
import com.nitish.gamershub.Fragments.HomeFragment;
import com.nitish.gamershub.Fragments.ProfileFragment;
import com.nitish.gamershub.Interface.AdmobInterstitialAdListener;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.Pojo.Categories;
import com.nitish.gamershub.Pojo.FireBase.WatchViewReward;
import com.nitish.gamershub.Pojo.NetWorkTimerResult;
import com.nitish.gamershub.Pojo.FireBase.TimerStatus;
import com.nitish.gamershub.Pojo.FireBase.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.ConstantsHelper;
import com.nitish.gamershub.Utils.DateTimeHelper;
import com.nitish.gamershub.Utils.DeviceHelper;
import com.nitish.gamershub.Utils.NotificationHelper;
import com.nitish.gamershub.Utils.ProgressBarHelper;
import com.nitish.gamershub.Utils.UserOperations;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends BasicActivity {

    // the whole game data
    JSONObject masterDataJsonObject;




    int currentSelectedFragPosition=0;



    HomeFragment homeFragment;
    ProfileFragment profileFragment;
    Fragment previousFragment;
    RequestQueue requestQueue;
    List<Categories> categoriesList;
    RecyclerView categoriesRecycler;


    NavigationView navigationView;
    DrawerLayout drawerLayout;
    LinearLayout linearAdContainer;
    FrameLayout frameLayout;

    AdView googleBannerAdView;
    boolean interstitialAdDismissed = false;



    private InterstitialAd interstitialAd;

    Button logoutButton;

    private RewardedAd rewardedAd;
    boolean isLoading;
    ArrayList<AllGamesItems> mainGamesArrayList;
    ProgressDialog progressDialog;


    // firebase auth

    FirebaseFirestore firestoreDb;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Paper.init(this);

        firestoreDb = FirebaseFirestore.getInstance();
        requestQueue = Volley.newRequestQueue(HomeActivity.this);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(HomeActivity.this);
        Paper.book().write(GoogleSignInAccountUser,acct);
        setViews();





        Log.d("pResponse","server timer response "+  Timestamp.now().toDate().toString());

        categoriesList = new ArrayList<>();
        navigationView.setVisibility(View.VISIBLE);


        NotificationHelper.generateFcmToken(HomeActivity.this);
        setHeader();
        updateUserInfo();

        loadInterstitialAd2(interstitialAdListener());


       MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logOutDialog();
            }
        });


        // just writing an empty favourite list to avoid null pointer when reading the data

        if(!Paper.book().contains(FavouriteList)||Paper.book().read(FavouriteList)==null) {
            ArrayList<AllGamesItems> favouriteArrayList = new ArrayList<>();
            Paper.book().write(FavouriteList, favouriteArrayList);
        }



            setBottomNavigationView();


        loadRewardedAd();
        setUpBannerAd();

   //     AdsHelper.loadInterstitialAd(this);

        setCategory();





    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }


    public AdmobInterstitialAdListener interstitialAdListener()
    {
        return  new AdmobInterstitialAdListener() {
            @Override
            public void onAdDismissed() {
                Intent intent = new Intent(HomeActivity.this, GameDetailActivity2.class);
                //    intent.putExtra(gameDataObject, SelectedGameObject);
                startActivity(intent);
            }

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
    public void setBottomNavigationView()
    {
        homeFragment = HomeFragment.newInstance("","");
        showHideFragment(homeFragment,homeFragment.getTag());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId())
                {
                    case R.id.homeMenu:
                        if(homeFragment==null)
                        {
                            homeFragment = HomeFragment.newInstance("","");

                        }
                        showHideFragment(homeFragment,homeFragment.getTag());


                        break;

                    case R.id.profileMenu:

                        if(profileFragment ==null)
                        {
                            profileFragment = profileFragment.newInstance("","");

                        }
                        showHideFragment(profileFragment, profileFragment.getTag());


                        break;

                }
                return false;
            }
        });
    }

    public void showHideFragment(Fragment fragment ,String tag)
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



    public void updateUserInfo()
    {

        getFirebaseUser().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {


                        UserProfile  userProfile=   documentSnapshot.toObject(UserProfile.class);

                        if(documentSnapshot.contains("timerStatus"))
                        {

                            setTimerStatus(userProfile);



                        }
                        else {
                            userProfile.setTimerStatus(createTimerStatus());
                        }

                        UserProfile.ProfileData profileData = userProfile.profileData;

                        profileData.setLastOpened(DateTimeHelper.datePojo().getGetCurrentDateString());
                        profileData.setDeviceInfo(DeviceHelper.getDeviceNameAndVersion());
                        if(profileData.getCreatedAt().trim().isEmpty())
                        {
                            profileData.setCreatedAt(DateTimeHelper.datePojo().getGetCurrentDateString());
                        }
                        userProfile.setProfileData(profileData);
                        firestoreDb.collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail)+"").set(userProfile, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Paper.book().write(UserInfo,userProfile);
                                }
                            }
                        });

                    }
                    else {
                        Toast.makeText(HomeActivity.this, "document does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this, "couldn't get the documents ", Toast.LENGTH_SHORT).show();
            }
        });



    }
    public TimerStatus createTimerStatus()
    {
       TimerStatus timerStatus = new TimerStatus();
        TimerStatus.DailyBonus dailyBonus = new TimerStatus.DailyBonus();

        dailyBonus.setClaimed(false);


        SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeHelper.simpleDateFormatPattern);
        String currentDateTime = dateFormat.format(new Date());

        dailyBonus.setLastResetDateTime(DateTimeHelper.resetDateToATime(currentDateTime,DateTimeHelper.time_7_am));
        dailyBonus.setClaimedDate(currentDateTime);

        timerStatus.setDailyBonus(dailyBonus);
        return  timerStatus;
    }


    public void updateTimerStatus(TimerStatus.DailyBonus dailyBonus)
    {

        firestoreDb.collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail)+"").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {

                        UserProfile  userProfile=   documentSnapshot.toObject(UserProfile.class);
                        TimerStatus timerStatus = userProfile.getTimerStatus();

                        userProfile.getTimerStatus().setDailyBonus(dailyBonus);
                        userProfile.setTimerStatus(timerStatus);

                        firestoreDb.collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail)+"").set(userProfile, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {





                               //     Toast.makeText(HomeActivity.this, "daily earn enabled", Toast.LENGTH_LONG).show();





                                }
                            }
                        });

                    }
                    else {
                        Toast.makeText(HomeActivity.this, "document does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this, "couldn't get the documents ", Toast.LENGTH_SHORT).show();
            }
        });



    }
    // post method
    public void getTimeApi(TimerStatus.DailyBonus dailyBonus)
    {



        String url = getString(R.string.getCurrentTimeAsiaKolkata);



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //    progressBarDialog.dismiss(); // close the progressbar
                        Log.d("pResponse","offlinebillgenerate response : "+response.toString());
                        try {



                            if(response.has("dateTime"))

                            {

                                NetWorkTimerResult netWorkTimerResult  = new Gson().fromJson(response.toString(),NetWorkTimerResult.class);
                                runTimerSample(netWorkTimerResult.toString(), dailyBonus);
                            }


                        } catch (Exception e) {

                            Toast.makeText(context, " Error333 , could generate the bill ", Toast.LENGTH_LONG).show();

                            Log.e("pError3223",e.toString());
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getContext(), "error "+error.toString(), Toast.LENGTH_LONG).show();
//                        progressBarDialog.dismiss(); // close the progressbar


                        Log.e("pError",error.toString());


                    }
                })



        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                //instead o f pass current user authtoken
//             params.put("authtoken", Paper.book().read("authToken"));
//
                params.put("authtoken", Paper.book().read("authToken"));




                return params;

            }
        };


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));




        requestQueue.add(jsonObjectRequest);
    }
    private void  setTimerStatus(UserProfile userProfile)
    {


        if(userProfile.getTimerStatus()==null)
            return;


            TimerStatus.DailyBonus dailyBonus = userProfile.getTimerStatus().getDailyBonus();


            if(dailyBonus!=null)
            {

                getTimeApi(dailyBonus);


            }
            else {
             dailyBonus = new TimerStatus.DailyBonus();
             userProfile.getTimerStatus().setDailyBonus(dailyBonus);
            }

            // initialize video reward
        if(userProfile.getTimerStatus().getWatchViewReward()==null)
        {
            WatchViewReward watchViewReward = new WatchViewReward();
            watchViewReward.setClaimed(false);
            watchViewReward.setClaimedTime(DateTimeHelper.datePojo().getGetCurrentDateString());

            userProfile.getTimerStatus().setWatchViewReward(watchViewReward);
        }
        }











    public void setCategory()
    {
        //category images

         categoriesList.add(new Categories(R.drawable.fav_on2,"","Favourites"));
        categoriesList.add(new Categories(R.drawable.new_icon1,"","New"));
        categoriesList.add(new Categories(R.drawable.best_games,"","Best"));
        categoriesList.add(new Categories(R.drawable.action_icon1,"","Action"));
        categoriesList.add(new Categories(R.drawable.arcade_icon1,"","Arcade"));
        categoriesList.add(new Categories(R.drawable.shooter_icon1,"","Shooting"));
        categoriesList.add(new Categories(R.drawable.puzzle_icon1,"","Puzzle"));
        categoriesList.add(new Categories(R.drawable.board_icon1,"","Board"));
        categoriesList.add(new Categories(R.drawable.racing_icon1,"","Racing"));
        categoriesList.add(new Categories(R.drawable.strategy_icon1,"","Strategy"));

        categoriesRecycler.setLayoutManager(new GridLayoutManager(this,2));
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(this,categoriesList);
        categoriesRecycler.setAdapter(categoriesAdapter);




    }

    public void startIntent()
    {
        if(interstitialAd!=null && ConstantsHelper.ShowAds)
        {
//           showInterstitial();
            showInterstitial2();
        }
        else {

            Intent intent = new Intent(HomeActivity.this, GameDetailActivity2.class);
        //    intent.putExtra(gameDataObject, SelectedGameObject);
            startActivity(intent);
        }
    }
    public void setUpBannerAd()
    {

        AdRequest adRequest = new AdRequest.Builder().build();
        googleBannerAdView.loadAd(adRequest);

    }

    public  void runTimerSample(String currentTime,TimerStatus.DailyBonus dailyBonus) {

        String lastModifiedTime = dailyBonus.getLastResetDateTime();
        Log.d("pResponse","current time "+currentTime);

        Date date1CurrentDate1 = null;
        try {
            date1CurrentDate1 = DateTimeHelper.convertStringIntoDate(currentTime);
            Date date2lastModifiedTime = DateTimeHelper.convertStringIntoDate(lastModifiedTime);
            long mills =  date1CurrentDate1.getTime()-date2lastModifiedTime.getTime() ;


            Log.v("Data1", "" + date1CurrentDate1.toString());
            Log.v("Data2", "" + date2lastModifiedTime.toString());


            int hours = (int) (mills / (1000 * 60 * 60));
            int mins = (int) (mills / (1000 * 60)) % 60;

            String diff = hours + ":" + mins; // u
            Log.d("pTimer","time difference " + diff);


            // reset the time to 8 pm when the time difference is 24 hours
            if(hours>=24)
            {
                dailyBonus.setClaimed(false);
                dailyBonus.setClaimedDate(DateTimeHelper.datePojo().getSimpleDateFormat().format(date1CurrentDate1)+"");
                dailyBonus.setLastResetDateTime(DateTimeHelper.resetDateToATime(date1CurrentDate1,DateTimeHelper.time_7_am));

                Log.d("pTimer","Calender timer " +  DateTimeHelper.datePojo().getSimpleDateFormat().format(date1CurrentDate1));
                updateTimerStatus(dailyBonus);

                //     dailyBonus.setLastResetDateTime();
            }


        } catch ( Exception e) {
            Log.d("pTimer","Hello, error ! " + e);

            e.printStackTrace();
        }


    }
    @Override
    public void onStart() {
        super.onStart();
        UserOperations.getFirestoreUser().addSnapshotListener(HomeActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Toast.makeText(HomeActivity.this, "error while getting doc 211", Toast.LENGTH_SHORT).show();
                }
                if(value!=null && value.exists())
                {
                    UserProfile  userProfile=   value.toObject(UserProfile.class);

                    UserProfile.ProfileData profileData = userProfile.profileData;

                }
            }
        });

    }

    @Override
    public void onPause() {

        if (googleBannerAdView != null) {
            googleBannerAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();

        if (googleBannerAdView != null) {
            googleBannerAdView.resume();

        }
//        // load the ad again
//        if(interstitialAd==null)
//            loadInterstitialAd();
//

    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (googleBannerAdView != null) {
            googleBannerAdView.destroy();
        }
        super.onDestroy();
    }



    @Override
    protected void onStop() {
        super.onStop();
        interstitialAdDismissed = false;
     //   Toast.makeText(this, "On stop", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
    private void loadRewardedAd() {
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
                            HomeActivity.this.isLoading = false;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            HomeActivity.this.rewardedAd = rewardedAd;
                            Log.d("rewardedAd", "onAdLoaded");
                            HomeActivity.this.isLoading = false;
                        }
                    });
        }
    }



    private void showRewardedVideo() {

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
                        Toast.makeText(HomeActivity.this, "Ad loading failed please try again", Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;

                        HomeActivity.this.loadRewardedAd();
                    }
                });
        Activity activityContext = HomeActivity.this;
        rewardedAd.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Toast.makeText(HomeActivity.this, "Congrats the ads are disabled for now", Toast.LENGTH_SHORT)
                                .show();
                        // Preload the next rewarded ad.
                        ConstantsHelper.ShowAds = false;
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                    }
                });
    }
    public void logOutDialog()
    {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
        android.app.AlertDialog deleteDialog = builder.create();


        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                googleSignOut();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();

    }

    public void setHeader()
    {


        TextView profileName = findViewById(R.id.profileName);
        CircleImageView profileIcon =findViewById(R.id.profileIcon);

     //   profileName.setText("hahahhaha");

        if(getGoogleSignInAccount()!=null) {

            Log.d("pResonse","google id name"+getGoogleSignInAccount().getDisplayName()+" image "+getGoogleSignInAccount().getPhotoUrl());
            profileName.setText(getGoogleSignInAccount().getDisplayName());
         //   if(!Objects.requireNonNull(getGoogleSignInAccount().getPhotoUrl()).equals("null"))
          //  Picasso.get().load(getGoogleSignInAccount().getPhotoUrl()).into(profileIcon);
        }
        else {
            Toast.makeText(this, "current user is null", Toast.LENGTH_SHORT).show();
        }





    }
    public void setViews()
    {
        logoutButton= findViewById(R.id.logoutButton);

        progressDialog = ProgressBarHelper.setProgressBarDialog(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        frameLayout = findViewById(R.id.frameLayout);
        googleBannerAdView = findViewById(R.id.googleBannerAdView);
        categoriesRecycler = findViewById(R.id.categoriesRecycler);
        bottomNavigationView= findViewById(R.id.bottomNavigationView);


    }


}