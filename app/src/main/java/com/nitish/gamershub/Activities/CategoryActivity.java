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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter;
import com.nitish.gamershub.Interface.AdmobInterstitialAdListener;
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
    RelativeLayout noTransactionRelative;
    List<AllGamesItems> categoryGamesList;

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
        noTransactionRelative =findViewById(R.id.noTransactionRelative);

        categoriesRecycler.setLayoutManager(new GridLayoutManager(this,4));

        setUpBannerAd();

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
            if(categoryGamesList.isEmpty())
            {
                noTransactionRelative.setVisibility(View.VISIBLE);
            }
            else {
                noTransactionRelative.setVisibility(View.GONE);
            }


        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_category;
    }

    public void categoryItemClick()
    {
        showInterstitialAdNew(setInterstitialAdListener());
    }

    public void startIntent()
    {

        Intent intent = new Intent(CategoryActivity.this, GameDetailActivity2.class);
        //    intent.putExtra(gameDataObject, NewAndPopularGamesAdapter.SelectedGameObject);
        startActivity(intent);
    }

    public AdmobInterstitialAdListener setInterstitialAdListener()
    {

        return new AdmobInterstitialAdListener() {
            @Override
            public void onAdDismissed() {
                super.onAdDismissed();
                startIntent();
            }

            @Override
            public void onAdShown() {
                super.onAdShown();
            }

            @Override
            public void onAdFailed() {
                super.onAdFailed();
            }

            @Override
            public void onAdLoading() {
                super.onAdLoading();
                startIntent();
            }
        };
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