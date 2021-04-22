package com.example.alarmacasablanca.ui.CrearAdministardor;

import androidx.activity.OnBackPressedCallback;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alarmacasablanca.Models.Usuarios;
import com.example.alarmacasablanca.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.alarmacasablanca.MainActivity.getAppContext;
import static com.example.alarmacasablanca.MainActivity.hideKeyboard;
import static com.example.alarmacasablanca.MainActivity.isNetworkConnected;

public class CrearAdministrador extends Fragment {

    FirebaseFirestore db;
    String Usuario;
    String Password;
    EditText etUsuario;
    EditText etPassword;
    View v;
    ProgressDialog progressDialog;

    public static CrearAdministrador newInstance() {
        return new CrearAdministrador();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        View view = inflater.inflate(R.layout.fragment_nuevo_usuario, container, false);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                progressDialog.dismiss();
                Navigation.findNavController(view).navigate(
                        R.id.action_crearAdministradorToMap);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button button = getView().findViewById(R.id.button_NuevoUsuario_Agregar);
        etUsuario = getView().findViewById(R.id.editText_NuevoUsuario_Email);
        etPassword= getView().findViewById(R.id.editText_NuevoUsuario_Password);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(getActivity());
                Usuario= etUsuario.getText().toString();
                Password = etPassword.getText().toString();
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

                if (Usuario.isEmpty()){
                    progressDialog.dismiss();
                    etUsuario.setError("El nombre de usuario es obligatorio");
                }
                else{
                    addUsuario();
                }

            }
        });
    }

    private void addUsuario() {
        db.collection("Usuarios").whereEqualTo("usuario",Usuario)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                      if (queryDocumentSnapshots.size() > 0){
                          progressDialog.dismiss();
                          etUsuario.setError("Usuario no disponible");
                      }else{
                          addUsuarioToFireStore();
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

    private void addUsuarioToFireStore() {
        Usuarios NuevoUsuario = new Usuarios(Usuario, Password);
        db.collection("Usuarios")
                .document(Usuario)
                .set(NuevoUsuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(getAppContext(),"Administrador ingresado exitosamente",Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(
                                R.id.action_crearAdministradorToMap);
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }
    /*private void addUsuarioToFireStore() {
        Usuarios NuevoUsuario = new Usuarios(Usuario, Password);
        db.collection("Usuarios")
                .add(NuevoUsuario)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getAppContext(),"Administrador ingresado exitosamente",Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(
                                R.id.action_crearAdministradorToMap);
                    }
                });
    }*/
}
