package com.example.coen390_groupproject_bearcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Our class objects
    private EditText editTextLoginEmailAddress;
    private EditText editTextLoginPassword;
    private Button buttonLogin;
    private TextView textViewCreateNewAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // A function setUpUI to connect our class objects to our layout widgets, and onClickListeners
        setUpUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void setUpUI() {

        // Connecting class objects to our layout widgets.
        editTextLoginEmailAddress = findViewById(R.id.editTextEmailAddress_login);
        editTextLoginPassword = findViewById(R.id.editTextPassword_login);
        buttonLogin = findViewById(R.id.button_login);
        textViewCreateNewAccount = findViewById(R.id.textViewCreateNewAccount_login);

        //onClickListeners
        textViewCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // A function goToSignUpActivity that has an intent.
                goToSignUpActivity();

            }

            public void goToSignUpActivity() {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);

                startActivity(intent);
            }
        });

    }



}