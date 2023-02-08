package com.nitish.gamershub.Adapters;

import static com.nitish.gamershub.Utils.ConstantsHelper.CategoryAction;
import static com.nitish.gamershub.Utils.ConstantsHelper.CategoryArcade;
import static com.nitish.gamershub.Utils.ConstantsHelper.CategoryBest;
import static com.nitish.gamershub.Utils.ConstantsHelper.CategoryBoard;
import static com.nitish.gamershub.Utils.ConstantsHelper.CategoryFavourites;
import static com.nitish.gamershub.Utils.ConstantsHelper.CategoryList;
import static com.nitish.gamershub.Utils.ConstantsHelper.CategoryNew;
import static com.nitish.gamershub.Utils.ConstantsHelper.CategoryPuzzle;
import static com.nitish.gamershub.Utils.ConstantsHelper.CategoryRacing;
import static com.nitish.gamershub.Utils.ConstantsHelper.CategoryShooting;
import static com.nitish.gamershub.Utils.ConstantsHelper.CategoryStrategy;
import static com.nitish.gamershub.Utils.ConstantsHelper.FavouriteList;
import static com.nitish.gamershub.Utils.ConstantsHelper.IntentData;
import static com.nitish.gamershub.Utils.ConstantsHelper.MainGamesList;
import static com.nitish.gamershub.Utils.ConstantsHelper.NewGamesList;
import static com.nitish.gamershub.Utils.ConstantsHelper.PopularGamesList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.animation.content.Content;
import com.nitish.gamershub.Activities.CategoryActivity;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.CategoryGamesLayoutBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.paperdb.Paper;

public class CategoryGamesAdapter extends RecyclerView.Adapter<CategoryGamesAdapter.CategoryGamesViewHolder> {

    private Context context;
    private List<AllGamesItems> categoryGameList;

    public CategoryGamesAdapter(Context context) {
        this.context = context;
    }

    public void changedCategoryGameList () {
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
        categoryGameList = CategoryGameList(CategoryList.get(position));

        if(categoryGameList.size() != 0) {
            categoryGameList = categoryGameList.stream().limit(4).collect(Collectors.toList());
            holder.categoryGamesLayoutBinding.categoryNameTextview.setVisibility(View.VISIBLE);
            holder.categoryGamesLayoutBinding.gamesRecyclerView.setVisibility(View.VISIBLE);
            holder.categoryGamesLayoutBinding.btnViewAllGames.setVisibility(View.VISIBLE);

            holder.categoryGamesLayoutBinding.categoryNameTextview.setText(CategoryList.get(position));

            holder.categoryGamesLayoutBinding.gamesRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.categoryGamesLayoutBinding.gamesRecyclerView.setAdapter(new NewAndPopularGamesAdapter(context, categoryGameList));

            holder.categoryGamesLayoutBinding.btnViewAllGames.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("categoryName", CategoryList.get(holder.getAdapterPosition()));
                    v.getContext().startActivity(intent);
                }
            });

        } else {
            holder.categoryGamesLayoutBinding.categoryNameTextview.setVisibility(View.GONE);
            holder.categoryGamesLayoutBinding.gamesRecyclerView.setVisibility(View.GONE);
            holder.categoryGamesLayoutBinding.btnViewAllGames.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return CategoryList.size();
    }

    public class CategoryGamesViewHolder extends RecyclerView.ViewHolder {


        CategoryGamesLayoutBinding categoryGamesLayoutBinding;

        public CategoryGamesViewHolder(@NonNull CategoryGamesLayoutBinding categoryGamesLayoutBinding) {
            super(categoryGamesLayoutBinding.getRoot());

            this.categoryGamesLayoutBinding = categoryGamesLayoutBinding;
        }
    }

    public List<AllGamesItems> CategoryGameList(String categoryTitle) {

        switch (categoryTitle) {
            case CategoryFavourites:
                return Paper.book().read(FavouriteList);
            case CategoryNew:
                return Paper.book().read(NewGamesList);
            case CategoryBest:
                return Paper.book().read(PopularGamesList);
            default:
                List<AllGamesItems> mainGamesList = Paper.book().read(MainGamesList);
                List<AllGamesItems> categoryGamesList = new ArrayList<>();
                for (int i = 0; i < mainGamesList.size(); i++) {
                    if (categoryTitle.toLowerCase().contains(mainGamesList.get(i).getCategory().toLowerCase())) {
                        categoryGamesList.add(mainGamesList.get(i));
                    }
                }
                return categoryGamesList;
        }
    }

}
