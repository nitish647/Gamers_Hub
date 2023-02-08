package com.nitish.gamershub.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nitish.gamershub.Activities.RedeemActivity;
import com.nitish.gamershub.Pojo.FireBase.RedeemListItem;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.RedeemItemLayoutBinding;

import java.util.List;

public class RedeemRecyclerviewAdapter extends RecyclerView.Adapter<RedeemRecyclerviewAdapter.RedeemViewHolder> {


    Context context;
    List<RedeemListItem> redeemListItemList;

    public RedeemRecyclerviewAdapter(Context context, List<RedeemListItem> redeemListItemList) {
        this.context = context;
        this.redeemListItemList = redeemListItemList;
    }

    @NonNull
    @Override
    public RedeemRecyclerviewAdapter.RedeemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RedeemItemLayoutBinding redeemItemLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.redeem_item_layout, parent, false);
        return new RedeemViewHolder(redeemItemLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RedeemViewHolder holder, int position) {

        RedeemListItem redeemListItem = redeemListItemList.get(position);

        Glide.with(context).load(redeemListItem.getImageUrl()).into( holder.redeemItemLayoutBinding.redeemImageview);
        String redeemType;

        if(redeemListItem.getName().toLowerCase().contains("paytm"))
        {
            redeemType ="Paytm";
            holder.redeemItemLayoutBinding.redeemAmountTextview.setTextColor(context.getResources().getColor(R.color.paytmBlue));
            holder.itemView.setBackgroundTintList(AppCompatResources.getColorStateList(context,R.color.paytmLightBlue));
        }
        else {
            redeemType="UPI";
            holder.redeemItemLayoutBinding.redeemAmountTextview.setTextColor(context.getResources().getColor(R.color.upiGreen));
            holder.itemView.setBackgroundTintList(AppCompatResources.getColorStateList(context,R.color.upiGreenLight));
        }
        holder.redeemItemLayoutBinding.redeemCoinsTextview.setText(redeemListItem.getCoins()+" coins");
        holder.redeemItemLayoutBinding.redeemAmountTextview.setText("₹ "+redeemListItem.getMoney()+" "+redeemType);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                ((RedeemActivity)context).redeemMoneyItemClick(redeemListItem);
            }
        });


    }



    @Override
    public int getItemCount() {
        return redeemListItemList!=null?redeemListItemList.size():0;
    }



    public static class RedeemViewHolder extends RecyclerView.ViewHolder
    {

        RedeemItemLayoutBinding redeemItemLayoutBinding;

        public RedeemViewHolder(@NonNull RedeemItemLayoutBinding redeemItemLayoutBinding) {

            super(redeemItemLayoutBinding.getRoot());
                this.redeemItemLayoutBinding = redeemItemLayoutBinding;

        }
    }
}
