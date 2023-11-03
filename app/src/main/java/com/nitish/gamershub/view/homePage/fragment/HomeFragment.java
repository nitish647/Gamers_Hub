package com.nitish.gamershub.view.homePage.fragment;

import static com.nitish.gamershub.utils.AppConstants.MainGamesList;
import static com.nitish.gamershub.utils.AppConstants.NewGamesList;
import static com.nitish.gamershub.utils.AppConstants.PopularGamesList;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.nitish.gamershub.databinding.FragmentHomeBinding;
import com.nitish.gamershub.model.firebase.TimerStatus;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.view.base.BaseFragment;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.view.loginSingup.activity.SplashScreen;
import com.nitish.gamershub.view.homePage.adapter.NewAndPopularGamesAdapter;
import com.nitish.gamershub.model.local.AllGamesItems;
import com.nitish.gamershub.model.local.Categories;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.ProgressBarHelper;
import com.nitish.gamershub.view.loginSingup.viewmodelRepo.LoginSignUpViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment {


    FragmentHomeBinding fragmentHomeBinding;

    private LoginSignUpViewModel viewModel;


    View view;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters


    // the whole game data
    JSONObject masterDataJsonObject;


    int currentSelectedFragPosition = 0;


    HomeActivity parentHomeActivity;
    RequestQueue requestQueue;
    List<Categories> categoriesList;
    RecyclerView categoriesRecycler;


    LinearLayout linearAdContainer;

    NewAndPopularGamesAdapter newAndPopularGamesAdapter;
    AdView googleBannerAdView;
    boolean interstitialAdDismissed = false;

    private InterstitialAd interstitialAd;


    private RewardedAd rewardedAd;
    boolean isLoading;
    ArrayList<AllGamesItems> mainGamesArrayList;

    ProgressDialog progressDialog;


    BottomNavigationView bottomNavigationView;


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        view = fragmentHomeBinding.getRoot();
        Paper.init(view.getContext());
        parentHomeActivity = (HomeActivity) (getActivity());

        requestQueue = Volley.newRequestQueue(view.getContext());
        // Initialize Firebase Auth
        viewModel = ViewModelProviders.of(this).get(LoginSignUpViewModel.class);

        progressDialog = ProgressBarHelper.setProgressBarDialog(view.getContext());

        categoriesRecycler = view.findViewById(R.id.categoriesRecycler);
        googleBannerAdView = view.findViewById(R.id.googleBannerAdView);
        setViews();
        categoriesList = new ArrayList<>();
        mainGamesArrayList = new ArrayList<>();

        setOnClickListens();
        bindObservers();
        setSearchView();
//        bannerImagesApi();
        callGetBannerData();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 4);
        fragmentHomeBinding.allGamesRecyclerView.setLayoutManager(gridLayoutManager);


        if (Paper.book().read(SplashScreen.MaterData) == null || !Paper.book().contains(SplashScreen.MaterData)) {
            callGetGamersHubMaterData();
//            getMaterData();
        } else
            saveGameData();

        return view;

    }

    public void bindObservers() {

        viewModel.getGamersHubMaterData.observe(getViewLifecycleOwner(), new Observer<NetworkResponse<JSONArray>>() {
            @Override
            public void onChanged(NetworkResponse<JSONArray> response) {
                if (response instanceof NetworkResponse.Success) {


                    hideLoader();
                    try {
                        JSONObject jsonObject = ((NetworkResponse.Success<JSONArray>) response).getData().getJSONObject(0);
                        Log.d("gResponse", "response jsonObject of mater data " + jsonObject);
                        Paper.book().write(SplashScreen.MaterData, jsonObject + "");

                        saveGameData();

                    } catch (Exception e) {

                    }

//                    Paper.book().write(UserInfo, updatedUserProfile);


                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<JSONArray>) response).getMessage();

                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

                    showLoader();
                }
            }
        });

        viewModel.getBannerData.observe(getViewLifecycleOwner(), new Observer<NetworkResponse<JSONObject>>() {
            @Override
            public void onChanged(NetworkResponse<JSONObject> response) {
                if (response instanceof NetworkResponse.Success) {


                    hideLoader();

                    try {
                        JSONArray bannerImagesJsonArray = ((NetworkResponse.Success<JSONObject>) response).getData().getJSONArray("bannerImages");
                        setImageSlider(bannerImagesJsonArray);

                    } catch (Exception e) {
                        Log.d("gError", "error in  banner_Images data " + e.toString());
                        e.printStackTrace();
                    }
//                    Paper.book().write(UserInfo, updatedUserProfile);


                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<JSONObject>) response).getMessage();

                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

                    showLoader();
                }
            }
        });

    }

    public void saveGameData() {
        try {
            masterDataJsonObject = new JSONObject(Paper.book().read(SplashScreen.MaterData) + "");
            JSONArray mainGameJsonArray = masterDataJsonObject.getJSONArray("main");
            JSONArray popularGamesJsonArray = masterDataJsonObject.getJSONArray("best");
            JSONArray newGamesJsonArray = masterDataJsonObject.getJSONArray("new");

            // will save the all games in paper in arraylist form
            saveAllGamesData(mainGameJsonArray, popularGamesJsonArray, newGamesJsonArray);
            //       setUpViewPager(allGamesJsonArrayString,popularGamesJsonArrayString,newGamesJsonArrayString);


        } catch (Exception e) {
            Log.d("gError", "exception in data 112 " + e);
            Toast.makeText(view.getContext(), "Some error has occurred : gError223", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public void setOnClickListens() {

    }


    public void setSearchView() {
        fragmentHomeBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // searching in all games
                newAndPopularGamesAdapter.searchFilter.filter(newText);
                // searching from favourites

                return false;
            }
        });
    }

    public void setImageSlider(JSONArray bannerImagesJsonArray) throws JSONException {

        ArrayList<String> imageUrlList = new ArrayList<>();
        for (int i = 0; i < bannerImagesJsonArray.length(); i++) {
            JSONObject jsonObject = bannerImagesJsonArray.getJSONObject(i);

            imageUrlList.add(jsonObject.getString("largeImageUrl"));

        }
        ArrayList<SlideModel> slideModelArrayList = new ArrayList<>();
        for (String imageUri : imageUrlList) {
            SlideModel slideModel = new SlideModel(imageUri, null, null);
            slideModelArrayList.add(slideModel);

        }
        fragmentHomeBinding.imageSlider.setImageList(slideModelArrayList, ScaleTypes.FIT);
    }

    // will set the main games data
    public void saveAllGamesData(JSONArray mainGamesArray, JSONArray newGamesJsonArray, JSONArray popularGamesJsonArray) {
        ArrayList<AllGamesItems> mainItemsArrayList = new ArrayList<>();
        ArrayList<AllGamesItems> newGamesItemsArrayList = new ArrayList<>();
        ArrayList<AllGamesItems> popularItemsArrayList = new ArrayList<>();
        Gson gson = new Gson();
        try {
            for (int i = 0; i < mainGamesArray.length(); i++) {
                JSONObject jsonObject = mainGamesArray.getJSONObject(i);
                AllGamesItems allGamesItems = gson.fromJson(jsonObject.toString(), AllGamesItems.class);
                mainItemsArrayList.add(allGamesItems);
            }

            newAndPopularGamesAdapter = new NewAndPopularGamesAdapter(view.getContext(), mainItemsArrayList, new NewAndPopularGamesAdapter.NewAndPopularGameAdapterInterface() {
                @Override
                public void onClick(AllGamesItems allGamesItems) {
                    ((HomeActivity) getActivity()).startIntent(allGamesItems);
                }
            });

            fragmentHomeBinding.allGamesRecyclerView.setAdapter(newAndPopularGamesAdapter);

            Paper.book().write(MainGamesList, mainItemsArrayList);

            for (int i = 0; i < newGamesJsonArray.length(); i++) {
                JSONObject jsonObject = newGamesJsonArray.getJSONObject(i);
                AllGamesItems allGamesItems = gson.fromJson(jsonObject.toString(), AllGamesItems.class);
                newGamesItemsArrayList.add(allGamesItems);
            }
            Paper.book().write(NewGamesList, newGamesItemsArrayList);


            for (int i = 0; i < popularGamesJsonArray.length(); i++) {
                JSONObject jsonObject = popularGamesJsonArray.getJSONObject(i);
                AllGamesItems allGamesItems = gson.fromJson(jsonObject.toString(), AllGamesItems.class);
                popularItemsArrayList.add(allGamesItems);
            }
            Paper.book().write(PopularGamesList, popularItemsArrayList);

            if (popularGamesJsonArray.length() > 0)
                Paper.book().read(PopularGamesList, popularItemsArrayList.get(0).getName());
        } catch (Exception e) {
            Toast.makeText(view.getContext(), "something went wrong while showing this data : error334 " + e, Toast.LENGTH_SHORT).show();
            Log.d("gError334", e.toString());
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {

        if (googleBannerAdView != null) {
            googleBannerAdView.pause();
        }
        super.onPause();
    }


    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (googleBannerAdView != null) {
            googleBannerAdView.destroy();
        }
        super.onDestroy();
    }



    private void callGetGamersHubMaterData() {


        viewModel.callGetGamersHubJson();
    }

    private void callGetBannerData() {


        viewModel.callGetBannerData();
    }


    public void setViews() {
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        fragmentHomeBinding.imageSlider.setClipToOutline(true);
    }

}