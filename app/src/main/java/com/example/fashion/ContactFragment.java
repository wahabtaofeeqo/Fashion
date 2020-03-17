package com.example.fashion;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment implements Validator.ValidationListener {


    private AppCompatActivity activity;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    private Validator validator;

    @NotEmpty
    private TextInputEditText subject;

    @NotEmpty
    private TextInputEditText message;

    public ContactFragment() {
        // Required empty public constructor
    }

    private OnFragmentChangeListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (AppCompatActivity) getActivity();
        sessionManager = new SessionManager(getContext());
        progressDialog = new ProgressDialog(getActivity());

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        subject = (TextInputEditText) view.findViewById(R.id.subject);
        message = (TextInputEditText) view.findViewById(R.id.message);

        //activity.getSupportActionBar().setTitle("Contact Us");
        MaterialButton button = (MaterialButton) view.findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        listener = (OnFragmentChangeListener) getActivity();
    }

    @Override
    public void onValidationSucceeded() {

        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        contact(sessionManager.getUsername(), subject.getText().toString(), message.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Utils.showMessage(activity, "Provide The Required Data");
    }

    private void contact(final String email, final String sub, final String msg) {

       addMessage(new Message(email, sub, msg));
    }

    private void addMessage(final Message message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages");
        String id = reference.push().getKey();

        if (id != null) {
            reference.child(id).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
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
        dialog.setMessage("Message sent Successfully");

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (listener != null) {
                    listener.onFragmentChange(new HomeFragment());
                }
            }
        });

        dialog.show();
    }
}
