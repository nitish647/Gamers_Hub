package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHub_ParentCollection;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserInfo;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserMail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nitish.gamershub.Adapters.UserTransactionListAdapter;
import com.nitish.gamershub.Pojo.UserProfile;
import com.nitish.gamershub.Pojo.UserTransactions;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.DeviceHelper;
import com.nitish.gamershub.databinding.ActivityTransactionHistoryBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import io.paperdb.Paper;

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
        userTransactionListAdapter = new UserTransactionListAdapter(TransactionHistoryActivity.this,transactionRequestList);
        binding.transactionRecycler.setAdapter(userTransactionListAdapter);
        binding.transactionRecycler.setLayoutManager(new LinearLayoutManager(this));
        getTransactionHistory();
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_transaction_history;
    }

    public void getTransactionHistory()
    {


        firebaseFirestore.collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail)+"").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists())
                    {


                        HashMap<String,Object> userTransactionsHashMap = (HashMap<String, Object>) documentSnapshot.get("userTransaction");

                        if(userTransactionsHashMap!=null) {
                            getUserTransactionList(userTransactionsHashMap);

                        }
                        else {
                            Toast.makeText(TransactionHistoryActivity.this, "userTransactionsHashMap is null", Toast.LENGTH_SHORT).show();
                        }
                        UserProfile userProfile=   documentSnapshot.toObject(UserProfile.class);

                        UserProfile.ProfileData profileData = userProfile.profileData;


                    }
                    else {
                        Toast.makeText(TransactionHistoryActivity.this, "document does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TransactionHistoryActivity.this, "couldn't get the documents ", Toast.LENGTH_SHORT).show();
            }
        });



    }
    public void getUserTransactionList(HashMap<String,Object> userTransactionsMap)
    {
        transactionRequestList.clear();

        Gson gson = new Gson();
        for(int i =0 ; i<=userTransactionsMap.keySet().toArray().length-1;i++)
        {

            HashMap<String,Object> hashMap = (HashMap<String, Object>) userTransactionsMap.get(userTransactionsMap.keySet().toArray()[i].toString());

            JsonElement jsonElement = gson.toJsonTree(hashMap);
            UserTransactions.TransactionRequest transactionRequest =gson.fromJson(jsonElement, UserTransactions.TransactionRequest.class);

            transactionRequestList.add(transactionRequest);
        }

        // reverseArrayList(transactionRequestList);
        userTransactionListAdapter.notifyDataSetChanged();



    }
}