package com.nitish.gamershub.view.loginSingup.activity;

import static com.nitish.gamershub.utils.AppConstants.IntentData;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import com.nitish.gamershub.model.local.DialogItems;
import com.nitish.gamershub.view.dialogs.DialogListener;
import com.nitish.gamershub.view.homePage.activity.HomeActivity;
import com.nitish.gamershub.utils.interfaces.ConfirmationDialogListener2;
import com.nitish.gamershub.databinding.ActivitySplashScreenBinding;
import com.nitish.gamershub.model.local.DialogHelperPojo;
import com.nitish.gamershub.R;
import com.nitish.gamershub.utils.NotificationHelper;
import com.nitish.gamershub.view.base.BaseActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import io.paperdb.Paper;

public class SplashScreen extends BaseActivity {
    ActivitySplashScreenBinding activitySplashScreenBinding;

    SharedPreferences sh;
    SharedPreferences.Editor editor;
    String data;
    String gameData;
    RequestQueue requestQueue;

    private Context context;
    public static String MaterData = "MasterData";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activitySplashScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash__screen);
        requestQueue = Volley.newRequestQueue(this);
        mAuth = FirebaseAuth.getInstance();
        context = SplashScreen.this;
        Paper.init(this);


        handlerFunctions();


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_splash__screen;
    }


    private void handlerFunctions() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
//                get_data();
                SplashScreen.this.getMaterData();

            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activitySplashScreenBinding.warning.setText(R.string.internet_is_slow);
            }
        }, 7000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activitySplashScreenBinding.warning.setText(R.string.please_check_your_internet_connection_and_try_again);
            }
        }, 11000);
    }

    public void getMaterData() {

        String url = getString(R.string.dbGitUrl) + "gamers_hub_data/masterData.json";

        Log.d("url", url);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray response) {

                Log.d("gResponse", "response json array of mater data " + response);

                //    Toast.makeText(Splash_Screen.this, response+"", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    Log.d("gResponse", "response jsonObject of mater data " + jsonObject);
                    Paper.book().write(SplashScreen.MaterData, jsonObject + "");


                    //      FirebaseUser currentUser = mAuth.getCurrentUser();


                    // when not singed in
                    Intent intent;
                    if (getGoogleSignInAccount() == null) {
                        intent = new Intent(SplashScreen.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        ;
                    } else
                    // when already  singed in
                    {
                        Toast.makeText(SplashScreen.this, "hello " + getGoogleSignInAccount().getEmail(), Toast.LENGTH_SHORT).show();

                        Bundle bundle = getIntent().getExtras();
                        if (bundle != null && bundle.containsKey("type")) {
                            intent = NotificationHelper.createNotificationIntent(bundle.getString("type"), context);
                            intent.putExtra(IntentData, bundle);

                        } else {
                            intent = new Intent(getApplicationContext(), HomeActivity.class);
                        }
                    }
                    startIntentWithFlags(intent, SplashScreen.this);
                    finish();

                } catch (Exception e) {
                    if (e.toString().toLowerCase().contains("connection")) {

                        DialogItems dialogItems = new DialogItems();
                        dialogItems.setMessage(getString(R.string.change_in_default_time_detected_please_set_the_time_to_default_network_time));


                        showConfirmationDialog2(dialogItems, new DialogListener() {
                            @Override
                            public void onYesClick() {
                                finish();
                            }

                            @Override
                            public void onNoClick() {

                            }
                        });

                    }
                    Log.d("gError", "error in  mater data " + e.toString());
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("gError", "error in mater data " + error);

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }


}