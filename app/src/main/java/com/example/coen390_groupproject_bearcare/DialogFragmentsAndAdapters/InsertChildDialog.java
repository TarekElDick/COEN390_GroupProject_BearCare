package com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters;

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

import com.example.coen390_groupproject_bearcare.Model.Child;
import com.example.coen390_groupproject_bearcare.Model.Date;
import com.example.coen390_groupproject_bearcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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

    //
    private FirebaseUser fUser;

    //
    private String parentId;

    String TAG = "debug_insertChildDialog";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Initializing Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        // Initializing Cloud FireStore
        fStore = FirebaseFirestore.getInstance();

        //
        fUser = mAuth.getCurrentUser();

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
        childBirthdayMonthEditText.setFilters(MonthFilterArray);

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

                // Add Child to the database before that
                // Look for child's parents and get the parentUID.
                // -> if parent doesn't exits then don't allow for child to be saved.
                // For now a child can only have one parent but we must implement a way for additional parent to add.
                // Get employeeID which is less complicated than getting the parents.

                // 1) get the parentsUID, also implement the user custom object
                fStore.collection("Users")
                        .whereEqualTo("firstName", parentFirstName)
                        .whereEqualTo("lastName", parentLastName)
                        .limit(1)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                parentId = document.getId();
                                Log.d(TAG, "Parent's Child found");
                                // Get the employeeId
                                String employeeId = fUser.getUid();

                                // Create the new child and birthday date using our custom classes.
                                Date date = new Date(Integer.parseInt(childBirthdayDay),Integer.parseInt(childBirthdayMonth),Integer.parseInt(childBirthdayYear) );
                                Child child = new Child( parentId, employeeId,childFirstName , childLastName, date );

                                // 2) Create the collection of Children for their information
                                // and documents ID are autogenerated with the use of .add(child)
                                // 3) Map the date using custom Child class that fireStore converts the objects into supported data types.
                                fStore.collection("Children").add(child).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                        getDialog().dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        if (parentId == null){
                            Log.d(TAG, "Parent's Child could not be found");
                            parentFirstNameEditText.setError("Parent not found: Case is sensitive");
                            parentFirstNameEditText.requestFocus();
                        }
                    }
                });
            // end of onclick
            }
        });

        return view;
        // end of onCreateView
    }

    // end of InsertChildDialog
}
