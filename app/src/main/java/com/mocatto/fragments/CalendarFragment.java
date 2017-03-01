package com.mocatto.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.imanoweb.calendarview.CalendarListener;
import com.imanoweb.calendarview.CustomCalendarView;
import com.imanoweb.calendarview.DayDecorator;
import com.imanoweb.calendarview.DayView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.CreateAccountActivity;
import com.mocatto.R;
import com.mocatto.adapters.ApointmentArrayAdapter;
import com.mocatto.db.ApointmentDB;
import com.mocatto.db.EventSQLiteOpenHelper;
import com.mocatto.dto.Apointment;
import com.mocatto.dto.Cuenta;
import com.mocatto.dto.Pet;
import com.mocatto.util.CustomEvent;
import com.mocatto.util.Util;
import com.p_v.flexiblecalendar.FlexibleCalendarView;
import com.p_v.flexiblecalendar.exception.HighValueException;
import com.p_v.flexiblecalendar.view.BaseCellView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "fechaEvento";
    private static final String ARG_PARAM3 = "evento";
    private static final String ARG_PARAM4 = "operation";
    private static final String ARG_PARAM5 = "cuenta";

    private EventSQLiteOpenHelper openHelper;
    private SQLiteDatabase database;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Util util;
    private String email;

    private EditText txtFechaSeleccionada;
    private ImageView ivAgregarNota;
    private FloatingActionButton fabAgregarNota;
    private CustomCalendarView calendarView;
    private FlexibleCalendarView eCalendarView;

    private OnFragmentInteractionListener mListener;
    private List<Apointment> listaEventos;
    private List<Apointment> listaEventosDelMes;
    private LinearLayout linearLayoutEvents;
    private Apointment evento;
    private View view;
    private ArrayAdapter adaptador;
    private ListView listView;
    private ArrayList<Pet> listaMascotas;
    Calendar currentCalendar;
    private ArrayList<Pet> itemsNamePet;
    private Hashtable hashTablePets;
    private Cuenta cuenta;
    private TextView mes;
    private String[] MONTHS;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static CalendarFragment newInstance(Bundle args) {
        CalendarFragment fragment = new CalendarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        MONTHS = new String[]{getString(R.string.enero),
                getString(R.string.febrero),
                getString(R.string.marzo),
                getString(R.string.abril),
                getString(R.string.Mayo),
                getString(R.string.Junio),
                getString(R.string.Julio),
                getString(R.string.agosto),
                getString(R.string.septiembre),
                getString(R.string.octubre),
                getString(R.string.noviembre),
                getString(R.string.diciembre)};

        getActivity().setTitle(getString(R.string.calendario));
        util = new Util();
        if (getArguments() != null) {
            email = getArguments().getString("email");
            cuenta = (Cuenta) getArguments().getSerializable("cuenta");
            listaMascotas = getArguments().getParcelableArrayList("listaMascotas");
        }
        mes = (TextView) view.findViewById(R.id.mes);

        txtFechaSeleccionada = (EditText) view.findViewById(R.id.txtFechaSeleccionada);
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        //Toast.makeText(getActivity(), today.month+"-"+today.year, Toast.LENGTH_SHORT).show();
        Date date = new Date();
        mes.setText(setMonth(today.month)+"/"+today.year);


        //util.startCalendarWithoutClose(txtFechaSeleccionada,calendarView);
        //startCalendarWithoutClose();
        itemsNamePet = new ArrayList<Pet>();
        hashTablePets = new Hashtable();
        callServiceGetPetsByEmail();


        fabAgregarNota = (FloatingActionButton) view.findViewById(R.id.fabAgregarNota);
        fabAgregarNota.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString(ARG_PARAM1, email);
                args.putString(ARG_PARAM2, txtFechaSeleccionada.getText().toString());
                args.putSerializable(ARG_PARAM4, getString(R.string.operation_insert));
                args.putSerializable(ARG_PARAM5, cuenta);
                args.putParcelableArrayList("listaMascotas",itemsNamePet);
                AddEventFragment fragment = AddEventFragment.newInstance(args);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.main_fragment, fragment);
                ft.commit();

            }
        });

        startFlexibleCalendar();


        return view;
    }

    public void callServiceGetPetsByEmail() {
        RequestParams params = new RequestParams();
        params.put("email",email);
        AsyncHttpClient client = new AsyncHttpClient();
        //String url = "https://mocatto.herokuapp.com/rest/en/pet/getPetsByEmail/"+email;
        String url = getString(R.string.service_gets_by_email)+email;

        client.get(url,params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                System.out.println(timeline);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                try {
                    JSONArray json = timeline.getJSONArray("petDTO");
                    Pet[] listData = new Gson().fromJson(json.toString(), Pet[].class);
                    for (int i = 0; i < listData.length; i++) {
                        Pet pet = listData[i];
                        if (pet.getWoopingCough()!=null) {
                            pet.setWoopingCough(Util.changeStringValueYesOrTrue(pet.getWoopingCough(),getContext()));
                        }
                        if (pet.getSterilized()!=null) {
                            pet.setSterilized(Util.changeStringValueYesOrTrue(pet.getSterilized(), getContext()));
                        }
                        if (pet.getAidsLeukemiaVaccine()!=null) {
                            pet.setAidsLeukemiaVaccine(Util.changeStringValueYesOrTrue(pet.getAidsLeukemiaVaccine(), getContext()));
                        }
                        if (pet.getSpecie().equals(getString(R.string.cat)) || pet.getSpecie().equals(getString(R.string.feline))){
                            pet.setSpecie(getString(R.string.cat));
                        }else if (pet.getSpecie().equals(getString(R.string.dog)) || pet.getSpecie().equals(getString(R.string.canine))){
                            pet.setSpecie(getString(R.string.dog));
                        }
                        itemsNamePet.add(pet);
                        hashTablePets.put(listData[i].getName(),listData[i].getSpecie());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private Map<Integer,List<CustomEvent>> eventMap;

    private void  initializeEvents(){
        eventMap = new HashMap<>();
        List<CustomEvent> colorLst;
        for (int i=1;i<=31;i++){
            Iterator iterator = listaEventosDelMes.iterator();
            colorLst = new ArrayList<>();
            while (iterator.hasNext()){
                Apointment apointment = (Apointment) iterator.next();
                String diaString = apointment.getFecha().split("-")[0];
                if (i==Integer.parseInt(diaString)){
                    colorLst.add(new CustomEvent(android.R.color.holo_red_dark));
                }
            }
            eventMap.put(i,colorLst);
        }
    }

    private String setMonth(int mes){
        return MONTHS[mes];
    }

    public void startFlexibleCalendar(){

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String fechaString =df.format(new Date());
        listaEventosDelMes = ApointmentDB.listApointmentByMonth(fechaString, getActivity());
        initializeEvents();

        eCalendarView = (FlexibleCalendarView)view.findViewById(R.id.newCalendar);
        eCalendarView.setMonthViewHorizontalSpacing(10);
        eCalendarView.setMonthViewVerticalSpacing(10);
        eCalendarView.setOnMonthChangeListener(new FlexibleCalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month, @FlexibleCalendarView.Direction int direction) {
                mes.setText(setMonth(month)+"/"+year);
                //Cuando cambia el mes
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String monthString = Util.valueInString(month+1);
                String dayString = Util.valueInString(1);
                String fechaString =dayString+"-"+monthString+"-"+year;
                listaEventosDelMes = ApointmentDB.listApointmentByMonth(fechaString, getActivity());
                //Toast.makeText(getContext(),"fecha: "+fechaString+"  lista de eventos:"+listaEventosDelMes.size(),Toast.LENGTH_SHORT).show();
                initializeEvents();
                eCalendarView.refresh();
            }
        });

        eCalendarView.setCalendarView(new FlexibleCalendarView.CalendarView() {
            @Override
            public BaseCellView getCellView(int position, View convertView, ViewGroup parent, int cellType) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar3_date_cell_view, null);
                }
                return cellView;
            }

            @Override
            public BaseCellView getWeekdayCellView(int position, View convertView, ViewGroup parent) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar3_week_cell_view, null);
                }
                return cellView;
            }

            @Override
            public String getDayOfWeekDisplayValue(int dayOfWeek, String defaultValue) {
                return null;
            }
        });

        eCalendarView.setOnDateClickListener(new FlexibleCalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(int year, int month, int day) {
                //Cuando se selecciona una fecha
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String fechaEnString;
                String monthString = Util.valueInString(month+1);
                String dayString=Util.valueInString(day);
                fechaEnString=dayString+"-"+monthString+"-"+year;
                java.util.Date fecha = new Date(year,month,day);

                listaEventosDelMes = ApointmentDB.listApointmentByMonth(fechaEnString, getActivity());
                //Toast.makeText(getContext(),"lista de eventos:"+listaEventosDelMes.size(),Toast.LENGTH_SHORT).show();
                initializeEvents();

                //Toast.makeText(getContext(),"fecha:"+day+"-"+month+"-"+year+"   "+df.format(fecha),Toast.LENGTH_SHORT).show();
                txtFechaSeleccionada.setText(fechaEnString);

                //setEventsInCalendar(txtFechaSeleccionada.getText().toString(), currentCalendar);

                //createAlarm(date);

                //Instancia del ListView
                listView = (ListView)view.findViewById(R.id.lista);
                listaEventos= ApointmentDB.listApointmentByDate(txtFechaSeleccionada.getText().toString(),getActivity(),hashTablePets);
                //Toast.makeText(getActivity(), "Cantidad eventos: "+listaEventos.size(), Toast.LENGTH_SHORT).show();
                linearLayoutEvents = (LinearLayout) view.findViewById(R.id.linearLayoutEvents);

                //Inicializar el adaptador con la fuente de datos
                adaptador = new ApointmentArrayAdapter(getActivity(), listaEventos);

                //Relacionando la lista con el adaptador
                listView.setAdapter(adaptador);

                //Estableciendo la escucha
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Apointment eventoActual = (Apointment) adaptador.getItem(position);
                        /*String msg = "Elegiste el evento"+eventoActual.getNombre()+"-"+eventoActual.getHora();
                        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();*/

                        Bundle args = new Bundle();
                        args.putString(ARG_PARAM1, email);
                        args.putSerializable(ARG_PARAM3, eventoActual);
                        args.putSerializable(ARG_PARAM4, getString(R.string.operation_update));
                        args.putSerializable(ARG_PARAM5, cuenta);
                        args.putParcelableArrayList("listaMascotas",itemsNamePet);
                        //cambio oct 3, redireccion a alarmview
                        //AddEventFragment fragment = AddEventFragment.newInstance(args);
                        AlarmViewFragment fragment = AlarmViewFragment.newInstance(args);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.addToBackStack(null);
                        ft.replace(R.id.main_fragment, fragment);
                        ft.commit();
                    }
                });

            }
        });




        eCalendarView.setEventDataProvider(new FlexibleCalendarView.EventDataProvider() {
            @Override
            public List<CustomEvent> getEventsForTheDay(int year, int month, int day) {
                //
                return getEvents(year, month, day);
            }
        });

    }

    public List<CustomEvent> getEvents(int year, int month, int day){
        return eventMap.get(day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        try{
            eCalendarView.selectDate(year,monthOfYear,dayOfMonth);
            //Toast.makeText(getContext(),"dia seleccionado: "+year+" "+ (monthOfYear+1),Toast.LENGTH_SHORT).show();
        }catch(HighValueException e){
            e.printStackTrace();
        }
    }


    public void startCalendarWithoutClose() {
        //Initialize calendar with date
        currentCalendar = Calendar.getInstance(Locale.getDefault());
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
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                txtFechaSeleccionada.setText(df.format(date));

                //setEventsInCalendar(txtFechaSeleccionada.getText().toString(), currentCalendar);

                //createAlarm(date);

                //Instancia del ListView
                listView = (ListView)view.findViewById(R.id.lista);
                listaEventos= ApointmentDB.listApointmentByDate(txtFechaSeleccionada.getText().toString(),getActivity(),hashTablePets);
                //Toast.makeText(getActivity(), "Cantidad eventos: "+listaEventos.size(), Toast.LENGTH_SHORT).show();
                linearLayoutEvents = (LinearLayout) view.findViewById(R.id.linearLayoutEvents);

                //Inicializar el adaptador con la fuente de datos
                adaptador = new ApointmentArrayAdapter(getActivity(), listaEventos);

                //Relacionando la lista con el adaptador
                listView.setAdapter(adaptador);

                //Estableciendo la escucha
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Apointment eventoActual = (Apointment) adaptador.getItem(position);
                        /*String msg = "Elegiste el evento"+eventoActual.getNombre()+"-"+eventoActual.getHora();
                        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();*/

                        Bundle args = new Bundle();
                        args.putString(ARG_PARAM1, email);
                        args.putSerializable(ARG_PARAM3, eventoActual);
                        args.putSerializable(ARG_PARAM4, getString(R.string.operation_update));
                        args.putSerializable(ARG_PARAM5, cuenta);
                        args.putParcelableArrayList("listaMascotas",itemsNamePet);
                        AddEventFragment fragment = AddEventFragment.newInstance(args);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.addToBackStack(null);
                        ft.replace(R.id.main_fragment, fragment);
                        ft.commit();
                    }
                });

            }
            @Override
            public void onMonthChanged(java.util.Date date) {
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String fechaString =df.format(date);
                //Log.println(Log.INFO,"cambioMes","fecha: "+fechaString);
                listaEventosDelMes = ApointmentDB.listApointmentByMonth(fechaString, getActivity());
                List decorators = new ArrayList<>();
                decorators.add(new ColorDecorator(listaEventosDelMes));
                calendarView.setDecorators(decorators);
                //Show Monday as first date of week
                calendarView.setFirstDayOfWeek(Calendar.MONDAY);
                //Show/hide overflow days of a month
                calendarView.setShowOverflowDate(false);

                calendarView.refreshDrawableState();

                //call refreshCalendar to update calendar the view
                //calendarView.refreshCalendar(currentCalendar);
            }
        });

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String fechaString =df.format(new Date());
        listaEventosDelMes = ApointmentDB.listApointmentByMonth(fechaString, getActivity());
        List decorators = new ArrayList<>();
        decorators.add(new ColorDecorator(listaEventosDelMes));
        calendarView.setDecorators(decorators);
        calendarView.refreshCalendar(currentCalendar);
    }

    private void setEventsInCalendar(String date, Calendar currentCalendar) {
        listaEventosDelMes = ApointmentDB.listApointmentByMonth(date, getActivity());
        List decorators = new ArrayList<>();
        decorators.add(new ColorDecorator(listaEventosDelMes));
        calendarView.setDecorators(decorators);
        calendarView.refreshCalendar(currentCalendar);
    }

    private class ColorDecorator implements DayDecorator {
        ArrayList<Apointment> listaEventosDelMesDecorator;
        public ColorDecorator(List<Apointment> listaEventos) {
            listaEventosDelMesDecorator= (ArrayList<Apointment>) listaEventos;
        }

        @Override
        public void decorate(DayView cell) {
            Random rnd = new Random();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String fechaString =df.format(cell.getDate());
            if (listaEventosDelMes!=null){
                int marcarCelda=0;
                for (int i=0;i<listaEventosDelMes.size();i++){
                    if(fechaString.equals(listaEventosDelMes.get(i).getFecha())){
                        marcarCelda=1;
                    }
                }
                if (marcarCelda==1){
                    cell.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                    cell.setTextColor(Color.WHITE);
                }
            }
        }
    }

    private void createAlarm(java.util.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();
        cal.set(date.getYear(),date.getMonth(),date.getDay(),12,45);

        AlarmManager alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), CreateAccountActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        // cal.add(Calendar.SECOND, 5);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
