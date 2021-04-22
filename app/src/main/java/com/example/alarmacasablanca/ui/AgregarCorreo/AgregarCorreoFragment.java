package com.example.alarmacasablanca.ui.AgregarCorreo;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.alarmacasablanca.Models.Correos;
import com.example.alarmacasablanca.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.alarmacasablanca.MainActivity.getAppContext;
import static com.example.alarmacasablanca.MainActivity.hideKeyboard;
import static com.example.alarmacasablanca.MainActivity.isNetworkConnected;

public class AgregarCorreoFragment extends Fragment {

    public static AgregarCorreoFragment newInstance() {
        return new AgregarCorreoFragment();
    }

    EditText etCorreo;
    String Correo;
    View v;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getActivity());

        View root = inflater.inflate(R.layout.fragment_nuevo_correo, container, false);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                progressDialog.dismiss();
                Navigation.findNavController(root).navigate(
                        R.id.action_agregarcorreoToMap);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button button = getView().findViewById(R.id.button_NuevoCorreo_Agregar);
        etCorreo = getView().findViewById(R.id.editText_NuevoCorreo_Email);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Correo = etCorreo.getText().toString();
                hideKeyboard(getActivity());
                v = view;

                progressDialog.show();
                progressDialog.setContentView(R.layout.progress);
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );

                if (!isNetworkConnected()){
                    progressDialog.dismiss();
                    Toast.makeText(getAppContext(),"Celular sin internet",Toast.LENGTH_SHORT).show();
                }

                if (Correo.isEmpty()) {
                    progressDialog.dismiss();
                    etCorreo.setError("Debe ingresar un correo");

                } else {
                    addCorreo();

                }


            }
        });
    }

    private void addCorreo() {
        db.collection("Correos").whereEqualTo("Correo",Correo)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0){
                            progressDialog.dismiss();
                            etCorreo.setError("Correo ya habia sido agregado");
                        }else{
                            addCorreoToFireStore();
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


    private void addCorreoToFireStore() {
        Correos NuevoCorreo = new Correos(Correo);
        db.collection("Correos")
                .document(Correo)
                .set(NuevoCorreo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getAppContext(), "Correo ingresado exitosamente", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(
                                R.id.action_agregarcorreoToMap);
                        progressDialog.dismiss();
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