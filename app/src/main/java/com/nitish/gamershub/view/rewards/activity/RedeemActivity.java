package com.nitish.gamershub.view.rewards.activity;

import static com.nitish.gamershub.utils.AppConstants.paytmImageLink;
import static com.nitish.gamershub.utils.AppConstants.upiImageLink;

import static com.nitish.gamershub.utils.AppConstants.TransactionStatusPending;
import static com.nitish.gamershub.utils.AppConstants.TransactionMessage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nitish.gamershub.databinding.ActivityReddeemBinding;
import com.nitish.gamershub.databinding.PaytmUpiNumberLayoutBinding;
import com.nitish.gamershub.model.firebase.profileData.ProfileData;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.view.loginSingup.viewmodelRepo.LoginSignUpViewModel;
import com.nitish.gamershub.view.rewards.adapter.RedeemRecyclerviewAdapter;
import com.nitish.gamershub.model.firebase.RedeemCoins;
import com.nitish.gamershub.model.firebase.userTransaction.UserTransactions;
import com.nitish.gamershub.model.firebase.RedeemListItem;
import com.nitish.gamershub.model.firebase.userProfile.UserProfile;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.utils.CommonMethods;
import com.nitish.gamershub.utils.Connectivity;
import com.nitish.gamershub.utils.timeUtils.DateTimeHelper;
import com.nitish.gamershub.utils.ToastHelper;
import com.nitish.gamershub.utils.ValidationHelper;
import com.nitish.gamershub.view.base.BaseActivity;

import java.util.ArrayList;

public class RedeemActivity extends BaseActivity {

    ActivityReddeemBinding binding;


    //  TextView walletCoinsTextview;
    FirebaseFirestore firebaseFirestore;

    RecyclerView redeemRecyclerView;
    ArrayList<RedeemListItem> redeemListItemArrayList = new ArrayList<>();
    int totalGameCoins = 0;
    ArrayList<UserTransactions.TransactionRequest> transactionRequestList;
    RedeemCoins redeemCoins;
    RedeemListItem redeemListItem;

    private LoginSignUpViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reddeem);
        redeemRecyclerView = findViewById(R.id.redeemRecyclerView);
        viewModel = ViewModelProviders.of(this).get(LoginSignUpViewModel.class);

        firebaseFirestore = FirebaseFirestore.getInstance();
        transactionRequestList = new ArrayList<>();
        CommonMethods.backButton(RedeemActivity.this);


//        hideLoader();
        getCoins();
        setUpBannerAd();
        totalGameCoins = getPreferencesMain().getUserProfile().getProfileData().getGameCoins();
