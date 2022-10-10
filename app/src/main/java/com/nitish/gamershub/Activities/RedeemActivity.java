package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.AppHelper.getUserProfileGlobalData;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.nitish.gamershub.Adapters.RedeemRecyclerviewAdapter;
import com.nitish.gamershub.Adapters.UserTransactionListAdapter;
import com.nitish.gamershub.Pojo.FireBase.UserTransactions;
import com.nitish.gamershub.Pojo.FireBase.RedeemListItem;
import com.nitish.gamershub.Pojo.FireBase.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.CommonMethods;
import com.nitish.gamershub.Utils.Connectivity;
import com.nitish.gamershub.Utils.ConstantsHelper;
import com.nitish.gamershub.Utils.DateTimeHelper;
import com.nitish.gamershub.Utils.ProgressBarHelper;
import com.nitish.gamershub.Utils.ToastHelper;
import com.nitish.gamershub.Utils.ValidationHelper;
import com.nitish.gamershub.databinding.PaytmUpiNumberLayoutBinding;

import java.util.ArrayList;

public class RedeemActivity extends BasicActivity {

  //  TextView walletCoinsTextview;
    FirebaseFirestore firebaseFirestore;

    RecyclerView redeemRecyclerView;
    ArrayList<RedeemListItem> redeemListItemArrayList = new ArrayList<>();
    int totalGameCoins=0;
    ArrayList<UserTransactions.TransactionRequest> transactionRequestList;

    RedeemListItem redeemListItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddeem);
        redeemRecyclerView = findViewById(R.id.redeemRecyclerView);


        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = ProgressBarHelper.setProgressBarDialog(this);
           transactionRequestList = new ArrayList<>();
           CommonMethods.backButton(RedeemActivity.this);




        getCoins();

        totalGameCoins = getUserProfileGlobalData().getProfileData().getGameCoins();
        setRedeemRecyclerView();


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_reddeem;
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
        this.redeemListItem = redeemListItem;
        String connectivityStatus =Connectivity.getConnectionStatus(RedeemActivity.this);

        if(connectivityStatus.equals(ConstantsHelper.ConnectionSignalStatus.NO_CONNECTIVITY+"")||
                connectivityStatus.equals(ConstantsHelper.ConnectionSignalStatus.POOR_STRENGTH+"" ))
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
                        redeemListItem.setPaytmNumber(paytmNumber+"");

                        UserTransactions userTransactions =   setUserTransactions(getUserProfileGlobalData().getUserTransactions(),redeemListItem);
                        updateUserWalletForTransaction(- redeemListItem.getCoins(),setUserDataLister(redeemListItem),userTransactions);


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







        return  transactionRequest;

    }

    public SetUserDataOnCompleteListener setUserDataLister(RedeemListItem redeemListItem)
    {
        return  new SetUserDataOnCompleteListener() {
            @Override
            public void onTaskSuccessful() {


                String message= "Congratulations , Redeem request generated for "+redeemListItem.getMoney();
                showRewardDialog(message);
                Toast.makeText(RedeemActivity.this, "Request Raised successfully", Toast.LENGTH_SHORT).show();


                getCoins();
            }
        };

    }

}