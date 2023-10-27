package com.nitish.gamershub.view.rewards.adapter;

import static com.nitish.gamershub.utils.AppConstants.TransactionMessage;
import static com.nitish.gamershub.utils.AppConstants.TransactionStatusFailed;
import static com.nitish.gamershub.utils.AppConstants.TransactionStatusPending;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nitish.gamershub.databinding.UserTransactionListLayoutBinding;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.view.rewards.activity.TransactionHistoryActivity;
import com.nitish.gamershub.model.firebase.UserTransactions;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;

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
//            holder.binding.transactionTypeText.setTextColor(context.getColor(R.color.upiGreen));
            holder.binding.transactionNumber.setText(userTransactionRequest.getUpiId());


        }
        else {
            holder.binding.transactionImage.setImageResource(R.drawable.paytm_logo);

            holder.binding.transactionTypeText.setText("Paytm");
            holder.binding.transactionNumber.setText(userTransactionRequest.getPaytmNumber());
//            holder.binding.transactionTypeText.setTextColor(context.getColor(R.color.paytmBlue));
        }

        if (!userTransactionRequest.isTransactionComplete() || userTransactionRequest.getTransactionStatus().contains(TransactionStatusPending))
        {
            holder.binding.statusTextview.setText("pending");
            holder.binding.statusTextview.setTextColor(Color.parseColor("#fd1b1b"));
            holder.binding.infoTransactionButton.setVisibility(View.GONE);
        }
        else if(userTransactionRequest.getTransactionStatus().contains(TransactionStatusFailed)) {
            holder.binding.statusTextview.setText("failed");
            holder.binding.statusTextview.setTextColor(Color.parseColor("#fd1b1b"));
            holder.binding.infoTransactionButton.setVisibility(View.VISIBLE);
            holder.binding.infoTransactionStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(userTransactionRequest.getTransactionMessage() != null) {
                        showDialogMessage(userTransactionRequest.getTransactionMessage());
                    } else {
                        showDialogMessage(TransactionMessage);
                    }
                }
            });

        }
        else {
            holder.binding.statusTextview.setText("success");
            holder.binding.statusTextview.setTextColor(Color.parseColor("#1fc648"));
            holder.binding.infoTransactionButton.setVisibility(View.GONE);
        }



        holder.binding.transactionAmount.setText("₹ "+userTransactionRequest.getAmount());
        holder.binding.transactionCoins.setText(""+userTransactionRequest.getCoins());

        holder.binding.transactionIdTextView.setText("Id: "+userTransactionRequest.getTransactionId());
// //        holder.binding.transactionDate.setText(""+ DateTimeHelper.convertIntoAnotherTimeFormat(userTransactionRequest.getRequestDate(),DateTimeHelper.simpleDateFormatPattern));
        holder.binding.transactionDate.setText(""+ DateTimeHelper.getTimeStringInAnotherFormat(userTransactionRequest.getRequestDate(),DateTimeHelper.simpleDateFormatPattern_MMMddYYYY));

        holder.binding.clipboardText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppHelper.copyToClipboard(context,userTransactionRequest.getTransactionId()+"");
                Toast.makeText(context, "Id copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
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


    private void showDialogMessage(String message) {
        DialogItems dialogItems = new DialogItems();
        dialogItems.setYesTitle("Ok");
        dialogItems.setTitle("Transaction Failed");
        dialogItems.setMessage(message);

        ((TransactionHistoryActivity) context).showConfirmationDialogSingleButton2(dialogItems,null);
    }
}

