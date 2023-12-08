package com.nitish.gamershub.view.homePage.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nitish.gamershub.utils.AppConstants;
import com.nitish.gamershub.view.homePage.activity.CategoryActivity;

import com.nitish.gamershub.model.local.CategoryItem;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.CategoryItemBinding;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {


    private Context context;
    List<CategoryItem> categoryItemList;

    public CategoriesAdapter(Context context, List<CategoryItem> categoryItemList) {
        this.context = context;
        this.categoryItemList = categoryItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryItemBinding categoryItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.category_item, parent, false);
        return new MyViewHolder(categoryItemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        CategoryItem categoryItem = categoryItemList.get(position);

        holder.categoryItemBinding.categoryImg.setImageResource(categoryItem.getImageSrc());
        holder.categoryItemBinding.listText.setText(categoryItem.getName());

        holder.categoryItemBinding.categoryImg.setImageResource(categoryItem.getImageRes());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CategoryActivity.class);
                intent.putExtra(AppConstants.IntentData, categoryItem.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryItemList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CategoryItemBinding categoryItemBinding;

        public MyViewHolder(@NonNull CategoryItemBinding categoryItemBinding) {

            super(categoryItemBinding.getRoot());
            this.categoryItemBinding = categoryItemBinding;

        }
    }
}
