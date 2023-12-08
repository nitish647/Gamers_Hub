package com.nitish.gamershub.view.homePage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.LayoutHomepageGameListBinding;
import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;
import com.nitish.gamershub.model.local.CategoryItem;

import java.util.ArrayList;

import kotlin.Pair;

public class HomeGamesAdapter extends RecyclerView.Adapter<HomeGamesAdapter.HomeGamesViewHolder> {

    private Context context;

    private ArrayList<CategoryItem> categoryItemArrayList;

    private HomeGamesAdapter.HomeGamesAdapterListener gamesAdapterListener;

    String usage = "";
    RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();


    public HomeGamesAdapter(Context context, ArrayList<CategoryItem> categoryItemArrayList, HomeGamesAdapter.HomeGamesAdapterListener gamesAdapterListener) {
        this.context = context;
        this.categoryItemArrayList = categoryItemArrayList;
        this.gamesAdapterListener = gamesAdapterListener;
    }

    public HomeGamesAdapter(Context context, String usage, ArrayList<CategoryItem> categoryItemArrayList, HomeGamesAdapter.HomeGamesAdapterListener gamesAdapterListener) {
        this.context = context;
        this.categoryItemArrayList = categoryItemArrayList;
        this.gamesAdapterListener = gamesAdapterListener;
        this.usage = usage;
    }

    public void changedCategoryGameList() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeGamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutHomepageGameListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_homepage_game_list, parent, false);
        return new HomeGamesAdapter.HomeGamesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeGamesViewHolder holder, int position) {
        CategoryItem categoryItem = categoryItemArrayList.get(position);


        ArrayList<GamesItems> gameCategoryList = new ArrayList<>(categoryItem.getCategoryGameList());


        Pair<Integer, Integer> colorAndListSizePair = getTitleBackgroundColorAndListSize(categoryItem.getName());

        int backgroundColor = colorAndListSizePair.getFirst();

        int spanCount = Math.min(colorAndListSizePair.getSecond(), gameCategoryList.size());
        holder.binding.setTitleBackgroundColor(ContextCompat.getColor(context, backgroundColor));
        holder.binding.setNoItemBannerTitle(categoryItem.getNoGameBannerTitle());

        if (gameCategoryList.isEmpty()) {
            holder.binding.btnViewAllGames.setVisibility(View.GONE);
            holder.binding.noGameBanner.setVisibility(View.VISIBLE);
        } else {
            holder.binding.btnViewAllGames.setVisibility(View.VISIBLE);
            holder.binding.noGameBanner.setVisibility(View.GONE);
        }

        holder.binding.recyclerView.setAdapter(new GameListAdapter(context, new ArrayList<>(gameCategoryList.subList(0, spanCount)), new GameListAdapter.GameListAdapterInterface() {
            @Override
            public void onClick(GamesItems gamesItems) {

                gamesAdapterListener.onGameClicked(gamesItems);
//                ((HomeActivity) context).startIntent(gamesItems);
            }
        }));
        holder.binding.recyclerView.setRecycledViewPool(viewPool);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        if (categoryItem.getName().equals(context.getString(R.string.all_games))) {
            layoutManager = new GridLayoutManager(context, 3);

        }
        holder.binding.recyclerView.setLayoutManager(layoutManager);

        holder.binding.setTextTitle(categoryItem.getName());


        holder.binding.btnViewAllGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gamesAdapterListener.onGameCategoryClick(categoryItem);

            }
        });


    }

    private Pair<Integer, Integer> getTitleBackgroundColorAndListSize(String title) {
        int colorRes = R.color.white;
        int listSize = 5;
        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(colorRes, listSize);

        if (title.equals(context.getString(R.string.favourites))) {
            colorRes = R.color.material_red;
            listSize = 5;
        }
        if (title.equals(context.getString(R.string.recently_played))) {
            colorRes = R.color.light_green_material2;
            listSize = 5;
        }
        if (title.equals(context.getString(R.string.all_games))) {
            colorRes = R.color.gamers_hub_theme;
            listSize = 10;
        }

        pair = new Pair<>(colorRes, listSize);
        return pair;
    }

    @Override
    public int getItemCount() {
        return categoryItemArrayList.size();
    }

    public void updateList(ArrayList<CategoryItem> mCategoryItemArrayList) {
        this.categoryItemArrayList = mCategoryItemArrayList;
        notifyDataSetChanged();

    }

    public class HomeGamesViewHolder extends RecyclerView.ViewHolder {


        LayoutHomepageGameListBinding binding;

        public HomeGamesViewHolder(@NonNull LayoutHomepageGameListBinding mBinding) {
            super(mBinding.getRoot());

            this.binding = mBinding;
        }
    }

    public interface HomeGamesAdapterListener {
        void onGameCategoryClick(CategoryItem categoryItem);

        void onGameClicked(GamesItems gamesItems);
    }


}
