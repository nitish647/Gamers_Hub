package com.nitish.gamershub;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Game_desc extends AppCompatActivity {
    ImageView imageView;
    LottieAnimationView lottieAnimationView;
    TextView textView, instruction;
    Button report_btn;
    Button play_button;
    ImageView fav;

    Intent intent1;
    AdView google_banner_ad;
    GradientDrawable gradientDrawable, btn_play_gd;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String name, instrution;
    Snackbar snackbar;
    TextView category, internet_req;
    private InterstitialAd admob_mInterstitialAd;
    public JSONArray jsonArray_fav = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_desc);
        String img_file = "", name = "", url = "", orientation = "";

        sharedPreferences = getSharedPreferences(RecyclerviewAdapter.sharedpref_fav, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        set_view();

        final Intent intent = getIntent();

        final String json_obj = intent.getStringExtra("json");

        try {
            JSONObject jsonObject = new JSONObject(json_obj);
            img_file = jsonObject.getString("img_file");
            name = jsonObject.getString("name");
            String cat = jsonObject.getString("category").toUpperCase();
            Picasso.get().load(img_file).into(imageView);
            textView.setText(name);
            category.setText(cat);
            setFav();
            url = jsonObject.getString("url");
            if (url.contains("shtoss"))
                internet_req.setText("you only need internet for the first time to play this game!");
            else
                internet_req.setText("Internet Connection is required for this game!");
            orientation = jsonObject.getString("orientation");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //   String img_file = intent.getStringExtra("img");
//       name = intent.getStringExtra("name");

        //google ads
        google_banner_ad = findViewById(R.id.google_banner_adView_desc);
        AdRequest adRequest = new AdRequest.Builder().build();
        google_banner_ad.loadAd(adRequest);

//        admob_mInterstitialAd = new InterstitialAd(this);
//        admob_mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_inter));
//        admob_mInterstitialAd.loadAd(new AdRequest.Builder().build());
    //    setAdmob_mInterstitialAd();

        //copy of variable
        final String finalName = name;
        final String finalUrl = url;
        if (sharedPreferences.contains(finalName))
            fav.setBackgroundResource(R.drawable.fav_on2);
        else
            fav.setBackgroundResource(R.drawable.fav_of2);
        report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Do you want to report " + finalName);

                builder.setPositiveButton("yes ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nitish.kumar647@gmail.com"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Problem in the game " + finalName);
                        intent.putExtra(Intent.EXTRA_TEXT, "The Game is not working");

                        startActivity(Intent.createChooser(intent, "Send Email"));
                        Helper_class.show_toast(view.getContext(), "Choose email");

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Helper_class.show_toast(view.getContext(), " No");
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setIconAttribute(R.drawable.gamers_hub_icon1);
                alertDialog.setIcon(R.drawable.gamers_hub_icon1);
                alertDialog.show();
            }
        });

        final String finalOrientation = orientation;
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent1 = new Intent(getApplicationContext(), Game_play.class);
                intent1.putExtra("url", finalUrl);
                intent1.putExtra("orientation", finalOrientation);


                startActivity(intent1);

            }
        });


        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String x = "";
                if (!sharedPreferences.contains(finalName)) {
                    x = "Added to Favourites";
                    fav.setBackgroundResource(R.drawable.fav_on2);
                    editor.putString(finalName, json_obj);


                } else if (sharedPreferences.contains(finalName)) {
                    fav.setBackgroundResource(R.drawable.fav_of2);
                    editor.remove(finalName);
                    x = "Removed from Favourites";


                } else
                    Helper_class.show_toast(getApplicationContext(), "error ");
                editor.apply();
                snackbar = Snackbar.make(view, x, Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                snackbar.setActionTextColor(Color.BLUE);
                View snackBarView = snackbar.getView();

                snackBarView.setBackground(Helper_class.setBackgroundWithGradient("#23C734", "#16EC2C", (float) 10, GradientDrawable.Orientation.LEFT_RIGHT));
                //   snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }

        });
    }

    public void set_view() {
        imageView = findViewById(R.id.desc_img);
        textView = findViewById(R.id.desc_text);
        category = findViewById(R.id.text_category);
        play_button = findViewById(R.id.btn_play);
        report_btn = findViewById(R.id.btn_report);
        fav = findViewById(R.id.fav);
        instruction = findViewById(R.id.instruction);

        internet_req = findViewById(R.id.internet_req);
        gradientDrawable = Helper_class.setBackgroundWithGradient("#FF2525", "#FF7E20", (float) 40, GradientDrawable.Orientation.LEFT_RIGHT);

        btn_play_gd = Helper_class.setBackgroundWithGradient("#FC3F03", "#F37D0D", (float) 20, GradientDrawable.Orientation.LEFT_RIGHT);
        play_button.setBackground(btn_play_gd);
        category.setBackground(gradientDrawable);
//btn_play_gd = Helper_class.set_Colors("#0D52F0","0D52F0", (float) 30, GradientDrawable.Orientation.LEFT_RIGHT);
        GradientDrawable gd2;
        gd2 = Helper_class.setBackgroundWithGradient("#22BCF6", "#0D52F0", (float) 30, GradientDrawable.Orientation.LEFT_RIGHT);
        ArrayList arrayList = new ArrayList();
        arrayList.add(getResources().getString(R.string.instruction));
        String x = arrayList.get(0).toString();

        instruction.setText(Html.fromHtml(getInstrution()));

        instruction.setSingleLine(false);

        instruction.setTextColor(Color.parseColor("#FFFFFF"));
        instruction.setBackground(gd2);

        gd2 = Helper_class.setBackgroundWithGradient("#FF0000", "#F5570D", (float) 30, GradientDrawable.Orientation.LEFT_RIGHT);
        internet_req.setBackground(gd2);
        internet_req.setTextColor(Color.parseColor("#FFFFFF"));

    }

    public void setFav() {
        if (sharedPreferences.contains(name))
            fav.setBackgroundResource(R.drawable.fav_on2);
        else
            fav.setBackgroundResource(R.drawable.fav_of2);
    }

    public GradientDrawable single_color_grad(String color, Float radius) {
        return Helper_class.setBackgroundWithGradient(color, color, radius, GradientDrawable.Orientation.LEFT_RIGHT);
    }

    public String getInstrution() {
        instrution = "<p>Instruction</p>\n" +
                "<p>If a blank screen appears while starting then come back and start the game with proper internet connection</p>\n" +
                "<p>The game's data is stored in the cache , if you clear the cache the data will be deleted</p>\n" +
                "<p>If a problem occurs then report the game</p>";
        return instrution;
    }

    @Override
    public void onBackPressed() {
        if (MainActivity.ads_state.contains("true")) {
//            if (admob_mInterstitialAd.isLoaded())
//                admob_mInterstitialAd.show();
//            else
//                super.onBackPressed();
        } else
            super.onBackPressed();
    }

//    public void setAdmob_mInterstitialAd() {
//        admob_mInterstitialAd.setAdListener(new AdListener() {
//
//            @Override
//            public void onAdClosed() {
//                admob_mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                onBackPressed();
//                super.onAdClosed();
//            }
//        });
//
//
//    }


}