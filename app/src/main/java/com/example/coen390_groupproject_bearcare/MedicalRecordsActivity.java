package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.InsertMedicalRecordsDialog;
import com.example.coen390_groupproject_bearcare.Storage.UploadFile;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.auth.api.signin.internal.Storage;
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
import com.itextpdf.text.pdf.codec.Base64;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static java.io.File.createTempFile;

public class MedicalRecordsActivity extends AppCompatActivity {

    private static final String TAG = "IN MEDICAL RECORDS" ;
    private Button goToFragment;
    private static final int REQUEST_CODE = 1;
    private String childId, childName;
    private ListView medicalRecordsList;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseRef;                                                          // firebase data object
    private final ArrayList<UploadFile> uploadedFilesArray = new ArrayList<>();                                               // array list of UploadFile objects
    private TextView child;                                                                         // set child's name

// gs://bearcare-9915a.appspot.com/uploadPDF/Fred Smith/Fred Smith Medical.pdf

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_records);

        Intent intent = getIntent();
        childId = intent.getStringExtra("childId");
        childName = intent.getStringExtra("childName");

        goToFragment = findViewById(R.id.buttonUploadRecord);
        child = findViewById(R.id.textViewChildNameMed);
        medicalRecordsList = findViewById(R.id.listViewMedicalRecords);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://bearcare-9915a.appspot.com/uploadPDF/Fred Smith/Fred Smith Medical.pdf");
        StorageReference pathRef = storageReference.child("uploadPDF/");
        StorageReference lowerPathRef = pathRef.child(pathRef.toString() +childName);

        //Toast.makeText(getApplicationContext(),lowerPathRef.toString(),Toast.LENGTH_LONG).show();

        checkStoragePermission();

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

                String fileName = fileToGet.getName();


//                StorageReference fileRef = storageReference.child(lowerPathRef.toString() +"/" + fileName +".pdf");

              //  Toast.makeText(getApplicationContext(),fileRef.toString(),Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),fileToGet.getName()+ " " + " " + fileToGet.getUrl(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("application/pdf");
                intent.setData(Uri.parse("gs://bearcare-9915a.appspot.com/uploadPDF/"+childName+"/" + fileName +".pdf"));
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

    private void retrieveFilesList() {                                                              // retrieve the files to the recycle view
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
                    String nameUrl = "File name: " + uploadedFilesArray.get(i).getName() + "\n" + "File URL: " + uploadedFilesArray.get(i).getUrl();
                    uploadedFilesString.add(nameUrl);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,uploadedFilesString);


                medicalRecordsList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(),getString(R.string.error) + " " + error.getMessage(), Toast.LENGTH_LONG).show();       // didnt work show the error

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