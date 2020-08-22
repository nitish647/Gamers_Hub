package com.nitish.gamershub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.sackcentury.shinebuttonlib.ShineButton;

import org.json.JSONArray;

public class Game_desc extends AppCompatActivity {
ImageView imageView ;
TextView textView;
Button play_button;
    ShineButton fav;
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor;
    Snackbar snackbar;
    String name;

    public JSONArray jsonArray_fav = new JSONArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_desc);
        sharedPreferences = getSharedPreferences(RecyclerviewAdapter.sharedpref_fav,MODE_PRIVATE);
       editor = sharedPreferences.edit();
        set_view();
        final Intent intent = getIntent();
        final String url = intent.getStringExtra("url");
        final String json_obj = intent.getStringExtra("json");
        int img_file = intent.getIntExtra("img",R.drawable.ic_launcher_background);
       name = intent.getStringExtra("name");
        setFav();
        imageView.setImageResource(img_file);
        textView.setText(name);
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(),Game_play.class);
                intent1.putExtra("url",url);
                startActivity(intent1);
            }
        });

        Helper_class.show_toast(getApplicationContext(),json_obj);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 if(!sharedPreferences.contains(name))
                {

                    fav.setBackgroundResource(R.drawable.fav_on1);
                    editor.putString(name, json_obj);
                    Helper_class.show_toast(getApplicationContext(),"added");
                    Helper_class.show_toast(getApplicationContext(),json_obj);
                }
                else if(sharedPreferences.contains(name))
                {  fav.setBackgroundResource(R.drawable.fab_off1);
                    editor.remove(name);
                    Helper_class.show_toast(getApplicationContext(),"removed");

                }
                else
                    Helper_class.show_toast(getApplicationContext(),"error ");
                editor.apply();
                snackbar =  Snackbar .make(view, "An Error Occurred!", Snackbar.LENGTH_LONG) .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                snackbar.setActionTextColor(Color.BLUE);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }

            });
        }

    public void set_view()
    {
        imageView = (ImageView)findViewById(R.id.desc_img);
        textView = (TextView) findViewById(R.id.desc_text);
        play_button = (Button) findViewById(R.id.btn_play);
        fav = (ShineButton) findViewById(R.id.fav);

    }
    public void setFav()
    {
     if (sharedPreferences.contains(name))
         fav.setBackgroundResource(R.drawable.fav_on1);
     else
         fav.setBackgroundResource(R.drawable.fab_off1);
    }

}