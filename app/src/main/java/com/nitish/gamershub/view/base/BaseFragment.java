package com.nitish.gamershub.view.base;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.PreferenceHelper;


public class BaseFragment extends Fragment {


    public BaseFragment() {
        // Required empty public constructor
    }

    BaseActivity baseActivity;

    public static BaseFragment newInstance(String param1, String param2) {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        baseActivity = (BaseActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    public void hideLoader() {
        if (baseActivity != null) {
            baseActivity.hideLoader();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        baseActivity.printCurrentScreenName(false, this.getClass().getSimpleName());
    }

    public void showLoader() {
        if (baseActivity != null) {
            baseActivity.showLoader();
        }
    }

    public PreferenceHelper getPreferencesMain() {
        return baseActivity.getPreferencesMain();
    }

}