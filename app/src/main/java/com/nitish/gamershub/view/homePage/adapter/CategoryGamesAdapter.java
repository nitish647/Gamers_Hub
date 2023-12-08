package com.nitish.gamershub.view.homePage.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nitish.gamershub.databinding.CategoryGamesLayoutBinding;
import com.nitish.gamershub.model.local.CategoryItem;
import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;
import com.nitish.gamershub.R;

import java.util.ArrayList;

public class CategoryGamesAdapter extends RecyclerView.Adapter<CategoryGamesAdapter.CategoryGamesViewHolder> {

    private Context context;

    private ArrayList<CategoryItem> categoryItemArrayList;
    RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    private CategoryGamesAdapterListener gamesAdapterListener;


    public CategoryGamesAdapter(Context context, ArrayList<CategoryItem> categoryItemArrayList, CategoryGamesAdapterListener gamesAdapterListener) {
        this.context = context;
        this.categoryItemArrayList = categoryItemArrayList;
        this.gamesAdapterListener = gamesAdapterListener;
    }


    public void changedCategoryGameList() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryGamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CategoryGamesLayoutBinding categoryGamesLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.category_games_layout, parent, false);
        return new CategoryGamesViewHolder(categoryGamesLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryGamesViewHolder holder, int position) {
        CategoryItem categoryItem = categoryItemArrayList.get(position);


        holder.categoryGamesLayoutBinding.categoryNameTextview.setVisibility(View.VISIBLE);
        holder.categoryGamesLayoutBinding.gamesRecyclerView.setVisibility(View.VISIBLE);


        ArrayList<GamesItems> gameCategoryList = new ArrayList<>(categoryItem.getCategoryGameList());

        holder.categoryGamesLayoutBinding.categoryImageview.setImageResource(categoryItem.getImageRes());
        holder.categoryGamesLayoutBinding.gamesRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        if (!gameCategoryList.isEmpty()) {
            holder.categoryGamesLayoutBinding.btnViewAllGames.setVisibility(View.VISIBLE);
        } else {
            holder.categoryGamesLayoutBinding.btnViewAllGames.setVisibility(View.GONE);
        }

        gameCategoryList = new ArrayList<>(gameCategoryList.subList(0, Math.min(gameCategoryList.size(), 4)));


        holder.categoryGamesLayoutBinding.gamesRecyclerView.setAdapter(new GameListAdapter(context, gameCategoryList, new GameListAdapter.GameListAdapterInterface() {
            @Override
            public void onClick(GamesItems gamesItems) {

                gamesAdapterListener.onGameClicked(gamesItems);
//                ((HomeActivity) context).startIntent(gamesItems);
            }
        }));
        holder.categoryGamesLayoutBinding.gamesRecyclerView.setRecycledViewPool(viewPool);

        holder.categoryGamesLayoutBinding.categoryNameTextview.setText(categoryItem.getName());


        holder.categoryGamesLayoutBinding.btnViewAllGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gamesAdapterListener.onGameCategoryClick(categoryItem);

            }
        });


    }

    @Override
    public int getItemCount() {
        return categoryItemArrayList.size();
    }

    public void updateList(ArrayList<CategoryItem> mCategoryItemArrayList) {
        this.categoryItemArrayList = mCategoryItemArrayList;
        notifyDataSetChanged();

    }

    public class CategoryGamesViewHolder extends RecyclerView.ViewHolder {


        CategoryGamesLayoutBinding categoryGamesLayoutBinding;

        public CategoryGamesViewHolder(@NonNull CategoryGamesLayoutBinding categoryGamesLayoutBinding) {
            super(categoryGamesLayoutBinding.getRoot());

            this.categoryGamesLayoutBinding = categoryGamesLayoutBinding;
        }
    }

    public interface CategoryGamesAdapterListener {
        void onGameCategoryClick(CategoryItem categoryItem);

        void onGameClicked(GamesItems gamesItems);
    }


}
