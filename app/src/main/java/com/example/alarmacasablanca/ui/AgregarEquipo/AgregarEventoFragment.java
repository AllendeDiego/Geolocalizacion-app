package com.example.alarmacasablanca.ui.AgregarEquipo;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.alarmacasablanca.Models.Equipos;
import com.example.alarmacasablanca.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;


import static com.example.alarmacasablanca.MainActivity.getAppContext;
import static com.example.alarmacasablanca.MainActivity.hideKeyboard;
import static com.example.alarmacasablanca.MainActivity.isNetworkConnected;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;


public class AgregarEventoFragment extends Fragment implements OnMapReadyCallback {


    private GoogleMap mMap;
    private GeoPoint Lati;
    private LatLng loca;
    SupportMapFragment mapFragment;
    String Nombre;
    String Numero;
    FirebaseFirestore db;
    EditText etNombre;
    EditText etNumero;
    View v;
    ProgressDialog progressDialog;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        loca = new LatLng(0.0, 0.0);
        progressDialog = new ProgressDialog(getActivity());
        View root = inflater.inflate(R.layout.fragment_nuevo_dispositivo, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.mapita);
        if (savedInstanceState == null) {
            // First incarnation of this activity.
            mapFragment.setRetainInstance(true);
        }

        mapFragment.getMapAsync(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                progressDialog.dismiss();
                Navigation.findNavController(root).navigate(
                        R.id.action_agregardispositivoToMap);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button button = getView().findViewById(R.id.button_NuevoDispositivo_agregar);
        etNombre = getView().findViewById(R.id.editTextNombre);
        etNumero = getView().findViewById(R.id.editTextNumero);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v = view;
                hideKeyboard(getActivity());

                progressDialog.show();
                progressDialog.setContentView(R.layout.progress);
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );

                if (!isNetworkConnected()){
                    progressDialog.dismiss();
                    Toast.makeText(getAppContext(),"Celular sin internet",Toast.LENGTH_SHORT).show();
                }
                Nombre = etNombre.getText().toString();
                Numero = etNumero.getText().toString();
                if (etNumero.getText().toString().length() != 12) {
                    progressDialog.dismiss();
                    etNumero.setError("Ingrese número válido: +56912345678");
                }
                else if (Numero.isEmpty()){
                    progressDialog.dismiss();
                    etNumero.setError("El número del equipo es obligatorio");
                }
                else if (Nombre.isEmpty()){
                    progressDialog.dismiss();
                    etNombre.setError("El nombre del equipo es obligatorio");
                }
                else if (loca.latitude == 0.0 && loca.longitude == 0.0) {
                    progressDialog.dismiss();
                    etNumero.setError("Ingrese coordenadas");
                    etNombre.setError("Ingrese coordenadas");
                }
                else{
                    addEquipo();
                }

            }
        });
    }

    private void addEquipo() {
        db.collection("Equipos").whereEqualTo("numero",Numero)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0){
                            progressDialog.dismiss();
                            etNumero.setError("El número ya esta siendo utilizado");
                        }else{
                            db.collection("Equipos").whereEqualTo("nombre",Nombre)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (queryDocumentSnapshots.size() > 0){
                                                progressDialog.dismiss();
                                                etNombre.setError("El nombre ya esta siendo utilizado");
                                            }else{
                                                addEquipoToFireStore();
                                            }
                                        }
                                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void addEquipoToFireStore() {
        Equipos NuevoEquipo = new Equipos(Nombre, Lati,0, Numero, "", HUE_GREEN);
        db.collection("Equipos")
                .document(Numero)
                .set(NuevoEquipo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(getAppContext(),"Equipo ingresado exitosamente",Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(
                                R.id.action_agregardispositivoToMap);
                    }

                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                hideKeyboard(getActivity());
                loca = latLng;
                LatLonPoint(loca);
                Toast.makeText(getAppContext(),"Puede seguir editando ubicación",Toast.LENGTH_SHORT).show();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .title("UBICACIÓN").icon(BitmapDescriptorFactory
                                .defaultMarker(HUE_BLUE))).showInfoWindow();

            }
        });
        LatLng oficina = new LatLng(-33.3213445, -71.4093725);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(oficina));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.5f ) );
    }

    private void LatLonPoint(LatLng location) {
        Lati = new GeoPoint(location.latitude, location.longitude);
    }


}