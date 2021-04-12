package com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coen390_groupproject_bearcare.R;
import com.example.coen390_groupproject_bearcare.Storage.UploadFile;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

public class InsertMedicalRecordsDialog extends DialogFragment {


    private static final String TAG = "ON CREATE FRAG" ;
    private EditText fileName, nameFile;                                                            // to select and name the file
    private Button uploadButton;                                                                    // self explanatory
    private FirebaseUser fUser;                                                                     // user needs to be authenticated
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private Uri fileUri;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private StorageTask uploadTask;

    private String childName, childId;

    private static final int PICK_FILE_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Initializing Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        // Initializing Cloud FireStore
        fStore = FirebaseFirestore.getInstance();

        //
        fUser = mAuth.getCurrentUser();



        View view = inflater.inflate(R.layout.fragment_upload_medical,container);

        fileName = view.findViewById(R.id.editTextFileName);
        nameFile = view.findViewById(R.id.editTextNameFile);
        uploadButton = view.findViewById(R.id.buttonUploadRecord);


        childId = this.getArguments().getString("ChildId");
        childName = this.getArguments().getString("childName");

        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference("uploadPDF/" + childName);  //uploadPDF\ChildName

        Log.d(TAG, "ChildID: "+ childId+ " ChildName: "+ childName);

        uploadButton.setEnabled(false);


        fileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openFileChooser();
            }
        });

        return view;
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data!= null && data.getData() !=null) {

            fileName.setText(data.getDataString().substring(data.getDataString().indexOf("/") + 1));
            fileUri = data.getData();


            if(nameFile.getText() != null)                                                          // make the user name the file
                uploadButton.setEnabled(true);                                                      // let them upload

            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(uploadTask != null && uploadTask.isInProgress()){
                        Toast.makeText(getActivity(), R.string.upload_in_progress,Toast.LENGTH_SHORT).show();
                    }else {
                        uploadPDF(fileUri);
                    }
                }
            });
        }
    }

    private void uploadPDF(Uri data) {

        String name = nameFile.getText().toString();

        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle("File is uploading");
        progress.show();

        StorageReference ref = storageRef.child(name + ".pdf");                                     // get the name set by the user in the edit text nameFile

        uploadTask= ref.putFile(data)                                                               // use this to prevent multiple clicks on the upload button
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        UploadFile putPDF = new UploadFile(nameFile.getText().toString().trim(),taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                        String uploadID = databaseRef.push().getKey();                              // use to create a unique id in database
                        databaseRef.child(uploadID).setValue(putPDF);                               // now put the object into the database with the unique key will show up in realtime database
                        Toast.makeText(getActivity(), R.string.file_uploaded, Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                        dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressValue = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                progress.setMessage( R.string.file_uploaded + (int) progressValue + "%");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), R.string.upload_failed,Toast.LENGTH_LONG).show();
            }
        });


    }
}
