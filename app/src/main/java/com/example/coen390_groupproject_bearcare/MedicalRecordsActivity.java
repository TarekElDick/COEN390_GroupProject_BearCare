package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.InsertMedicalRecordsDialog;

public class MedicalRecordsActivity extends AppCompatActivity {

    private static final String TAG = "IN MEDICAL RECORDS" ;
    private Button goToFragment;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_records);

        goToFragment = findViewById(R.id.buttonUploadRecord);

        checkStoragePermission();

        goToFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertMedicalRecordsDialog dialog = new InsertMedicalRecordsDialog();


                dialog.show(getSupportFragmentManager(),"Create Medical Record");
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