package com.example.snapchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    EditText emailEditText;
    EditText passwordEditText;
    private FirebaseAuth mAuth;
    ConstraintLayout constraintLayout;
    ImageView imageView;
    TextView textView;

    public void snaps(){
        emailEditText.setText("");
        passwordEditText.setText("");
        Intent intent = new Intent(this,SnapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

    }

    public void login(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        if(!emailEditText.getText().toString().equals("") && !passwordEditText.getText().toString().equals("")) {
            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setMessage("Login Successful! Welcome...")
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                emailEditText.setText("");
                                                passwordEditText.setText("");
                                                snaps();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            } else {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("ERROR!!!")
                                        .setMessage("Login failed! Please try again...")
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                emailEditText.setText("");
                                                passwordEditText.setText("");
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        }
                    });
        }else{
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("ERROR!!!")
                    .setMessage("Login failed! Please fill all details...")
                    .setNeutralButton("Ok", null)
                    .setCancelable(false)
                    .show();
        }
    }

    public void signup(View view){
        Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Picschat");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        mAuth = FirebaseAuth.getInstance();
        constraintLayout = findViewById(R.id.constraintLayout);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        constraintLayout.setOnClickListener(this);
        imageView.setOnClickListener(this);
        textView.setOnClickListener(this);

        passwordEditText.setOnKeyListener(this);

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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.constraintLayout || v.getId() == R.id.imageView || v.getId() == R.id.textView){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if(i == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == keyEvent.ACTION_DOWN){
            login(view);
        }
        return false;
    }
}
