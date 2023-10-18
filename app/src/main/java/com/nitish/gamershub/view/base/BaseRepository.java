package com.nitish.gamershub.view.base;


import static com.nitish.gamershub.utils.AppConstants.UserInfo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.model.local.NetWorkTimerResult;
import com.nitish.gamershub.utils.AppConstants;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.PreferenceHelper;
import com.nitish.gamershub.utils.firebaseUtils.FireBaseService;

import org.json.JSONObject;

import io.paperdb.Paper;

abstract public class BaseRepository {

    Context context = new AppClass().getInstance().getApplicationContext();
    PreferenceHelper preferenceHelper = new PreferenceHelper(context);


    public <K> void getFireBaseDocumentReference(DocumentReference documentReference, MutableLiveData<NetworkResponse<K>> mutableLiveData, Class<K> responseType) {

        mutableLiveData.postValue(new NetworkResponse.Loading<>());


        callGetFireBaseDocumentReference(documentReference, new FireBaseDocReferenceListener() {


            @Override
            public void documentExists(Boolean documentExists, DocumentSnapshot documentSnapshot) {
                if (documentExists) {
                    try {
                        JSONObject jsonObject = new JSONObject(documentSnapshot.getData());


                        Object obj = new Gson().fromJson(jsonObject.toString(), responseType);

                        if (obj.getClass().getSimpleName().equals(UserProfile.class.getSimpleName())) {

                            saveUserProfile((UserProfile) obj);
                        }

                        mutableLiveData.postValue(new NetworkResponse.Success<>((K) obj));

                    } catch (Exception e) {

                        Log.d("pError", "Error in converting data into objects  : " + e);

                        mutableLiveData.postValue(new NetworkResponse.Error<>(e.toString()));

                    }


                } else {


                    mutableLiveData.postValue(new NetworkResponse.Error<>(FireBaseService.FirebaseMessage.DOCUMENT_DOES_NOT_EXISTS.toString()));


                }
            }

            @Override
            public void OnFailure(Exception e) {
                mutableLiveData.postValue(new NetworkResponse.Error<>(e.toString()));

            }

            @Override
            public void onTaskNotSuccessFull() {

            }
        });


//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                if (task.isSuccessful()) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//
//
//                    if (documentSnapshot.exists()) {
//
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(documentSnapshot.getData());
//
//
//                            Object obj = new Gson().fromJson(jsonObject.toString(), responseType);
//
//
//                            mutableLiveData.postValue(new NetworkResponse.Success<>((K) obj));
//
//                        } catch (Exception e) {
//
//                            Log.d("pError", "Error in converting data into objects  : " + e);
//
//                            mutableLiveData.postValue(new NetworkResponse.Error<>(e.toString()));
//
//                        }
//
//
//                    } else {
//
//
//                        mutableLiveData.postValue(new NetworkResponse.Error<>("document does not exist"));
//
//
//                    }
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//                mutableLiveData.postValue(new NetworkResponse.Error<>(e.toString()));
//
//
//            }
//        });

    }

    public <K> void setFirebaseDocumentReference(DocumentReference documentReference, MutableLiveData<NetworkResponse<K>> mutableLiveData, Object dataObject, SetOptions setOptions) {

        Task<Void> task = null;
        if (setOptions == null)
            task = documentReference.set(dataObject);
        else {
            task = documentReference.set(dataObject, SetOptions.merge());
        }


        mutableLiveData.postValue(new NetworkResponse.Loading<>());

        task.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {


                    mutableLiveData.postValue(new NetworkResponse.Success<>((K) dataObject));

                    if (dataObject.getClass().getSimpleName().equals(UserProfile.class.getSimpleName())) {

                        saveUserProfile((UserProfile) dataObject);
                    }

                } else {
                    mutableLiveData.postValue(new NetworkResponse.Error<>("Task Filed"));


                }

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mutableLiveData.postValue(new NetworkResponse.Error<>(e.toString()));

                e.printStackTrace();


            }
        });

    }

    private void callGetFireBaseDocumentReference(DocumentReference documentReference, FireBaseDocReferenceListener fireBaseDocReferenceListener) {

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();


                    if (documentSnapshot.exists()) {

                        fireBaseDocReferenceListener.documentExists(true, documentSnapshot);


                    } else {

                        fireBaseDocReferenceListener.documentExists(false, null);


                    }
                } else {
                    fireBaseDocReferenceListener.onTaskNotSuccessFull();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                e.printStackTrace();
                fireBaseDocReferenceListener.OnFailure(e);

            }
        });

    }


    public <K> void callVolleyRequest(Integer requestMethod, MutableLiveData<NetworkResponse<K>> mutableLiveData, String url, Class<K> responseType) {


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestMethod, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {


//                    if (response.has("dateTime")) {

                    Object obj = new Gson().fromJson(response.toString(), responseType);

                    mutableLiveData.postValue(new NetworkResponse.Success<>((K) obj));


                } catch (Exception e) {
                    mutableLiveData.postValue(new NetworkResponse.Error<>(e.toString()));
                    e.printStackTrace();
                }


            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mutableLiveData.postValue(new NetworkResponse.Error<>(error.toString()));
            }
        });

        Volley.newRequestQueue(new AppClass().getInstance()).add(jsonObjectRequest);


    }

    public void saveUserProfile(UserProfile userProfile) {

        preferenceHelper.saveUserProfile(userProfile);
        Paper.book().write(UserInfo, userProfile);
    }


    interface FireBaseDocReferenceListener {
        void documentExists(Boolean documentExists, DocumentSnapshot documentSnapshot);

        void OnFailure(Exception e);

        void onTaskNotSuccessFull();
    }


}
