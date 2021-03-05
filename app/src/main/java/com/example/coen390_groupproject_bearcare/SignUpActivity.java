package com.example.coen390_groupproject_bearcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    //Uur class objects
    private EditText editTextSignUpFullName;
    private EditText editTextSignUpEmailAddress;
    private EditText editTextSignUpPhoneNumber;
    private EditText editTextSignUpPassword;
    private EditText editTextSignUpConfirmPassword;
    private Button buttonSignUpCreateAccount;
    private TextView textViewSignUpLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // SetUpUI function
        setUpUI();


    }

    private void setUpUI() {

        editTextSignUpFullName = findViewById(R.id.editTextFullName_signup);
        editTextSignUpEmailAddress = findViewById(R.id.editTextEmailAddress_signup);
        editTextSignUpPhoneNumber = findViewById(R.id.editTextPhone_signup);
        editTextSignUpPassword = findViewById(R.id.editTextPassword_signup);
        editTextSignUpConfirmPassword = findViewById(R.id.editTextConfirmPassword_signup);
        buttonSignUpCreateAccount = findViewById(R.id.buttonCreateAccount_signup);
        textViewSignUpLogin = findViewById(R.id.textViewLogin_signup);

        textViewSignUpLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLoginActivity();
            }

            public void gotoLoginActivity() {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}