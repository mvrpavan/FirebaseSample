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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class AuthenticationActivity extends AppCompatActivity {

    TextView textViewLoginStatus, textViewDetailsName;
    EditText editTextRegisterName, editTextRegisterEmailID, editTextRegisterPassword, editTextLoginEmailID, editTextLoginPassword, editTextDetailsEmailID;
    Button btnRegisterUser, btnLoginSignIn, btnDetailsUser;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        InitializeControls();

        BindEventsToControls();
    }

    void InitializeControls() {

        editTextRegisterName = (EditText) findViewById(R.id.editTextRegisterName);
        editTextRegisterEmailID = (EditText) findViewById(R.id.editTextRegisterEmailID);
        editTextRegisterPassword = (EditText) findViewById(R.id.editTextRegisterPassword);
        btnRegisterUser = (Button) findViewById(R.id.btnRegisterUser);

        editTextLoginEmailID = (EditText) findViewById(R.id.editTextLoginEmailID);
        editTextLoginPassword = (EditText) findViewById(R.id.editTextLoginPassword);
        textViewLoginStatus = (TextView) findViewById(R.id.textViewLoginStatus);
        btnLoginSignIn = (Button) findViewById(R.id.btnLoginSignIn);

        editTextDetailsEmailID = (EditText) findViewById(R.id.editTextDetailsEmailID);
        textViewDetailsName = (TextView) findViewById(R.id.textViewDetailsName);
        btnDetailsUser = (Button) findViewById(R.id.btnDetailsUser);
    }

    void BindEventsToControls() {

        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = editTextRegisterName.getText().toString();
                String Email = editTextRegisterEmailID.getText().toString();
                String Password = editTextRegisterPassword.getText().toString();

                if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) {
                    Toast.makeText(AuthenticationActivity.this, "Name/Email/Password cannot be empty!!!", Toast.LENGTH_SHORT).show();
                } else {
                    CreateUser(Name, Email, Password);
                }
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    editTextLoginEmailID.setEnabled(false);
                    editTextLoginPassword.setEnabled(false);
                    textViewLoginStatus.setText("Hello, " + user.getDisplayName());
                    btnLoginSignIn.setText("Sign Out");
                } else {
                    editTextLoginEmailID.setEnabled(true);
                    editTextLoginPassword.setEnabled(true);
                    textViewLoginStatus.setText("");
                    btnLoginSignIn.setText("Sign In");
                }
            }
        };

        btnLoginSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {       //Implement Sign In Event
                    String Email = editTextLoginEmailID.getText().toString();
                    String Password = editTextLoginPassword.getText().toString();

                    if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) {
                        Toast.makeText(AuthenticationActivity.this, "Error:Email/Password cannot be empty!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        SignIn(Email, Password);
                    }
                } else {      //Implement Sign out Event
                    mAuth.signOut();
                    Toast.makeText(AuthenticationActivity.this, "User Logged out successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDetailsUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = editTextDetailsEmailID.getText().toString();

                if (TextUtils.isEmpty(Email)) {
                    Toast.makeText(AuthenticationActivity.this, "Error:Email cannot be empty!!!", Toast.LENGTH_SHORT).show();
                } else {
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String Email = editTextDetailsEmailID.getText().toString();
                            Boolean Found = false;
                            for (DataSnapshot user:dataSnapshot.getChildren()){
                                if (user.child("email").getValue().toString().equalsIgnoreCase(Email)) {
                                    textViewDetailsName.setText(user.child("name").getValue().toString());
                                    Found = true;
                                    break;
                                }
                            }

                            if (!Found) {
                                Toast.makeText(AuthenticationActivity.this, "User cannot be found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    void CreateUser(final String Name, String Email, String Password) {
        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(AuthenticationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AuthenticationActivity.this, "User Registration successful", Toast.LENGTH_SHORT).show();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(Name).build();

                            mAuth.getCurrentUser().updateProfile(profileUpdates);

                            try {
                                DatabaseReference dbUser = mDatabase.child(mAuth.getCurrentUser().getUid());
                                dbUser.child("email").setValue(mAuth.getCurrentUser().getEmail().toLowerCase());
                                dbUser.child("name").setValue(Name);
                            }
                            catch (Exception ex) {
                                Toast.makeText(AuthenticationActivity.this, "Error: " + ex.getMessage() + "!!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AuthenticationActivity.this, "Error: " + task.getException().getMessage() + "!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void SignIn(String Email, String Password) {
        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(AuthenticationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AuthenticationActivity.this, "User Login successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AuthenticationActivity.this, "Incorrect Email/Password!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
