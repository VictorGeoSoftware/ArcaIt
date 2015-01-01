package com.arcait.victor.arcait;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Victor on 1/1/15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver{

    TextView txtEstadoConexion;

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork == null){
            Toast.makeText(context, "Se ha perdido la conexión a internet", Toast.LENGTH_SHORT).show();
            MainActivity.txtEstadoConexion.setTextColor(Color.RED);

            MainActivity.txtEstadoConexion.setText("Desconectada");
        } else{
            Toast.makeText(context, "Se ha recuperado la conexión a internet", Toast.LENGTH_SHORT).show();
            MainActivity.txtEstadoConexion.setTextColor(Color.GREEN);
            MainActivity.txtEstadoConexion.setText("Activa" + " "
            + activeNetwork.getTypeName());
        }
    }
}
