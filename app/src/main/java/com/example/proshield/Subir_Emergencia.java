package com.example.proshield;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class Subir_Emergencia extends Fragment {

    private EditText editTextNombre, editTextDescripcion;
    private Spinner spinnerTipoEmergencia;
    private Button buttonSubirEmergencia, buttonSubirImagen;

    private DatabaseReference databaseRef;
    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseRef = FirebaseDatabase.getInstance().getReference("Emergencia");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subir__emergencia, container, false);

        // Inicializar componentes
        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextDescripcion = view.findViewById(R.id.editTextDescripcion);
        spinnerTipoEmergencia = view.findViewById(R.id.spinnerTipoEmergencia);
        buttonSubirEmergencia = view.findViewById(R.id.buttonSubirEmergencia);
        buttonSubirImagen = view.findViewById(R.id.buttonSubirImagen);

        // Configurar Spinner con tipos de emergencia
        String[] tiposEmergencia = {
                "Accidente de tráfico", "Incendio", "Desastre natural",
                "Enfermedad repentina", "Corte de energía", "Problemas estructurales",
                "Explosión", "Tiroteo", "Desaparición", "Emergencia médica",
                "Contaminación", "Pérdida de agua", "Envenenamiento", "Fuga de gas",
                "Accidente doméstico", "Robo", "Amenaza de bomba", "Inundación",
                "Desplazamiento forzado", "Otra"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tiposEmergencia);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoEmergencia.setAdapter(adapter);

        // Configurar botón para subir imagen
        buttonSubirImagen.setOnClickListener(v -> seleccionarImagen());

        // Configurar botón para subir emergencia
        buttonSubirEmergencia.setOnClickListener(v -> subirEmergencia());

        return view;
    }

    private void seleccionarImagen() {
        // Método para seleccionar una imagen de la galería
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            Toast.makeText(getContext(), "Imagen seleccionada", Toast.LENGTH_SHORT).show();
        }
    }

    private void subirEmergencia() {
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String tipoEmergencia = spinnerTipoEmergencia.getSelectedItem().toString();

        // Obtener el RUT desde SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("ProShieldPrefs", getActivity().MODE_PRIVATE);
        String rut = preferences.getString("user_rut", null); // Leer el RUT

        if (nombre.isEmpty() || descripcion.isEmpty() || rut == null) {
            Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String idEmergencia = databaseRef.push().getKey(); // Generar una nueva clave para la emergencia
        HashMap<String, Object> emergenciaData = new HashMap<>();
        emergenciaData.put("id", idEmergencia);
        emergenciaData.put("nombre", nombre);
        emergenciaData.put("descripcion", descripcion);
        emergenciaData.put("tipo", tipoEmergencia);
        emergenciaData.put("rutUsuario", rut); // Guardar el RUT del usuario que reporta la emergencia

        if (imageUri != null) {
            // Subir la imagen a Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("Emergencias/" + idEmergencia);
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(getContext(), "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
                // Guardar la URL de la imagen en la base de datos
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    emergenciaData.put("imagenUrl", uri.toString());
                    databaseRef.child(idEmergencia).setValue(emergenciaData)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Emergencia subida exitosamente", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show());
        } else {
            // Si no hay imagen, subir solo los datos de la emergencia
            databaseRef.child(idEmergencia).setValue(emergenciaData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Emergencia subida exitosamente", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
