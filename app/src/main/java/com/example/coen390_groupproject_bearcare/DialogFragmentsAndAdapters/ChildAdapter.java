package com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coen390_groupproject_bearcare.Model.Child;
import com.example.coen390_groupproject_bearcare.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ChildAdapter extends FirestoreRecyclerAdapter<Child, ChildAdapter.ChildHolder> {

    private OnItemClickListener listener;

    public ChildAdapter(@NonNull FirestoreRecyclerOptions<Child> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChildHolder holder, int position, @NonNull Child model) {

        // What information do we want to show
        holder.child_name.setText(model.getFullName());

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
        private final TextView child_name;
        private final Button takeTemp;

        // Child View Holder Constructor, where we connect layout objects to view objects
        public ChildHolder(@NonNull View itemView) {
            super(itemView);

            child_name = itemView.findViewById(R.id.textViewChildName_recyclerItem);
            takeTemp = itemView.findViewById(R.id.buttonTakeTemp_recyclerItem);

            // OnclickListener for ChildHolder
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    // can implement intent here but for re-usability we will put it in the class it called from.
                    if(position != RecyclerView.NO_POSITION && listener!=null){
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
                    if(position != RecyclerView.NO_POSITION && listener!=null){
                        listener.onTakeTempButtonClick(getSnapshots().getSnapshot(position), position);
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
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


// end of Child Adapter
}
