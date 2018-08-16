package com.example.andres.proyectofinal.Login;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres.proyectofinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class correo extends ActivityBase implements View.OnClickListener {

    private TextView mStatusTextView;
    private EditText mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth mAuth; // para hacer a autenticación en Firebase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correo);

        // tomando de la vista/layout
        mStatusTextView = (TextView) findViewById(R.id.status);
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        // Botones
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance(); // se toma la instancia para autenticar

    }

    // Se valida si el usuario está "conectado"
    @Override
    public void onStart() {
        super.onStart();
        // se Revisa si el usuario está autenticado y se actualiza la info
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    //Proceso para crear un usuario (correo/contraseña)
    private void createAccount(String email, String password) {
        Toast.makeText(this, getResources().getString(R.string.creandoCuenta) + email, Toast.LENGTH_SHORT).show();
        if (!validateForm()) { //se valida que la inf esté (correo y contraseña)
            return;
        }
        showProgressDialog();  //ojo que este llamado es de la clabe base....

        // se intenta crear el usuario
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Si el usuario se logró crear se actualiza la info
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.usuarioCreadoOk), Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            finish();

                        } else {
                            //Si falló la creación se informa y se actualiza
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.falloCreacion), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        hideProgressDialog();


                    }
                });
    }

    //Autenticar un usuario ya registrado
    private void signIn(String email, String password) {
        if (!validateForm()) {  //se valida que la información este (correo/contraseña)
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.conectandoUsuario) + email, Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        // se inicia la autenticación
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Se autenticó y se actualiza la info
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.usuarioAutenticadoOk), Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            finish();

                        } else {
                            // No se autenticó y se actualiza la info
                            Toast.makeText(getApplicationContext(), R.string.auth_failed, Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                    }
                });

    }


    //se desconecta el usuario
    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    //Enviar un correo de verificación
    private void sendEmailVerification() {
        // Deshabilitar el boton
        findViewById(R.id.verify_email_button).setEnabled(false);

        // Enviar un correo para verificar usuario
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //se habilita el boton para verificar
                        findViewById(R.id.verify_email_button).setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.correoEnviado) + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.falloCorreoEnviado), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Este método básicamente  valida que la información se digitara bien
    private boolean validateForm() {
        boolean valid = true;
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getResources().getString(R.string.P_CampoObligatorio));
            valid = false;
        } else {
            mEmailField.setError(null);
        }
        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getResources().getString(R.string.P_CampoObligatorio));
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        return valid;
    }

    //este método básicamente actualiza la información en el layout
    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.correo_contrasena_status_fmt, user.getEmail(), user.isEmailVerified()));

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);

            findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }

    //Ojo que se implementó así para poder pasar por parámetros la información
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        }
    }
}
