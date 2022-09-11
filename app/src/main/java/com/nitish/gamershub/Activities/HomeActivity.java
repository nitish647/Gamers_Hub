package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.ConstantsHelper.FavouriteList;
import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHub_ParentCollection;
import static com.nitish.gamershub.Utils.ConstantsHelper.GeneralRewardCoins;
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

import com.android.volley.RequestQueue;
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
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.nitish.gamershub.Adapters.CategoriesAdapter;
import com.nitish.gamershub.Fragments.HomeFragment;
import com.nitish.gamershub.Fragments.ProfileFragment;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.Pojo.Categories;
import com.nitish.gamershub.Pojo.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.ConstantsHelper;
import com.nitish.gamershub.Utils.DeviceHelper;
import com.nitish.gamershub.Utils.NotificationHelper;
import com.nitish.gamershub.Utils.ProgressBarHelper;
import com.nitish.gamershub.Utils.UserOperations;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    private FirebaseAuth mAuth;

    FirebaseFirestore firestoreDb;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Paper.init(this);
        mAuth = FirebaseAuth.getInstance();
        firestoreDb = FirebaseFirestore.getInstance();

        setViews();
        categoriesList = new ArrayList<>();
        navigationView.setVisibility(View.VISIBLE);

        FirebaseApp.initializeApp(HomeActivity.this);
        NotificationHelper.generateFcmToken(HomeActivity.this);
        setHeader();
        updateUserInfo();

        loadInterstitialAd2(new AdDismissedListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(HomeActivity.this, GameDetailActivity2.class);
                //    intent.putExtra(gameDataObject, SelectedGameObject);
                startActivity(intent);
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


        loadRewardedAd();
        setUpBannerAd();

   //     AdsHelper.loadInterstitialAd(this);

        setCategory();




        try {
            masterDataJsonObject = new JSONObject(Paper.book().read(Splash_Screen.MaterData)+"");
           JSONArray mainGameJsonArray = masterDataJsonObject.getJSONArray("main");
            JSONArray popularGamesJsonArray = masterDataJsonObject.getJSONArray("best");
            JSONArray newGamesJsonArray =  masterDataJsonObject.getJSONArray("new");
     //       setUpViewPager(allGamesJsonArrayString,popularGamesJsonArrayString,newGamesJsonArrayString);

        } catch (Exception e) {
            Toast.makeText(this, "Some error has occurred : gError223", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
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

        firestoreDb.collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail)+"").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {

                        UserProfile  userProfile=   documentSnapshot.toObject(UserProfile.class);

                        UserProfile.ProfileData profileData = userProfile.profileData;

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDateTime = dateFormat.format(new Date()); // Find todays date
                        profileData.setLastOpened(currentDateTime);
                        profileData.setDeviceInfo(DeviceHelper.getDeviceNameAndVersion());
                        if(profileData.getCreatedAt().trim().isEmpty())
                        {
                            profileData.setCreatedAt(currentDateTime);
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

    public void getGamersHubData()
    {

        firestoreDb.collection("Gamers Hub Data").document("rewardCoins"+"").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {

                        HashMap<String,Object> hashMap=   (HashMap<String, Object>) documentSnapshot.getData();

                        HashMap<String,Object> rewardCoinsHashmap=   (HashMap<String, Object>) hashMap.get("rewardCoinsList");

                         Object generalCoins =  rewardCoinsHashmap.get(rewardCoinsHashmap.keySet().toArray()[0]);

                         int intGeneralCoins  = Integer.parseInt( generalCoins+"");
                        Paper.book().write(GeneralRewardCoins,generalCoins);


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

    public void setUpViewPager(String allGamesListJsonArray, String popularGamesJsonArray, String newGamesJsonArray)
    {
        //sending the titles in bundle
        Bundle bundle = new Bundle();
        Bundle bundle2 = new Bundle();
     //   Bundle bundle3 = new Bundle();

        bundle.putString("data", allGamesListJsonArray);
        bundle.putString("title","All Games");
       bundle.putString("masterData",Paper.book().read(Splash_Screen.MaterData)+"");

        bundle2.putString("data", newGamesJsonArray);
        bundle2.putString("title","Favourites");
      bundle2.putString("masterData",Paper.book().read(Splash_Screen.MaterData)+"");

//
//        bundle3.putString("data", newGamesJsonArray);
//        bundle3.putString("title","Favourites");
//       bundle3.putString("masterData",Paper.book().read(Splash_Screen.MaterData)+"");
//

        // when there is no fragment added in viewpager adapter then only add the fragment


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

                dialogInterface.dismiss();

                progressDialog.show();

                AuthUI.getInstance()
                        .signOut(HomeActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                progressDialog.dismiss();
                                startActivity(new Intent(HomeActivity.this,LoginPage.class));
                                finish();

                            }
                        });


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

        if(mAuth!=null &&  mAuth.getCurrentUser()!=null) {


            profileName.setText(mAuth.getCurrentUser().getDisplayName());
            Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).into(profileIcon);
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
    public void copyCollection()
    {

        firestoreDb.collection(GamersHub_ParentCollection).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                 List<DocumentSnapshot> documentSnapshotList =  task.getResult().getDocuments();

                 firestoreDb.collection(GamersHub_ParentCollection+"2");
                }
            }
        });


    }
}