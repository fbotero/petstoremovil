package com.mocatto.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mocatto.R;
import com.mocatto.db.ApointmentDB;
import com.mocatto.dto.Apointment;
import com.mocatto.dto.Cuenta;
import com.mocatto.dto.Pet;
import com.mocatto.util.Util;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by froilan.ruiz on 8/7/2016.
 */
public class Alarm {
    private static final String HORA_RECORDATORIO="10:00";
    private static final int SIETE = 7;
    private static final int CINCO = 5;
    private static final int VEINTITRES = 23;
    private static final int ONCE = 11;
    private static final int TRES = 3;
    private static final int QUINCE = 15;
    private static final int DOS = 2;

    public static void createAlarm(Apointment evento, Activity activity, Cuenta cuenta) {
        String[] hora = evento.getHora().split(":");
        String[] fecha = evento.getFecha().split("-");

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();

        cal.set(Calendar.DAY_OF_MONTH,Integer.valueOf(fecha[0]));
        cal.set(Calendar.MONTH,Integer.valueOf(fecha[1])-1);
        cal.set(Calendar.YEAR,Integer.valueOf(fecha[2]));

        cal.set(Calendar.AM_PM, Calendar.AM );
        cal.set(Calendar.HOUR, Integer.valueOf(hora[0]));

        if( Integer.valueOf(hora[0]) >= 12){
            cal.set(Calendar.AM_PM, Calendar.PM );
            cal.set(Calendar.HOUR, Integer.valueOf(hora[0])-12);
        }
        cal.set(Calendar.MINUTE, Integer.valueOf(hora[1]) );
        cal.set(Calendar.SECOND, 0 );


        AlarmManager alarmMgr = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, AlarmReceiver.class);

        intent.putExtra("evento", evento);
        intent.putExtra("cuenta",cuenta);
        intent.putExtra("idEvento",evento.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        // cal.add(Calendar.SECOND, 5);
            alarmMgr.set(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);
        //alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10 * 1000, pendingIntent);
        //Log.println(Log.DEBUG,"createAlarm","Se almacenó la fecha y se creo la alarma: "+evento.toString());
    }

    public static void createAlarmSeveralTimes(Apointment evento, Activity activity, Cuenta cuenta) {
        String[] hora = evento.getHora().split(":");
        String[] fecha = evento.getFecha().split("-");

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();

        cal.set(Calendar.DAY_OF_MONTH,Integer.valueOf(fecha[0]));
        cal.set(Calendar.MONTH,Integer.valueOf(fecha[1])-1);
        //No se pone el año para que se programa cada año.como por ejemplo el cumpleaños

        cal.set(Calendar.AM_PM, Calendar.AM );
        cal.set(Calendar.HOUR, Integer.valueOf(hora[0]));

        if( Integer.valueOf(hora[0]) >= 12){
            cal.set(Calendar.AM_PM, Calendar.PM );
            cal.set(Calendar.HOUR, Integer.valueOf(hora[0])-12);
        }
        cal.set(Calendar.MINUTE, Integer.valueOf(hora[1]) );
        cal.set(Calendar.SECOND, 0 );


        AlarmManager alarmMgr = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, AlarmReceiver.class);

