package com.example.coen390_groupproject_bearcare;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coen390_groupproject_bearcare.Model.Child;

import java.util.List;

public class ChildRecyclerViewAdapter extends RecyclerView.Adapter<ChildRecyclerViewAdapter.MyViewHolder> {

    List<Child> children;
    Context context;

    public ChildRecyclerViewAdapter(List<Child> children, Context context) {
        this.children = children;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_child_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // here we can manipulate the items inside recycler_child_item

        String name = children.get(position).getFullName();
        Log.i("Directory", children.get(position).getFullName());
        holder.childName.setText(name);
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView childName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            childName = itemView.findViewById(R.id.textView_recyclerChildName);
        }
    }
}
