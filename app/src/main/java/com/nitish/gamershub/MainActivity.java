package com.nitish.gamershub;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.navigation.NavigationView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
ArrayList game_name;
ArrayList game_link;
ArrayList game_img;
public static Context main_context;
    ActionBarDrawerToggle  actionBarDrawerToggle;
RecyclerView recyclerView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ToggleButton toggleButton;
    HashMap<String,HashMap>  hashMap;
    JSONArray jsonArray;
    TextView textView;
    //ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.main_text);
   navigationView =(NavigationView)findViewById(R.id.navigation_view);
  drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        recyclerView = (RecyclerView)findViewById(R.id.main_recycler) ;
main_context = getApplicationContext();
        jsonArray = new JSONArray();
   actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.navigation_drawer_open, R.string.navigation_drawer_close)
   {
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
setNavigationView();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
 actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        game_link = new ArrayList();
        game_name = new ArrayList();
        game_img = new ArrayList();
        Set_array_list();
        try {
            jsonlist("Furious Road",R.drawable.furious_road_shotoss,"https://m.shtoss.com/game/furious-road/index.html");
            jsonlist("Rotare",R.drawable.rotare_shotoss,"https://m.shtoss.com/game/rotare/index.html");
            jsonlist("Color Shape",R.drawable.color_shape,"https://play.famobi.com/color-shape");
            jsonlist("Perfect Piano",R.drawable.perfect_piano,"https://play.famobi.com/perfect-piano");
            jsonlist("Stupid Zombies 2",R.drawable.stupid_zombies2,"https://html5.gamedistribution.com/5c0ad273bb7f4945a53f69d7d9adfc70/");
            show_Toast(jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter("main",this,game_name,game_link,game_img,jsonArray);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setAdapter(recyclerviewAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
    }
    public void Set_array_list()
    {
        game_name.add("Color Shape");
        game_name.add("Perfect Piano");
        game_link.add("https://play.famobi.com/color-shape");
        game_link.add("https://play.famobi.com/perfect-piano");

        game_img.add("https://img.cdn.famobi.com/portal/html5games/images/tmp/ColorShapeTeaser.jpg");
        game_img.add("https://img.cdn.famobi.com/portal/html5games/images/tmp/PerfectPiano_Teaser.jpg");

    }
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item);
    }
    public void setNavigationView()
    {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()== R.id.fav)
                {
                    Toast.makeText(getBaseContext(),"fav",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),Fav_activity.class));
                }
                if(item.getItemId()== R.id.clear_pref)
                {
                    Toast.makeText(getBaseContext(),"cleared",Toast.LENGTH_LONG).show();
                    deleteSharedPreferences(RecyclerviewAdapter.sharedpref_fav);
                                    }
                drawerLayout.closeDrawer(Gravity.LEFT);
                return false;
            }
        });
    }

    @Override
    protected void onRestart() {


        finish();
        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);

        super.onRestart();
    }



    public  void show_Toast(String message)
    {
        Toast.makeText(main_context,message,Toast.LENGTH_LONG).show();
    }
    public static void refresh()
    {
        new MainActivity().recreate();
       // new MainActivity().startActivity(new MainActivity().getIntent());
    }

    @Override
    protected void onResume() {

        super.onResume();
    }
    public void setHashMap() throws JSONException {



        hashMap= new HashMap();
        HashMap hashMap2= new HashMap();
        hashMap2.put("hj",R.drawable.fab_off1);
        ArrayMap arrayMap = new ArrayMap();
        hashMap.put("this is 1", hashMap2);
        hashMap2.values();

        JSONArray jsonArray = new JSONArray(hashMap.values());
       show_Toast( String.valueOf(jsonArray.getJSONObject(0).getInt("hj")));
    }
    public void get_hashmap()
    {



       ArrayList arrayList = new ArrayList<>();
HashMap hashMap4 = null;
        HashSet hashSet = new HashSet();
        hashSet.addAll(hashMap.values());
        arrayList.add( hashMap.values());


        show_Toast(hashSet.toString());


    }
    public void jsonlist(String name , int img_file ,String url) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name",name);
        jsonObject.put("img_file",img_file);
        jsonObject.put(
                "url",url);

        jsonArray.put(jsonObject);
      //  show_Toast( jsonArray.getJSONObject(0).getString("name"));
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(Collections.singleton(jsonArray.getJSONObject(0).getString("name")));



    }
    public void set_textview(String x)
    {
        textView.setText(x);
    }
}