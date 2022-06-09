package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter.gameDataObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nitish.gamershub.Fragments.GameDetailsFragment;
import com.nitish.gamershub.Fragments.GamePlayFragment;
import com.nitish.gamershub.Helper_class;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.R;
import com.squareup.picasso.Picasso;

public class GameDetailActivity2 extends AppCompatActivity {




    Button playButton,checkVisibilityButton;
    static  String DetailFrag = "GameDetailFragment";
    Fragment gamePlayFragment,gameDetailsFragment;

    boolean gamePlayVisibility= false;
    AllGamesItems allGamesItems;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        playButton = findViewById(R.id.playButton);
        checkVisibilityButton =findViewById(R.id.checkVisibilityButton);
         allGamesItems = (AllGamesItems) getIntent().getSerializableExtra(gameDataObject);
         fragmentManager = getSupportFragmentManager();
        gamePlayFragment = fragmentManager.findFragmentById(R.id.gamePlayFrag);
        gameDetailsFragment = fragmentManager.findFragmentById(R.id.gameDescFrag);



        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction      fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.show(gamePlayFragment).hide(gameDetailsFragment).addToBackStack(null).commit();

                    if (allGamesItems.getOrientation().toLowerCase().contains("hori")) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    }

                    gamePlayVisibility = true;
                    // game play fragment is visible


            }
        });



    }
    @Override
    public void onBackPressed() {
        if (gamePlayVisibility) {
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage("Do you want to Exit the game");

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FragmentTransaction      fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    show_ads_admob_first();
//                    adblockWebView.onPause();
//                    wv_paused = true;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    fragmentTransaction.show(gameDetailsFragment).hide(gamePlayFragment);
                    fragmentTransaction.disallowAddToBackStack();
                    fragmentTransaction.commit();
                    // game play fragment is not visible
                    gamePlayVisibility = false;



                }
            });

            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();


            Button positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMarginStart(10);
            if (positive != null) {
                positive.setBackground(Helper_class.setBackgroundWithGradient("#FF0303", "#FF0303", (float) 20, GradientDrawable.Orientation.LEFT_RIGHT));
                positive.setTextColor(Color.WHITE);
                positive.setGravity(Gravity.CENTER);
                positive.setLayoutParams(params);

            }
            if (negative != null) {
                negative.setTextColor(Color.WHITE);
                negative.setGravity(Gravity.CENTER);
                negative.setLayoutParams(params);
                negative.setBackground(Helper_class.setBackgroundWithGradient("#FF0303", "#FF0303", (float) 20, GradientDrawable.Orientation.LEFT_RIGHT));
            }

        } else
            super.onBackPressed();


    }
}