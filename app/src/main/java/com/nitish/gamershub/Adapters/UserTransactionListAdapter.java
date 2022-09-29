package com.nitish.gamershub.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nitish.gamershub.Pojo.FireBase.UserTransactions;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.UserTransactionListLayoutBinding;

import java.util.List;

public class UserTransactionListAdapter extends RecyclerView.Adapter<UserTransactionListAdapter.TransactionViewHolder> {


    Context context;
    List<UserTransactions.TransactionRequest> transactionRequestList;

    public UserTransactionListAdapter(Context context, List<UserTransactions.TransactionRequest> transactionRequestList) {
        this.context = context;
        this.transactionRequestList = transactionRequestList;
    }

    @NonNull
    @Override
    public UserTransactionListAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        UserTransactionListLayoutBinding binding = DataBindingUtil.inflate( LayoutInflater.from(context),R.layout.user_transaction_list_layout,parent,false);

        return new UserTransactionListAdapter.TransactionViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull UserTransactionListAdapter.TransactionViewHolder holder, int position) {

        UserTransactions.TransactionRequest userTransactionRequest = transactionRequestList.get(position);


        if(userTransactionRequest.getRedeemType().toLowerCase().contains("upi"))
        {
            holder.binding.transactionImage.setImageResource(R.drawable.upi_icon_1);
            holder.binding.transactionTypeText.setText("UPI");
            holder.binding.transactionTypeText.setTextColor(context.getColor(R.color.upiGreen));
            holder.binding.transactionNumber.setText(userTransactionRequest.getUpiId());


        }
        else {
            holder.binding.transactionImage.setImageResource(R.drawable.paytm_icom);

            holder.binding.transactionTypeText.setText("Paytm");
            holder.binding.transactionNumber.setText(userTransactionRequest.getPaytmNumber());
            holder.binding.transactionTypeText.setTextColor(context.getColor(R.color.paytmBlue));
        }

        if(!userTransactionRequest.isPaid)
        {
            holder.binding.statusTextview.setText("Pending");
            holder.binding.statusTextview.setTextColor(Color.parseColor("#fd1b1b"));
        }
        else {

            holder.binding.statusTextview.setText("Success");
            holder.binding.statusTextview.setTextColor(Color.parseColor("#1fc648"));
        }
        holder.binding.transactionAmount.setText("₹ "+userTransactionRequest.getAmount());
        holder.binding.transactionCoins.setText(""+userTransactionRequest.getCoins());

        holder.binding.transactionDate.setText(""+userTransactionRequest.getRequestDate());
    }



    @Override
    public int getItemCount() {
        return transactionRequestList.size();
    }



    public static class TransactionViewHolder extends RecyclerView.ViewHolder
    {
        UserTransactionListLayoutBinding binding;



        public TransactionViewHolder(@NonNull  UserTransactionListLayoutBinding userTransactionListLayoutBinding) {

            super(userTransactionListLayoutBinding.getRoot());


          this.binding =  userTransactionListLayoutBinding;
        }
    }
}

