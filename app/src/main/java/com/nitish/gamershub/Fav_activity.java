package com.nitish.gamershub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Fav_activity extends AppCompatActivity {
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences, sharedPreferences_category;
    HashMap hashMap;
    AdView fav_banner;
    String category_text;
    TextView fav_category;
    Button back_button;
    String parent_json_text;
    JSONArray jsonArray, jsonArray_category_list, json_best, jsonArray_new, jsonArray_type;
    ArrayList key_url, key_value, category_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        recyclerView = findViewById(R.id.fav_recycler);
        fav_category = findViewById(R.id.fav_category);
        Intent intent = getIntent();
        back_button = findViewById(R.id.back_btn);
        category_text = intent.getStringExtra("category");
        hashMap = new HashMap();
        key_url = new ArrayList();
        key_value = new ArrayList();

        //setting textview
        fav_category.setText(category_text);

        sharedPreferences = getSharedPreferences(RecyclerviewAdapter.sharedpref_fav, MODE_PRIVATE);

        sharedPreferences_category = getApplicationContext().getSharedPreferences("json_data", Context.MODE_PRIVATE);

        String x = null;
        hashMap.putAll(sharedPreferences.getAll());

        JSONArray jsonArray_fav = new JSONArray();

        category_resolver();

        fav_banner = findViewById(R.id.fav_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        fav_banner.loadAd(adRequest);


        // back button
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //json_new and json_best

        try {

            jsonArray_type = new JSONArray();
            JSONArray jsonArray_test = new JSONArray(parent_json_text);
            if (category_text.toLowerCase().contains("best"))
                jsonArray_type = jsonArray_test.getJSONObject(0).getJSONArray("best");
            else if (category_text.toLowerCase().contains("new"))
                jsonArray_type = jsonArray_test.getJSONObject(0).getJSONArray("new");

            jsonArray = new JSONArray(hashMap.values().toString());

            for (int i = 0; i < jsonArray.length(); i++) {

                jsonArray_fav.put(jsonArray.getJSONObject(i));

            }


        } catch (JSONException e) {


            e.printStackTrace();
        }


        key_url.addAll(hashMap.keySet());

        key_value.addAll(hashMap.values());
        RecyclerviewAdapter recyclerviewAdapter;
        if (category_text.toLowerCase().contains("favour"))
            recyclerviewAdapter = new RecyclerviewAdapter("fav_act", getApplicationContext(), key_value, key_url, key_value, jsonArray_fav);
        else if (category_text.toLowerCase().contains("best") || category_text.toLowerCase().contains("new"))
            recyclerviewAdapter = new RecyclerviewAdapter("fav_act", getApplicationContext(), key_value, key_url, key_value, jsonArray_type);
        else
            recyclerviewAdapter = new RecyclerviewAdapter("fav_act", getApplicationContext(), key_value, key_url, key_value, jsonArray_category_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setAdapter(recyclerviewAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        //animation
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_left_to_right);
        recyclerView.setLayoutAnimation(animation);

    }

    public void show_toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestart() {


        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);

        super.onRestart();
    }

    public void category_resolver() {
        category_list = new ArrayList();
        parent_json_text = sharedPreferences_category.getString("json_parent", null);
        JSONArray jsonArray;
        JSONArray jsonArray2;
        JSONObject jsonObject2;
        try {
            jsonArray = new JSONArray(parent_json_text);


            jsonArray2 = jsonArray.getJSONObject(0).getJSONArray("main");

            for (int i = 0; i < jsonArray2.length(); i++) {
                jsonObject2 = jsonArray2.getJSONObject(i);

                category_list.addAll(Collections.singleton(jsonObject2.getString("category").toLowerCase()));


            }


            List<Integer> matchingIndices = new ArrayList<>();
            for (int i = 0; i < category_list.size(); i++) {
                String element = (String) category_list.get(i);

                if (category_text.toLowerCase().contains(element.toLowerCase())) {
                    matchingIndices.add(i);
                }
            }

            jsonArray_category_list = new JSONArray();
            for (int i = 0; i < matchingIndices.size(); i++) {

                jsonArray_category_list.put(jsonArray2.getJSONObject(matchingIndices.get(i)));

            }


        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}