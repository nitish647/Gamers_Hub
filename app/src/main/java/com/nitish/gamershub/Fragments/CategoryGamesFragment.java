package com.nitish.gamershub.Fragments;

import static com.nitish.gamershub.Utils.ConstantsHelper.populateCategoryList;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.api.LogDescriptor;
import com.nitish.gamershub.Adapters.CategoryGamesAdapter;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.FragmentCategoryGamesBinding;


public class CategoryGamesFragment extends Fragment {

    FragmentCategoryGamesBinding fragmentCategoryGamesBinding;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private CategoryGamesAdapter categoryGamesAdapter;

    public static CategoryGamesFragment newInstance(String param1, String param2) {
        CategoryGamesFragment fragment = new CategoryGamesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        populateCategoryList();

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentCategoryGamesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_games, container, false);

        fragmentCategoryGamesBinding.categoryGamesRecyclerView.setLayoutManager(new LinearLayoutManager(fragmentCategoryGamesBinding.getRoot().getContext(), LinearLayoutManager.VERTICAL, false));
        categoryGamesAdapter = new CategoryGamesAdapter(fragmentCategoryGamesBinding.getRoot().getContext());
        fragmentCategoryGamesBinding.categoryGamesRecyclerView.setAdapter(categoryGamesAdapter);

        return fragmentCategoryGamesBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(categoryGamesAdapter != null) {
            categoryGamesAdapter.changedCategoryGameList();
        }

    }

}