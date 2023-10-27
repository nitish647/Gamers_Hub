package com.nitish.gamershub.view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.nitish.gamershub.R;
import com.nitish.gamershub.databinding.BlockUserDialogBinding;
import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.utils.AppHelper;

public class SuspensionDialog extends DialogFragment {
    private DialogListener dialogListener;
    BlockUserDialogBinding binding;

    private DialogItems dialogItems;

    public SuspensionDialog(DialogItems dialogItems, DialogListener mDialogListener) {

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

        this.binding = DataBindingUtil.inflate(inflater, R.layout.block_user_dialog, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        AppHelper.setDialogBackgroundTransparent(getDialog());

        setOnClickListener();
        initViews();
    }

    private void initViews() {


        binding.titleTextview.setText(dialogItems.getTitle());

        if(dialogItems.getMessage()!=null)
        binding.bodyTextview.setText(Html.fromHtml(dialogItems.getMessage()));

//        binding.yesButton.setText(dialogItems.getYesTitle());
//        binding.noButton.setText(dialogItems.getNoTitle());
//        binding.dialogIcon.setImageResource(dialogItems.getDialogIcon());
//
//        if (dialogItems.getSingleButton()) {
//            binding.yesButton.setText("Okay");
//            binding.noButton.setVisibility(View.GONE);
//        }


    }

    private void setOnClickListener() {
        binding.understandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.onYesClick();
                dismiss();
            }
        });
//        binding.noButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dismiss();
//            }
//        });

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

    public static SuspensionDialog newInstance(DialogItems dialogItems, DialogListener mDialogListener) {


        if (mDialogListener == null) {
            mDialogListener = new DialogListener() {
                @Override
                public void onYesClick() {

                }

                @Override
                public void onNoClick() {

                }
            };
        }

        return new SuspensionDialog(dialogItems, mDialogListener);
    }

}
