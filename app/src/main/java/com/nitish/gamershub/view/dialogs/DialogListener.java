package com.nitish.gamershub.view.dialogs;

public interface DialogListener {


    void onYesClick();
    void onNoClick();
    default void onDialogDismissed(){}

}
