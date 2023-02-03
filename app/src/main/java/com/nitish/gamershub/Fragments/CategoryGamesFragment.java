package com.nitish.gamershub.Fragments;

import static com.nitish.gamershub.Utils.ConstantsHelper.populateCategoryList;

import android.os.Bundle;

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


public class CategoryGamesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView categoryGamesRecyclerView;
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
        View view = inflater.inflate(R.layout.fragment_category_games, container, false);

        categoryGamesRecyclerView = view.findViewById(R.id.categoryGamesRecyclerView);
        categoryGamesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        categoryGamesAdapter = new CategoryGamesAdapter(view.getContext());
        categoryGamesRecyclerView.setAdapter(categoryGamesAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(categoryGamesAdapter != null) {
            categoryGamesAdapter.changedCategoryGameList();
        }

    }

}