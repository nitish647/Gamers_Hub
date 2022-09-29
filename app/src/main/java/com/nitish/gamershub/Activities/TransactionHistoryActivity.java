package com.nitish.gamershub.Activities;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nitish.gamershub.Adapters.UserTransactionListAdapter;
import com.nitish.gamershub.Pojo.FireBase.UserProfile;
import com.nitish.gamershub.Pojo.FireBase.UserTransactions;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.ActivityTransactionHistoryBinding;

import java.util.ArrayList;

public class TransactionHistoryActivity extends BasicActivity {

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


      getFirebaseUser().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    dismissProgressBar();
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {

                        UserProfile userProfile=   documentSnapshot.toObject(UserProfile.class);


                        if(userProfile!=null && userProfile.getUserTransactions()!=null ) {

                            getUserTransactionList(userProfile.getUserTransactions().getTransactionRequestArrayList());

                        }
                        else {
                            binding.noTransactionRelative.setVisibility(View.VISIBLE);

                        }


                    }
                    else {
                        dismissProgressBar();
                        Toast.makeText(TransactionHistoryActivity.this, "document does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissProgressBar();
                Toast.makeText(TransactionHistoryActivity.this, "couldn't get the documents ", Toast.LENGTH_SHORT).show();
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