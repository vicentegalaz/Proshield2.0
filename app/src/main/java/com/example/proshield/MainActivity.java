package com.example.proshield;

import android.content.Intent;
import android.content.SharedPreferences; // Importa SharedPreferences
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText rutInput, passwordInput;
    private Button loginButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rutInput = findViewById(R.id.rutInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        // Inicializar Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String rut = rutInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(rut)) {
            rutInput.setError("El RUT es obligatorio");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("La contraseña es obligatoria");
            return;
        }

        // Buscar usuario en la base de datos
        databaseReference.child(rut).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    if (usuario != null && usuario.getContraseña().equals(password)) {
                        // Inicio de sesión exitoso
                        Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                        // Guardar el RUT en SharedPreferences
                        SharedPreferences preferences = getSharedPreferences("ProShieldPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("user_rut", rut);
                        editor.apply();

                        // Navegar a la actividad Principal
                        Intent intent = new Intent(MainActivity.this, Principal.class);
                        startActivity(intent);
                        finish(); // Opcional: cerrar la actividad de inicio de sesión
                    } else {
                        // Contraseña incorrecta
                        Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Usuario no encontrado
                    Toast.makeText(MainActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejo de errores en la consulta
                Toast.makeText(MainActivity.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

