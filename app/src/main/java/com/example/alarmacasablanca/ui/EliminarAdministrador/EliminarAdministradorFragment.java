package com.example.alarmacasablanca.ui.EliminarAdministrador;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.alarmacasablanca.Models.Usuarios;
import com.example.alarmacasablanca.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.alarmacasablanca.MainActivity.getAppContext;


public class EliminarAdministradorFragment extends Fragment {


    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private MyEliminarAdministradorRecyclerViewAdapter adapterAdmi;
    FirebaseFirestore db;
    List<Usuarios> UsuariosList;
    ProgressDialog progressDialog;

    public EliminarAdministradorFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static EliminarAdministradorFragment newInstance(int columnCount) {
        EliminarAdministradorFragment fragment = new EliminarAdministradorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getActivity());

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eliminaradministrador_list, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                progressDialog.dismiss();
                Navigation.findNavController(view).navigate(
                        R.id.action_nav_EliminarAdministrador_to_nav_map);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;


            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress);
                    progressDialog.getWindow().setBackgroundDrawableResource(
                            android.R.color.transparent
                    );
                    db.collection("Usuarios").document(String.valueOf(adapterAdmi.getCorreoAt(viewHolder.getAdapterPosition()).getUsuario()))
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getAppContext(), "Administrador eliminado", Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(view).navigate(
                                            R.id.action_nav_EliminarAdministrador_to_nav_map);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getAppContext(), "Administrador no pudo ser eliminado", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).attachToRecyclerView(recyclerView);


            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(mColumnCount, StaggeredGridLayoutManager.VERTICAL));
            }



            db.collection("Usuarios")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            UsuariosList = new ArrayList<>();
                            for(DocumentSnapshot document:task.getResult()){
                                Usuarios userItem = document.toObject(Usuarios.class);
                                UsuariosList.add(userItem);
                                adapterAdmi = new MyEliminarAdministradorRecyclerViewAdapter(UsuariosList);
                                recyclerView.setAdapter(adapterAdmi);
                            }
                        }
                    });

        }
        return view;
    }



}
