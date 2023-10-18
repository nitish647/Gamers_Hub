package com.nitish.gamershub.view.rewards.activity;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.nitish.gamershub.databinding.ActivityFaqBinding;
import com.nitish.gamershub.view.rewards.adapter.FaqAdapter;
import com.nitish.gamershub.model.local.FaqPojo;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;
import com.nitish.gamershub.view.base.BaseActivity;

import java.util.ArrayList;

public class FaqActivity extends BaseActivity {

    ArrayList<FaqPojo> faqPojoArrayList=  new ArrayList<>();
    ActivityFaqBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     binding = DataBindingUtil.setContentView(this, R.layout.activity_faq);

        setFaqPojoArrayList();
        setRecyclerview();
        setOnClickListeners();


    }

    @Override
    protected int getLayoutResourceId() {
        return  R.layout.activity_faq;
    }

    public void setOnClickListeners()
    {
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void setRecyclerview()
    {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FaqAdapter faqAdapter = new FaqAdapter(faqPojoArrayList);
        binding.recyclerView.setAdapter(faqAdapter);
    }

    public void setFaqPojoArrayList()
    {
        faqPojoArrayList.add(new FaqPojo("How to earn coins by playing games at Gamers Hub ?",
                "To earn coins , open a game -> play the game for the given time -> you will earn coins according to it ." ));

        faqPojoArrayList.add(new FaqPojo("Do I need money to play the games ?",
                "No, you don't need to pay any money to play the games " ));

        faqPojoArrayList.add(new FaqPojo("What are the additional ways to earn money ?",
                "You can earn more money by using daily bonus and watch video rewards " ));
        faqPojoArrayList.add(new FaqPojo("How to use the game coins earned?",
                "You can redeem your coins into your payment or upi account ." ));

        faqPojoArrayList.add(new FaqPojo("How much time it will take to complete a transaction request?",
                AppHelper.getGamersHubDataGlobal().getMessage().getPayoutMessage()));

        faqPojoArrayList.add(new FaqPojo("Do I need internet connection to play the game?",
                "Yes , you need a proper internet connection to play the games. " ));

        faqPojoArrayList.add(new FaqPojo("Do I need to install additional app to play the games?",
                "No, you don't need to install anything else , you can play all the games within the app. " ));

        faqPojoArrayList.add(new FaqPojo("How do I delete an account ?",
                "You can delete your account by contacting us on our mail " ));

        faqPojoArrayList.add(new FaqPojo("Where I can contact if I need additional help?",
                "You can contact in the contact us section in the profile page." ));

        faqPojoArrayList.add(new FaqPojo("What happens to an inactive account?",
                "If the account is not active for 2 months , we will delete the account and all the money will be lost?" ));

    }


}