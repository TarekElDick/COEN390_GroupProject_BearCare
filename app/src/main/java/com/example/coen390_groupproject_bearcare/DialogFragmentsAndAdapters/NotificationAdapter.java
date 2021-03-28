package com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coen390_groupproject_bearcare.Model.Notification;
import com.example.coen390_groupproject_bearcare.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class NotificationAdapter extends FirestoreRecyclerAdapter<Notification, NotificationAdapter.NotificationHolder> {


    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<Notification> options) { super(options); }

    @Override
    protected void onBindViewHolder(@NonNull NotificationHolder holder, int position, @NonNull Notification model) {
        // Info we want to show at every position.
        holder.childName.setText(model.getChildName());
        holder.notificationTitle.setText(model.getNotificationTitle());
        holder.date.setText(model.getDate().toString());
        holder.time.setText(model.getTime().toString());

    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Which layout it has to inflate
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_notification_item, parent, false);
        return new NotificationHolder(view);
    }

    // View Holder
    class NotificationHolder extends RecyclerView.ViewHolder{

        // Our view objects
        private final TextView childName, notificationTitle, date, time;

        // Child View Holder Constructor, where we connect layout objects to view objects
        public NotificationHolder(@NonNull View itemView) {
            super(itemView);

            childName = itemView.findViewById(R.id.textViewChildName_recyclerNotificationItem);
            notificationTitle = itemView.findViewById(R.id.textViewTitle_recyclerNotificationItem);
            date = itemView.findViewById(R.id.textViewDate_recyclerNotificationItem);
            time = itemView.findViewById(R.id.textViewTime_recyclerNotificationItem);

        }
    }
}
