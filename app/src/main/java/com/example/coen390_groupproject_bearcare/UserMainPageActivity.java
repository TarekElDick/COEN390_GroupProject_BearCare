package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.ChildAdapter;
import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.NotificationAdapter;
import com.example.coen390_groupproject_bearcare.Model.Attendance;
import com.example.coen390_groupproject_bearcare.Model.Child;
import com.example.coen390_groupproject_bearcare.Model.Date;
import com.example.coen390_groupproject_bearcare.Model.Notification;
import com.example.coen390_groupproject_bearcare.Model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static com.example.coen390_groupproject_bearcare.R.string.logging_out;
import static com.example.coen390_groupproject_bearcare.R.string.only_employees_temp;

public class UserMainPageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore fStore;
    private CollectionReference childrenRef;
    private NotificationAdapter notificationAdapter;
    private ChildAdapter childAdapter;
    private TextView displayName, questionnaireLastReceived, questionnaireTimestamp, corner;
    private boolean isEmployee;
    private String userId, userName;
    String TAG = "debug_dashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);

        // get the intent
        isEmployee = getIntent().getBooleanExtra("isEmployeeD",false);
        Log.d(TAG, "Making sure if employee in onCreate " + isEmployee);

        // Initializing Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        // Initializing the user
        user = mAuth.getCurrentUser();

        // Initializing firestore
        fStore = FirebaseFirestore.getInstance();

        // Initializing the reference to the children database
        childrenRef = fStore.collection("Children");

        // get the user ID of the user currently logged in.
        userId = user.getUid();
        userName = user.getDisplayName();

        // SetUpUI function
        setUpUI();


        // end of onCreate()
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "adapter is starting");
        childAdapter.startListening();
        notificationAdapter.startListening();

        if (user != null){
            // user is signed in
            Log.d(TAG, "User is signed in");
            displayName.setText(user.getDisplayName());

        } else {
            Log.d(TAG, "User is signed out");
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        // end of on start
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "adapter is stopping");
        childAdapter.stopListening();
        notificationAdapter.stopListening();
    }

    public void setUpUI() {

        // Connections
        corner = findViewById(R.id.textViewParentsCorner_dashboard);
        displayName = findViewById(R.id.textViewUserName_dashboard);
        TextView accessChildDirectory = findViewById(R.id.textViewAccessChildDirectory_dashboard);
        Button buttonFillQuestionnaire = findViewById(R.id.buttonFillDailyQuestionnaire_dashboard);

        // initially make everything invisible
        accessChildDirectory.setVisibility(View.INVISIBLE);
        buttonFillQuestionnaire.setVisibility(View.INVISIBLE);
        // onClickListeners
        accessChildDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "User accessing child directory");
                startActivity(new Intent(getApplicationContext(),ChildDirectoryActivity.class ));
            }
        });

        buttonFillQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO fill daily questionnaire activity , we can always get the user id we don't have to pass anything with the intent.
                Intent intent = new Intent(getApplicationContext(), QuestionnaireActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });

        runRecyclerView();
        // The following code is to double check if the user is an employee or not.
        String userId = user.getUid();
        // fix fStore reference
        DocumentReference docRef = fStore.collection("Users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                isEmployee = user.isEmployee();
                runRecyclerView();
                // RecyclerView for children setup

                Log.d(TAG, "User id is: " + userId);

                if(isEmployee){
                    String cornerText = getString(R.string.employee_corner);
                    corner.setText(cornerText);
                    accessChildDirectory.setVisibility(View.VISIBLE);
                    buttonFillQuestionnaire.setVisibility(View.INVISIBLE);
                }else{
                    String cornerText = getString(R.string.parent_corner);
                    corner.setText(cornerText);
                    accessChildDirectory.setVisibility(View.INVISIBLE);
                    buttonFillQuestionnaire.setVisibility(View.VISIBLE);

                    // TODO disable sensor icon, on the item probably within adapter?
                }
            }
        });

        //end of setUpUI function
    }

    public void runRecyclerView() {

        Log.d(TAG, "Recycler View is initializing");
        Log.d(TAG, "Making sure if employee in runRecyclerView " + isEmployee);

        Query notificationQuery;
        Query childQuery;
        if(isEmployee) {
            Log.d(TAG, "Query for employee is starting");
            // Query if user is employee
            childQuery = childrenRef.whereEqualTo("employeeId", userId)
                    .orderBy("lastName", Query.Direction.ASCENDING);

            notificationQuery = fStore.collection("Notifications")
                    .whereEqualTo("employeeId", userId);

            // Recycler Options. To get out query into the adapter.
            FirestoreRecyclerOptions<Child> options1 = new FirestoreRecyclerOptions.Builder<Child>()
                    .setQuery(childQuery, Child.class)
                    .build();

            FirestoreRecyclerOptions<Notification> options2 = new FirestoreRecyclerOptions.Builder<Notification>()
                    .setQuery(notificationQuery, Notification.class)
                    .build();

            childAdapter = new ChildAdapter(options1);
            notificationAdapter = new NotificationAdapter(options2);
        } else {
            Log.d(TAG, "Query for parent is starting");
            // Query if user is parent
            childQuery = childrenRef.whereEqualTo("parentId", userId)
                    .orderBy("lastName", Query.Direction.ASCENDING);
            notificationQuery = fStore.collection("Notifications")
                    .whereEqualTo("parentId", userId);

            // Recycler Options. To get out query into the adapter.
            FirestoreRecyclerOptions<Child> options1 = new FirestoreRecyclerOptions.Builder<Child>()
                    .setQuery(childQuery, Child.class)
                    .build();

            FirestoreRecyclerOptions<Notification> options2 = new FirestoreRecyclerOptions.Builder<Notification>()
                    .setQuery(notificationQuery, Notification.class)
                    .build();

            childAdapter = new ChildAdapter(options1);
            notificationAdapter = new NotificationAdapter(options2);
        }
        onStart();

        // Connecting our class object of recycler view to the layout recycler view
        RecyclerView firestoreChildrenRecyclerView = findViewById(R.id.recyclerViewChildren_dashboard);
        RecyclerView firestoreNotificationRecyclerView = findViewById(R.id.recyclerViewNotification_dashboard);


        // Connect our class object recycler view to the adapter
        firestoreChildrenRecyclerView.setHasFixedSize(true);
        firestoreChildrenRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        firestoreChildrenRecyclerView.setAdapter(childAdapter);

        firestoreNotificationRecyclerView.setHasFixedSize(true);
        firestoreNotificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        firestoreNotificationRecyclerView.setAdapter(notificationAdapter);

        // ItemTouchHelper to implement delete functionality, only employee's can delete and take its attendance
        if(isEmployee) {
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                            // Here is where we implement swipe to delete
                            Log.d(TAG, "Child Item is being swiped");
                            new AlertDialog.Builder(viewHolder.itemView.getContext())
                                    .setMessage(R.string.are_you_sure)
                                    .setPositiveButton(R.string.yes_string, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // User wants to delete the item
                                            Log.d(TAG, "Child item is deleted");
                                            // Refresh the adapter to prevent the item staying on the UI.
                                            childAdapter.deleteItem(viewHolder.getAdapterPosition());
                                            runRecyclerView();
                                        }
                                    })
                                    .setNegativeButton(R.string.no_string, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // User canceled the delete item
                                            Log.d(TAG, "Child item is not deleted");
                                            // Refresh the adapter to prevent the item from UI to update.
                                            childAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                        }
                                    })
                                    .create()
                                    .show();
                }

                // This method allows us to add a onSwipe drawing things
                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftBackgroundColor(ContextCompat.getColor(UserMainPageActivity.this, R.color.red))
                            .addSwipeLeftActionIcon(R.drawable.ic_delete)
                            .create()
                            .decorate();


                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }).attachToRecyclerView(firestoreChildrenRecyclerView);


        }

        //On click for the item itself
        childAdapter.setOnItemClickListener(new ChildAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                //Translate data into our class custom object
                Child child = documentSnapshot.toObject(Child.class);

                // Get the child's info to send.
                String childId = documentSnapshot.getId();

                //Get child info
                String firstName = child.getFirstName();
                String lastName = child.getLastName();
                String parentId = child.getParentId();
                String name = firstName + " " + lastName;
                String birthday = child.getBirthday().toString();

                Intent intent = new Intent(getApplicationContext(), ChildProfileActivity.class);
                intent.putExtra("childId", childId);
                intent.putExtra("childName", name);
                intent.putExtra("parentId", parentId);
                intent.putExtra("birthday", birthday);
                startActivity(intent);
            }

            // OnClick for the take temperature button
            @Override
            public void onTakeTempButtonClick(DocumentSnapshot documentSnapshot, int position) {
                Log.d(TAG, "Take Temp Button Clicked");
                if(isEmployee) {
                    // Code for when take temp button is clicked
                    // Get the document ID.
                    String childId = documentSnapshot.getId();
                    //get child name
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");
                    String childName = firstName + " " + lastName;
                    // Go to takeTempActivity
                    Intent intent = new Intent(getApplicationContext(), TemperatureActivity.class);
                    intent.putExtra("childId", childId);
                    intent.putExtra("childName", childName);
                    startActivity(intent);
                } else{
                    Log.d(TAG, "Not Employee");
                    Toast.makeText(UserMainPageActivity.this, only_employees_temp, Toast.LENGTH_LONG).show();
                }
            }

            // Onclick for the temp history
            @Override
            public void onTempHistoryButtonClick(DocumentSnapshot documentSnapshot, int position) {
                Log.d(TAG, "Temp History Button Clicked");
                // Code for when take temp button is clicked
                // Get the document ID.
                String childId = documentSnapshot.getId();
                //get child name
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                String childName = firstName + " " + lastName;

                Intent intent = new Intent(getApplicationContext(), TemperatureHistoryActivity.class);
                intent.putExtra("childId", childId);
                intent.putExtra("childName", childName);
                Log.d(TAG, "TempHistory clicked");
                startActivity(intent);
            }

            @Override
            public void medicalRecords(DocumentSnapshot documentSnapshot, int position) {
                Log.d(TAG, "Medical Record Button Clicked");
                // Get the document ID.
                String childId = documentSnapshot.getId();
                //get child name.
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                String childName = firstName + " " + lastName;

                Intent intent = new Intent(getApplicationContext(), MedicalRecordsActivity.class);
                intent.putExtra("childId", childId);
                intent.putExtra("childName", childName);
                Log.d(TAG, "Medical Records clicked");
                startActivity(intent);
            }

            @Override
            public void onAttendanceClick(DocumentSnapshot documentSnapshot, int position) {
                Log.d(TAG, "Attendance Clicked");
                if(isEmployee) {
                // Check if the attendance exists for the day.
                int attendanceDay, attendanceMonth, attendanceYear;
                Calendar cal = Calendar.getInstance();
                // 4.4.2) Set our requested time
                attendanceYear = cal.get(Calendar.YEAR);
                attendanceMonth = cal.get(Calendar.MONTH);
                attendanceDay = cal.get(Calendar.DAY_OF_MONTH);

                Date date = new Date(attendanceDay, attendanceMonth, attendanceYear);

                childrenRef.document(documentSnapshot.getId()).collection("Attendance").whereEqualTo("date", date).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Attendance attendanceCheck =  document.toObject(Attendance.class);
                                boolean check = attendanceCheck.isCurrentAttendance();
                                Log.d(TAG, "what is check " + check);
                                if (!check){
                                    //set true
                                    childrenRef.document(documentSnapshot.getId()).collection("Attendance").document(document.getId()).update("currentAttendance", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated! to true");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);
                                        }
                                    });
                                } else {
                                    // set false
                                    childrenRef.document(documentSnapshot.getId()).collection("Attendance").document(document.getId()).update("currentAttendance", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated! to false");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.isEmpty()) {
                            Attendance attendance = new Attendance(date, true);
                            childrenRef.document(documentSnapshot.getId()).collection("Attendance").add(attendance)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Error adding document", e);
                                }
                            });
                        }
                        // Update child adapter
                        childAdapter.notifyItemChanged(position);
                    }
                });
            } else{
                Log.d(TAG, "Not Employee");
                Toast.makeText(UserMainPageActivity.this, R.string.only_employees_attendance, Toast.LENGTH_LONG).show();
            }
            }
        });

        // Notifications !
        // ItemTouchHelper to implement delete functionality, only employee's can delete.
        if(isEmployee) {
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    // Here is where we implement swipe to delete
                    Log.d(TAG, "Notification Item is being swiped");

                    new AlertDialog.Builder(viewHolder.itemView.getContext())
                            .setMessage(R.string.notification_delete)
                            .setPositiveButton(R.string.yes_string, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // User wants to delete the item
                                    Log.d(TAG, "Notification item is deleted");
                                    notificationAdapter.deleteItem(viewHolder.getAdapterPosition());
                                    runRecyclerView();
                                }
                            })
                            .setNegativeButton(R.string.no_string, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // User canceled the delete item
                                    Log.d(TAG, "Notification item is not deleted");
                                    // Refresh the adapter to prevent the item from UI.
                                    notificationAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            })
                            .create()
                            .show();
                }
            }).attachToRecyclerView(firestoreNotificationRecyclerView);
        }

    }

    // Created our menu layout file in the resource directory (res/menu/menu_DASHBOARD), and we connected it to this activity.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    // Now we need to specify what happens once our menu items are clicked.
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuItem_logout:

                Log.d(TAG, "User is logging out");
                Toast.makeText(this, logging_out, Toast.LENGTH_SHORT).show();

                // We sign out the firebase user and they are sent to the login activity (MainActivity.java)
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class ));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // end of activity
}