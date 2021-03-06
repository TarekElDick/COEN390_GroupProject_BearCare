package com.example.coen390_groupproject_bearcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    //Our class objects
    private EditText editTextSignUpFullName;
    private EditText editTextSignUpEmailAddress;
    private EditText editTextSignUpPhoneNumber;
    private EditText editTextSignUpPassword;
    private EditText editTextSignUpConfirmPassword;
    private Button buttonSignUpCreateAccount;
    private TextView textViewSignUpLogin;

    private RadioGroup radioGroupSignUp;
    private RadioButton radioButtonParent, radioButtonEmployee, radioButtonDirector;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // SetUpUI function
        setUpUI();



    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void setUpUI() {

        editTextSignUpFullName        = findViewById(R.id.editTextFullName_signup);
        editTextSignUpEmailAddress    = findViewById(R.id.editTextEmailAddress_signup);
        editTextSignUpPhoneNumber     = findViewById(R.id.editTextPhone_signup);
        editTextSignUpPassword        = findViewById(R.id.editTextPassword_signup);
        editTextSignUpConfirmPassword = findViewById(R.id.editTextConfirmPassword_signup);
        buttonSignUpCreateAccount     = findViewById(R.id.buttonCreateAccount_signup);
        textViewSignUpLogin           = findViewById(R.id.textViewLogin_signup);
        radioGroupSignUp              = findViewById(R.id.radioGroup_signup);
        radioButtonParent             = findViewById(R.id.radioButtonParent_signup);
        radioButtonEmployee           = findViewById(R.id.radioButtonEmployee_signup);
        radioButtonDirector           = findViewById(R.id.radioButtonDirector_signup);

        buttonSignUpCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the strings
                String fullName     = editTextSignUpFullName.getText().toString().trim();
                String emailAddress = editTextSignUpEmailAddress.getText().toString().trim();
                String phoneNumber  = editTextSignUpPhoneNumber.getText().toString().trim();
                String password     = editTextSignUpPassword.getText().toString().trim();
                String confirmPass  = editTextSignUpConfirmPassword.getText().toString().trim();

                // TO-DO check they are correct.

                // If correct then make a new user, and display progress bar.


            }
        });

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

