package com.example.andres.proyectofinal.Login;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres.proyectofinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class telefono extends ActivityBase implements View.OnClickListener {

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    //Para la autenticacion
    private FirebaseAuth mAuth;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ViewGroup mPhoneNumberViews;
    private ViewGroup mSignedInViews;

    private TextView mStatusText;

    private EditText mPhoneNumberField;
    private EditText mVerificationField;

    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;
    private Button mSignOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telefono);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        // Asignar los objetos de la vista
        mPhoneNumberViews = (ViewGroup) findViewById(R.id.phone_auth_fields);
        mSignedInViews = (ViewGroup) findViewById(R.id.signed_in_buttons);

        mStatusText = (TextView) findViewById(R.id.status);

        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);

        mStartButton = (Button) findViewById(R.id.button_start_verification);
        mVerifyButton = (Button) findViewById(R.id.button_verify_phone);
        mResendButton = (Button) findViewById(R.id.button_resend);
        mSignOutButton = (Button) findViewById(R.id.sign_out_button);

        // Se hace asi para poder pasar parametros
        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);

        //Se toma la instancia de autenticacion
        mAuth = FirebaseAuth.getInstance();

        //Se definen los metodos para cuando cambia el estado de autenciacion
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // Pueden darse 2 casos
                // 1 - Verificacion instantanea.  No se tiene que enviar el codigo
                // 2 - Auto-recuperacion. Recibido el codigo, no se tiene que digitar
                Toast.makeText(telefono.this, getResources().getString(R.string.Secompletalaautenticacion) + credential, Toast.LENGTH_SHORT).show();
                mVerificationInProgress = false;

                // Se actualiza el layout
                updateUI(STATE_VERIFY_SUCCESS, credential);

                signInWithPhoneAuthCredential(credential);


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                //si fallo la autenticacion
                Toast.makeText(telefono.this, getResources().getString(R.string.autenticacionFallida), Toast.LENGTH_SHORT).show();

                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberField.setError(getResources().getString(R.string.numeroInvalido));
                } else if (e instanceof FirebaseTooManyRequestsException) {   //se llego a la cuota de mensajes de texto
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.noSePuedeEnviarMsg), Snackbar.LENGTH_SHORT).show();
                }

                updateUI(STATE_VERIFY_FAILED);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Se envia el codigo SMS al numero y el usuario debe escribir el codigo
                Toast.makeText(telefono.this, getResources().getString(R.string.codigoEnviado) + " " + verificationId, Toast.LENGTH_SHORT).show();

                // Se almacena el codigo de verificacion y el token.
                mVerificationId = verificationId;
                mResendToken = token;

                updateUI(STATE_CODE_SENT);
            }
        };
    }

    // Se valida si estÃ¡ auntenticado
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mPhoneNumberField.getText().toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        //Autenticacion por telefono
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // numero
                60,                 // duracion
                TimeUnit.SECONDS,   // unidad de duracion (1 minuto)
                this,               // El activity actual
                mCallbacks);        // OnVerificationStateChangedCallbacks

        mVerificationInProgress = true;
    }

    //validar el codigo recibido
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    //reenviar un cÃ³digo (OJO ES DIFERENTE)
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);             // PARA FORZAR EL ENVIO
    }

    // autenticando ya con las credenciales del telefono
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(telefono.this, R.string.autenticado, Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                            finish();
                        } else {
                            Toast.makeText(telefono.this, R.string.Faltalaautentificacion, Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mVerificationField.setError(getResources().getString(R.string.codigoInvalido));
                            }
                            updateUI(STATE_SIGNIN_FAILED);
                        }
                    }
                });
    }

    //desconectarse
    private void signOut() {
        mAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                //activar solo el numero de telefono y el boton de enviar codigo
                enableViews(mStartButton, mPhoneNumberField);
                disableViews(mVerifyButton, mResendButton, mVerificationField);
                break;
            case STATE_CODE_SENT:
                //activar el registro del codigo
                enableViews(mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField);
                disableViews(mStartButton);
                break;
            case STATE_VERIFY_FAILED:
                //algo fallo se activan todas
                enableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField);
                break;
            case STATE_VERIFY_SUCCESS:
                //se diÃ³ la verificacion OK
                disableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField);

                //tomar las credenciales
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        mVerificationField.setText(cred.getSmsCode());
                    } else {
                        mVerificationField.setText(R.string.instant_validation);
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                Toast.makeText(this, R.string.status_sign_in_failed, Toast.LENGTH_SHORT).show();
                break;
            case STATE_SIGNIN_SUCCESS:
                break;
        }

        if (user == null) {
            mPhoneNumberViews.setVisibility(View.VISIBLE);
            mSignedInViews.setVisibility(View.GONE);
            mStatusText.setText(R.string.signed_out);
            ;
        } else {
            mPhoneNumberViews.setVisibility(View.GONE);
            mSignedInViews.setVisibility(View.VISIBLE);
            enableViews(mPhoneNumberField, mVerificationField);
            mPhoneNumberField.setText(null);
            mVerificationField.setText(null);
            mStatusText.setText(R.string.signed_in);
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError(getResources().getString(R.string.numeroInvalidos));
            return false;
        }
        return true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification(mPhoneNumberField.getText().toString());
                break;
            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError(getResources().getString(R.string.P_CampoObligatorio));
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

}
