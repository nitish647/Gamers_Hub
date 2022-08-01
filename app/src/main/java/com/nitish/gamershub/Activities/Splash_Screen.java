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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nitish.gamershub.Activities.HomeActivity;
import com.nitish.gamershub.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class Splash_Screen extends AppCompatActivity {
    SharedPreferences sh;
    SharedPreferences.Editor editor;
    TextView textView;
    String data;
    String    gameData;
    RequestQueue requestQueue;

    public static String MaterData = "MasterData";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        Paper.init(this);
        requestQueue = Volley.newRequestQueue(this);
        mAuth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.warning);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                get_data();
                getMaterData();

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
    public void getMaterData()
    {

        String url = getString(R.string.dbGitUrl)+"gamers_hub_data/masterData.json";

        Log.d("url",url);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray response) {

                Log.d("gResponse","response json array of mater data "+response);

            //    Toast.makeText(Splash_Screen.this, response+"", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject =  response.getJSONObject(0);
                    Log.d("gResponse","response jsonObject of mater data "+jsonObject);
                    Paper.book().write(Splash_Screen.MaterData,jsonObject+"");



                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    // when not singed in
                    Intent intent;
                    if(currentUser==null) {
                        intent = new Intent(getApplicationContext(), LoginPage.class);
                    }
                    else
                    // when already  singed in
                    {
                        Toast.makeText(Splash_Screen.this, "hello "+currentUser.getEmail(), Toast.LENGTH_SHORT).show();

                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                    }
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    Log.d("gError","error in  mater data "+e.toString());
                    e.printStackTrace();
                }







        }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("gError","error in mater data "+error);

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }


}