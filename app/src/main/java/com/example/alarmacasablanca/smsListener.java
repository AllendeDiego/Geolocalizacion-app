package com.example.alarmacasablanca;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.alarmacasablanca.Models.Eventos;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static com.example.alarmacasablanca.MainActivity.getAppContext;
import static com.example.alarmacasablanca.ui.Mapa.MapFragment.dispositivos;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;



public class smsListener extends BroadcastReceiver {

    public static List<String> numeros_de_celular = new ArrayList<>();
    public static int contador;
    final SmsManager sms = SmsManager.getDefault();
    private Date fecha;
    FirebaseFirestore db;
    String message;
    String senderNum;
    SmsMessage currentMessage;


    @Override
        public void onReceive(Context context, Intent intent) {
            // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        fecha = new Date();
        db = FirebaseFirestore.getInstance();

            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        senderNum = currentMessage.getDisplayOriginatingAddress();
                        message = currentMessage.getDisplayMessageBody();

                        if(isCelularValid(senderNum)){
                            switch (message){
                                case "ALARMA FASE 1"  : case "ALARMA FASE 2" : case "ALARMA FASE 3":
                                case "Alarma Fase 1" : case "Alarma Fase 2" : case "Alarma Fase 3":
                                case "Alarma fase 1" : case "Alarma fase 2" : case "Alarma fase 3":
                                case "Alarma fase en regular 1( lRotunda)": case "Alarma fase en regular 2(Rotunda)":
                                case "EQUIPO ABIERTO": case "Apertura Gabinete VERIFICAR": case "Cuidado !! Equipo Abierto":
                                case "GABINETE ABIERTO": {


                                    db.collection("Equipos").document(senderNum)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.getLong("color") == HUE_GREEN){
                                                        numeros_de_celular.add(senderNum);
                                                        contador = 0;
                                                        updateColor(HUE_RED, senderNum);
                                                        addFallaToFireStore(message, fecha, senderNum);
                                                        AumentarFalla(senderNum);
                                                        MainActivity.appInstance.startTimer();
                                                    }
                                                }
                                            });

                                    //sendNotification(context, message, 666);
                                    break;
                                }
                                case "RESTAURACION FASE 1": case "RESTAURACION FASE 2": case "RESTAURACION FASE 3":
                                case "Restauracion Fase 1": case "Restauracion Fase 2" : case "Restauracion  Fase 3":
                                case "Restauracion fase 1": case "Restauracion fase 2" : case "Restauracion  fase 3":
                                case "Restauracion Fase 3": case "Equipo Cerrado" : case "GABINETE CERRADO":
                                case "RESTAURA FASE 1":  case "RESTAURA FASE 2":  case "RESTAURA FASE 3":
                                case "Restauracion fase en regular 1(Rotunda)": case "Restauracion fase en regular 2(Rotunda)": {
                                    updateColor(HUE_BLUE, senderNum);
                                    break;
                                }
                                default:
                                    updateColor(HUE_YELLOW, senderNum);
                                    Toast toast = Toast.makeText(getAppContext(),
                                            "Alerta desconocida, comunicarse con el programador", Toast.LENGTH_LONG);
                                    toast.show();
                                    break;
                            }
                        }
                    } // end for loop
                } // bundle is null
            } catch (Exception e) {
            }
        }

    private boolean isCelularValid(String senderNum) {
        for (int j = 0; j < dispositivos.size(); j++) {
            if(dispositivos.get(j).getNumero().equals(senderNum)){
                return true;
            }
        }
        return false;
    }


    private void updateColor(float color, String Numero) {
        db.collection("Equipos").document(Numero)
                .update("color", color);
    }


    private void addFallaToFireStore(String Falla, Date Fecha, String Numero) {
        Eventos NuevaFalla = new Eventos(Falla, Fecha, Numero);
        db.collection("Eventos")
                .document(Fecha.toString())
                .set(NuevaFalla);
    }

    private void AumentarFalla(String Numero) {
        db.collection("Equipos").document(Numero)
                .update("fallas", FieldValue.increment(1));
    }
}