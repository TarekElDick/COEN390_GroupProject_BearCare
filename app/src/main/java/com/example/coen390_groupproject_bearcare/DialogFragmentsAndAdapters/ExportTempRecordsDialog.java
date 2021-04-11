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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coen390_groupproject_bearcare.Model.Child;
import com.example.coen390_groupproject_bearcare.Model.Temperature;
import com.example.coen390_groupproject_bearcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ExportTempRecordsDialog extends DialogFragment {

    private static final String TAG = "ChildrenNames";
    Button cancelButton, exportButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String[] children;
    private String childId;
    public interface OnChildListener{
        void sendChildId(String childId, String fullName);
    }

    public ExportTempRecordsDialog.OnChildListener mOnChildListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_export_temperature, container);
        exportButton = view.findViewById(R.id.buttonExportTempRecords);
        cancelButton = view.findViewById(R.id.buttonCancelTempRecords);
        AutoCompleteTextView editText = view.findViewById(R.id.editTextAutoCompleteChildName);

        db.collection("Children")
                .orderBy("fullName", Query.Direction.valueOf("ASCENDING"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            children = new String[task.getResult().size()];
                            Log.d(TAG, "onComplete: size = "+ task.getResult().size());
                            int index = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Child temp = document.toObject(Child.class);
                                children[index] = temp.getFullName();
                                Log.d(TAG, "onComplete: index = " +index+", name = "+children[index]);
                                index++;
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, children);
                            editText.setAdapter(adapter);
                            editText.setOnTouchListener(new View.OnTouchListener() {

                                @SuppressLint("ClickableViewAccessibility")
                                @Override
                                public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                                    if (children.length > 0) {
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
                if (editText.getText().toString().isEmpty()) {
                    editText.setError(getString(R.string.full_name_required));
                    editText.requestFocus();
                } else {

                    String fullName = editText.getText().toString();
                    db.collection("Children")
                            .whereEqualTo("fullName", fullName)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            childId = document.getId();
                                        }

                                        mOnChildListener.sendChildId(childId,fullName);


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
            mOnChildListener = (ExportTempRecordsDialog.OnChildListener) getActivity();
        }catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

}
