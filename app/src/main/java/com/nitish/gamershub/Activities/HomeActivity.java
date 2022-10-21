package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Adapters.CategoriesAdapter.context;
import static com.nitish.gamershub.Utils.ConstantsHelper.FavouriteList;
import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHub_ParentCollection;
import static com.nitish.gamershub.Utils.ConstantsHelper.GoogleSignInAccountUser;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserMail;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.instacart.library.truetime.TrueTime;
import com.nitish.gamershub.Adapters.CategoriesAdapter;
import com.nitish.gamershub.BuildConfig;
import com.nitish.gamershub.Fragments.HomeFragment;
import com.nitish.gamershub.Fragments.ProfileFragment;
import com.nitish.gamershub.Interface.AdmobInterstitialAdListener;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.Pojo.Categories;
import com.nitish.gamershub.Pojo.FireBase.AdViewedStats;
import com.nitish.gamershub.Pojo.FireBase.GamersHubData;
import com.nitish.gamershub.Pojo.FireBase.UserAccountStatus;
import com.nitish.gamershub.Pojo.FireBase.WatchViewReward;
import com.nitish.gamershub.Pojo.NetWorkTimerResult;
import com.nitish.gamershub.Pojo.FireBase.TimerStatus;
import com.nitish.gamershub.Pojo.FireBase.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.AppHelper;
import com.nitish.gamershub.Utils.ConstantsHelper;
import com.nitish.gamershub.Utils.DateTimeHelper;
import com.nitish.gamershub.Utils.DeviceHelper;
import com.nitish.gamershub.Utils.NotificationHelper;
import com.nitish.gamershub.Utils.ProgressBarHelper;
import com.nitish.gamershub.Utils.SNTPClient;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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


        String playStoreVersionCode = FirebaseRemoteConfig.getInstance().getString(
                "appVersion");

        Log.d("pResponse","playStoreVersionCode "+  playStoreVersionCode);
        categoriesList = new ArrayList<>();
        navigationView.setVisibility(View.VISIBLE);

        new AsyncTaskExample().execute("","","");


        NotificationHelper.generateFcmToken();
        setHeader();
        updateUserInfo();

        loadInterstitialAdNew();

        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {

            @Override
            public void onTimeResponse(String rawDate, Date date, Exception ex) {

                Log.d("SNTPClient",DateTimeHelper.convertDateToString(date));
            }
        });
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

                openGameDetailsActivity();
            }

            @Override
            public void onAdShown() {

            }

            @Override
            public void onAdFailed() {

            }

            @Override
            public void onAdLoading() {

                openGameDetailsActivity();
            }
        };
    }

  public void   openGameDetailsActivity()
    {
        startActivityIntent(HomeActivity.this,GameDetailActivity2.class);

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




    public void onGetGamersHubData(GamersHubData gamersHubData)
    {


    }
    public void updateUserInfo()
    {


        getUserProfileGlobal(getUserProfileDataListener());


    }

    public TimerStatus createTimerStatus()
    {
       TimerStatus timerStatus = new TimerStatus();


       // DAILY BONUS
        TimerStatus.DailyBonus dailyBonus = new TimerStatus.DailyBonus();

        dailyBonus.setClaimed(false);


        String currentDateTime = DateTimeHelper.getDatePojo().getGetCurrentDateString();

        dailyBonus.setLastResetDateTime(DateTimeHelper.resetDateToATime(currentDateTime,DateTimeHelper.time_7_am));
        dailyBonus.setClaimedDate(currentDateTime);

        timerStatus.setDailyBonus(dailyBonus);

        // watchViewReward
        WatchViewReward watchViewReward = new WatchViewReward();
        watchViewReward.setClaimed(false);
        watchViewReward.setClaimedTime(currentDateTime);
        timerStatus.setWatchViewReward(watchViewReward);

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
                                checkDailyBonus(netWorkTimerResult.toString(), dailyBonus);
                            }


                        } catch (Exception e) {

                            Toast.makeText(context, " Error333 ,  ", Toast.LENGTH_LONG).show();

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
            TimerStatus.DailyBonus dailyBonus = userProfile.getTimerStatus().getDailyBonus();

            if(dailyBonus!=null)
            {

                // when the daily bonus is claimed then check for the time
                if(dailyBonus.getClaimed())
                   getTimeApi(dailyBonus);
                else
                {
                    showBottomSheet();
                }


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
            watchViewReward.setClaimedTime(DateTimeHelper.getDatePojo().getGetCurrentDateString());

            userProfile.getTimerStatus().setWatchViewReward(watchViewReward);
        }
        else {

            WatchViewReward watchViewReward = userProfile.getTimerStatus().getWatchViewReward();

            if(!watchViewReward.isClaimed())
            {
                showBottomSheet();
            }
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

          showInterstitialAdNew(interstitialAdListener());


    }
    public void setUpBannerAd()
    {

        AdRequest adRequest = new AdRequest.Builder().build();
        googleBannerAdView.loadAd(adRequest);

    }

    public  void checkDailyBonus(String currentTime, TimerStatus.DailyBonus dailyBonus) {

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
            // enable the daily bonus
            if(hours>=24)
            {
                showBottomSheet();
                dailyBonus.setClaimed(false);
                dailyBonus.setClaimedDate(DateTimeHelper.getDatePojo().getSimpleDateFormat().format(date1CurrentDate1)+"");
                dailyBonus.setLastResetDateTime(DateTimeHelper.resetDateToATime(date1CurrentDate1,DateTimeHelper.time_7_am));

                Log.d("pTimer","Calender timer " +  DateTimeHelper.getDatePojo().getSimpleDateFormat().format(date1CurrentDate1));
                updateTimerStatus(dailyBonus);


                //     dailyBonus.setLastResetDateTime();
            }


        } catch ( Exception e) {
            Log.d("pTimer","Hello, error ! " + e);

            e.printStackTrace();
        }


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
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(timeChangedReceiver2, filter);


        if (googleBannerAdView != null) {
            googleBannerAdView.resume();

        }


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
        unregisterReceiver(timeChangedReceiver2);
        interstitialAdDismissed = false;
     //   Toast.makeText(this, "On stop", Toast.LENGTH_SHORT).show();


    }




    public void setHeader()
    {



        if(getGoogleSignInAccount()!=null) {

            Log.d("pResonse","google id name"+getGoogleSignInAccount().getDisplayName()+" image "+getGoogleSignInAccount().getPhotoUrl());

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

    public GetUserProfileDataListener getUserProfileDataListener()
    {
        return  new GetUserProfileDataListener() {
            @Override
            public void onTaskSuccessful(UserProfile userProfile) {




                getGamersHubData(new GetGamersHubDataListener() {
                    @Override
                    public void onTaskSuccessful(GamersHubData gamersHubData) {

                        if( !AppHelper.isAppUpdated()) {
                            showUpdate(gamersHubData);
                        }

                    }
                });
                if (userProfile.getTimerStatus() != null) {

                    setTimerStatus(userProfile);


                } else {
                    userProfile.setTimerStatus(createTimerStatus());
                }

                if(userProfile.getUserAccountStatus()==null)
                {
                    UserAccountStatus userAccountStatus = new UserAccountStatus();
                    userAccountStatus.setSuspensionInfo(getString(R.string.suspensionMessage));
                    userProfile.setUserAccountStatus(userAccountStatus);
                }
                else {
                    getUserAccountStatus(userProfile.getUserAccountStatus());
                }

                if (userProfile.getAdViewedStats() == null) {
                    userProfile.setAdViewedStats(new AdViewedStats());
                }

                // update profile data

                UserProfile.ProfileData profileData = userProfile.getProfileData();

                if (profileData != null) {

                    if(profileData.getName()==null|| profileData.getName().trim().isEmpty())
                    {
                        profileData.setName(UserProfile.ProfileData.getProfileData().getName());
                    }
                    if(profileData.getEmail()==null|| profileData.getEmail().trim().isEmpty())
                    {
                        profileData.setEmail(UserProfile.ProfileData.getProfileData().getEmail());
                    }
                    profileData.setVersionName(BuildConfig.VERSION_NAME+"");
                    profileData.setFirebaseFcmToken(AppHelper.getFireBaseFcmToken());
                    profileData.setLastOpened(DateTimeHelper.getDatePojo().getGetCurrentDateString());
                    profileData.setDeviceInfo(DeviceHelper.getDeviceNameAndVersion());
                    if (profileData.getCreatedAt().trim().isEmpty()) {
                        profileData.setCreatedAt(DateTimeHelper.getDatePojo().getGetCurrentDateString());
                    }
                    userProfile.setProfileData(profileData);


                }
                else {
                    UserProfile.ProfileData profileData1 = new UserProfile.ProfileData();
                    userProfile.setProfileData(profileData1);
                }

                setUserProfile(userProfile, new SetUserDataOnCompleteListener() {
                    @Override
                    public void onTaskSuccessful() {

                    }
                });

            }

        };
    }

    public void getUserAccountStatus(UserAccountStatus userAccountStatus)
    {
        if(userAccountStatus.getAccountStatus()!= ConstantsHelper.AccountActive)
        {
            showSuspendDialog(userAccountStatus.getSuspensionMessage());
        }
    }

   public void showUpdate(GamersHubData gamersHubData){
       ConfirmationDialogListener confirmationDialogListener = new ConfirmationDialogListener() {
           @Override
           public void onDismissListener() {

           }

           @Override
           public void onYesClick() {
               openPlayStore();
           }

           @Override
           public void onNoClick() {

           }

           @Override
           public void onRewardGrantedListener() {

           }
       };

        if(gamersHubData.getGamesData().isForceUpdate())
        {
            showConfirmationDialogSingleButton("Update","Pending Update","A new update of the app has been released , please update ",confirmationDialogListener
            );

        }
        else {
            showConfirmationDialog("Pending Update","A new update of the app has been released , please update ",confirmationDialogListener
            );
        }


   }
    private class AsyncTaskExample extends AsyncTask<String, String, String> {
        public static final String TIME_SERVER = "time-a.nist.gov";


        @Override
        protected String doInBackground(String... strings) {

                 NTPUDPClient timeClient = new NTPUDPClient();
               InetAddress inetAddress;

                try {
                    inetAddress = InetAddress.getByName(TIME_SERVER);
                    TimeInfo timeInfo = timeClient.getTime(inetAddress);
                    long returnTime = timeInfo.getReturnTime();
                    Date time = new Date(returnTime);
                    Log.d("ServerResponse"," "+ DateTimeHelper.convertDateToString(time));


                    return  DateTimeHelper.convertDateToString(time);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("ServerResponse","exception "+ e);

                    return  "exception "+e;
                }
            }

        }
    }


