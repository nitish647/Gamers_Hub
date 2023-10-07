package com.nitish.gamershub.view.homePage.fragment;

import static com.nitish.gamershub.utils.AppConstants.populateCategoryList;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nitish.gamershub.databinding.FragmentCategoryGamesBinding;
import com.nitish.gamershub.view.base.BaseFragment;
import com.nitish.gamershub.view.homePage.adapter.CategoryGamesAdapter;
import com.nitish.gamershub.R;


public class CategoryGamesFragment extends BaseFragment {

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