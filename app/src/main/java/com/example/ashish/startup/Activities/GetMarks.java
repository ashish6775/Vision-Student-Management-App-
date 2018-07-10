package com.example.ashish.startup.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.ashish.startup.Adapters.AttendanceListAdapter;
import com.example.ashish.startup.Adapters.MarksInputListAdapter;
import com.example.ashish.startup.Models.Attendance;
import com.example.ashish.startup.Models.Marks;
import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetMarks extends AppCompatActivity {

    private RecyclerView mMainList;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private List<Marks> marksInputList;
    private MarksInputListAdapter marksInputListAdapter;
    private Button submitMarks, viewDetailedMarks, updateMarks;
    private ProgressBar progressBar;
    ProgressDialog dialog;
    String Institute,class_id,marksID,email_red;
    int i;
    Map<String,Object> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_marks);
        mAuth = FirebaseAuth.getInstance();

        marksInputList = new ArrayList<>();
        marksInputListAdapter = new MarksInputListAdapter(this,marksInputList);

        if (getIntent().hasExtra("marksID") && getIntent().hasExtra("institute") && getIntent().hasExtra("class_id")){
            Institute = getIntent().getStringExtra("institute");
            marksID = getIntent().getStringExtra("marksID");
            class_id = getIntent().getStringExtra("class_id");

            Toolbar toolbar = findViewById(R.id.my_toolbar1);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("Submit Marks");
            }

            submitMarks = findViewById(R.id.submit_marks);
            viewDetailedMarks = findViewById(R.id.view_detailed_marks);
            updateMarks = findViewById(R.id.update_marks);
            progressBar = findViewById(R.id.progressBar7);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setScaleY(2f);
            dialog = new ProgressDialog(this);


            mMainList = findViewById(R.id.get_marks_list);
            mMainList.setHasFixedSize(true);
            mMainList.setLayoutManager(new LinearLayoutManager(this));
            mMainList.setAdapter(marksInputListAdapter);

            mFirestore = FirebaseFirestore.getInstance();

            final FirebaseUser user = mAuth.getCurrentUser();
            String email = user.getEmail();
            email_red = email.substring(0, email.length() - 10);

            mFirestore.collection("Users").whereEqualTo("Institute_Admin", Institute +"_No").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().collection("Subjects").document(class_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null && document.exists()) {
                                            document.getReference().getParent().getParent().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        progressBar.setVisibility(View.GONE);
                                                        DocumentSnapshot doc = task.getResult();
                                                        Marks marks = doc.toObject(Marks.class);
                                                        marksInputList.add(marks);
                                                        Collections.sort(marksInputList, Marks.BY_NAME_ALPHABETICAL);
                                                        marksInputListAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }

        submitMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data = new HashMap<>();
                for (i =0;i<marksInputList.size();i++) {
                    final String a ;
                    a = marksInputList.get(i).inputMarks;
                    data.put(marksInputList.get(i).getUsername(), a);
                }
                dialog.setMessage("Submitting Marks");
                dialog.show();

                mFirestore.collection("Users").document(email_red).collection("Subjects").document(class_id).collection("Marks").document(marksID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            document.getReference().update(data);
                        }
                        else{
                            Log.e("error", task.getException().getLocalizedMessage() + "" );
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("er", e.getLocalizedMessage() +" " );
                    }
                });

                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();
                        KToast.successToast(GetMarks.this,"Marks added successfully !", Gravity.BOTTOM,KToast.LENGTH_AUTO);
                    }
                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 3000);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}