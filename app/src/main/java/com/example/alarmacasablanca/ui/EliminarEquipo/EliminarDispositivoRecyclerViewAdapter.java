package com.example.alarmacasablanca.ui.EliminarEquipo;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alarmacasablanca.Models.Equipos;
import com.example.alarmacasablanca.R;

import java.util.List;



public class EliminarDispositivoRecyclerViewAdapter extends RecyclerView.Adapter<EliminarDispositivoRecyclerViewAdapter.ViewHolder>{

    private List<Equipos> mValues;




    public EliminarDispositivoRecyclerViewAdapter(List<Equipos> items) {
        mValues = items;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_eliminardispositivo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.TextView_Nombre.setText(holder.mItem.getNombre());
        holder.TextView_Celular.setText(String.valueOf(holder.mItem.getNumero()));

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

    public void setNuevosDipositivos(List<Equipos> nuevosDipositivos){
        this.mValues = nuevosDipositivos;
        notifyDataSetChanged();
    }

    public Equipos getDispositivoAt(int position){
        return mValues.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public final TextView TextView_Nombre;
        public final TextView TextView_Celular;
        public Equipos mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            TextView_Nombre =  view.findViewById(R.id.textView_Nombre);
            TextView_Celular =  view.findViewById(R.id.textView_Celular);
        }
        @Override
        public String toString() {
            return super.toString() + " '" + TextView_Nombre.getText() + "'";
        }
    }
}
