package com.nitish.gamershub.Fragments;

import static com.nitish.gamershub.Activities.HomeActivity.FavouriteList;
import static com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter.gameDataObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.LogDescriptor;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nitish.gamershub.Activities.GameDetailActivity2;
import com.nitish.gamershub.Activities.GamePlayActivity;
import com.nitish.gamershub.Activities.HomeActivity;
import com.nitish.gamershub.Helper_class;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.paperdb.Paper;

public class GameDetailsFragment extends Fragment {

    View view;

    ImageView gameImageImageVIew;
    Button playButton;
    ImageView backButton;
    RelativeLayout gameImageContainerRelative;
    int vibrant ;
    int vibrantLight;
    int vibrantDark;
    int muted;
    int mutedLight;
    int mutedDark;
    TextView gameDescTextview,gameNameTextview;
    ImageView favButton;

    AllGamesItems allGamesItems;
    ArrayList<AllGamesItems> favouriteArrayList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_game_details, container, false);

        Paper.init(view.getContext());
        backButton=  view.findViewById(R.id.backButton);
        playButton = view.findViewById(R.id.playButton);
        gameImageImageVIew = view.findViewById(R.id.gameImageImageVIew);
        gameImageContainerRelative =view.findViewById(R.id.gameImageContainerRelative);
        gameNameTextview =view.findViewById(R.id.gameNameTextview);
        gameDescTextview =view.findViewById(R.id.gameDescTextview);
        favButton =view.findViewById(R.id.favButton);

         allGamesItems = (AllGamesItems) getActivity().getIntent().getSerializableExtra(gameDataObject);


        favouriteArrayList = Paper.book().read(FavouriteList);



        gameNameTextview.setText(allGamesItems.getName());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        if(checkInFavList(allGamesItems))
        {
            favButton.setImageResource(R.drawable.fav_on2);

        }
        // when it is not in favourite then add in favourites
        else {
            favButton.setImageResource(R.drawable.fav_of2);

        }

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // when it is in favourite then remove from favourites


                if(checkInFavList(allGamesItems))
                {
                    deleteFromFavourite(allGamesItems);
                    favButton.setImageResource(R.drawable.fav_of2);
                }
                // when it is not in favourite then add in favourites
                else {
                    saveToFavourite(allGamesItems);
                    favButton.setImageResource(R.drawable.fav_on2);

                }

            }
        });
        gameDescTextview.setText(allGamesItems.getDescription());



        Picasso.get().load(allGamesItems.getImg_file()).into(gameImageImageVIew,new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

                getDominantGradient(gameImageImageVIew);
                gameImageContainerRelative.setBackgroundColor(vibrant);
            }

            @Override
            public void onError(Exception e) {

            }

        });



        playButton.setBackground(Helper_class.setSingleColorRoundBackground("#F0740D", 15.0F));

        return  view;
    }
    public void saveToFavourite(AllGamesItems allGamesItems)
    {
        favouriteArrayList.add(allGamesItems);
        Paper.book().write(FavouriteList, favouriteArrayList);
    }
    public void deleteFromFavourite(AllGamesItems allGamesItems)
    {

        for(int i =0;i<favouriteArrayList.size();i++)
        if(favouriteArrayList.get(i).getGameUrl().equals(allGamesItems.getGameUrl())) {

            favouriteArrayList.remove(i);
            break;
        }

        Paper.book().write(FavouriteList, favouriteArrayList);
    }

    public boolean checkInFavList(AllGamesItems allGamesItems)
    {

        boolean isInFavList=false;

            for(int i =0;i<favouriteArrayList.size();i++)
        {
            if(favouriteArrayList.get(i).getGameUrl().equals(allGamesItems.getGameUrl())) {
                isInFavList = true;

                break;
            }

        }
        return isInFavList;
    }
    public void getDominantGradient(ImageView imageView)
    {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        Palette.from(bitmap).generate(palette -> {
            assert palette != null;
             vibrant = palette.getVibrantColor(0x003001); // <=== color you want

            vibrantLight = palette.getLightVibrantColor(0x00000);
            vibrantDark = palette.getDarkVibrantColor(0x000000);
            muted = palette.getMutedColor(0x000000);
            mutedLight = palette.getLightMutedColor(0x000000);
            mutedDark = palette.getDarkMutedColor(0x000000);
        });
    }


}