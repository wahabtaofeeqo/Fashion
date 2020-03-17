package com.example.fashion;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private RecyclerView recyclerView;
    private StylesAdapter adapter;

    private List<Style> styles;
    private Design design;
    private Style style;
    private Uri uri;

    private ProgressDialog progressDialog;

    private SwipeRefreshLayout refresh;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStyles();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        getStyles();

        return view;
    }

    private void getStyles() {

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("styles");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<Style> allStyles = new ArrayList<>();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    design = snapshot.getValue(Design.class);

                    allStyles.add(new Style(design.getName(), design.getDesc(), design.getDesigner(), design.getStyle()));
                }

                adapter = new StylesAdapter(getContext(), allStyles, (MainActivity) getActivity());
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                recyclerView.setAdapter(adapter);

                refresh.setRefreshing(false);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressDialog.dismiss();
                Utils.logMessage(getContext(), databaseError.getMessage());
            }
        });
    }

}