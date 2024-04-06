package com.example.lab5_and103_md18305;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab5_and103_md18305.model.Response;
import com.example.lab5_and103_md18305.model.User;
import com.example.lab5_and103_md18305.services.HttpRequest;
import retrofit2.Call;
import retrofit2.Callback;



public class LoginActivity extends AppCompatActivity {
    EditText username,password;
    Button btnLogin;
    TextView tvdangki;
    private HttpRequest httpRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        httpRequest = new HttpRequest();
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        tvdangki = findViewById(R.id.tvdangki);

        tvdangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                String _username = username.getText().toString();
                String _password = password.getText().toString();
                user.setUsername(_username);
                user.setPassword(_password);
                httpRequest.callAPI().login(user).enqueue(new Callback<Response<User>>() {
                    @Override
                    public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                        if(response.isSuccessful()){
                            if(response.body().getStatus() == 200){
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPreferences = getSharedPreferences("INFO",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("token",response.body().getToken());
                                editor.putString("refreshToken",response.body().getRefreshToken());
                                editor.putString("id",response.body().getData().get_id());
                                editor.apply();
                                startActivity(new Intent(LoginActivity.this, FruitActivity.class));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Response<User>> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
