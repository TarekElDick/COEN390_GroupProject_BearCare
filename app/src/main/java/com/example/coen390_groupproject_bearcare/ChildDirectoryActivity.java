package com.example.coen390_groupproject_bearcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.coen390_groupproject_bearcare.Model.Child;
import com.example.coen390_groupproject_bearcare.Model.Date;

import java.util.ArrayList;
import java.util.List;

public class ChildDirectoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    List<Child> children = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_directory);

        // TODO replace this code where we fill the list of children
        // these should actually come from our DB of children
        children.add(new Child(0,0,0, "Ella Fitzgerald", new Date(1, 1, 2015)));
        children.add(new Child(0,0,0, "Nina Simone", new Date(1, 1, 2016)));
        children.add(new Child(0,0,0, "Charles Mingus", new Date(1, 1, 2015)));
        children.add(new Child(0,0,0, "Ella Fitzgerald", new Date(1, 1, 2015)));
        children.add(new Child(0,0,0, "Nina Simone", new Date(1, 1, 2016)));
        children.add(new Child(0,0,0, "Charles Mingus", new Date(1, 1, 2015)));
        children.add(new Child(0,0,0, "Ella Fitzgerald", new Date(1, 1, 2015)));
        children.add(new Child(0,0,0, "Nina Simone", new Date(1, 1, 2016)));
        children.add(new Child(0,0,0, "Charles Mingus", new Date(1, 1, 2015)));
        children.add(new Child(0,0,0, "Ella Fitzgerald", new Date(1, 1, 2015)));
        children.add(new Child(0,0,0, "Nina Simone", new Date(1, 1, 2016)));
        children.add(new Child(0,0,0, "Charles Mingus", new Date(1, 1, 2015)));
        children.add(new Child(0,0,0, "Ella Fitzgerald", new Date(1, 1, 2015)));
        children.add(new Child(0,0,0, "Nina Simone", new Date(1, 1, 2016)));
        children.add(new Child(0,0,0, "Charles Mingus", new Date(1, 1, 2015)));

        // all recycler view setup
        recyclerView = findViewById(R.id. recyclerView_childList);
        recyclerView.setHasFixedSize(true);     // we don't expect the app to change the size of the cards
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChildRecyclerViewAdapter(children, this);
        recyclerView.setAdapter(adapter);
    }
}