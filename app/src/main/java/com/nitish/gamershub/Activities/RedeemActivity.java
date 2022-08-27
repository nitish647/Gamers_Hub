package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHub_ParentCollection;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserMail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nitish.gamershub.Adapters.RedeemRecyclerviewAdapter;
import com.nitish.gamershub.Adapters.UserTransactionListAdapter;
import com.nitish.gamershub.Pojo.UserTransactions;
import com.nitish.gamershub.Pojo.RedeemListItem;
import com.nitish.gamershub.Pojo.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.ProgressBarHelper;
import com.nitish.gamershub.Utils.UserOperations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class RedeemActivity extends AppCompatActivity {

    TextView walletCoinsTextview;
    FirebaseFirestore firebaseFirestore;

    RecyclerView redeemRecyclerView;
    ArrayList<RedeemListItem> redeemListItemArrayList = new ArrayList<>();
    int totalGameCoins=0;
    ProgressDialog progressDialog;
    RelativeLayout redeemHistoryButton;
    UserTransactionListAdapter userTransactionListAdapter;
    ArrayList<UserTransactions.TransactionRequest> transactionRequestList;
     AlertDialog userTransactionDialog;
    RecyclerView userTransactionListRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddeem);
        walletCoinsTextview = findViewById(R.id.walletCoinsTextview);
        redeemRecyclerView = findViewById(R.id.redeemRecyclerView);
        redeemHistoryButton = findViewById(R.id.redeemHistoryButton);

        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = ProgressBarHelper.setProgressBarDialog(this);
           transactionRequestList = new ArrayList<>();



        userTransactionListAdapter = new UserTransactionListAdapter(RedeemActivity.this,transactionRequestList);

        setUserTransactionDialog();

        getUserCoins();
        setRedeemRecyclerView();

        redeemHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userTransactionDialog.show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        UserOperations.getFirestoreUser().addSnapshotListener(RedeemActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Toast.makeText(RedeemActivity.this, "error while getting doc 211", Toast.LENGTH_SHORT).show();
                }
                if(value!=null && value.exists())
                {
                    UserProfile  userProfile=   value.toObject(UserProfile.class);

                    UserProfile.ProfileData profileData = userProfile.profileData;

              //      walletCoinsTextview.setText(profileData.gameCoins +"");


                }
            }
        });

    }
    public void getUserCoins()
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
                        UserProfile userProfile=   documentSnapshot.toObject(UserProfile.class);

                        UserProfile.ProfileData profileData = userProfile.profileData;

                        totalGameCoins = profileData.gameCoins;
                        walletCoinsTextview.setText(profileData.gameCoins +"");
                    }
                    else {
                        Toast.makeText(RedeemActivity.this, "document does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RedeemActivity.this, "couldn't get the documents ", Toast.LENGTH_SHORT).show();
            }
        });



    }
    public void setRedeemRecyclerView()
    {
        redeemListItemArrayList.add(new RedeemListItem("Upi50 ",10,1,R.drawable.upi_icon_1));
        redeemListItemArrayList.add(new RedeemListItem("Paytm50 ",5000,50,R.drawable.paytm_icom));
        redeemListItemArrayList.add(new RedeemListItem("Upi100",10000,100,R.drawable.upi_icon_1));
        redeemListItemArrayList.add(new RedeemListItem("Paytm100 ",10000,100,R.drawable.paytm_icom));
        redeemListItemArrayList.add(new RedeemListItem("Upi25 ",2500,25,R.drawable.upi_icon_1));
        redeemListItemArrayList.add(new RedeemListItem("Paytm25 ",2500,25,R.drawable.paytm_icom));

        RedeemRecyclerviewAdapter redeemRecyclerviewAdapter = new RedeemRecyclerviewAdapter(this, redeemListItemArrayList);
        redeemRecyclerView.setAdapter(redeemRecyclerviewAdapter);
        redeemRecyclerView.setLayoutManager(new GridLayoutManager(this,2));

    }

    public void redeemMoneyIcon(RedeemListItem redeemListItem)
    {
        if(totalGameCoins< redeemListItem.getCoins())
        {
            Toast.makeText(this, "Not enough funds to redeem ", Toast.LENGTH_LONG).show();

        }
        else {

         updateUserWallet(redeemListItem);
        }

    }
    public void raiseRedeemRequest(RedeemListItem redeemListItem)
    {

        firebaseFirestore.collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail)+"").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();


                    if(documentSnapshot.exists())
                    {


                            UserProfile userProfile=   documentSnapshot.toObject(UserProfile.class);

                            UserProfile.ProfileData profileData = userProfile.profileData;

                            totalGameCoins = profileData.gameCoins;
                            walletCoinsTextview.setText(profileData.gameCoins +"");


                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDateTime = dateFormat.format(new Date()); // Find todays date

                        HashMap<String , Object> redeemHistory = new HashMap<>();
                        HashMap<String , Object> transactions = new HashMap<>();
                        UserTransactions.TransactionRequest transactionRequest = new UserTransactions.TransactionRequest();
                        transactionRequest.setRequestDate(currentDateTime);
                        transactionRequest.setPaid(false);

                        transactionRequest.setCoins(redeemListItem.getCoins());
                        transactionRequest.setAmount(redeemListItem.getMoney());
                        transactionRequest.setRedeemType(redeemListItem.getName());
                        transactionRequest.setRemainingCoins(totalGameCoins);
                        transactionRequest.setPaidDate(null);



                        transactions.put( currentDateTime,transactionRequest);
                        redeemHistory.put("userTransaction",transactions);
                        Toast.makeText(RedeemActivity.this, "Request Raised successfully", Toast.LENGTH_SHORT).show();

                        firebaseFirestore.collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail)+"").set(redeemHistory, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                getUserCoins();
                            }
                        });


                    }
                    else {
                        Toast.makeText(RedeemActivity.this, "document does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RedeemActivity.this, "couldn't get the documents ", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void updateUserWallet(RedeemListItem redeemListItem)
    {
        progressDialog.show();
        UserOperations.getFirestoreUser().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);

                    if(userProfile!=null) {
                        UserOperations.getFirestoreUser().set(UserOperations.addCoinsToWallet(userProfile, -redeemListItem.getCoins()), SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {


                                    raiseRedeemRequest(redeemListItem);
                                }
                            }
                        });


                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(RedeemActivity.this, "can't redeem as user is null ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RedeemActivity.this, "can't redeem as user document doesn't exists ", Toast.LENGTH_SHORT).show();

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
    public ArrayList<UserTransactions.TransactionRequest> reverseArrayList(ArrayList<UserTransactions.TransactionRequest> alist)
    {
        // Arraylist for storing reversed elements
        ArrayList<UserTransactions.TransactionRequest> revArrayList = new ArrayList<UserTransactions.TransactionRequest>();
        for (int i = alist.size() - 1; i >= 0; i--) {

            // Append the elements in reverse order
            revArrayList.add(alist.get(i));
        }

        // Return the reversed arraylist
        return revArrayList;
    }
    public void setUserTransactionDialog()
    {
        LayoutInflater factory = LayoutInflater.from(RedeemActivity.this);

        final View userTransactionListLayout = factory.inflate(R.layout.user_transaction_list_dialog_list, null);

         userTransactionDialog = new AlertDialog.Builder(RedeemActivity.this).create();


        userTransactionDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        userTransactionDialog.setView(userTransactionListLayout);



         userTransactionListRecycler = userTransactionListLayout.findViewById(R.id.userTransactionListRecycler);

        userTransactionListRecycler.setLayoutManager(new LinearLayoutManager(this));
        userTransactionListRecycler.setAdapter(userTransactionListAdapter);

    }

}