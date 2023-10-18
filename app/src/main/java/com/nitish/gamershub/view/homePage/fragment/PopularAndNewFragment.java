package com.nitish.gamershub.view.homePage.fragment;


import static com.nitish.gamershub.utils.AppConstants.FavouriteList;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nitish.gamershub.databinding.FragmentPopularAndNewBinding;
import com.nitish.gamershub.view.base.BaseFragment;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.view.homePage.adapter.NewAndPopularGamesAdapter;
import com.nitish.gamershub.model.local.AllGamesItems;
import com.nitish.gamershub.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;


public class PopularAndNewFragment extends BaseFragment {

    FragmentPopularAndNewBinding fragmentPopularAndNewBinding;
    NewAndPopularGamesAdapter newAndPopularGamesAdapter;
    View view;

    String gameDataString;
    String title;
    List<AllGamesItems> allGamesItemsList,favItemList ;
    String gameData="";
    Gson gson ;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentPopularAndNewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_popular_and_new, container, false);
        view = fragmentPopularAndNewBinding.getRoot();

        allGamesItemsList =  new ArrayList<>();
        favItemList = new ArrayList<>();
        gson = new Gson();


        // checking the size of the bundle data
        Parcel parcel = Parcel.obtain();
        parcel.writeValue(getArguments());
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        double  bundleSizeInKb  =((double) bytes.length)/1000;
        Log.d("bundleSizeInKb",bundleSizeInKb+" kb");


        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(),4);
        newAndPopularGamesAdapter = new NewAndPopularGamesAdapter(view.getContext(), allGamesItemsList, new NewAndPopularGamesAdapter.NewAndPopularGameAdapterInterface() {
            @Override
            public void onClick(AllGamesItems allGamesItems) {
                ((HomeActivity) getActivity()).startIntent(allGamesItems);
            }
        });
        fragmentPopularAndNewBinding.newPopularRecycler.setAdapter(newAndPopularGamesAdapter);
        fragmentPopularAndNewBinding.newPopularRecycler.setLayoutManager(gridLayoutManager);

          searchView=   getActivity().findViewById(R.id.searchView);

        if(getArguments()!=null)
        {
            title = getArguments().getString("title");
            gameData  = getArguments().getString("data");


            if(title.toLowerCase().contains("all"))
            {

                setNewPopularRecycler(gameData);
            }
            else {

                setFavouritesRecycler();

            }


        }

        return view;
    }

    @Override
    public void onResume() {
        if(title.toLowerCase().contains("favourites"))
            setFavouritesRecycler();

        super.onResume();
    }


    public void setFavouritesRecycler()
    {

        allGamesItemsList.clear();

           ArrayList<AllGamesItems> favlist2 = Paper.book().read(FavouriteList);

           allGamesItemsList.addAll(favlist2);


            newAndPopularGamesAdapter.notifyDataSetChanged();

    }
    public void setSetSearchFilter(String searchQuery)
    {
        newAndPopularGamesAdapter.searchFilter.filter(searchQuery);

    }
    public void setNewPopularRecycler(String gameData)
    {
        allGamesItemsList.clear();
        try {
            JSONArray newAndPopularDataArray = new JSONArray(gameData);
            for(int i =0 ; i<newAndPopularDataArray.length();i++)
            {
                JSONObject jsonObject = newAndPopularDataArray.getJSONObject(i);
                AllGamesItems allGamesItems = gson.fromJson(jsonObject.toString(),AllGamesItems.class);
                allGamesItemsList.add(allGamesItems);
            }
            newAndPopularGamesAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Toast.makeText(view.getContext(), "something went wrong while showing this data : error334", Toast.LENGTH_SHORT).show();
            Log.d("gError334",e.toString());
            e.printStackTrace();
        }


    }
}