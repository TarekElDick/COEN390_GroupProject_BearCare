package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    // Our class objects
    private EditText editTextLoginEmailAddress;
    private EditText editTextLoginPassword;
    private Button buttonLogin;
    private TextView textViewMessage;
    private TextView textViewCreateNewAccount;
    private ProgressBar progressBarLogin;

    // Firebase Shared Instance of a Authentication object
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // A function setUpUI to connect our class objects to our layout widgets, and onClickListeners
        setUpUI();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();



    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check if user is already logged in or not, if they are a returning user
        if(mAuth.getCurrentUser() != null){

            // send them to their activity
            Toast.makeText(MainActivity.this, "Welcome Back", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), UserMainPageActivity.class));
        }

    }

    private void setUpUI() {

        // Connecting class objects to our layout widgets.
        editTextLoginEmailAddress = findViewById(R.id.editTextEmailAddress_login);
        editTextLoginPassword     = findViewById(R.id.editTextPassword_login);
        buttonLogin               = findViewById(R.id.buttonLogin_login);
        textViewMessage           = findViewById(R.id.textViewMessage_login);
        textViewCreateNewAccount  = findViewById(R.id.textViewCreateNewAccount_login);
        progressBarLogin          = findViewById(R.id.progressBar_login);

        //onClickListeners
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextLoginEmailAddress.getText().toString().trim();
                String password = editTextLoginPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    editTextLoginEmailAddress.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    editTextLoginPassword.setError("Password is Required");
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
                            Toast.makeText(MainActivity.this, "User Created", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), UserMainPageActivity.class));
                        }else{
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

                startActivity(new Intent(getApplicationContext(),SignUpActivity.class ));

            }

        });

        // TODO change this temporary code
        // use the login button to go directly to the child directory activity
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChildDirectoryActivity.class);

                startActivity(intent);
            }
        });

    }



}