        intent.putExtra("evento",(Serializable) evento);
        intent.putExtra("cuenta",cuenta);
        intent.putExtra("idEvento",evento.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        // cal.add(Calendar.SECOND, 5);
        //alarmMgr.set(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);

        alarmMgr.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), millisUntilNextYear(),pendingIntent);


        //Log.println(Log.DEBUG,"createAlarmSeveralTimes","Se almacenó la fecha y se creo la alarma");
    }

    private static long millisUntilNextYear(){

        //Set days in a year for Leap and Regular
        final int daysInLeapYear = 366;
        final int daysInYear = 365;

        //Get calendar instance
        GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();

        //Get this year and next year
        int thisYear = cal.get(GregorianCalendar.YEAR);
        int nextYear = thisYear + 1;

        //Get today's month
        int thisMonth = cal.get(GregorianCalendar.MONTH);

        //Get today's date
        int dayOfMonth = cal.get(GregorianCalendar.DAY_OF_MONTH);

        //Is today before February? If so then the following February is in THIS year
        if (thisMonth < GregorianCalendar.FEBRUARY){

            //Check if THIS year is leapYear, and return correct days (converted to millis)
            return cal.isLeapYear(thisYear) ? daysToMillis(daysInLeapYear) : daysToMillis(daysInYear);
        }

        //Is today after February? If so then the following February is NEXT year
        else if (thisMonth > GregorianCalendar.FEBRUARY) {
            //Check if NEXT year is leapYear, and return correct days (converted to millis)
            return cal.isLeapYear(nextYear) ? daysToMillis(daysInLeapYear) : daysToMillis(daysInYear);
        }

        //Then today must be February.
        else {
            //Special case: today is February 29
            if (dayOfMonth == 29){
                return daysToMillis(daysInYear);
            } else {
                //Check if THIS year is leapYear, and return correct days (converted to millis)
                return cal.isLeapYear(thisYear) ? daysToMillis(daysInLeapYear) : daysToMillis(daysInYear);
            }
        }
    }

    public static long daysToMillis(double day)
    {
        return (long) (day * 24 * 60 * 60 * 1000);
    }

    public static void createApointmentFromPetProfileInformation(Pet pet, Activity activity, Cuenta cuenta){
        if (pet.getBirthDate()!=null){
            //Date fechaCumpleanios = Util.sumYearsToDate(pet.getBirthDate(),1);
            //Date fechaEvento = Util.sumDaysToDate(fechaCumpleanios,-1);
            Date fechaCumpleanios=pet.getBirthDate();
            //Armar la fecha de cumpleaños del año actual
            SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");
            String fechaCumpleString = formatoDelTexto.format(fechaCumpleanios);
            String anio= fechaCumpleString.split("-")[0];
            String mes= fechaCumpleString.split("-")[1];
            String dia= fechaCumpleString.split("-")[2];


            Date fechaEvento;

            //Obtener el día y mes del cumpleaños
            int month = fechaCumpleanios.getMonth();
            int day = fechaCumpleanios.getDay();
            //Obtener año actual

            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);


            String strFecha = year+"-"+mes+"-"+dia;
            Date fecha = null;
            try {
                fecha = formatoDelTexto.parse(strFecha);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }

            if (Util.isDateInThePast(fecha)){
                year=year+1;
                strFecha = year+"-"+mes+"-"+dia;
                try {
                    fecha = formatoDelTexto.parse(strFecha);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
            fechaCumpleanios = fecha;
            fechaEvento = Util.sumDaysToDate(fechaCumpleanios,-1);

            String mensaje = activity.getString(R.string.not_mje_manana) + pet.getName() + activity.getString(R.string.not_mje_birthday_before);
            int tipoEvento = R.string.tipoevento_birthday;

            //Log.println(Log.INFO,"createAlarm","fechaCumpleanios: "+fechaCumpleanios +" fechaEvento:"+fechaEvento);
            //Log.println(Log.INFO,"createAlarm","mensaje: "+mensaje);
            //Crear un recordatorio 1 día antes del cumpleaños
            createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
            //Crear un recordatorio el día de su cumpleaños
            mensaje = pet.getName() + activity.getString(R.string.not_mje_birthday);
            createApointment(pet, activity, cuenta, fechaCumpleanios, mensaje,tipoEvento);
        }
    }

    private static void createApointment(Pet pet, Activity activity, Cuenta cuenta, Date fechaEvento, String mensaje,int tipoEvento) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Apointment apointment = new Apointment();
        apointment.setCountReminder(Util.CERO);
        apointment.setFecha(simpleDateFormat.format(fechaEvento));
        apointment.setCreatedBy(Util.CREATE_AUTOMATICALLY);
        apointment.setEmail(pet.getEmail());
        apointment.setEspecie(pet.getSpecie());
        apointment.setEstado(Util.PENDIENTE);
        apointment.setHora(HORA_RECORDATORIO);
        apointment.setNombre(mensaje);
        apointment.setRecordarMinutosAntes("");
        apointment.setTipoEvento(activity.getResources().getString(tipoEvento));
        apointment.setUbicacion("");
        apointment.setMascota(pet.getName());
        apointment.setBathFrecuency(pet.getBathFrecuency());
        apointment.setFoodBuyRegularity(pet.getFoodBuyRegularity());

        //Se eliminan eventos similares ya creados para poderlo presentar
        ApointmentDB.deleteApointment(apointment,activity);

        apointment.setId(ApointmentDB.createApointment(apointment,activity));
        createAlarm(apointment,activity,cuenta);
    }

    private static void createApointmentSeveralTimes(Pet pet, Activity activity, Cuenta cuenta, Date fechaEvento, String mensaje,int tipoEvento) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Apointment apointment = new Apointment();
        apointment.setCountReminder(Util.CERO);
        apointment.setFecha(simpleDateFormat.format(fechaEvento));
        apointment.setCreatedBy(Util.CREATE_AUTOMATICALLY);
        apointment.setEmail(pet.getEmail());
        apointment.setEspecie(pet.getSpecie());
        apointment.setEstado(Util.PENDIENTE);
        apointment.setHora(HORA_RECORDATORIO);
        apointment.setNombre(mensaje);
        apointment.setRecordarMinutosAntes("");
        apointment.setTipoEvento(activity.getResources().getString(tipoEvento));
        apointment.setUbicacion("");
        apointment.setMascota(pet.getName());
        apointment.setBathFrecuency(pet.getBathFrecuency());
        apointment.setFoodBuyRegularity(pet.getFoodBuyRegularity());

        //Se eliminan eventos similares ya creados para poderlo presentar
        ApointmentDB.deleteApointment(apointment,activity);

        apointment.setId(ApointmentDB.createApointment(apointment,activity));

        createAlarmSeveralTimes(apointment,activity,cuenta);
    }

    public static void createApointmentsFromPetInformation(Pet pet, Activity activity, Cuenta cuenta){
        long edad = getDaysBetweenDates(pet.getBirthDate(),new Date());
        String mensaje = "";
        int tipoEvento = 0;
        Date fechaEvento=null;
        Date fechaTemporal=null;
        if (pet.getSpecie().equals(activity.getString(R.string.dog))){
            //if (pet.getVaccine()!=null) {
                tipoEvento = R.string.tipoevento_vacuna;
                if (edad < 6) {
                    if (pet.getVaccine().equals(activity.getString(R.string.vacuna_ninguna))) {
                        //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets,
                        // que debe aplicarle la vacuna Moquillo y Parvo virus
                        fechaEvento = Util.sumDaysToDate(new Date(),SIETE);
                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_parvovirus);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_parvovirus))) {
                        //o	Se le debe recordar cuando el perro cumpla 5 meses y 23 días que debe aplicarse la vacuna Quíntuple
                        if (pet.getLastVaccine()!=null) {
                            fechaTemporal = Util.sumMonthsToDate(pet.getLastVaccine(), CINCO);
                            fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                        }else{
                            fechaEvento = new Date();
                        }


                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_quintuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_quintuple))) {
                        //o	Se le debe recordar cuando el perro cumpla 7 meses y 23 días que debe aplicarse
                        // la vacuna Segunda Quíntuple
                        if (pet.getLastVaccine()!=null) {
                            fechaTemporal = Util.sumMonthsToDate(pet.getLastVaccine(), SIETE);
                            fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                        }else{
                            fechaEvento = new Date();
                        }

                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_2da_quintuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_2da_quintuple))) {
                        //o	Se le debe recordar cuando el perro cumpla 11 meses y 23 días que debe aplicarse la vacuna séxtuple,
                        // con respecto a la fecha de la vacunación de la segunda quíntuple.
                        if (pet.getLastVaccine()!=null) {
                            fechaTemporal = Util.sumMonthsToDate(pet.getLastVaccine(),ONCE);
                            fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                        }else{
                            fechaEvento = new Date();
                        }


                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_sextuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
                    }
                } else if (edad >= 6 && edad < 8) {
                    if (pet.getVaccine().equals(activity.getString(R.string.vacuna_ninguna))) {
                        //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets,
                        // que debe aplicarle la vacuna Quíntuple
                        fechaEvento = Util.sumDaysToDate(new Date(),SIETE);
                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_quintuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_parvovirus))) {
                        //o	Se le debe recordar cuando el perro cumpla 5 meses y 23 días que debe aplicarse la vacuna Quíntuple
                        if (pet.getLastVaccine()!=null) {
                            fechaTemporal = Util.sumMonthsToDate(pet.getLastVaccine(),CINCO);
                            fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                        }else{
                            fechaEvento = new Date();
                        }


                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_quintuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_quintuple))) {
                        //o	Se le debe recordar la segunda quíntuple cuando el perro cumpla 7 meses y 23 días de nacido.
                        if (pet.getLastVaccine()!=null) {
                            fechaTemporal = Util.sumMonthsToDate(pet.getLastVaccine(),SIETE);
                            fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                        }else{
                            fechaEvento = new Date();
                        }


                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_2da_quintuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_2da_quintuple))) {
                        //o	Se le debe recordar cuando el perro cumpla 11 meses y 23 días que debe aplicarse la vacuna Séxtuple,
                        // con respecto a la fecha en que se aplico la segunda Quíntuple.
                        if (pet.getLastVaccine()!=null) {
                            fechaTemporal = Util.sumMonthsToDate(pet.getLastVaccine(),ONCE);
                            fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                        }else{
                            fechaEvento = new Date();
                        }


                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_sextuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
                    }

                } else if (edad >= 8 && edad < 10) {
                    if (pet.getVaccine().equals(R.string.vacuna_ninguna)) {
                        //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets, que debe aplicarle
                        // la vacuna Segunda Quíntuple
                        fechaEvento = Util.sumDaysToDate(new Date(),SIETE);
                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_2da_quintuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    } else if (pet.getVaccine().equals(R.string.vacuna_parvovirus)) {
                        //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets,
                        // que debe aplicarle la vacuna Quíntuple
                        fechaEvento = Util.sumDaysToDate(new Date(),SIETE);
                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_quintuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    } else if (pet.getVaccine().equals(R.string.vacuna_quintuple)) {
                        //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets, que debe aplicarle
                        // la vacuna Segunda Quíntuple
                        fechaEvento = Util.sumDaysToDate(new Date(),SIETE);
                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_2da_quintuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_sextuple))) {
                        //o	Con respecto al día que fue vacunado con la séxtuple, se le debe recordar a los 11 meses y 23 días
                        // que se le debe volver a aplicar la Séxtuple.
                        if (pet.getLastVaccine()!=null) {
                            fechaTemporal = Util.sumMonthsToDate(pet.getLastVaccine(),ONCE);
                            fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                        }else{
                            fechaEvento = new Date();
                        }


                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_sextuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
                    }
                } else if (edad >= 10 && edad < 12) {//NO ESTA DEFINIDA EN EL DOCUMENTO DE REQUERIMIENTOS
                  /*  if (pet.getVaccine().equals("Ninguna")){
                        //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets, que debe aplicarle la
                         v acuna Segunda Quíntuple

                    }else if (pet.getVaccine().equals("Parvovirus")){
                        //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets, que debe aplicarle la vacuna Quíntuple

                    }else if (pet.getVaccine().equals("Quintuple")){
                        //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets, que debe aplicarle la
                         v acuna Segunda Quíntuple

                    }else if (pet.getVaccine().equals("Sextuple")){
                        //o	Con respecto al día que fue vacunado con la séxtuple, se le debe recordar a los 11 meses y 23 días
                        que se le debe volver a aplicar la Séxtuple.
                    }*/
                } else if (edad > 10) {//originalmente era 12 pero al no estar especificado entre 10 y 12 se pone desde los 10 meses
                    if (pet.getVaccine().equals(R.string.vacuna_ninguna)) {
                        //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets,
                        // que debe aplicarle la vacuna Sextuple
                        fechaEvento = Util.sumDaysToDate(new Date(),SIETE);
                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_sextuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    } else if (pet.getVaccine().equals(R.string.vacuna_parvovirus)) {
                        //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets,
                        // que debe aplicarle la vacuna Quíntuple
                        fechaEvento = Util.sumDaysToDate(new Date(),SIETE);
                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_quintuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    } else if (pet.getVaccine().equals(R.string.vacuna_quintuple) || pet.getVaccine().equals(R.string.vacuna_sextuple) ||
                            pet.getVaccine().equals(R.string.vacuna_2da_quintuple)) {
                        //o	Se le debe programar el recordatorio a los 11 meses y 23 días que se le aplique la vacuna Séxtuple
                        if (pet.getLastVaccine()!=null) {
                            fechaTemporal = Util.sumMonthsToDate(pet.getLastVaccine(),ONCE);
                            fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                        }else{
                            fechaEvento = new Date();
                        }

                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_sextuple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    }
                }
            //}

            //Tos de perrera
            tipoEvento = R.string.tipoevento_vacuna;
            if (pet.getWoopingCough().equals(activity.getString(R.string.si))){
                //o	Se debe programar el recordatorio a los 11 meses y 23 días de día que fue aplicada la vacuna.
                fechaTemporal = Util.sumMonthsToDate(pet.getWoopingCoughDate(),ONCE);
                fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                        activity.getString(R.string.not_mje_vacuna)+
                        activity.getString(R.string.tos_de_perrera);
                createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
            }else {
                //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets,
                // que debe aplicarle la vacuna TOS DE PERRERA
                fechaEvento = Util.sumDaysToDate(new Date(),SIETE);
                mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                        activity.getString(R.string.not_mje_vacuna)+
                        activity.getString(R.string.tos_de_perrera);
                createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
            }

            //SPA
            tipoEvento = R.string.tipoevento_banio;
            if (pet.getLastBath()!=null && pet.getBathFrecuency()!=null){
                //Crea un evento en la fecha = fechaUltimBaño + Nro días de cada cuanto baña la mascota
                fechaEvento = Util.sumDaysToDate(pet.getLastBath(),pet.getBathFrecuency());
                if (Util.isDateInThePast(fechaEvento)){
                    fechaEvento = Util.sumDaysToDate(2);
                }
                mensaje = pet.getName()+activity.getString(R.string.not_mje_banio);
                createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
            }


        } else if (pet.getSpecie().equals(activity.getString(R.string.cat))){
            //if (pet.getVaccine()!=null) {
                tipoEvento = R.string.tipoevento_vacuna;
                if (edad < 2) {
                    if (pet.getVaccine().equals(activity.getString(R.string.vacuna_ninguna))) {
                        //o	Se le debe recordar a los 5 días de haber sido registrado en Mocatto Pets, que debe
                        // aplicarle la vacuna Triple Felina
                        if (pet.getLastVaccine()!=null) {
                            fechaEvento = Util.sumDaysToDate(pet.getLastVaccine(),CINCO);
                        }else{
                            fechaEvento = new Date();
                        }

                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_triple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);

                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_triple))) {
                        //o	Se le debe recordar cuando el gato cumpla 2 meses y 23 días que debe aplicarse la
                        // vacuna Triple Felina x 2da vez
                        if (pet.getLastVaccine()!=null) {
                            fechaTemporal = Util.sumMonthsToDate(pet.getLastVaccine(),DOS);
                        }else{
                            fechaTemporal = new Date();
                        }

                        fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_2nd_triple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_2nd_triple))) {
                        //o	Se le debe recordar cuando el perro cumpla 3 meses y 23 días que debe
                        // aplicarse la vacuna Triple Felina + Rabia
                        if (pet.getLastVaccine()!=null) {
                            fechaTemporal = Util.sumMonthsToDate(pet.getLastVaccine(),TRES);
                        }else{
                            fechaTemporal= new Date();
                        }

                        fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_triple_rabia);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
                    }
                } else if (edad >= 2 && edad < 4) {
                    if (pet.getVaccine().equals(activity.getString(R.string.vacuna_ninguna))) {
                        //o	Se le debe recordar a los 5 días de haber sido registrado en Mocatto Pets, que debe
                        // aplicarle la vacuna Triple Felina
                        if (pet.getLastVaccine()!=null) {
                            fechaEvento = Util.sumDaysToDate(pet.getLastVaccine(),CINCO);
                        }else{
                            fechaEvento = new Date();
                        }

                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_triple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_triple))) {
                        //o	Se le debe recordar a los 5 días de haber sido registrado en Mocatto Pets, que debe
                        // aplicarle la vacuna Triple Felina x 2da vez
                        if (pet.getVaccine()!=null) {
                            fechaEvento = Util.sumDaysToDate(pet.getLastVaccine(),CINCO);
                        }else{
                            fechaEvento = new Date();
                        }

                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_2nd_triple);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_triple_rabia))) {
                        //o	Se le debe recordar cuando el gato cumpla 11 meses y 23 días
                        // que debe aplicarse la vacuna Triple Felina + Rabia
                        if (pet.getLastVaccine()!=null) {
                            fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                        }else{
                            fechaEvento = new Date();
                        }


                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_triple_rabia);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
                    }
                } else if (edad >= 4 && edad < 6) {
                    if (pet.getVaccine().equals(activity.getString(R.string.vacuna_ninguna))) {
                        //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets, que debe
                        // aplicarle la vacuna Triple Felina + Rabia
                        if (pet.getLastVaccine()!=null) {
                            fechaEvento = Util.sumDaysToDate(pet.getLastVaccine(),SIETE);
                        }else{
                            fechaEvento = new Date();
                        }

                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_triple_rabia);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_2nd_triple))) {
                        //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets, que debe
                        // aplicarle la vacuna Triple Felina + Rabia
                        if (pet.getLastVaccine()!=null) {
                            fechaEvento = Util.sumDaysToDate(pet.getLastVaccine(),SIETE);
                        }else{
                            fechaEvento = new Date();
                        }

                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_triple_rabia);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
                    } else if (pet.getVaccine().equals(activity.getString(R.string.vacuna_triple_rabia))) {
                        //o	Con respecto al día que fue vacunado con la Triple Felina + Rabia, se le debe
                        // a los 11 meses y 23 días que se le debe volver a aplicar la Triple Felina + Rabia
                        if (pet.getLastVaccine()!=null) {
                            fechaTemporal = Util.sumMonthsToDate(pet.getLastVaccine(),ONCE);
                        }else{
                            fechaTemporal = new Date();
                        }

                        fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                        mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                                activity.getString(R.string.not_mje_vacuna)+
                                activity.getString(R.string.vacuna_triple_rabia);
                        createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
                    }
                }
            //}
            tipoEvento = R.string.tipoevento_vacuna;
            if (pet.getAidsLeukemiaVaccine().equals(activity.getString(R.string.si))) {
                //o	Se debe programar el recordatorio a los 11 meses y 23 días de día que fue aplicada la vacuna
                fechaTemporal = Util.sumMonthsToDate(pet.getAidsLeukemiaVaccineDate(),ONCE);
                fechaEvento = Util.sumDaysToDate(fechaTemporal,VEINTITRES);
                mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                        activity.getString(R.string.not_mje_vacuna)+
                        activity.getString(R.string.vacuna_leucemia_sida);
                createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
            } else {
                //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets, que debe aplicarle
                // la vacuna LEUCEMIA Y SIDA FELINO
                fechaEvento = Util.sumDaysToDate(new Date(),SIETE);
                mensaje = activity.getString(R.string.not_mje_a)+pet.getName()+
                        activity.getString(R.string.not_mje_vacuna)+
                        activity.getString(R.string.vacuna_leucemia_sida);
                createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
            }
        }

        //Ultima desparacitada
        tipoEvento = R.string.tipoevento_desparacitada;
        if (pet.getLastWorm()!=null){
            //o	Se le debe programar los recordatorio de desparasitación cada 3 meses y 15 días con respecto a la
            // ultima fecha de desparasitación
            fechaTemporal = Util.sumMonthsToDate(pet.getLastWorm(),TRES);
            fechaEvento = Util.sumDaysToDate(fechaTemporal,QUINCE);
            mensaje = pet.getName()+ activity.getString(R.string.not_mje_desparacitar);
            createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
        }else {
            //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets, que debe desparasitar cada 4 meses
            fechaEvento = Util.sumDaysToDate(new Date(),SIETE);
            mensaje = pet.getName()+ activity.getString(R.string.not_mje_desparacitar);
            createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
        }

        //Ultima visita al veterinario
        tipoEvento = R.string.tipoevento_visitaveterinario;
        if (pet.getLastVetVisit()!=null){
            //o	Se le debe programar los recordatorio de Visita de Rutina al Veterinario cada 5 meses y 15 días
            // con respecto a la ultima fecha de Visita al Veterinario
            fechaTemporal = Util.sumMonthsToDate(pet.getLastVetVisit(),CINCO);
            fechaEvento = Util.sumDaysToDate(fechaTemporal,QUINCE);
            mensaje = pet.getName()+ activity.getString(R.string.not_mje_visita_veterinario);
            createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
        }else {
            //o	Se le debe recordar a los 7 días de haber sido registrado en Mocatto Pets,
            // que debe visitar al veterinario cada 6 meses
            fechaEvento = Util.sumDaysToDate(new Date(),SIETE);
            mensaje = pet.getName()+ activity.getString(R.string.not_mje_visita_veterinario);
            createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
        }

        //Alimentación
        tipoEvento = R.string.tipoevento_compracomida;
        if (pet.getLastFoodBuyDate()!=null && pet.getFoodBuyRegularity()!=null){
            //Crea un evento en la fecha = fechaUltimaCompra + Nro días de cada cuanto compra comida
            fechaEvento = Util.sumDaysToDate(pet.getLastFoodBuyDate(),pet.getFoodBuyRegularity());

            if (fechaEvento.before(new Date())){
                fechaEvento = Util.sumDaysToDate(DOS);
            }

            mensaje = activity.getString(R.string.not_mje_food_for_pet)+
                    pet.getName()+
                    activity.getString(R.string.not_mje_food_for_pet_2);
            createApointment(pet, activity, cuenta, fechaEvento, mensaje,tipoEvento);
        }

    }

    public static long getDaysBetweenDates(Date initialDate, Date endDate){
        //Log.println(Log.DEBUG,"RESTAR FECHAS","initialDate: "+initialDate+"   endDate:"+endDate);
        final long MILLIS_PER_DAY = 24 * 3600 * 1000;
        long msDiff= endDate.getTime() - initialDate.getTime();
        long daysDiff = Math.round(msDiff / ((double)MILLIS_PER_DAY));
        //Log.println(Log.DEBUG,"RESTAR FECHAS","days: "+daysDiff+"   meses:"+(daysDiff/30));
        return (daysDiff/30);//Retornar meses
    }

}


