package com.nitish.gamershub;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.annotations.NotNull;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList game_name;
    ArrayList game_link;
    ArrayList game_img;
    public static String ads_state = "true";
    public RewardedVideoAd fb_rewardedVideoAd;
    SharedPreferences sh;
    SharedPreferences.Editor editor;
    JSONArray jsonArray_new;
    RecyclerviewAdapter recyclerviewAdapter;
    ArrayList cat_img, cat_title, info_title, info_img;
    Button navigation_btn;
    public static Context main_context;
    ActionBarDrawerToggle actionBarDrawerToggle;
    RecyclerView recyclerView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ToggleButton toggleButton;
    HashMap<String, HashMap> hashMap;
    RewardedAdLoadCallback adLoadCallback;
    JSONArray jsonArray, jsonArray2, jsonArray_best;
    String data;
    Button remove_ads;
    GridView gridView, gridView_info;
    SearchView searchView;
    private RewardedAd admob_rewardedAd;
    private AdView main_banner;

    public static void refresh() {
        new MainActivity().recreate();
        // new MainActivity().startActivity(new MainActivity().getIntent());
    }

    //ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sh = getApplicationContext().getSharedPreferences("json_data", Context.MODE_PRIVATE);
        editor = sh.edit();
        searchView = findViewById(R.id.main_searchview);

        navigationView = findViewById(R.id.navigation_view);

        drawerLayout = findViewById(R.id.drawer);
        main_context = getApplicationContext();
        //recieving data from splash
        Intent intent = getIntent();
        data = intent.getStringExtra("json");
        setJsonArray_new();
        editor.putString("json", jsonArray2.toString());
        editor.putString("json_parent", data);
        editor.putString("json_best", jsonArray_best.toString());

        editor.putString("json_new", jsonArray_best.toString());
        editor.apply();
        set_btn();
        Set_array_list();
        setListView();
        recyclerView = findViewById(R.id.main_recycler);
        jsonArray = new JSONArray();

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization


//google ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        main_banner = findViewById(R.id.main_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        main_banner.loadAd(adRequest);

        //admob reward
//        admob_rewardedAd = new RewardedAd(this,
//                getResources().getString(R.string.admob_reward));
       // loadAdmob_rewardedAd();

        //facebook ads
        AudienceNetworkAds.initialize(this);
        fb_rewardedVideoAd = new RewardedVideoAd(MainActivity.this,
                getResources().getString(R.string.fb_reward_nitish));
        load_fb_reward_ads();

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_Toast("opened");
            }
        });

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ColorDrawable colorDrawable = null;

//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F0740D")));

        try {
            jsonlist("Furious Road", R.drawable.furious_road_shotoss, "https://m.shtoss.com/game/furious-road/index.html");

        } catch (JSONException e) {
            e.printStackTrace();
        }
