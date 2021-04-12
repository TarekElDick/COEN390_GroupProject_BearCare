package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.InsertMedicalRecordsDialog;
import com.example.coen390_groupproject_bearcare.Storage.UploadFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MedicalRecordsActivity extends AppCompatActivity {

    private static final String TAG = "IN MEDICAL RECORDS" ;
    private Button goToFragment;
    private static final int REQUEST_CODE = 1;
    private String childId, childName;
    private ListView medicalRecordsList;
    private DatabaseReference databaseRef, dataBaseRefDownload;                                                          // firebase data object
    private FirebaseStorage storage;
    private StorageReference storageRef,ref;
    private ArrayList<UploadFile> uploadedFilesArray = new ArrayList<>();                                               // array list of UploadFile objects
    private TextView child;                                                                         // set child's name


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_records);

        goToFragment = findViewById(R.id.buttonUploadRecord);
        child = findViewById(R.id.textViewChildNameMed);
        medicalRecordsList = findViewById(R.id.listViewMedicalRecords);


        checkStoragePermission();

        Intent intent = getIntent();
        childId = intent.getStringExtra("childId");
        childName = intent.getStringExtra("childName");

        child.setText(childName + " Medical Records");


        retrieveFilesList();

        goToFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertMedicalRecordsDialog dialog = new InsertMedicalRecordsDialog();

                Bundle notificationBundle = new Bundle();
                notificationBundle.putString("childId", childId);
                notificationBundle.putString("childName", childName);
                dialog.setArguments(notificationBundle);


                dialog.show(getSupportFragmentManager(), "Create Medical Record");
            }
        });

        medicalRecordsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                UploadFile fileToGet = uploadedFilesArray.get(position);

                String file = fileToGet.getName();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();


                Toast.makeText(getApplicationContext(),fileToGet.getName()+ " " + " " + fileToGet.getUrl(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setType(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(fileToGet.getUrl()));
                Intent choose = Intent.createChooser(intent,getString(R.string.choose_application));
                startActivity(choose);

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void retrieveFilesList() {                                                              // retrieve the files to the recyle view
        uploadedFilesArray.clear();

        databaseRef = FirebaseDatabase.getInstance().getReference("uploadPDF/" + childName);  // go to storage and get the PDFs associated with the childname
        databaseRef.addValueEventListener(new ValueEventListener(){


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {                              // get data out of the node

                ArrayList<String> uploadedFilesString = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()){
                    UploadFile uploadedFiles = ds.getValue(UploadFile.class);
                    uploadedFilesArray.add(uploadedFiles);

                }

                for(int i =0; i < uploadedFilesArray.size(); i++)
                {
                    uploadedFilesString.add(uploadedFilesArray.get(i).toString());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,uploadedFilesString);






                medicalRecordsList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(),"Error" + " " + error.getMessage(), Toast.LENGTH_LONG).show();       // didnt work show the error

            }
        });
    }

    private void checkStoragePermission()
    {
        Log.d(TAG, "checkStoragePermission: asking user for permission");
        String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[0]) == PackageManager.PERMISSION_GRANTED)
        {
            return;

        }else{
            ActivityCompat.requestPermissions(MedicalRecordsActivity.this,permissions,REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checkStoragePermission();
    }

}