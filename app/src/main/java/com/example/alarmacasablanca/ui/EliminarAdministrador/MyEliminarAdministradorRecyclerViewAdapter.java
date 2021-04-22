package com.example.alarmacasablanca.ui.EliminarAdministrador;

import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alarmacasablanca.Models.Usuarios;
import com.example.alarmacasablanca.R;
import java.util.List;


public class MyEliminarAdministradorRecyclerViewAdapter extends RecyclerView.Adapter<MyEliminarAdministradorRecyclerViewAdapter.ViewHolder> {

    private List<Usuarios> mValues;

    public MyEliminarAdministradorRecyclerViewAdapter(List<Usuarios> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_eliminaradministrador, parent, false);
        return new MyEliminarAdministradorRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyEliminarAdministradorRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.TextView_Usuario.setText(holder.mItem.getUsuario());


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                }

        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public Usuarios getCorreoAt(int position){
        return mValues.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView TextView_Usuario;
        public Usuarios mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            TextView_Usuario = view.findViewById(R.id.textView_Correo);
        }
        @Override
        public String toString() {
            return super.toString() + " '" + TextView_Usuario.getText() + "'";
        }
    }
}
