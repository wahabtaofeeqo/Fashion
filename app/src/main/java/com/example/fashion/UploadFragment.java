package com.example.fashion;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.File;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends Fragment implements Validator.ValidationListener {

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

    public UploadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validator = new Validator(this);
        validator.setValidationListener(this);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = database.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        name = (TextInputEditText) view.findViewById(R.id.name);
        desc = (TextInputEditText) view.findViewById(R.id.desc);
        style = (AppCompatImageView) view.findViewById(R.id.style);

        final MaterialButton upload = (MaterialButton) view.findViewById(R.id.upload);
        final MaterialButton save = (MaterialButton) view.findViewById(R.id.save);

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

        style.setVisibility(View.VISIBLE);


        final StorageReference reference = storageReference.child("/images");
        reference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference item: listResult.getItems()) {
                   item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           Glide.with(getContext()).load(uri).into(style);
                       }
                   });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

        return view;
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
                Utils.logMessage(getContext(), e.getMessage());
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
                    Utils.showMessage(getContext(), "Your Style has been added Successfully");
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