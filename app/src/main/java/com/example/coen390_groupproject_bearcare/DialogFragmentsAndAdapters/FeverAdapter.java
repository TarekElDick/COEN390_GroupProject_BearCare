package com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coen390_groupproject_bearcare.Model.Child;
import com.example.coen390_groupproject_bearcare.Model.Temperature;
import com.example.coen390_groupproject_bearcare.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class FeverAdapter extends FirestoreRecyclerAdapter<Temperature, FeverAdapter.FeverHolder> {


    public FeverAdapter(@NonNull FirestoreRecyclerOptions<Temperature> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FeverHolder holder, int position, @NonNull Temperature model) {
        // What information do we want to show
        holder.fever_temp.setText(Double.toString(model.getTemp())+"°C");
        holder.fever_date.setText(model.getDate());
    }

    @NonNull
    @Override
    public FeverHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Which layout it has to inflate
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_fever_item, parent, false);

        return new FeverHolder(view);
    }

    // View Holder
    class FeverHolder extends RecyclerView.ViewHolder{

        // Our View Objects
        private final TextView fever_temp;
        private final TextView fever_date;

        public FeverHolder(@NonNull View itemView) {
            super(itemView);
            fever_temp = itemView.findViewById(R.id.textViewFeverDate_recyclerFeverItem);
            fever_date = itemView.findViewById(R.id.textViewFeverTemp_recyclerFeverItem);
        }

        // end of Child View Holder
    }
// end of Child Adapter
}
