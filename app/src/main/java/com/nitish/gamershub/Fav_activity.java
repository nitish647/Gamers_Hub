package com.nitish.gamershub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Fav_activity extends AppCompatActivity {
RecyclerView recyclerView;
SharedPreferences sharedPreferences ;
HashMap hashMap ;
TextView textView;
JSONArray jsonArray ,jsonArray2;
ArrayList key_url,key_value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        recyclerView=(RecyclerView)findViewById(R.id.fav_recycler);
        textView =(TextView)findViewById(R.id.test_textview);
        hashMap = new HashMap();
        key_url = new ArrayList();

        key_value = new ArrayList();
//        key_value.add("https://img.cdn.famobi.com/portal/html5games/images/tmp/ColorShapeTeaser.jpg");
//       key_value.add("https://img.cdn.famobi.com/portal/html5games/images/tmp/PerfectPiano_Teaser.jpg");
        sharedPreferences = getSharedPreferences(RecyclerviewAdapter.sharedpref_fav,MODE_PRIVATE);



String x = null;
        hashMap.putAll(sharedPreferences.getAll());

        JSONArray jsonArray3 = new JSONArray();


        try {

            jsonArray = new JSONArray(hashMap.values().toString());

            for(int i = 0;i<jsonArray.length();i++) {

                jsonArray3.put(jsonArray.getJSONObject(i));

            }

            textView.setText("json  "+jsonArray3);

           // show_toast("aaray is "+jsonArray.toString());
           // show_toast("aaray is "+jsonArray.getString(0));

//         textView.setText("key "+hashMap.keySet()+"  "+jsonArray3.toString());
        } catch (JSONException e) {
//            textView.setText("error "+e.toString());
            show_toast("error "+e.toString());
            e.printStackTrace();
        }



        key_url.addAll(hashMap.keySet());

        key_value.addAll(hashMap.values());
        RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter("fav_act",getApplicationContext(),key_value,key_url,key_value,jsonArray3);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setAdapter(recyclerviewAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        show_toast(key_url.toString());
    }
    public void show_toast(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}