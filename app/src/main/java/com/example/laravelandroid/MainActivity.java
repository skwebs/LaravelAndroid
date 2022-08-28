package com.example.laravelandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    //        variables declaration
    Button btnRegister, btnLogin, btnLogout, btnSignIn;
    UserSession userSession;
    LinearLayout guestLayout, loggedInLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login | Laravel Android");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // variable initialisation
        userSession = UserSession.getInstance(getApplicationContext());
        guestLayout = findViewById(R.id.ll_guest_layout);
        loggedInLayout = findViewById(R.id.ll_logged_in_layout);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);

        btnSignIn= findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        if(userSession.isLoggedIn()){
            Toast.makeText(this, "User logged in.", Toast.LENGTH_SHORT).show();
            loggedInInterface(true);
        }else loggedInInterface(false);

        btnLogout.setOnClickListener(v -> {
            userSession.userLogout();
            loggedInInterface(false);
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

//        event on button
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loggedInInterface(boolean b) {
        if(b){
            guestLayout.setVisibility(View.GONE);
            loggedInLayout.setVisibility(View.VISIBLE);
        }else{
            loggedInLayout.setVisibility(View.GONE);
            guestLayout.setVisibility(View.VISIBLE);
        }
    }

}