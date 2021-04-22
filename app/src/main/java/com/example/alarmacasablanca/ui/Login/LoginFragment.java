package com.example.alarmacasablanca.ui.Login;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.alarmacasablanca.Config;
import com.example.alarmacasablanca.MainActivity;
import com.example.alarmacasablanca.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.alarmacasablanca.MainActivity.getAppContext;
import static com.example.alarmacasablanca.MainActivity.hideKeyboard;
import static com.example.alarmacasablanca.MainActivity.isNetworkConnected;


public class LoginFragment extends Fragment {

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }
    FirebaseFirestore db;
    EditText admi;
    EditText pass;
    View v;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        View root = inflater.inflate(R.layout.login, container, false);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                progressDialog.dismiss();
                Navigation.findNavController(root).navigate(
                        R.id.action_loginToMap);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Button button = getView().findViewById(R.id.button_Login_Ingresar);
        admi = getView().findViewById(R.id.editText_Login_Email);
        pass = getView().findViewById(R.id.editText_Login_Password);

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
                ComprobarUsuario();
            }
        });

    }

    private void ComprobarUsuario() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        db.collection("Usuarios").whereEqualTo("usuario",admi.getText().toString())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            db.collection("Usuarios").whereEqualTo("password", pass.getText().toString())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (queryDocumentSnapshots.size() > 0) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getAppContext(), "BIENVENIDO", Toast.LENGTH_SHORT).show();
                                                editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                                                editor.apply();
                                                Navigation.findNavController(v).navigate(
                                                        R.id.action_loginToMap);
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(getAppContext(), "USUARIO y/o CONTRASEÑA NO VALIDOS", Toast.LENGTH_SHORT).show();
                                                pass.setError("Contraseña no valida");
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
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(getAppContext(), "USUARIO y/o CONTRASEÑA NO VALIDOS", Toast.LENGTH_SHORT).show();
                            admi.setError("Usuario no valido");
                        }
                    }
                });
    }
}





