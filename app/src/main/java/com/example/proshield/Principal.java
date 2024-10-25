package com.example.proshield;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class Principal extends AppCompatActivity {

    private ListView listViewOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        listViewOptions = findViewById(R.id.listViewOptions);
        ImageView selectFragmentView = findViewById(R.id.selectFragmentView);
        ImageView imageViewProfile = findViewById(R.id.imageViewProfile); // Agregar referencia al ImageView del perfil

    // Configurar el ListView
        String[] options = {"Inicio", "Lista", "Registro", "Subir Emergencia","Historial Emergencias"}; // Agregar la nueva opción
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        listViewOptions.setAdapter(adapter);


    // Manejar clics en el ListView
        listViewOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new Inicio();
                        break;
                    case 1:
                        fragment = new Lista();
                        break;
                    case 2:
                        fragment = new Registro();
                        break;
                    case 3: // Manejar el nuevo caso
                        fragment = new Subir_Emergencia(); // Cargar el nuevo fragmento
                        break;
                    case 4: // Manejar el nuevo caso
                        fragment = new HistorialEmergencias(); // Cargar el nuevo fragmento
                        break;
                }
                if (fragment != null) {
                    loadFragment(fragment);
                }
                // Ocultar la lista después de hacer clic
                listViewOptions.setVisibility(View.GONE);
            }
        });


        // Manejar clics en el ImageView para mostrar/ocultar el ListView
        selectFragmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listViewOptions.getVisibility() == View.GONE) {
                    listViewOptions.setVisibility(View.VISIBLE); // Mostrar la lista
                } else {
                    listViewOptions.setVisibility(View.GONE); // Ocultar la lista
                }
            }
        });

        // Manejar clics en el ImageView del perfil
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Perfil()); // Cargar el fragmento de perfil
            }
        });

        // Cargar el fragmento inicial si es necesario
        if (savedInstanceState == null) {
            loadFragment(new Inicio());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
