package com.nitish.gamershub.view.rewards.activity;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nitish.gamershub.view.rewards.adapter.UserTransactionListAdapter;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.model.firebase.UserTransactions;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.ActivityTransactionHistoryBinding;
import com.nitish.gamershub.view.base.BaseActivity;

import java.util.ArrayList;

public class TransactionHistoryActivity extends BaseActivity {

    FirebaseFirestore firebaseFirestore;
    ActivityTransactionHistoryBinding binding;
    ArrayList<UserTransactions.TransactionRequest> transactionRequestList= new ArrayList<>();
    UserTransactionListAdapter userTransactionListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_transaction_history);
        firebaseFirestore= FirebaseFirestore.getInstance();

        binding.transactionRecycler.setLayoutManager(new LinearLayoutManager(this));
        getTransactionHistory();
        onClickListeners();
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_transaction_history;
    }


    public void onClickListeners()
    {
        binding.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void getTransactionHistory()
    {

        showProgressBar().setMessage("Loading transaction");



        getUserProfileGlobal(new GetUserProfileDataListener() {
            @Override
            public void onTaskSuccessful(UserProfile userProfile) {
                if(userProfile!=null && userProfile.getUserTransactions()!=null ) {

                    getUserTransactionList(userProfile.getUserTransactions().getTransactionRequestArrayList());

                }
                else {
                    binding.noTransactionRelative.setVisibility(View.VISIBLE);

                }

            }
        });



    }
    public void getUserTransactionList(ArrayList<UserTransactions.TransactionRequest> mTransactionRequestList )
    {


        this.transactionRequestList = mTransactionRequestList;


        if(transactionRequestList.size()==0)
        {
            binding.noTransactionRelative.setVisibility(View.VISIBLE);

        }
        else {
            binding.noTransactionRelative.setVisibility(View.GONE);
            userTransactionListAdapter = new UserTransactionListAdapter(TransactionHistoryActivity.this,transactionRequestList);
            binding.transactionRecycler.setAdapter(userTransactionListAdapter);

        }

        // reverseArrayList(transactionRequestList);
        userTransactionListAdapter.notifyDataSetChanged();



    }
}