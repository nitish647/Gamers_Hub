package com.nitish.gamershub.view.homePage.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.nitish.gamershub.databinding.FragmentHomeBinding;
import com.nitish.gamershub.model.bannerImagesData.BannerImagesItem;
import com.nitish.gamershub.model.bannerImagesData.ResponseBannerImages;
import com.nitish.gamershub.model.gamersHubMaterData.AllGamesResponseItem;
import com.nitish.gamershub.model.local.CategoryItem;
import com.nitish.gamershub.utils.AppConstants;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.view.base.BaseFragment;
import com.nitish.gamershub.view.homePage.activity.CategoryActivity;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.view.homePage.adapter.HomeGamesAdapter;
import com.nitish.gamershub.view.homePage.adapter.GameListAdapter;
import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;
import com.nitish.gamershub.R;
import com.nitish.gamershub.view.loginSingup.viewmodelRepo.LoginSignUpViewModel;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment {


    FragmentHomeBinding binding;

    private LoginSignUpViewModel viewModel;


    ArrayList<GamesItems> mainGamesItemArrayList = new ArrayList<>();
    HomeActivity homeActivity;

    View view;

    ArrayList<CategoryItem> categoryItemArrayList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    public static String Tag = HomeFragment.class.getSimpleName();

    HomeActivity parentHomeActivity;

    ArrayList<GamesItems> recentlyPlayedList = new ArrayList<>();
    ArrayList<GamesItems> favouriteGamesList = new ArrayList<>();
    HomeGamesAdapter homeGamesAdapter;


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        homeActivity = (HomeActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        view = binding.getRoot();
        Paper.init(view.getContext());
        parentHomeActivity = (HomeActivity) (getActivity());

        // Initialize Firebase Auth
        viewModel = ViewModelProviders.of(this).get(LoginSignUpViewModel.class);

        setViews();

        setOnClickListens();
        bindObservers();

        callGetBannerData();
//        callGetGamersHubMaterData();

        return view;

    }

    public void bindObservers() {

        viewModel.getGamersHubMaterData.observe(getViewLifecycleOwner(), new Observer<NetworkResponse<AllGamesResponseItem>>() {
            @Override
            public void onChanged(NetworkResponse<AllGamesResponseItem> response) {
                if (response instanceof NetworkResponse.Success) {


                    hideLoader();

                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<AllGamesResponseItem>) response).getMessage();

                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

//                    showLoader();
                }
            }
        });

        viewModel.getBannerData.observe(getViewLifecycleOwner(), new Observer<NetworkResponse<ResponseBannerImages>>() {
            @Override
            public void onChanged(NetworkResponse<ResponseBannerImages> response) {
                if (response instanceof NetworkResponse.Success) {


//                    hideLoader();

                    ResponseBannerImages responseBannerImages = ((NetworkResponse.Success<ResponseBannerImages>) response).getData();

//                    setImageSlider(responseBannerImages.getBannerImages());


                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<ResponseBannerImages>) response).getMessage();

//                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

//                    showLoader();
                }
            }
        });

    }


    public void setOnClickListens() {

    }


    public void setImageSlider(List<BannerImagesItem> getBannerImages) {

        ArrayList<String> imageUrlList = new ArrayList<>();
        for (int i = 0; i < getBannerImages.size(); i++) {


            imageUrlList.add(getBannerImages.get(i).getLargeImageUrl());

        }
        ArrayList<SlideModel> slideModelArrayList = new ArrayList<>();
        for (String imageUri : imageUrlList) {
            SlideModel slideModel = new SlideModel(imageUri, null, null);
            slideModelArrayList.add(slideModel);

        }
        binding.imageSlider.setImageList(slideModelArrayList, ScaleTypes.FIT);
    }

    // will set the main games data


    @Override
    public void onPause() {

        super.onPause();
    }


    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {

        super.onDestroy();
    }


    private void callGetGamersHubMaterData() {


        viewModel.callGetGamersHubJson(requireContext());
    }

    private void callGetBannerData() {
        viewModel.callGetBannerData(requireContext());
    }


    public void setViews() {
        mainGamesItemArrayList = new ArrayList<>(getPreferencesMain().getAllGamesResponseMaterData().getMainGamesList());
        binding.imageSlider.setClipToOutline(true);
        getCategory();
        setUpRecyclerViews();
    }

    public GameListAdapter.GameListAdapterInterface gameAdapterInterface = new GameListAdapter.GameListAdapterInterface() {
        @Override
        public void onClick(GamesItems gamesItems) {

            ((HomeActivity) getActivity()).startIntent(gamesItems);
        }
    };


    private void setUpRecyclerViews() {

//        setUpFavouritesRecycler();
//        setUpRecentlyPlayedRecycler();
//        setUpMainRecyclerView();


        homeGamesAdapter = new HomeGamesAdapter(requireContext(), Tag, categoryItemArrayList, new HomeGamesAdapter.HomeGamesAdapterListener() {
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

        binding.recyclerView.setAdapter(homeGamesAdapter);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

    }

    @Override
    public void onResume() {
        super.onResume();
        updateViews();
    }

    private void updateViews() {

        favouriteGamesList = getPreferencesMain().getSavedFavouriteList();
        recentlyPlayedList = getPreferencesMain().getRecentlyPlayedList();

        getCategory();


        homeGamesAdapter.updateList(categoryItemArrayList);


    }


    private void getCategory() {
        categoryItemArrayList.clear();
        categoryItemArrayList.add(getFavoritesCategory());
        categoryItemArrayList.add(getRecentlyPlayedCategory());

        categoryItemArrayList.add(getAllGameCategory());

    }

    private CategoryItem getRecentlyPlayedCategory() {
        CategoryItem categoryItem = new CategoryItem(getString(R.string.recently_played), 0);
        categoryItem.setCategoryGameList(recentlyPlayedList);
        categoryItem.setNoGameBannerTitle(getString(R.string.no_recently_played_game));
        return categoryItem;
    }

    private CategoryItem getFavoritesCategory() {
        CategoryItem categoryItem = new CategoryItem(getString(R.string.favourites), 0);
        categoryItem.setCategoryGameList(favouriteGamesList);
        categoryItem.setNoGameBannerTitle(getString(R.string.no_favourites_yet));
        return categoryItem;
    }

    private CategoryItem getAllGameCategory() {
        CategoryItem categoryItem = new CategoryItem(getString(R.string.all_games), 0);
        categoryItem.setCategoryGameList(mainGamesItemArrayList);
        return categoryItem;
    }

}