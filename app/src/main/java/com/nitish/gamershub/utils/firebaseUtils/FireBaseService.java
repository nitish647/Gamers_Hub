package com.nitish.gamershub.utils.firebaseUtils;

import static com.nitish.gamershub.utils.AppConstants.GamersHub_DATA;
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


    public static DocumentReference getFirebaseGamersHubData() {
        return FirebaseFirestore.getInstance().collection(GamersHub_DATA).document("gamersHubData");
    }
    public static DocumentReference getGamersHubRedeemCoinsList() {
        return FirebaseFirestore.getInstance().collection(GamersHub_DATA).document("redeemCoinsList");
    }
    public static DocumentReference getFirebaseUser(String userMail) {
        return FirebaseFirestore.getInstance().collection(GamersHub_ParentCollection).document(userMail);
    }
    public static DocumentReference checkIfUserExists(String userMail)
    {
        return FirebaseFirestore.getInstance().collection(GamersHub_ParentCollection).document(userMail);
    }
}
