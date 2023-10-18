package com.nitish.gamershub.view.homePage.fragment;

import static com.nitish.gamershub.utils.AppHelper.getUserProfileGlobalData;
import static com.nitish.gamershub.utils.AppConstants.UserInfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.nitish.gamershub.view.base.BaseFragment;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.view.rewards.activity.FaqActivity;
import com.nitish.gamershub.view.rewards.activity.RewardsActivity;
import com.nitish.gamershub.utils.interfaces.ConfirmationDialogListener2;
import com.nitish.gamershub.model.local.DialogHelperPojo;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends BaseFragment {





    FragmentProfileBinding binding;

    FirebaseAuth firebaseAuth;
    UserProfile userProfile ;
    HomeActivity parentHomeActivity;
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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false);

        parentHomeActivity = (HomeActivity) binding.getRoot().getContext();

        firebaseAuth = FirebaseAuth.getInstance();
        Paper.init(parentHomeActivity);
        userProfile = Paper.book().read(UserInfo);

        setViews();
        setonClickListeners();

        return binding.getRoot();
    }


    public FragmentProfileBinding getFragmentProfileBinding()
    {
        return binding;
    }

    @Override
    public void onResume() {


        binding.redeemCoinsTextview.setText( getUserProfileGlobalData().profileData.getGameCoins() +" coins");

        super.onResume();
    }

    public void setViews()
    {


            binding.profileName.setText(AppHelper.getGoogleSignInAccountUser().getDisplayName());
            if(AppHelper.getGoogleSignInUserProfile()!=null)
                if(!(AppHelper.getGoogleSignInUserProfile()+"").equals("null"))
                    Picasso.get().load(AppHelper.getGoogleSignInUserProfile()).placeholder(R.drawable.gamers_hub_icon15).into(binding.profileIcon);


            // TestCode: Anuraag
            // UserProfile user= null;
            // binding.redeemCoinsTextview.setText( user.getProfileData().getGameCoins() +" coins");
        binding.redeemCoinsTextview.setText( getUserProfileGlobalData().getProfileData().getGameCoins() +" coins");

    }
    public void setonClickListeners()
    {


        binding.rewardsRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parentHomeActivity, RewardsActivity.class);
                startActivity(intent);
            }
        });

//        binding.rewardsRelative.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                Toast.makeText(parentHomeActivity, "clicked", Toast.LENGTH_SHORT).show();
//                AppHelper.readCalenderData();
//                return false;
//            }
//        });

        binding.logOutRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentHomeActivity.showLogOutDialog();
            }
        });


        binding.rateUsRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                parentHomeActivity.openPlayStore();

            }
        });
        binding.faqRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parentHomeActivity, FaqActivity.class);
                startActivity(intent);
            }
        });
        binding.privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentHomeActivity.showWebviewDialog();
            }
        });

        binding.contactUsRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showContactConfirmDialog();

            }
        });




    }
    public void showContactConfirmDialog()
    {
        DialogHelperPojo dialogHelperPojo = new DialogHelperPojo();
        dialogHelperPojo.setYesButton("Send");
        dialogHelperPojo.setTitle("Confirmation");
        dialogHelperPojo.setMessage("You can contact us on or email <b>"+getString(R.string.contact_mail)+ "</b> in case of any doubt or issue. We will try to reach you out as soon as we can.");
        parentHomeActivity.getConfirmationDialog(dialogHelperPojo, new ConfirmationDialogListener2() {
            @Override
            public void onYesClick() {
                UserProfile.ProfileData profileData = getUserProfileGlobalData().getProfileData();

                String body = "Hi I am  "+profileData.getName()+", my user id is "+profileData.getEmail()+" \n I have a doubt regarding ...";

                Uri uri =  AppHelper.getMailMessageUri(parentHomeActivity,"",body);
                Intent intent = new Intent(Intent.ACTION_SENDTO,uri);

                startActivity(Intent.createChooser(intent,"Send us email "));
            }

            @Override
            public void onNoClick() {

            }
        });
    }



}