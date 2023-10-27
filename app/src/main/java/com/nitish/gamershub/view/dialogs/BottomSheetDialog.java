package com.nitish.gamershub.view.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nitish.gamershub.databinding.BottomDialogSheetBinding;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.view.rewards.activity.RewardsActivity;
import com.nitish.gamershub.model.firebase.TimerStatus;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.AppHelper;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    BottomDialogSheetBinding binding;

  static   String ARG_PARAM1;

    HomeActivity parentActivity;
    TimerStatus timerStatus;

    public static BottomSheetDialog newInstance(String param1) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        bottomSheetDialog.setArguments(args);
        return bottomSheetDialog;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_dialog_sheet,container,false);
        parentActivity =(HomeActivity) unwrap(binding.getRoot().getContext());
        timerStatus = AppHelper.getUserProfileGlobalData().getTimerStatus();
        setViews();
        setOnClickListeners();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void setOnClickListeners()
    {

        binding.watchVideoRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parentActivity, RewardsActivity.class);
                startActivity(intent);
                dismiss();
            }
        });
        binding.dailyBonusRelative.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(parentActivity, RewardsActivity.class);
            startActivity(intent);
            dismiss();
        }
    });
    }
    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }
    public BottomDialogSheetBinding getBinding()
    {
        return binding;
    }


    public void setViews()
    {


        if(timerStatus.getDailyBonus().getClaimed())
        {
            binding.dailyBonusRelative.setVisibility(View.GONE);
        }
        else {
            binding.dailyBonusRelative.setVisibility(View.VISIBLE);
        }

        if(timerStatus.getWatchViewReward().isClaimed())
        {
            binding.watchVideoRelative.setVisibility(View.GONE);
        }
        else {
            binding.watchVideoRelative.setVisibility(View.VISIBLE);
        }
    }
}
