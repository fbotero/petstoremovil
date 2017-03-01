package com.mocatto.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.R;
import com.mocatto.alarm.Alarm;
import com.mocatto.alarm.AlarmReceiver;
import com.mocatto.db.ApointmentDB;
import com.mocatto.dto.Apointment;
import com.mocatto.dto.Cuenta;
import com.mocatto.dto.Pet;
import com.mocatto.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import picker.ugurtekbas.com.Picker.Picker;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEventFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EMAIL = "email";
    private static final String FECHA = "fecha";
    private static final String LISTA_MASCOTAS = "listaMascotas";
    private static final String CUENTA = "cuenta";
    private static final String EVENTO = "evento";

    private OnFragmentInteractionListener mListener;

    private String email;
    private String fechaEvento;
    private EditText txtFechaEvento,txtNombreEvento,txtRecordar,txtUbicacion,txtHora;
    private Spinner spTipoEvento;
    Util util;
    private LinearLayout layoutCalendario;
    private Picker picker;
    private Button btnSeleccionHora;
    private ImageView btnGuardarEvento;
    private Apointment evento;
    //private AutoCompleteTextView txtNamePet;
    private Spinner txtNamePet;
    private ArrayList<String> itemsNamePet;//={"mascota 1","mascota 2","Pet 3","Pet 4"};
    private ArrayList<Pet> listaMascotas;
    private View view;
    ProgressDialog prgDialog;
    private String errorMessage;
    private String operation;
    private Cuenta cuenta;
    private AdView mAdView;
    private AdRequest adRequest;

    public AddEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddEventFragment newInstance(String param1, String param2) {
        AddEventFragment fragment = new AddEventFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, param1);
        args.putString(FECHA, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddEventFragment newInstance(Bundle args) {
        AddEventFragment fragment = new AddEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(getActivity());
        // Set Progress Dialog Text
        prgDialog.setMessage(getString(R.string.request_in_progress));
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        prgDialog.setProgressStyle(R.style.progress_bar_style);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_event, container, false);
        getActivity().setTitle(R.string.event);
        mAdView = (AdView) view.findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        startFieldsForm();
        startInitialValues();
        //callServiceGetPetsByEmail();
        loadSpinnerApoinmentType();
        startEvento();
        startComponetWatch();
        startListenersForm();
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
                        itemsNamePet.add(listData[i].getName());
                    }
                    txtNamePet.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,itemsNamePet));
                    Util.setValueInSpinner(evento.getMascota(),new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,itemsNamePet));
                    //setPet();
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

    private void startListenersForm() {
        txtFechaEvento.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //util.iniciarCalendario(txtFechaEvento,calendarView);
                //calendarView.setVisibility(View.VISIBLE);
                Util.showCalendar(txtFechaEvento,getActivity());
            }
        });

        txtHora.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showHourSelector(txtHora,getActivity());
            }
        });

        btnSeleccionHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String minute=Integer.toString(picker.getCurrentMin());
                if(picker.getCurrentMin()<10){minute= "0"+minute;}
                txtHora.setText(picker.getCurrentHour()+":"+minute );
                picker.setVisibility(v.GONE);
                btnSeleccionHora.setVisibility(v.GONE);
            }
        });

        btnGuardarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean obligatoryIsGood = true;
                if (util.isEmpty(txtFechaEvento) || util.isEmpty(txtHora) || util.isEmpty(txtNombreEvento)){
                    obligatoryIsGood = false;
                    errorMessage = getString(R.string.mje_fields_obligatory_event);
                }
                /*if (Util.isDateInThePast(txtFechaEvento.getText().toString())){
                    errorMessage+= " "+getString(R.string.mje_date_must_be_future);
                }*/
                if (obligatoryIsGood){
                    if (operation.equals(getString(R.string.operation_update))){
                        evento.setNombre(txtNombreEvento.getText().toString());
                        evento.setFecha(txtFechaEvento.getText().toString());
                        evento.setHora(txtHora.getText().toString());
                        evento.setUbicacion(txtUbicacion.getText().toString());
                        evento.setRecordarMinutosAntes(txtRecordar.getText().toString());
                        evento.setEmail(email);
                        evento.setMascota(txtNamePet.getSelectedItem().toString());
                        evento.setTipoEvento(spTipoEvento.getSelectedItem().toString());
                        ApointmentDB.updateApointment(evento,getActivity());
                        Toast.makeText(getActivity(), R.string.mje_save_apointment_succesful, Toast.LENGTH_SHORT).show();
                    }else{
                        evento = new Apointment(
                                null,
                                txtNombreEvento.getText().toString(),
                                txtFechaEvento.getText().toString(),
                                txtHora.getText().toString(),
                                txtUbicacion.getText().toString(),
                                txtRecordar.getText().toString(),
                                email,
                                txtNamePet.getSelectedItem().toString(),
                                spTipoEvento.getSelectedItem().toString(),
                                "",
                                Util.PENDIENTE,//status
                                Util.CREATE_BY_USER,//createdBy
                                Util.CERO//countreminder
                        );
                        evento.setId(ApointmentDB.createApointment(evento,getActivity()));
                        Toast.makeText(getActivity(), R.string.mje_save_apointment_succesful, Toast.LENGTH_SHORT).show();
                    }
                    //Log.println(Log.DEBUG,"ID EVENTO","idevento: "+evento.getId());
                    //createAlarm();
                    Alarm.createAlarm(evento,getActivity(),cuenta);



                    //txtNamePet.setText("");
                    txtFechaEvento.setText("");
                    txtHora.setText("");
                    txtNombreEvento.setText("");
                    txtUbicacion.setText("");
                    txtRecordar.setText("");
                    //Retorna al menú
                    Bundle args = new Bundle();
                    args.putString(EMAIL, email);
                    args.putString(FECHA, txtFechaEvento.getText().toString());
                    args.putSerializable(FECHA, cuenta);
                    CalendarFragment fragment = CalendarFragment.newInstance(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.main_fragment, fragment);
                    ft.commit();
                }else {
                    Toast toast =
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.LEFT, 0, 0);
                    toast.show();
                    errorMessage="";
                }
            }
        });
    }

    private void createAlarm() {
        String[] hora = evento.getHora().split(":");
        String[] fecha = evento.getFecha().split("-");

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();
        //cal.set(Integer.valueOf(fecha[2]),Integer.valueOf(fecha[1]),Integer.valueOf(fecha[0]),Integer.valueOf(hora[0]),Integer.valueOf(hora[1]));

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


        AlarmManager alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        /*intent.putExtra("idEvento",evento.getId());
        intent.putExtra("tipoEvento",evento.getTipoEvento());
        intent.putExtra("nombreEvento",evento.getNombre());
        intent.putExtra("fechaHoraEvento",evento.getFecha()+" "+evento.getHora());
        intent.putExtra("mascota",evento.getMascota());*/
        intent.putExtra(EVENTO,(Parcelable) evento);
        intent.putExtra(CUENTA,cuenta);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        // cal.add(Calendar.SECOND, 5);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        //alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10 * 1000, pendingIntent);
        //Log.println(Log.DEBUG,"ALARMA","Se almacenó la fecha y se creo la alarma");
    }


    private void startComponetWatch() {
        //Set background color
        picker.setCanvasColor(Color.WHITE);
        //Set dial color
        picker.setDialColor(Color.parseColor("#ff8000"));
        //Set clock color
        picker.setClockColor(Color.BLACK);
        //Set text color
        picker.setTextColor(Color.BLACK);
        //Enable 24 or 12 hour clock
        picker.setHourFormat(true);
        //get current hour
        picker.getCurrentHour();
        //get current minutes
        picker.getCurrentMin();
        //Set TimeChangedListener
       /* picker.setTimeChangedListener(new TimeChangedListener() {
            @Override
            public void timeChanged(Date date) {
                String minute=Integer.toString(picker.getCurrentMin());
                if(picker.getCurrentMin()<10){
                    minute= "0"+minute;
                }
                Log.println(Log.INFO,"TIME","esta es la hora: "+picker.getCurrentHour()+":"+minute);
            }
        });*/
    }

    private void loadSpinnerApoinmentType() {
        spTipoEvento = (Spinner) view.findViewById(R.id.spTipoEvento);
        ArrayAdapter adaptadorTipo = ArrayAdapter.createFromResource(
                getActivity(), R.array.opcApointmentType, android.R.layout.simple_spinner_item);
        adaptadorTipo.setDropDownViewResource(R.layout.spinner_item);
        spTipoEvento.setAdapter(adaptadorTipo);
        spTipoEvento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.GRAY);
                ((TextView) adapterView.getChildAt(0)).setTextSize(18);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void startFieldsForm() {
        itemsNamePet = new ArrayList<String>();
        spTipoEvento = (Spinner)view.findViewById(R.id.spTipoEvento);
        //txtNamePet = (AutoCompleteTextView)view.findViewById(R.id.txtNamePet);
        txtNamePet = (Spinner)view.findViewById(R.id.txtNamePet);
        txtNamePet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.GRAY);
                ((TextView) adapterView.getChildAt(0)).setTextSize(18);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        txtFechaEvento = (EditText) view.findViewById(R.id.txtFechaEvento);
        layoutCalendario = (LinearLayout) view.findViewById(R.id.layoutCalendario);
        picker = (Picker) view.findViewById(R.id.picker);
        btnSeleccionHora  =   (Button)view.findViewById(R.id.btnSeleccionHora);
        txtHora = (EditText) view.findViewById(R.id.txtHora);
        txtNombreEvento = (EditText)view.findViewById(R.id.txtNombreEvento);
        //hideKeyboard(txtNombreEvento);
        txtUbicacion = (EditText)view.findViewById(R.id.txtUbicacion);
        //hideKeyboard(txtUbicacion);
        txtRecordar  = (EditText)view.findViewById(R.id.txtRecordar);
        //hideKeyboard(txtRecordar);
        btnGuardarEvento = (ImageView)view.findViewById(R.id.ivBotonGuardar);
        btnSeleccionHora = (Button)view.findViewById(R.id.btnSeleccionHora);
        txtNombreEvento.setImeOptions(EditorInfo.IME_ACTION_DONE);
        txtUbicacion.setImeOptions(EditorInfo.IME_ACTION_DONE);
        txtRecordar.setImeOptions(EditorInfo.IME_ACTION_DONE);

    }

    private void enableOrDisableAdmobBanner(View view, boolean b) {
        if(b){
            mAdView.destroy();
            mAdView.setVisibility(View.GONE);
            b = false;
        }else {
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
            b = true;
        }
    }

    private void hideSoftInput(View view) {
        view.clearFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void startEvento(){
        txtFechaEvento.setText(fechaEvento);
        if (evento!=null){
            int position = 1;
            ArrayAdapter adaptadorTipo = ArrayAdapter.createFromResource(
                    getActivity(), R.array.opcApointmentType, android.R.layout.simple_spinner_item);
            for (int index = 0; index < adaptadorTipo.getCount(); ++index) {
                String value = (String)adaptadorTipo.getItem(index);
                if (value.equals(evento.getTipoEvento())){
                    position=index;
                }
            }
            spTipoEvento.setSelection(position);


            //txtNamePet.setText(evento.getMascota());

            txtFechaEvento.setText(evento.getFecha());
            txtHora.setText(evento.getHora());
            txtNombreEvento.setText(evento.getNombre());
            txtUbicacion.setText(evento.getUbicacion());
            txtRecordar.setText(evento.getRecordarMinutosAntes());
        }
    }

    private void setPet() {
        if (evento!=null){
            int positionNamePet = 1;
            ArrayAdapter adaptadorNamePet = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,itemsNamePet);

            for (int index = 0; index < adaptadorNamePet.getCount(); ++index) {
                String value = (String)adaptadorNamePet.getItem(index);
                if (value.equals(evento.getMascota())){
                    positionNamePet=index;
                }
            }
            txtNamePet.setSelection(positionNamePet);
        }
    }

    private void startInitialValues() {
        util = new Util();
        if (getArguments() != null) {
            email = getArguments().getString(EMAIL);
            fechaEvento = getArguments().getString("fechaEvento");
            evento = (Apointment) getArguments().getSerializable(EVENTO);
            operation = getArguments().getString("operation");
            listaMascotas = getArguments().getParcelableArrayList(LISTA_MASCOTAS);
            cuenta = (Cuenta) getArguments().getSerializable(CUENTA);
            if (listaMascotas!=null) {
                for (int i = 0; i < listaMascotas.size(); i++) {
                    itemsNamePet.add(listaMascotas.get(i).getName());
                }
                txtNamePet.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,itemsNamePet));
                if (evento!=null){
                    int position=Util.setValueInSpinner(evento.getMascota(),new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,itemsNamePet));
                    txtNamePet.setSelection(position);
                }
            }
        }

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

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
