package com.example.rckbrswatch2app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.Transaction;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int RC_SIGN_IN = 9001;
    private Button singInButton;

    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSingInClient;
    ElementViewModel elementViewModel;

    static String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        elementViewModel = ViewModelProviders.of(this).get(ElementViewModel.class);

        singInButton = (Button) findViewById(R.id.login);
        singInButton.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSingInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            Log.d("GoogleLogin", "firebaseLogin:  " + currentUser.getUid());
            userID = currentUser.getUid();

            intent.putExtra("userID", userID);
           // elementViewModel.checkUser("aasdoajsdkj askw3ieq3e");
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        signIn();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSingInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void createUserProfile(String userID, String displayName, String email){
        com.example.rckbrswatch2app.User user = new User(userID, displayName, email);

        //Zmienić nazwę funkcji
        elementViewModel.registerUserOutside(user);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Exception exception = task.getException();
            if (task.isSuccessful()){
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    assert account != null;
                    Log.d("GoogleLogin", "firebaseLogin:  " + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            } else {
                Log.w("SignInACtivity", exception);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("GoogleLogin", "GoogleLogin Successfully");
                        FirebaseUser user = mAuth.getCurrentUser();
                        //Funkcja spajaąca
                        assert user != null;
                        createUserProfile(user.getUid(), user.getDisplayName(), user.getEmail());

                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d("GoogleLogin", "GoogleLogin Failed because of " + task.getException());
                    }
                });
    }
}