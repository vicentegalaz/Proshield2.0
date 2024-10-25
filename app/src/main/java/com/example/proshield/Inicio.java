package com.example.proshield;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Inicio extends Fragment {

    public Inicio() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        // Obtener referencias a las imágenes
        ImageView imageButton1 = view.findViewById(R.id.imageButton1);
        ImageView imageButton2 = view.findViewById(R.id.imageButton2); // Escaner
        ImageView imageButton3 = view.findViewById(R.id.imageButton3); // Telefono

        // Establecer los OnClickListener
        imageButton1.setOnClickListener(v -> loadFragment(new Alarma2()));
        imageButton2.setOnClickListener(v -> loadFragment(new Escaner())); // Cambiado a Escaner
        imageButton3.setOnClickListener(v -> loadFragment(new Telefono())); // Cambiado a Telefono

        return view;
    }

    private void loadFragment(Fragment fragment) {
        // Cargar el nuevo fragmento
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment) // Asegúrate de que el ID sea el correcto
                    .addToBackStack(null)
                    .commit();
        }
    }
}
