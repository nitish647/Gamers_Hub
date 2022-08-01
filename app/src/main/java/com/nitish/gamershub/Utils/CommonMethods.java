package com.nitish.gamershub.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nitish.gamershub.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

// this class contains all the common methods that are used in various files ,
// so that we can easily copy paste those codes from here
public class CommonMethods {


    // this class is just contains the common methods  so that are users can copy paste the methods from here
    Context context;

    // common yes no dialog bix
    public void deleteOrderBillDialog(String orderId , int position , ViewGroup rootLayout)
    {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        android.app.AlertDialog deleteDialog = builder.create();


        builder.setMessage("Do you want to delete this bill?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();

    }
    // common dialog box
    public void addAccountDialog() {
        LayoutInflater factory = LayoutInflater.from(context);

        final View addBankAccLayout = factory.inflate(R.layout.activity_category, null);

        final AlertDialog addAccountDialog = new AlertDialog.Builder(context).create();


        addAccountDialog.setView(addBankAccLayout);

        addAccountDialog.show();

        EditText accNumberEditText = addBankAccLayout.findViewById(R.id.searchView);
        Spinner accountTypeSpinner = addBankAccLayout.findViewById(R.id.webView);

        TextView dismissDialogButton = addBankAccLayout.findViewById(R.id.playButton);
    }



    // simple dialog
    public void deleteAddressDialog(Context context) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage("Delete the address?");

        alertBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });


        alertBuilder.show();


    }



    // post method
    public void offlineBillGenerateApi()
    {

        // don't declare the requestQueue here , it should be in onCreate method
        RequestQueue requestQueue = Volley.newRequestQueue(context);



//        generateBillDialog.dismiss();// dismiss the dialog
//        progressBarDialog.show(); // show the progressbar
        String url = context.getResources().getString(R.string.admob_inter)+"v1/api/offlinebillgenerate";


        HashMap<String , Object> hashMap = new HashMap<>();

        JSONObject jsonObject = new JSONObject(hashMap);

        Log.d("pRequestBody","offlinebillgenerate request body : "+ jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    //    progressBarDialog.dismiss(); // close the progressbar
                        Log.d("pResponse","offlinebillgenerate response : "+response.toString());
                        try {

                            if(response.getBoolean("status"))


                                Log.d("Response",response.toString());
                            // if the response is success do something here






                        } catch (JSONException e) {

                            Toast.makeText(context, " Error333 , could generate the bill ", Toast.LENGTH_LONG).show();

                            Log.e("pError3223",e.toString());
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getContext(), "error "+error.toString(), Toast.LENGTH_LONG).show();
//                        progressBarDialog.dismiss(); // close the progressbar


                       Log.e("pError",error.toString());


                    }
                })



        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                //instead o f pass current user authtoken
//             params.put("authtoken", Paper.book().read("authToken"));
//
                params.put("authtoken", Paper.book().read("authToken"));




                return params;

            }
        };


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));




        requestQueue.add(jsonObjectRequest);
    }

    // get method
    public void showBankAccounts(Context context)
    {
        // don't declare the requestQueue here , it should be in onCreate method
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = context.getResources().getString(R.string.admob_inter)+"v1/api/viewAcoounts";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("pResponse","viewAcoounts api response "+ response);

                        try {




                            if(response.getBoolean("status"))
                            {
                                Toast.makeText(context, "Success ", Toast.LENGTH_SHORT).show();

//                                JSONArray accountsListArray = response.getJSONArray("accounts");
//
//                                if(accountsListArray.length()==0)
//                                {
//                                    noBankAccountLinear.setVisibility(View.VISIBLE);
//                                    addAccountDialog();
//                                }
//                                for(int i = 0; i< accountsListArray.length(); i++)
//                                {
//                                    JSONObject jsonObject = accountsListArray.getJSONObject(i);
//                                    BankAccounts BankAccounts = gson.fromJson(jsonObject.toString(), BankAccounts.class);
//                                    bankAccountsList.add(BankAccounts);
//
//                                }
//                                bankAccountsAdapter.notifyDataSetChanged();
//

                            }










                        } catch (JSONException e) {
                            Toast.makeText(context, "Sorry could fetch the data", Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Server error ", Toast.LENGTH_SHORT).show();

                        Log.d("pError:",error.toString());


                    }
                })



        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                //instead o f pass current user authtoken
//             params.put("authtoken", Paper.book().read("authToken"));
//
                params.put("authtoken", Paper.book().read("authToken"));




                return params;

            }
        };


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                6000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));




        requestQueue.add(jsonObjectRequest);
    }




}
