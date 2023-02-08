package com.nitish.gamershub.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nitish.gamershub.Activities.CategoryActivity;

import com.nitish.gamershub.Pojo.Categories;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.CategoryItemBinding;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

    public static

    Context context;
    List<Categories> categoriesList;

    public CategoriesAdapter(Context context, List<Categories> categoriesList) {
        this.context = context;
        this.categoriesList = categoriesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryItemBinding categoryItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.category_item, parent, false);
        return new MyViewHolder(categoryItemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Categories categories = categoriesList.get(position);

        holder.categoryItemBinding.categoryImg.setImageResource(categories.getImageSrc());
        holder.categoryItemBinding.listText.setText(categories.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CategoryActivity.class);
                intent.putExtra("categoryName",categories.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        CategoryItemBinding categoryItemBinding;
        public MyViewHolder(@NonNull CategoryItemBinding categoryItemBinding) {

            super(categoryItemBinding.getRoot());
            this.categoryItemBinding = categoryItemBinding;

        }
    }
}
