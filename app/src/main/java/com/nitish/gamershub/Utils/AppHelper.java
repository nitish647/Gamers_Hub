package com.nitish.gamershub.Utils;

import static com.nitish.gamershub.Utils.ConstantsHelper.GoogleSignInAccountUser;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import io.paperdb.Paper;

public class AppHelper {




    //--------------------login page -----------------//
    public static void saveGoogleSignInAccountUser(GoogleSignInAccount googleSignInAccount)
    {
        Paper.book().write(GoogleSignInAccountUser,googleSignInAccount);
    }

    public static GoogleSignInAccount getGoogleSignInAccountUser()
    {
       return (GoogleSignInAccount) Paper.book().read(GoogleSignInAccountUser);
    }



}
