package com.mr_abdali.childapp;

import android.*;
import android.Manifest;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.*;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG1 = "CallLog";
    private static final int URL_LOADER = 1;
    public static final String EXTRA_MESSAGE = "UserId";
    private static final String TAG = "LoginActivity";
    private Button login;
    private EditText EmailID,password;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    public  String Parentkey;

    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        login=(Button)findViewById(R.id.Login);
        login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                //  attempLogin();
                login();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference().child("childlist");
        EmailID=(EditText)findViewById(R.id.emailID);
        password=(EditText)findViewById(R.id.password);
        mAuth=FirebaseAuth.getInstance();

        // --- Start location ---------------//
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        // --- End location ---------------//
    }

    // --- location ---------------//
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            android.location.Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

            if (location != null){
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                
            }
        }
    }
    // --- location ---------------//


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
        login.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        onLoginSuccess();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        //  onLoginSuccess();
                        progressDialog.dismiss();
                    }
                }, 2000);
    }
    public boolean validate() {
        boolean valid = true;
        String email = EmailID.getText().toString();
        String Password = password.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EmailID.setError("enter a valid email address");
            valid = false;
        } else {
            EmailID.setError(null);
        }
        if (Password.isEmpty() || Password.length() < 6 || Password.length() > 15) {
            password.setError("between 6 and 15 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }
        return valid;
    }
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        login.setEnabled(true);
    }
    public void onLoginSuccess() {
        login.setEnabled(true);
        //TODO Login logic here
        String emailAddress = EmailID.getText().toString().trim();
        String Password = password.getText().toString().trim();
        if (!TextUtils.isEmpty(emailAddress) && !TextUtils.isEmpty(Password)){
            mAuth.signInWithEmailAndPassword(emailAddress,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        final String user_id = mAuth.getCurrentUser().getUid();
                        Intent intent = new Intent(LoginActivity.this, Main_chat.class);
                        intent.putExtra(EXTRA_MESSAGE, user_id);
                        startActivity(intent);
                        finish();
                    }
                    if (!task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Not internet Connection  ",Toast.LENGTH_LONG).show();
                    }}});}}
    @Override
    protected void onResume() {
        super.onResume();

        //  progressBar.setVisibility(View.GONE);
    }
    @Override
    public void onBackPressed() {
        // Disable going back to the LoginActivity
        moveTaskToBack(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}

