package com.example.proshield;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistorialEmergencias extends Fragment {

    private RecyclerView recyclerViewHistorial;
    private EmergenciaAdapter adapter;
    private List<Emergencia> listaEmergencias;

    public HistorialEmergencias() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaEmergencias = new ArrayList<>();
        cargarEmergencias();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_emergencias, container, false);

        recyclerViewHistorial = view.findViewById(R.id.recyclerViewHistorial);
        recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EmergenciaAdapter(listaEmergencias);
        recyclerViewHistorial.setAdapter(adapter);

        return view;
    }

    private void cargarEmergencias() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Emergencia");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaEmergencias.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String rutUsuario = snapshot.child("rutUsuario").getValue(String.class);
                    Emergencia emergencia = new Emergencia(nombre, rutUsuario);
                    listaEmergencias.add(emergencia);
                }
                adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores aquí
            }
        });
    }
}
