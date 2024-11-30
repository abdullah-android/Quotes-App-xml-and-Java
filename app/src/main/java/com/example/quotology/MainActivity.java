package com.example.quotology;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.StatusBarManager;
import android.content.Intent;
import android.graphics.SweepGradient;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.quotology.databinding.ActivityMainBinding;
import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore fireStore;
    FloatingActionButton fabAddQuotes;
    androidx.recyclerview.widget.RecyclerView rvQuotesData;
    ArrayList<QuotesData> quotesDataArrayList;
    MyAdapter myAdapter;
    ProgressDialog progressDialog;
    androidx.appcompat.widget.Toolbar toolbar3;
    SearchView searchData;
    TextView txtSearchNotFound;
    ImageView imgMenu;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        fireStore = FirebaseFirestore.getInstance();

        fabAddQuotes = findViewById(R.id.fabAddQuotes);
        toolbar3 = findViewById(R.id.toolbar3);
        imgMenu = findViewById(R.id.imgMenu);
        searchData = findViewById(R.id.searchData);

        searchData.clearFocus();

        rvQuotesData = findViewById(R.id.rvQuotesData);
        rvQuotesData.setHasFixedSize(true);
        quotesDataArrayList = new ArrayList<QuotesData>();

        rvQuotesData.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(this , quotesDataArrayList);

        rvQuotesData.setAdapter(myAdapter);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching the Data.....");
        progressDialog.show();

        toolbar3.setTitle("Quotes");


        AddData(); // set the Method that Contains the Data of Quotes.
        UpdateData();
        RemoveData();



                imgMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu(v);
                    }
                });


        searchData.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList(query);
                UpdateData();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        fabAddQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , AddQuotesActivity.class);
                startActivity(intent);
            }
        });

    }



    private void PopupMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(this, view);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.nav_addQuote) {

                    Intent intent = new Intent(getApplicationContext(), AddQuotesActivity.class);
                    startActivity(intent);

                    return true;
                }

                return false;
            }
        });

        popupMenu.getMenuInflater().inflate(R.menu.quote_data_menu, popupMenu.getMenu());

        popupMenu.show();

    }

    // Search Method that Search the Data By the Quotes and Author Name
    public void searchList(String text) {

        ArrayList<QuotesData> searchList = new ArrayList<>();

        for (QuotesData quotesData : quotesDataArrayList) {

            if (quotesData.getQuote().toLowerCase().contains(text.toLowerCase())) {

                searchList.add(quotesData);

            }
            else if (quotesData.getName().toLowerCase().contains(text.toLowerCase())) {

                searchList.add(quotesData);

            }


        }

        myAdapter.searchDataList(searchList);

    }



    // AddData Method That Add the Data in Recycler View
    private void AddData() {

        fireStore.collection("Quotes")
                .orderBy("time" , Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.ADDED) {

                                quotesDataArrayList.add(document.getDocument().toObject(QuotesData.class));

                            }

                            myAdapter.notifyDataSetChanged();

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }

                    }
                });


    }

    // UpdateData Method That Update the Data in Recycler View
    public void UpdateData() {

        fireStore.collection("Quotes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.MODIFIED) {

                                String modifiedDocId = document.getDocument().getId(); // this Line get the Document Id in the FireStore.
                                int indexOfUpdatedItem = -1; // This Line represent if Item in Recycler view -1 means not exists.
                                for (int i = 0; i < quotesDataArrayList.size(); i++) { // this is a for Loop that represent if recycler view have item and get the quotesDataArraylist Size Like each Item.

                                    QuotesData item = quotesDataArrayList.get(i); // this Like get the Current Item in Recycler View.
                                    if (item.getKey().equals(modifiedDocId)) { // this if Condition Check if the key in fireStore field == current key.
                                        indexOfUpdatedItem = i; // this Line Represent if key in fireStore field == current key so set the indexOfUpdatedItem = i means Recycler View have Item.
                                        break;
                                    }
                                }
                                if (indexOfUpdatedItem != -1) { // this Line Check if Recycler View != have no Item if that means recycler view have item.
                                    quotesDataArrayList.set(indexOfUpdatedItem, document.getDocument().toObject(QuotesData.class)); // this set the Data in quotesDataArrayList.
                                }

                            }

                            myAdapter.notifyDataSetChanged();

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }

                    }
                });

    }


    // RemoveData Method that Remove the Data from Recycler View
    public void RemoveData() {

        fireStore.collection("Quotes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        for (DocumentChange document : value.getDocumentChanges()) {

                            if (document.getType() == DocumentChange.Type.REMOVED) {

                                String removedDocId = document.getDocument().getId();
                                int indexOfRemovedItem = -1;
                                for (int i = 0 ; i < quotesDataArrayList.size() ; i++) {

                                    QuotesData item = quotesDataArrayList.get(i);
                                    if (item.getKey().equals(removedDocId)) {
                                        indexOfRemovedItem = i;
                                        break;
                                    }

                                }

                                if (indexOfRemovedItem != -1) {
                                    quotesDataArrayList.remove(indexOfRemovedItem);
                                    quotesDataArrayList.remove(document.getDocument().toObject(QuotesData.class));
                                }

                            }

                            myAdapter.notifyDataSetChanged();

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }

                    }
                });

    }


}