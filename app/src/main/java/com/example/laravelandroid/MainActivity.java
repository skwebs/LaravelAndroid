package com.example.laravelandroid;

import static com.example.laravelandroid.Constants.API_BASE_URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    //    variables declaration
    TextInputLayout etEmail, etPassword;
    Button btnLogin, btnRegister, btnForget;
    String email, password;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login | Laravel Android");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // variable initialisation
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnForget = findViewById(R.id.btnForgot);



        sharedPref = getSharedPreferences("session", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);
//        event on button

        btnLogin.setOnClickListener(v -> validEditInputText());

        btnForget.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgetActivity.class);
            startActivity(intent);
        });
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void validEditInputText() {
        email = Objects.requireNonNull(etEmail.getEditText()).getText().toString().trim();
        password = Objects.requireNonNull(etPassword.getEditText()).getText().toString().trim();
        if (email.isEmpty()) {
            etEmail.setError("Email is required.");
        } else if (password.isEmpty()) {
            etEmail.setError(null);
            etPassword.setError("Password is required.");
        } else {
            etPassword.setError(null);
            authenticateLogin();
        }
    }

    private void authenticateLogin() {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_BASE_URL+"/login",response -> {

            try {
                JSONObject responseJsonObject = new JSONObject(response);
                boolean status = responseJsonObject.getBoolean("status");
                String message = responseJsonObject.getString("message");
                JSONObject data = responseJsonObject.getJSONObject("data");
                String token = data.getString("token");
                Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
                Log.d("Login", "data: "+data);

                Log.d("Login", "status:"+status);
                Log.d("Login", "message:"+message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }catch (JSONException e){
                Log.d("Login", "error:"+e);
                e.printStackTrace();
            }

            Log.d("Login", "authenticateLogin: "+response);
            Objects.requireNonNull(etEmail.getEditText()).clearComposingText();
            Objects.requireNonNull(etPassword.getEditText()).clearComposingText();
            Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }, error -> {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
        }){
            @NonNull
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<>();

                // Adding All values to Params.
                // The firs argument should be same sa your MySQL database table columns.

                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
//        stringRequest ended here
        requestQueue.add(stringRequest);
    }
}