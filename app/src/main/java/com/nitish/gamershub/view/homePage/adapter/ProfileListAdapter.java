package com.nitish.gamershub.view.homePage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.RvItemsGameListLayoutBinding;
import com.nitish.gamershub.databinding.RvItemsProfileListItemsBinding;
import com.nitish.gamershub.model.local.ProfileListItems;

import java.util.ArrayList;

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ProfileListViewHolder> {

    private Context context;
    private ArrayList<ProfileListItems> profileListItemsArrayList;
    private ProfileListListener listListener;

    public ProfileListAdapter(Context context, ArrayList<ProfileListItems> profileListItemsArrayList, ProfileListListener listListener) {
        this.context = context;
        this.profileListItemsArrayList = profileListItemsArrayList;
        this.listListener = listListener;
    }

    @NonNull
    @Override
    public ProfileListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RvItemsProfileListItemsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.rv_items_profile_list_items,parent,false);
        return  new ProfileListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileListViewHolder holder, int position) {
        ProfileListItems profileListItems = profileListItemsArrayList.get(position);

        holder.binding.titleTextview.setText(profileListItems.getTitle());
        holder.binding.mainImageview.setImageResource(profileListItems.getIcon());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listListener.onItemClick(profileListItems);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profileListItemsArrayList.size();
    }

    public class ProfileListViewHolder extends RecyclerView.ViewHolder {

        RvItemsProfileListItemsBinding binding ;
        public ProfileListViewHolder(@NonNull RvItemsProfileListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    public interface ProfileListListener{
       public void onItemClick(ProfileListItems profileListItems);
    }
}
