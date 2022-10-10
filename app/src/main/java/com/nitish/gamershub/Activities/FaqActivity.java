package com.nitish.gamershub.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.nitish.gamershub.Pojo.FaqPojo;
import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.ActivityFaqBinding;

import java.util.ArrayList;

public class FaqActivity extends AppCompatActivity {

    ArrayList<FaqPojo> faqPojoArrayList=  new ArrayList<>();
    ActivityFaqBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     binding = DataBindingUtil.setContentView(this, R.layout.activity_faq);




    }


}