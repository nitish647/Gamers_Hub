package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter.SelectedGameObject;
import static com.nitish.gamershub.Utils.ConstantsHelper.FavouriteList;
import static com.nitish.gamershub.Utils.ConstantsHelper.MainGamesList;
import static com.nitish.gamershub.Utils.ConstantsHelper.NewGamesList;
import static com.nitish.gamershub.Utils.ConstantsHelper.PopularGamesList;
import static com.nitish.gamershub.Utils.ConstantsHelper.gameDataObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.ConstantsHelper;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class CategoryActivity extends BasicActivity {
    ImageView backButton;
    TextView categoryNameTextview;
    RecyclerView categoriesRecycler;
    List<AllGamesItems> categoryGamesList;

    private InterstitialAd interstitialAd;
    AdView googleBannerAdView;
    ArrayList <AllGamesItems> popularGamesList , newGamesList, mainGamesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        Paper.init(this);
        categoriesRecycler =findViewById(R.id.categoriesRecycler);
        categoryNameTextview =findViewById(R.id.categoryNameTextview);
        backButton = findViewById(R.id.backButton);
        categoryGamesList = new ArrayList<>();
        googleBannerAdView = findViewById(R.id.googleBannerAdView);
        popularGamesList = Paper.book().read(PopularGamesList);
        newGamesList = Paper.book().read(NewGamesList);
        mainGamesList = Paper.book().read(MainGamesList);

        categoriesRecycler.setLayoutManager(new GridLayoutManager(this,4));

        setUpBannerAd();
        loadInterstitialAd();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(getIntent()!=null)
        {
            String categoryName  = getIntent().getStringExtra("categoryName");
            categoryNameTextview.setText(categoryName);

            if(categoryName.toLowerCase().contains("best"))
            {
                categoryGamesList = popularGamesList;
            }
            else if(categoryName.toLowerCase().contains("new"))
            {
                categoryGamesList = newGamesList;
            }
            else if(categoryName.toLowerCase().contains("fav"))
            {
                ArrayList<AllGamesItems> favlist2 = Paper.book().read(FavouriteList);
                categoryGamesList = favlist2;
            }


            else {
                for (int i = 0; i < mainGamesList.size(); i++) {
                    if (categoryName.toLowerCase().contains(mainGamesList.get(i).getCategory().toLowerCase())) {
                        categoryGamesList.add(mainGamesList.get(i));
                    }
                }
            }

            NewAndPopularGamesAdapter newAndPopularGamesAdapter = new NewAndPopularGamesAdapter(CategoryActivity.this,categoryGamesList);
            categoriesRecycler.setAdapter(newAndPopularGamesAdapter);


        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_category;
    }

    public void startIntent()
    {
        if(interstitialAd!=null && ConstantsHelper.ShowAds)
        {
            showInterstitial();
        }
        else {

            Intent intent = new Intent(this, GameDetailActivity2.class);
//            intent.putExtra(gameDataObject, SelectedGameObject);
            startActivity(intent);
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
                        CategoryActivity.this.interstitialAd = interstitialAd;
                        Log.i("gInterstitialAd", "onAdLoaded");

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        CategoryActivity.this.interstitialAd = null;


                                        Intent intent = new Intent(CategoryActivity.this, GameDetailActivity2.class);
                                    //    intent.putExtra(gameDataObject, NewAndPopularGamesAdapter.SelectedGameObject);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        CategoryActivity.this.interstitialAd = null;
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
    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.

        if (interstitialAd != null && ConstantsHelper.ShowAds) {
            interstitialAd.show(this);
        }
    }
    public void setUpBannerAd()
    {

        AdRequest adRequest = new AdRequest.Builder().build();
        googleBannerAdView.loadAd(adRequest);

    }

    /** Called when leaving the activity */
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

}