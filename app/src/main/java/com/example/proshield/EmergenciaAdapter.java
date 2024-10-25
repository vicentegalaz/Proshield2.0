package com.example.proshield;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmergenciaAdapter extends RecyclerView.Adapter<EmergenciaAdapter.ViewHolder> {

    private List<Emergencia> listaEmergencias;

    public EmergenciaAdapter(List<Emergencia> listaEmergencias) {
        this.listaEmergencias = listaEmergencias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emergencia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Emergencia emergencia = listaEmergencias.get(position);
        holder.textViewNombre.setText(emergencia.getNombre());
        holder.textViewRut.setText("RUT: " + emergencia.getRutUsuario());
    }

    @Override
    public int getItemCount() {
        return listaEmergencias.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre;
        TextView textViewRut;

        ViewHolder(View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewRut = itemView.findViewById(R.id.textViewRut);
        }
    }
}
