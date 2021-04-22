package com.example.alarmacasablanca.ui.Historial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alarmacasablanca.Models.Equipos;
import com.example.alarmacasablanca.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static com.example.alarmacasablanca.MainActivity.getAppContext;
import static com.example.alarmacasablanca.ui.Mapa.MapFragment.dispositivos;


public class HistorialRecyclerViewAdapter extends RecyclerView.Adapter<HistorialRecyclerViewAdapter.ViewHolder> {

    private List<Equipos> mValues;
    private Context ctx;
    private String comentario;
    FirebaseFirestore db;

    public HistorialRecyclerViewAdapter(List<Equipos> items, Context ctx) {
        mValues = items;
        this.ctx = ctx;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        db = FirebaseFirestore.getInstance();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.TextView_Nombre.setText(holder.mItem.getNombre());
        holder.TextView_Celular.setText(holder.mItem.getNumero());
        holder.TextView_NumeroFallas.setText(String.valueOf(holder.mItem.getFallas()));
        holder.TextView_Comentarios.setText( String.valueOf(holder.mItem.getBateria()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(ctx);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                Equipos item = mValues.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                input.setLayoutParams(lp);
                input.setHint("Ingrese Comentario");
                //input = new EditText(ctx);
                builder.setTitle("Comentario");
                builder.setMessage("Desea agregar comentario?");
                builder.setView(input);


                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        comentario = input.getText().toString();
                        updateComentario(comentario,item.getNumero());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.setIcon(android.R.drawable.ic_menu_info_details);
                builder.show();
            }
        });
    }

    private void updateComentario(String comentario, String numero) {

        db.collection("Equipos").document(numero)
                .update("bateria", comentario)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getAppContext(),"Comentario ingresado",Toast.LENGTH_SHORT).show();
                            setNuevosDipositivos(dispositivos);
                        }
                        else{
                            Toast.makeText(getAppContext(),"Comentario no ingresado",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setNuevosDipositivos(List<Equipos> nuevosDipositivos){
        mValues = nuevosDipositivos;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView TextView_Nombre;
        public final TextView TextView_Celular;
        public final TextView TextView_NumeroFallas;
        public final TextView TextView_Comentarios;
        public Equipos mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            TextView_Nombre =  view.findViewById(R.id.textView_Nombre);
            TextView_Celular =  view.findViewById(R.id.textView_Celular);
            TextView_NumeroFallas = view.findViewById(R.id.textView_NumeroFallas);
            TextView_Comentarios = view.findViewById(R.id.textView_Comentarios);
        }

        @Override
        @NonNull
        public String toString() {
            return super.toString() + " '" + TextView_Nombre.getText() + "'";
        }
    }
}
