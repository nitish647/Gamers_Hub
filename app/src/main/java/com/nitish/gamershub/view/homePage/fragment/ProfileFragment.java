package com.nitish.gamershub.view.homePage.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.nitish.gamershub.model.firebase.profileData.ProfileData;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.model.local.GlideData;
import com.nitish.gamershub.model.local.ProfileListItems;
import com.nitish.gamershub.utils.GlideHelper;
import com.nitish.gamershub.view.base.BaseFragment;
import com.nitish.gamershub.view.dialogs.DialogListener;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.view.homePage.adapter.ProfileListAdapter;
import com.nitish.gamershub.view.rewards.activity.FaqActivity;
import com.nitish.gamershub.view.rewards.activity.RewardsActivity;
import com.nitish.gamershub.model.firebase.userProfile.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.databinding.FragmentProfileBinding;


import java.util.ArrayList;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends BaseFragment {


    FragmentProfileBinding binding;

    FirebaseAuth firebaseAuth;
    UserProfile userProfile;
    HomeActivity parentHomeActivity;
    ProfileListAdapter profileListAdapter;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        parentHomeActivity = (HomeActivity) binding.getRoot().getContext();

        firebaseAuth = FirebaseAuth.getInstance();
        Paper.init(parentHomeActivity);
        userProfile = getPreferencesMain().getUserProfile();

        setViews();
        setOnClickListeners();

        return binding.getRoot();
    }


    public FragmentProfileBinding getFragmentProfileBinding() {
        return binding;
    }

    @Override
    public void onResume() {


        binding.redeemCoinsTextview.setText(getPreferencesMain().getUserProfile().getProfileData().getGameCoins() + " coins");

        super.onResume();
    }

    public void setViews() {


        GoogleSignInAccount googleSignInAccount = getPreferencesMain().getGoogleSignInAccountUser();

        if (googleSignInAccount != null)
            if (!(googleSignInAccount + "").equals("null")) {
                binding.profileName.setText(googleSignInAccount.getDisplayName());

                GlideData glideData = new GlideData();
                glideData.setImageUrl(googleSignInAccount.getPhotoUrl().toString());
                glideData.setPlaceHolder(R.drawable.gamers_hub_icon15);
                GlideHelper.loadGlideImage(binding.profileIcon, glideData, null);

            }

        setProfileListRecyclerview();

        binding.redeemCoinsTextview.setText(getPreferencesMain().getUserProfile().getProfileData().getGameCoins() + " coins");

    }


    private ArrayList<ProfileListItems> getProfileItemList() {
        ArrayList<ProfileListItems> profileListItemsArrayList = new ArrayList<>();
        profileListItemsArrayList.add(new ProfileListItems(getString(R.string.rewards), R.drawable.ic_money_bag));
        profileListItemsArrayList.add(new ProfileListItems(getString(R.string.privacy_policy), R.drawable.ic_privacy_policy));
        profileListItemsArrayList.add(new ProfileListItems(getString(R.string.about_us), R.drawable.about_us_2));
        profileListItemsArrayList.add(new ProfileListItems(getString(R.string.rate_us), R.drawable.ic_google_play));
        profileListItemsArrayList.add(new ProfileListItems(getString(R.string.faq), R.drawable.ic_faq));
        profileListItemsArrayList.add(new ProfileListItems(getString(R.string.contact_us), R.drawable.ic_contact_us));
        profileListItemsArrayList.add(new ProfileListItems(getString(R.string.log_out), R.drawable.ic_logout));

        return profileListItemsArrayList;
    }

    private void setProfileListRecyclerview() {
        profileListAdapter = new ProfileListAdapter(requireContext(), getProfileItemList(), new ProfileListAdapter.ProfileListListener() {
            @Override
            public void onItemClick(ProfileListItems profileListItems) {
                handleLisItemClick(profileListItems);
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(profileListAdapter);
    }

    private void handleLisItemClick(ProfileListItems profileListItems) {

        Intent intent;
        if (profileListItems.getTitle().equals(getString(R.string.rewards))) {
            intent = new Intent(parentHomeActivity, RewardsActivity.class);
            startActivity(intent);
        }
        if (profileListItems.getTitle().equals(getString(R.string.privacy_policy))) {
            parentHomeActivity.showPrivacyPolicyDialog();
        }
        if (profileListItems.getTitle().equals(getString(R.string.about_us))) {
            parentHomeActivity.showAboutUsDialog();
        }
        if (profileListItems.getTitle().equals(getString(R.string.rate_us))) {
            parentHomeActivity.openPlayStore();
        }
        if (profileListItems.getTitle().equals(getString(R.string.faq))) {
            intent = new Intent(parentHomeActivity, FaqActivity.class);
            startActivity(intent);
        }
        if (profileListItems.getTitle().equals(getString(R.string.contact_us))) {
            showContactConfirmDialog();
        }
        if (profileListItems.getTitle().equals(getString(R.string.log_out))) {
            parentHomeActivity.showLogOutDialog();

        }


    }

    public void setOnClickListeners() {


    }

    public void showContactConfirmDialog() {

        DialogItems dialogItems = new DialogItems();
        dialogItems.setYesTitle("Send");
        dialogItems.setTitle("Confirmation");
        dialogItems.setMessage("You can contact us on or email <b>" + getString(R.string.contact_mail) + "</b> in case of any doubt or issue. We will try to reach you out as soon as we can.");


        parentHomeActivity.showConfirmationDialog2(dialogItems, new DialogListener() {
            @Override
            public void onYesClick() {
                ProfileData profileData = getPreferencesMain().getUserProfile().getProfileData();

                String body = "Hi I am  " + profileData.getName() + ", my user id is " + profileData.getEmail() + " \n I have a doubt regarding ...";

                Uri uri = AppHelper.getMailMessageUri(parentHomeActivity, userProfile, "", body);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

                startActivity(Intent.createChooser(intent, "Send us email "));
            }

            @Override
            public void onNoClick() {

            }
        });

    }


}