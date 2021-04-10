package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.ChildAdapter;
import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.FeverAdapter;
import com.example.coen390_groupproject_bearcare.Model.Child;
import com.example.coen390_groupproject_bearcare.Model.Temperature;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.SimpleDateFormat;

public class TemperatureHistoryActivity extends AppCompatActivity {

    String TAG = "TemperatureHistoryActivity";
    GraphView graphView;
    LineGraphSeries series;
    LineGraphSeries series2;
    //PointsGraphSeries series4;

    private FeverAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_history);
        Intent intent =  getIntent();
        String childName = intent.getStringExtra("childName");
        String childId = intent.getStringExtra("childId");
        Log.d("tempHistory", "onCreate: ");
        graphView = (GraphView) findViewById((R.id.tempHistoryGraph));
        graphView.setTitle(childName + getString(R.string.temperature_history_of_child));
        graphView.getViewport().setScrollable(true); // enables horizontal scrolling
        graphView.getViewport().setScrollableY(true); // enables vertical scrolling
        graphView.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graphView.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        graphView.getViewport().setMinX(1);
        graphView.getViewport().setMaxX(31);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(45);
        series = new LineGraphSeries();
        series.setColor(Color.BLUE);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);
        graphView.addSeries(series);

        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 37),
                new DataPoint(30, 37)
        });
        series3.setColor(Color.RED);
        graphView.addSeries(series3);

        series2 = new LineGraphSeries();
        series2.setColor(Color.GREEN);
        graphView.addSeries(series2);

//        series4 = new PointsGraphSeries();
//        series4.setColor(Color.RED);

        // custom label formatter to show temperature "°C"
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueY) {
                if (isValueY) {
                    // show normal x values
                    return super.formatLabel(value, isValueY);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueY) + " °C";
                }
            }
        });
        // RecyclerView Set Up
        Log.d(TAG, "Recycler View is initializing and Query is starting");

        // Queries
        Query query = db.collection("Children").document(childId).collection("Temperatures").whereGreaterThanOrEqualTo("temp",37);

        // Recycler Options. To get our query into the adapter.
        FirestoreRecyclerOptions<Temperature> options = new FirestoreRecyclerOptions.Builder<Temperature>()
                .setQuery(query, Temperature.class)
                .build();

        Log.d(TAG, "Adapter is initializing with Fever values");
        adapter = new FeverAdapter(options);

        // Connecting our class object of recycler view to the layout recycler view
        // Firestore Recycler List
        RecyclerView firestoreFeverRecyclerView = findViewById(R.id.recyclerViewFeverList_temperatureHistory);

        // Connect out class object recycler view to the adapter
        firestoreFeverRecyclerView.setHasFixedSize(true);
        firestoreFeverRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        firestoreFeverRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();
        Intent intent =  getIntent();
        String childId = intent.getStringExtra("childId");

        Log.d("tempHistory", "onStart ");
        Log.d("tempHistory", "1");

        Log.d(TAG, "childId "+childId);

        db.collection("Children").document(childId).collection("Temperatures")
                .orderBy("day", Query.Direction.valueOf("ASCENDING"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           if (task.isSuccessful()) {
                               Log.d("tempHistory", "1.1");
                               DataPoint[] tempGraph = new DataPoint[task.getResult().size()];
                               Log.d(TAG, "size of collection: "+task.getResult().size());
                               DataPoint[] criticalLine = new DataPoint[task.getResult().size()];
                               int index = 0;
                               for (QueryDocumentSnapshot document : task.getResult()) {
                                   Log.d(TAG, document.getId() + " => " + document.getData());
                                   Temperature pointValue = document.toObject(Temperature.class);
                                   Log.d("tempHistory", "iteration #" + index);
                                   pointValue.print();
                                   //Log.d("tempHistory", "value is: " + pointValue);
                                   tempGraph[index]= new DataPoint(pointValue.getDay(), pointValue.getTemp());
                                   criticalLine[index]= new DataPoint(pointValue.getDay(), 37);
                                   Log.d("tempHistory", "array data: " + tempGraph[index]);
                                   index++;
                               }
                               series.resetData(tempGraph);
                               series2.resetData(criticalLine);
                           } else {
                               Log.w(TAG, "Error getting documents.", task.getException());
                           }
                       }
                });

        //code to highlight fever readings, not working yet

//        db.collection("Children").document(childId).collection("Temperatures")
//                .whereGreaterThanOrEqualTo("temp",37)
//                .orderBy("day", Query.Direction.valueOf("ASCENDING"))
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Log.d("tempHistory", "3.1");
//                            //DataPoint[] tempGraph = new DataPoint[task.getResult().size()];
//                            Log.d(TAG, "size of collection: "+task.getResult().size());
//                            DataPoint[] criticalPoints = new DataPoint[task.getResult().size()];
//                            int index = 0;
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                Temperature pointValue = document.toObject(Temperature.class);
//                                //Log.d("tempHistory", "iteration #" + index);
//                                //pointValue.print();
//                                //tempGraph[index]= new DataPoint(pointValue.getDay(), pointValue.getTemp());
//                                criticalPoints[index]= new DataPoint(pointValue.getDay(), pointValue.getTemp());
//                                //Log.d("tempHistory", "array data: " + tempGraph[index]);
//                                index++;
//                            }
//                            series4.resetData(criticalPoints);
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });


        Log.d("tempHistory", "2");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "adapter is stopping");
        adapter.stopListening();
    }
}