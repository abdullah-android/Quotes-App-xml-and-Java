package com.example.quotology;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateQuotesActivity extends AppCompatActivity {

    EditText edtUpdateName, edtUpdateQuote;
    Button btnUpdateQuotes;
    androidx.appcompat.widget.Toolbar toolbar2;
    FirebaseFirestore fireStore;
    ArrayList<QuotesData> quotesDataArrayList;
    int updateNameMinLength = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_quotes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        fireStore = FirebaseFirestore.getInstance();

        edtUpdateName = findViewById(R.id.edtUpdateName);
        edtUpdateQuote = findViewById(R.id.edtUpdateQuote);
        btnUpdateQuotes = findViewById(R.id.btnUpdateQuotes);

        toolbar2 = findViewById(R.id.toolbar2);

        // set Title on Toolbar
        toolbar2.setTitle("Update Quote");
        // set the Back Icon in Toolbar
        toolbar2.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow_icon));
        // set the on Click to Back Icon in Toolbar
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            edtUpdateName.setText(bundle.getString("name"));
            edtUpdateQuote.setText(bundle.getString("quote"));
        }

        btnUpdateQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updateName = edtUpdateName.getText().toString();
                String updateQuote = edtUpdateQuote.getText().toString();

                if (bundle != null) {

                    if (updateName.isEmpty() || updateQuote.isEmpty()) {
                        Toast.makeText(UpdateQuotesActivity.this, "Inputs Can't be empty", Toast.LENGTH_SHORT).show();

                    } else if (updateName.length() > updateNameMinLength) {
                        Toast.makeText(UpdateQuotesActivity.this, "Make sure You Enter the Author name less than or equals to " + updateNameMinLength, Toast.LENGTH_SHORT).show();

                    } else {

                        Map<String , Object> updateData = new HashMap<>();
                        updateData.put("name" , updateName);
                        updateData.put("quote" , updateQuote);

                        String key = bundle.getString("key");

                        fireStore.collection("Quotes")
                                .document(key)
                                .update(updateData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            finish();

                                            edtUpdateName.getText().clear();
                                            edtUpdateQuote.getText().clear();


                                        } else {
                                            Toast.makeText(UpdateQuotesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                    }

                }

            }
        });


    }


}