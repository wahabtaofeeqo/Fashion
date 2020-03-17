package com.example.fashion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailsActivity extends AppCompatActivity {

    private String design;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        final AppCompatImageView style = (AppCompatImageView) findViewById(R.id.style);
        TextView name = (TextView) findViewById(R.id.name);
        TextView desc = (TextView) findViewById(R.id.desc);
        TextView designer = (TextView) findViewById(R.id.designer);
        MaterialButton share = (MaterialButton) findViewById(R.id.share);

        progressDialog = new ProgressDialog(this);

        Intent intent = getIntent();

        if (intent != null) {

            design = intent.getStringExtra("style");
            String n = intent.getStringExtra("name");
            String d = intent.getStringExtra("desc");
            String ds = intent.getStringExtra("designer");

            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            StorageReference ref = FirebaseStorage.getInstance().getReference("images/" + design);
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri).into(style);

                    progressDialog.dismiss();
                }
            });

            name.setText(n);
            desc.setText(d);
            designer.setText(ds);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage();
                }
            });
        }
    }

    private void sendMessage() {

        StorageReference ref = FirebaseStorage.getInstance().getReference("images/" + design);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, uri.toString());
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Choose"));
            }
        });
    }
}