//main recycler
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerviewAdapter = new RecyclerviewAdapter("main", this, game_name, game_link, game_img, jsonArray2);

        recyclerView.setAdapter(recyclerviewAdapter);

        recyclerView.setLayoutManager(gridLayoutManager);

        //best recycler
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(this, 1);
        recyclerviewAdapter = new RecyclerviewAdapter("main", this, game_name, game_link, game_img, jsonArray_best);
        //set animation
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_anim_fall_down);

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_anim_fall_down);

        recyclerView.setLayoutAnimation(animation);

        recyclerView.scheduleLayoutAnimation();
        recyclerView.animate();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isScrolling = false;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 1) {

                    this.isScrolling = true;
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentItem = gridLayoutManager.getChildCount();
                int totlItem = gridLayoutManager.getItemCount();

                int scrolledOutItem = gridLayoutManager.findFirstVisibleItemPosition();
                if (currentItem + scrolledOutItem == totlItem) {


                    this.isScrolling = false;
                    // jsonParse();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item);
    }


//    @Override
//    protected void onRestart() {
//
//
//        finish();
//        overridePendingTransition( 0, 0);
//        startActivity(getIntent());
//        overridePendingTransition( 0, 0);
//
//        super.onRestart();
//    }

    public void Set_array_list() {
        game_link = new ArrayList();
        game_name = new ArrayList();
        game_img = new ArrayList();
        cat_img = new ArrayList();
        cat_title = new ArrayList();
        info_img = new ArrayList();
        info_title = new ArrayList();
        game_name.add("Color Shape");
        game_name.add("Perfect Piano");
        game_name.add("Color Shape");
        game_name.add("Perfect Piano");
        game_name.add("Color Shape");
        game_name.add("Perfect Piano");
        game_name.add("Color Shape");
        game_name.add("Perfect Piano");
        game_link.add("https://play.famobi.com/color-shape");
        game_link.add("https://play.famobi.com/perfect-piano");


        //category images
        cat_img.add(R.drawable.fav_on2);
        cat_img.add(R.drawable.new_icon1);
        cat_img.add(R.drawable.best_games);
        cat_img.add(R.drawable.action_icon1);
        cat_img.add(R.drawable.arcade_icon1);
        cat_img.add(R.drawable.shooter_icon1);
        cat_img.add(R.drawable.puzzle_icon1);
        cat_img.add(R.drawable.board_icon1);
        cat_img.add(R.drawable.racing_icon1);
        cat_img.add(R.drawable.strategy_icon1);

        //category title
        cat_title.add("Favourites");
        cat_title.add("New");
        cat_title.add("Best");
        cat_title.add("Action");
        cat_title.add("Arcade");
        cat_title.add("Shooting");
        cat_title.add("Puzzle");
        cat_title.add("Board");
        cat_title.add("Racing");
        cat_title.add("Strategy");

        //info title
        info_title.add("More apps");
        info_title.add("Privacy Policy");
        info_title.add("About us");
        info_title.add("Share");

        //info_img
        info_img.add(R.drawable.more_apps);
        info_img.add(R.drawable.priv_policy);
        info_img.add(R.drawable.about_us);
        info_img.add(R.drawable.share_1);
//        game_img.add("https://img.cdn.famobi.com/portal/html5games/images/tmp/ColorShapeTeaser.jpg");
//        game_img.add("https://img.cdn.famobi.com/portal/html5games/images/tmp/PerfectPiano_Teaser.jpg");

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

//        if (id == R.id.btn_clear_pref) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                deleteSharedPreferences(RecyclerviewAdapter.sharedpref_fav);
//                show_Toast("deleted");
//            } else
//                show_Toast("error");
//            drawerLayout.closeDrawer(Gravity.LEFT);
//        }


        if (id == R.id.remove_ads) {
            if (ads_state.contains("true")) {
                final AlertDialog builder = new AlertDialog.Builder(this).create();

                builder.setMessage("Watch a video ad to remove all the full screen ads ,The ads will be removed temporarily");
                builder.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        builder.dismiss();
                    }
                });
                builder.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)

                    {

                        builder.dismiss();
                    }
                });
                builder.show();
            } else
                show_Toast("Ads are already Removed");

        }
        if (id == R.id.navigation_btn)
            drawerLayout.openDrawer(Gravity.LEFT);

    }

    public void show_Toast(String message) {
        Toast.makeText(main_context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }


    public void jsonlist(String name, int img_file, String url) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("img_file", img_file);
        jsonObject.put(
                "url", url);

        jsonArray.put(jsonObject);
        //  show_Toast( jsonArray.getJSONObject(0).getString("name"));
        ArrayList arrayList = new ArrayList();

        arrayList.addAll(Collections.singleton(jsonArray.getJSONObject(0).getString("name")));


    }


    public void set_btn() {


        navigation_btn = findViewById(R.id.navigation_btn);
        remove_ads = findViewById(R.id.remove_ads);

        navigation_btn.setOnClickListener(this);
        remove_ads.setOnClickListener(this);
        remove_ads.setBackground(Helper_class.setBackgroundWithGradient("#E51512", "#E1541F", (float) 20, GradientDrawable.Orientation.LEFT_RIGHT));
        gridView = findViewById(R.id.grid_category);
        gridView_info = findViewById(R.id.info);


    }

    public void setListView() {
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.category_item, cat_title) {
            @NotNull
            public View getView(int position, View convertView, ViewGroup container) {

                if (convertView == null) {
                    if (container == null)
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_item, null);
                    else

                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_item, container, false);
                }

                TextView title = convertView.findViewById(R.id.list_text);
                title.setTextColor(Color.BLACK);
                ImageView imageView = convertView.findViewById(R.id.category_img);

                imageView.setImageResource((Integer) cat_img.get(position));
                title.setText((CharSequence) cat_title.get(position));

                return convertView;
            }
        };

        gridView.setAdapter(arrayAdapter);
        gridView.setBackground(Helper_class.setBackgroundWithGradient("#E9903B", "#E9E93B", (float) 30, GradientDrawable.Orientation.TOP_BOTTOM));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), Fav_activity.class);
                intent.putExtra("category", cat_title.get(i).toString());
                startActivity(intent);
                drawerLayout.closeDrawer(Gravity.LEFT);

            }
        });

        final ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, R.layout.category_item, info_title) {
            public View getView(int position, View convertView, ViewGroup container) {
                if (convertView == null) {

                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_item, container, false);
                } else
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_item, null);
                TextView title = convertView.findViewById(R.id.list_text);
                title.setTextSize(15);
                title.setTextColor(Color.parseColor("#FFFFFF"));
                ImageView imageView = convertView.findViewById(R.id.category_img);
                imageView.setImageResource((Integer) info_img.get(position));
                title.setText((CharSequence) info_title.get(position));

                return convertView;
            }
        };
        gridView_info.setAdapter(arrayAdapter2);


        gridView_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WebView webView;
                if (i == 0) {
                    Intent send = new Intent(Intent.ACTION_VIEW);
                    send.setData(Uri.parse("https://play.google.com/store/apps/developer?id=GLASSWEB&hl=en"));
                    startActivity(send);
                }

                if (i == 1 || (i == 2)) {

                    webView = new WebView(getApplicationContext());
                    if (i == 1)
                        webView.loadUrl("file:///android_asset/policy.html");

                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            MainActivity.this).create();
                    if (i == 2) {
                        alertDialog.setTitle("About us");
                        alertDialog.setMessage(Html.fromHtml(get_about_us()));
                    } else

                        alertDialog.setView(webView);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ok", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                if (i == 3) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Gamers Hub -Best game collection in just a single app   https://play.google.com/store/apps/details?id=com.nitish.gamershub";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }


                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

    }

    public void setJsonArray_new() {
        jsonArray2 = new JSONArray();
        jsonArray_best = new JSONArray();
        jsonArray_new = new JSONArray();
        try {
            JSONArray jsonArray3 = new JSONArray(data);

            //   JSONObject jsonObject1 = new JSONObject(sh.getString("json",null));
            //    jsonArray2 = jsonObject1.getJSONArray("main");

            jsonArray2 = jsonArray3.getJSONObject(0).getJSONArray("main");
            jsonArray_best = jsonArray3.getJSONObject(0).getJSONArray("best");
            jsonArray_new = jsonArray3.getJSONObject(0).getJSONArray("new");
        } catch (JSONException e) {
            e.printStackTrace();
            show_Toast("error " + e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        ads_state = "true";
        if (fb_rewardedVideoAd != null) {
            fb_rewardedVideoAd.destroy();
            fb_rewardedVideoAd = null;
        }
        super.onDestroy();
    }

//    public void loadAdmob_rewardedAd() {
//        adLoadCallback = new RewardedAdLoadCallback() {
//            @Override
//            public void onRewardedAdLoaded() {
//                // Ad successfully loaded.
//            }
//
//            @Override
//            public void onRewardedAdFailedToLoad(LoadAdError adError) {
//                // Ad failed to load.
//            }
//        };
//        admob_rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
//
//    }

//    public void show_rewarded_ad() {
//
//        Activity activityContext = MainActivity.this;
//        RewardedAdCallback adCallback = new RewardedAdCallback() {
//            @Override
//            public void onRewardedAdOpened() {
//                // Ad opened.
//            }
//
//            @Override
//            public void onRewardedAdClosed() {
//                admob_rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
//            }
//
//            @Override
//            public void onUserEarnedReward(@NonNull RewardItem reward) {
//                ads_state = "false";
//                show_Toast("ads removed");
//
//            }
//
//            @Override
//            public void onRewardedAdFailedToShow(AdError adError) {
//                // Ad failed to display.
//            }
//        };
//        admob_rewardedAd.show(activityContext, adCallback);
//    }


    public String get_about_us() {
        String x =
                "  Glassweb is an Android app development company owned by Nitish Kumar\n" +
                        "\n" +
                        "  We create quality apps for the market,\n" +
                        "\n" +
                        "  Company loacation : Panta ,India\n" +
                        "\n" +
                        "  Company contact : Nitish.kumar647@gmail.com";
        return x;

    }

    public void load_fb_reward_ads() {

        RewardedVideoAdListener rewardedAdListener = new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoCompleted() {
                ads_state = "false";
                show_Toast("Ads removed");
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }

            @Override
            public void onRewardedVideoClosed() {

            }

            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

        };
        fb_rewardedVideoAd.loadAd(fb_rewardedVideoAd.buildLoadAdConfig().withAdListener(rewardedAdListener).build());
    }


}