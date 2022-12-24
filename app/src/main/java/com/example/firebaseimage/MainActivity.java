package com.example.firebaseimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button chooser,uploader,shower;
    private EditText input;
    private ImageView imgView;
    private ProgressBar progressBar;
    private Uri imageUri;
    private int IMAGE_REQUEST = 1;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Gating Touch to The XML
        chooser = findViewById(R.id.chooser);
        uploader = findViewById(R.id.uploader);
        shower = findViewById(R.id.shower);
        progressBar = findViewById(R.id.progressbar);
        imgView = findViewById(R.id.imageView);
        input = findViewById(R.id.editText);


        //Initialize....
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        //if Chooser Clicked....
        chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preview();
            }
        });

        uploader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });


    }

    //File Chooser will open and selected image will shown...
    private void preview() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imgView);
        }
    }

    protected void upload(){
        if(imageUri!=null){

            // Code for showing progressDialog while uploading
            //ProgressDialog progressDialog = new ProgressDialog(this);
            //progressDialog.setTitle("Uploading...");
            //progressDialog.show();

            //Storage Reference...
            Toast.makeText(this, "Uploading Start...", Toast.LENGTH_SHORT).show();
            StorageReference reference = storageReference.child("images/" + UUID.randomUUID().toString());
            reference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "File Uploaded Successful!!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "File Upload Failed!!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            //progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            progressBar.setProgress((int) progress);
                        }
                    });
        }

    }





}