package com.mocatto.db;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.mocatto.dto.Apointment;
import com.mocatto.util.Util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by froilan.ruiz on 6/21/2016.
 */
public class ApointmentDB {

    public static List<Apointment> listApointment(Activity activity) {
        EventSQLiteOpenHelper admin = new EventSQLiteOpenHelper(activity, Util.BDNAME, null, Util.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select id,eventName,appointmentDate,hour, location, minutesBefore,email,namePet," +
                        "type,status,createdBy,countReminder,bathFrecuency,foodBuyRegularity from Apointment", null);
        List<Apointment> listaEventos = new ArrayList<Apointment>();
        Apointment evento;
        if (fila!=null) {
            while (fila.moveToNext()) {
                evento = new Apointment();
                evento.setId(fila.getInt(0));
                evento.setNombre(fila.getString(1));
                evento.setFecha(fila.getString(2));
                evento.setHora(fila.getString(3));
                evento.setUbicacion(fila.getString(4));
                evento.setRecordarMinutosAntes(fila.getString(5));
                evento.setEmail(fila.getString(6));
                evento.setMascota(fila.getString(7));
                evento.setTipoEvento(fila.getString(8));
                evento.setEstado(fila.getInt(9));
                evento.setCreatedBy(fila.getInt(10));
                evento.setCountReminder(fila.getInt(11));
                evento.setBathFrecuency(fila.getInt(12));
                evento.setFoodBuyRegularity(fila.getInt(13));
                listaEventos.add(evento);
            }
        }else {
            Toast.makeText(activity, "No hay eventos: ", Toast.LENGTH_SHORT).show();
        }
        bd.close();
        return listaEventos;
    }

