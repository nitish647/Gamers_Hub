package com.nitish.gamershub.view.base;

import static com.nitish.gamershub.utils.AppHelper.saveUserProfileGlobal;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.nitish.gamershub.model.firebase.UserProfile;
import com.nitish.gamershub.utils.NetworkResponse;

import org.json.JSONObject;

abstract public class BaseRepository {


    public <K> void getFireBaseDocumentReference(DocumentReference documentReference, MutableLiveData<NetworkResponse<K>> mutableLiveData, Class<K> responseType) {

        mutableLiveData.postValue(new NetworkResponse.Loading<>());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();


                    if (documentSnapshot.exists()) {


                        try {
                            JSONObject jsonObject = new JSONObject(documentSnapshot.getData());


                            Object obj = new Gson().fromJson(jsonObject.toString(), responseType);


                            mutableLiveData.postValue(new NetworkResponse.Success<>((K) obj));

                        } catch (Exception e) {

                            Log.d("pError", "Error in converting data into objects  : " + e);

                            mutableLiveData.postValue(new NetworkResponse.Error<>(e.toString()));

                        }


                    } else {


                        mutableLiveData.postValue(new NetworkResponse.Error<>("document does not exist"));


                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                mutableLiveData.postValue(new NetworkResponse.Error<>(e.toString()));


            }
        });

    }


}
