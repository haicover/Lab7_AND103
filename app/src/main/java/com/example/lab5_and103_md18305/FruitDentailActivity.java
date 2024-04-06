package com.example.lab5_and103_md18305;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lab5_and103_md18305.adapter.ImageAdapter;
import com.example.lab5_and103_md18305.model.Fruit;

import java.io.File;
import java.util.ArrayList;

public class FruitDentailActivity extends AppCompatActivity {
    Fruit fruit;
    ArrayList<File> _ds_image;
    private ImageAdapter adapter;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_dentail);

        ImageView btn_back = findViewById(R.id.btn_back);
        TextView tvName= findViewById(R.id.tv_name);
        TextView tvPrice= findViewById(R.id.tv_price);
        TextView tvDescription= findViewById(R.id.tv_description);
        TextView tvQuantity= findViewById(R.id.tv_quantity);
        TextView tvStatus= findViewById(R.id.tv_status);
        RecyclerView rcvImg = findViewById(R.id.rcv_img);

        Intent intent = getIntent();
        if (intent != null) {
            fruit = (Fruit) intent.getSerializableExtra("fruit");

            if (fruit != null) {
                tvName.setText("Name: " + fruit.getName());
                tvPrice.setText("Price: " + fruit.getPrice());
                tvDescription.setText("Description: " + fruit.getDescription());
                tvQuantity.setText("Quantity: " + fruit.getQuantity());
                tvStatus.setText("Status: " + fruit.getStatus());

                Glide.with(this).load(fruit.getImage().get(0)).into(imageView);

                adapter = new ImageAdapter(_ds_image, this);
                rcvImg.setAdapter(adapter);
            }
        }
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

}