package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.ConstantsHelper.From;
import static com.nitish.gamershub.Utils.ConstantsHelper.IntentData;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.nitish.gamershub.Interface.ConfirmationDialogListener2;
import com.nitish.gamershub.Pojo.DialogHelperPojo;
import com.nitish.gamershub.R;
import com.nitish.gamershub.Utils.ConstantsHelper;
import com.nitish.gamershub.Utils.NotificationHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import io.paperdb.Paper;

public class Splash_Screen extends BasicActivity {
    SharedPreferences sh;
    SharedPreferences.Editor editor;
    TextView textView;
    String data;
    String    gameData;
    RequestQueue requestQueue;

    static Context context;
    public static String MaterData = "MasterData";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        requestQueue = Volley.newRequestQueue(this);
        mAuth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.warning);
        context = Splash_Screen.this;
        Paper.init(this);


        handlerFunctions();





    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_splash__screen;
    }



    private void handlerFunctions()
    {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
//                get_data();
                Splash_Screen.this.getMaterData();

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



              //      FirebaseUser currentUser = mAuth.getCurrentUser();


                    // when not singed in
                    Intent intent;
                    if( getGoogleSignInAccount()==null) {
                        intent = new Intent(Splash_Screen.this, LoginPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);;
                    }
                    else
                    // when already  singed in
                    {
                        Toast.makeText(Splash_Screen.this, "hello "+ getGoogleSignInAccount().getEmail(), Toast.LENGTH_SHORT).show();

                        Bundle bundle = getIntent().getExtras();
                        if(bundle!=null && bundle.containsKey("type"))
                        {
                            intent = NotificationHelper.createNotificationIntent(bundle.getString("type"),context);
                            intent.putExtra(IntentData,bundle);

                        }
                        else {
                            intent = new Intent(getApplicationContext(), HomeActivity.class);
                        }
                    }
                   startIntentWithFlags(intent,Splash_Screen.this);
                    finish();

                } catch (Exception e) {
                    if(e.toString().toLowerCase().contains("connection"))
                    {
                        DialogHelperPojo dialogHelper = new DialogHelperPojo();
                        dialogHelper.setMessage("Change in default time detected , please set the time to default network time");

                        getConfirmationDialogSingleButton(dialogHelper, new ConfirmationDialogListener2() {
                            @Override
                            public void onYesClick() {
                                finish();
                            }

                            @Override
                            public void onNoClick() {

                            }
                        });

                    }
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