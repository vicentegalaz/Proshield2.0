package com.example.proshield;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Alarma2 extends Fragment {

    private Spinner listaAlarma, listaExtintor;
    private Button botonActivarAlarma, botonDesactivarAlarma;
    private Button botonActivarExtintor, botonDesactivarExtintor;

    private DatabaseReference databaseRef;
    private List<Alarma> alarmas;
    private List<Extintores> extintores;

    public Alarma2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarma2, container, false);

        // Inicializar componentes
        listaAlarma = view.findViewById(R.id.lista_alarma);
        listaExtintor = view.findViewById(R.id.lista_extintor);
        botonActivarAlarma = view.findViewById(R.id.boton_activar_alarma);
        botonDesactivarAlarma = view.findViewById(R.id.boton_desactivar_alarma);
        botonActivarExtintor = view.findViewById(R.id.boton_activar_extintor);
        botonDesactivarExtintor = view.findViewById(R.id.boton_desactivar_extintor);

        // Cargar alarmas y extintores
        cargarAlarmas();
        cargarExtintores();

        // Configurar los botones
        botonActivarAlarma.setOnClickListener(v -> activarAlarma());
        botonDesactivarAlarma.setOnClickListener(v -> desactivarAlarma());
        botonActivarExtintor.setOnClickListener(v -> activarExtintor());
        botonDesactivarExtintor.setOnClickListener(v -> desactivarExtintor());

        return view;
    }

    private void cargarAlarmas() {
        databaseRef.child("alarmas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alarmas = new ArrayList<>();
                for (DataSnapshot alarmSnapshot : snapshot.getChildren()) {
                    Alarma alarma = alarmSnapshot.getValue(Alarma.class);
                    alarmas.add(alarma);
                }
                ArrayAdapter<Alarma> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, alarmas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listaAlarma.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar alarmas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarExtintores() {
        databaseRef.child("extintores").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                extintores = new ArrayList<>();
                for (DataSnapshot extintorSnapshot : snapshot.getChildren()) {
                    Extintores extintor = extintorSnapshot.getValue(Extintores.class);
                    extintores.add(extintor);
                }
                ArrayAdapter<Extintores> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, extintores);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listaExtintor.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar extintores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void activarAlarma() {
        Alarma selectedAlarma = (Alarma) listaAlarma.getSelectedItem();
        if (selectedAlarma != null) {
            databaseRef.child("alarmas").child(selectedAlarma.getId()).child("activada").setValue(true);
            Toast.makeText(getContext(), "Alarma activada", Toast.LENGTH_SHORT).show();

            // Mostrar iconos de alarma
            mostrarIconosAlarma(selectedAlarma);
        }
    }

    private void desactivarAlarma() {
        Alarma selectedAlarma = (Alarma) listaAlarma.getSelectedItem();
        if (selectedAlarma != null) {
            databaseRef.child("alarmas").child(selectedAlarma.getId()).child("activada").setValue(false);
            Toast.makeText(getContext(), "Alarma desactivada", Toast.LENGTH_SHORT).show();

            // Ocultar iconos de alarma
            ocultarIconosAlarma();
        }
    }

    private void activarExtintor() {
        Extintores selectedExtintor = (Extintores) listaExtintor.getSelectedItem();
        if (selectedExtintor != null) {
            databaseRef.child("extintores").child(selectedExtintor.getId()).child("activado").setValue(true);
            Toast.makeText(getContext(), "Extintor activado", Toast.LENGTH_SHORT).show();

            // Mostrar iconos de extintor
            mostrarIconosExtintor(selectedExtintor);
        }
    }

    private void desactivarExtintor() {
        Extintores selectedExtintor = (Extintores) listaExtintor.getSelectedItem();
        if (selectedExtintor != null) {
            databaseRef.child("extintores").child(selectedExtintor.getId()).child("activado").setValue(false);
            Toast.makeText(getContext(), "Extintor desactivado", Toast.LENGTH_SHORT).show();

            // Ocultar iconos de extintor
            ocultarIconosExtintor();
        }
    }

    private void mostrarIconosAlarma(Alarma alarma) {
        // Cambiar la visibilidad de los iconos de alarma según la alarma activada
        // Aquí puedes personalizar qué iconos mostrar
        // Ejemplo de activación de icono 1
        ImageView iconoAlarma1 = getView().findViewById(R.id.icono_alarma_1);
        iconoAlarma1.setVisibility(View.VISIBLE);
    }

    private void ocultarIconosAlarma() {
        // Ocultar todos los iconos de alarma
        ImageView iconoAlarma1 = getView().findViewById(R.id.icono_alarma_1);
        iconoAlarma1.setVisibility(View.INVISIBLE);
    }

    private void mostrarIconosExtintor(Extintores extintor) {
        // Cambiar la visibilidad de los iconos de extintor según el extintor activado
        // Ejemplo de activación de icono 1
        ImageView iconoExtintor1 = getView().findViewById(R.id.icono_extintor_1);
        iconoExtintor1.setVisibility(View.VISIBLE);
    }

    private void ocultarIconosExtintor() {
        // Ocultar todos los iconos de extintor
        ImageView iconoExtintor1 = getView().findViewById(R.id.icono_extintor_1);
        iconoExtintor1.setVisibility(View.INVISIBLE);
    }

}
