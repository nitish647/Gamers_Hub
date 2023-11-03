package com.nitish.gamershub.view.gamePlay;

import static com.nitish.gamershub.utils.AppHelper.getGamersHubDataGlobal;
import static com.nitish.gamershub.utils.AppHelper.getUserProfileGlobalData;
import static com.nitish.gamershub.utils.AppConstants.FavouriteList;
import static com.nitish.gamershub.utils.AppConstants.IntentData;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.palette.graphics.Palette;

import com.nitish.gamershub.databinding.FragmentGameDetailsBinding;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.utils.interfaces.ConfirmationDialogListener2;
import com.nitish.gamershub.R;
import com.nitish.gamershub.model.firebase.GamePlayedStatus;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.model.local.AllGamesItems;
import com.nitish.gamershub.model.local.DialogHelperPojo;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.view.base.BaseFragment;
import com.nitish.gamershub.view.dialogs.DialogListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.paperdb.Paper;

public class GameDetailsFragment extends BaseFragment {


    GameDetailActivity2 parentActivity;


    AllGamesItems allGamesItems;
    ArrayList<AllGamesItems> favouriteArrayList;

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

        favouriteArrayList = new ArrayList<>();
//        allGamesItems = NewAndPopularGamesAdapter.SelectedGameObject;
        allGamesItems = (AllGamesItems) getActivity().getIntent().getSerializableExtra(IntentData);

        ArrayList<AllGamesItems> itemsArrayList = (ArrayList<AllGamesItems>) Paper.book().read(FavouriteList);
        favouriteArrayList = itemsArrayList;
        userProfile = getUserProfileGlobalData();
        gamePlayedStatus = getUserProfileGlobalData().getGamePlayedStatus();

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


                if (checkInFavList(allGamesItems)) {
                    deleteFromFavourite(allGamesItems);
                    binding.favButton.setImageResource(R.drawable.fav_of2);
                }
                // when it is not in favourite then add in favourites
                else {
                    saveToFavourite(allGamesItems);
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

        binding.gameDescTextview.setText(allGamesItems.getDescription());
        binding.gameImageImageVIew.setClipToOutline(true);
        binding.gameNameTextview.setText(allGamesItems.getName());
        if (checkInFavList(allGamesItems)) {
            binding.favButton.setImageResource(R.drawable.fav_on2);

        }
        // when it is not in favourite then add in favourites
        else {
            binding.favButton.setImageResource(R.drawable.fav_of2);

        }

        Picasso.get().load(allGamesItems.getImg_file()).into(binding.gameImageImageVIew, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

//                getDominantGradient(binding.gameImageImageVIew);
//                binding.gameImageContainerRelative.setBackgroundColor(vibrant);
            }

            @Override
            public void onError(Exception e) {

            }

        });

        setGamePlayInstructions();


        binding.playButton.setBackground(AppHelper.setSingleColorRoundBackground("#F0740D", 15.0F));

    }

    @Override
    public void onResume() {
        super.onResume();
        userProfile = getUserProfileGlobalData();
        gamePlayedStatus = getUserProfileGlobalData().getGamePlayedStatus();
        setViews();

    }

    public void setGamePlayInstructions() {
        if (gamePlayedStatus.getGamePlayedToday() >= getGamersHubDataGlobal().gamesData.getDailyGamePlayLimit()) {
            String text = getString(R.string.your_have_reached_the_daily_reward_limit_of) +
                    getGamersHubDataGlobal().getGamesData().getDailyGamePlayLimit() + getString(R.string.games_come_back_tomorrow_to_get_more_rewards);
            binding.gamePlayTimeTextview.setText(text);

        } else {
            int min = getGamersHubDataGlobal().gamesData.getGamePlaySecs() / 60;
            String text = getString(R.string.play_this_game_for) +
                    min + getString(R.string.minutes_to_get_rewarded) +
                    getString(R.string.game_played_reward_left) + gamePlayedStatus.getGamePlayedToday() + "/" + getGamersHubDataGlobal().getGamesData().getDailyGamePlayLimit() + ")";
            binding.gamePlayTimeTextview.setText(text);

        }
    }

    public void saveToFavourite(AllGamesItems allGamesItems) {
        favouriteArrayList.add(allGamesItems);
        Paper.book().write(FavouriteList, favouriteArrayList);
    }

    public void deleteFromFavourite(AllGamesItems allGamesItems) {

        for (int i = 0; i < favouriteArrayList.size(); i++)
            if (favouriteArrayList.get(i).getGameUrl().equals(allGamesItems.getGameUrl())) {

                favouriteArrayList.remove(i);
                break;
            }

        Paper.book().write(FavouriteList, favouriteArrayList);
    }

    public boolean checkInFavList(AllGamesItems allGamesItems) {

        boolean isInFavList = false;

        for (int i = 0; i < favouriteArrayList.size(); i++) {
            if (favouriteArrayList.get(i).getGameUrl().equals(allGamesItems.getGameUrl())) {
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
                String subject = getString(R.string.issue_in_the_game) + allGamesItems.getName();
                Uri uri = AppHelper.getMailMessageUri(parentActivity, subject, "");
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

                startActivity(Intent.createChooser(intent, getString(R.string.send_us_email)));
            }

            @Override
            public void onNoClick() {

            }
        });
    }


}