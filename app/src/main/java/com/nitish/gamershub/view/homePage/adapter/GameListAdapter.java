package com.nitish.gamershub.view.homePage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nitish.gamershub.databinding.RvItemsGameListLayoutBinding;
import com.nitish.gamershub.model.gamersHubMaterData.GamesItems;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.RvItemsGameListLayoutBinding;
import com.nitish.gamershub.model.local.GlideData;
import com.nitish.gamershub.utils.GlideHelper;

import java.util.ArrayList;
import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.PopularAndNewViewHolder> implements Filterable {


    Context context;
    ArrayList<GamesItems> gamesItemsList;
    ArrayList<GamesItems> gamesItemsListFull;
    GameListAdapterInterface gameListAdapterInterface;

    public GameListAdapter(Context context, ArrayList<GamesItems> gamesItemsList, GameListAdapterInterface mGameListAdapterInterface) {
        this.context = context;
        this.gamesItemsList = gamesItemsList;
        this.gamesItemsListFull = gamesItemsList;
        this.gameListAdapterInterface = mGameListAdapterInterface;
    }

    public void updateGamesList(ArrayList<GamesItems> gamesItemsList) {
        this.gamesItemsList = gamesItemsList;
        this.gamesItemsListFull = gamesItemsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameListAdapter.PopularAndNewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RvItemsGameListLayoutBinding RvItemsGameListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.rv_items_game_list_layout, parent, false);
        return new PopularAndNewViewHolder(RvItemsGameListLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAndNewViewHolder holder, int position) {

        GamesItems gamesItems = gamesItemsList.get(position);

        GlideData glideData = new GlideData();

        glideData.setImageUrl(gamesItems.getImg_file());

        GlideHelper.loadGlideImage(holder.binding.gameImg, glideData, null);

        holder.binding.gameName.setText(gamesItems.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gameListAdapterInterface.onClick(gamesItems);

            }
        });
    }


    @Override
    public int getItemCount() {
        return gamesItemsList.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    public Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<GamesItems> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0 || constraint.toString().trim().isEmpty()) {

                filteredList.addAll(gamesItemsListFull);

            } else {

                String filterPattern = constraint.toString().toLowerCase().trim();
                for (GamesItems item : gamesItemsListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            filterResults.values = filteredList;
            filterResults.count = filteredList.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            gamesItemsList = (ArrayList<GamesItems>) results.values;
            notifyDataSetChanged();
        }

    };

    public static class PopularAndNewViewHolder extends RecyclerView.ViewHolder {
        RvItemsGameListLayoutBinding binding;

        public PopularAndNewViewHolder(@NonNull RvItemsGameListLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface GameListAdapterInterface {
        public void onClick(GamesItems gamesItems);
    }

}
