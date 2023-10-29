package com.nitish.gamershub.view.homePage.activity;

//import static com.nitish.gamershub.view.homePage.adapter.NewAndPopularGamesAdapter.SelectedGameObject;
import static com.nitish.gamershub.utils.AppConstants.FavouriteList;
import static com.nitish.gamershub.utils.AppConstants.IntentData;
import static com.nitish.gamershub.utils.AppConstants.MainGamesList;
import static com.nitish.gamershub.utils.AppConstants.NewGamesList;
import static com.nitish.gamershub.utils.AppConstants.PopularGamesList;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.nitish.gamershub.view.gamePlay.GameDetailActivity2;
import com.nitish.gamershub.view.homePage.adapter.NewAndPopularGamesAdapter;
import com.nitish.gamershub.utils.adsUtils.AdmobInterstitialAdListener;
import com.nitish.gamershub.model.local.AllGamesItems;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.ActivityCategoryBinding;
import com.nitish.gamershub.view.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class CategoryActivity extends BaseActivity {

    ActivityCategoryBinding categoryBinding;

    List<AllGamesItems> categoryGamesList;
    NewAndPopularGamesAdapter newAndPopularGamesAdapter;
    ArrayList <AllGamesItems> popularGamesList , newGamesList, mainGamesList;
    private AllGamesItems mAllGamesItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryBinding = DataBindingUtil.setContentView(this, getLayoutResourceId());

        Paper.init(this);
        categoryGamesList = new ArrayList<>();
        popularGamesList = Paper.book().read(PopularGamesList);
        newGamesList = Paper.book().read(NewGamesList);
        mainGamesList = Paper.book().read(MainGamesList);

        categoryBinding.categoriesRecycler.setLayoutManager(new GridLayoutManager(this,4));

        setUpBannerAd();

        categoryBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(getIntent()!=null)
        {
            getCategoryGamesList();

            newAndPopularGamesAdapter = new NewAndPopularGamesAdapter(CategoryActivity.this, categoryGamesList, new NewAndPopularGamesAdapter.NewAndPopularGameAdapterInterface() {
                @Override
                public void onClick(AllGamesItems allGamesItems) {
                    mAllGamesItems = allGamesItems;
                    categoryItemClick();
                }
            });

            categoryBinding.categoriesRecycler.setAdapter(newAndPopularGamesAdapter);
            if(categoryGamesList.isEmpty())
            {
                categoryBinding.noTransactionRelative.setVisibility(View.VISIBLE);
            }
            else {
                categoryBinding.noTransactionRelative.setVisibility(View.GONE);
            }


        }
    }

    private void getCategoryGamesList() {
        String categoryName  = getIntent().getStringExtra("categoryName");
        categoryBinding.categoryNameTextview.setText(categoryName);

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
        intent.putExtra(IntentData, mAllGamesItems);
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
        categoryBinding.googleBannerAdView.loadAd(adRequest);

    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (categoryBinding.googleBannerAdView != null) {
            categoryBinding.googleBannerAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (categoryBinding.googleBannerAdView != null) {
            categoryBinding.googleBannerAdView.resume();
        }

        getCategoryGamesList();
        newAndPopularGamesAdapter.changedNewAndPopularGamesList(categoryGamesList);
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (categoryBinding.googleBannerAdView != null) {
            categoryBinding.googleBannerAdView.destroy();
        }
        super.onDestroy();
    }

}