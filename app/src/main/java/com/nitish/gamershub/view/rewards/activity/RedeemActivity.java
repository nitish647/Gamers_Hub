package com.nitish.gamershub.view.rewards.activity;

import static com.nitish.gamershub.utils.AppHelper.getUserProfileGlobalData;
import static com.nitish.gamershub.utils.AppConstants.TransactionStatusPending;
import static com.nitish.gamershub.utils.AppConstants.TransactionMessage;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.nitish.gamershub.databinding.ActivityReddeemBinding;
import com.nitish.gamershub.databinding.PaytmUpiNumberLayoutBinding;
import com.nitish.gamershub.view.rewards.adapter.RedeemRecyclerviewAdapter;
import com.nitish.gamershub.model.firebase.RedeemCoins;
import com.nitish.gamershub.model.firebase.UserTransactions;
import com.nitish.gamershub.model.firebase.RedeemListItem;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.CommonMethods;
import com.nitish.gamershub.utils.Connectivity;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;
import com.nitish.gamershub.utils.ProgressBarHelper;
import com.nitish.gamershub.utils.ToastHelper;
import com.nitish.gamershub.utils.ValidationHelper;
import com.nitish.gamershub.view.base.BaseActivity;

import org.json.JSONObject;

import java.util.ArrayList;

public class RedeemActivity extends BaseActivity {

    ActivityReddeemBinding binding;

    String upiImageLink="https://i.ibb.co/JB3b9fc/upi-icon-1.png";
    String paytmImageLink=  "https://i.ibb.co/C9VzmMx/paytm-icom.png";
  //  TextView walletCoinsTextview;
    FirebaseFirestore firebaseFirestore;

    RecyclerView redeemRecyclerView;
    ArrayList<RedeemListItem> redeemListItemArrayList = new ArrayList<>();
    int totalGameCoins=0;
    ArrayList<UserTransactions.TransactionRequest> transactionRequestList;
    RedeemCoins redeemCoins;
    RedeemListItem redeemListItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    binding =   DataBindingUtil.setContentView(this,  R.layout.activity_reddeem);
        redeemRecyclerView = findViewById(R.id.redeemRecyclerView);

        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = ProgressBarHelper.setProgressBarDialog(this);
           transactionRequestList = new ArrayList<>();
           CommonMethods.backButton(RedeemActivity.this);



