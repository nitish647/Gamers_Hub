package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHub_ParentCollection;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserMail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import com.nitish.gamershub.Adapters.RedeemRecyclerviewAdapter;
import com.nitish.gamershub.Adapters.UserTransactionListAdapter;
import com.nitish.gamershub.Pojo.UserTransactions;
import com.nitish.gamershub.Pojo.RedeemListItem;
import com.nitish.gamershub.Pojo.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.CommonMethods;
import com.nitish.gamershub.Utils.Connectivity;
import com.nitish.gamershub.Utils.ConstantsHelper;
import com.nitish.gamershub.Utils.ProgressBarHelper;
import com.nitish.gamershub.Utils.ToastHelper;
import com.nitish.gamershub.Utils.UserOperations;
import com.nitish.gamershub.Utils.ValidationHelper;
import com.nitish.gamershub.databinding.PaytmUpiNumberLayoutBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import io.paperdb.Paper;

public class RedeemActivity extends BasicActivity {

  //  TextView walletCoinsTextview;
    FirebaseFirestore firebaseFirestore;

    RecyclerView redeemRecyclerView;
    ArrayList<RedeemListItem> redeemListItemArrayList = new ArrayList<>();
    int totalGameCoins=0;
    ProgressDialog progressDialog;
    UserTransactionListAdapter userTransactionListAdapter;
    ArrayList<UserTransactions.TransactionRequest> transactionRequestList;
     AlertDialog userTransactionDialog;
    RecyclerView userTransactionListRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddeem);
        redeemRecyclerView = findViewById(R.id.redeemRecyclerView);


        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = ProgressBarHelper.setProgressBarDialog(this);
           transactionRequestList = new ArrayList<>();
           CommonMethods.backButton(RedeemActivity.this);


        userTransactionListAdapter = new UserTransactionListAdapter(RedeemActivity.this,transactionRequestList);


        getUserCoins();
        setRedeemRecyclerView();


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_reddeem;
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


                        UserProfile userProfile=   documentSnapshot.toObject(UserProfile.class);

                        UserProfile.ProfileData profileData = userProfile.profileData;

                        totalGameCoins = profileData.gameCoins;
                  //      walletCoinsTextview.setText(profileData.gameCoins +"");
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
        redeemListItemArrayList.add(new RedeemListItem("Paytm50 ",10,1,R.drawable.paytm_icom));
        redeemListItemArrayList.add(new RedeemListItem("Upi100",10000,100,R.drawable.upi_icon_1));
        redeemListItemArrayList.add(new RedeemListItem("Paytm100 ",10000,100,R.drawable.paytm_icom));
        redeemListItemArrayList.add(new RedeemListItem("Upi25 ",2500,25,R.drawable.upi_icon_1));
        redeemListItemArrayList.add(new RedeemListItem("Paytm25 ",2500,25,R.drawable.paytm_icom));

        RedeemRecyclerviewAdapter redeemRecyclerviewAdapter = new RedeemRecyclerviewAdapter(this, redeemListItemArrayList);
        redeemRecyclerView.setAdapter(redeemRecyclerviewAdapter);
        redeemRecyclerView.setLayoutManager(new GridLayoutManager(this,2));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void redeemMoneyIcon(RedeemListItem redeemListItem)
    {
        String connectivityStatus =Connectivity.getConnectionStatus(RedeemActivity.this);

        if(connectivityStatus.equals(ConstantsHelper.ConnectionSignalStatus.NO_CONNECTIVITY+"")||
                connectivityStatus.equals(ConstantsHelper.ConnectionSignalStatus.POOR_STRENGTH+"" ))
        {
            Toast.makeText(this, "Network is not good "+connectivityStatus, Toast.LENGTH_SHORT).show();
            return;
        }
        if(totalGameCoins< redeemListItem.getCoins())
        {
            Toast.makeText(this, "Not enough funds to redeem ", Toast.LENGTH_LONG).show();

        }
        else {
            enterPaytmUpiAlertDialog(redeemListItem);

    //     updateUserWallet(redeemListItem);
        }



    }
    public PaytmUpiDialogListener paytmUpiDialogListener()
    {
        PaytmUpiDialogListener paytmUpiDialogListener = new PaytmUpiDialogListener() {
            @Override
            public void redeemClick() {
      //          updateUserWallet(redeemListItem);
            }
        };
        return paytmUpiDialogListener;
    }
    public void enterPaytmUpiAlertDialog(RedeemListItem redeemListItem)
    {
        AlertDialog paytmUpiDialog ;
        LayoutInflater factory = LayoutInflater.from(RedeemActivity.this);
        PaytmUpiNumberLayoutBinding paytmUpiNumberLayoutBinding = DataBindingUtil.inflate(factory,R.layout.paytm_upi_number_layout,null,false);
        paytmUpiDialog = new AlertDialog.Builder(RedeemActivity.this).create();
        paytmUpiDialog.setView(paytmUpiNumberLayoutBinding.getRoot());
        paytmUpiDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.fui_transparent));
        paytmUpiDialog.show();

        // for paytm
        if(redeemListItem.getName().toLowerCase().contains("paytm"))
        {
            paytmUpiNumberLayoutBinding.upiLinear.setVisibility(View.GONE);
            paytmUpiNumberLayoutBinding.paytmLinear.setVisibility(View.VISIBLE);

            paytmUpiNumberLayoutBinding.redeemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    String paytmNumber =paytmUpiNumberLayoutBinding.paytmEdittext.getText().toString();
                    if(ValidationHelper.isValidIndianMobileNo(paytmNumber))
                    {
                        redeemListItem.setPaytmNumber(paytmNumber+"");

                        updateUserWallet(redeemListItem);
                        paytmUpiDialog.dismiss();
                    }
                    else {

                        ToastHelper.customToast(RedeemActivity.this,"Enter a Valid Number");
                    }


                }
            });
        }
        // for upi
        else {
            paytmUpiNumberLayoutBinding.upiLinear.setVisibility(View.VISIBLE);
            paytmUpiNumberLayoutBinding.paytmLinear.setVisibility(View.GONE);

            paytmUpiNumberLayoutBinding.redeemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String upiId =paytmUpiNumberLayoutBinding.upiEdittext.getText().toString();
                    if(ValidationHelper.isValidUpiId(upiId))
                    {
                        redeemListItem.setUpiID(upiId+"");

                        updateUserWallet(redeemListItem);
                        paytmUpiDialog.dismiss();
                    }
                    else {

                        ToastHelper.customToast(RedeemActivity.this,"Enter a Valid upi id");
                    }


                }
            });

        }

        paytmUpiNumberLayoutBinding.noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paytmUpiDialog.dismiss();
            }
        });

    }
    public void upiValidation()
    {

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
                       //     walletCoinsTextview.setText(profileData.gameCoins +"");




                        firebaseFirestore.collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail)+"").set(generateTransactionRequest(redeemListItem), SetOptions.merge()).


                                addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                showRewardDialog(redeemListItem.getMoney());
                                Toast.makeText(RedeemActivity.this, "Request Raised successfully", Toast.LENGTH_SHORT).show();

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


    public HashMap<String,Object> generateTransactionRequest(RedeemListItem redeemListItem)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date()); // Find todays date

        HashMap<String , Object> redeemHistory = new HashMap<>();
        HashMap<String , Object> transactions = new HashMap<>();

        UserTransactions.TransactionRequest transactionRequest = new UserTransactions.TransactionRequest();
        transactionRequest.setRequestDate(currentDateTime);
        transactionRequest.setIsPaid(false);

        transactionRequest.setCoins(redeemListItem.getCoins());
        transactionRequest.setAmount(redeemListItem.getMoney());


        transactionRequest.setRedeemType(redeemListItem.getName());


        if(redeemListItem.getName().toLowerCase().contains("upi"))
        {
            transactionRequest.setUpiId(redeemListItem.getUpiID());
        }
        else {
            transactionRequest.setPaytmNumber(redeemListItem.getPaytmNumber());

        }
        transactionRequest.setRemainingCoins(totalGameCoins);
        transactionRequest.setPaidDate(null);



        transactions.put( currentDateTime,transactionRequest);
        redeemHistory.put("userTransaction",transactions);


        return  redeemHistory;

    }


}