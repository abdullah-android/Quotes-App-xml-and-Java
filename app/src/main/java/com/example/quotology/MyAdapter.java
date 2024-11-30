package com.example.quotology;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import kr.co.prnd.readmore.ReadMoreTextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<QuotesData> quotesDataArrayList;


    public MyAdapter(Context context, ArrayList<QuotesData> quotesDataArrayList) {
        this.context = context;
        this.quotesDataArrayList = quotesDataArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quotes_data, parent, false);
        return new MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        QuotesData quotesData = quotesDataArrayList.get(position);

        holder.txtName.setText(quotesData.getName());
        holder.txtQuote.setText(quotesData.getQuote());

        UpdateQuote(holder, quotesData);
        DeleteQuote(holder, quotesData);




    }



    @Override
    public int getItemCount() {
        return quotesDataArrayList.size();
    }

    public void searchDataList(ArrayList<QuotesData> searchList) {
        quotesDataArrayList = searchList;
        notifyDataSetChanged();
    }


    public void DeleteQuote(@NonNull MyViewHolder holder, QuotesData quotesData) {

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.are_you_sure_you_want_to_delete_this_quote));
                builder.setCancelable(false);


                // set the on Click to Cancel Button on Alert Dialog
                builder.setNegativeButton(context.getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                // set the on Click to Delete Button on Alert Dialog
                builder.setPositiveButton(context.getResources().getString(R.string.delete),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String key = quotesData.getKey();

                                holder.fireStore.collection("Quotes")
                                        .document(key)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                dialog.dismiss();
                            }
                        });
                builder.show();

            }
        });


    }

    public void UpdateQuote(@NonNull MyViewHolder holder, QuotesData quotesData) {

        holder.imgUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , UpdateQuotesActivity.class);
                intent.putExtra("name" , quotesData.getName());
                intent.putExtra("quote" , quotesData.getQuote());
                intent.putExtra("key" , quotesData.getKey());
                context.startActivity(intent);
            }
        });

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ReadMoreTextView txtQuote;
        TextView txtName;
        ImageView imgUpdate, imgDelete;
        FirebaseFirestore fireStore;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtQuote = itemView.findViewById(R.id.txtQuote);
            txtName = itemView.findViewById(R.id.txtName);
            imgUpdate = itemView.findViewById(R.id.imgUpdate);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            fireStore = FirebaseFirestore.getInstance();



        }

    }


}