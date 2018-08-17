package com.example.andres.proyectofinal.Cliente;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres.proyectofinal.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class funciones_cliente extends AppCompatActivity {
    DatabaseReference cliente_REFERENCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funciones_cliente);
        setTitle(getResources().getString(R.string.A_NuevoMostrarClientes));
        final String cedulaSeleccionado = getIntent().getStringExtra("cedula");
        final String nombreSeleccionado = getIntent().getStringExtra("nombre");
        final String telefonoSeleccionado = getIntent().getStringExtra("telefono");
        final String direccionSeleccionado = getIntent().getStringExtra("direccion");
        final String correoSeleccionado = getIntent().getStringExtra("correo");
        llenarInformacion(cedulaSeleccionado, nombreSeleccionado, telefonoSeleccionado, direccionSeleccionado, correoSeleccionado);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        cliente_REFERENCE = database.getReference(Referencias.CLIENTE_REFERENCE);
    }

    public void llenarInformacion(String nCedula, String nNombre, String nTelefono, String nDireccion, String nCorreo) {
        TextView cedula = (TextView) findViewById(R.id.txtCedula_FuncionesCliente);
        cedula.setText(nCedula);
        cedula.setEnabled(true);
        TextView nombre = (TextView) findViewById(R.id.txtNombre_FuncionesCliente);
        nombre.setText(nNombre);
        TextView telefono = (TextView) findViewById(R.id.txtTelefono_FuncionesCliente);
        telefono.setText(nTelefono);
        TextView direccion = (TextView) findViewById(R.id.txtDireccion_FuncionesCliente);
        direccion.setText(nDireccion);
        TextView correo = (TextView) findViewById(R.id.txtCorreo_FuncionesCliente);
        correo.setText(nCorreo);
    }

    public void Guardar(View view) {
        if (Validar("G")) {
            String pCedula = ((TextView) findViewById(R.id.txtCedula_FuncionesCliente)).getText().toString();
            String pNombre = ((TextView) findViewById(R.id.txtNombre_FuncionesCliente)).getText().toString();
            String pTelefono = ((TextView) findViewById(R.id.txtTelefono_FuncionesCliente)).getText().toString();
            String pDireccion = ((TextView) findViewById(R.id.txtDireccion_FuncionesCliente)).getText().toString();
            String pCorreo = ((TextView) findViewById(R.id.txtCorreo_FuncionesCliente)).getText().toString();

            Cliente cliente = new Cliente(pCedula, pNombre, pTelefono, pDireccion, pCorreo);
            cliente_REFERENCE.child(pCedula).setValue(cliente);
            Toast.makeText(this, getResources().getString(R.string.clienteGuardadoCorrectamente), Toast.LENGTH_SHORT).show();
            this.finish();

        } else {
            Toast.makeText(this, getResources().getString(R.string.noGuardoCliente), Toast.LENGTH_SHORT).show();

        }
    }

    public void Eliminar(View view) {
        if (Validar("E")) {
            String pCedula = ((TextView) findViewById(R.id.txtCedula_FuncionesCliente)).getText().toString();
            cliente_REFERENCE.child(pCedula).removeValue();
            Toast.makeText(this, getResources().getString(R.string.clienteEliminadoCorrectamentes), Toast.LENGTH_SHORT).show();
            this.finish();
        } else {
            Toast.makeText(this, getResources().getString(R.string.EliminarCliente), Toast.LENGTH_SHORT).show();
            Limpiar();
        }
    }

    public boolean Validar(String Validacion) {
        if (Validacion == "G") {
            if (((EditText) findViewById(R.id.txtCedula_FuncionesCliente)).getText().toString().equals("")) {
                TextView cedula = (TextView) findViewById(R.id.txtCedula_FuncionesCliente);
                cedula.setError(getResources().getString(R.string.P_CampoObligatorio));
                return false;
            }
            if (((TextView) findViewById(R.id.txtCedula_FuncionesCliente)).length() != 9) {
                TextView cliente = (TextView) findViewById(R.id.txtCedula_FuncionesCliente);
                cliente.setError(getResources().getString(R.string.P_CampoInvalido));
                return false;
            }
            if (((EditText) findViewById(R.id.txtNombre_FuncionesCliente)).getText().toString().equals("")) {
                TextView nombre = (TextView) findViewById(R.id.txtNombre_FuncionesCliente);
                nombre.setError(getResources().getString(R.string.P_CampoObligatorio));
                return false;
            }
            if (((EditText) findViewById(R.id.txtTelefono_FuncionesCliente)).getText().toString().equals("")) {
                TextView telefono = (TextView) findViewById(R.id.txtTelefono_FuncionesCliente);
                telefono.setError(getResources().getString(R.string.P_CampoObligatorio));
                return false;
            }
            if (((EditText) findViewById(R.id.txtTelefono_FuncionesCliente)).length() != 8) {
                TextView telefono = (TextView) findViewById(R.id.txtTelefono_FuncionesCliente);
                telefono.setError(getResources().getString(R.string.P_CampoInvalido));
                return false;
            }
            if (((EditText) findViewById(R.id.txtDireccion_FuncionesCliente)).getText().toString().equals("")) {
                TextView direccion = (TextView) findViewById(R.id.txtDireccion_FuncionesCliente);
                direccion.setError(getResources().getString(R.string.P_CampoObligatorio));
                return false;
            }
            if (((TextView) findViewById(R.id.txtCorreo_FuncionesCliente)).getText().toString().equals("")) {
                TextView correo = (TextView) findViewById(R.id.txtCorreo_FuncionesCliente);
                correo.setError(getResources().getString(R.string.P_CampoObligatorio));
                return false;
            }
        }
        if (Validacion == "E") {
            if (((TextView) findViewById(R.id.txtCedula_FuncionesCliente)).getText().toString().equals("")) {
                TextView cedula = (TextView) findViewById(R.id.txtCedula_FuncionesCliente);
                cedula.setError(getResources().getString(R.string.P_CampoObligatorio));
                return false;
            }
            if (((TextView) findViewById(R.id.txtCedula_FuncionesCliente)).length() != 9) {
                TextView cliente = (TextView) findViewById(R.id.txtCedula_FuncionesCliente);
                cliente.setError(getResources().getString(R.string.P_CampoInvalido));
                return false;
            }
        }
        return true;
    }

    private void Limpiar() {
        ((TextView) findViewById(R.id.txtCedula_FuncionesCliente)).setText("");
        ((TextView) findViewById(R.id.txtNombre_FuncionesCliente)).setText("");
        ((TextView) findViewById(R.id.txtTelefono_FuncionesCliente)).setText("");
        ((TextView) findViewById(R.id.txtDireccion_FuncionesCliente)).setText("");
        ((TextView) findViewById(R.id.txtCorreo_FuncionesCliente)).setText("");

    }
}
