package com.example.proshield;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Lista extends Fragment {
    private String rut; // Variable para almacenar el RUT del usuario a modificar

    private TableLayout tableLayoutUsuarios;
    private DatabaseReference databaseReference;

    public Lista() {
        // Constructor vacío
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializar la referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragment
        View view = inflater.inflate(R.layout.fragment_lista, container, false);
        tableLayoutUsuarios = view.findViewById(R.id.tableLayoutUsuarios);

        // Cargar los usuarios desde Firebase
        cargarUsuarios();

        return view;
    }

    private void cargarUsuarios() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpiar la tabla antes de agregar nuevos datos
                tableLayoutUsuarios.removeViews(1, tableLayoutUsuarios.getChildCount() - 1);

                for (DataSnapshot usuarioSnapshot : dataSnapshot.getChildren()) {
                    String rut = usuarioSnapshot.getKey();
                    String nombre = usuarioSnapshot.child("nombre").getValue(String.class);
                    String tipo = usuarioSnapshot.child("tipo").getValue(String.class);

                    // Crear una nueva fila para el usuario
                    TableRow tableRow = new TableRow(getContext());

                    TextView textViewRut = new TextView(getContext());
                    textViewRut.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                    textViewRut.setText(rut);

                    TextView textViewCargo = new TextView(getContext());
                    textViewCargo.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                    textViewCargo.setText(tipo);

                    TextView textViewNombre = new TextView(getContext());
                    textViewNombre.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                    textViewNombre.setText(nombre);

                    // Crear botones de acciones
                    LinearLayout linearLayout = new LinearLayout(getContext());
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                    Button buttonEliminar = new Button(getContext());
                    buttonEliminar.setText("Eliminar");
                    buttonEliminar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    buttonEliminar.setOnClickListener(v -> eliminarUsuario(rut));

                    Button buttonActualizar = new Button(getContext());
                    buttonActualizar.setText("Actualizar");
                    buttonActualizar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    buttonActualizar.setOnClickListener(v -> actualizarUsuario(rut));

                    linearLayout.addView(buttonEliminar);
                    linearLayout.addView(buttonActualizar);

                    // Agregar las vistas a la fila
                    tableRow.addView(textViewRut);
                    tableRow.addView(textViewCargo);
                    tableRow.addView(textViewNombre);
                    tableRow.addView(linearLayout);

                    // Agregar la fila a la tabla
                    tableLayoutUsuarios.addView(tableRow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error al cargar usuarios: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarUsuario(String rut) {
        // Crear un AlertDialog para confirmar la eliminación
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este usuario?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Lógica para eliminar el usuario
                        databaseReference.child(rut).removeValue().addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al eliminar usuario", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Cerrar el diálogo sin hacer nada
                    }
                })
                .show();
    }


    private void actualizarUsuario(String rut) {
        // Crear una nueva instancia del fragmento Modificar
        Modificar modificarFragment = new Modificar();

        // Crear un Bundle para pasar el RUT
        Bundle bundle = new Bundle();
        bundle.putString("usuarioRut", rut); // Asegúrate de usar la misma clave que en el fragmento
        modificarFragment.setArguments(bundle);

        // Reemplazar el fragmento actual con el nuevo fragmento Modificar
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, modificarFragment) // Asegúrate de que 'fragment_container' es el ID correcto
                .addToBackStack(null) // Esto permite que el usuario regrese al fragmento anterior
                .commit();
    }
}
