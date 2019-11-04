package com.example.snapchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText passwordEditText;
    private FirebaseAuth mAuth;

    public void snaps(){
        emailEditText.setText("");
        passwordEditText.setText("");
        Intent intent = new Intent(this,SnapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

    }

    public void login(View view){
        if(!emailEditText.getText().toString().equals("") && !passwordEditText.getText().toString().equals("")) {
            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                               Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                snaps();
                            } else {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("ERROR!!!")
                                        .setMessage("Login failed! Please try again...")
                                        .setNeutralButton("Ok", null)
                                        .show();
                            }
                        }
                    });
        }else{
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("ERROR!!!")
                    .setMessage("Login failed! Please try again...")
                    .setNeutralButton("Ok", null)
                    .show();
        }
    }

    public void signup(View view){
        if(!emailEditText.getText().toString().equals("") && !passwordEditText.getText().toString().equals("")) {
            mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                try {
                                    FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).child("email").setValue(emailEditText.getText().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(MainActivity.this, "SignUp Successful!", Toast.LENGTH_SHORT).show();
                                snaps();
                            } else {
                                // If sign in fails, display a message to the user.
                                new AlertDialog.Builder(MainActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("ERROR!!!")
                                        .setMessage("SignUp failed! Please try again...")
                                        .setNeutralButton("Ok", null)
                                        .show();
                            }

                        }
                    });
        }else{
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("ERROR!!!")
                    .setMessage("SignUp failed! Please try again...")
                    .setNeutralButton("Ok", null)
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Picschat");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        mAuth = FirebaseAuth.getInstance();

        emailEditText.setText("");
        passwordEditText.setText("");


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            snaps();
        }
    }
}
