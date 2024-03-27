package com.example.lab5_and103_md18305.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5_and103_md18305.R;
import com.example.lab5_and103_md18305.handle.Item_Distributor_Handle;
import com.example.lab5_and103_md18305.model.Distributor;


import java.util.ArrayList;


public class DistributorAdapter extends RecyclerView.Adapter<DistributorAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Distributor> list;
    private Item_Distributor_Handle handle;



    public DistributorAdapter(Context context, ArrayList<Distributor> distributors, Item_Distributor_Handle handle) {
        this.context = context;
        this.list = distributors;
        this.handle = handle;
    }
    @NonNull
    @Override
    public DistributorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DistributorAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Distributor distributor = list.get(position);
        holder.tvName.setText(distributor.getName());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDistributor(distributor.getId());
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDistributor(distributor.getId(),distributor);
            }
        });
    }

    private void deleteDistributor(String id) {
        handle.Delete(id);
    }

    private void updateDistributor(String id, Distributor distributor) {
        handle.Update( id,distributor);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView btnDelete, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
