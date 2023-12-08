package com.nitish.gamershub.view.homePage.fragment;

import static com.nitish.gamershub.utils.AppConstants.CategoryBest;
import static com.nitish.gamershub.utils.AppConstants.CategoryFavourites;
import static com.nitish.gamershub.utils.AppConstants.CategoryNew;
import static com.nitish.gamershub.utils.AppConstants.getCategoryList;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nitish.gamershub.databinding.FragmentCategoryGamesBinding;
import com.nitish.gamershub.model.gamersHubMaterData.AllGamesResponseItem;
import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;
import com.nitish.gamershub.model.local.CategoryItem;
import com.nitish.gamershub.utils.AppConstants;
import com.nitish.gamershub.view.base.BaseActivity;
import com.nitish.gamershub.view.base.BaseFragment;
import com.nitish.gamershub.view.homePage.activity.CategoryActivity;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.view.homePage.adapter.CategoryGamesAdapter;
import com.nitish.gamershub.R;

import java.util.ArrayList;


public class CategoryGamesFragment extends BaseFragment {

    FragmentCategoryGamesBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private CategoryGamesAdapter categoryGamesAdapter;

    private HomeActivity homeActivity;
    ArrayList<CategoryItem> categoryItemArrayList = new ArrayList<>();

    public static String Tag = CategoryGamesFragment.class.getSimpleName();

    public static CategoryGamesFragment newInstance(String param1, String param2) {
        CategoryGamesFragment fragment = new CategoryGamesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        getCategoryList();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_games, container, false);
        homeActivity = (HomeActivity) getActivity();
        setRecyclerviewAdapter();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (categoryGamesAdapter != null) {
            categoryGamesAdapter.changedCategoryGameList();
        }

    }

    private ArrayList<CategoryItem> geUpdatedCategoryList() {
        ArrayList<CategoryItem> categoryItemArrayList = getCategoryList();
        for (CategoryItem categoryItem : categoryItemArrayList) {
            categoryItem.setCategoryGameList(getCategoryGameList(categoryItem.getName()));
        }
        return categoryItemArrayList;
    }

    private void setRecyclerviewAdapter() {
        binding.categoryGamesRecyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.VERTICAL, false));
        categoryGamesAdapter = new CategoryGamesAdapter(requireContext(), geUpdatedCategoryList(), new CategoryGamesAdapter.CategoryGamesAdapterListener() {
            @Override
            public void onGameCategoryClick(CategoryItem categoryItem) {

                Intent intent = new Intent(requireActivity(), CategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(AppConstants.BundleData, categoryItem);

                intent.putExtra(AppConstants.From, Tag);
                intent.putExtra(AppConstants.IntentData, bundle);
                homeActivity.startActivity(intent);


            }

            @Override
            public void onGameClicked(GamesItems gamesItems) {
                homeActivity.startIntent(gamesItems);
            }
        });
        binding.categoryGamesRecyclerView.setAdapter(categoryGamesAdapter);

    }

    public ArrayList<GamesItems> getCategoryGameList(String categoryTitle) {

        AllGamesResponseItem allGamesResponseItem = getPreferencesMain().getAllGamesResponseMaterData();
        switch (categoryTitle) {
            case CategoryFavourites:
                return new ArrayList<>(); //Paper.book().read(FavouriteList);
            case CategoryNew:
                return new ArrayList<>(allGamesResponseItem.getNewGamesList());//Paper.book().read(NewGamesList);
            case CategoryBest:
                return new ArrayList<>(allGamesResponseItem.getBestGamesList());//Paper.book().read(PopularGamesList);
            default:
                ArrayList<GamesItems> mainGamesList = new ArrayList<>(allGamesResponseItem.getMainGamesList()); //Paper.book().read(MainGamesList);
                ArrayList<GamesItems> categoryGamesList = new ArrayList<>();
                for (int i = 0; i < mainGamesList.size(); i++) {
                    if (categoryTitle.toLowerCase().contains(mainGamesList.get(i).getCategory().toLowerCase())) {
                        categoryGamesList.add(mainGamesList.get(i));
                    }
                }
                return categoryGamesList;
        }
    }


}