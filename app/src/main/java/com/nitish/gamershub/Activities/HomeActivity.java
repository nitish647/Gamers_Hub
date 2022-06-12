package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter.SelectedGameObject;
import static com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter.gameDataObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.nitish.gamershub.Adapters.CategoriesAdapter;
import com.nitish.gamershub.Adapters.ViewPagerAdapter2;
import com.nitish.gamershub.Fragments.PopularAndNewFragment;
import com.nitish.gamershub.Helper_class;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.Pojo.Categories;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.ConstantsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    // the whole game data
    JSONArray masterDataJsonArray ;

    public static  String FavouriteList = "FavouriteList";
    public static  String MainGamesList = "MainGamesList";
    public static  String NewGamesList = "NewGamesList";
    public static  String PopularGamesList = "PopularGamesList";
    int currentSelectedFragPosition=0;
    static private TabLayout tabLayout;
    static private ViewPager viewPager;
    ViewPagerAdapter2 viewPagerAdapter;
    SearchView searchView;
    RequestQueue requestQueue;
    List<Categories> categoriesList;
    RecyclerView categoriesRecycler;
    Button navigationButton;

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    LinearLayout linearAdContainer;

    AdView googleBannerAdView;
    boolean interstitialAdDismissed = false;

    Button removeAdsButton;
    private InterstitialAd interstitialAd;

    private RewardedAd rewardedAd;
    boolean isLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Paper.init(this);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        searchView = findViewById(R.id.searchView);
        tabLayout.setupWithViewPager(viewPager);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationButton =findViewById(R.id.navigationButton);
        removeAdsButton =findViewById(R.id.removeAdsButton);
        categoriesRecycler = findViewById(R.id.categoriesRecycler);
        googleBannerAdView = findViewById(R.id.googleBannerAdView);
        categoriesList = new ArrayList<>();
        navigationView.setVisibility(View.VISIBLE);
        viewPagerAdapter = new ViewPagerAdapter2(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }

        });

        removeAdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ConstantsHelper.ShowAds==false)
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



        loadRewardedAd();
        setUpBannerAd();
      loadInterstitialAd();
   //     AdsHelper.loadInterstitialAd(this);

        setCategory();
        setSearchView();

        // just writing an empty favourite list to avoid null pointer when reading the data

        if(!Paper.book().contains(FavouriteList)) {
            ArrayList<AllGamesItems> favouriteArrayList = new ArrayList<>();
            Paper.book().write(FavouriteList, favouriteArrayList);
        }

        try {
            masterDataJsonArray = new JSONArray(Paper.book().read(Splash_Screen.MaterData)+"");

            JSONArray mainGameJsonArray = masterDataJsonArray.getJSONObject(0).getJSONArray("main");
            JSONArray popularGamesJsonArray = masterDataJsonArray.getJSONObject(0).getJSONArray("best");
            JSONArray newGamesJsonArray =  masterDataJsonArray.getJSONObject(0).getJSONArray("new");
            String allGamesJsonArrayString = mainGameJsonArray.toString();
            String popularGamesJsonArrayString = popularGamesJsonArray.toString();
            String newGamesJsonArrayString = newGamesJsonArray.toString();


            // will save the all games in paper in arraylist form
            saveAllGamesData(mainGameJsonArray,popularGamesJsonArray,newGamesJsonArray);

            setUpViewPager(allGamesJsonArrayString,popularGamesJsonArrayString,newGamesJsonArrayString);


        } catch (JSONException e) {
            e.printStackTrace();
        }


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

//            viewPagerAdapter.addFragment(new AllGamesFragment(), "All Games");
            viewPagerAdapter.addFragment(new PopularAndNewFragment(), "All Games");
            viewPagerAdapter.addFragment(new PopularAndNewFragment(), "Favourites");


        //passing the json data to the fragments
        viewPagerAdapter.getItem(0).setArguments(bundle);
        viewPagerAdapter.getItem(1).setArguments(bundle2);

        viewPager.setAdapter(viewPagerAdapter);




        viewPager.setCurrentItem(currentSelectedFragPosition);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                currentSelectedFragPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
    public void setCategory()
    {
        //category images

       // categoriesList.add(new Categories(R.drawable.fav_on2,"","Favourites"));
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
            intent.putExtra(gameDataObject, SelectedGameObject);
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
                if(viewPagerAdapter!=null&&viewPagerAdapter.getCount()==2)
                {
                    // searching in all games
                    ((PopularAndNewFragment)viewPagerAdapter.getItem(0)).setSetSearchFilter(newText);
                    // searching from favourites
                    ((PopularAndNewFragment)viewPagerAdapter.getItem(1)).setSetSearchFilter(newText);
                }
                return false;
            }
        });
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

            Paper.book().write(HomeActivity.MainGamesList,mainItemsArrayList);

            for(int i = 0; i< newGamesJsonArray.length(); i++)
            {
                JSONObject jsonObject = newGamesJsonArray.getJSONObject(i);
                AllGamesItems allGamesItems = gson.fromJson(jsonObject.toString(),AllGamesItems.class);
                newGamesItemsArrayList.add(allGamesItems);
            }
            Paper.book().write(HomeActivity.NewGamesList,newGamesItemsArrayList);


            for(int i = 0; i< popularGamesJsonArray.length(); i++)
            {
                JSONObject jsonObject = popularGamesJsonArray.getJSONObject(i);
                AllGamesItems allGamesItems = gson.fromJson(jsonObject.toString(),AllGamesItems.class);
                popularItemsArrayList.add(allGamesItems);
            }
            Paper.book().write(HomeActivity.PopularGamesList,popularItemsArrayList);

        } catch (JSONException e) {
            Toast.makeText(HomeActivity.this, "something went wrong while showing this data : error334", Toast.LENGTH_SHORT).show();
            Log.d("gError334",e.toString());
            e.printStackTrace();
        }
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
                                        intent.putExtra(gameDataObject, SelectedGameObject);
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
}