//        getRedeemListData();
        getRedeemCoinsData();
        setonClickListener();
        setViews();
        bindObservers();


    }

    private void setViews() {
        binding.redeemMessageText.setText(getPreferencesMain().getGamersHubData().getMessage().getPayoutMessage() + "");
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_reddeem;
    }

    private void bindObservers() {
        viewModel.getUerProfileLD.observe(this, new Observer<NetworkResponse<UserProfile>>() {
            @Override
            public void onChanged(NetworkResponse<UserProfile> response) {
                if (response instanceof NetworkResponse.Success) {
                    hideLoader();


                    totalGameCoins = getPreferencesMain().getUserProfile().getProfileData().getGameCoins();


                } else if (response instanceof NetworkResponse.Error) {

                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

                    showLoader();
                }
            }
        });

        viewModel.updateUserProfileLD.observe(this, new Observer<NetworkResponse<Object>>() {
            @Override
            public void onChanged(NetworkResponse<Object> response) {
                if (response instanceof NetworkResponse.Success) {

                    String message = getString(R.string.congratulations_redeem_request_generated_for) + redeemListItem.getMoney();
                    showRewardDialog(message, null);
                    Toast.makeText(RedeemActivity.this, getString(R.string.request_raised_successfully), Toast.LENGTH_SHORT).show();


                    getCoins();
                    hideLoader();

                } else if (response instanceof NetworkResponse.Error) {

                    String message = ((NetworkResponse.Error<Object>) response).getMessage();

                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

                    showLoader();
                }
            }
        });

        viewModel.getRedeemCoinsLD.observe(this, new Observer<NetworkResponse<RedeemCoins>>() {
            @Override
            public void onChanged(NetworkResponse<RedeemCoins> response) {
                if (response instanceof NetworkResponse.Success) {
                    hideLoader();

                    RedeemCoins redeemCoins = ((NetworkResponse.Success<RedeemCoins>) response).getData();

                    redeemListItemArrayList = redeemCoins.getRedeemListItemList();
                    setRedeemRecyclerView();
                } else if (response instanceof NetworkResponse.Error) {

                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

                    showLoader();
                }
            }
        });
        viewModel.setRedeemCoinsLD.observe(this, new Observer<NetworkResponse<Object>>() {
            @Override
            public void onChanged(NetworkResponse<Object> response) {
                if (response instanceof NetworkResponse.Success) {
                    hideLoader();
                    Toast.makeText(RedeemActivity.this, getString(R.string.added_the_data), Toast.LENGTH_SHORT).show();

                } else if (response instanceof NetworkResponse.Error) {

                    hideLoader();
                } else if (response instanceof NetworkResponse.Loading) {

                    showLoader();
                }
            }
        });


    }


    private void setonClickListener() {
        binding.backButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                setRedeemListData();
                return true;

            }
        });
    }

    private void setRedeemRecyclerView() {

        if (redeemListItemArrayList == null || redeemListItemArrayList.size() == 0) {
            binding.noTransactionRelative.setVisibility(View.VISIBLE);
        } else {
            binding.noTransactionRelative.setVisibility(View.GONE);

        }
        RedeemRecyclerviewAdapter redeemRecyclerviewAdapter = new RedeemRecyclerviewAdapter(this, redeemListItemArrayList);
        redeemRecyclerView.setAdapter(redeemRecyclerviewAdapter);
        redeemRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));


    }

    private void setRedeemListData() {


        redeemListItemArrayList = new ArrayList<>();
//        redeemListItemArrayList.add(new RedeemListItem("Upi10 ", 1000, 10, upiImageLink));
//        redeemListItemArrayList.add(new RedeemListItem("Paytm10 ", 1000, 10, paytmImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Upi25 ", 2500, 25, upiImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Paytm25 ", 2500, 25, paytmImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Upi50 ", 5000, 50, upiImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Paytm50 ", 5000, 50, paytmImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Upi100", 10000, 100, upiImageLink));
        redeemListItemArrayList.add(new RedeemListItem("Paytm100 ", 10000, 100, paytmImageLink));


        redeemCoins = new RedeemCoins();
        redeemCoins.setRedeemListItemList(redeemListItemArrayList);


        hideLoader();


        calSetRedeemCoinsData(redeemCoins);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void redeemMoneyItemClick(RedeemListItem redeemListItem) {
        this.redeemListItem = redeemListItem;
        String connectivityStatus = Connectivity.getConnectionStatus(RedeemActivity.this);

        if (!isInternetAvailable()) {
            Toast.makeText(this, getString(R.string.network_is_not_good) + connectivityStatus, Toast.LENGTH_SHORT).show();
            return;
        }
        if (totalGameCoins < this.redeemListItem.getCoins()) {
            Toast.makeText(this, getString(R.string.not_enough_funds_to_redeem), Toast.LENGTH_LONG).show();

        } else {
            enterPaytmUpiAlertDialog();

            //     updateUserWallet(redeemListItem);
        }


    }

    public void enterPaytmUpiAlertDialog() {
        AlertDialog paytmUpiDialog;
        LayoutInflater factory = LayoutInflater.from(RedeemActivity.this);
        PaytmUpiNumberLayoutBinding paytmUpiNumberLayoutBinding = DataBindingUtil.inflate(factory, R.layout.paytm_upi_number_layout, null, false);
        paytmUpiDialog = new AlertDialog.Builder(RedeemActivity.this).create();
        paytmUpiDialog.setView(paytmUpiNumberLayoutBinding.getRoot());
        paytmUpiDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.fui_transparent));
        paytmUpiDialog.show();

        // for paytm
        if (redeemListItem.getName().toLowerCase().contains("paytm")) {
            paytmUpiNumberLayoutBinding.upiLinear.setVisibility(View.GONE);
            paytmUpiNumberLayoutBinding.paytmLinear.setVisibility(View.VISIBLE);

            paytmUpiNumberLayoutBinding.redeemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    String paytmNumber = paytmUpiNumberLayoutBinding.paytmEdittext.getText().toString();
                    if (ValidationHelper.isValidIndianMobileNo(paytmNumber)) {
                        paytmUpiDialog.dismiss();
                        redeemListItem.setPaytmNumber(paytmNumber + "");

                        performUpdateUserTransaction();
//                        UserTransactions userTransactions = setUserTransactions(getPreferencesMain().getUserProfile().getUserTransactions(), redeemListItem);
//                        updateUserWalletForTransaction(-redeemListItem.getCoins(), setUserDataLister(redeemListItem), userTransactions);


                    } else {

                        ToastHelper.customToast(RedeemActivity.this, getString(R.string.enter_a_valid_number));
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

                    String upiId = paytmUpiNumberLayoutBinding.upiEdittext.getText().toString();
                    if (ValidationHelper.isValidUpiId(upiId)) {
                        redeemListItem.setUpiID(upiId + "");

                        performUpdateUserTransaction();
//                        UserTransactions userTransactions = setUserTransactions(getPreferencesMain().getUserProfile().getUserTransactions(), redeemListItem);
//                        updateUserWalletForTransaction(-redeemListItem.getCoins(), setUserDataLister(redeemListItem), userTransactions);

                        paytmUpiDialog.dismiss();
                    } else {

                        ToastHelper.customToast(RedeemActivity.this, getString(R.string.enter_a_valid_upi_id));
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


    public void getCoins() {
        totalGameCoins = getPreferencesMain().getUserProfile().getProfileData().getGameCoins();
//        getUserProfile();

    }

    public UserTransactions setUserTransactions(UserTransactions userTransactions, RedeemListItem redeemListItem) {
        if (userTransactions == null) {
            userTransactions = new UserTransactions();
        }


        ArrayList<UserTransactions.TransactionRequest> transactionRequestArrayList;
        if (userTransactions.getTransactionRequestArrayList() != null) {
            transactionRequestArrayList = userTransactions.getTransactionRequestArrayList();
        } else {
            transactionRequestArrayList = new ArrayList<>();
        }


        transactionRequestArrayList.add(generateTransactionRequest(redeemListItem));
        userTransactions.setTransactionRequestArrayList(transactionRequestArrayList);

        return userTransactions;


    }


    public UserTransactions.TransactionRequest generateTransactionRequest(RedeemListItem redeemListItem) {


        String currentDateTime = DateTimeHelper.getDatePojo().getGetCurrentDateString(); // Find todays date


        UserTransactions.TransactionRequest transactionRequest = new UserTransactions.TransactionRequest();
        transactionRequest.setRequestDate(currentDateTime);
        transactionRequest.setTransactionComplete(false);

        transactionRequest.setCoins(redeemListItem.getCoins());
        transactionRequest.setAmount(redeemListItem.getMoney());


        transactionRequest.setRedeemType(redeemListItem.getName());

        transactionRequest.setTransactionStatus(TransactionStatusPending);
        transactionRequest.setTransactionMessage(TransactionMessage);

        if (redeemListItem.getName().toLowerCase().contains("upi")) {
            transactionRequest.setUpiId(redeemListItem.getUpiID());
        } else {
            transactionRequest.setPaytmNumber(redeemListItem.getPaytmNumber());

        }
        String transactionId = redeemListItem.getName() + "_" + AppHelper.generateRandomNumber();
        transactionId = transactionId.replace(" ", "");
        transactionRequest.setTransactionId(transactionId);
        transactionRequest.setRemainingCoins(totalGameCoins);
        transactionRequest.setPaidDate(null);


        return transactionRequest;

    }


    public void performUpdateUserTransaction() {
        UserTransactions userTransactions = setUserTransactions(getPreferencesMain().getUserProfile().getUserTransactions(), redeemListItem);

        updateUserWalletForTransaction(-redeemListItem.getCoins(), userTransactions);

    }

    public UserProfile updateUserWalletForTransaction(int amount, UserTransactions userTransactions) {

        UserProfile userProfile = getPreferencesMain().getUserProfile();
        ProfileData profileData = userProfile.getProfileData();
        int gameCoins = profileData.getGameCoins();
        int totalCoins = gameCoins + amount;
        profileData.setGameCoins(totalCoins);
        userProfile.setProfileData(profileData);

        userProfile.setUserTransactions(userTransactions);


        hideLoader();
        callUpdateUserProfile(userProfile, "");
//        setUserProfile(userProfile, setUserDataOnCompleteListener);

        return userProfile;
    }


    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();


        binding.googleBannerAdView.resume();


    }


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

    public void setUpBannerAd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.googleBannerAdView.loadAd(adRequest);

    }

    private void getRedeemCoinsData() {
        viewModel.callGetRedeemCoins();
    }

    private void calSetRedeemCoinsData(RedeemCoins redeemCoins) {

        viewModel.callSetRedeemCoinsLD(redeemCoins);
    }

    private void getUserProfile() {
        viewModel.callGetUserProfile();
    }

    private void callUpdateUserProfile(UserProfile userProfile, String usage) {

        viewModel.callUpdateUserProfile(userProfile);
    }
}