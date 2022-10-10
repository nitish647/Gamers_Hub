package com.nitish.gamershub.Utils;

import static com.nitish.gamershub.Utils.ConstantsHelper.FirebaseFCMToken;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import io.paperdb.Paper;

public class NotificationHelper {

    public static void generateFcmToken()
    {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {

                if(task.isSuccessful())
                {

                    String fcmToken = task.getResult();

                    Log.d("fcmToken","fcmToken generated successfully "+fcmToken);
                  AppHelper.saveFireBaseFcmToken(fcmToken);

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("fcmToken","fcmToken generation failed : "+e);
            }
        });

    }


}