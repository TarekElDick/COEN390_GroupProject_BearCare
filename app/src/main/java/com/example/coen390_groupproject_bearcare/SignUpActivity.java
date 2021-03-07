package com.example.coen390_groupproject_bearcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

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

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // SetUpUI function
        setUpUI();

        //
        mAuth = FirebaseAuth.getInstance();


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
                String confirmPassword  = editTextSignUpConfirmPassword.getText().toString().trim();

                // TO-DO check they are correct.
                // If correct then make a new user, and display progress bar.

                String TAG = "debug1";
                if (fullName.isEmpty()){
                    editTextSignUpFullName.setError("Full name is required");
                    editTextSignUpFullName.requestFocus();
                    //motoreturn;
                }

                //case empty
                else if (emailAddress.isEmpty()){
                    editTextSignUpEmailAddress.setError("Email is required");
                    editTextSignUpEmailAddress.requestFocus();
                    //return;
                }

                //case email is not valid
                else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
                    editTextSignUpEmailAddress.setError("Please provide valid email");
                    editTextSignUpEmailAddress.requestFocus();
                    //return;
                }

                //case empty
                else if (phoneNumber.isEmpty()){
                    editTextSignUpPhoneNumber.setError("Phone number is required");
                    editTextSignUpPhoneNumber.requestFocus();
                    //return;
                }
                //case phone number is not valid
                else if (!Patterns.PHONE.matcher(phoneNumber).matches()){
                    editTextSignUpPhoneNumber.setError("Please provide valid phone number");
                    editTextSignUpPhoneNumber.requestFocus();
                    //return;
                }

                //case empty
                else if (password.isEmpty()){
                    editTextSignUpPassword.setError("Setting Password is required");
                    editTextSignUpPassword.requestFocus();
                    //return;
                }

                //case password less than 6 characters
                else if(password.length() < 6){
                    editTextSignUpPassword.setError("Password has to be more than 6 characters long");
                    editTextSignUpPassword.requestFocus();
                    //return;
                }

                //case empty
                else if (confirmPassword.isEmpty()){
                    editTextSignUpConfirmPassword.setError("Confirming Password is required");
                    editTextSignUpConfirmPassword.requestFocus();
                    //return;
                }

                //case passwords do not conform
                else if (!(confirmPassword.equals(password))){
                    Log.d(TAG, "confirm pass:" + confirmPassword);
                    Log.d(TAG, "pass:" + password);
                    editTextSignUpConfirmPassword.setError("Password did not match");
                    //editTextSignUpPassword.requestFocus();
                }
                else {
                    Log.d(TAG, "confirm pass out:" + confirmPassword);
                    Log.d(TAG, "pass out:" + password);

                    if(true) {
                        Log.d("debug2", "force trial");
                        mAuth.createUserWithEmailAndPassword(emailAddress, password)

                                .addOnCompleteListener(task -> {

                                    Log.d("debug2", "before first condition");

                                    if (task.isSuccessful()) {
                                        User user = new User(fullName, emailAddress, phoneNumber, radioButtonEmployee.isChecked());

                                        Log.d("debug3", "after first condition");


                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(user).addOnCompleteListener(task1 -> {

                                            if (task1.isSuccessful()) {
                                                Log.d("debug3", "user registered");
                                                Toast.makeText(SignUpActivity.this, "User registered successfully!", Toast.LENGTH_LONG).show();

                                                //route to login
                                            } else {
                                                Log.d("debug3", "try again");
                                                Toast.makeText(SignUpActivity.this, "Failed to register, try again!", Toast.LENGTH_LONG).show();
                                            }
                                        });


                                    } else {
                                        Log.d("debug2", "failed");
                                        Toast.makeText(SignUpActivity.this, "Failed to register!", Toast.LENGTH_LONG).show();

                                    }
                                });
                        Log.d("debug2", "leaving force trial");
                    }
                    Log.d("debug", "WTF");
                    return;
                }
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



