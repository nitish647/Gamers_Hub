package com.nitish.gamershub.view.base;


import static com.nitish.gamershub.utils.AppConstants.GamersHubDataGlobal;
import static com.nitish.gamershub.utils.AppConstants.UserInfo;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.nitish.gamershub.apiUtils.ApiService;
import com.nitish.gamershub.model.firebase.FirebaseDataPassingHelper;
import com.nitish.gamershub.model.firebase.GamersHubData;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.model.gamersHubMaterData.AllGamesResponseItem;
import com.nitish.gamershub.utils.NetworkResponse;
import com.nitish.gamershub.utils.PreferenceHelper;
import com.nitish.gamershub.utils.firebaseUtils.FireBaseService;
import com.nitish.gamershub.view.loginSingup.activity.SplashScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import io.paperdb.Paper;

abstract public class BaseRepository {
    Context context = AppClass.getInstance();
    PreferenceHelper preferenceHelper = new PreferenceHelper(context);


    public <K> void getFireBaseDocumentReference(DocumentReference documentReference, MutableLiveData<NetworkResponse<K>> mutableLiveData, Class<K> responseType) {

        mutableLiveData.postValue(new NetworkResponse.Loading<>());


        callGetFireBaseDocumentReference(documentReference, new FireBaseDocReferenceListener() {


            @Override
            public void documentExists(Boolean documentExists, DocumentSnapshot documentSnapshot) {
                if (documentExists) {

                    JSONObject jsonObject = new JSONObject(documentSnapshot.getData());


                    Object obj = new Gson().fromJson(jsonObject.toString(), responseType);

                    saveData(obj);


                    mutableLiveData.postValue(new NetworkResponse.Success<>((K) obj));


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

    public <K> void setFirebaseDocumentReference(FirebaseDataPassingHelper<K> firebaseDocumentReference) {

        SetOptions setOptions = firebaseDocumentReference.getSetOptions();
        DocumentReference documentReference = firebaseDocumentReference.getDocumentReference();
        Object dataObject = firebaseDocumentReference.getDataObject();
        MutableLiveData<NetworkResponse<K>> mutableLiveData = firebaseDocumentReference.getMutableLiveData();
        String message = firebaseDocumentReference.getMessage();
        message = message == null ? "" : message;


        Task<Void> task = null;
        if (setOptions == null)
            task = documentReference.set(dataObject);
        else {
            task = documentReference.set(dataObject, SetOptions.merge());
        }


        mutableLiveData.postValue(new NetworkResponse.Loading<>());

        String finalMessage = message;
        task.addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {



                    mutableLiveData.postValue(new NetworkResponse.Success<>((K) dataObject, finalMessage));

                    saveData(dataObject);


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


    public <K> void callVolleyRequest(Context context, Integer requestMethod, MutableLiveData<NetworkResponse<K>> mutableLiveData, String url, Class<K> responseType) {


        Log.w(BaseRepository.class.getSimpleName(), "called Api " + url);


        StringRequest request = new StringRequest(requestMethod, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.w(BaseRepository.class.getSimpleName(), url + "\n Response:");
                Log.w(BaseRepository.class.getSimpleName(), response);


                if (url.equals(ApiService.materJsonUrl)) {

                    Type type = new TypeToken<List<AllGamesResponseItem>>() {
                    }.getType();

                    // Parsing the JSON into a list of GamersHubData objects
                    List<AllGamesResponseItem> gamersHubDataList = new Gson().fromJson(response, type);

//                    Object obj = new Gson().fromJson(gamersHubDataList.get(0).toString(), ResponseItem.class);

                    AllGamesResponseItem allGamesResponseItem =   gamersHubDataList.get(0);

                    mutableLiveData.postValue(new NetworkResponse.Success<>((K) gamersHubDataList.get(0)));

                    preferenceHelper.saveAllGamesResponseMaterData(allGamesResponseItem);

                    try {

                        Paper.book().write(SplashScreen.MaterData, new Gson().toJson(new JSONArray(response).toString()) + "");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    return;

                }
                Object obj = new Gson().fromJson(response, responseType);

                mutableLiveData.postValue(new NetworkResponse.Success<>((K) obj));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mutableLiveData.postValue(new NetworkResponse.Error<>(error.toString()));
                error.printStackTrace();
            }
        });

        //        Request<Object> request = new Request<Object>(requestMethod, url, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                mutableLiveData.postValue(new NetworkResponse.Error<>(error.toString()));
//                error.printStackTrace();
//            }
//        }) {
//            @Override
//            protected Response<Object> parseNetworkResponse(com.android.volley.NetworkResponse response) {
//
//
//                return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
//
//
//            }
//
//            @Override
//            protected void deliverResponse(Object response) {
//
//
//            }
//        };
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestMethod, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                try {
//
//
////                    if (response.has("dateTime")) {
//
//                    Object obj = new Gson().fromJson(response.toString(), responseType);
//
//                    mutableLiveData.postValue(new NetworkResponse.Success<>((K) obj));
//
//
//                } catch (Exception e) {
//                    mutableLiveData.postValue(new NetworkResponse.Error<>(e.toString()));
//                    e.printStackTrace();
//                }
//
//
//            }
//        }
//                , new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                mutableLiveData.postValue(new NetworkResponse.Error<>(error.toString()));
//            }
//        });
        Volley.newRequestQueue(context).add(request);


    }


    public void saveData(Object object) {

        String objectClassSimpleName = object.getClass().getSimpleName();


        if (objectClassSimpleName.equals(UserProfile.class.getSimpleName())) {

            saveUserProfile((UserProfile) object);

        } else if (objectClassSimpleName.equals(GamersHubData.class.getSimpleName())) {

            saveGamersHubDataProfile((GamersHubData) object);
        }
    }

    public void saveUserProfile(UserProfile userProfile) {
//        Context context = AppClass.getInstance();
//        PreferenceHelper preferenceHelper = new PreferenceHelper(context);

        preferenceHelper.saveUserProfile(userProfile);
        Paper.book().write(UserInfo, userProfile);
    }

    public void saveGamersHubDataProfile(GamersHubData gamersHubData) {

        preferenceHelper.saveGamersHubData(gamersHubData);
        Paper.book().write(GamersHubDataGlobal, gamersHubData);
    }


    interface FireBaseDocReferenceListener {
        void documentExists(Boolean documentExists, DocumentSnapshot documentSnapshot);

        void OnFailure(Exception e);

        void onTaskNotSuccessFull();
    }


}
