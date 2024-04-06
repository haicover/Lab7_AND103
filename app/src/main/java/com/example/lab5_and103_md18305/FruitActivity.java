package com.example.lab5_and103_md18305;

import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lab5_and103_md18305.adapter.FruitAdapter;
import com.example.lab5_and103_md18305.handle.Item_Fruit_Handler;
import com.example.lab5_and103_md18305.model.Fruit;
import com.example.lab5_and103_md18305.model.Page;
import com.example.lab5_and103_md18305.model.Response;
import com.example.lab5_and103_md18305.services.HttpRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Header;

public class FruitActivity extends AppCompatActivity implements Item_Fruit_Handler {
    RecyclerView rvFruit;
    FruitAdapter adapter;
    private ArrayList<Fruit> list = new ArrayList<>();
    FloatingActionButton cartBtn;
    EditText edTimKiem, edGia;
    Button btnLoc;
    SharedPreferences sharedPreferences;
    HttpRequest httpRequest;
    private String token;
    private static final String TAG = "FruitActivity";
    private ProgressBar loadmore;
    private int page = 1;
    private int totalPage = 0;
    Spinner ascending;
    private String sort = "";
    private NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit);


        httpRequest = new HttpRequest(token);
        sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getFruitAPI);
        edTimKiem = findViewById(R.id.edTimKiem);
        edGia = findViewById(R.id.edGia);
        rvFruit = findViewById(R.id.rvFruit);
        btnLoc = findViewById(R.id.btnLoc);
        cartBtn = findViewById(R.id.cartBtn);
        ascending = findViewById(R.id.ascending);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        loadmore = findViewById(R.id.loadmore);
        Map<String, String> map = getMapFilter(page, "", "0", "-1");
        httpRequest.callAPI().getPageFruit(map).enqueue(getListFruitResponse);
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
                        .enqueue(new Callback<Response<ArrayList<Fruit>>>() {
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
                        });
            }
        });
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (loadmore.getVisibility() == View.GONE) {
                        loadmore.setVisibility(View.GONE);
                        page++;
                        FilterFruit();
                    }
                }
            }
        });

        ArrayAdapter<CharSequence> sequenceArrayAdapter = ArrayAdapter.createFromResource(this, R.array.spiner_price, android.R.layout.simple_spinner_item);
        ascending.setAdapter(sequenceArrayAdapter);
        ascending.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CharSequence sequence = (CharSequence) adapterView.getAdapter().getItem(i);
                if (sequence.toString().equals("Ascending")) {
                    sort = "1";
                } else if (sequence.toString().equals("Decrease")) {
                    sort = "-1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ascending.setSelection(1);

        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 1;
                list.clear();
                FilterFruit();
            }
        });


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

    private void FilterFruit() {
        String _name = edTimKiem.getText().toString().equals("") ? "" : edTimKiem.getText().toString();
        String _price = edGia.getText().toString().equals("") ? "0" : edGia.getText().toString();
        String _sort = sort.equals("") ? "-1" : sort;
        Map<String, String> map = getMapFilter(page, _name, _price, _sort);
        httpRequest.callAPI().getPageFruit(map).enqueue(getListFruitResponse);
    }

    private Map<String, String> getMapFilter(int _page, String _name, String _price, String _sort) {
        Map<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(_page));
        map.put("name", String.valueOf(_name));
        map.put("price", String.valueOf(_price));
        map.put("sort", String.valueOf(_sort));
        return map;
    }

    private void getData() {
        if (loadmore.getVisibility() == View.VISIBLE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemInserted(list.size() - 1);
                    loadmore.setVisibility(View.GONE);
                    list.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }, 1000);
            return;
        }
        list.addAll(list);
        adapter = new FruitAdapter(this, list, this);
        rvFruit.setLayoutManager(new LinearLayoutManager(this));
        rvFruit.setAdapter(adapter);
    }

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
                            .enqueue(new Callback<Response<Fruit>>() {
                                @Override
                                public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
                                    if (response.isSuccessful()) {
                                        if (response.body().getStatus() == 200) {
                                            httpRequest.callAPI()
                                                    .getListFruit("Bearer"+token)
                                                    .enqueue(new Callback<Response<ArrayList<Fruit>>>() {
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
                                                    });
                                            Toast.makeText(FruitActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Response<Fruit>> call, Throwable t) {
                                    Log.e(TAG, "onFailure: " + t.getMessage());
                                }
                            });
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(FruitActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.show();
    }

    Callback<Response<Page<ArrayList<Fruit>>>> getListFruitResponse = new Callback<Response<Page<ArrayList<Fruit>>>>() {
        @Override
        public void onResponse(Call<Response<Page<ArrayList<Fruit>>>> call, retrofit2.Response<Response<Page<ArrayList<Fruit>>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    totalPage = response.body().getData().getTotalPage();
                    list = response.body().getData().getData();
                    getData();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Page<ArrayList<Fruit>>>> call, Throwable t) {
            Log.d(">>>getListFruit", "onFailure" + t.getMessage());
        }
    };

    @Override
    public void Delete(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FruitActivity.this);
        builder.setTitle("Confirm delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            httpRequest.callAPI()
                    .deleteFruit(id)
                    .enqueue(new Callback<Response<Fruit>>() {
                        @Override
                        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus() == 200) {
                                    httpRequest.callAPI()
                                            .getListFruit("Bearer"+token)
                                            .enqueue(new Callback<Response<ArrayList<Fruit>>>() {
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
                                            });
                                    Toast.makeText(FruitActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Response<Fruit>> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage());
                        }
                    });
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
    public void Dentail(String id) {
        httpRequest.callAPI().getFruitById(id).enqueue(new Callback<Response<Fruit>>() {
            @Override
            public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
                Intent intent = new Intent(FruitActivity.this, FruitDentailActivity.class);
                intent.putExtra("fruit", id);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Response<Fruit>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("loadddddd", "onResume: ");
        page = 1;
        list.clear();
        FilterFruit();
    }


}