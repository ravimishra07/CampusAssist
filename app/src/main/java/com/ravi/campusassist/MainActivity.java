package com.ravi.campusassist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
Button selectFile;
public  static Integer PICK_IMAGE_REQUEST = 101;
    FirebaseStorage storage;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectFile  = findViewById(R.id.selectFile);

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

    }

    void pickImage(){
        // Defining Implicit Intent to mobile gallery
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(
//                Intent.createChooser(
//                        intent,
//                        "Select Image from here..."),
//                PICK_IMAGE_REQUEST);

        Intent intentPDF = new Intent(Intent.ACTION_GET_CONTENT);
        intentPDF.setType("application/pdf");
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intentPDF , "Select pdf"), PICK_IMAGE_REQUEST);

    }
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
     if (resultCode == PICK_IMAGE_REQUEST){
         Uri selectedUri_PDF = data.getData();
     }
    }
}