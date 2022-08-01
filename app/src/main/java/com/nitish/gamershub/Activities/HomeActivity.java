package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Activities.LoginPage.GamersHub_ParentCollection;
import static com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter.SelectedGameObject;
import static com.nitish.gamershub.Utils.ConstantsHelper.FavouriteList;
import static com.nitish.gamershub.Utils.ConstantsHelper.MainGamesList;
import static com.nitish.gamershub.Utils.ConstantsHelper.NewGamesList;
import static com.nitish.gamershub.Utils.ConstantsHelper.PopularGamesList;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserMail;
import static com.nitish.gamershub.Utils.ConstantsHelper.gameDataObject;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.nitish.gamershub.Adapters.CategoriesAdapter;
import com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.Pojo.Categories;
import com.nitish.gamershub.Pojo.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.ConstantsHelper;
import com.nitish.gamershub.Utils.NotificationHelper;
import com.nitish.gamershub.Utils.ProgressBarHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    // the whole game data
    JSONObject masterDataJsonObject;


    int currentSelectedFragPosition=0;



    SearchView searchView;
    RequestQueue requestQueue;
    List<Categories> categoriesList;
    RecyclerView categoriesRecycler;
    Button navigationButton;

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    LinearLayout linearAdContainer;

    NewAndPopularGamesAdapter newAndPopularGamesAdapter;
    AdView googleBannerAdView;
    boolean interstitialAdDismissed = false;

    TextView totalPointsTextview;
    Button removeAdsButton;
    private InterstitialAd interstitialAd;

    Button logoutButton;

    private RewardedAd rewardedAd;
    boolean isLoading;
    RecyclerView allGamesRecyclerView;
    ArrayList<AllGamesItems> mainGamesArrayList;
    ImageSlider imageSlider;
    ProgressDialog progressDialog;


    // firebase auth
    private FirebaseAuth mAuth;

    FirebaseFirestore firestoreDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Paper.init(this);
        requestQueue = Volley.newRequestQueue(this);
        allGamesRecyclerView = findViewById(R.id.allGamesRecyclerView);
        totalPointsTextview = findViewById(R.id.totalPointsTextview);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        firestoreDb = FirebaseFirestore.getInstance();
         logoutButton= findViewById(R.id.logoutButton);
        searchView = findViewById(R.id.searchView);
        progressDialog = ProgressBarHelper.setProgressBarDialog(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        imageSlider = findViewById(R.id.imageSlider);
        navigationView = findViewById(R.id.navigationView);
        navigationButton =findViewById(R.id.navigationButton);
        removeAdsButton =findViewById(R.id.removeAdsButton);
        categoriesRecycler = findViewById(R.id.categoriesRecycler);
        googleBannerAdView = findViewById(R.id.googleBannerAdView);
        categoriesList = new ArrayList<>();
        mainGamesArrayList = new ArrayList<>();
        navigationView.setVisibility(View.VISIBLE);

        FirebaseApp.initializeApp(HomeActivity.this);
        NotificationHelper.generateFcmToken(HomeActivity.this);
        getUserData();


        // firebase login


        // Choose authentication providers




        setHeader();
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
        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }

        });

        // just writing an empty favourite list to avoid null pointer when reading the data

        if(!Paper.book().contains(FavouriteList)||Paper.book().read(FavouriteList)==null) {
            ArrayList<AllGamesItems> favouriteArrayList = new ArrayList<>();
            Paper.book().write(FavouriteList, favouriteArrayList);
        }

        removeAdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!ConstantsHelper.ShowAds)
                {
                    Toast.makeText(HomeActivity.this, "Ads are already disabled", Toast.LENGTH_SHORT).show();
                    removeAdsButton.setText("Ads Disabled");
                }
                else {

                    removeAdsButton.setText("Remove ads (Free)");
                    showRewardedVideo();
                }
            }
        });




        bannerImagesApi();
        loadRewardedAd();
        setUpBannerAd();
      loadInterstitialAd();
   //     AdsHelper.loadInterstitialAd(this);

        setCategory();
        setSearchView();


        GridLayoutManager gridLayoutManager = new GridLayoutManager(HomeActivity.this,4);
        allGamesRecyclerView.setLayoutManager(gridLayoutManager);


        try {
            masterDataJsonObject = new JSONObject(Paper.book().read(Splash_Screen.MaterData)+"");
           JSONArray mainGameJsonArray = masterDataJsonObject.getJSONArray("main");
            JSONArray popularGamesJsonArray = masterDataJsonObject.getJSONArray("best");
            JSONArray newGamesJsonArray =  masterDataJsonObject.getJSONArray("new");


          // will save the all games in paper in arraylist form
            saveAllGamesData(mainGameJsonArray,popularGamesJsonArray,newGamesJsonArray);








     //       setUpViewPager(allGamesJsonArrayString,popularGamesJsonArrayString,newGamesJsonArrayString);


        } catch (Exception e) {
            Toast.makeText(this, "Some error has occurred : gError223", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }





    public void getUserData()
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

                        totalPointsTextview.setText(profileData.gamePoints+"");
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
           showInterstitial();
        }
        else {

            Intent intent = new Intent(HomeActivity.this, GameDetailActivity2.class);
        //    intent.putExtra(gameDataObject, SelectedGameObject);
            startActivity(intent);
        }
    }
    // post method
    public void parseAllGamesData( JSONArray allGamesJsonArray)
    {


        try {
            JSONObject masterObject =  allGamesJsonArray.getJSONObject(0);

            JSONArray mainObject  = masterObject.getJSONArray("main");




        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
    public void setSearchView()
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                    // searching in all games
                    newAndPopularGamesAdapter.searchFilter.filter(newText);
                    // searching from favourites

                return false;
            }
        });
    }
    public void setImageSlider(JSONArray bannerImagesJsonArray) throws JSONException {

        ArrayList<String> imageUrlList = new ArrayList<>();
        for(int i =0;i<bannerImagesJsonArray.length();i++)
        {
            JSONObject jsonObject = bannerImagesJsonArray.getJSONObject(i);

            imageUrlList.add(jsonObject.getString("largeImageUrl"));

        }
        ArrayList<SlideModel> slideModelArrayList = new ArrayList<>();
        for(String imageUri : imageUrlList)
        {
            SlideModel slideModel = new SlideModel(imageUri,null,null);
            slideModelArrayList.add(slideModel);

        }
        imageSlider.setImageList(slideModelArrayList, ScaleTypes.FIT);
    }
    public void showSnackBar(String message , ViewGroup viewGroup)
    {

        Snackbar snackbar = Snackbar.make( viewGroup  ,""+message, Snackbar.LENGTH_LONG);

        snackbar.setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.material_dynamic_tertiary99));
        snackbar.show();
    }
    // will set the main games data
    public void saveAllGamesData(JSONArray mainGamesArray, JSONArray newGamesJsonArray, JSONArray popularGamesJsonArray)
    {
        ArrayList<AllGamesItems> mainItemsArrayList = new ArrayList<>();
        ArrayList<AllGamesItems> newGamesItemsArrayList = new ArrayList<>();
        ArrayList<AllGamesItems> popularItemsArrayList = new ArrayList<>();
        Gson gson = new Gson();
        try {
            for(int i = 0; i< mainGamesArray.length(); i++)
            {
                JSONObject jsonObject = mainGamesArray.getJSONObject(i);
                AllGamesItems allGamesItems = gson.fromJson(jsonObject.toString(),AllGamesItems.class);
                mainItemsArrayList.add(allGamesItems);
            }

            newAndPopularGamesAdapter = new NewAndPopularGamesAdapter(HomeActivity.this,mainItemsArrayList);
            allGamesRecyclerView.setAdapter(newAndPopularGamesAdapter);

            Paper.book().write(MainGamesList,mainItemsArrayList);

            for(int i = 0; i< newGamesJsonArray.length(); i++)
            {
                JSONObject jsonObject = newGamesJsonArray.getJSONObject(i);
                AllGamesItems allGamesItems = gson.fromJson(jsonObject.toString(),AllGamesItems.class);
                newGamesItemsArrayList.add(allGamesItems);
            }
            Paper.book().write(NewGamesList,newGamesItemsArrayList);


            for(int i = 0; i< popularGamesJsonArray.length(); i++)
            {
                JSONObject jsonObject = popularGamesJsonArray.getJSONObject(i);
                AllGamesItems allGamesItems = gson.fromJson(jsonObject.toString(),AllGamesItems.class);
                popularItemsArrayList.add(allGamesItems);
            }
            Paper.book().write(PopularGamesList,popularItemsArrayList);

            if(popularGamesJsonArray.length()>0)
            Paper.book().read(PopularGamesList,popularItemsArrayList.get(0).getName());
        } catch (Exception e) {
            Toast.makeText(HomeActivity.this, "something went wrong while showing this data : error334 "+e, Toast.LENGTH_SHORT).show();
            Log.d("gError334",e.toString());
            e.printStackTrace();
        }
    }

    public void bannerImagesApi()
    {

        String url = getString(R.string.dbGitUrl)+"gamers_hub_data/banner_Images.json";

        Log.d("url",url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {

                Log.d("gResponse","response banner_Images data "+response);

                //    Toast.makeText(Splash_Screen.this, response+"", Toast.LENGTH_SHORT).show();
                try {
                    JSONArray bannerImagesJsonArray =  response.getJSONArray("bannerImages");
                    setImageSlider(bannerImagesJsonArray);

                } catch (Exception e) {
                    Log.d("gError","error in  banner_Images data "+e.toString());
                    e.printStackTrace();
                }







            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("gError","error in banner_Images data "+error);

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }
    public void loadInterstitialAd() {


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
                        HomeActivity.this.interstitialAd = interstitialAd;
                        Log.i("gInterstitialAd", "onAdLoaded");

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        HomeActivity.this.interstitialAd = null;


                                        Intent intent = new Intent(HomeActivity.this, GameDetailActivity2.class);
                                    //    intent.putExtra(gameDataObject, SelectedGameObject);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        HomeActivity.this.interstitialAd = null;
                                        Log.d("gInterstitialAd", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
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
    public void setUpBannerAd()
    {

        AdRequest adRequest = new AdRequest.Builder().build();
        googleBannerAdView.loadAd(adRequest);

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
        // load the ad again
        if(interstitialAd==null)
            loadInterstitialAd();


    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (googleBannerAdView != null) {
            googleBannerAdView.destroy();
        }
        super.onDestroy();
    }
    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.

        if (interstitialAd != null && ConstantsHelper.ShowAds) {
            interstitialAd.show(this);
        }
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
                        removeAdsButton.setText("Ads Disabled ");
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
}