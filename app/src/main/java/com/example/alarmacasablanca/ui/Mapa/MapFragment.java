package com.example.alarmacasablanca.ui.Mapa;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.alarmacasablanca.Email.GMailSender;
import com.example.alarmacasablanca.MainActivity;
import com.example.alarmacasablanca.Models.Correos;
import com.example.alarmacasablanca.Models.Equipos;
import com.example.alarmacasablanca.R;
import com.example.alarmacasablanca.ui.EliminarEquipo.EliminarDispositivoRecyclerViewAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.alarmacasablanca.MainActivity.getAppContext;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    public  List<String> correos = new ArrayList<String>();
    public static List<Equipos> dispositivos = new ArrayList<>();
    FirebaseFirestore db;
    DatabaseReference mEquipos;
    Context ctx;
    private NotificationManager mNotificationManager;
    SupportMapFragment mapFragment;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = FirebaseFirestore.getInstance();
        mEquipos= FirebaseDatabase.getInstance().getReference("Equipos");
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        ctx = getContext();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (savedInstanceState == null) {
            // First incarnation of this activity.
            mapFragment.setRetainInstance(true);
        }
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
     /*   new LoadLocalKmlFile(R.raw.edecsa1).execute();
        new LoadLocalKmlFile(R.raw.edecsa2).execute();
        new LoadLocalKmlFile(R.raw.edecsa3).execute();
        new LoadLocalKmlFile(R.raw.edecsa4).execute();
        new LoadLocalKmlFile(R.raw.edecsa5).execute();
        new LoadLocalKmlFile(R.raw.edecsa6).execute();
        new LoadLocalKmlFile(R.raw.edecsa7).execute();
        new LoadLocalKmlFile(R.raw.edecsa8).execute();
        new LoadLocalKmlFile(R.raw.edecsa9).execute();
        new LoadLocalKmlFile(R.raw.edecsa10).execute();
        new LoadLocalKmlFile(R.raw.edecsa11).execute();
        new LoadLocalKmlFile(R.raw.edecsa12).execute();
*/
    db.collection("Equipos")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "listen:error", e);
                        return;
                    }
                    cargarmarcadores();
                }
            });
        LatLng oficina = new LatLng(-33.3213445, -71.4093725);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(oficina));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10.5f));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    private void cargarmarcadores() {
        db.collection("Equipos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        dispositivos = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Equipos EquipoItem = document.toObject(Equipos.class);
                            dispositivos.add(EquipoItem);
                            GeoPoint latitude = EquipoItem.getLocation();
                            float color = EquipoItem.getColor();
                            LatLng dispositivo = new LatLng(latitude.getLatitude(), latitude.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(dispositivo)
                                    .title(EquipoItem.getNombre()).icon(BitmapDescriptorFactory
                                            .defaultMarker(color))).showInfoWindow();
                        }
                        notification(getAppContext(),"Los equipos estan actualizados" ,665);
                    }
                });
    }

    private void notification(Context context, String nombre_dispo, int channel_id){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "notify_002");
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Datos actualizados");
        bigText.setBigContentTitle(nombre_dispo);

        Intent fullScreenIntent = new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(fullScreenIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_casablanca_round);
        mBuilder.setLargeIcon(largeIcon);
        mBuilder.setSmallIcon(R.mipmap.ic_casablanca_round);
        mBuilder.setContentTitle(nombre_dispo);
        mBuilder.setContentText("Datos actualizados");
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setLights(0xff0000ff, 300, 1000);
        mBuilder.setStyle(bigText);
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setAutoCancel(true);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "actualización_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    nombre_dispo,
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
            channel.setVibrationPattern(new long[]{0, 250, 500, 750, 1000});
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        }
        mNotificationManager.notify(channel_id, mBuilder.build());
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        for (int j = 0; j < dispositivos.size(); j++) {
            if(dispositivos.get(j).getNombre().equals(marker.getTitle()) && dispositivos.get(j).getColor() != HUE_GREEN && dispositivos.get(j).getColor() != 200.0){
            if (isNetworkConnected()) {

                db.collection("Correos")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                correos = new ArrayList<>();
                                for (DocumentSnapshot document : task.getResult()) {
                                    Correos emailItem = document.toObject(Correos.class);
                                    correos.add(emailItem.getCorreo());
                                }

                                updateColor(HUE_GREEN, getCelular(marker));
                                Toast.makeText(getContext(), "EQUIPO VERIFICADO \n CORREO DE NOTIFICACION ENVIADO",
                                        Toast.LENGTH_LONG).show();
                                String nombre = marker.getTitle();
                                Double ubi1 = marker.getPosition().latitude;
                                Double ubi2 = marker.getPosition().longitude;
                                String ubica1 = ubi1.toString();
                                String ubica2 = ubi2.toString();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            GMailSender sender = new GMailSender("EMAIL",
                                                    "PASSWORD");
                                            for (int i = 0; i < correos.size(); i++) {
                                                sender.sendMail("Restauración de servicio", "Restauración de servicio en " + nombre + "\n " +
                                                                "https://www.google.com/maps/place/" + ubica1 + "," + ubica2,
                                                        "EMAIL", correos.get(i));
                                            }
                                        } catch (Exception e) {
                                        }
                                    }
                                }).start();

                            }
                        });

            } else {
                Toast toast = Toast.makeText(getAppContext(),
                        "Celular sin internet, no se ha podido verificar restauración de equipo", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }
    }

    private String getCelular(Marker marker) {
        for (int j = 0; j < dispositivos.size(); j++) {
            if(dispositivos.get(j).getNombre().equals(marker.getTitle())){
                            return dispositivos.get(j).getNumero();
                }
        }
        Toast toast = Toast.makeText(getAppContext(),"Equipo no ubicado", Toast.LENGTH_SHORT);
        toast.show();
        return "hola";
    }



    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void updateColor(float color, String Numero) {
        db.collection("Equipos").document(Numero)
                .update("color", color);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (int j = 0; j < dispositivos.size(); j++) {
            if (dispositivos.get(j).getNombre().equals(marker.getTitle())) {
                marker.remove();
                GeoPoint latitude = dispositivos.get(j).getLocation();
                float color = dispositivos.get(j).getColor();
                LatLng dispositivo = new LatLng(latitude.getLatitude(), latitude.getLongitude());
                mMap.addMarker(new MarkerOptions().position(dispositivo)
                        .title(dispositivos.get(j).getNombre()).icon(BitmapDescriptorFactory
                                .defaultMarker(color))).showInfoWindow();
            }

        }
        return false;
    }


}

/*

    private class LoadLocalKmlFile extends AsyncTask<String, Void, KmlLayer> {
        private final int mResourceId;

        LoadLocalKmlFile(int resourceId) {
            mResourceId = resourceId;
        }

        @Override
        protected KmlLayer doInBackground(String... strings) {
            try {
                return new KmlLayer(mMap, mResourceId, getActivity().getApplicationContext());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(KmlLayer kmlLayer) {
            addKmlToMap(kmlLayer);
        }
    }

    private void addKmlToMap(KmlLayer kmlLayer) {
        if (kmlLayer != null) {
            kmlLayer.addLayerToMap();
        }
    }

    */



