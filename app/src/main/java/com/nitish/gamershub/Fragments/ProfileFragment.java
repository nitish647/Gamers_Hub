package com.nitish.gamershub.Fragments;

import static com.nitish.gamershub.Utils.ConstantsHelper.UserInfo;

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
import com.nitish.gamershub.Activities.HomeActivity;
import com.nitish.gamershub.Activities.RewardsActivity;
import com.nitish.gamershub.Pojo.FireBase.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {





    FragmentProfileBinding binding;

    FirebaseAuth firebaseAuth;
    UserProfile userProfile ;
    HomeActivity parentHomeActivity;
    // TODO: Rename parameter arguments, choose names that match
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


        binding.redeemCoinsTextview.setText( parentHomeActivity.getUserProfileGlobalData().profileData.getGameCoins() +" coins");

        super.onResume();
    }

    public void setViews()
    {
        if( parentHomeActivity.getGoogleSignInAccount()!=null) {


            binding.profileName.setText(parentHomeActivity.getGoogleSignInAccount().getDisplayName());
            if(parentHomeActivity.getGoogleSignInAccount().getPhotoUrl()!=null)
                if(!(parentHomeActivity.getGoogleSignInAccount().getPhotoUrl()+"").equals("null"))
                    Picasso.get().load(parentHomeActivity.getGoogleSignInAccount().getPhotoUrl()).into(binding.profileIcon);

        }

        binding.redeemCoinsTextview.setText( parentHomeActivity.getUserProfileGlobalData().profileData.getGameCoins() +" coins");


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

        binding.logOutRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentHomeActivity.logOutDialog();
            }
        });


        binding.rateUsRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent  = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.play_store_link)));
                startActivity(intent);

            }
        });

        binding.privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentHomeActivity.showWebviewDialog();
            }
        });




    }


}