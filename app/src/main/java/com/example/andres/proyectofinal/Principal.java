package com.example.andres.proyectofinal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.andres.proyectofinal.Adapter.ClienteAdapter;
import com.example.andres.proyectofinal.Camara.Camara;
import com.example.andres.proyectofinal.Cliente.Cliente;
import com.example.andres.proyectofinal.Cliente.Referencias;
import com.example.andres.proyectofinal.Cliente.funciones_cliente;
import com.example.andres.proyectofinal.GPS.gps;
import com.example.andres.proyectofinal.Login.Inicio;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference cliente_REFERENCE;
    Cliente cliente = new Cliente();
    ArrayList<Cliente> lCliente = new ArrayList<Cliente>();
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private FusedLocationProviderClient mFusedLocationClient;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNuevo_MostrarClientes);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevoCliente();
*//*                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*//*
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //gps
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.subirLatLong();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(this, Camara.class));
        } else if (id == R.id.nav_gps) {
            startActivity(new Intent(this, gps.class));
        } else if (id == R.id.nav_logout) {
            desconectarUsuario();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void desconectarUsuario() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        startActivity(new Intent(getBaseContext(), Inicio.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        llenarClientes();
    }

    public void llenarContent(ArrayList<Cliente> nCliente) {
        //Nuevo
        RecyclerView recycler;
        RecyclerView.Adapter adapter;
        RecyclerView.LayoutManager lManager;

        final ArrayList<Cliente> cCliente = nCliente;

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.rcvClientes_MostrarClientes);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new ClienteAdapter(cCliente);
        recycler.setAdapter(adapter);

        //Para seleccionar item de recycler

        final GestureDetector mGestureDetector = new GestureDetector(Principal.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        recycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {
            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                try {
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                    if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                        //almacena el item seleccionado del recyclerView
                        int position = recyclerView.getChildAdapterPosition(child);
                        //crear cliente con los datos del item seleccionado del recycler
                        Cliente clienteSeleccionado = cCliente.get(position);
                        if (EnviarDatosCliente(clienteSeleccionado)) ;
                        {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recycler, MotionEvent motionEvent) {
            }
        });

    }

    public boolean EnviarDatosCliente(final Cliente pCliente) {
        startActivity(new Intent(Principal.this, funciones_cliente.class)
                .putExtra("cedula", pCliente.getCedula())
                .putExtra("nombre", pCliente.getNombre())
                .putExtra("telefono", pCliente.getTelefono())
                .putExtra("direccion", pCliente.getDireccion())
                .putExtra("correo", pCliente.getCorreo()));
        return true;
    }

    public void nuevoCliente(View view) {
        startActivity(new Intent(this, funciones_cliente.class));
    }

    public void llenarClientes() {
        lCliente.clear();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        cliente_REFERENCE = database.getReference(Referencias.CLIENTE_REFERENCE);

        cliente_REFERENCE.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                cliente = dataSnapshot.getValue(Cliente.class);
                lCliente.add(cliente);
                llenarContent(lCliente);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                cliente = dataSnapshot.getValue(Cliente.class);
                lCliente.add(cliente);
                llenarContent(lCliente);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                cliente = dataSnapshot.getValue(Cliente.class);
                lCliente.add(cliente);
                llenarContent(lCliente);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                cliente = dataSnapshot.getValue(Cliente.class);
                lCliente.add(cliente);
                llenarContent(lCliente);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void subirLatLong() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Principal.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    Log.e("Latitud", +location.getLatitude() + "Longitud: " + location.getLongitude());
                    Map<String, Object> latlang = new HashMap<>();
                    latlang.put("Latitud", location.getLatitude());
                    latlang.put("Longitud", location.getLongitude());
                    mDatabase.child("informacion").push().setValue(latlang);
                }
            }
        });
    }
}
