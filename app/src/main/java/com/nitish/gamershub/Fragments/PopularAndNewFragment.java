package com.nitish.gamershub.Fragments;


import static com.nitish.gamershub.Utils.ConstantsHelper.FavouriteList;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;


public class PopularAndNewFragment extends Fragment {


    NewAndPopularGamesAdapter newAndPopularGamesAdapter;
    View view;
    RecyclerView newPopularRecycler;
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
        view=  inflater.inflate(R.layout.fragment_popular_and_new, container, false);
        newPopularRecycler = view.findViewById(R.id.newPopularRecycler);
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
        newAndPopularGamesAdapter = new NewAndPopularGamesAdapter(view.getContext(),allGamesItemsList);
        newPopularRecycler.setAdapter(newAndPopularGamesAdapter);
         newPopularRecycler.setLayoutManager(gridLayoutManager);

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