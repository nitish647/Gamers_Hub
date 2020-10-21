package com.nitish.gamershub;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class Desc_frag extends Fragment {

    TextView instruction, textview_category, textView_game_name, internet_req_textview;
    ImageView imageView;
    AdView google_banner_ad;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String name, instrution, img_file, json_obj;
    Button game_play_btn, report_btn;
    ShineButton fav_btn;

    public Desc_frag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_desc_frag, container, false);
        fav_btn = view.findViewById(R.id.desc_frag_fav_btn);
        report_btn = view.findViewById(R.id.frag_report_button);
        game_play_btn = view.findViewById(R.id.frag_desc_start_btn);
        imageView = view.findViewById(R.id.frag_desc_img);
        textview_category = view.findViewById(R.id.frag_text_cat);
        internet_req_textview = view.findViewById(R.id.frag_internet_req_text);
        textView_game_name = view.findViewById(R.id.frag_game_name_text);
        instruction = view.findViewById(R.id.frag_instr_textview);

        design();


        json_obj = RecyclerviewAdapter.recycler_json;
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(RecyclerviewAdapter.sharedpref_fav, MODE_PRIVATE);
        editor = sharedPreferences.edit();


        instruction.setText(Html.fromHtml(getInstrution()));
        instruction.setBackground(Helper_class.set_Colors("#22BCF6", "#0D52F0", (float) 30, GradientDrawable.Orientation.LEFT_RIGHT));

//google ads
        google_banner_ad = view.findViewById(R.id.google_banner_adView_desc);
        AdRequest adRequest = new AdRequest.Builder().build();
        google_banner_ad.loadAd(adRequest);
        if (!this.isVisible())
            google_banner_ad.destroy();

        String img_file = "", name = "", url = "", orientation = "";

        try {
            JSONObject jsonObject = new JSONObject(RecyclerviewAdapter.recycler_json);
            img_file = jsonObject.getString("img_file");
            name = jsonObject.getString("name");
            String cat = jsonObject.getString("category").toUpperCase();
            Picasso.get().load(img_file).into(imageView);
            textView_game_name.setText(name);
            textview_category.setText(cat);
            setFav();
            url = jsonObject.getString("url");
            if (url.contains("shtoss") || url.contains("lagged"))
                internet_req_textview.setText("you only need internet for the first time to play this game!");
            else
                internet_req_textview.setText("Internet Connection is required for this game!");
            orientation = jsonObject.getString("orientation");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //copy of variable
        final String finalName = name;


        if (sharedPreferences.contains(name))
            fav_btn.setBackgroundResource(R.drawable.fav_on2);
        else
            fav_btn.setBackgroundResource(R.drawable.fav_of2);

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


        game_play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                intent1 = new Intent(getApplicationContext(),Game_play.class);
//                intent1.putExtra("url", finalUrl);
//                intent1.putExtra("orientation", finalOrientation);
//
//
//                startActivity(intent1);


            }
        });


        fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String x = "";
                if (!sharedPreferences.contains(finalName)) {
                    x = "Added to Favourites";
                    fav_btn.setBackgroundResource(R.drawable.fav_on2);
                    editor.putString(finalName, json_obj);


                } else if (sharedPreferences.contains(finalName)) {
                    fav_btn.setBackgroundResource(R.drawable.fav_of2);
                    editor.remove(finalName);
                    x = "Removed from Favourites";


                } else
                    Helper_class.show_toast(view.getContext(), "error ");
                editor.apply();
                Snackbar snackbar = Snackbar.make(view, x, Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                snackbar.setActionTextColor(Color.BLUE);
                View snackBarView = snackbar.getView();

                snackBarView.setBackground(Helper_class.set_Colors("#23C734", "#16EC2C", (float) 10, GradientDrawable.Orientation.LEFT_RIGHT));
                //   snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }

        });


        return view;
    }

    public String getInstrution() {
        String instrution = "<p>Instruction</p>\n" +
                "<p>If a blank screen appears while starting the game then come back and start the game with proper internet connection</p>\n" +
                "<p>The game's data is stored in the cache , if you clear the cache the data will be deleted</p>\n" +
                "<p>If a problem occurs then report the game</p>";
        return instrution;
    }

    public void setFav() {
        if (sharedPreferences.contains(name))
            fav_btn.setBackgroundResource(R.drawable.fav_on2);
        else
            fav_btn.setBackgroundResource(R.drawable.fav_of2);
    }

    public void design() {
        game_play_btn.setBackground(Helper_class.set_Colors("#FC3F03", "#F37D0D", (float) 20, GradientDrawable.Orientation.LEFT_RIGHT));
        internet_req_textview.setBackground(Helper_class.set_Colors("#FC3F03", "#F37D0D", (float) 20, GradientDrawable.Orientation.LEFT_RIGHT));
        textview_category.setBackground(Helper_class.set_Colors("#FFA406", "#FFA406", (float) 40, GradientDrawable.Orientation.LEFT_RIGHT));
    }

}