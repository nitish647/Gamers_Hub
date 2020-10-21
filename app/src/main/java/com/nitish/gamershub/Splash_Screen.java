package com.nitish.gamershub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Splash_Screen extends AppCompatActivity {
    SharedPreferences sh;
    SharedPreferences.Editor editor;
    TextView textView;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
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


                } else {
                    Toast.makeText(getBaseContext(), "document does not exists", Toast.LENGTH_LONG).show();

                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("json", data);
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}