package com.example.snapchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class CreateSnaps extends AppCompatActivity implements View.OnClickListener {

    ImageView snapImageView;
    EditText messageEditText;
    StorageReference mStorageRef;
    String imageName = UUID.randomUUID().toString() + ".jpg";
    Button next;
    Button choose;
    ProgressDialog progressDialog;
    ConstraintLayout createLayout;


    public void nextClicked(View view){

        next.setEnabled(false);
        choose.setEnabled(false);

        showProgressDialogWithTitle("Creating Snap","Please wait...");

        // Get the data from an ImageView as bytes
        snapImageView.setDrawingCacheEnabled(true);
        snapImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) snapImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("Status",exception.toString());
                new AlertDialog.Builder(CreateSnaps.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("ERROR!")
                        .setMessage("Failed to send snap...")
                        .setNeutralButton("Ok",null)
                        .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                hideProgressDialogWithTitle();
                //loadingLayout.setVisibility(View.GONE);
                Intent intent = new Intent(CreateSnaps.this,ChooseUserListActivity.class);
                intent.putExtra("imageName",imageName);
                intent.putExtra("message",messageEditText.getText().toString());
                startActivity(intent);
            }
        });
    }

    public void chooseImageClicked(View view){
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            getPhoto();

        }
    }

    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
        next.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            try {
                Uri selectedImg = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImg);
                snapImageView.setImageBitmap(bitmap);
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Intent intent = new Intent(CreateSnaps.this,SnapsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_snaps);

        setTitle("Create Snap");

        snapImageView = findViewById(R.id.snapImageView);
        messageEditText = findViewById(R.id.messageEditText);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        next = findViewById(R.id.nextButton);
        choose = findViewById(R.id.chooseImageButton);
        progressDialog = new ProgressDialog(this);
        createLayout = findViewById(R.id.createLayout);

        createLayout.setOnClickListener(this);

        next.setEnabled(false);
        choose.setEnabled(true);

    }

    private void showProgressDialogWithTitle(String title,String substring) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        progressDialog.setCancelable(false);
        //Setting Title
        progressDialog.setTitle(title);
        progressDialog.setMessage(substring);
        progressDialog.show();

    }

    // Method to hide/ dismiss Progress bar
    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.createLayout){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }
}
