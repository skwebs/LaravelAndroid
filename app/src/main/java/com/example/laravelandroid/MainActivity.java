package com.example.laravelandroid;

import static com.example.laravelandroid.Constants.API_BASE_URL;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

//        variables declaration
    TextInputLayout etEmail, etPassword;
    Button btnLogin, btnRegister, btnForget, btnLogout;
    String email, password;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    UserSession userSession;
    LinearLayout loginLayout, loggedInLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login | Laravel Android");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // variable initialisation
        userSession = UserSession.getInstance(getApplicationContext());
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        loginLayout = findViewById(R.id.ll_login_layout);
        loggedInLayout = findViewById(R.id.ll_logged_in_layout);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnForget = findViewById(R.id.btnForgot);
        btnLogout = findViewById(R.id.btnLogout);

        if(userSession.isLoggedIn()){
            Toast.makeText(this, "User logged in.", Toast.LENGTH_SHORT).show();
            loggedInInterface(true);
        }else loggedInInterface(false);

        btnLogout.setOnClickListener(v -> {
            userSession.userLogout();
            loggedInInterface(false);
        });

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
        

        
//        validate input data
        if (email.isEmpty()) {
            etEmail.setError("Email is required.");
        } else if (password.isEmpty()) {
            etEmail.setError(null);
            etPassword.setError("Password is required.");
        } else {
            etPassword.setError(null);
            
//            if get valid data then process for authentication
            authenticateLogin();
        }
    }

    private void authenticateLogin() {
//        Show progress dialog until process
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

//        create stringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_BASE_URL+"/login",response -> {

            try {
//                get server response in json object
                JSONObject responseJsonObject = new JSONObject(response);
                boolean status = responseJsonObject.getBoolean("status");
//                if status is true
                if(status){
//                    retrieve "data" from api response
                    JSONObject data = responseJsonObject.getJSONObject("data");

//                    retrieve "token" from api response
                    String token = data.getString("token");

//                    store user "token" in "sharedPreferences"
                    userSession.setUserDetails(true, token);

//                    Logged in interface
                    loggedInInterface(true);
                }
//                if statement end here ----------------------------------------------------

//                    show toast of server response message
                Toast.makeText(this, responseJsonObject.getString("message"), Toast.LENGTH_SHORT).show();

//                show server response
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                
//                log server response
                Log.d("LOGIN", "response: "+response);
            }
//            catch if any error found
            catch (JSONException e){
                Log.d("Login", "error:"+e);
                e.printStackTrace();
            }
//            try catch end here ---------------------------------------------------------

//            dismissed the progress dialog after completing the request
            progressDialog.dismiss();

        }, error -> {
//            dismissed the progress dialog after completing the request
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            Log.d("LOGIN", "error: ");
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

//        add stringRequest in queue
        requestQueue.add(stringRequest);
    }

    private void loggedInInterface(boolean b) {
        if(b){
            loginLayout.setVisibility(View.GONE);
            loggedInLayout.setVisibility(View.VISIBLE);
        }else{
            loggedInLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
        }
    }

}