package com.mocatto.util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.imanoweb.calendarview.CalendarListener;
import com.imanoweb.calendarview.CustomCalendarView;
import com.mocatto.R;
import com.mocatto.dto.Apointment;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by froilan.ruiz on 6/6/2016.
 */
public class Util {
    public static final Integer CERO = 0;
    private SimpleDateFormat simpleDateFormat;
    //Version de la base de datos SQLite
    public final static int VERSION = 7;
    public final static String BDNAME = "mocatto";
    public static Integer PENDIENTE = 1;
    public static Integer LANZADO = 2;
    public static Integer YA_LO_HICE = 3;
    public static Integer RECORDAR_DESPUES = 4;
    public static Integer NO_RECORDAR_MAS = 5;
    public static Integer CANCELADO = 6;
    public static Integer CREATE_BY_USER = 1;
    public static Integer CREATE_AUTOMATICALLY = 2;

    public static String dateToString(Date date){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        String retorno = "";
        if (date!=null){
            retorno = sd.format(date);
        }
        return retorno;
    }

    public static String integerToString(Integer value){
        String retorno = "0";
        if (value!=null){
            retorno = String.valueOf(value);
        }
        return retorno;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Middle parameter is quality, but since PNG is lossless, it doesn't matter
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public void iniciarCalendario(final EditText campoFecha, final CustomCalendarView calendarView) {
        //Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        //Show Monday as first date of week
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        //Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false);
        //call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar);
        //Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(java.util.Date date) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                campoFecha.setText(df.format(date));
                calendarView.setVisibility(View.GONE);
            }
            @Override
            public void onMonthChanged(java.util.Date date) {

            }

        });
    }

    public void startCalendarWithoutClose(final EditText campoFecha, final CustomCalendarView calendarView) {
        //Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        //Show Monday as first date of week
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        //Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false);
        //call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar);
        //Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(java.util.Date date) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                campoFecha.setText(df.format(date));
            }
            @Override
            public void onMonthChanged(java.util.Date date) {

            }

        });
    }

    /*public boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }*/

    public boolean isEmpty(Object object) {
        boolean result = false;
        if (object instanceof EditText) {
            result = ((EditText) object).getText().toString().trim().length() <= 0;
        }else if(object instanceof Spinner){
            result = ((Spinner) object).getSelectedItem().toString().trim().length() <= 0;
        }
        return result;
    }

    public Date verificarDateNull(EditText txtCampo){
        Date fecha=null;
        if(!txtCampo.getText().toString().matches("")) {
            try {
                simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                fecha = simpleDateFormat.parse(txtCampo.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return fecha;
    }

    public Integer verificarIntegersNull(EditText txtCampo){
        Integer valor=0;
        if(txtCampo!=null) {
            try {
                valor = Integer.parseInt(txtCampo.getText().toString());
            }catch (NumberFormatException e){
                valor = 0;
            }

        }
        //Log.println(Log.DEBUG,"VALOR","valor del campo de texto: "+valor);
        return valor;
    }

    public Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0,0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(rectF,35,35,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }

    public boolean isEmpty(ImageView imageView){
        boolean result=true;
        if(null!=imageView.getDrawable()) {
            result=false;//tiene imagen asociada
        }else{
            result=true;//NO tiene imagen asociada
        }
        return result;
    }

    public void setFocus(final Object object, final EditText editText){
        if(object instanceof EditText){
            ((EditText)object).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        editText.requestFocus();
                    }
                }
            });
        }else if (object instanceof Spinner){
            ((Spinner)object).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        editText.requestFocus();
                    }
                }
            });
        }
    }

    public static void setFocus(final Object object, final EditText editText,Activity activity){
        InputMethodManager inputMethodManager =(InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getApplicationWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY );
        if(object instanceof EditText){
            ((EditText)object).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        editText.requestFocus();
                    }
                }
            });
        }else if (object instanceof Spinner){
            ((Spinner)object).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        editText.requestFocus();
                    }
                }
            });
        }
    }



    public static boolean isDateInTheFuture(Date date){
        boolean result=false;
        Date today = new Date();
        if (date!=null) {
            if (date.compareTo(today) >= 0) {
                result = true;
            }
        }
        return result;
    }

    public static boolean isDateInThePast(Date date){
        boolean result=false;
        Date today = new Date();
        if (date!=null) {
            if (date.compareTo(today) < 0) {
                result = true;
            }
        }
        return result;
    }

    public static void showCalendar(final EditText dateField, Activity activity){
        // Get Current Date
        int mYear, mMonth, mDay, mHour, mMinute;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        dateField.setText(valueInString(dayOfMonth) + "-" + valueInString(monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    public static void showHourSelector(final EditText dateHour, Activity activity){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minuteOfHour) {
                        if (minuteOfHour<10){
                            dateHour.setText(hourOfDay + ":0" + minuteOfHour);
                        }else {
                            dateHour.setText(hourOfDay + ":" + minuteOfHour);
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }

    public static void disabledSppiner(boolean enabled, Spinner spinner){
        if (enabled){
            spinner.setEnabled(true);
            spinner.setClickable(true);
        }else{
            spinner.setEnabled(false);
            spinner.setClickable(false);
        }

    }

    public static int setValueInSpinner(String valueToSelect, ArrayAdapter adapter){
        int position = 1;

        for (int index = 0; index < adapter.getCount(); ++index) {
            String value = (String)adapter.getItem(index);
            if (value.equals(valueToSelect)){
                Log.println(Log.DEBUG,"PROFILE","adaptadorSexo.getCount():"+adapter.getCount()+"valueToSelect: "+valueToSelect+" value:"+value);
                position=index;
            }
        }
        return position;
    }

    /*
     1   <item>Baño Felino</item> -- Baño y se determina si es perro o gato para colocar el icono correspondiente
     2   <item>Baño Canino</item> -- Baño y se determina si es perro o gato para colocar el icono correspondiente
     3   <item>Compra comida Felino</item> -- Compra Comida y se determina si es perro o gato para colocar el icono correspondiente
     4   <item>Compra comida Canino</item> -- Compra Comida y se determina si es perro o gato para colocar el icono correspondiente
     5   <item>Cumpleaños</item>
     6   <item>Desparacitada</item>
     7   <item>Otro tipo de evento</item>
     8   <item>Salida de paseo</item>
     9   <item>Vacuna</item>
     10   <item>Visita al veterinario</item>
        */
    public static void setImageApointment(Apointment evento, ImageView image, Context context){
        if (evento.getTipoEvento().equals("Baño") || evento.getTipoEvento().equals("Shower")){
            if (evento.getEspecie()!=null){
                if (evento.getEspecie().equals("Canino") || evento.getEspecie().equals("Dog")){//context.getResources().getString(R.string.dog)
                    image.setImageResource(R.drawable.ic_banio_perro);
                }else if (evento.getEspecie().equals("Felino") || evento.getEspecie().equals("Cat")){
                    image.setImageResource(R.drawable.ic_banio_gato);
                }
            }else{
                image.setImageResource(R.drawable.ic_banio_perro);
            }
        }else if (evento.getTipoEvento().equals("Compra comida") || evento.getTipoEvento().equals("Buy food")){
            if (evento.getEspecie()!=null){
                if (evento.getEspecie().equals("Canino") || evento.getEspecie().equals("Dog")){
                    image.setImageResource(R.drawable.ic_compra_comida_perro);
                }else if (evento.getEspecie().equals("Felino") || evento.getEspecie().equals("Cat")){//evento.getEspecie().equals(context.getResources().getString(R.string.cat))
                    image.setImageResource(R.drawable.ic_compra_comida_gato);
                }
            }else{
                image.setImageResource(R.drawable.ic_compra_comida_perro);
            }
        }else if (evento.getTipoEvento().equals("Cumpleaños") || evento.getTipoEvento().equals("Birthday")){
            image.setImageResource(R.drawable.ic_cumpleanios);
        }else if (evento.getTipoEvento().equals("Desparacitada") || evento.getTipoEvento().equals("Deworming")){
            image.setImageResource(R.drawable.ic_desparacitada);
        }else if (evento.getTipoEvento().equals("Otro tipo de evento") || evento.getTipoEvento().equals("Other kind of event")){
            image.setImageResource(R.drawable.ic_otro_tipo_evento);
        }else if (evento.getTipoEvento().equals("Salida de paseo") || evento.getTipoEvento().equals("Walkout")){
            image.setImageResource(R.drawable.ic_salida_paseo);
        }else if (evento.getTipoEvento().equals("Vacuna") || evento.getTipoEvento().equals("Vaccine")){
            image.setImageResource(R.drawable.ic_vacuna);
        }else if (evento.getTipoEvento().equals("Visita al veterinario") || evento.getTipoEvento().equals("Visit to veterinarian")){
            image.setImageResource(R.drawable.ic_visita_veterinario);
        }
    }

    public static Date sumDaysToDate(int days){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
    }

    public static Date sumDaysToDate(Date fecha, int days){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, days);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
    }

    public static Date sumMonthsToDate(Date fecha, int months){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.MONTH, months);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
    }

    public static Date sumYearsToDate(Date fecha, int years){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.YEAR, years);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
    }

    public static String changeStringValueYesOrTrue(String value,Context context){
        String retorno="";
        if (value.equals("Si")){
            retorno="true";
        }else if(value.equals("No")){
            retorno="false";
        }else if(value.equals("Yes")){
            retorno="true";
        }else if(value.equals("true")){
            String locale = context.getResources().getConfiguration().locale.getDisplayName();
            Log.println(Log.INFO,"LOCALE","locale: "+locale);
            if (locale.equals("ES") || locale.equals("es")) {
                retorno="Si";
            }else {
                retorno="Yes";
            }

        }else if(value.equals("false")){
            retorno="No";
        }

        return retorno;
    }

    public static String valueInString(int value) {
        String valueString=String.valueOf(value);
        if (value<10){
            valueString="0"+value;
        }
        return valueString;
    }
}
