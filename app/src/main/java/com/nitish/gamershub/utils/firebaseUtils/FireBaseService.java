package com.nitish.gamershub.utils.firebaseUtils;

import static com.nitish.gamershub.utils.AppConstants.GamersHub_ParentCollection;
import static com.nitish.gamershub.utils.AppConstants.UserMail;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import io.paperdb.Paper;

public interface FireBaseService {


    public static DocumentReference getFirebaseUser() {
        return FirebaseFirestore.getInstance().collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail) + "");
    }
}
