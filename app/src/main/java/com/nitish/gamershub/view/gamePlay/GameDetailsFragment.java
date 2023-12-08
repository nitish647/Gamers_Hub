package com.nitish.gamershub.view.gamePlay;


import static com.nitish.gamershub.utils.AppConstants.IntentData;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.nitish.gamershub.databinding.FragmentGameDetailsBinding;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.R;
import com.nitish.gamershub.model.firebase.GamePlayedStatus;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;
import com.nitish.gamershub.model.local.GlideData;
import com.nitish.gamershub.model.local.SnackBarItems;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.GlideHelper;
import com.nitish.gamershub.view.base.BaseFragment;
import com.nitish.gamershub.view.dialogs.DialogListener;

import java.util.ArrayList;

import io.paperdb.Paper;

public class GameDetailsFragment extends BaseFragment {


    GameDetailActivity2 parentActivity;


    GamesItems gamesItems;

    FragmentGameDetailsBinding binding;
    UserProfile userProfile;
    GamePlayedStatus gamePlayedStatus;


    public static GameDetailsFragment newInstance() {
        GameDetailsFragment fragment = new GameDetailsFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_details, container, false);
        Paper.init(binding.getRoot().getContext());

        parentActivity = (GameDetailActivity2) binding.getRoot().getContext();
        gamesItems = (GamesItems) getActivity().getIntent().getSerializableExtra(IntentData);


        userProfile = getPreferencesMain().getUserProfile();
        gamePlayedStatus = userProfile.getGamePlayedStatus();


        setOnClickListeners();
        setViews();


        return binding.getRoot();
    }

    public void setOnClickListeners() {
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        binding.favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // when it is in favourite then remove from favourites


                if (isGameFav(gamesItems)) {

                    deleteFromFavourite(gamesItems);
                    binding.favButton.setImageResource(R.drawable.fav_of2);
                }
                // when it is not in favourite then add in favourites
                else {
                    saveToFavourite(gamesItems);
                    binding.favButton.setImageResource(R.drawable.fav_on2);

                }

            }
        });

        binding.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentActivity.playButtonClick();
            }
        });

        binding.reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGameReportDialog();
            }
        });


    }

    public void setViews() {
        // TestCode: Anuraag
//        allGamesItems = null;

        binding.gameCategoryTextview.setText(gamesItems.getCategory());
        binding.gameDescTextview.setText(gamesItems.getDescription());
        binding.gameImageImageVIew.setClipToOutline(true);
        binding.gameNameTextview.setText(gamesItems.getName());


        if (isGameFav(gamesItems)) {

            binding.favButton.setImageResource(R.drawable.fav_on2);

        }
        // when it is not in favourite then add in favourites
        else {
            binding.favButton.setImageResource(R.drawable.fav_of2);

        }


        loadGameImage();
        setGamePlayInstructions();


    }

    private void loadGameImage() {
        GlideData glideData = new GlideData();
        glideData.setImageUrl(gamesItems.getImg_file());
        glideData.setPlaceHolder(R.drawable.gamers_hub_icon15);
        GlideHelper.loadGlideImage(binding.gameImageImageVIew, glideData, new GlideHelper.GlideListener() {
            @Override
            public void onImageLoadFailed() {

            }

            @Override
            public void onImageLoaded(Drawable drawable) {


//                AppHelper.getDominantGradient(binding.gameImageImageVIew, new AppHelper.OnPalleteColorGet() {
//                    @Override
//                    public void getDominantColor(int color) {
//                        binding.gameImageContainerRelative.setBackgroundTintList(ColorStateList.valueOf(color));
//
//                    }
//
//                });


            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        gamePlayedStatus = getPreferencesMain().getUserProfile().getGamePlayedStatus();
        setViews();

    }

    public void setGamePlayInstructions() {
        if (gamePlayedStatus.getGamePlayedToday() >= getPreferencesMain().getGamersHubData().gamesData.getDailyGamePlayLimit()) {
            String text = getString(R.string.your_have_reached_the_daily_reward_limit_of) +
                    getPreferencesMain().getGamersHubData().getGamesData().getDailyGamePlayLimit() + getString(R.string.games_come_back_tomorrow_to_get_more_rewards);
            binding.gamePlayTimeTextview.setText(text);

        } else {
            int min = getPreferencesMain().getGamersHubData().gamesData.getGamePlaySecs() / 60;
            String text = getString(R.string.play_this_game_for) +
                    min + getString(R.string.minutes_to_get_rewarded) +
                    getString(R.string.game_played_reward_left) + gamePlayedStatus.getGamePlayedToday() + "/" + getPreferencesMain().getGamersHubData().getGamesData().getDailyGamePlayLimit() + ")";
            binding.gamePlayTimeTextview.setText(text);

        }
    }

    public void saveToFavourite(GamesItems gamesItems) {

        getPreferencesMain().saveFavouriteItemInList(gamesItems);

        showFavSnackBar(true);

    }

    public void deleteFromFavourite(GamesItems gamesItems) {

        ArrayList<GamesItems> favouriteArrayList = getPreferencesMain().getSavedFavouriteList();
        favouriteArrayList.removeIf(items -> items.getGameUrl().equals(gamesItems.getGameUrl()));
        getPreferencesMain().saveFavouriteList(favouriteArrayList);
        showFavSnackBar(false);
    }

    public void showFavSnackBar(boolean isFavAdded) {
        String message = getString(R.string.added_to_favourites);

        // when removed from favorites
        if (!isFavAdded) {
            message = getString(R.string.removed_from_favourites);
        }

        SnackBarItems snackBarItems = new SnackBarItems(message);
        snackBarItems.setBackgroundColor(R.color.material_red);

        parentActivity.showSnackBar(binding.getRoot(), snackBarItems);
    }

    public boolean isGameFav(GamesItems gamesItems) {
        ArrayList<GamesItems> favouriteArrayList = getPreferencesMain().getSavedFavouriteList();

        boolean isInFavList = false;

        for (int i = 0; i < favouriteArrayList.size(); i++) {
            if (favouriteArrayList.get(i).getGameUrl().equals(gamesItems.getGameUrl())) {
                isInFavList = true;

                break;
            }

        }
        return isInFavList;
    }

    public void showGameReportDialog() {

        DialogItems dialogItems = new DialogItems();
        dialogItems.setYesTitle(getString(R.string.send));
        dialogItems.setTitle(getString(R.string.confirmation));
        dialogItems.setMessage(getString(R.string.do_you_want_to_report_this_game));

        parentActivity.showConfirmationDialog2(dialogItems, new DialogListener() {
            @Override
            public void onYesClick() {
                String subject = getString(R.string.issue_in_the_game) + gamesItems.getName();
                Uri uri = AppHelper.getMailMessageUri(parentActivity, userProfile, subject, "");
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

                startActivity(Intent.createChooser(intent, getString(R.string.send_us_email)));
            }

            @Override
            public void onNoClick() {

            }
        });
    }


}