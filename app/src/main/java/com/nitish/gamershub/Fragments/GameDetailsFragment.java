package com.nitish.gamershub.Fragments;

import static com.nitish.gamershub.Utils.ConstantsHelper.FavouriteList;
import static com.nitish.gamershub.Utils.ConstantsHelper.gameDataObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
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
import com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter;
import com.nitish.gamershub.Helper_class;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.FragmentGameDetailsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.paperdb.Paper;

public class GameDetailsFragment extends Fragment {

    View view;

    GameDetailActivity2 parentActivity;
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

    FragmentGameDetailsBinding binding;
    public static GameDetailsFragment newInstance() {
        GameDetailsFragment fragment = new GameDetailsFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_game_details, container, false);
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_game_details,container,false);
        Paper.init(view.getContext());
        backButton=  view.findViewById(R.id.backButton);
        playButton = view.findViewById(R.id.playButton);
        gameImageImageVIew = view.findViewById(R.id.gameImageImageVIew);
        gameImageContainerRelative =view.findViewById(R.id.gameImageContainerRelative);
        gameNameTextview =view.findViewById(R.id.gameNameTextview);
        gameDescTextview =view.findViewById(R.id.gameDescTextview);
        favButton =view.findViewById(R.id.favButton);
        parentActivity = (GameDetailActivity2) view.getContext();

        favouriteArrayList = new ArrayList<>();
         allGamesItems = NewAndPopularGamesAdapter.SelectedGameObject;




        ArrayList<AllGamesItems> itemsArrayList = (ArrayList<AllGamesItems>) Paper.book().read(FavouriteList);
        favouriteArrayList = itemsArrayList;




        setViews();

        setOnClickListeners();



        return  binding.getRoot();
    }
    public void setOnClickListeners()
    {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });



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
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentActivity.playButtonClick();
            }
        });


    }
    public void setViews()
    {
        gameDescTextview.setText(allGamesItems.getDescription());

        gameNameTextview.setText(allGamesItems.getName());
        if(checkInFavList(allGamesItems))
        {
            favButton.setImageResource(R.drawable.fav_on2);

        }
        // when it is not in favourite then add in favourites
        else {
            favButton.setImageResource(R.drawable.fav_of2);

        }
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

        String text =" Play this game for "+
                parentActivity.getGamersHubDataGlobal().gamesData.getGamePlaySecs()+" seconds to get rewarded";
        binding.gamePlayTimeTextview.setText(text);


        playButton.setBackground(Helper_class.setSingleColorRoundBackground("#F0740D", 15.0F));

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