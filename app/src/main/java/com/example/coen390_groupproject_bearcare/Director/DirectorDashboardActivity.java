package com.example.coen390_groupproject_bearcare.Director;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.ChildDirectoryActivity;
import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.ExportQuestRecordsDialog;
import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.ExportTempRecordsDialog;
import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.NotificationAdapter;
import com.example.coen390_groupproject_bearcare.MainActivity;
import com.example.coen390_groupproject_bearcare.Model.Notification;
import com.example.coen390_groupproject_bearcare.Model.Questionnaire;
import com.example.coen390_groupproject_bearcare.Model.Temperature;
import com.example.coen390_groupproject_bearcare.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.example.coen390_groupproject_bearcare.R.string.logging_out;

public class DirectorDashboardActivity extends AppCompatActivity implements ExportQuestRecordsDialog.OnParentListener, ExportTempRecordsDialog.OnChildListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    Random r = new Random();
    String TAG="debug_directoryDashboard";

    private NotificationAdapter notificationAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        notificationAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        notificationAdapter.stopListening();
    }

    @Override
    public void sendParentId(String parentId, String fullName) {
        Log.d(TAG, "sendParentId: acquired = "+parentId);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.d(TAG, "onPermissionGranted: Questionnaire Records");
                        createQuestionnaireRecordsPdfFile(Cmmon.getAppPath(DirectorDashboardActivity.this)+"questionnaire_export.pdf",parentId,fullName);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createQuestionnaireRecordsPdfFile(String path, String parentId, String fullName) {
        if(new File(path).exists())
            new File(path).delete();

            Log.d(TAG, "createQuestionnaireRecordsPdfFile: created");
            db.collection("Users").document(parentId).collection("Questionnaires")
                    .orderBy("day", Query.Direction.valueOf("DESCENDING"))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int i=0;
                                try{
                                    Document doc = new Document();

                                    //Save
                                    PdfWriter.getInstance(doc, new FileOutputStream(path));

                                    //Open to Write
                                    doc.open();

                                    //Settings
                                    doc.setPageSize(PageSize.A4);
                                    doc.addCreationDate();
                                    doc.addAuthor("BearCare");
                                    doc.addCreator(user.getDisplayName());
                                    //Font
                                    BaseColor colorAccent = new BaseColor(0,153,204,255);
                                    float fontSize = 20.0f;
                                    float valueFontSize = 26.0f;
                                    //Title Font
                                    //BaseFont ...
                                    Font titleFont = new Font(Font.FontFamily.valueOf("COURIER"), 8.0f,0);
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    if (i == 0){

                                        //cover page

                                    }
                                    //export lines to document
                                    addLineSpace(doc);
                                    //addNewItem(doc,"Questionnaire Records",Element.ALIGN_CENTER);
                                    Chunk chunk = new Chunk("Questionnaire Records");
                                    chunk.setUnderline(1,-1);
                                    Paragraph paragraph = new Paragraph(chunk);
                                    paragraph.setAlignment(Element.ALIGN_CENTER);
                                    doc.add(paragraph);
                                    Questionnaire temp = document.toObject(Questionnaire.class);
                                    addLineSpace(doc);
                                    addNewItemWithLeftandRight(doc,fullName,temp.getDate());
                                    addLineSeparator(doc);
                                    addNewItem(doc,"Does your child have one or multiple symptoms in the following list?",Element.ALIGN_LEFT);
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(0),temp.getZero());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(1),temp.getOne());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(2),temp.getTwo());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(3),temp.getThree());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(4),temp.getFour());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(5),temp.getFive());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(6),temp.getSix());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(7),temp.getSeven());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(8),temp.getEight());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(9),temp.getNine());
                                    addLineSeparator(doc);
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(10),temp.getTen());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(11),temp.getEleven());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(12),temp.getTwelve());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(13),temp.getThirteen());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(14),temp.getFourteen());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(15),temp.getFifteen());
                                    addNewItemWithLeftandRight(doc, Questionnaire.getQuestion(16),temp.getSixteen());
                                    addLineSeparator(doc);
                                    addNewItem(doc, "I, " + fullName + ", agree that the information provided is true and that any misinformation could lead to legal complications.",Element.ALIGN_JUSTIFIED);
                                    addLineSeparator(doc);
                                    if(i<task.getResult().size())
                                        doc.newPage();
                                }

                                doc.close();
                                    Toast.makeText(DirectorDashboardActivity.this, "Record Created",Toast.LENGTH_LONG).show();
                                printPDF(true);

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (DocumentException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });




    }

    private void addNewItemWithLeftandRight(Document doc, String textLeft, String textRight) throws DocumentException {
        Chunk chunkTextLeft = new Chunk(textLeft);
        Chunk chunkTextRight= new Chunk(textRight);
        if(textRight.equals("YES")) {
            chunkTextLeft.setBackground(new BaseColor(239,0,0, 68));
            chunkTextRight.setBackground(new BaseColor(239,0,0,68));
        }
        Paragraph p = new Paragraph(chunkTextLeft);
        p.add(new Chunk(new VerticalPositionMark()));
        p.add(chunkTextRight);
        doc.add(p);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void printPDF(boolean quest) {
        PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
        try{
            if (quest){
                PrintDocumentAdapter printDocumentAdapter = new PDFDocumentAdapter(DirectorDashboardActivity.this,Cmmon.getAppPath(DirectorDashboardActivity.this)+"questionnaire_export.pdf");
                printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());}
            else{
                PrintDocumentAdapter printDocumentAdapter = new PDFDocumentAdapter(DirectorDashboardActivity.this,Cmmon.getAppPath(DirectorDashboardActivity.this)+"Temperature_export.pdf");
                printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build()); }
        } catch (Exception ex){
            Log.e(TAG, "printPDF: Error: "+ex.getMessage());
        }
    }

    private void addLineSeparator(Document document) throws DocumentException {
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0,0,0,68));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph("\0"));
    }

    private void addNewItem(Document document, String text, int align) throws DocumentException {
        Chunk chunk = new Chunk(text);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_dashboard);

        // Initializing Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        // Initializing the user
        user = mAuth.getCurrentUser();

        Button exportTempButton = findViewById(R.id.buttonExportTempRecords);
        Button exportQuestButton = findViewById(R.id.buttonExportQuestRecords);



        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();




        exportTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportTempRecordsDialog dialog = new ExportTempRecordsDialog();
                dialog.show(getSupportFragmentManager(),"export temp record");
            }
        });

        exportQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportQuestRecordsDialog dialog = new ExportQuestRecordsDialog();
                dialog.show(getSupportFragmentManager(),"export temp record");
            }
        });



        // Tarek's Work: Implementing notification, childDirectory
        TextView accessChildDirectory = findViewById(R.id.textViewAccessChildDirectory_directorDashboard);
        // onClickListeners
        accessChildDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "User accessing child directory");
                startActivity(new Intent(getApplicationContext(), ChildDirectoryActivity.class ));
            }
        });

        runRecyclerView();

    }

    private void runRecyclerView() {
        Log.d(TAG, "Recycler View is initializing");

        // Query
        Query notificationQuery;
        notificationQuery = db.collection("Notifications");

        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(notificationQuery, Notification.class)
                .build();


        notificationAdapter = new NotificationAdapter(options);

        // Connecting our class object of recycler view to the layout recycler view
        RecyclerView firestoreNotificationRecyclerView = findViewById(R.id.recyclerViewNotifications_directorDashboard);

        firestoreNotificationRecyclerView.setHasFixedSize(true);
        firestoreNotificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        firestoreNotificationRecyclerView.setAdapter(notificationAdapter);

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
                startActivity(new Intent(getApplicationContext(), MainActivity.class ));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void sendChildId(String childId, String fullName) {
        Log.d(TAG, "sendChildId: acquired = "+childId);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.d(TAG, "onPermissionGranted: Temperature Records");
                        createTemperatureRecordsPdfFile(Cmmon.getAppPath(DirectorDashboardActivity.this)+"Temperature_export.pdf",childId,fullName);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createTemperatureRecordsPdfFile(String path, String childId, String fullName) {
        if(new File(path).exists())
            new File(path).delete();

        Log.d(TAG, "createTemperatureRecordsPdfFile: created");
        db.collection("Children").document(childId).collection("Temperatures")
                .orderBy("day", Query.Direction.valueOf("ASCENDING"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i=0;
                            try{
                                Document doc = new Document();

                                //Save
                                PdfWriter.getInstance(doc, new FileOutputStream(path));

                                //Open to Write
                                doc.open();
                                Log.d(TAG, "onComplete: doc2 open");

                                //Settings
                                doc.setPageSize(PageSize.A4);
                                doc.addCreationDate();
                                doc.addAuthor("BearCare");
                                doc.addCreator(user.getDisplayName());



                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    Temperature temp = new Temperature();
                                    temp = document.toObject(Temperature.class);

                                    if (i == 0) {

                                        //export lines to document
                                        addLineSpace(doc);
                                        //addNewItem(doc,"Questionnaire Records",Element.ALIGN_CENTER);
                                        Chunk chunk = new Chunk("Temperature Records");
                                        chunk.setUnderline(1, -1);
                                        Paragraph paragraph = new Paragraph(chunk);
                                        paragraph.setAlignment(Element.ALIGN_CENTER);
                                        doc.add(paragraph);
                                        addLineSpace(doc);
                                        addNewItem(doc, fullName, Element.ALIGN_CENTER);
                                        addLineSeparator(doc);
                                        addNewItemWithLeftandRight(doc, "Date", "Temperature");
                                        addLineSpace(doc);
                                    }

                                    //addNewItemWithLeftandRight(doc, temp.getDate(), String.valueOf(temp.getTemp())+"°C");
                                    Chunk chunkTextLeft = new Chunk(temp.getDate());
                                    Chunk chunkTextRight= new Chunk(temp.getTemp() +"°C");
                                    if(temp.getTemp()>=37) {
                                        chunkTextLeft.setBackground(new BaseColor(239,0,0, 68));
                                        chunkTextRight.setBackground(new BaseColor(239,0,0,68));
                                    }
                                    Paragraph p = new Paragraph(chunkTextLeft);
                                    p.add(new Chunk(new VerticalPositionMark()));
                                    p.add(chunkTextRight);
                                    doc.add(p);

                                    i++;

                                    if (i == task.getResult().size())
                                        addLineSeparator(doc);

                                }

                                doc.close();
                                Toast.makeText(DirectorDashboardActivity.this, "Record Created",Toast.LENGTH_LONG).show();
                                printPDF(false);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });



    }
}