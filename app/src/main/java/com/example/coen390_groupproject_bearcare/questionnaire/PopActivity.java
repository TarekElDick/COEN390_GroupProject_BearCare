package com.example.coen390_groupproject_bearcare.questionnaire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.coen390_groupproject_bearcare.R;

public class PopActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        Log.d("PopActivity", "POPPED");
        Intent intent =  getIntent();
        String userName = intent.getStringExtra("userName");
        String userId = intent.getStringExtra("userId");
        Button agree = findViewById(R.id.acceptButton);
        TextView text = findViewById(R.id.popUpTextView);

        text.setText("By clicking Agree, I, " + userName + ", agree that the information provided is true and that any misinformation could lead to legal complications");

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionnaireTermsSingleton.get().areAccepted();
                finish();
                //fill form
                //send back to user main page activity
            }
        });


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.45));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

    }

    void fillAndNotify(){

    }
}