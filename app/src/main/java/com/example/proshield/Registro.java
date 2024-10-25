package com.example.proshield;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class Registro extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1; // Código para la solicitud de imagen

    private EditText editTextNombre;
    private EditText editTextContraseña;
    private EditText editTextRUT;
    private EditText editTextNumeroContacto;
    private EditText editTextNumeroEmergencia;
    private EditText editTextTrabajo; // Nuevo campo para el trabajo
    private Spinner spinnerPoseeEnfermedad;
    private Spinner spinnerTipo;
    private Button buttonRegistro;
    private ImageView imageViewProfile; // Agregar la referencia a ImageView

    private Uri imageUri; // Variable para almacenar la URI de la imagen seleccionada

    private DatabaseReference databaseReference;

    public Registro() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializar la referencia de la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        // Inicializar las vistas
        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextContraseña = view.findViewById(R.id.editTextContraseña);
        editTextRUT = view.findViewById(R.id.editTextRUT);
        editTextNumeroContacto = view.findViewById(R.id.editTextNumeroContacto);
        editTextNumeroEmergencia = view.findViewById(R.id.editTextNumeroEmergencia);
        editTextTrabajo = view.findViewById(R.id.editTextTrabajo); // Inicializar EditText para trabajo
        spinnerPoseeEnfermedad = view.findViewById(R.id.spinnerPoseeEnfermedad);
        spinnerTipo = view.findViewById(R.id.spinnerTipo);
        buttonRegistro = view.findViewById(R.id.buttonRegistro);
        imageViewProfile = view.findViewById(R.id.imageViewProfile); // Inicializar ImageView

        // Configurar el Spinner para "Posee Enfermedad"
        String[] opcionesEnfermedad = {"Sí", "No"};
        ArrayAdapter<String> adapterEnfermedad = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, opcionesEnfermedad);
        adapterEnfermedad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPoseeEnfermedad.setAdapter(adapterEnfermedad);

        // Configurar el Spinner para "Tipo de Usuario"
        String[] opcionesTipo = {"Usuario", "Encargado"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, opcionesTipo);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        // Configurar el evento de clic para seleccionar la imagen del perfil
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(); // Llamar al método para abrir la galería
            }
        });

        // Configurar el botón de registro
        buttonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        return view;
    }

    // Método para abrir la galería
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null && data.getData() != null) {
            imageUri = data.getData(); // Obtener la URI de la imagen seleccionada
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imageViewProfile.setImageBitmap(bitmap); // Establecer la imagen seleccionada en el ImageView
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registrarUsuario() {
        String nombre = editTextNombre.getText().toString().trim();
        String contraseña = editTextContraseña.getText().toString().trim();
        String rut = editTextRUT.getText().toString().trim().replace(".", "").replace("-", ""); // Limpiar el RUT
        String numeroContacto = editTextNumeroContacto.getText().toString().trim();
        String numeroEmergencia = editTextNumeroEmergencia.getText().toString().trim();
        String trabajo = editTextTrabajo.getText().toString().trim(); // Obtener el trabajo ingresado
        String poseeEnfermedad = spinnerPoseeEnfermedad.getSelectedItem().toString();
        String tipo = spinnerTipo.getSelectedItem().toString();

        // Validar que los campos no estén vacíos
        if (nombre.isEmpty() || contraseña.isEmpty() || rut.isEmpty() ||
                numeroContacto.isEmpty() || numeroEmergencia.isEmpty() || trabajo.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar el RUT
        if (!validarRut(rut)) {
            Toast.makeText(getActivity(), "RUT inválido. Por favor verifica.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Subir la imagen al Storage de Firebase y obtener la URL
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("perfil/" + rut + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Crear un objeto Usuario con la URL de la foto de perfil y el trabajo
                            Usuario usuario = new Usuario(nombre, contraseña, numeroContacto, numeroEmergencia,
                                    poseeEnfermedad, tipo, uri.toString(), trabajo); // Incluir el trabajo

                            // Guardar el usuario en la base de datos utilizando el RUT como ID
                            databaseReference.child(rut).setValue(usuario)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                                            // Limpiar los campos después del registro
                                            limpiarCampos();
                                        } else {
                                            Toast.makeText(getActivity(), "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al subir la imagen", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getActivity(), "Por favor selecciona una imagen", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para validar el RUT
    private boolean validarRut(String rut) {
        // Verificar que no esté vacío y que no contenga caracteres no permitidos
        if (rut.isEmpty()) {
            return false; // RUT vacío
        }

        // Comprobar que el RUT solo contenga dígitos
        for (char c : rut.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false; // RUT contiene caracteres no numéricos
            }
        }

        // Puedes agregar más lógica aquí si es necesario
        // Por ejemplo, verificar la longitud mínima y máxima
        return rut.length() >= 7 && rut.length() <= 12; // Ajusta según tus necesidades
    }

    private void limpiarCampos() {
        editTextNombre.setText("");
        editTextContraseña.setText("");
        editTextRUT.setText("");
        editTextNumeroContacto.setText("");
        editTextNumeroEmergencia.setText("");
        editTextTrabajo.setText(""); // Limpiar el campo de trabajo
        spinnerPoseeEnfermedad.setSelection(0); // Restablecer al primer elemento
        spinnerTipo.setSelection(0); // Restablecer al primer elemento
        imageViewProfile.setImageResource(R.mipmap.ic_launcher); // Restablecer la imagen de perfil
    }
}
