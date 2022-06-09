package com.nitish.gamershub.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nitish.gamershub.Activities.GameDetailActivity2;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewAndPopularGamesAdapter  extends RecyclerView.Adapter<NewAndPopularGamesAdapter.PopularAndNewViewHolder> implements Filterable {

    public static  String gameDataObject = "gameDataObject";
    Context context;
    List<AllGamesItems> allGamesItemsList ;
    List<AllGamesItems> allGamesItemsListFull;
    public NewAndPopularGamesAdapter(Context context, List<AllGamesItems> allGamesItemsList) {
        this.context = context;
        this.allGamesItemsList = allGamesItemsList;
        allGamesItemsListFull  =allGamesItemsList;
    }

    @NonNull
    @Override
    public NewAndPopularGamesAdapter.PopularAndNewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.popular_and_new_layout,parent,false);

        return new NewAndPopularGamesAdapter.PopularAndNewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAndNewViewHolder holder, int position) {

        AllGamesItems allGamesItems = allGamesItemsList.get(position);

        Picasso.get().load(allGamesItems.getImg_file()).into(holder.game_image);
        holder.game_name.setText(allGamesItems.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GameDetailActivity2.class);
                intent.putExtra(gameDataObject,allGamesItems);
                context.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return allGamesItemsList.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    public Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<AllGamesItems> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0|| constraint.toString().trim().isEmpty()) {
//                filterResults.count = CustomerTransactionItemsArrayList.size();
//                filterResults.values = CustomerTransactionItemsArrayList;
                filteredList.addAll(allGamesItemsListFull);

            } else {

                String filterPattern = constraint.toString().toLowerCase().trim();
                for (AllGamesItems item : allGamesItemsListFull) {
                    // match the pattern if searched for the product name
                    if (item.getName().toLowerCase().contains(filterPattern))
                    {
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
            allGamesItemsList = (ArrayList<AllGamesItems>) results.values;
            notifyDataSetChanged();
        }

    };

    public static class PopularAndNewViewHolder extends RecyclerView.ViewHolder
    {
        ImageView game_image;
        TextView game_name;

        public PopularAndNewViewHolder(@NonNull View itemView) {

            super(itemView);
            game_image = itemView.findViewById(R.id.game_img);
            game_name = itemView.findViewById(R.id.game_name);


        }
    }
}
