package com.nitish.gamershub.view.homePage.fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.FragmentHomeAllGamesBinding;
import com.nitish.gamershub.view.base.BaseFragment;


public class AllGamesFragment extends BaseFragment {




    FragmentHomeAllGamesBinding fragmentHomeAllGamesBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentHomeAllGamesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_all_games, container, false);
        return fragmentHomeAllGamesBinding.getRoot();
    }
}