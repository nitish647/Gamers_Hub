package com.nitish.gamershub.utils.firebaseUtils;

public interface FirebaseDocumentListener<T> {



    void  onSuccess(T data);

    void onFailure(String message,Exception e);
}
