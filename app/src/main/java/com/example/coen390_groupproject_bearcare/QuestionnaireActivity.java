package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.Questionnaire.PopActivity;
import com.example.coen390_groupproject_bearcare.Questionnaire.QuestionnaireTermsSingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
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
        CheckBox noToList = findViewById(R.id.questionnaireCheckBox10plus1);
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
        Map<String, Object> allCheck = new HashMap<>();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        Intent intent =  getIntent();
        String userName = intent.getStringExtra("userName");
        String userId = intent.getStringExtra("userId");
        Calendar calendar = Calendar.getInstance();
        String tempTimeStamp = DateFormat.getDateInstance().format(calendar.getTime());
        int day = new Date().getDate();
        ScrollView sv = findViewById(R.id.scrollView2);


        if (QuestionnaireTermsSingleton.get().getStatus()==true){

            allCheck.put("Date", tempTimeStamp);
            allCheck.put("day", day);


            if (list1.isChecked()) {
                allCheck.put("zero", "YES");
            } else {
                allCheck.put("zero", "NO");
            }
            if (list2.isChecked()) {
                allCheck.put("one", "YES");
            } else {
                allCheck.put("one", "NO");
            }
            if (list3.isChecked()) {
                allCheck.put("two", "YES");
            } else {
                allCheck.put("two", "NO");
            }
            if (list4.isChecked()) {
                allCheck.put("three", "YES");
            } else {
                allCheck.put("three", "NO");
            }
            if (list5.isChecked()) {
                allCheck.put("four", "YES");
            } else {
                allCheck.put("four", "NO");
            }
            if (list6.isChecked()) {
                allCheck.put("five", "YES");
            } else {
                allCheck.put("five", "NO");
            }
            if (list7.isChecked()) {
                allCheck.put("six", "YES");
            } else {
                allCheck.put("six", "NO");
            }
            if (list8.isChecked()) {
                allCheck.put("seven", "YES");
            } else {
                allCheck.put("seven", "NO");
            }
            if (list9.isChecked()) {
                allCheck.put("eight", "YES");
            } else {
                allCheck.put("eight", "NO");
            }
            if (list10.isChecked()) {
                allCheck.put("nine", "YES");
            } else {
                allCheck.put("nine", "NO");
            }
            if (question1.isChecked()) {
                allCheck.put("ten", "YES");
            } else {
                allCheck.put("ten", "NO");
            }
            if (question2.isChecked()) {
                allCheck.put("eleven", "YES");
            } else {
                allCheck.put("eleven", "NO");
            }
            if (question3.isChecked()) {
                allCheck.put("twelve", "YES");
            } else {
                allCheck.put("twelve", "NO");
            }
            if (question4.isChecked()) {
                allCheck.put("thirteen", "YES");
            } else {
                allCheck.put("thirteen", "NO");
            }
            if (question5.isChecked()) {
                allCheck.put("fourteen", "YES");
            } else {
                allCheck.put("fourteen", "NO");
            }
            if (question6.isChecked()) {
                allCheck.put("fifteen", "YES");
            } else {
                allCheck.put("fifteen", "NO");
            }
            if (question7.isChecked()) {
                allCheck.put("sixteen", "YES");
            } else {
                allCheck.put("sixteen", "NO");
            }

            if(question1.isChecked() || question2.isChecked() || question3.isChecked() || question4.isChecked()
                    || question5.isChecked() || question6.isChecked() || question7.isChecked() || list1.isChecked() || list2.isChecked()
                    || list3.isChecked() || list4.isChecked() || list5.isChecked() || list6.isChecked() || list7.isChecked()
                    || list8.isChecked() || list9.isChecked() || list10.isChecked())
            {

                if (noToAll.isChecked() == true){
                    //TODO FIX ERROR MESSAGE
                    Log.d(TAG, "onResume: some errors are checked");
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

            else if(noToAll.isChecked() == false || noToList.isChecked() == false){
                //TODO FIX ERROR MESSAGE
                if (noToAll.isChecked() == false){
                    Log.d(TAG, "onResume: no to all");
                    noToAll.requestFocus();
                    noToAll.setError("check box if the answer is NO TO ALL");
                    return;
                    }
                if (noToList.isChecked() == false){
                    Log.d(TAG, "onResume: no to list");
                    scrollToView(sv, noToList);
                    noToList.setError("check box if the answer is NO TO LIST");
                    return;
                    }
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

    public static void scrollToView(final View scrollView, final View view) {
        view.requestFocus();
        final Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);
        if (!view.getLocalVisibleRect(scrollBounds)) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    int toScroll = getRelativeTop(view) - getRelativeTop(scrollView);
                    ((ScrollView) scrollView).smoothScrollTo(0, toScroll-120);
                }
            });
        }
    }
    public static int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView()) return myView.getTop();
        else return myView.getTop() + getRelativeTop((View) myView.getParent());
    }
}