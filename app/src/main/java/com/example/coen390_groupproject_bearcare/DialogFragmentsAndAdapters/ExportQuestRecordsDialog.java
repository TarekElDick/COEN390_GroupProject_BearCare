package com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coen390_groupproject_bearcare.Model.User;
import com.example.coen390_groupproject_bearcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ExportQuestRecordsDialog extends DialogFragment {

    private static final String TAG = "ParentNames";
    Button cancelButton, exportButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String[] parents;
    private String parentId;
    public interface OnParentListener{
        void sendParentId(String parentId, String fullName);
    }

    public OnParentListener mOnParentListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_export_questionnaire_by_name, container);
        exportButton = view.findViewById(R.id.buttonExport_quest);
        cancelButton = view.findViewById(R.id.buttonCancel_quest);

//        EditText day = view.findViewById(R.id.day_quest);
//        EditText month = view.findViewById(R.id.month_quest);
//        EditText year = view.findViewById(R.id.year_quest);
//
//        // Applying a filter for the editTextAge to 2 numerals long. day 01-31
//        int dayMaxLength = 2;
//        InputFilter[] DayFilterArray = new InputFilter[1];
//        DayFilterArray[0] = new InputFilter.LengthFilter(dayMaxLength);
//        day.setFilters(DayFilterArray);
//
//        // Applying a filter for the editTextMonth to 2 numerals long. 01 - 12
//        int monthMaxLength = 2;
//        InputFilter[] MonthFilterArray = new InputFilter[1];
//        MonthFilterArray[0] = new InputFilter.LengthFilter(monthMaxLength);
//        month.setFilters(MonthFilterArray);
//
//        // Applying a filter for the year to 4 numerals long. 0000 - 9999
//        int yearMaxLength = 4;
//        InputFilter[] yearFilterArray = new InputFilter[1];
//        yearFilterArray[0] = new InputFilter.LengthFilter(yearMaxLength);
//        year.setFilters(yearFilterArray);

        AutoCompleteTextView editText = view.findViewById(R.id.editTextAutoCompleteParentName);

        db.collection("Users")
                .whereEqualTo("employee",false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            parents = new String[task.getResult().size()];
                            Log.d(TAG, "onComplete: size = "+ task.getResult().size());
                            int index = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                User temp = document.toObject(User.class);
                                parents[index] = temp.getFullName();
                                Log.d(TAG, "onComplete: index = " +index+", name = "+parents[index]);
                                index++;
                            }

                            Arrays.sort(parents);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, parents);
                            editText.setAdapter(adapter);
                            editText.setOnTouchListener(new View.OnTouchListener() {

                                @SuppressLint("ClickableViewAccessibility")
                                @Override
                                public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                                    if (parents.length > 0) {
                                        // show all suggestions
                                        if (!editText.getText().toString().equals(""))
                                            adapter.getFilter().filter(null);
                                        editText.showDropDown();
                                    }
                                    return false;
                                }
                            });

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if ((day.getText().toString().isEmpty()) || (month.getText().toString().isEmpty()) || (year.getText().toString().isEmpty())) {
//                    Log.d(TAG, "Birthday check");
//                    year.setError(getString(R.string.date_of_birth_required));
//                    year.requestFocus();
//                    return;
//                }
//                if ((Integer.parseInt(day.getText().toString()) < 1) || Integer.parseInt(day.getText().toString()) > 31) {
//                    Log.d(TAG, "Birthday day check");
//                    day.setError(getString(R.string.wrong_day_format));
//                    day.requestFocus();
//                    return;
//                }
//                if ((Integer.parseInt(month.getText().toString()) < 1) || Integer.parseInt(month.getText().toString()) > 12) {
//                    Log.d(TAG, "Birthday month check");
//                    month.setError(getString(R.string.wrong_month_format));
//                    month.requestFocus();
//                    return;
//                }
//                if ((Integer.parseInt(year.getText().toString()) < 1980) || Integer.parseInt(year.getText().toString()) > 2500) {
//                    Log.d(TAG, "Birthday year check");
//                    year.setError(getString(R.string.wrong_year_format));
//                    year.requestFocus();
//                    return;
//                } else {
//
//
//
//                }

                if (editText.getText().toString().isEmpty()) {
                    editText.setError(getString(R.string.full_name_required));
                    editText.requestFocus();
                } else {

                    String fullName = editText.getText().toString();
                    db.collection("Users")
                            .whereEqualTo("fullName", fullName)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            parentId = document.getId();
                                        }

                                        mOnParentListener.sendParentId(parentId,fullName);

                                    }
                                }
                            });
                }

              }

        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mOnParentListener = (OnParentListener) getActivity();
        }catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
