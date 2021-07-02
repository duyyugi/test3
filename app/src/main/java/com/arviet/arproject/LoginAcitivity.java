package com.arviet.arproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arviet.arproject.api.ApiService;
import com.arviet.arproject.model.Ulti;
import com.arviet.arproject.requestModel.LoginRequest;
import com.arviet.arproject.responseModel.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginAcitivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView txv_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Ulti skb = new Ulti();
        skb.setupUI(findViewById(R.id.login_view),LoginAcitivity.this);
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                if(username.length()==0){
                    edtUsername.requestFocus();
                    edtUsername.setError("Mục này không được để trống");
                } else if (password.length()==0) {
                    edtPassword.requestFocus();
                    edtPassword.setError("Mục này không được để trống");
                }else{
                    LoginRequest loginRequest = new LoginRequest(username,password);
                    Login(loginRequest);
                }
            }
        });
        txv_register = findViewById(R.id.txv_register);
        txv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginAcitivity.this,RegisterActivity.class));
            }
        });
    }

    public void Login(LoginRequest loginRequest){
        ApiService.apiService.Login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
               if (response.code()==200){
                   LoginResponse loginResponse = response.body();
                   if (loginResponse.getStatus().equals("login sucessfully")){
                       String token = loginResponse.getToken();
                       SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginAcitivity.this);
                       preferences.edit().putString("ACCESS_TOKEN",token).apply();
                       preferences.edit().putString("username", loginResponse.getUsername()).apply();
                       LoginAcitivity.this.finish();
                       startActivity(new Intent(LoginAcitivity.this,HomePageActivity.class));
                   } else if (loginResponse.getStatus().equals("ten dang nhap hoac mat khau khong dung")){
                       Toast.makeText(LoginAcitivity.this,"Tên đăng nhập hoặc mật khẩu không đúng",Toast.LENGTH_LONG).show();
                   } else if (loginResponse.getStatus().equals("username is not existed")){
                       edtUsername.requestFocus();
                       edtUsername.setError("Tên đăng nhập không tồn tại");
                   } else if (loginResponse.getStatus().equals("password is incorrect")) {
                       edtPassword.requestFocus();
                       edtPassword.setError("Sai mật khẩu");
                   }
               }else{
                   Toast.makeText(LoginAcitivity.this,"Lỗi không xác định",Toast.LENGTH_SHORT).show();
               }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginAcitivity.this,"Lỗi không xác định",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onBackPressed() {
        //do nothing
    }
}