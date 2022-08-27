package com.nitish.gamershub.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nitish.gamershub.Activities.RedeemActivity;
import com.nitish.gamershub.Pojo.RedeemListItem;
import com.nitish.gamershub.Pojo.UserTransactions;
import com.nitish.gamershub.R;

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
        View v = LayoutInflater.from(context).inflate(R.layout.user_transaction_list_layout,parent,false);

        return new UserTransactionListAdapter.TransactionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserTransactionListAdapter.TransactionViewHolder holder, int position) {

        UserTransactions.TransactionRequest userTransactionRequest = transactionRequestList.get(position);

        holder.transactionAmount.setText("Transaction amount rs "+userTransactionRequest.getAmount());
        holder.transactionCoins.setText("Coins Redeemed "+userTransactionRequest.getCoins());

        holder.statusTextview.setText(userTransactionRequest.isPaid+"");
        holder.transactionDate.setText("Request date : "+userTransactionRequest.getRequestDate());
    }



    @Override
    public int getItemCount() {
        return transactionRequestList.size();
    }



    public static class TransactionViewHolder extends RecyclerView.ViewHolder
    {

        TextView statusTextview,transactionAmount,transactionCoins,transactionDate;

        public TransactionViewHolder(@NonNull View itemView) {

            super(itemView);
            transactionDate = itemView.findViewById(R.id.transactionDate);
            statusTextview = itemView.findViewById(R.id.statusTextview);
            transactionAmount = itemView.findViewById(R.id.transactionAmount);
            transactionCoins = itemView.findViewById(R.id.transactionCoins);
        }
    }
}

