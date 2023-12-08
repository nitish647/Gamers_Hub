package com.nitish.gamershub.view.homePage.activity;

//import static com.nitish.gamershub.view.homePage.adapter.NewAndPopularGamesAdapter.SelectedGameObject;

import static com.nitish.gamershub.utils.AppConstants.IntentData;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.nitish.gamershub.model.gamersHubMaterData.AllGamesResponseItem;
import com.nitish.gamershub.model.local.CategoryItem;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.utils.AppConstants;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.view.dialogs.DialogListener;
import com.nitish.gamershub.view.gamePlay.GameDetailActivity2;
import com.nitish.gamershub.view.homePage.adapter.GameListAdapter;
import com.nitish.gamershub.utils.adsUtils.AdmobInterstitialAdListener;
import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.ActivityCategoryBinding;
import com.nitish.gamershub.view.base.BaseActivity;
import com.nitish.gamershub.view.homePage.fragment.CategoryGamesFragment;
import com.nitish.gamershub.view.homePage.fragment.HomeFragment;

import java.util.ArrayList;

import io.paperdb.Paper;

public class CategoryActivity extends BaseActivity {

    ActivityCategoryBinding binding;

    ArrayList<GamesItems> categoryGamesList = new ArrayList<>();
    CategoryItem categoryItem;
    GameListAdapter gameListAdapter;
    ArrayList<GamesItems> popularGamesList, newGamesList, mainGamesList;
    private GamesItems mGamesItems;

    String titleText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutResourceId());

        Paper.init(this);

        AllGamesResponseItem allGamesResponseItem = getPreferencesMain().getAllGamesResponseMaterData();

        popularGamesList = new ArrayList<>(allGamesResponseItem.getBestGamesList());// Paper.book().read(PopularGamesList);
        newGamesList = new ArrayList<>(allGamesResponseItem.getNewGamesList());   //Paper.book().read(NewGamesList);
        mainGamesList = new ArrayList<>(allGamesResponseItem.getMainGamesList()); //Paper.book().read(MainGamesList);

        handleIntent();

        initViews();

        setOnClickListeners();


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_category;
    }

    private void setSearchView() {

        binding.searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                gameListAdapter.searchFilter.filter(s.toString());
                if (s.toString().length() > 0) {
                    binding.crossButton.setVisibility(View.VISIBLE);
                } else {
                    binding.crossButton.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void handleIntent() {

        String from = getIntent().getStringExtra(AppConstants.From);
        if (!AppHelper.isNullOrEmpty(from)) {

            if (from.equals(CategoryGamesFragment.Tag) || from.equals(HomeFragment.Tag)) {

                Bundle bundle = getIntent().getBundleExtra(IntentData);
                {
                    categoryItem = (CategoryItem) bundle.getSerializable(AppConstants.BundleData);

                    if (categoryItem != null) {
                        categoryGamesList = categoryItem.getCategoryGameList();
                        titleText = categoryItem.getName();
                    }


                }
            }


        }

    }


    private void initViews() {


        binding.categoryNameTextview.setText(titleText);
        setUpRecyclerview();
        setSearchView();
        showClearListButton();
        setUpBannerAd();
    }

    private void setOnClickListeners() {

        binding.clearListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearListDialog();
            }
        });
        binding.crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchEdittext.getText().clear();
            }
        });
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void setUpRecyclerview() {
        gameListAdapter = new GameListAdapter(CategoryActivity.this, categoryGamesList, new GameListAdapter.GameListAdapterInterface() {
            @Override
            public void onClick(GamesItems gamesItems) {
                mGamesItems = gamesItems;
                categoryItemClick();
            }
        });

        binding.recyclerView.setAdapter(gameListAdapter);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));


    }

    private void showClearListDialog() {

        DialogItems dialogItems = new DialogItems();
        dialogItems.setMessage(getString(R.string.clear_list_warning));
        dialogItems.setTitle(getString(R.string.warning));

        showConfirmationDialog2(dialogItems, new DialogListener() {
            @Override
            public void onYesClick() {
                clearGamesList();
            }

            @Override
            public void onNoClick() {

            }
        });


    }

    private void clearGamesList() {
        if (categoryItem.getName().equals(getString(R.string.favourites))) {
            getPreferencesMain().saveFavouriteList(new ArrayList<>());
        }
        if (categoryItem.getName().equals(getString(R.string.recently_played))) {
            getPreferencesMain().saveRecentlyPlayedList(new ArrayList<>());
        }

        categoryGamesList.clear();
        gameListAdapter.notifyDataSetChanged();

        binding.clearListButton.setVisibility(View.GONE);
        binding.noCategoryRelative.setVisibility(View.VISIBLE);

    }

    private void showClearListButton() {

        String categoryName = categoryItem.getName();
        if (categoryName.equals(getString(R.string.favourites)) || categoryName.equals(getString(R.string.recently_played))) {
            binding.clearListButton.setVisibility(View.VISIBLE);

        } else {
            binding.clearListButton.setVisibility(View.GONE);

        }

    }

    private void getCategoryGamesList() {

        String categoryName = getIntent().getStringExtra(AppConstants.IntentData);
        if (categoryName == null) {
            categoryName = "";
        }

        binding.categoryNameTextview.setText(categoryName);

        if (categoryName.toLowerCase().contains("best")) {
            categoryGamesList = popularGamesList;
        } else if (categoryName.toLowerCase().contains("new")) {
            categoryGamesList = newGamesList;
        } else if (categoryName.toLowerCase().contains("fav")) {

            categoryGamesList = getPreferencesMain().getSavedFavouriteList();

            if (categoryGamesList == null) {
                categoryGamesList = new ArrayList<>();
            }
            if (categoryGamesList.isEmpty()) {
                binding.noCategoryRelative.setVisibility(View.VISIBLE);
            } else {
                binding.noCategoryRelative.setVisibility(View.GONE);
            }


        } else {
            for (int i = 0; i < mainGamesList.size(); i++) {
                if (categoryName.toLowerCase().contains(mainGamesList.get(i).getCategory().toLowerCase())) {
                    categoryGamesList.add(mainGamesList.get(i));
                }
            }
        }

        gameListAdapter.updateGamesList(categoryGamesList);
    }


    public void categoryItemClick() {
        showInterstitialAdNew(setInterstitialAdListener());
    }

    public void startIntent() {

        Intent intent = new Intent(CategoryActivity.this, GameDetailActivity2.class);
        intent.putExtra(IntentData, mGamesItems);
        startActivity(intent);
    }

    public AdmobInterstitialAdListener setInterstitialAdListener() {

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

    public void setUpBannerAd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.googleBannerAdView.loadAd(adRequest);

    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {

        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();


    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {

        super.onDestroy();
    }

}