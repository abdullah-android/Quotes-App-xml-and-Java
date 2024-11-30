package com.example.quotology;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quotology.databinding.ActivityAddQuotesBinding;
import com.example.quotology.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddQuotesActivity extends AppCompatActivity {
    EditText edtName, edtQuote;
    Button btnAddQuotes;
    FirebaseFirestore fireStore;
    androidx.appcompat.widget.Toolbar toolbar;
    int nameMinLength = 20;
    private final int INTERNET_PERMISSION_CODE = 3465;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_quotes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fireStore = FirebaseFirestore.getInstance();

        btnAddQuotes = findViewById(R.id.btnAddQuotes);
        edtName = findViewById(R.id.edtName);
        edtQuote = findViewById(R.id.edtQuote);

        toolbar = findViewById(R.id.toolbar);

        // set Title on Toolbar
        toolbar.setTitle("Add Quote");
        // set the Back Icon in Toolbar
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow_icon));
        // set the on Click to Back Icon in Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddingQuote();
            }
        });

    }

    private String generateUniqueClientId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private void AddingQuote() {

        String name = edtName.getText().toString();
        String quote = edtQuote.getText().toString();

        if (name.isEmpty() || quote.isEmpty()) {
            Toast.makeText(AddQuotesActivity.this, "Inputs Can't be empty", Toast.LENGTH_SHORT).show();

        } else if (name.length() > nameMinLength) {
            Toast.makeText(AddQuotesActivity.this, "Make sure You Enter the Author name less than or equals to " + nameMinLength, Toast.LENGTH_SHORT).show();

        }
        else {

            Map<String , Object> quotesData = new HashMap<>();
            quotesData.put("name" , name);
            quotesData.put("quote" , quote);

            String key = generateUniqueClientId();
            quotesData.put("key" , key);
            quotesData.put("time" , FieldValue.serverTimestamp());


            fireStore.collection("Quotes")
                    .document(key)
                    .set(quotesData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            finish();

                            edtName.getText().clear();
                            edtQuote.getText().clear();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddQuotesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }


    }


}