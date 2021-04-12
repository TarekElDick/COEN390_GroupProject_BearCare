package com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coen390_groupproject_bearcare.Model.Attendance;
import com.example.coen390_groupproject_bearcare.Model.Child;
import com.example.coen390_groupproject_bearcare.Model.Date;
import com.example.coen390_groupproject_bearcare.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ChildAdapter extends FirestoreRecyclerAdapter<Child, ChildAdapter.ChildHolder> {

    private OnItemClickListener listener;

    public ChildAdapter(@NonNull FirestoreRecyclerOptions<Child> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChildHolder holder, int position, @NonNull Child model) {

        // What information do we want to show
        holder.child_name.setText(model.getFullName());

        // Check if the attendance exists for the day.
        int attendanceDay, attendanceMonth, attendanceYear;
        Calendar cal = Calendar.getInstance();
        // 4.4.2) Set our requested time
        attendanceYear = cal.get(Calendar.YEAR);
        attendanceMonth = cal.get(Calendar.MONTH);
        attendanceDay = cal.get(Calendar.DAY_OF_MONTH);

        Date date = new Date(attendanceDay, attendanceMonth, attendanceYear);
        Attendance attendance = new Attendance(date, false);

        String TAG = "debug_childAdapter";

        FirebaseFirestore.getInstance()
                .collection("Children").document(getSnapshots().getSnapshot(position).getId())
                .collection("Attendance").whereEqualTo("date", date).limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                           Log.d(TAG, "Task Successful");
                           for (QueryDocumentSnapshot document : task.getResult()) {
                                   Log.d(TAG, document.getId() + " => " + document.getData());
                                   Attendance attendanceCheck = document.toObject(Attendance.class);
                                   boolean check = attendanceCheck.isCurrentAttendance();
                                   Log.d(TAG, model.getFullName() + " Attendance " + check);

                                   if (!check) {
                                       // set absent
                                       holder.attendance.setTextColor(Color.RED);
                                       holder.attendance.setText(R.string.absent_attendance);
                                       Log.d(TAG, "Set to Absent");
                                   } else {
                                       // set present
                                       holder.attendance.setTextColor(Color.GREEN);
                                       holder.attendance.setText(R.string.present_attendance);
                                       Log.d(TAG, "Set to Present");
                                   }
                           }
                       } else {
                           Log.d(TAG, "Error while getting documents");
                       }
                    }
                }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "Document doesn't exist");
                            // set absent
                            holder.attendance.setTextColor(Color.RED);
                            holder.attendance.setText("Absent");
                            Log.d(TAG, "Set to Absent");
                        }
                    }
                });
    }

    @NonNull
    @Override
    public ChildHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Which layout it has to inflate
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_child_item, parent, false);

        return new ChildHolder(view);
    }

    // To delete a child item
    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }


    // View Holder
    class ChildHolder extends RecyclerView.ViewHolder{

        // Our View Objects
        private final TextView child_name, attendance;
        private final Button takeTemp, tempHistory, medicalRecord;

        // Child View Holder Constructor, where we connect layout objects to view objects
        public ChildHolder(@NonNull View itemView) {
            super(itemView);

            child_name = itemView.findViewById(R.id.textViewChildName_recyclerChildItem);
            takeTemp = itemView.findViewById(R.id.buttonDownloadFile);
            tempHistory = itemView.findViewById(R.id.buttonTempHistory_recyclerItem);
            attendance = itemView.findViewById(R.id.textViewAttendance_recyclerItem);
            medicalRecord = itemView.findViewById(R.id.buttonMedicalRecords_recyclerItem);

            // OnclickListener for ChildHolder, not the buttons !
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    // can implement intent here but for re-usability we will put it in the class its called from.
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            // OnClickListener for the button itself
            takeTemp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    // can implement intent here but for re-usability we will put it in the class it called from.
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onTakeTempButtonClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            tempHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    // can implement intent here but for re-usability we will put it in the class it called from.
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onTempHistoryButtonClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            medicalRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    // can implement intent here but for re-usability we will put it in the class it called from.
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.medicalRecords(getSnapshots().getSnapshot(position), position);
                    }
                }
                });


                attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    // can implement intent here but for re-usability we will put it in the class it called from.
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onAttendanceClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
            // end of constructor


        }
        // end of Child View Holder
    }

    // Interface function to deal with the onclick listener
    public interface OnItemClickListener{

        // If you want to send anything else from adapter to activity we have to change the arguments here.
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

        // Adding  method for our buttons

        // Take temp button
        void onTakeTempButtonClick(DocumentSnapshot documentSnapshot, int position);

        // Temp History button
        void onTempHistoryButtonClick(DocumentSnapshot documentSnapshot, int position);

        void medicalRecords(DocumentSnapshot documentSnapshot, int position);

        void onAttendanceClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


// end of Child Adapter
}
