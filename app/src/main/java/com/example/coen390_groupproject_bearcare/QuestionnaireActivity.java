package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.Questionnaire.PopActivity;
import com.example.coen390_groupproject_bearcare.Questionnaire.QuestionnaireTermsSingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class QuestionnaireActivity extends AppCompatActivity {


String TAG = "debug_questionnaireActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        Log.d(TAG, "onCreate");
        //setUpUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if(true){

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        // Connections
        CheckBox list1 = findViewById(R.id.questionnaireCheckBox1);
        CheckBox list2 = findViewById(R.id.questionnaireCheckBox2);
        CheckBox list3 = findViewById(R.id.questionnaireCheckBox3);
        CheckBox list4 = findViewById(R.id.questionnaireCheckBox4);
        CheckBox list5 = findViewById(R.id.questionnaireCheckBox5);
        CheckBox list6 = findViewById(R.id.questionnaireCheckBox6);
        CheckBox list7 = findViewById(R.id.questionnaireCheckBox7);
        CheckBox list8 = findViewById(R.id.questionnaireCheckBox8);
        CheckBox list9 = findViewById(R.id.questionnaireCheckBox9);
        CheckBox list10 = findViewById(R.id.questionnaireCheckBox10);
        CheckBox question1 = findViewById(R.id.questionnaireCheckBox11);
        CheckBox question2 = findViewById(R.id.questionnaireCheckBox12);
        CheckBox question3 = findViewById(R.id.questionnaireCheckBox13);
        CheckBox question4 = findViewById(R.id.questionnaireCheckBox14);
        CheckBox question5 = findViewById(R.id.questionnaireCheckBox15);
        CheckBox question6 = findViewById(R.id.questionnaireCheckBox16);
        CheckBox question7 = findViewById(R.id.questionnaireCheckBox17);
        CheckBox noToAll = findViewById(R.id.questionnaireCheckBox18);
        Button submitQuestionnaire =  findViewById(R.id.questionnaireSubmitButton);
        TableRow lastRow = findViewById(R.id.row18);
        boolean notifyDirector = false;
        Map<String, String> allCheck = new HashMap<>();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        Intent intent =  getIntent();
        String userName = intent.getStringExtra("userName");
        String userId = intent.getStringExtra("userId");
        Calendar calendar = Calendar.getInstance();
        String tempTimeStamp = DateFormat.getDateInstance().format(calendar.getTime());


        if (QuestionnaireTermsSingleton.get().getStatus()==true){

            allCheck.put("Date", tempTimeStamp);

            if (list1.isChecked()) {
                allCheck.put("Cough (new or aggravated)", "YES");
            } else {
                allCheck.put("Cough (new or aggravated)", "NO");
            }
            if (list2.isChecked()) {
                allCheck.put("Fever (rectal temperature of 38.5°C or higher", "YES");
            } else {
                allCheck.put("Fever (rectal temperature of 38.5°C or higher", "NO");
            }
            if (list3.isChecked()) {
                allCheck.put("Sudden loss of smell (without nasal congestion", "YES");
            } else {
                allCheck.put("Sudden loss of smell (without nasal congestion", "NO");
            }
            if (list4.isChecked()) {
                allCheck.put("Difficulty Breathing", "YES");
            } else {
                allCheck.put("Difficulty Breathing", "NO");
            }
            if (list5.isChecked()) {
                allCheck.put("Muscle pain (generalised - not related to physical effort)", "YES");
            } else {
                allCheck.put("Muscle pain (generalised - not related to physical effort)", "NO");
            }
            if (list6.isChecked()) {
                allCheck.put("Intense fatigue", "YES");
            } else {
                allCheck.put("Intense fatigue", "NO");
            }
            if (list7.isChecked()) {
                allCheck.put("Major loss of appetite", "YES");
            } else {
                allCheck.put("Major loss of appetite", "NO");
            }
            if (list8.isChecked()) {
                allCheck.put("Sore throat", "YES");
            } else {
                allCheck.put("Sore throat", "NO");
            }
            if (list9.isChecked()) {
                allCheck.put("Runny nose", "YES");
            } else {
                allCheck.put("Runny nose", "NO");
            }
            if (list10.isChecked()) {
                allCheck.put("Vomiting or Diarrhea", "YES");
            } else {
                allCheck.put("Vomiting or Diarrhea", "NO");
            }
            if (question1.isChecked()) {
                allCheck.put("Does any of the household members have any of the symptoms listed above?", "YES");
            } else {
                allCheck.put("Does any of the household members have any of the symptoms listed above?", "NO");
            }
            if (question2.isChecked()) {
                allCheck.put("Have you given your child any medication in the last 24 hours to reduce fevers?", "YES");
            } else {
                allCheck.put("Have you given your child any medication in the last 24 hours to reduce fevers?", "NO");
            }
            if (question3.isChecked()) {
                allCheck.put("Has your child received a positive COVID-19 diagnosis?", "YES");
            } else {
                allCheck.put("Has your child received a positive COVID-19 diagnosis?", "NO");
            }
            if (question4.isChecked()) {
                allCheck.put("Is your child waiting for the results of a COVID-19 test?", "YES");
            } else {
                allCheck.put("Is your child waiting for the results of a COVID-19 test?", "NO");
            }
            if (question5.isChecked()) {
                allCheck.put("Did the child's parent or someone living with the child receive a positive diagnosis for COVID-19 test?", "YES");
            } else {
                allCheck.put("Did the child's parent or someone living with the child receive a positive diagnosis for COVID-19 test?", "NO");
            }
            if (question6.isChecked()) {
                allCheck.put("Are the child's parent or someone living with the child waiting for the results of a COVID-19 test?", "YES");
            } else {
                allCheck.put("Are the child's parent or someone living with the child waiting for the results of a COVID-19 test?", "NO");
            }
            if (question7.isChecked()) {
                allCheck.put("Did the child or a member of the family whom the child lives with travel outside of Canada in the last 14 days?", "YES");
            } else {
                allCheck.put("Did the child or a member of the family whom the child lives with travel outside of Canada in the last 14 days?", "NO");
            }

            if(question1.isChecked() || question2.isChecked() || question3.isChecked() || question4.isChecked()
                    || question5.isChecked() || question6.isChecked() || question7.isChecked() || list1.isChecked() || list2.isChecked()
                    || list3.isChecked() || list4.isChecked() || list5.isChecked() || list6.isChecked() || list7.isChecked()
                    || list8.isChecked() || list9.isChecked() || list10.isChecked())
            {

                if (noToAll.isChecked() == true){
                    //TODO FIX ERROR MESSAGE
                    noToAll.setError("some symptoms are checked");
                    noToAll.requestFocus();
                }
                else {
                    notifyDirector = true; //TODO notify director
                    fStore.collection("Users").document(userId).collection("Questionnaires").document()
                            .set(allCheck)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });

                    QuestionnaireTermsSingleton.get().reset();
                    Toast.makeText(QuestionnaireActivity.this, "Questionnaire Filled Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), UserMainPageActivity.class));
                }

            }

            else if(noToAll.isChecked() == false){
                //TODO FIX ERROR MESSAGE
                noToAll.setError("check box if the answer is NO TO ALL");
                noToAll.requestFocus();
            }
            else {

                fStore.collection("Users").document(userId).collection("Questionnaires").document()
                        .set(allCheck)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                QuestionnaireTermsSingleton.get().reset();
                Toast.makeText(QuestionnaireActivity.this, "Questionnaire Filled Successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), UserMainPageActivity.class));
            }

        }

        // onClickListeners
        submitQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //popup
                Log.d(TAG, "submit clicked");

                Intent intent = new Intent(getApplicationContext(), PopActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userName", userName);
                startActivity(intent);

            }
        });


    }

}