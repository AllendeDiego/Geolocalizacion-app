package com.example.alarmacasablanca.ui.EliminarCorreo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmacasablanca.Models.Correos;
import com.example.alarmacasablanca.R;

import java.util.List;

public class EliminarCorreoRecyclerViewAdapter extends RecyclerView.Adapter<EliminarCorreoRecyclerViewAdapter.ViewHolder>{

    private List<Correos> mValues;

    public EliminarCorreoRecyclerViewAdapter(List<Correos> items) {
        mValues = items;
    }

    @Override
    public EliminarCorreoRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_eliminar_correo, parent, false);
        return new EliminarCorreoRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EliminarCorreoRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.TextView_Correo.setText(holder.mItem.getCorreo());

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

    public void setNuevosCorreos(List<Correos> nuevosCorreos){
        this.mValues = nuevosCorreos;
        notifyDataSetChanged();
    }

    public Correos getCorreoAt(int position){
        return mValues.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView TextView_Correo;
        public Correos mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            TextView_Correo = view.findViewById(R.id.textView_Correo);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + TextView_Correo.getText() + "'";
        }
    }
}

