package com.example.fashion;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class StylesAdapter extends RecyclerView.Adapter<StylesAdapter.ViewHolder> {

    private List<Style> designs;
    private Context context;

    private MainActivity activity;

    public StylesAdapter(Context context, List<Style> designs, MainActivity activity) {
        this.context = context;
        this.designs = designs;
        this.activity = activity;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public View v;
        public AppCompatImageView style;
        public TextView name;
        public TextView designer;

        public ViewHolder(View view) {
            super(view);
            v = view;
            style = (AppCompatImageView) view.findViewById(R.id.style);
            name = (TextView) view.findViewById(R.id.name);
            designer = (TextView) view.findViewById(R.id.designer);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.style_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Style style = designs.get(position);

        holder.name.setText(style.getName());
        holder.designer.setText(style.getDesigner());

        StorageReference ref = FirebaseStorage.getInstance().getReference("images/" + style.getStyle());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               Glide.with(context).load(uri).into(holder.style);
            }
        });

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showDetails(style);
            }
        });
    }

    @Override
    public int getItemCount() {
        return designs.size();
    }
}
