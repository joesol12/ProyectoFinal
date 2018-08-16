package com.example.andres.proyectofinal.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.andres.proyectofinal.Principal;
import com.example.andres.proyectofinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
    }

    @Override
    public void onResume(){
        super.onResume();
        validarUsuario();
    }

    public void onClickCorreo(View view) {
        startActivity(new Intent(this, correo.class));
    }

    public void onClickGoogle(View view) {

        startActivity(new Intent(this, google.class));
    }

    public void onClickTelefono(View view) {
        startActivity(new Intent(this, telefono.class));
    }

    public void validarUsuario() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){
            Toast.makeText(this, getResources().getString(R.string.NecesarioAutenticarse), Toast.LENGTH_LONG).show();
        } else {
            startActivity(new Intent(getBaseContext(), Principal.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }
}
