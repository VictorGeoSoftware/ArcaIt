package com.arcait.victor.arcait;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Browser;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;


public class MainActivity extends ActionBarActivity{

    //----- Declaro elementos
    static TextView txtEstadoConexion;
    TextView txtEstadoTethering;
    TextView txtDatosRecibidos;
    TextView txtDatosTotales;

    TextView txtLatitud;
    TextView txtLongitud;
    TextView txtAltura;

    ListView lstHistorial;


    Handler dataHandler = new Handler();
    LocationManager locationManager;
    LocationListener locListenerGps;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //----- Inicializo elementos
        txtEstadoConexion = (TextView) findViewById(R.id.txt_estado_conexion);
        txtEstadoTethering = (TextView) findViewById(R.id.txt_estado_tethering);
        txtDatosRecibidos = (TextView) findViewById(R.id.txt_datos_recibidos);
        txtDatosTotales = (TextView) findViewById(R.id.txt_datos_totales);

        txtLatitud = (TextView) findViewById(R.id.txt_latitud);
        txtLongitud = (TextView) findViewById(R.id.txt_longitud);
        txtAltura = (TextView) findViewById(R.id.txt_altura);

        lstHistorial = (ListView) findViewById(R.id.listView);


        locListenerGps = new MiLocationListener();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListenerGps);


        //----- Controlo estado conexión (En BroadCast Receiver)
        ConnectivityManager cnm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cnm.getActiveNetworkInfo();
        if(activeNetworkInfo != null){
            if(activeNetworkInfo.isConnected()){
                txtEstadoConexion.setTextColor(Color.GREEN);
                txtEstadoConexion.setText(getString(R.string.activa) + " " + activeNetworkInfo.getTypeName());
            }else{
                txtEstadoConexion.setTextColor(Color.RED);
                txtEstadoConexion.setText(getString(R.string.desconectada));
            }
        }


        //----- Controlo WIFI y enciendo Tethering
        txtEstadoTethering.setTextColor(Color.RED);
        txtEstadoTethering.setText(getString(R.string.desconectada));
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if(wifi.isWifiEnabled()){
            wifi.setWifiEnabled(false);
        }

        Method[] wmMethods = wifi.getClass().getDeclaredMethods();

        for(Method method: wmMethods){
            if(method.getName().equals("setWifiApEnabled")){
                try{
                    method.invoke(wifi, null, true);
                    txtEstadoTethering.setTextColor(Color.GREEN);
                    txtEstadoTethering.setText(getString(R.string.activa));
                }catch (Exception e){
                    Log.i("", "---------------- ERROR BUCLE 2 ----------------");
                    e.printStackTrace();
                }

                break;
            }
        }


        //----- Historial de navegación --> Solo funciona para el historial del propio telefono
//        String[] proj = new String[] {Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL};
//        String sel = Browser.BookmarkColumns.BOOKMARK + " =0";
//        Cursor c = getContentResolver().query(Browser.BOOKMARKS_URI, proj, sel, null, null);
//
//        c.moveToFirst();
//
//        String title = "";
//        String url = "";
//
//        if(c.moveToFirst() && c.getCount() > 0){
//            boolean cont = true;
//
//            while (c.isAfterLast() == false && cont){
//                title = c.getString(c.getColumnIndex(Browser.BookmarkColumns.TITLE));
//                url = c.getString(c.getColumnIndex(Browser.BookmarkColumns.URL));
//                Log.i("", "Valores: " + title + " - " + url);
//                c.moveToNext();
//            }
//        }


        //----- Consumo de datos
        dataHandler.postDelayed(runnable, 1000);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            DecimalFormat formato = new DecimalFormat("###,###,###,###,###");
            long datosRecibidos = 0;
            long totalDatos = 0;
            datosRecibidos = android.net.TrafficStats.getMobileRxBytes();
            totalDatos = android.net.TrafficStats.getTotalRxBytes();

            txtDatosRecibidos.setText(formato.format(datosRecibidos) + " bytes");
            txtDatosTotales.setText(formato.format(totalDatos) + " bytes");

            dataHandler.postDelayed(this, 1000);
        }
    };

    public class MiLocationListener implements LocationListener {
        // Se da por supuesto que el GPS estará conectado por defecto
        DecimalFormat formato = new DecimalFormat("0.000");

        public void onLocationChanged(Location loc){
            txtLatitud.setText(loc.getLatitude() + "º");
            txtLongitud.setText(loc.getLongitude() + "º");
            txtAltura.setText(formato.format(loc.getAltitude()) + " m");
        }

        public void onProviderDisabled(String provider){

        }

        public void onProviderEnabled (String provider){

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
}
