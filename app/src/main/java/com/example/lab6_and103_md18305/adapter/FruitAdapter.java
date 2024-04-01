package com.example.lab5_and103_md18305.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab5_and103_md18305.R;
import com.example.lab5_and103_md18305.handle.Item_Fruit_Handler;
import com.example.lab5_and103_md18305.model.Fruit;

import java.util.ArrayList;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Fruit> fruits;
    private Item_Fruit_Handler handle;
    public FruitAdapter(Context context, ArrayList<Fruit> fruits,Item_Fruit_Handler handle) {
        this.context = context;
        this.fruits = fruits;
        this.handle = handle;
    }

    @NonNull
    @Override
    public FruitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fruit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FruitAdapter.ViewHolder holder, int position) {
        Fruit fruit = fruits.get(position);
        holder.tvName.setText("Name: "+fruit.getName());
        holder.tvQuantity.setText("Quantity: " + Integer.parseInt(String.valueOf(fruit.getQuantity())));
        holder.tvPrice.setText("Price: $" + Integer.parseInt(String.valueOf(fruit.getPrice())));

        Glide.with(context)
                .load(fruit.getImage().get(0))
                .thumbnail(Glide.with(context).load(R.mipmap.img1))
                .into(holder.avatar);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFruit(fruit.get_id());
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFruit(fruit.get_id(),fruit);
            }
        });
    }
    private void deleteFruit(String id) {
        handle.Delete(id);
    }

    private void updateFruit(String id, Fruit fruit) {
        handle.Update( id,fruit);
    }
    @Override
    public int getItemCount() {
        return fruits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvPrice;
        ImageView btnDelete, btnEdit, avatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }
}
