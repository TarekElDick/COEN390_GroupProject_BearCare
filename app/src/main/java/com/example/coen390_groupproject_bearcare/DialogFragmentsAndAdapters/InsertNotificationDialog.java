package com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.coen390_groupproject_bearcare.AlertReceiver;
import com.example.coen390_groupproject_bearcare.Model.Child;
import com.example.coen390_groupproject_bearcare.Model.Date;
import com.example.coen390_groupproject_bearcare.Model.Notification;
import com.example.coen390_groupproject_bearcare.Model.Time;
import com.example.coen390_groupproject_bearcare.R;
import com.example.coen390_groupproject_bearcare.SignUpActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Objects;

public class InsertNotificationDialog extends DialogFragment {

    // Private class objects.
    private TextView textViewChildName, textViewDate, textViewTime;
    private EditText editTextTitle, editTextDescription;
    private Button buttonSave, buttonCancel;
    private ProgressBar progressBar;

    private String childName, childId, notificationTitle, notificationDescription;
    private int mDay, mMonth, mYear, mHour, mMinute,  notificationDay,  notificationMonth, notificationYear,  notificationHour,  notificationMinute;

    // Date and Time Picker dialog objects.
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    String TAG = "debug_insertNotifications";

    // OnCreateView for our dialog.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 1) Connect an inflater which inflates the xml layout kinda like the engine, we pass the fragment and container.
        View view = inflater.inflate(R.layout.fragment_insert_notification, container);

        // 2) Connect our class objects to our fragment layout objects.
        textViewChildName   = view.findViewById(R.id.textViewChildName_insertNotification);
        editTextTitle       = view.findViewById(R.id.editTextTitle_insertNotification);
        editTextDescription = view.findViewById(R.id.editTextDescription_insertNotification);
        textViewDate        = view.findViewById(R.id.textViewDate_insertNotification);
        textViewTime        = view.findViewById(R.id.textViewTime_insertNotification);
        buttonSave          = view.findViewById(R.id.buttonSave_insertNotification);
        buttonCancel        = view.findViewById(R.id.buttonCancel__insertNotification);
        progressBar         = view.findViewById(R.id.progressBar_insertNotification);

        // TODO 3) Apply filters for inputs, might be necessary so that user cant pick old date value, or/and set childName.
        // 3) Set up dialog view and inputs filters
        // 3.1) Get the values from the activity.
        childId = this.getArguments().getString("childId");
        childName = this.getArguments().getString("childName");
        Log.d(TAG, "ChildID: "+ childId+" ChildName: "+childName);

        // 3.2) Show the child's names
        textViewChildName.setText(childName);

