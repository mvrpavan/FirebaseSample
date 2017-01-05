package com.freelanceinc.mvrpavan.firebasesample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseDemoActivity extends AppCompatActivity {

    EditText editTextKey, editTextValue, editTextRetrieveKey, editTextDeleteKey;
    Button btnAddKeyValue, btnRetrieveValue, btnDeleteKey;
    TextView textViewNumberOfKeys, textViewRetrieveValue;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_demo);

        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fir-sample-9d2cd.firebaseio.com/");

        InitializeControls();

        BindEventsToControls();
    }

    void InitializeControls() {

        editTextKey = (EditText)findViewById(R.id.editTextKey);
        editTextValue = (EditText)findViewById(R.id.editTextValue);
        btnAddKeyValue = (Button)findViewById(R.id.btnAddKeyValue);

        editTextRetrieveKey = (EditText)findViewById(R.id.editTextRetrieveKey);
        textViewRetrieveValue = (TextView)findViewById(R.id.textViewRetrieveValue);
        btnRetrieveValue = (Button)findViewById(R.id.btnRetrieveValue);

        textViewNumberOfKeys = (TextView)findViewById(R.id.textViewNumberOfKeys);

        editTextDeleteKey = (EditText)findViewById(R.id.editTextDeleteKey);
        btnDeleteKey = (Button)findViewById(R.id.btnDeleteKey);
    }

    void BindEventsToControls(){

        btnAddKeyValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Key = editTextKey.getText().toString();
                String Value = editTextValue.getText().toString();

                if (TextUtils.isEmpty(Key) || TextUtils.isEmpty(Value)) {
                    Toast.makeText(DatabaseDemoActivity.this, "Either Key or Value cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    mDatabase.child(Key).setValue(Value).addOnCompleteListener(DatabaseDemoActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(DatabaseDemoActivity.this, "Key-Value added successfully", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(DatabaseDemoActivity.this, "Unable to add Key-Value!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        btnRetrieveValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Key = editTextRetrieveKey.getText().toString();

                if (TextUtils.isEmpty(Key)) {
                    Toast.makeText(DatabaseDemoActivity.this, "Key cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    mDatabase.child(Key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String Value = dataSnapshot.getValue(String.class);

                            if (!dataSnapshot.exists()) {
                                Toast.makeText(DatabaseDemoActivity.this, "Key not found", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                textViewRetrieveValue.setText(Value);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(DatabaseDemoActivity.this, "Unable to get Value for Key", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewNumberOfKeys.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnDeleteKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Key = editTextDeleteKey.getText().toString();

                if (TextUtils.isEmpty(Key)) {
                    Toast.makeText(DatabaseDemoActivity.this, "Key cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    mDatabase.child(Key).removeValue().addOnCompleteListener(DatabaseDemoActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(DatabaseDemoActivity.this, "Deleted Key successfully", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(DatabaseDemoActivity.this, "Unable to delete Key", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
