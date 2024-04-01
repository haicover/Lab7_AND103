package com.example.lab5_and103_md18305;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lab5_and103_md18305.adapter.FruitAdapter;
import com.example.lab5_and103_md18305.handle.Item_Fruit_Handler;
import com.example.lab5_and103_md18305.model.Fruit;
import com.example.lab5_and103_md18305.model.Response;
import com.example.lab5_and103_md18305.services.HttpRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class FruitActivity extends AppCompatActivity implements Item_Fruit_Handler {
    RecyclerView rvFruit;
    FruitAdapter adapter;
    private ArrayList<Fruit> list = new ArrayList<>();
    FloatingActionButton cartBtn;
    EditText edTimKiem;
    SharedPreferences sharedPreferences;
    HttpRequest httpRequest;
    private String token;
    private static final String TAG = "FruitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit);


        httpRequest = new HttpRequest();
        sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getFruitAPI);
        edTimKiem = findViewById(R.id.edTimKiem);
        rvFruit = findViewById(R.id.rvFruit);
        cartBtn = findViewById(R.id.cartBtn);

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FruitActivity.this, AddFruitActivity.class));
                finish();
            }
        });

        edTimKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = edTimKiem.getText().toString();
                httpRequest.callAPI()
                        .searchFruit(key)
                        .enqueue(getFruitAPI);
            }
        });
    }

    private void getData() {
        adapter = new FruitAdapter(this, list, this);
        rvFruit.setLayoutManager(new LinearLayoutManager(this));
        rvFruit.setAdapter(adapter);
    }

    Callback<Response<ArrayList<Fruit>>> getFruitAPI = new Callback<Response<ArrayList<Fruit>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    list = response.body().getData();
                    getData();
                    Log.d(TAG, response.body().getMessenger() + list.size());
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {
            Log.e(TAG, "onFailure: " + t.getMessage());
        }
    };

    Callback<Response<Fruit>> responseFruitAPI = new Callback<Response<Fruit>>() {
        @Override
        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    httpRequest.callAPI()
                            .getListFruit("Bearer" + token)
                            .enqueue(getFruitAPI);
                    Toast.makeText(FruitActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Fruit>> call, Throwable t) {
            Log.e(TAG, "onFailure: " + t.getMessage());
        }
    };

    private void showDialogEdit(String id, Fruit fruit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FruitActivity.this);
        View dialogView = LayoutInflater.from(FruitActivity.this).inflate(R.layout.item_update_fruit, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextQuantity = dialogView.findViewById(R.id.editTextQuantity);
        EditText editTextPrice = dialogView.findViewById(R.id.editTextPrice);
        EditText editTextStatus = dialogView.findViewById(R.id.editTextStatus);
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        TextView buttonUpdate = dialogView.findViewById(R.id.buttonUpdate);


        String fruitName = fruit.getName();
        editTextName.setText(fruitName);
        int fruitQuantity = fruit.getQuantity();
        editTextQuantity.setText(String.valueOf(fruitQuantity));
        int fruitPrice = fruit.getPrice();
        editTextPrice.setText(String.valueOf(fruitPrice));
        int fruitStatus = fruit.getStatus();
        editTextStatus.setText(String.valueOf(fruitStatus));
        String fruitDescription = fruit.getDescription();
        editTextDescription.setText(fruitDescription);


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = editTextName.getText().toString().trim();
                String updatedQuantity = editTextQuantity.getText().toString().trim();
                String updatedPrice = editTextPrice.getText().toString().trim();
                String updatedStatus = editTextStatus.getText().toString().trim();
                String updatedDescription = editTextDescription.getText().toString().trim();

                if (!updatedName.isEmpty() || !updatedQuantity.isEmpty() || !updatedPrice.isEmpty() || !updatedStatus.isEmpty() || !updatedDescription.isEmpty()) {
                    fruit.setName(updatedName);
                    fruit.setQuantity(Integer.parseInt(updatedQuantity));
                    fruit.setPrice(Integer.parseInt(updatedPrice));
                    fruit.setStatus(Integer.parseInt(updatedStatus));
                    fruit.setDescription(updatedDescription);
                    httpRequest.callAPI()
                            .updateFruit(id, fruit)
                            .enqueue(responseFruitAPI);
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(FruitActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.show();
    }

    @Override
    public void Delete(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FruitActivity.this);
        builder.setTitle("Confirm delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            httpRequest.callAPI()
                    .deleteFruit(id)
                    .enqueue(responseFruitAPI);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    @Override
    public void Update(String id, Fruit fruit) {
        showDialogEdit(id, fruit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        httpRequest.callAPI().getListFruit("Bearer" + token).enqueue(getFruitAPI);
    }
}