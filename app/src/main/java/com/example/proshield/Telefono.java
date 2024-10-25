package com.example.proshield;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Telefono extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Telefono() {
        // Required empty public constructor
    }

    public static Telefono newInstance(String param1, String param2) {
        Telefono fragment = new Telefono();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_telefono, container, false);

        ImageView bomberosImage = view.findViewById(R.id.bomberosImage);
        ImageView policiasImage = view.findViewById(R.id.policiasImage);
        ImageView ambulanciaImage = view.findViewById(R.id.ambulanciaImage);

        bomberosImage.setOnClickListener(v -> makeCall("132")); // Número de Bomberos
        policiasImage.setOnClickListener(v -> makeCall("133")); // Número de Carabineros
        ambulanciaImage.setOnClickListener(v -> makeCall("131")); // Número de Ambulancia

        return view;
    }

    private void makeCall(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                Toast.makeText(getActivity(), "Permiso de llamada concedido.", Toast.LENGTH_SHORT).show();
            } else {
                // Permiso denegado
                Toast.makeText(getActivity(), "Permiso de llamada denegado.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
