package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    //Our class objects
    private EditText editTextSignUpFullName, editTextSignUpEmailAddress,  editTextSignUpPhoneNumber,  editTextSignUpPassword,  editTextSignUpConfirmPassword;
    private Button buttonSignUpCreateAccount;
    private TextView textViewClickableLogin, textViewMessage;

    private RadioGroup radioGroupSignUp;
    private RadioButton radioButtonParent, radioButtonEmployee, radioButtonDirector;

    private ProgressBar progressBarSignUp;

    // Firebase Shared Instance of a Authentication object
    private FirebaseAuth mAuth;

    // Firebase Shared Instance of a Cloud FireStore object.
    private FirebaseFirestore fStore;

    // user ID
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // SetUpUI function
        setUpUI();

        //// Initializing Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        // Initializing Cloud FireStore
        fStore = FirebaseFirestore.getInstance();

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
        textViewMessage               = findViewById(R.id.textViewaMessage_signup);
        textViewClickableLogin        = findViewById(R.id.textViewClickableLogin_signup);
        radioGroupSignUp              = findViewById(R.id.radioGroup_signup);
        radioButtonParent             = findViewById(R.id.radioButtonParent_signup);
        radioButtonEmployee           = findViewById(R.id.radioButtonEmployee_signup);
        radioButtonDirector           = findViewById(R.id.radioButtonDirector_signup);
        progressBarSignUp             = findViewById(R.id.progressBar_signup);

        buttonSignUpCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the strings.
                String fullName     = editTextSignUpFullName.getText().toString().trim();
                String emailAddress = editTextSignUpEmailAddress.getText().toString().trim();
                String phoneNumber  = editTextSignUpPhoneNumber.getText().toString().trim();
                String password     = editTextSignUpPassword.getText().toString().trim();
                String confirmPassword  = editTextSignUpConfirmPassword.getText().toString().trim();
                boolean isEmployee      = radioButtonEmployee.isChecked();

                // Check inputs are correct.
                if (fullName.isEmpty()){
                    editTextSignUpFullName.setError("Full name is required");
                    editTextSignUpFullName.requestFocus();
                } else if (emailAddress.isEmpty()){
                    editTextSignUpEmailAddress.setError("Email is required");
                    editTextSignUpEmailAddress.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
                    editTextSignUpEmailAddress.setError("Please provide valid email");
                    editTextSignUpEmailAddress.requestFocus();

                } else if (phoneNumber.isEmpty()){
                    editTextSignUpPhoneNumber.setError("Phone number is required");
                    editTextSignUpPhoneNumber.requestFocus();

                }else if (!Patterns.PHONE.matcher(phoneNumber).matches()){
                    editTextSignUpPhoneNumber.setError("Please provide valid phone number");
                    editTextSignUpPhoneNumber.requestFocus();

                }else if (password.isEmpty()){
                    editTextSignUpPassword.setError("Setting Password is required");
                    editTextSignUpPassword.requestFocus();

                } else if(password.length() < 6){
                    editTextSignUpPassword.setError("Password has to be more than 6 characters long");
                    editTextSignUpPassword.requestFocus();

                } else if (confirmPassword.isEmpty()){
                    editTextSignUpConfirmPassword.setError("Confirming Password is required");
                    editTextSignUpConfirmPassword.requestFocus();

                } else if (!(confirmPassword.equals(password))){
                    editTextSignUpConfirmPassword.setError("Password did not match");
                    editTextSignUpConfirmPassword.requestFocus();
                } else {

                    // Display progressbar to let the user know something is happening
                    textViewMessage.setVisibility(View.INVISIBLE);
                    textViewClickableLogin.setVisibility(View.INVISIBLE);
                    buttonSignUpCreateAccount.setVisibility(View.INVISIBLE);
                    progressBarSignUp.setVisibility(View.VISIBLE);

                    // Create a User with email and password, and connect it to a onCompleteListener to know if it was successful.
                    mAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_LONG).show();

                                // Adding a user to the database with their information.
                                // 1) Get user ID, using the current instance of firebase authentication
                                userID = mAuth.getCurrentUser().getUid();

                                // 2) Create the collection of Users for their information and documents are named after the userID. Using document reference which is a FireStore function.
                                DocumentReference documentReference = fStore.collection("Users").document(userID);

                                // 3) Map the data, using the hashMap. Creating a new map, since email is already saved within authentication we don't need to save it again. Its optional
                                Map<String,Object> user = new HashMap<>();
                                user.put("fullName", fullName);
                                user.put("emailAddress", emailAddress);
                                user.put("phoneNumber", phoneNumber);
                                user.put("isEmployee", isEmployee);

                                // 4) Insert into the Cloud Database, using the documentReference object and we will use a variable set. Also we will using a listener to check for success.
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SignUpActivity.this, "User Profile added to database", Toast.LENGTH_LONG).show();
                                    }
                                });

                                startActivity(new Intent(getApplicationContext(), UserMainPageActivity.class));
                            }else{

                                // Let user know that the registration failed. Bring all UI-components visible again.
                                Toast.makeText(SignUpActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                                textViewMessage.setVisibility(View.VISIBLE);
                                textViewClickableLogin.setVisibility(View.VISIBLE);
                                buttonSignUpCreateAccount.setVisibility(View.VISIBLE);
                                progressBarSignUp.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });


        textViewClickableLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }

        });

        // end of setUP activity
    }


    // end of class
}



