package com.example.firebaseimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private Button chooser,uploader,shower;
    private EditText input;
    private ImageView imgView;
    private ProgressBar progressBar;
    private Uri imageUri;
    private int IMAGE_REQUEST = 1;

    StorageTask uploadTask;
    DatabaseReference databaseReference;
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
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        //if Chooser Clicked....
        chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preview();
            }
        });

        //if Uploader Clicked....
        uploader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uploadTask!=null && uploadTask.isInProgress()){
                    Toast.makeText(MainActivity.this, "File Uploading Please Wait...", Toast.LENGTH_SHORT).show();
                }else{
                    upload();
                }
            }
        });

        //if Shower Clicked....
        shower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ImageActivity.class);
                startActivity(intent);
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

    //Gating File Extension...
    public String getFileExtension(Uri imageUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    //Upload Method...
    protected void upload(){
        String imageName = input.getText().toString().trim();

        if(imageName.isEmpty()){
            input.setError("Please Enter Image Name....");
            input.requestFocus();
            return;
        }

        if(imageUri!=null){
            /*
            Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            Toast.makeText(this, "Uploading Start...", Toast.LENGTH_SHORT).show()
            */

            //Storage Reference...
            StorageReference reference = storageReference.child("images/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            reference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();

                            Task<Uri>urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();

                            DB_Model db_model = new DB_Model(imageName,downloadUrl.toString());
                            String key = databaseReference.push().getKey();
                            databaseReference.child(key).setValue(db_model);
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