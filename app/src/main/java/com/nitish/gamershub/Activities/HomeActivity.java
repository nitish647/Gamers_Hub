package com.nitish.gamershub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.nitish.gamershub.Adapters.CategoriesAdapter;
import com.nitish.gamershub.Adapters.ViewPagerAdapter2;
import com.nitish.gamershub.Fragments.PopularAndNewFragment;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.Pojo.Categories;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Splash_Screen;

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
        categoriesRecycler = findViewById(R.id.categoriesRecycler);
        categoriesList = new ArrayList<>();
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

        setCategory();

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
}