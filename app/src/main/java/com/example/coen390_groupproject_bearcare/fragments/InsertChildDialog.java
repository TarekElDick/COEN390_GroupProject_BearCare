package com.example.coen390_groupproject_bearcare.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coen390_groupproject_bearcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class InsertChildDialog extends DialogFragment {


    private EditText childFirstNameEditText, childLastNameEditText , parentFirstNameEditText , parentLastNameEditText, childBirthdayDayEditText, childBirthdayMonthEditText, childBirthdayYearEditText;

    private Button saveButtonChild, cancelButtonChild;


    // Firebase Shared Instance of a Authentication object
    private FirebaseAuth mAuth;

    // Firebase Shared Instance of a Cloud FireStore object.
    private FirebaseFirestore fStore;


    String TAG = "debug_insertChildDialog";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Initializing Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        // Initializing Cloud FireStore
        fStore = FirebaseFirestore.getInstance();

        // Inflater , Inflates the xml kinda like the engine, we pass the fragment and the container
        // Because by itself the fragment doesn't run , we need an inflater
        // We cant findViewByID our fragment.
        View view = inflater.inflate(R.layout.fragment_insert_child, container);

        // Connect our class objects to our fragment layout objects
        childFirstNameEditText     = view.findViewById(R.id.editTextChildFirstName_addChildFragment);
        childLastNameEditText      = view.findViewById(R.id.editTextChildLastName_addChildFragment);
        childBirthdayDayEditText   = view.findViewById(R.id.editTextChildBirthdayDay_addAChildFragment);

        // Applying a filter for the editTextAge to 2 numerals long. day 01-31
        int dayMaxLength = 2;
        InputFilter[] DayFilterArray = new InputFilter[1];
        DayFilterArray[0] = new InputFilter.LengthFilter(dayMaxLength);
        childBirthdayDayEditText.setFilters(DayFilterArray);

        childBirthdayMonthEditText = view.findViewById(R.id.editTextChildBirthdayMonth_addAChildFragment);

        // Applying a filter for the editTextMonth to 2 numerals long. 01 - 12
        int monthMaxLength = 2;
        InputFilter[] MonthFilterArray = new InputFilter[1];
        MonthFilterArray[0] = new InputFilter.LengthFilter(monthMaxLength);
        childBirthdayDayEditText.setFilters(MonthFilterArray);

        childBirthdayYearEditText  = view.findViewById(R.id.editTextChildBirthdayYear_addAChildFragment);

        // Applying a filter for the year to 4 numerals long. 0000 - 9999
        int yearMaxLength = 4;
        InputFilter[] yearFilterArray = new InputFilter[1];
        yearFilterArray[0] = new InputFilter.LengthFilter(yearMaxLength);
        childBirthdayYearEditText.setFilters(yearFilterArray);

        parentFirstNameEditText    = view.findViewById(R.id.editTextParentFirstName_addChildFragment);
        parentLastNameEditText     = view.findViewById(R.id.editTextParentLastName_addChildFragment);
        saveButtonChild            = view.findViewById(R.id.buttonSave_addChildFragment);
        cancelButtonChild          = view.findViewById(R.id.buttonCancel_addChildFragment);




        // onClickListener for our buttons
        cancelButtonChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // dismiss the dialog
                getDialog().dismiss();
            }
        });

        // saveButton onClickListener. Consider using date picker for birthday.
        saveButtonChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // We want to get the user inputs and check if acceptable.
                String childFirstName = childFirstNameEditText.getText().toString().trim();
                String childLastName  = childLastNameEditText.getText().toString().trim();
                String childBirthdayDay   = childBirthdayDayEditText.getText().toString().trim();
                String childBirthdayMonth = childBirthdayMonthEditText.getText().toString().trim();
                String childBirthdayYear  = childBirthdayYearEditText.getText().toString().trim();
                String parentFirstName= parentFirstNameEditText.getText().toString().trim();
                String parentLastName = parentLastNameEditText.getText().toString().trim();

                // Check if acceptable.
                Log.d(TAG, "User inputs received");
                if(childFirstName.isEmpty()){
                    Log.d(TAG, "First name check");
                    childFirstNameEditText.setError("Child first name is Required");
                    childFirstNameEditText.requestFocus();
                    return;
                }
                if(childLastName.isEmpty()){
                    Log.d(TAG, "Last name check");
                    childLastNameEditText.setError("Child first name is Required");
                    childLastNameEditText.requestFocus();
                    return;
                }
                if( (childBirthdayDay.isEmpty()) || (childBirthdayMonth.isEmpty()) || (childBirthdayYear.isEmpty()) ){
                    Log.d(TAG, "Birthday check");
                    childBirthdayYearEditText.setError("Child date of birth is required");
                    childBirthdayYearEditText.requestFocus();
                    return;
                }
                if( (Integer.parseInt(childBirthdayDay) < 1) || Integer.parseInt(childBirthdayDay) > 31 ){
                    Log.d(TAG, "Birthday day check");
                    childBirthdayDayEditText.setError("Wrong day format");
                    childBirthdayDayEditText.requestFocus();
                    return;
                }
                if( (Integer.parseInt(childBirthdayMonth) < 1) || Integer.parseInt(childBirthdayMonth) > 12 ){
                    Log.d(TAG, "Birthday month check");
                    childBirthdayMonthEditText.setError("Wrong month format");
                    childBirthdayMonthEditText.requestFocus();
                    return;
                }
                if( ( Integer.parseInt(childBirthdayYear) < 1980 ) || Integer.parseInt(childBirthdayYear) > 2500 ){
                    Log.d(TAG, "Birthday year check");
                    childBirthdayYearEditText.setError("Wrong year format");
                    childBirthdayYearEditText.requestFocus();
                    return;
                }
                if(parentFirstName.isEmpty()){
                    Log.d(TAG, "parent first name  check");
                    parentFirstNameEditText.setError("Parent first name is required");
                    parentFirstNameEditText.requestFocus();
                    return;
                }
                if(parentLastName.isEmpty()){
                    Log.d(TAG, "parent last name  check");
                    parentLastNameEditText.setError("Parent first name is required");
                    parentLastNameEditText.requestFocus();
                    return;
                }
                Log.d(TAG, "User inputs are accepted ready to be add a child");

                // If accepted than save the new child in the database with parent ID and employee ID.
                // Adding child to the database.
                // First we will need the employee and parent userID.

                // Employee ID is easy we just need to use the authentication instance
                FirebaseUser userEmployee = mAuth.getCurrentUser();
                String employeeID = userEmployee.getUid();
                String[] parentID = {null};
                // The parents is gonna be a bit more complicated basically we have to check the
                // database and look for the parents UID from their names we go from the insertChildDialog.
                

                // Now add a child to database with the information




                // dismiss the dialog
                //getDialog().dismiss();
            // end of onclick
            }
        });

        return view;
        // end of onCreateView
    }

    // end of InsertChildDialog
}
