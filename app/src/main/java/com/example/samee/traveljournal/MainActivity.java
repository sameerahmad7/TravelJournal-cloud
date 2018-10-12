package com.example.samee.traveljournal;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Boolean loggedin;
    Button loginBtn;
    EditText emailText,passText;
    ProgressBar progressBar;
    String email,password;
    private FirebaseAuth mAuth;
    private String TAG="MainActivity";
private CallbackManager mCallbackManager;
    TextView signUpText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        mAuth=FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.buttonFacebookLogin);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
// ...

        if(null!=mAuth.getCurrentUser())
        {
            updateUI();

        }
        emailText=(EditText)findViewById(R.id.emailText);
        passText=(EditText)findViewById(R.id.passwordText);
        signUpText=(TextView)findViewById(R.id.Signup);
        loginBtn=(Button)findViewById(R.id.login);
        signUpText.setOnClickListener(this);
        loginBtn.setOnClickListener(this);



    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
       //                     updateUI(null);
                        }

                        // ...
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            updateUI();
        }
    }
    public void updateUI()
    {

        Toast.makeText(getApplicationContext(),"You are logged in!",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.Signup) {
            if (v.getTag().equals("login")) {
                loggedin = true;
                loginBtn.setText("Login");
                signUpText.setText("Don't have an account? Sign up");
                signUpText.setTag("Signup");
            } else if (v.getTag().equals("Signup")) {
                loggedin = false;
                loginBtn.setText("Signup");
                signUpText.setText("Already have an account? Login");
                signUpText.setTag("login");
            }
        }
        else if(v.getId()==R.id.login)
        {
            Register();
        }

    }
    public void Register()
    {
         email=emailText.getText().toString().trim();
         password=passText.getText().toString().trim();
        if(email.isEmpty())
        {
            emailText.setError("Email is required");
            emailText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Enter a valid email");
            emailText.requestFocus();
            return;

        }
        if(password.isEmpty())
        {
            passText.setError("Password is required");
            passText.requestFocus();
            return;
        }
        if(password.length()<6)
        {
            passText.setError("Min length of password should be 6");
            passText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        signUpText.setVisibility(View.GONE);

        if(!loggedin)
        {
            registerUser();
        }
        else if(loggedin)
        {
            loginUser();
        }
    }
    public void loginUser()
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Logged in Successfully!",Toast.LENGTH_SHORT).show();
                    updateUI();

                }
                else {
                    Toast.makeText(getApplicationContext(),"Login Failed!",Toast.LENGTH_SHORT).show();
                    signUpText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void registerUser()
    {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Registered Successfully!",Toast.LENGTH_SHORT).show();
                }
                else if(task.getException() instanceof FirebaseAuthUserCollisionException)
                {
                    Toast.makeText(getApplicationContext(),"You are already registered!",Toast.LENGTH_SHORT).show();
                }
                else {

                    Toast.makeText(getApplicationContext(),"Error occured while registering!",Toast.LENGTH_SHORT).show();
                }
                signUpText.setVisibility(View.VISIBLE);
            }
        });
    }
}
