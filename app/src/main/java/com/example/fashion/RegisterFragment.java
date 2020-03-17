package com.example.fashion;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements Validator.ValidationListener {


    private MaterialButton btnRegister;

    @NotEmpty
    private TextInputEditText editName;

    @NotEmpty
    private TextInputEditText editEmail;

    @NotEmpty
    private TextInputEditText editPhone;

    @Password(min = 5, scheme = Password.Scheme.ANY)
    private TextInputEditText editPassword;

    @ConfirmPassword
    private TextInputEditText editCPassword;


    private Validator validator;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;

    private AppCompatActivity activity;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validator = new Validator(this);
        validator.setValidationListener(this);
        progressDialog = new ProgressDialog(getContext());

        activity = (AppCompatActivity) getActivity();
        sessionManager = new SessionManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        editName = (TextInputEditText) view.findViewById(R.id.editName);
        editEmail = (TextInputEditText) view.findViewById(R.id.editEmail);
        editPhone = (TextInputEditText) view.findViewById(R.id.editPhone);
        editPassword = (TextInputEditText) view.findViewById(R.id.editPassword);
        editCPassword = (TextInputEditText) view.findViewById(R.id.editCPassword);

        btnRegister = (MaterialButton) view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
        return view;
    }

    @Override
    public void onValidationSucceeded() {

        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        register();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Utils.showMessage(getContext(), "Make Sure You Provide Valid Data");
    }

    private void register() {

        final String email = editEmail.getText().toString();
        final String name = editName.getText().toString();
        final String phone = editPhone.getText().toString();
        final String password = editPassword.getText().toString();

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        addUser(new User(name, email, phone, password));
    }

    private void addUser(final User user) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        String id = reference.push().getKey();

        if (id != null) {
            reference.child(id).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    sessionManager.setUsername(user.getEmail());

                    responseDialog();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void responseDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage("Your account has been created Successfully");

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getContext(), UploadActivity.class));
            }
        });

        dialog.show();
    }
}
