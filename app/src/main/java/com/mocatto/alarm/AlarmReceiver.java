package com.mocatto.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.mocatto.NavigationDrawerActivity;
import com.mocatto.R;
import com.mocatto.db.ApointmentDB;
import com.mocatto.dto.Apointment;
import com.mocatto.dto.Cuenta;
import com.mocatto.util.Util;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by froilan.ruiz on 7/26/2016.
 */
public class AlarmReceiver extends android.content.BroadcastReceiver {

    private Context context;
    private NotificationManager notifyMgr;
    private Cuenta cuenta;
    private Apointment evento;
    private int idEvento;

    @Override
    public void onReceive(android.content.Context cont, android.content.Intent intent) {
        context=cont;
        notifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        evento = (Apointment) intent.getExtras().getSerializable("evento");
        cuenta = (Cuenta) intent.getExtras().getSerializable("cuenta");
        idEvento = intent.getExtras().getInt("idEvento");
        boolean fromAlarmReceiver = true;


        //Cuando la fecha ya paso no lanzar la notificación si el evento es un cumpleaños
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        if (evento.getTipoEvento().equals("Cumpleaños") || evento.getTipoEvento().equals("Birthday")){
            Date date = null;
            try {
                date = simpleDateFormat.parse(evento.getFecha());
            } catch (ParseException e) {
                Log.println(Log.ERROR,"AlarmReceiver",e.getMessage());
            }
            if (date!=null){
                if(Util.isDateInThePast(date)){
                    ApointmentDB.updateStatusApointment(evento, Util.YA_LO_HICE,context);
                }else{
                    ApointmentDB.updateStatusApointment(evento, Util.LANZADO,context);
                    notification();
                }
            }
        }else {
            ApointmentDB.updateStatusApointment(evento, Util.LANZADO,context);
            notification();
        }
    }

    public String setMessageForPush(){
        String message="";
        if (evento.getTipoEvento().equals("Baño") || evento.getTipoEvento().equals("Shower")){
            message=context.getString(R.string.mje_push_banio)+evento.getMascota();
        }else if (evento.getTipoEvento().equals("Compra comida") || evento.getTipoEvento().equals("Buy food")){
            message=context.getString(R.string.mje_push_compracomida);
        }else if (evento.getTipoEvento().equals("Cumpleaños") || evento.getTipoEvento().equals("Birthday")){
            message=context.getString(R.string.mje_push_cumpleanios);
        }else if (evento.getTipoEvento().equals("Desparacitada") || evento.getTipoEvento().equals("Deworming")){
            message=context.getString(R.string.mje_push_desparacitacion)+evento.getMascota();
        }else if (evento.getTipoEvento().equals("Otro tipo de evento") || evento.getTipoEvento().equals("Other kind of event")){
            message=context.getString(R.string.mje_mocatto_reminder);
        }else if (evento.getTipoEvento().equals("Salida de paseo") || evento.getTipoEvento().equals("Walkout")){
            message=context.getString(R.string.mje_mocatto_reminder);
        }else if (evento.getTipoEvento().equals("Vacuna") || evento.getTipoEvento().equals("Vaccine")){
            message=context.getString(R.string.mje_push_vacuna)+evento.getMascota();
        }else if (evento.getTipoEvento().equals("Visita al veterinario") || evento.getTipoEvento().equals("Visit to veterinarian")){
            message=context.getString(R.string.mje_push_visitaveterinario)+evento.getMascota();
        }
        return message;
    }

    public void notification() {
        String message = setMessageForPush();
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_logo_push_blanco_small)//se cambiara por icono con burbuja en blanco y con la M de mocatto en transparente
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_notificacion))
                        .setContentTitle(context.getResources().getString(R.string.reminder))
                        .setContentText(message)
                        .setColor(context.getResources().getColor(R.color.black));

        
        // Creamos el Intent que llamará a nuestra Activity
        Intent targetIntent = new Intent(context,
                NavigationDrawerActivity.class);
        targetIntent.putExtra("evento",(Serializable) evento);
        targetIntent.putExtra("fromAlarmReceiver",true);
        //targetIntent.putExtra("isFromCreateAccount","false");
        targetIntent.putExtra("cuenta",cuenta);

        // Creamos el PendingIntent
        PendingIntent contentIntent = PendingIntent.getActivity(
                context, 0, targetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        builder.setAutoCancel(true);


        // Construir la notificación y emitirla
        notifyMgr.notify((int) System.currentTimeMillis(), builder.build());
    }
}