        getCoins();
        setUpBannerAd();
        totalGameCoins = getUserProfileGlobalData().getProfileData().getGameCoins();
        getRedeemListData();
        setonClickListener();
        setViews();



    }

    private void setViews() {
        binding.redeemMessageText.setText(AppHelper.getGamersHubDataGlobal().getMessage().getPayoutMessage()+"");
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_reddeem;
    }



    private void setonClickListener()
    {
        binding.backButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                setRedeemListData();
                return true;

            }
        });
    }
    private void setRedeemRecyclerView()
    {

        if(redeemListItemArrayList==null || redeemListItemArrayList.size()==0)
        {
            binding.noTransactionRelative.setVisibility(View.VISIBLE);
        }
        else {
            binding.noTransactionRelative.setVisibility(View.GONE);

        }
        RedeemRecyclerviewAdapter redeemRecyclerviewAdapter = new RedeemRecyclerviewAdapter(this, redeemListItemArrayList);
        redeemRecyclerView.setAdapter(redeemRecyclerviewAdapter);
        redeemRecyclerView.setLayoutManager(new GridLayoutManager(this,2));



    }

    private void getRedeemListData()
    {
        showProgressBar();
        getGamersHubRedeemCoinsList(new OnFirestoreDataCompleteListener() {
            @Override
            public void OnComplete(DocumentSnapshot documentSnapshot) {


                JSONObject jsonObject = new JSONObject(documentSnapshot.getData());
                RedeemCoins redeemCoins = new Gson().fromJson(jsonObject.toString(),RedeemCoins.class);
                redeemListItemArrayList =  redeemCoins.getRedeemListItemList();
                setRedeemRecyclerView();
            }
        });
    }
    private void setRedeemListData()
    {


        redeemListItemArrayList = new ArrayList<>();
        redeemListItemArrayList.add(new RedeemListItem("Upi10 ",1000,10,upiImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Paytm10 ",1000,10,paytmImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Upi25 ",2500,25,upiImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Paytm25 ",2500,25,paytmImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Upi50 ",5000,50,upiImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Paytm50 ",5000,50,paytmImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Upi100",10000,100,upiImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Paytm100 ",10000,100,paytmImageLink));


        redeemCoins = new RedeemCoins();
        redeemCoins.setRedeemListItemList(redeemListItemArrayList);


        showProgressBar();
        setGamersHubRedeemCoinsList(new OnFirestoreDataCompleteListener() {

            @Override
            public void OnComplete(DocumentSnapshot documentSnapshot) {
                dismissProgressBar();
                setGamersHubRedeemCoinsList().set(redeemCoins).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(RedeemActivity.this, "Added the data", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RedeemActivity.this, "FAILED  the data "+e, Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void redeemMoneyItemClick(RedeemListItem redeemListItem)
    {
        this.redeemListItem = redeemListItem;
        String connectivityStatus =Connectivity.getConnectionStatus(RedeemActivity.this);

        if(!isInternetAvailable())
        {
            Toast.makeText(this, "Network is not good "+connectivityStatus, Toast.LENGTH_SHORT).show();
            return;
        }
        if(totalGameCoins< this.redeemListItem.getCoins())
        {
            Toast.makeText(this, "Not enough funds to redeem ", Toast.LENGTH_LONG).show();

        }
        else {
            enterPaytmUpiAlertDialog();

    //     updateUserWallet(redeemListItem);
        }



    }

    public void enterPaytmUpiAlertDialog()
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
                        paytmUpiDialog.dismiss();
                        redeemListItem.setPaytmNumber(paytmNumber+"");

                        UserTransactions userTransactions =   setUserTransactions(getUserProfileGlobalData().getUserTransactions(),redeemListItem);
                        updateUserWalletForTransaction(- redeemListItem.getCoins(),setUserDataLister(redeemListItem),userTransactions);



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

                        UserTransactions userTransactions =   setUserTransactions(getUserProfileGlobalData().getUserTransactions(),redeemListItem);
                        updateUserWalletForTransaction(- redeemListItem.getCoins(),setUserDataLister(redeemListItem),userTransactions);

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







    public void getCoins()
    {
        progressDialog.show();
        getUserProfileGlobal(new GetUserProfileDataListener() {
            @Override
            public void onTaskSuccessful(UserProfile userProfile) {
                progressDialog.dismiss();
                totalGameCoins = getUserProfileGlobalData().getProfileData().getGameCoins();

            }
        });
    }

    public UserTransactions setUserTransactions(UserTransactions userTransactions, RedeemListItem redeemListItem)
    {
        if(userTransactions == null)
        {
            userTransactions = new UserTransactions();
        }


        ArrayList<UserTransactions.TransactionRequest>  transactionRequestArrayList;
        if(userTransactions.getTransactionRequestArrayList()!=null) {
            transactionRequestArrayList = userTransactions.getTransactionRequestArrayList();
        }
        else
        {
            transactionRequestArrayList = new ArrayList<>();
        }


        transactionRequestArrayList.add(generateTransactionRequest(redeemListItem));
        userTransactions.setTransactionRequestArrayList(transactionRequestArrayList);

        return userTransactions;


    }


    public UserTransactions.TransactionRequest generateTransactionRequest(RedeemListItem redeemListItem)
    {



        String currentDateTime = DateTimeHelper.getDatePojo().getGetCurrentDateString(); // Find todays date


        UserTransactions.TransactionRequest transactionRequest = new UserTransactions.TransactionRequest();
        transactionRequest.setRequestDate(currentDateTime);
        transactionRequest.setTransactionComplete(false);

        transactionRequest.setCoins(redeemListItem.getCoins());
        transactionRequest.setAmount(redeemListItem.getMoney());


        transactionRequest.setRedeemType(redeemListItem.getName());

        transactionRequest.setTransactionStatus(TransactionStatusPending);
        transactionRequest.setTransactionMessage(TransactionMessage);

        if(redeemListItem.getName().toLowerCase().contains("upi"))
        {
            transactionRequest.setUpiId(redeemListItem.getUpiID());
        }
        else {
            transactionRequest.setPaytmNumber(redeemListItem.getPaytmNumber());

        }
        String transactionId =  redeemListItem.getName()+"_"+ AppHelper.generateRandomNumber();
        transactionId = transactionId.replace(" ","");
        transactionRequest.setTransactionId(transactionId);
        transactionRequest.setRemainingCoins(totalGameCoins);
        transactionRequest.setPaidDate(null);







        return  transactionRequest;

    }

    public SetUserDataOnCompleteListener setUserDataLister(RedeemListItem redeemListItem)
    {
        return  new SetUserDataOnCompleteListener() {
            @Override
            public void onTaskSuccessful() {


                String message= "Congratulations , Redeem request generated for ₹"+redeemListItem.getMoney();
                showRewardDialog(message);
                Toast.makeText(RedeemActivity.this, "Request Raised successfully", Toast.LENGTH_SHORT).show();


                getCoins();
            }
        };

    }


    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();



            binding.googleBannerAdView.resume();




    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {

            binding.googleBannerAdView.destroy();


        super.onDestroy();
    }
    @Override
    protected void onPause() {

            binding.googleBannerAdView.pause();

        super.onPause();
    }

    public void setUpBannerAd()
    {

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.googleBannerAdView.loadAd(adRequest);

    }

}