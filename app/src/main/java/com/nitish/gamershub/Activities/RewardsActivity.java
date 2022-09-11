package com.nitish.gamershub.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.ActivityRewardsBinding;

public class RewardsActivity extends BasicActivity {

    ActivityRewardsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_rewards);
        setOnclickListeners();
    }

    public void setOnclickListeners()
    {
        binding.transactionHistoryRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RewardsActivity.this,TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });

        binding.redeemCoinsRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RewardsActivity.this,RedeemActivity.class);
                startActivity(intent);
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_rewards;
    }
}