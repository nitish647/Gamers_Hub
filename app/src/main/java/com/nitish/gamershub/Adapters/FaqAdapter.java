package com.nitish.gamershub.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nitish.gamershub.Pojo.FaqPojo;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.FaqListItemBinding;

import java.util.ArrayList;
import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.MyViewHolder> {


    ArrayList<FaqPojo> faqPojoArrayList;

    public FaqAdapter(ArrayList<FaqPojo> faqPojoArrayList) {
        this.faqPojoArrayList = faqPojoArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        FaqListItemBinding faqListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.faq_list_item,parent,false);
        return new MyViewHolder(faqListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return faqPojoArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {

        FaqListItemBinding faqListItemBinding;

        public MyViewHolder(@NonNull FaqListItemBinding binding) {
            super(binding.getRoot());

            this.faqListItemBinding = binding;
        }
    }
}
