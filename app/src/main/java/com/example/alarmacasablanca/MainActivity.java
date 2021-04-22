package com.example.alarmacasablanca;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.alarmacasablanca.Email.GMailSender;
import com.example.alarmacasablanca.Models.Correos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Debug;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.alarmacasablanca.R.id.nav_view;
import static com.example.alarmacasablanca.smsListener.contador;
import static com.example.alarmacasablanca.smsListener.numeros_de_celular;
import static com.example.alarmacasablanca.ui.Mapa.MapFragment.dispositivos;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public MenuItem item1;
    public MenuItem item2;
    public MenuItem item3;
    public MenuItem item4;
    public MenuItem item5;
    public MenuItem item6;
    public MenuItem item7;
    public MenuItem item8;
    private Menu menu;
    public static MainActivity appInstance;
    private StringBuffer nombres = new StringBuffer("");
    private StringBuffer locaciones = new StringBuffer("");
    FirebaseFirestore db;
    FirebaseDatabase database;
    static boolean calledAlready = false;
    public List<String> correos = new ArrayList<String>();
    private NotificationManager mNotificationManager;
    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Debug.waitForDebugger();
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        db = FirebaseFirestore.getInstance();

        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        database = FirebaseDatabase.getInstance();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.RECEIVE_SMS},
                    2);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.INTERNET},
                    2);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                    2);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.FOREGROUND_SERVICE},
                    2);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.RECEIVE_SMS},
                    2);
        }
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences(com.example.alarmacasablanca.Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(com.example.alarmacasablanca.Config.LOGGEDIN_SHARED_PREF, false);
        editor.apply();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_slideshow, R.id.nav_EliminarDispositivo, R.id.nav_AgregarDispositivo, R.id.nav_EliminarAdministrador,
                R.id.nav_map, R.id.nav_AgregarCorreo, R.id.nav_EliminarCorreo, R.id.nav_login, R.id.crearAdministrador,
                R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        appInstance = this;


        startService(toolbar);
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        Date before = calendar.getTime();

        Calendar calendar1 = Calendar.getInstance();
        Date until = calendar1.getTime();
        db.collection("Eventos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED ) {


                                Intent startIntent = getBaseContext()
                                        .getPackageManager()
                                        .getLaunchIntentForPackage(getBaseContext().getPackageName());

                                startIntent.setFlags(
                                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                                );
                                getBaseContext().startActivity(startIntent);

                                Date date = dc.getDocument().getTimestamp("fecha").toDate();
                                String tipo = dc.getDocument().getString("tipo");
                                String numero = dc.getDocument().getString("numero");
                                for (int j = 0; j < dispositivos.size(); j++) {
                                    if (dispositivos.get(j).getNumero().equals(numero)) {
                                        String nombre = dispositivos.get(j).getNombre();
                                        if (!before.after(date) && !until.before(date)){
                                            Log.d("life", "Data: "+date);
                                            notification(getAppContext(), "Alarma en "+ nombre, 667, tipo);
                                            break;
                                    }
                                }
                                }
                            }
                        }
                    }
                });



    }

    private void notification(Context context, String nombre_dispo, int channel_id, String alarma_nombre){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "notify_002");
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(alarma_nombre);
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
        mBuilder.setContentText(alarma_nombre);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setLights(0xff0000ff, 300, 1000);
        mBuilder.setStyle(bigText);
        mBuilder.setVibrate(new long[]{0, 250, 500, 750, 1000});
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setAutoCancel(true);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "alarma_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    nombre_dispo,
                    NotificationManager.IMPORTANCE_HIGH);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
            mBuilder.setChannelId(channelId);
            channel.setVibrationPattern(new long[]{0, 250, 500, 750, 1000});
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        }
        if (mNotificationManager != null) {
            mNotificationManager.notify(channel_id, mBuilder.build());
        }


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        updateMenuItems(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    private void updateMenuItems(Menu menu) {
        SharedPreferences sharedPreferences = getSharedPreferences(com.example.alarmacasablanca.Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean loggedIn = sharedPreferences.getBoolean(com.example.alarmacasablanca.Config.LOGGEDIN_SHARED_PREF, false);
        if (loggedIn == false) {
            item1 = menu.findItem(R.id.nav_logout).setVisible(false);
            item2 = menu.findItem(R.id.nav_AgregarCorreo).setVisible(false);
            item3 = menu.findItem(R.id.nav_AgregarDispositivo).setVisible(false);
            item4 = menu.findItem(R.id.nav_EliminarCorreo).setVisible(false);
            item5 = menu.findItem(R.id.nav_EliminarDispositivo).setVisible(false);
            item6 = menu.findItem(R.id.nav_login).setVisible(true);
            item7 = menu.findItem(R.id.crearAdministrador).setVisible(false);
            item8 = menu.findItem(R.id.nav_EliminarAdministrador).setVisible(false);
        } else {
            item1 = menu.findItem(R.id.nav_logout).setVisible(true);
            item2 = menu.findItem(R.id.nav_AgregarCorreo).setVisible(true);
            item3 = menu.findItem(R.id.nav_AgregarDispositivo).setVisible(true);
            item4 = menu.findItem(R.id.nav_EliminarCorreo).setVisible(true);
            item5 = menu.findItem(R.id.nav_EliminarDispositivo).setVisible(true);
            item6 = menu.findItem(R.id.nav_login).setVisible(false);
            item7 = menu.findItem(R.id.crearAdministrador).setVisible(true);
            item8 = menu.findItem(R.id.nav_EliminarAdministrador).setVisible(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        SharedPreferences sharedPreferences = getSharedPreferences(com.example.alarmacasablanca.Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean loggedIn = sharedPreferences.getBoolean(com.example.alarmacasablanca.Config.LOGGEDIN_SHARED_PREF, false);
        item1 = menu.findItem(R.id.nav_logout);
        item2 = menu.findItem(R.id.nav_AgregarCorreo);
        item3 = menu.findItem(R.id.nav_AgregarDispositivo);
        item4 = menu.findItem(R.id.nav_EliminarCorreo);
        item5 = menu.findItem(R.id.nav_EliminarDispositivo);
        item6 = menu.findItem(R.id.nav_login);
        item7 = menu.findItem(R.id.crearAdministrador);
        item8 = menu.findItem(R.id.tutorial);

        if (loggedIn == false) {
            item1.setVisible(false);
            item2.setVisible(false);
            item3.setVisible(false);
            item4.setVisible(false);
            item5.setVisible(false);
            item6.setVisible(true);
            item7.setVisible(false);
            item8.setVisible(true);
        } else {
            item1.setVisible(true);
            item2.setVisible(true);
            item3.setVisible(true);
            item4.setVisible(true);
            item5.setVisible(true);
            item6.setVisible(false);
            item7.setVisible(true);
            item8.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        closeOptionsMenu();
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        if (item.getItemId() == R.id.nav_logout) {
            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
            editor.apply();
            return NavigationUI.onNavDestinationSelected(item, navController)
                    || super.onOptionsItemSelected(item);
        } else if (item.getItemId() == R.id.tutorial) {
            startActivity(new Intent(MainActivity.this, tutorialActivity.class));
            return super.onOptionsItemSelected(item);
        } else {
            return NavigationUI.onNavDestinationSelected(item, navController)
                    || super.onOptionsItemSelected(item);
        }

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void afficher() {
        stopTimer();
        handler.postDelayed(runnable, 10000);
        if (contador == 1) {
            stopTimer();
            contador = 0;
            locaciones.delete(0, locaciones.length());
            nombres.delete(0, nombres.length());

            if (isNetworkConnected()) {
                Toast toast = Toast.makeText(getAppContext(),
                        "Correo enviado", Toast.LENGTH_SHORT);
                toast.show();

                for (int j = 0; j < dispositivos.size(); j++) {
                    if (numeros_de_celular.contains(dispositivos.get(j).getNumero())) {
                        nombres.append(", ");
                        nombres.append(dispositivos.get(j).getNombre());
                        GeoPoint ubi1 = dispositivos.get(j).getLocation();
                        Double ubi2 = ubi1.getLatitude();
                        Double ubi3 = ubi1.getLongitude();
                        String ubica1 = ubi2.toString();
                        String ubica2 = ubi3.toString();
                        locaciones.append("/");
                        locaciones.append(ubica1);
                        locaciones.append(",");
                        locaciones.append(ubica2);
                    }
                }
                if (!numeros_de_celular.isEmpty()) {
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

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                GMailSender sender = new GMailSender("EMAIL",
                                                        "EMAIL_PASSWORD");
                                                    if (numeros_de_celular.size() == 1) {
                                                        for (int x = 0; x < correos.size(); x++) {
                                                            sender.sendMail("ALARMA: Caida de servicio", "Alarma ubicada en" + nombres + "\n " +
                                                                            "https://www.google.com/maps/place" + locaciones + "/@-33.3097213,-71.4629706,12z",
                                                                    "EMAIL", correos.get(x));
                                                        }
                                                        numeros_de_celular.clear();
                                                    } else {
                                                        for (int x = 0; x < correos.size(); x++) {
                                                        sender.sendMail("ALARMA: Caida de servicio", "Alarma ubicada en" + nombres + "\n " +
                                                                        "https://www.google.com/maps/dir//" + locaciones + "//@-33.3097213,-71.4629706,12z",
                                                                "EMAIL", correos.get(x));
                                                    }
                                                        numeros_de_celular.clear();
                                                }
                                            } catch (Exception e) {
                                            }
                                        }

                                    }).start();
                                }
                            });

                }



            } else {
                Toast toast = Toast.makeText(getAppContext(),
                        "Celular sin internet, correo no enviado", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            contador++;
        }
    }

    public void startTimer() {
        runnable.run();
    }

    public void stopTimer() {
        handler.removeCallbacks(runnable);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            afficher();
        }
    };


    ////////////////////////////

    public void startService(View v) {

        Intent serviceIntent = new Intent(this, ExampleService.class);
        serviceIntent.putExtra("inputExtra", "Servicio de notificacion");

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);
    }


    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }








}

