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

import com.nitish.gamershub.model.local.AllGamesItems;
import com.nitish.gamershub.model.local.AllGamesItemsSerializable;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.PopularAndNewLayoutBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewAndPopularGamesAdapter  extends RecyclerView.Adapter<NewAndPopularGamesAdapter.PopularAndNewViewHolder> implements Filterable {


    Context context;
    List<AllGamesItems> allGamesItemsList ;
    List<AllGamesItems> allGamesItemsListFull;
//    public static  AllGamesItems SelectedGameObject ;
    NewAndPopularGameAdapterInterface newAndPopularGameAdapterInterface;
    public NewAndPopularGamesAdapter(Context context, List<AllGamesItems> allGamesItemsList,NewAndPopularGameAdapterInterface mNewAndPopularGameAdapterInterface) {
        this.context = context;
        this.allGamesItemsList = allGamesItemsList;
        this.allGamesItemsListFull  =allGamesItemsList;
        this.newAndPopularGameAdapterInterface = mNewAndPopularGameAdapterInterface;
    }

    public void changedNewAndPopularGamesList(List<AllGamesItems> allGamesItemsList) {
        this.allGamesItemsList = allGamesItemsList;
        this.allGamesItemsListFull =allGamesItemsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewAndPopularGamesAdapter.PopularAndNewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PopularAndNewLayoutBinding popularAndNewLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.popular_and_new_layout, parent, false);
        return new PopularAndNewViewHolder(popularAndNewLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAndNewViewHolder holder, int position) {

        AllGamesItems allGamesItems = allGamesItemsList.get(position);

        AllGamesItemsSerializable allGamesItemsSerializable = new AllGamesItemsSerializable();
        allGamesItemsSerializable.setGameUrl(allGamesItems.getGameUrl());
        allGamesItemsSerializable.setCategory(allGamesItems.getCategory());
        allGamesItemsSerializable.setImg_file(allGamesItems.getImg_file());
        allGamesItemsSerializable.setOrientation(allGamesItems.getOrientation());
        allGamesItemsSerializable.setDescription(allGamesItems.getDescription());
        allGamesItemsSerializable.setName(allGamesItems.getName());


        Picasso.get().load(allGamesItems.getImg_file()).into(holder.popularAndNewLayoutBinding.gameImg);
        holder.popularAndNewLayoutBinding.gameImg.setClipToOutline(true);
        holder.popularAndNewLayoutBinding.gameName.setText(allGamesItems.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newAndPopularGameAdapterInterface.onClick(allGamesItems);
//                SelectedGameObject = allGamesItems;
//                if(context.getClass()== HomeActivity.class)
//                {
//                    ((HomeActivity)context).startIntent();
//                }
//                else {
//                    ((CategoryActivity)context).categoryItemClick();
//                }

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
        PopularAndNewLayoutBinding popularAndNewLayoutBinding;

        public PopularAndNewViewHolder(@NonNull PopularAndNewLayoutBinding popularAndNewLayoutBinding) {
            super(popularAndNewLayoutBinding.getRoot());
            this.popularAndNewLayoutBinding = popularAndNewLayoutBinding;
        }
    }
    public interface NewAndPopularGameAdapterInterface {
        public void onClick(AllGamesItems allGamesItems);
    }

}