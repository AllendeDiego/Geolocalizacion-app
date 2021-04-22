package com.example.alarmacasablanca.ui.Historial;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.alarmacasablanca.Email.GMailSender;
import com.example.alarmacasablanca.Models.Correos;
import com.example.alarmacasablanca.Models.Equipos;
import com.example.alarmacasablanca.Models.Eventos;
import com.example.alarmacasablanca.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.alarmacasablanca.MainActivity.getAppContext;
import static com.example.alarmacasablanca.MainActivity.isNetworkConnected;
import static com.example.alarmacasablanca.ui.Mapa.MapFragment.dispositivos;

public class HistorialFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private List<Equipos> EquiposList;
    private HistorialRecyclerViewAdapter adapterDispositivos;
    private StringBuffer historial = new StringBuffer("");
    private List<Eventos> eventos ;
    private List<String> correos = new ArrayList<String>();
    public static Context contexto;
    public static View vista;
    FirebaseFirestore db;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    public HistorialFragment(){
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HistorialFragment newInstance(int columnCount) {
        HistorialFragment fragment = new HistorialFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        super.onCreate(savedInstanceState);
        EquiposList = new ArrayList<>();
        correos = new ArrayList<>();
        eventos = new ArrayList<>();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_historial_list, container, false);
        Context context = view.getContext();
        final Button Button = view.findViewById(R.id.button_historial);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargar_Eventos();
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress);
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );
                if (!isNetworkConnected()){
                    progressDialog.dismiss();
                    Toast.makeText(getAppContext(),"Celular sin internet",Toast.LENGTH_SHORT).show();
                }

            }
        });
        EquiposList = dispositivos;
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapterDispositivos = new HistorialRecyclerViewAdapter(EquiposList, getActivity());
        recyclerView.setAdapter(adapterDispositivos);
        contexto = context;
        vista = view;
        return view;
    }


    private void cargarDatos(){
        if(isNetworkConnected()){
            db.collection("Correos")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Correos emailItem = document.toObject(Correos.class);
                                correos.add(emailItem.getCorreo());
                            }

                            if(correos.isEmpty()){
                                Toast.makeText(getAppContext(),"Correos no ingresados",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getAppContext(), "Correo enviado ", Toast.LENGTH_SHORT).show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            GMailSender sender = new GMailSender("alerta.casablanca@gmail.com",
                                                    "Portales187");
                                            for (int j = 0; j < dispositivos.size(); j++) {
                                                historial.append(dispositivos.get(j).getNombre());
                                                historial.append(": ");
                                                historial.append(dispositivos.get(j).getNumero());
                                                historial.append("\n");
                                                historial.append("   ");
                                                historial.append("Numero de fallas: ");
                                                historial.append(dispositivos.get(j).getFallas());
                                                historial.append("\n");
                                                historial.append("   ");
                                                historial.append("Comentarios: ");
                                                historial.append(dispositivos.get(j).getBateria());
                                                historial.append("\n");
                                                historial.append("   ");
                                                historial.append("Historial de fallas: ");
                                                historial.append("\n");
                                                for (int a = 0; a < eventos.size(); a++) {
                                                    if (eventos.get(a).getNumero().equals(dispositivos.get(j).getNumero())) {
                                                        historial.append("            ");
                                                        historial.append(eventos.get(a).getTipo());
                                                        historial.append("  --  ");
                                                        historial.append(eventos.get(a).getFecha());
                                                        historial.append("\n");
                                                    }
                                                }
                                                historial.append("--------------------------");
                                                historial.append("\n");
                                            }
                                            progressDialog.dismiss();
                                            for (int x = 0; x < correos.size(); x++) {
                                                sender.sendMail("Historial de dispositivos", "HISTORIAL: \n\n" + historial,
                                                        "alerta.casablanca@gmail.com", correos.get(x));
                                            }
                                            historial.delete(0, historial.length());


                                        } catch (Exception e) {
                                        }
                                    }
                                }).start();
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
        else{
            progressDialog.dismiss();
            Toast toast = Toast.makeText(getAppContext(),
                    "Celular sin internet, correo no enviado", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void cargar_Eventos() {
        Query first = db.collection("Eventos")
                .limit(100);
                first.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot document : task.getResult()) {
                            progressDialog.dismiss();
                            Eventos eventoItem = document.toObject(Eventos.class);
                            eventos.add(eventoItem);
                        }
                        cargarDatos();
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