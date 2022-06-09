package com.nitish.gamershub.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.R;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class CategoryActivity extends AppCompatActivity {
    ImageView backButton;
    TextView categoryNameTextview;
    RecyclerView categoriesRecycler;
    List<AllGamesItems> categoryGamesList;

    AdView googleBannerAdView;
    ArrayList <AllGamesItems> popularGamesList , newGamesList, mainGamesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Paper.init(this);
        categoriesRecycler =findViewById(R.id.categoriesRecycler);
        categoryNameTextview =findViewById(R.id.categoryNameTextview);
        backButton = findViewById(R.id.backButton);
        categoryGamesList = new ArrayList<>();
        googleBannerAdView = findViewById(R.id.googleBannerAdView);
        popularGamesList = Paper.book().read(HomeActivity.PopularGamesList);
        newGamesList = Paper.book().read(HomeActivity.NewGamesList);
        mainGamesList = Paper.book().read(HomeActivity.MainGamesList);

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