    public static int createApointment(Apointment evento, Activity activity) {
        EventSQLiteOpenHelper admin = new EventSQLiteOpenHelper(activity, Util.BDNAME, null, Util.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues params = new ContentValues();
        params.put("id",evento.getId());
        params.put("eventName",evento.getNombre());
        params.put("appointmentDate",evento.getFecha());
        params.put("hour",evento.getHora());
        params.put("Location",evento.getUbicacion());
        params.put("minutesBefore",evento.getRecordarMinutosAntes());
        params.put("email",evento.getEmail());
        params.put("namePet",evento.getMascota());
        params.put("type",evento.getTipoEvento());
        params.put("status",evento.getEstado());//pendiente
        params.put("createdBy",evento.getCreatedBy());
        params.put("countReminder",evento.getCountReminder());

        Integer foodBuyFrecuency, bathFrecuency;
        if (evento.getFoodBuyRegularity()!=null){
            foodBuyFrecuency=evento.getFoodBuyRegularity();
        }else{
            foodBuyFrecuency=0;
        }
        if (evento.getBathFrecuency()!=null){
            bathFrecuency=evento.getBathFrecuency();
        }else{
            bathFrecuency=0;
        }
        params.put("foodBuyRegularity",foodBuyFrecuency);
        params.put("bathFrecuency",bathFrecuency);
        //bathFrecuency integer, foodBuyRegularity integer

        long id = bd.insert("Apointment", null, params);
        bd.close();
        int retorno = (int) (long) id;
        return retorno;
    }


        public static List<Apointment> listApointmentByDate(String date, Activity activity, Hashtable hashtablePets) {
            EventSQLiteOpenHelper admin = new EventSQLiteOpenHelper(activity, Util.BDNAME, null, Util.VERSION);
            SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor fila = bd.rawQuery(
                    "select id,eventName,appointmentDate,hour, location, minutesBefore,email," +
                            "namePet,type,status,createdBy,countReminder,bathFrecuency,foodBuyRegularity from Apointment " +
                            "where appointmentDate='"+date+"'", null);
            List<Apointment> listaEventos = new ArrayList<Apointment>();
            Apointment evento;
            if (fila!=null) {
                while (fila.moveToNext()) {
                    evento = new Apointment();
                    evento.setId(fila.getInt(0));
                    evento.setNombre(fila.getString(1));
                    evento.setFecha(fila.getString(2));
                    evento.setHora(fila.getString(3));
                    evento.setUbicacion(fila.getString(4));
                    evento.setRecordarMinutosAntes(fila.getString(5));
                    evento.setEmail(fila.getString(6));
                    evento.setMascota(fila.getString(7));
                    evento.setTipoEvento(fila.getString(8));
                    evento.setEstado(fila.getInt(9));
                    evento.setCreatedBy(fila.getInt(10));
                    evento.setCountReminder(fila.getInt(11));
                    evento.setBathFrecuency(fila.getInt(12));
                    evento.setFoodBuyRegularity(fila.getInt(13));
                    if (hashtablePets!=null) {
                        evento.setEspecie((String) hashtablePets.get(evento.getMascota()));
                    }
                    listaEventos.add(evento);
                }
            }else {
                Toast.makeText(activity, "No hay eventos: ", Toast.LENGTH_SHORT).show();
            }
            bd.close();
            return listaEventos;
        }

    public static void updateApointment(Apointment evento, Activity activity) {
        EventSQLiteOpenHelper admin = new EventSQLiteOpenHelper(activity, Util.BDNAME, null, Util.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues params = new ContentValues();
        params.put("id",evento.getId());
        params.put("eventName",evento.getNombre());
        params.put("appointmentDate",evento.getFecha());
        params.put("hour",evento.getHora());
        params.put("Location",evento.getUbicacion());
        params.put("minutesBefore",evento.getRecordarMinutosAntes());
        params.put("email",evento.getEmail());
        params.put("namePet",evento.getMascota());
        params.put("type",evento.getTipoEvento());
        params.put("status",evento.getEstado());
        params.put("createdBy",evento.getCreatedBy());
        params.put("countReminder",evento.getCountReminder());
        Integer foodBuyFrecuency, bathFrecuency;
        if (evento.getFoodBuyRegularity()!=null){
            foodBuyFrecuency=evento.getFoodBuyRegularity();
        }else{
            foodBuyFrecuency=0;
        }
        if (evento.getBathFrecuency()!=null){
            bathFrecuency=evento.getBathFrecuency();
        }else{
            bathFrecuency=0;
        }
        params.put("foodBuyRegularity",foodBuyFrecuency);
        params.put("bathFrecuency",bathFrecuency);


        bd.update("Apointment", params, "id = "+evento.getId(),null);
        bd.close();
    }

    public static List<Apointment> listApointmentByMonth(String date,Activity activity) {
        String dateShort = date.substring(3);
        EventSQLiteOpenHelper admin = new EventSQLiteOpenHelper(activity, Util.BDNAME, null, Util.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select id,eventName,appointmentDate,hour, location, minutesBefore,email,namePet," +
                        "type,status,createdBy,countReminder,bathFrecuency,foodBuyRegularity " +
                        "from Apointment where substr(appointmentDate, 4)='"+dateShort+"'", null);
        List<Apointment> listaEventos = new ArrayList<Apointment>();
        Apointment evento;
        if (fila!=null) {
            while (fila.moveToNext()) {
                evento = new Apointment();
                evento.setId(fila.getInt(0));
                evento.setNombre(fila.getString(1));
                evento.setFecha(fila.getString(2));
                evento.setHora(fila.getString(3));
                evento.setUbicacion(fila.getString(4));
                evento.setRecordarMinutosAntes(fila.getString(5));
                evento.setEmail(fila.getString(6));
                evento.setMascota(fila.getString(7));
                evento.setTipoEvento(fila.getString(8));
                evento.setEstado(fila.getInt(9));
                evento.setCreatedBy(fila.getInt(10));
                evento.setCountReminder(fila.getInt(11));
                evento.setBathFrecuency(fila.getInt(12));
                evento.setFoodBuyRegularity(fila.getInt(13));
                listaEventos.add(evento);
            }
        }else {
            Toast.makeText(activity, "No hay eventos: ", Toast.LENGTH_SHORT).show();
        }
        bd.close();
        return listaEventos;
    }

    public static List<Apointment> getApointmentsForListInActionBar(String email, Activity activity,Hashtable hashtablePets) {
        EventSQLiteOpenHelper admin = new EventSQLiteOpenHelper(activity, Util.BDNAME, null, Util.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select id,eventName,appointmentDate,hour, location, minutesBefore,email,namePet,type," +
                        "status,createdBy,countReminder,bathFrecuency,foodBuyRegularity " +
                        "from Apointment where email='"+email+"' and (status="+Util.LANZADO+" or status="+Util.RECORDAR_DESPUES+") " +
                        "order by appointmentDate", null);

        List<Apointment> listaEventos = new ArrayList<Apointment>();
        Apointment evento;
        if (fila!=null) {
            while (fila.moveToNext()) {
                evento = new Apointment();
                evento.setId(fila.getInt(0));
                evento.setNombre(fila.getString(1));
                evento.setFecha(fila.getString(2));
                evento.setHora(fila.getString(3));
                evento.setUbicacion(fila.getString(4));
                evento.setRecordarMinutosAntes(fila.getString(5));
                evento.setEmail(fila.getString(6));
                evento.setMascota(fila.getString(7));
                evento.setTipoEvento(fila.getString(8));
                evento.setEstado(fila.getInt(9));
                evento.setCreatedBy(fila.getInt(10));
                evento.setCountReminder(fila.getInt(11));
                evento.setBathFrecuency(fila.getInt(12));
                evento.setFoodBuyRegularity(fila.getInt(13));
                if (hashtablePets!=null) {
                    evento.setEspecie((String) hashtablePets.get(evento.getMascota()));
                }
                listaEventos.add(evento);
            }
        }else {
            Toast.makeText(activity, "No hay eventos: ", Toast.LENGTH_SHORT).show();
        }
        bd.close();
        return listaEventos;
    }

    public static void updateStatusApointment(Apointment evento, Integer status, Context context) {
        EventSQLiteOpenHelper admin = new EventSQLiteOpenHelper(context, Util.BDNAME, null, Util.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues params = new ContentValues();
        params.put("id",evento.getId());
        params.put("eventName",evento.getNombre());
        params.put("appointmentDate",evento.getFecha());
        params.put("hour",evento.getHora());
        params.put("Location",evento.getUbicacion());
        params.put("minutesBefore",evento.getRecordarMinutosAntes());
        params.put("email",evento.getEmail());
        params.put("namePet",evento.getMascota());
        params.put("type",evento.getTipoEvento());
        params.put("status",status);
        params.put("createdBy",evento.getCreatedBy());
        Integer counter = evento.getCountReminder();
        if (status==Util.LANZADO){
            counter++;
        }
        params.put("countReminder",counter);
        Integer foodBuyFrecuency, bathFrecuency;
        if (evento.getFoodBuyRegularity()!=null){
            foodBuyFrecuency=evento.getFoodBuyRegularity();
        }else{
            foodBuyFrecuency=0;
        }
        if (evento.getBathFrecuency()!=null){
            bathFrecuency=evento.getBathFrecuency();
        }else{
            bathFrecuency=0;
        }
        params.put("foodBuyRegularity",foodBuyFrecuency);
        params.put("bathFrecuency",bathFrecuency);
        int upd = bd.update("Apointment", params, "id = "+evento.getId(),null);
        bd.close();
    }

    public static void deleteApointment(Apointment evento, Activity activity) {
        EventSQLiteOpenHelper admin = new EventSQLiteOpenHelper(activity, Util.BDNAME, null, Util.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String whereClause = "createdBy= "+ Util.CREATE_AUTOMATICALLY+" and namePet='"+evento.getMascota()+"' and " +
                "email='"+evento.getEmail()+"' and eventName='"+evento.getNombre()+"'";
        bd.delete("Apointment",whereClause ,null);
        bd.close();
    }
}
