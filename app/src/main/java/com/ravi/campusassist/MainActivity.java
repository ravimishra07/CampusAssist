package com.ravi.campusassist;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    Button selectFile, uploadFileBtn;
    public static Integer PICK_IMAGE_REQUEST = 101;
    public static Integer PERMISSION_GRANT = 9;

    FirebaseStorage storage;
    FirebaseDatabase database;
     Uri pdfUri;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        selectFile = findViewById(R.id.selectFile);
        uploadFileBtn = findViewById(R.id.uploadFile);

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
        progressDialog= new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
         progressDialog.setTitle("Uploading file");
         progressDialog.setProgress(0);
         progressDialog.show();
        String filename = System.currentTimeMillis()+"";
        StorageReference storageReference = storage.getReference();
        UploadTask uploadTask =   storageReference.child("PDFS").child(filename).putFile(pdfUri);
        storageReference.child("PDFS").child(filename).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task =  uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return storageReference.getDownloadUrl();
//storage.getReference().getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri pdfUri = task.getResult();
                                    DatabaseReference reference = database.getReference();
                                    reference.child(filename).setValue(pdfUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(MainActivity.this, "file successfully uploaded", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(MainActivity.this, "file not uploaded", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                                } else {
                                    Log.v("stan","file not uploaded");
                                    Toast.makeText(MainActivity.this, "file not uploaded", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                }).addOnFailureListener(e ->
                Toast.makeText(MainActivity.this, "file not uploaded", Toast.LENGTH_SHORT).show()).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                if (snapshot.getTotalByteCount() != 0) {
                    if (snapshot.getBytesTransferred() == snapshot.getTotalByteCount() ){
                        progressDialog.dismiss();

                    }else{
                        int currentProgress = (int) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setProgress(currentProgress);
                    }

                }
            }
        });

    }
/*
Task<Uri> url= storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DatabaseReference reference = database.getReference();
                                reference.child(filename).setValue(uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(MainActivity.this, "file successfully uploaded", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(MainActivity.this, "file not uploaded", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        });
 */
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
            Toast.makeText(this, "A file is successfully selected"+data.getData().getLastPathSegment() , Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Please a select a file", Toast.LENGTH_SHORT).show();
        }
    }
}