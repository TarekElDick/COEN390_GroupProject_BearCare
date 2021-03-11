package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.Bluetooth.BluetoothScanner;
import com.example.coen390_groupproject_bearcare.Bluetooth.MyBluetoothService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Our class objects
    private EditText editTextLoginEmailAddress;
    private EditText editTextLoginPassword;
    private Button buttonLogin;
    private Button bttn;
    private TextView textViewMessage;
    private TextView textViewCreateNewAccount;
    private ProgressBar progressBarLogin;

    // Firebase Shared Instance of a Authentication object
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    String TAG = "debug_login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // A function setUpUI to connect our class objects to our layout widgets, and onClickListeners
        setUpUI();

        // Initializing Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();


    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check if user is already logged in or not, if they are a returning user
        if(mAuth.getCurrentUser() != null ){

            Log.d(TAG, " User is not null");
            // send them to their activity
            Toast.makeText(MainActivity.this, "Welcome Back", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), UserMainPageActivity.class));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyBluetoothService mine = new MyBluetoothService();
        MyBluetoothService.close();
    }

    private void setUpUI() {

        // Connecting class objects to our layout widgets.
        editTextLoginEmailAddress = findViewById(R.id.editTextEmailAddress_login);
        editTextLoginPassword     = findViewById(R.id.editTextPassword_login);
        buttonLogin               = findViewById(R.id.buttonLogin_login);
        textViewMessage           = findViewById(R.id.textViewMessage_login);
        textViewCreateNewAccount  = findViewById(R.id.textViewCreateNewAccount_login);
        progressBarLogin          = findViewById(R.id.progressBar_login);
        bttn =                    findViewById(R.id.button);


        bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TemperatureActivity.class);
                startActivity(i);
            }
        });
        //onClickListeners
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextLoginEmailAddress.getText().toString().trim();
                String password = editTextLoginPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    editTextLoginEmailAddress.setError("Email is Required");
                    editTextLoginEmailAddress.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    editTextLoginPassword.setError("Password is Required");
                    editTextLoginPassword.requestFocus();
                    return;
                }

                // Display progressbar to let the user know something is happening
                textViewMessage.setVisibility(View.INVISIBLE);
                textViewCreateNewAccount.setVisibility(View.INVISIBLE);
                buttonLogin.setVisibility(View.INVISIBLE);
                progressBarLogin.setVisibility(View.VISIBLE);

                // onCompleteListener to check if the user account was already created before.
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Log.d(TAG, " User signed in successfully");
                            Toast.makeText(MainActivity.this, "Signed in successfully ", Toast.LENGTH_LONG).show();
                            //redirect to user profile
                            startActivity(new Intent(getApplicationContext(), UserMainPageActivity.class));

                        }else{
                            Log.d(TAG, " User didn't signed in successfully");
                            Toast.makeText(MainActivity.this, "No account associated with this email/password", Toast.LENGTH_LONG).show();
                            textViewMessage.setVisibility(View.VISIBLE);
                            textViewCreateNewAccount.setVisibility(View.VISIBLE);
                            buttonLogin.setVisibility(View.VISIBLE);
                            progressBarLogin.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });

        textViewCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, " User is going to create an account");
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class ));

            }

        });

        // end of setUpUI function
    }



}