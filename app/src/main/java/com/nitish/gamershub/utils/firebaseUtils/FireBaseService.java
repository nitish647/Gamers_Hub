package com.nitish.gamershub.utils.firebaseUtils;

import static com.nitish.gamershub.utils.AppConstants.GamersHub_ParentCollection;
import static com.nitish.gamershub.utils.AppConstants.UserMail;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import io.paperdb.Paper;

public interface FireBaseService {

    public static enum FirebaseMessage
    {
        DOCUMENT_DOES_NOT_EXISTS,
       TASK_COMPLETED_SUCCESSFULLY()
    }


    public static DocumentReference getFirebaseUser() {
        return FirebaseFirestore.getInstance().collection(GamersHub_ParentCollection).document(Paper.book().read(UserMail) + "");
    }
    public static DocumentReference checkIfUserExists(String userMail)
    {
        return FirebaseFirestore.getInstance().collection(GamersHub_ParentCollection).document(userMail);
    }
}
