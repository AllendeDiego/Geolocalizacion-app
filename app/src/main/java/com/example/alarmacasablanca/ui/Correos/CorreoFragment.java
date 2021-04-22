package com.example.alarmacasablanca.ui.Correos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.alarmacasablanca.Models.Correos;
import com.example.alarmacasablanca.R;
import com.example.alarmacasablanca.ui.EliminarCorreo.EliminarCorreoFragment;
import com.example.alarmacasablanca.ui.EliminarCorreo.EliminarCorreoRecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CorreoFragment extends Fragment {


    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private List<Correos> CorreosList;
    private EliminarCorreoRecyclerViewAdapter adapterCorreos;
    FirebaseFirestore db;

    public CorreoFragment() {
    }

    // TODO: Customize parameter initialization

    public static EliminarCorreoFragment newInstance(int columnCount) {
        EliminarCorreoFragment fragment = new EliminarCorreoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eliminar_correo_list, container, false);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).navigate(
                        R.id.action_correoToMap);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(mColumnCount, StaggeredGridLayoutManager.VERTICAL));
            }


            db.collection("Correos")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            CorreosList = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Correos userItem = document.toObject(Correos.class);
                                CorreosList.add(userItem);
                                adapterCorreos = new EliminarCorreoRecyclerViewAdapter(CorreosList);
                                recyclerView.setAdapter(adapterCorreos);
                            }
                        }
                    });


        }
        return view;
    }
}

