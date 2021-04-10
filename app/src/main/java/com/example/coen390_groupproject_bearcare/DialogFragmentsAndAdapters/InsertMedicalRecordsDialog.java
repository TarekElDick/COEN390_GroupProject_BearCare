package com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coen390_groupproject_bearcare.R;
import com.example.coen390_groupproject_bearcare.Storage.PutFile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import javax.annotation.Nonnull;

import static android.app.Activity.RESULT_OK;

public class InsertMedicalRecordsDialog extends DialogFragment {


    private EditText fileName;
    private Button uploadButton;
    private String parentId;
    private FirebaseUser fUser;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Initializing Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        // Initializing Cloud FireStore
        fStore = FirebaseFirestore.getInstance();

        //
        fUser = mAuth.getCurrentUser();

        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference("uploadPDF");

        View view = inflater.inflate(R.layout.fragment_upload_medical,container);

        fileName = view.findViewById(R.id.editTextFileName);
        uploadButton = view.findViewById(R.id.buttonUploadRecord);

        uploadButton.setEnabled(false);


        fileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });

        return view;
    }


    private void selectPDF(){
        Intent intent = new Intent();
        intent.setType("application/PDF");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"PDF FILE SELECT"),12);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 12 && resultCode==RESULT_OK && data!= null && data.getData() !=null) {
            uploadButton.setEnabled(true);
            fileName.setText(data.getDataString().substring(data.getDataString().indexOf("/") + 1));

            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPDF(data.getData());
                }
            });

        }
    }

    private void uploadPDF(Uri data) {

        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle("File is uploading");
        progress.show();

        StorageReference ref = storageRef.child("upload"+System.currentTimeMillis()+".pdf");

        ref.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete());
                        Uri uri = uriTask.getResult();

                        PutFile putPDF = new PutFile(fileName.getText().toString(),uri.toString());
                        databaseRef.child(databaseRef.push().getKey()).setValue(putPDF);
                        Toast.makeText(getActivity(), "File Uploaded", Toast.LENGTH_SHORT).show();
                        progress.dismiss();


                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressValue = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                progress.setMessage("File Uploaded  " + (int) progressValue + "%");

            }
        });


    }
}
