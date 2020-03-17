package com.example.fashion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty
    private TextInputEditText name;

    @NotEmpty
    private TextInputEditText desc;

    private AppCompatImageView style;

    private String filePath;
    private Uri path;
    private Validator validator;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

//        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        validator = new Validator(this);
        validator.setValidationListener(this);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = database.getReference();

        name = (TextInputEditText) findViewById(R.id.name);
        desc = (TextInputEditText) findViewById(R.id.desc);
        style = (AppCompatImageView) findViewById(R.id.style);

        final MaterialButton upload = (MaterialButton) findViewById(R.id.upload);
        final MaterialButton save = (MaterialButton) findViewById(R.id.save);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

    }

    @Override
    public void onValidationSucceeded() {
        if (path != null) {
            uploadStyle(path);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Choose Style"), Utils.REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.REQUEST_CODE && data != null && data.getData() != null && resultCode == Activity.RESULT_OK) {
            style.setVisibility(View.VISIBLE);
            style.setImageURI(data.getData());

            path = data.getData();
        }
    }

    private void uploadStyle(Uri path) {

        final String filename = UUID.randomUUID().toString();

        StorageReference reference = storageReference.child("images/" + filename);
        reference.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                Design design = new Design(name.getText().toString(), desc.getText().toString(), "Old School", filename);
                addStyle(design);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utils.logMessage(getApplicationContext(), e.getMessage());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }

    private void addStyle(Design design) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("styles");
        String id = reference.push().getKey();

        if (id != null) {
            reference.child(id).setValue(design).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Utils.showMessage(getApplicationContext(), "Your Style has been added Successfully");
                    path = null;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