        // 4) OnClick Listeners for the buttons, and Date/Time textViews.
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel button clicked");
                getDialog().dismiss();
            }
        });
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Date textView clicked");
                // 4.1) Implement Date View functionality
                // 4.1.1) Create calender object for day, month year. Also it makes sure it opens today's date.
                Calendar cal = Calendar.getInstance();
                mYear = cal.get(Calendar.YEAR);
                mMonth = cal.get(Calendar.MONTH);
                mDay = cal.get(Calendar.DAY_OF_MONTH);

                // 4.1.2) Create DatePickerDialog object
                DatePickerDialog dateDialog = new DatePickerDialog(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, mYear, mMonth, mDay);

                // 4.1.3) Make Background transparent
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // 4.1.4) Show the Date Dialog.
                dateDialog.show();

                // Step 5 for initializing our mDateSetListener, below.
            }
        });
        textViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Time textView clicked");
                // 4.2) Implement Time View functionality
                // 4.2.1) Calender object for time
                Calendar cal = Calendar.getInstance();
                mHour = cal.get(Calendar.HOUR_OF_DAY);
                mMinute = cal.get(Calendar.MINUTE);

                // 4.2.2) Create and Initialize TimePickerDialog
                TimePickerDialog timeDialog = new TimePickerDialog(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeSetListener, mHour, mMinute, false);

                // 4.2.3) Make Background transparent
                timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // 4.2.4) Show the Date Dialog.
                timeDialog.show();

                // Step 5 for initializing our mTimeSetListener, below.
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Save button clicked");
                // 4.3 Implement save button functionality
                // 4.3.1) Get user inputs and check acceptability
                notificationTitle = editTextTitle.getText().toString().trim();
                notificationDescription = editTextDescription.getText().toString().trim();

                if(notificationTitle.isEmpty()){
                    editTextTitle.setError("Please specify a title");
                    editTextTitle.requestFocus();
                } else {
                    // 4.3.2) Show progress bar.
                    progressBar.setVisibility(View.VISIBLE);
                    buttonCancel.setVisibility(View.INVISIBLE);
                    buttonSave.setVisibility(View.INVISIBLE);

                    // 4.3.3) Save notification with attributes.
                    // 4.3.2) Get the child's parents ID and employee ID.
                    // 4.3.2.1) Document Reference
                    DocumentReference childRef = FirebaseFirestore.getInstance().collection("Children").document(childId);
                    childRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d(TAG, "Getting child information");
                            // Translate the data into our custom object class
                            Child child = documentSnapshot.toObject(Child.class);

                            // Get child data
                            String employeeId = child.getEmployeeId();
                            String parentId = child.getParentId();

                            // Save the date and time
                            Date date = new Date(notificationDay, notificationMonth, notificationYear);

                            Time time = new Time(notificationHour, notificationMinute);

                            // Save the data to the notification. Create a new Notification using our defined custom class.
                            Notification notification = new Notification(parentId, employeeId, childId, childName, notificationTitle, notificationDescription, date, time);

                            // Save notifications in firebase save childID, parent ID, employee ID, title, description , time and date
                            FirebaseFirestore.getInstance().collection("Notifications").document().set(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Saving notification to database");
                                    getDialog().dismiss();
                                }
                            });

                            // 4.4) Implement alarm Manager and notifications.
                            // 4.4.1) Create a calender instance
                            Calendar c = Calendar.getInstance();

                            // 4.4.2) Set our requested time
                            c.set(Calendar.DAY_OF_MONTH, notificationDay);
                            c.set(Calendar.MONTH, notificationMonth);
                            c.set(Calendar.YEAR, notificationYear );
                            c.set(Calendar.HOUR_OF_DAY, notificationHour);
                            c.set(Calendar.MINUTE, notificationMinute);
                            c.set(Calendar.SECOND, 0);

                            // Step 6 for initializing our alarm
                            startAlarm(c);

                        }
                    });
                }
            }
        });


        // 5) Initialize our Set Listeners
        // 5.1) Initializing mDateSetListener.
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                Log.d(TAG, "Before Formatting, onDateSet: dd/mm/yyyy " + "day: " + day +" month: "+ month +" year: "+year);
                // Formatting needed: January is 0 and December is 11.
                int formattedMonth = month + 1;
                Log.d(TAG, "After Formatting, onDateSet: dd/mm/yyyy " + "day: " + day +" month: "+ formattedMonth +" year: "+year);

                // 5.1.1) Set our date to the text view
                String date = day + "/" + formattedMonth + "/" + year;
                textViewDate.setText(date);

                // 5.1.2) Store values globally to be used for database.
                notificationDay = day;
                notificationMonth = month;
                notificationYear = year;
            }
        };

        // 5.2) Initializing mTimeSetListener.
        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Log.d(TAG, "Before Formatting, onTimeSet: Hour : Minute " + "Hour: " + hourOfDay + " Minute: "+ minute);

                // Formatting needed: Cause we wanna show pm/am but we only get 24 hour time.
                int formattedHour;
                String phase;
                String extraZero = "";
                if(hourOfDay == 0) {
                    formattedHour = 12;
                    phase = "AM";
                } else if(hourOfDay > 12){
                    formattedHour = hourOfDay-12;
                    phase = "PM";
                } else if(hourOfDay == 12){
                    formattedHour = hourOfDay;
                    phase = "PM";
                } else {
                    formattedHour = hourOfDay;
                    phase = "AM";
                }
                if(minute < 10){
                    extraZero = "0";
                }

                Log.d(TAG, "After Formatting, onTimeSet: Hour : Minute " + "Hour: " + formattedHour + " Minute: "+ minute);

                // 5.2.1) Set our time to the text view
                String time = formattedHour +":"+extraZero+ minute + " " + phase;
                textViewTime.setText(time);

                // 5.2.2) Store values globally to be used for database.
                notificationHour = hourOfDay;
                notificationMinute = minute;
            }
        };

        return view;
    }

    // 6) Initializing our alarm
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c){
        Log.d(TAG, "Initializing alarm");
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ALARM_SERVICE);
        Intent intent =  new Intent(getActivity(), AlertReceiver.class);
        intent.putExtra("notificationTitle",notificationTitle );
        intent.putExtra("notificationDescription", notificationDescription);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);

        // Initialize our alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),pendingIntent);
    }
}