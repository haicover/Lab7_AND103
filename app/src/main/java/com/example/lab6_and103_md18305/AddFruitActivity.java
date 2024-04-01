package com.example.lab5_and103_md18305;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.lab5_and103_md18305.adapter.ImageAdapter;
import com.example.lab5_and103_md18305.model.Distributor;
import com.example.lab5_and103_md18305.model.Fruit;
import com.example.lab5_and103_md18305.model.Response;
import com.example.lab5_and103_md18305.services.HttpRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class AddFruitActivity extends AppCompatActivity {
    private HttpRequest httpRequest;
    private Spinner sp_distributor;
    private String id_distributor;
    private String token;
    private RecyclerView avatarRecyclerView;
    private ImageAdapter adapter;
    private ArrayList<File> ds_image = new ArrayList<>();
    private ArrayList<Fruit> list = new ArrayList<>();
    private ArrayList<Distributor> distributorArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fruit);

        httpRequest = new HttpRequest();
        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextQuantity = findViewById(R.id.editTextQuantity);
        EditText editTextPrice = findViewById(R.id.editTextPrice);
        EditText editTextStatus = findViewById(R.id.editTextStatus);
        EditText editTextDescription = findViewById(R.id.editTextDescription);
        Button buttonUpload = findViewById(R.id.buttonUpload);
        Button buttonAdd = findViewById(R.id.buttonAdd);

        avatarRecyclerView = findViewById(R.id.avatarRecyclerView);
        sp_distributor = findViewById(R.id.sp_distributor);
        httpRequest.callAPI().getListDistributor().enqueue(new Callback<Response<ArrayList<Distributor>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        distributorArrayList = response.body().getData();
                        String[] items = new String[distributorArrayList.size()];

                        for (int i = 0; i < distributorArrayList.size(); i++) {
                            items[i] = distributorArrayList.get(i).getName();
                        }
                        ArrayAdapter<String> adapterSpin = new ArrayAdapter<>(AddFruitActivity.this, android.R.layout.simple_spinner_item, items);
                        adapterSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_distributor.setAdapter(adapterSpin);
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable t) {

            }
        });

        sp_distributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_distributor = distributorArrayList.get(i).getId();
                Log.d("123123", "onItemSelected" + id_distributor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sp_distributor.setSelection(0);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, RequestBody> mapRequestBody = new HashMap<>();
                String _name = editTextName.getText().toString();
                String _quantity = editTextQuantity.getText().toString();
                String _price = editTextPrice.getText().toString();
                String _status = editTextStatus.getText().toString();
                String _description = editTextDescription.getText().toString();
                //Put request body
                mapRequestBody.put("name", getRequestBody(_name));
                mapRequestBody.put("quantity", getRequestBody(_quantity));
                mapRequestBody.put("price", getRequestBody(_price));
                mapRequestBody.put("status", getRequestBody(_status));
                mapRequestBody.put("description", getRequestBody(_description));
                mapRequestBody.put("id_distributor", getRequestBody(id_distributor));

                ArrayList<MultipartBody.Part> _ds_image = new ArrayList<>();
                ds_image.forEach(file -> {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
                    _ds_image.add(part);
                });
                String name = editTextName.getText().toString().trim();
                String quantity = editTextQuantity.getText().toString().trim();
                String price = editTextPrice.getText().toString().trim();
                String status = editTextStatus.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();

                if (name.isEmpty() || quantity.isEmpty() || price.isEmpty() || status.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AddFruitActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Chuyển đổi các giá trị string thành số nguyên
                int quantityStr = Integer.parseInt(quantity);
                int priceStr = Integer.parseInt(price);
                int statusStr = Integer.parseInt(status);
                Fruit fruit = new Fruit();
                fruit.setName(name);
                fruit.setQuantity(quantityStr);
                fruit.setPrice(priceStr);
                fruit.setStatus(statusStr);
                fruit.setDescription(description);

                httpRequest.callAPI().addFruitWithFileImage(mapRequestBody, _ds_image).enqueue(responseFruit);
            }
        });
    }

    private void chooseImage() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            getImage.launch(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            if (data.getClipData() != null) {
                                int itemCount = data.getClipData().getItemCount();
                                for (int i = 0; i < itemCount; i++) {
                                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                    File file1 = createFileFromUri(imageUri, "" + itemCount);
                                    ds_image.add(file1);
                                }
                                setViewListHinh(ds_image);
                            }
                        } else {
                            Uri imagePath = data.getData();
                            File file = createFileFromUri(imagePath, "");
                            ds_image.add(file);
                            setViewListHinh(ds_image);
                        }
                    }
                }
            });

    private File createFileFromUri(Uri path, String name) {
        File file1 = new File(AddFruitActivity.this.getCacheDir(), name + ".png");
        try {
            InputStream in = AddFruitActivity.this.getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(file1);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            return file1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void setViewListHinh(ArrayList<File> _ds_image) {
        adapter = new ImageAdapter(_ds_image, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        avatarRecyclerView.setLayoutManager(linearLayoutManager);
        avatarRecyclerView.setAdapter(adapter);
    }

    private RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

    Callback<Response<Fruit>> responseFruit = new Callback<Response<Fruit>>() {
        @Override
        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    Toast.makeText(AddFruitActivity.this, "Thêm fruit thành công", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(AddFruitActivity.this, FruitActivity.class));
                } else {
                    // Xử lý khi thêm fruit không thành công
                    Toast.makeText(AddFruitActivity.this, "Thêm fruit thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Fruit>> call, Throwable t) {
            Toast.makeText(AddFruitActivity.this, "Đã xảy ra lỗi khi gọi API", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        httpRequest.callAPI().getListFruit("Bearer" + token).enqueue(new Callback<Response<ArrayList<Fruit>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
                if (response.isSuccessful()) {
                    Response<ArrayList<Fruit>> responseBody = response.body();
                    if (responseBody != null) {
                        ArrayList<Fruit> fruits = responseBody.getData();
                        // Xử lý danh sách trái cây ở đây
                    }
                } else {
                    // Xử lý khi có lỗi trong phản hồi từ máy chủ
                    Toast.makeText(AddFruitActivity.this, "Có lỗi xảy ra khi lấy danh sách trái cây", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {
                Toast.makeText(AddFruitActivity.this, "Đã xảy ra lỗi khi gọi API", Toast.LENGTH_SHORT).show();
            }
        });
    }

}