package com.nitish.gamershub.model.firebase;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;
import com.nitish.gamershub.utils.NetworkResponse;

public class FirebaseDataPassingHelper<K> {

        private DocumentReference documentReference;
        private MutableLiveData<NetworkResponse<K>> mutableLiveData;
        private Object dataObject;
        private SetOptions setOptions;
        private String message;


        public FirebaseDataPassingHelper()
        {

        }
        public DocumentReference getDocumentReference() {
            return documentReference;
        }

        public void setDocumentReference(DocumentReference documentReference) {
            this.documentReference = documentReference;
        }

        public MutableLiveData<NetworkResponse<K>> getMutableLiveData() {
            return mutableLiveData;
        }

        public void setMutableLiveData(MutableLiveData<NetworkResponse<K>> mutableLiveData) {
            this.mutableLiveData = mutableLiveData;
        }

        public Object getDataObject() {
            return dataObject;
        }

        public void setDataObject(Object dataObject) {
            this.dataObject = dataObject;
        }

        public SetOptions getSetOptions() {
            return setOptions;
        }

        public void setSetOptions(SetOptions setOptions) {
            this.setOptions = setOptions;
        }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
