package com.example.lab5_and103_md18305;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab5_and103_md18305.adapter.DistributorAdapter;
import com.example.lab5_and103_md18305.handle.Item_Distributor_Handle;
import com.example.lab5_and103_md18305.model.Distributor;
import com.example.lab5_and103_md18305.model.Response;
import com.example.lab5_and103_md18305.services.HttpRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;


public class MainActivity extends AppCompatActivity implements Item_Distributor_Handle {
    RecyclerView rvDistributor;
    DistributorAdapter adapter;
    private ArrayList<Distributor> list = new ArrayList<>();
    FloatingActionButton cartBtn;
    EditText edTimKiem;
    HttpRequest httpRequest;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        edTimKiem = findViewById(R.id.edTimKiem);
        rvDistributor = findViewById(R.id.rvDistributor);
        cartBtn = findViewById(R.id.cartBtn);
        httpRequest = new HttpRequest();
        httpRequest.callAPI()
                .getListDistributor()
                .enqueue(getDistributorAPI);

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemDialog();
            }
        });

        edTimKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = edTimKiem.getText().toString();
                httpRequest.callAPI()
                        .searchDistributor(key)
                        .enqueue(getDistributorAPI);
            }
        });

    }

    Callback<Response<ArrayList<Distributor>>> getDistributorAPI = new Callback<Response<ArrayList<Distributor>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    list = response.body().getData();
                    getData();
                    Log.d(TAG, "onResponse: " + list.size());
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable t) {
            Log.e(TAG, "onFailure: " + t.getMessage());
        }
    };

    Callback<Response<Distributor>> responseDistributorAPI = new Callback<Response<Distributor>>() {
        @Override
        public void onResponse(Call<Response<Distributor>> call, retrofit2.Response<Response<Distributor>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    httpRequest.callAPI()
                            .getListDistributor()
                            .enqueue(getDistributorAPI);
                    Toast.makeText(MainActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Distributor>> call, Throwable t) {
            Log.e(TAG, "onFailure: " + t.getMessage());
        }
    };

    private void getData() {
        adapter = new DistributorAdapter(MainActivity.this, list, this);
        rvDistributor.setLayoutManager(new LinearLayoutManager(this));
        rvDistributor.setAdapter(adapter);
    }

    private void showAddItemDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.item_add_student);
        EditText addName = dialog.findViewById(R.id.addName);
        TextView btnAdd = dialog.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            String name = addName.getText().toString().trim();
            if (!name.isEmpty()) {
                Distributor distributor = new Distributor();
                distributor.setName(name);
                httpRequest.callAPI()
                        .addDistributor(distributor)
                        .enqueue(responseDistributorAPI);
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }


    private void showDialogEdit(String id,Distributor distributor) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.item_update_student);
        EditText upName = dialog.findViewById(R.id.upName);
        TextView btnUpdate = dialog.findViewById(R.id.btnUpdate);

        String distributorName = distributor.getName();
        upName.setText(distributorName);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = upName.getText().toString().trim();
                if (!updatedName.isEmpty()) {
                    distributor.setName(updatedName);
                    httpRequest.callAPI()
                            .updateDistributor(id, distributor)
                            .enqueue(responseDistributorAPI);
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void Delete(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirm delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            httpRequest.callAPI()
                    .deleteDistributor(id)
                    .enqueue(responseDistributorAPI);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    @Override
    public void Update(String id,Distributor distributor) {
        showDialogEdit(id,distributor);
    }
}