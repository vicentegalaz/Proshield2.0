package com.example.proshield;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class Modificar extends Fragment {

    private EditText editTextNombre;
    private EditText editTextContraseña;
    private EditText editTextNumeroContacto;
    private EditText editTextNumeroEmergencia;
    private EditText editTextTrabajo;
    private Spinner spinnerPoseeEnfermedad;
    private Spinner spinnerTipo;
    private Button buttonGuardarCambios;

    private DatabaseReference databaseReference;
    private String usuarioRut; // Suponiendo que este es el RUT del usuario a modificar

    public Modificar() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializar la referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        // Aquí puedes obtener el RUT del usuario si lo estás pasando como argumento
        if (getArguments() != null) {
            usuarioRut = getArguments().getString("usuarioRut");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modificar, container, false);

        // Inicializar los campos
        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextContraseña = view.findViewById(R.id.editTextContraseña);
        editTextNumeroContacto = view.findViewById(R.id.editTextNumeroContacto);
        editTextNumeroEmergencia = view.findViewById(R.id.editTextNumeroEmergencia);
        editTextTrabajo = view.findViewById(R.id.editTextTrabajo);
        spinnerPoseeEnfermedad = view.findViewById(R.id.spinnerPoseeEnfermedad);
        spinnerTipo = view.findViewById(R.id.spinnerTipo);
        // En tu método onCreateView
        spinnerPoseeEnfermedad.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"Sí", "No"}));
        spinnerTipo.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"usuario", "encargado"}));

        buttonGuardarCambios = view.findViewById(R.id.buttonGuardarCambios);

        // Cargar los datos del usuario
        cargarDatosUsuario();

        // Configurar el botón para guardar cambios
        buttonGuardarCambios.setOnClickListener(v -> guardarCambios());

        return view;
    }

    private void cargarDatosUsuario() {
        databaseReference.child(usuarioRut).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    if (usuario != null) {
                        // Establecer los valores en los EditText
                        editTextNombre.setText(usuario.getNombre());
                        editTextContraseña.setText(usuario.getContraseña());
                        editTextNumeroContacto.setText(usuario.getNumeroContacto());
                        editTextNumeroEmergencia.setText(usuario.getNumeroEmergencia());
                        editTextTrabajo.setText(usuario.getTrabajo());

                        // Establecer el índice de los Spinners según los valores del usuario
                        String poseeEnfermedad = usuario.getPoseeEnfermedad();
                        String tipo = usuario.getTipo();

                        // Verificar si los valores son nulos antes de llamar a equals
                        if (poseeEnfermedad != null) {
                            spinnerPoseeEnfermedad.setSelection(poseeEnfermedad.equals("Sí") ? 0 : 1);
                        } else {
                            spinnerPoseeEnfermedad.setSelection(1); // O alguna opción por defecto
                        }

                        if (tipo != null) {
                            spinnerTipo.setSelection(tipo.equals("usuario") ? 0 : 1);
                        } else {
                            spinnerTipo.setSelection(0); // O alguna opción por defecto
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error al cargar datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void guardarCambios() {
        String nombre = editTextNombre.getText().toString().trim();
        String contraseña = editTextContraseña.getText().toString().trim();
        String numeroContacto = editTextNumeroContacto.getText().toString().trim();
        String numeroEmergencia = editTextNumeroEmergencia.getText().toString().trim();
        String trabajo = editTextTrabajo.getText().toString().trim();
        String poseeEnfermedad = spinnerPoseeEnfermedad.getSelectedItem().toString();
        String tipo = spinnerTipo.getSelectedItem().toString();

        // Validar los campos
        if (nombre.isEmpty() || contraseña.isEmpty() || numeroContacto.isEmpty() ||
                numeroEmergencia.isEmpty() || trabajo.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un nuevo objeto Usuario
        Usuario usuarioActualizado = new Usuario(nombre, contraseña, numeroContacto, numeroEmergencia, poseeEnfermedad, tipo, "", trabajo);

        // Guardar los cambios en Firebase
        databaseReference.child(usuarioRut).setValue(usuarioActualizado).addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();

            // Navegar al fragmento Lista
            Fragment listaFragment = new Lista(); // Asegúrate de que 'Lista' es el nombre correcto de tu fragmento
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, listaFragment) // Asegúrate de que 'fragment_container' es el ID correcto
                    .addToBackStack(null) // Esto permite que el usuario regrese al fragmento anterior
                    .commit();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error al actualizar el usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
