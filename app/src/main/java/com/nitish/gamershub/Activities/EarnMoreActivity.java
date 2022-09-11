package com.nitish.gamershub.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.ActivityEarnMoreBinding;

public class EarnMoreActivity extends BasicActivity {

    ActivityEarnMoreBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =   DataBindingUtil.setContentView(this,R.layout.activity_earn_more);


    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_earn_more;
    }
}