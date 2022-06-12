package com.nitish.gamershub.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nitish.gamershub.Activities.HomeActivity;
import com.nitish.gamershub.R;

import io.paperdb.Paper;

public class Splash_Screen extends AppCompatActivity {
    SharedPreferences sh;
    SharedPreferences.Editor editor;
    TextView textView;
    String data;
    String    gameData;

    public static String MaterData = "MasterData";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        Paper.init(this);

        textView = findViewById(R.id.warning);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                get_data();

            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText("Internet is slow...");
            }
        }, 7000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText("PLease check your internet connection and try again");
            }
        }, 11000);
    }


    public void get_data() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference noteref = db.document("nitish/gamers hub");
        noteref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    data = documentSnapshot.getString("json_data");
                         gameData =  documentSnapshot.getString("gameData");
                    Paper.book().write(MaterData,gameData);

                } else {
                    Toast.makeText(getBaseContext(), "document does not exists", Toast.LENGTH_LONG).show();

                }

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("json", data);
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("error11",e.toString());
            }
        });
    }


}