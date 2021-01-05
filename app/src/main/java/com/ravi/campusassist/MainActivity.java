package com.ravi.campusassist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    Button selectFile, uploadFileBtn;
    public static Integer PICK_IMAGE_REQUEST = 101;
    public static Integer PERMISSION_GRANT = 9;

    FirebaseStorage storage;
    FirebaseDatabase database;
    Uri pdfUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        selectFile = findViewById(R.id.selectFile);

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    pickFile();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_GRANT);
                }
            }
        });
        uploadFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pdfUri !=null){
                    uploadFile(pdfUri);
                }else{
                    Toast.makeText(MainActivity.this, "Select a file", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadFile(Uri pdfUri) {
        String filename = System.currentTimeMillis()+"";
        StorageReference storageReference = storage.getReference();
        storageReference.child("PDFS").child(filename).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_GRANT && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            pickFile();
        }else{
            Toast.makeText(this, "please provide permission", Toast.LENGTH_SHORT).show();
        }
    }
    

    void pickFile() {


        Intent intentPDF = new Intent(Intent.ACTION_GET_CONTENT);
        intentPDF.setType("application/pdf");
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intentPDF, "Select pdf"), PICK_IMAGE_REQUEST);

    }

//    public String getPath(Uri uri) {
//        String[] projection = {MediaStore.MediaColumns.DATA};
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//        cursor.moveToFirst();
//        imagePath = cursor.getString(column_index);
//
//        return cursor.getString(column_index);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null) {
            pdfUri = data.getData();
        }else{
            Toast.makeText(this, "Please a select a file", Toast.LENGTH_SHORT).show();
        }
    }
}