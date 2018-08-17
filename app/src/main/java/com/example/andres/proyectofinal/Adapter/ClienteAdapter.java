package com.example.andres.proyectofinal.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andres.proyectofinal.Cliente.Cliente;
import com.example.andres.proyectofinal.R;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> lCliente;

    public ClienteAdapter(List<Cliente> items) {
        this.lCliente = items;
    }

    @Override
    public int getItemCount() {
        return lCliente.size();
    }

    @Override
    public ClienteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.presentador_cliente, viewGroup, false);
        return new ClienteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ClienteViewHolder viewHolder, int i) {
        viewHolder.cedula.setText(viewHolder.cedula.getText() + " " + lCliente.get(i).getCedula());
        viewHolder.nombre.setText(viewHolder.nombre.getText() + " " + lCliente.get(i).getNombre());
        viewHolder.telefono.setText(viewHolder.telefono.getText() + " " + lCliente.get(i).getTelefono());
    }

    public static class ClienteViewHolder extends RecyclerView.ViewHolder {
        public TextView cedula;
        public TextView nombre;
        public TextView telefono;

        public ClienteViewHolder(View v) {
            super(v);
            cedula = (TextView) v.findViewById(R.id.txvCedula_PresentadorCliente);
            nombre = (TextView) v.findViewById(R.id.txvNombre_PresentadorCliente);
            telefono = (TextView) v.findViewById(R.id.txvTelefono_PresentadorCliente);
        }
    }
}
