package com.example.andres.proyectofinal.Sitios;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andres.proyectofinal.Cliente.Referencias;
import com.example.andres.proyectofinal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Sitios extends AppCompatActivity {

    private DatabaseReference infoReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitios);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        infoReference = database.getReference(Referencias.INFO_REFERENCE);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Sitios.this, "Error leyendo datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registrar(View view){
        int id = Integer.parseInt(((EditText)findViewById(R.id.etId)).getText().toString());
        String nombre = ((EditText)findViewById(R.id.etNombre)).getText().toString();
        String tipo = ((EditText)findViewById(R.id.etTipo)).getText().toString();
        String ubicacion = ((EditText)findViewById(R.id.etUbicacion)).getText().toString();
        String latitud = ((EditText)findViewById(R.id.etLatitud)).getText().toString();
        String longitud = ((EditText)findViewById(R.id.etLongitud)).getText().toString();
        String comenta = ((EditText)findViewById(R.id.etComent)).getText().toString();
        int evalua = Integer.parseInt(((EditText)findViewById(R.id.etPuntos)).getText().toString());
        Lugar lugar = new Lugar(id,nombre,tipo,ubicacion,latitud,longitud,comenta,evalua);
        infoReference.child(Referencias.SITIOS_REFERENCE).push().setValue(lugar);

        Toast.makeText(Sitios.this, "Los datos se guardaron..", Toast.LENGTH_SHORT).show();
    }

    public void consulta(View view){
        /*infoReference.child(References.SITIOS_REFERENCE).push().setValue(lugar);
        int id;
        String nombre;
        String tipo;
        String ubicacion;
        String latitud;
        String longitud;
        String comenta;
        int evalua;

        Lugar lugar = dataSnapshot.getValue(Lugar.class);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String  = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/

    }


    // Obtener la instacia en Firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    // Crear una referencia de base de datos... que se va llamar numero
    private DatabaseReference myRef = database.getReference("id"); //numero


    /*public void actualizar(View view ){ // Write a message to the database
        String numero=((TextView)findViewById(R.id.etId)).getText().toString();
        myRef.setValue(numero);
    }*/
}

