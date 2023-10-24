package com.nitish.gamershub.view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.GameRewardDialogBinding;
import com.nitish.gamershub.model.local.DialogItems;

public class DialogGamerReward extends DialogFragment {


    private DialogListener dialogListener;
    GameRewardDialogBinding binding;

    private DialogItems dialogItems;

    public DialogGamerReward(DialogItems dialogItems, DialogListener mDialogListener) {

        this.dialogListener = mDialogListener;
        this.dialogItems = dialogItems;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.binding = DataBindingUtil.inflate(inflater, R.layout.game_reward_dialog, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null || getDialog().getWindow() != null) {

            getDialog().getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));

        }

        setOnClickListener();
        initViews();
    }

    private void initViews()
    {
        binding.earnedCoinsTextview.setText(dialogItems.getMessage());

        if(dialogItems.getRawAnimation()!=null)
        {
            binding.gameCoinsImage.setAnimation(dialogItems.getRawAnimation());

        }
    }
    private void setOnClickListener() {
        binding.earnedCoinsTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.onYesClick();
                dismiss();
            }
        });
        binding.cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        dialogListener.onDialogDismissed();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        return super.onCreateDialog(savedInstanceState);
    }

    public static DialogGamerReward newInstance(DialogItems dialogItems, DialogListener mDialogListener) {


        if (mDialogListener==null)
        {
            mDialogListener = new DialogListener() {
                @Override
                public void onYesClick() {

                }

                @Override
                public void onNoClick() {

                }
            };
        }

        DialogGamerReward fragment = new DialogGamerReward(dialogItems, mDialogListener);
        return fragment;
    }


}
