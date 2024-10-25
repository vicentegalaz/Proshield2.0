package com.example.proshield;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide; // Asegúrate de haber añadido Glide a tu proyecto
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class Perfil extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView jobTitle, userName, userRUT, contactNumber, emergencyNumber;
    private ImageView profileImage;
    private Button logoutButton, editProfileImageButton;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Uri imageUri; // Para almacenar la URI de la imagen seleccionada

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializar Firebase Realtime Database y Storage
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializar vistas
        jobTitle = view.findViewById(R.id.job_title);
        userName = view.findViewById(R.id.user_name);
        userRUT = view.findViewById(R.id.user_rut);
        contactNumber = view.findViewById(R.id.contact_number);
        emergencyNumber = view.findViewById(R.id.emergency_number);
        profileImage = view.findViewById(R.id.profile_image);
        logoutButton = view.findViewById(R.id.logout_button);
        editProfileImageButton = view.findViewById(R.id.edit_profile_image_button); // Nuevo botón

        // Cargar datos del usuario
        loadUserData();

        // Manejar el evento de cerrar sesión
        logoutButton.setOnClickListener(v -> logout());

        // Manejar el evento de editar la imagen de perfil
        editProfileImageButton.setOnClickListener(v -> openImageChooser());

        return view;
    }

    private void loadUserData() {
        // Obtener el RUT del usuario de SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("ProShieldPrefs", getContext().MODE_PRIVATE);
        String rut = preferences.getString("user_rut", null);

        if (rut != null) {
            // Realizar consulta en Firebase usando el RUT
            databaseReference.child(rut).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        if (usuario != null) {
                            // Actualizar las vistas con los datos del usuario
                            userName.setText(usuario.getNombre());
                            userRUT.setText(rut); // Mostrar el RUT
                            jobTitle.setText(usuario.getTrabajo()); // Suponiendo que el trabajo se almacena aquí
                            contactNumber.setText(usuario.getNumeroContacto());
                            emergencyNumber.setText(usuario.getNumeroEmergencia());

                            // Cargar la imagen de perfil
                            if (usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isEmpty()) {
                                // Si hay URL de imagen, cargarla (usando Glide)
                                Glide.with(getContext())
                                        .load(usuario.getFotoPerfil())
                                        .into(profileImage);
                            } else {
                                // Si no hay imagen, usar la imagen por defecto
                                profileImage.setImageResource(R.mipmap.usuario);
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "RUT no encontrado en preferencias", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImageToFirebase(); // Subir imagen a Firebase
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // Generar un ID único para la imagen
            String imageId = UUID.randomUUID().toString();
            StorageReference fileReference = storageReference.child(imageId + ".jpg");

            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Obtener la URL de descarga de la imagen
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        updateProfileImageInDatabase(imageUrl); // Actualizar la URL en la base de datos
                    });
                }
            }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al subir la imagen", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateProfileImageInDatabase(String imageUrl) {
        // Obtener el RUT del usuario de SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("ProShieldPrefs", getContext().MODE_PRIVATE);
        String rut = preferences.getString("user_rut", null);

        if (rut != null) {
            databaseReference.child(rut).child("fotoPerfil").setValue(imageUrl)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Foto de perfil actualizada", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al actualizar la foto de perfil", Toast.LENGTH_SHORT).show());
        }
    }

    private void logout() {
        // Limpiar SharedPreferences al cerrar sesión
        SharedPreferences preferences = requireActivity().getSharedPreferences("ProShieldPrefs", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("user_rut");
        editor.apply();

        // Navegar a la actividad de inicio de sesión
        Intent intent = new Intent(getActivity(), MainActivity.class); // Asegúrate de reemplazar LoginActivity con el nombre real de tu actividad de inicio de sesión
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Limpiar la pila de actividades
        startActivity(intent); // Iniciar la actividad de inicio de sesión
        requireActivity().finish(); // Finalizar la actividad actual si es necesario
    }

}
