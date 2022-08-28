package com.example.laravelandroid;
import static com.example.laravelandroid.Constants.API_BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    UserSession userSession;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    TextInputLayout etName;
    TextInputLayout etEmail;
    TextInputLayout etPassword;
    TextInputLayout etConfirmation;
    Button btnRegister;
    String name;
    String email;
    String password;
    String confirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userSession = UserSession.getInstance(getApplicationContext());
        progressDialog = new ProgressDialog(this);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmation = findViewById(R.id.etConfirmation);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> validateUserData());

    }

    private void validateUserData() {

        name = String.valueOf(Objects.requireNonNull(etName.getEditText()).getText()).trim();
        email = String.valueOf(Objects.requireNonNull(etEmail.getEditText()).getText()).trim();
        password = String.valueOf(Objects.requireNonNull(etPassword.getEditText()).getText()).trim();
        confirmation = String.valueOf(Objects.requireNonNull(etConfirmation.getEditText()).getText()).trim();

        if (name.isEmpty()) {
            alertFail("Name is required.");
        } else if (email.isEmpty()) {
            alertFail("Email is required.");
        } else if (password.isEmpty()) {
            alertFail("Password is required.");
        } else if (confirmation.isEmpty()) {
            alertFail("Confirm Password is required.");
        } else if (!password.equals(confirmation)) {
            alertFail("Password and Confirm Password doesn't match.");
        } else {
            registerUser();
        }
    }

    private void registerUser() {
// Showing progress dialog at user registration time.
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

//        create string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_BASE_URL+"/register", response -> {
            try {
                JSONObject responseJsonObject = new JSONObject(response);

                if (responseJsonObject.getBoolean("status")){
                    JSONObject data = responseJsonObject.getJSONObject("data");
                    userSession.setUserDetails(true,responseJsonObject.getString("token"));
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                Log.d("REGISTER", "response: "+response);
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.d("REGISTER", "error: "+e);
                e.printStackTrace();
            }

//            dismissed the processDialog after completing request
            progressDialog.dismiss();

        }, error -> {
            Log.d("REGISTER", "error: " + error);
            Toast.makeText(this, (CharSequence) error, Toast.LENGTH_SHORT).show();

//           dismissed the processDialog after completing request
            progressDialog.dismiss();
        }){
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                // Creating Map String Params.
                Map<String, String> params = new HashMap<>();

                // Adding All values to Params.
                // The firs argument should be same sa your MySQL database table columns.
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("password_confirmation", confirmation);

                return params;
            }
        };
//        string request ended here

//        add stringRequest in requestQueue
        requestQueue.add(stringRequest);
    }

    private void alertFail(String s) {
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setMessage(s)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
    }

}