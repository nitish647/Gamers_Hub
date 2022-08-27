package com.nitish.gamershub.Activities;

import static com.nitish.gamershub.Utils.ConstantsHelper.GamersHub_ParentCollection;
import static com.nitish.gamershub.Utils.ConstantsHelper.UserMail;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.nitish.gamershub.Pojo.AllGamesItems;
import com.nitish.gamershub.Pojo.UserProfile;
import com.nitish.gamershub.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.paperdb.Paper;

public class LoginPage extends AppCompatActivity {

    SignInButton googleSingInButton;

    FirebaseFirestore firestoreDb;

    public  static String Users_ParentDocument ="Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        googleSingInButton = findViewById(R.id.singInButton);
        Paper.init(this);
        firestoreDb = FirebaseFirestore.getInstance();

        googleSingInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<AuthUI.IdpConfig> providers = Arrays.asList(


                        new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build();
                signInLauncher.launch(signInIntent);

            }
        });

    }
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {

            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null && user.getEmail()!=null) {

                firestoreDb.collection(GamersHub_ParentCollection).document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(task.isSuccessful()) {

                            if (documentSnapshot.exists()) {

                                // for existing users



                                UserProfile userProfile =  documentSnapshot.toObject(UserProfile.class);

                                UserProfile.ProfileData profileData = userProfile.profileData;

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String currentDateTime = dateFormat.format(new Date()); // Find todays date
                                profileData.setLastLogin(currentDateTime);


                                userProfile.setProfileData(profileData);

                                firestoreDb.collection(GamersHub_ParentCollection).document(user.getEmail()).set(userProfile, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(LoginPage.this, "Welcome Back", Toast.LENGTH_SHORT).show();


                                        Paper.book().write(UserMail,user.getEmail());
                                        Intent intent = new Intent(LoginPage.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            } else {


                                // for new users

                                UserProfile userProfile = new UserProfile();

                                UserProfile.ProfileData profileData = new UserProfile.ProfileData();

                                profileData.setEmail(user.getEmail());
                                profileData.setName(user.getDisplayName());

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String currentDateTime = dateFormat.format(new Date()); // Find todays date
                                profileData.setLastLogin(currentDateTime);

                                userProfile.setProfileData(profileData);


                                firestoreDb.collection(GamersHub_ParentCollection).document(user.getEmail()).set(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(LoginPage.this, "Welcome User", Toast.LENGTH_SHORT).show();
                                        Paper.book().write(UserMail,user.getEmail());
                                        Intent intent = new Intent(LoginPage.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginPage.this, "failed "+e, Toast.LENGTH_SHORT).show();

                    }
                });
            }
            else {
                Toast.makeText(this, "user is null", Toast.LENGTH_SHORT).show();
            }

            // ...
        } else {


            Toast.makeText(this, "Login Failed "+ result.getIdpResponse()+" "+result.getResultCode(), Toast.LENGTH_SHORT).show();

            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }

    }

}