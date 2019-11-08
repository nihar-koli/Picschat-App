package com.example.snapchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText passwordEditText;
    EditText cpasswordEditText;
    private FirebaseAuth mAuth;

    public void signup(View view){
        if(emailEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("") || cpasswordEditText.getText().toString().equals("")){
            new AlertDialog.Builder(RegisterActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("ERROR!!!")
                    .setMessage("Please fill all details...")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            emailEditText.setText("");
                            passwordEditText.setText("");
                        }
                    })
                    .setCancelable(false)
                    .show();
        }else if(passwordEditText.getText().toString().equals(cpasswordEditText.getText().toString())){
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
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setMessage("SignUp Successful!")
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            } else {
                                // If sign in fails, display a message to the user.
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("ERROR!!!")
                                        .setMessage("SignUp failed! Please try again...")
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                emailEditText.setText("");
                                                passwordEditText.setText("");
                                                cpasswordEditText.setText("");
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }

                        }
                    });
        }else{
            new AlertDialog.Builder(RegisterActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("ERROR!!!")
                    .setMessage("Password mismatch...")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            passwordEditText.setText("");
                            cpasswordEditText.setText("");
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("Register");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        cpasswordEditText = findViewById(R.id.cpasswordEditText);
        mAuth = FirebaseAuth.getInstance();

    }
}
