package com.nitish.gamershub.Fragments;

import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHub_ParentCollection;
import static com.nitish.gamershub.Utils.ConstantsHelper.GeneralRewardCoins;
import static com.nitish.gamershub.Utils.ConstantsHelper.MainGamesList;
import static com.nitish.gamershub.Utils.ConstantsHelper.NewGamesList;
import static com.nitish.gamershub.Utils.ConstantsHelper.PopularGamesList;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.nitish.gamershub.Activities.HomeActivity;
import com.nitish.gamershub.Activities.LoginPage;
import com.nitish.gamershub.Activities.Splash_Screen;
import com.nitish.gamershub.Adapters.NewAndPopularGamesAdapter;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.Pojo.Categories;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.NotificationHelper;
import com.nitish.gamershub.Utils.ProgressBarHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    
    
    
    
    
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


    int currentSelectedFragPosition=0;



    HomeActivity parentHomeActivity;
    SearchView searchView;
    RequestQueue requestQueue;
    List<Categories> categoriesList;
    RecyclerView categoriesRecycler;
    Button navigationButton;

    DrawerLayout drawerLayout;
    LinearLayout linearAdContainer;

    NewAndPopularGamesAdapter newAndPopularGamesAdapter;
    AdView googleBannerAdView;
    boolean interstitialAdDismissed = false;

    private InterstitialAd interstitialAd;

    Button logoutButton;

    private RewardedAd rewardedAd;
    boolean isLoading;
    RecyclerView allGamesRecyclerView;
    ArrayList<AllGamesItems> mainGamesArrayList;
    ImageSlider imageSlider;
    ProgressDialog progressDialog;


    // firebase auth
    private FirebaseAuth mAuth;

    FirebaseFirestore firestoreDb;

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
         view = inflater.inflate(R.layout.fragment_home, container, false);
        Paper.init(view.getContext());
        parentHomeActivity = (HomeActivity) (getActivity());

        requestQueue = Volley.newRequestQueue(view.getContext());
        allGamesRecyclerView = view.findViewById(R.id.allGamesRecyclerView);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        firestoreDb = FirebaseFirestore.getInstance();
        logoutButton= view.findViewById(R.id.logoutButton);
        searchView = view.findViewById(R.id.searchView);
        progressDialog = ProgressBarHelper.setProgressBarDialog(view.getContext());
        drawerLayout = parentHomeActivity.findViewById(R.id.drawerLayout);
        imageSlider = view.findViewById(R.id.imageSlider);

        navigationButton =view.findViewById(R.id.navigationButton);

        categoriesRecycler = view.findViewById(R.id.categoriesRecycler);
        googleBannerAdView = view.findViewById(R.id.googleBannerAdView);
        setViews();
        categoriesList = new ArrayList<>();
        mainGamesArrayList = new ArrayList<>();

        FirebaseApp.initializeApp(view.getContext());
        NotificationHelper.generateFcmToken(view.getContext());
        setOnClickListens();
        getGamersHubData();
        setSearchView();
        bannerImagesApi();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(),4);
        allGamesRecyclerView.setLayoutManager(gridLayoutManager);


        try {
            masterDataJsonObject = new JSONObject(Paper.book().read(Splash_Screen.MaterData)+"");
            JSONArray mainGameJsonArray = masterDataJsonObject.getJSONArray("main");
            JSONArray popularGamesJsonArray = masterDataJsonObject.getJSONArray("best");
            JSONArray newGamesJsonArray =  masterDataJsonObject.getJSONArray("new");

            // will save the all games in paper in arraylist form
            saveAllGamesData(mainGameJsonArray,popularGamesJsonArray,newGamesJsonArray);
            //       setUpViewPager(allGamesJsonArrayString,popularGamesJsonArrayString,newGamesJsonArrayString);


        } catch (Exception e) {
            Toast.makeText(view.getContext(), "Some error has occurred : gError223", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return view;
        
    }

    public void setOnClickListens()
    {
        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }

        });
    }


    public void getGamersHubData()
    {

        firestoreDb.collection("Gamers Hub Data").document("rewardCoins"+"").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {

                        HashMap<String,Object> hashMap=   (HashMap<String, Object>) documentSnapshot.getData();

                        HashMap<String,Object> rewardCoinsHashmap=   (HashMap<String, Object>) hashMap.get("rewardCoinsList");

                        Object generalCoins =  rewardCoinsHashmap.get(rewardCoinsHashmap.keySet().toArray()[0]);

                        int intGeneralCoins  = Integer.parseInt( generalCoins+"");
                        Paper.book().write(GeneralRewardCoins,generalCoins);


                    }
                    else {
                        Toast.makeText(view.getContext(), "document does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(view.getContext(), "couldn't get the documents ", Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void setUpViewPager(String allGamesListJsonArray, String popularGamesJsonArray, String newGamesJsonArray)
    {
        //sending the titles in bundle
        Bundle bundle = new Bundle();
        Bundle bundle2 = new Bundle();
        //   Bundle bundle3 = new Bundle();

        bundle.putString("data", allGamesListJsonArray);
        bundle.putString("title","All Games");
        bundle.putString("masterData",Paper.book().read(Splash_Screen.MaterData)+"");

        bundle2.putString("data", newGamesJsonArray);
        bundle2.putString("title","Favourites");
        bundle2.putString("masterData",Paper.book().read(Splash_Screen.MaterData)+"");

//
//        bundle3.putString("data", newGamesJsonArray);
//        bundle3.putString("title","Favourites");
//       bundle3.putString("masterData",Paper.book().read(Splash_Screen.MaterData)+"");
//

        // when there is no fragment added in viewpager adapter then only add the fragment


    }



    // post method
    public void parseAllGamesData( JSONArray allGamesJsonArray)
    {


        try {
            JSONObject masterObject =  allGamesJsonArray.getJSONObject(0);

            JSONArray mainObject  = masterObject.getJSONArray("main");




        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
    public void setSearchView()
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        for(int i =0;i<bannerImagesJsonArray.length();i++)
        {
            JSONObject jsonObject = bannerImagesJsonArray.getJSONObject(i);

            imageUrlList.add(jsonObject.getString("largeImageUrl"));

        }
        ArrayList<SlideModel> slideModelArrayList = new ArrayList<>();
        for(String imageUri : imageUrlList)
        {
            SlideModel slideModel = new SlideModel(imageUri,null,null);
            slideModelArrayList.add(slideModel);

        }
        imageSlider.setImageList(slideModelArrayList, ScaleTypes.FIT);
    }

    // will set the main games data
    public void saveAllGamesData(JSONArray mainGamesArray, JSONArray newGamesJsonArray, JSONArray popularGamesJsonArray)
    {
        ArrayList<AllGamesItems> mainItemsArrayList = new ArrayList<>();
        ArrayList<AllGamesItems> newGamesItemsArrayList = new ArrayList<>();
        ArrayList<AllGamesItems> popularItemsArrayList = new ArrayList<>();
        Gson gson = new Gson();
        try {
            for(int i = 0; i< mainGamesArray.length(); i++)
            {
                JSONObject jsonObject = mainGamesArray.getJSONObject(i);
                AllGamesItems allGamesItems = gson.fromJson(jsonObject.toString(),AllGamesItems.class);
                mainItemsArrayList.add(allGamesItems);
            }

            newAndPopularGamesAdapter = new NewAndPopularGamesAdapter(view.getContext(),mainItemsArrayList);
            allGamesRecyclerView.setAdapter(newAndPopularGamesAdapter);

            Paper.book().write(MainGamesList,mainItemsArrayList);

            for(int i = 0; i< newGamesJsonArray.length(); i++)
            {
                JSONObject jsonObject = newGamesJsonArray.getJSONObject(i);
                AllGamesItems allGamesItems = gson.fromJson(jsonObject.toString(),AllGamesItems.class);
                newGamesItemsArrayList.add(allGamesItems);
            }
            Paper.book().write(NewGamesList,newGamesItemsArrayList);


            for(int i = 0; i< popularGamesJsonArray.length(); i++)
            {
                JSONObject jsonObject = popularGamesJsonArray.getJSONObject(i);
                AllGamesItems allGamesItems = gson.fromJson(jsonObject.toString(),AllGamesItems.class);
                popularItemsArrayList.add(allGamesItems);
            }
            Paper.book().write(PopularGamesList,popularItemsArrayList);

            if(popularGamesJsonArray.length()>0)
                Paper.book().read(PopularGamesList,popularItemsArrayList.get(0).getName());
        } catch (Exception e) {
            Toast.makeText(view.getContext(), "something went wrong while showing this data : error334 "+e, Toast.LENGTH_SHORT).show();
            Log.d("gError334",e.toString());
            e.printStackTrace();
        }
    }

    public void bannerImagesApi()
    {

        String url = getString(R.string.dbGitUrl)+"gamers_hub_data/banner_Images.json";

        Log.d("url",url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {

                Log.d("gResponse","response banner_Images data "+response);

                //    Toast.makeText(Splash_Screen.this, response+"", Toast.LENGTH_SHORT).show();
                try {
                    JSONArray bannerImagesJsonArray =  response.getJSONArray("bannerImages");
                    setImageSlider(bannerImagesJsonArray);

                } catch (Exception e) {
                    Log.d("gError","error in  banner_Images data "+e.toString());
                    e.printStackTrace();
                }







            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("gError","error in banner_Images data "+error);

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }
    public void setUpBannerAd()
    {

        AdRequest adRequest = new AdRequest.Builder().build();
        googleBannerAdView.loadAd(adRequest);

    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        UserOperations.getFirestoreUser().addSnapshotListener(parentHomeActivity, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                if(error!=null)
//                {
//                    Toast.makeText(view.getContext(), "error while getting doc 211", Toast.LENGTH_SHORT).show();
//                }
//                if(value!=null && value.exists())
//                {
//                    UserProfile  userProfile=   value.toObject(UserProfile.class);
//
//                    UserProfile.ProfileData profileData = userProfile.profileData;
//
//                    totalPointsTextview.setText(profileData.gameCoins +"");
//                }
//            }
//        });
//
//    }

    @Override
    public void onPause() {

        if (googleBannerAdView != null) {
            googleBannerAdView.pause();
        }
        super.onPause();
    }


    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (googleBannerAdView != null) {
            googleBannerAdView.destroy();
        }
        super.onDestroy();
    }




    public void logOutDialog()
    {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());
        android.app.AlertDialog deleteDialog = builder.create();


        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                progressDialog.show();

                AuthUI.getInstance()
                        .signOut(view.getContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                progressDialog.dismiss();
                                startActivity(new Intent(view.getContext(), LoginPage.class));
                                parentHomeActivity.finish();

                            }
                        });


            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();

    }
    public void setHeader()
    {


        TextView profileName = view.findViewById(R.id.profileName);
        CircleImageView profileIcon =view.findViewById(R.id.profileIcon);

        //   profileName.setText("hahahhaha");

        if(mAuth!=null &&  mAuth.getCurrentUser()!=null) {


            profileName.setText(mAuth.getCurrentUser().getDisplayName());
            Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).into(profileIcon);
        }
        else {
            Toast.makeText(view.getContext(), "current user is null", Toast.LENGTH_SHORT).show();
        }





    }
    public void setViews()
    {
        bottomNavigationView= view.findViewById(R.id.bottomNavigationView);
    }
    public void copyCollection()
    {

        firestoreDb.collection(GamersHub_ParentCollection).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<DocumentSnapshot> documentSnapshotList =  task.getResult().getDocuments();

                    firestoreDb.collection(GamersHub_ParentCollection+"2");
                }
            }
        });


    }
}