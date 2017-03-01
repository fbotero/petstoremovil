package com.mocatto.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.NavigationDrawerActivity;
import com.mocatto.R;
import com.mocatto.alarm.Alarm;
import com.mocatto.db.ApointmentDB;
import com.mocatto.dto.Apointment;
import com.mocatto.dto.Cuenta;
import com.mocatto.dto.Data;
import com.mocatto.loaders.ImageLoader;
import com.mocatto.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlarmViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlarmViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmViewFragment extends Fragment implements LoaderManager.LoaderCallbacks {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EMAIL = "email";
    private static final String EVENTO = "evento";
    private static final String CUENTA = "cuenta";
    private static final int IMAGE_LOADER = 1;

    // TODO: Rename and change types of parameters
    private String email;
    private Apointment evento;
    private Cuenta cuenta;
    private SimpleDateFormat simpleDateFormat;
    private View view;
    private OnFragmentInteractionListener mListener;
    private TextView txtRecordarDespues, txtnombreEvento, txttipoEvento;
    private ImageView image,impublicidad;
    private ImageButton btn_yalohice, btn_recordardespues, btn_norecordarmas;
    Toast toast;

    ImageLoader imageLoader;
    private boolean flag_alarm=false;

    public AlarmViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlarmViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmViewFragment newInstance(String param1, String param2) {
        AlarmViewFragment fragment = new AlarmViewFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, param1);
        args.putString(EVENTO, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static AlarmViewFragment newInstance(Bundle args) {
        AlarmViewFragment fragment = new AlarmViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            imageLoader = (ImageLoader) getLoaderManager().getLoader(IMAGE_LOADER);
        }else {
            //callServiceImage();
            imageLoader = (ImageLoader) getLoaderManager().initLoader(IMAGE_LOADER, null, this);
        }
    }

    public void callServiceImage() {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = Resources.getSystem().getString(R.string.url_servicio_imagenes)+1;
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
                    JSONArray json = timeline.getJSONArray("returnDTO");
                    Data[] listData = new Gson().fromJson(json.toString(), Data[].class);
                    String urlImagen ="https://lh3.googleusercontent.com/z4xDOfdXMgdA9KVYLorBnySZbLlUbtyuwo2mQK_APeelCCMgaRuyJM2O9fIo74i3psYJDKov2n28kRM=w1366-h673";



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

    private void initializeFields() {
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        if (getArguments() != null) {
            email = getArguments().getString(EMAIL);
            evento = (Apointment) getArguments().getSerializable(EVENTO);
            cuenta = (Cuenta) getArguments().getSerializable(CUENTA);
        }
        /*txtfechaEvento = (EditText)view.findViewById(R.id.fechaEvento);
        txthoraEvento = (EditText)view.findViewById(R.id.horaEvento);
        txtidEento = (EditText)view.findViewById(R.id.idEento);
        txtlugarEvento = (EditText)view.findViewById(R.id.lugarEvento);
        txtmascotaEvento = (EditText)view.findViewById(R.id.mascotaEvento);*/
        txtnombreEvento = (TextView) view.findViewById(R.id.nombreEvento);
        txttipoEvento = (TextView) view.findViewById(R.id.tipoEvento);
        image = (ImageView) view.findViewById(R.id.imagen);
        impublicidad = (ImageView)view.findViewById(R.id.imPublicidad);

        imageLoader.forceLoad();

        setButtonsInCorrectLanguage();
    }

    private void setButtonsInCorrectLanguage() {
        btn_yalohice = (ImageButton) view.findViewById(R.id.btn_yalohice);
        btn_recordardespues = (ImageButton) view.findViewById(R.id.btn_recordardespues);
        btn_norecordarmas = (ImageButton) view.findViewById(R.id.btn_norecordarmas);
        txtRecordarDespues = (TextView) view.findViewById(R.id.tvRecordarDespues);
        String locale = getContext().getResources().getConfiguration().locale.getDisplayName();
        Log.println(Log.INFO, "LOCALE", "locale: " + locale);
        if (locale.equals("ES") || locale.equals("es")) {
            //btn_yalohice.setImageResource(R.drawable.btn_yalohice);
            //btn_recordardespues.setImageResource(R.drawable.btn_recordardespues);
            btn_norecordarmas.setImageResource(R.drawable.btn_rojoenblanco);

        } else {
            //btn_yalohice.setImageResource(R.drawable.btn_yalohice_en);
            //btn_recordardespues.setImageResource(R.drawable.btn_recordardespues_en);
            btn_norecordarmas.setImageResource(R.drawable.btn_rojo_grande);
        }
        if (evento.getCreatedBy() == Util.CREATE_AUTOMATICALLY) {
            btn_norecordarmas.setVisibility(View.VISIBLE);
        } else {
            btn_norecordarmas.setVisibility(View.GONE);
        }
    }

    private void setValuesApointmentinForm() {
        txtnombreEvento.setText(evento.getNombre());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_alarm_view, container, false);
        getActivity().setTitle(getString(R.string.reminder));
        initializeFields();
        setValuesApointmentinForm();
        Util.setImageApointment(evento, image, getContext());
       /* int width = 500;//192
        int height = 280;//108
        image.getLayoutParams().height = height;
        image.getLayoutParams().width= width;*/
        setOnClickButtons();
        return view;
    }

    private void setOnClickButtons() {
        btn_yalohice.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Date date=null;

                        if (evento.getCreatedBy().intValue()==Util.CREATE_AUTOMATICALLY) {//Creado automaticamente
                            date = setNewFechaYaLoHice();

                            String nuevaFecha = simpleDateFormat.format(date);
                            evento.setFecha(nuevaFecha);
                            ApointmentDB.updateStatusApointment(evento, Util.YA_LO_HICE, getContext());
                            Log.println(Log.DEBUG, "FECHA", "Nueva fecha del evento: " + evento.getFecha());
                            //Programar nuevamente el recordatorio createAlarm()
                            if (flag_alarm) {
                                Alarm.createAlarm(evento, getActivity(), cuenta);
                            }

                        }else {
                            ApointmentDB.updateStatusApointment(evento, Util.YA_LO_HICE, getContext());
                        }

                        toast =
                                Toast.makeText(getContext(), "El estado del evento fue actualizado satisfactoriamente", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER | Gravity.LEFT, 0, 0);
                        toast.show();

                        goMainActivity();

                    }
                }
        );

        btn_recordardespues.setOnClickListener(new Button.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       //Establecer nueva fecha, si es creado por el usuario 1 día despues, si es creado automaticamente segun condiciones
                       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                       Date date=null;
                       if (evento.getCreatedBy()==Util.CREATE_BY_USER) {
                           date = Util.sumDaysToDate(1);
                       }else{
                           date = setNewFecha();
                           //date = Util.sumDaysToDate(10);//borrar esta linea
                       }
                       String nuevaFecha = simpleDateFormat.format(date);
                       evento.setFecha(nuevaFecha);
                       Log.println(Log.DEBUG, "FECHA", "Nueva fecha del evento: " + evento.getFecha());

                       //Actualizar estado y fecha del evento
                       ApointmentDB.updateStatusApointment(evento, Util.RECORDAR_DESPUES, getContext());

                       //Programar nuevamente el recordatorio createAlarm()
                       if (flag_alarm) {
                           //Actualizar estado y fecha del evento
                           ApointmentDB.updateStatusApointment(evento, Util.RECORDAR_DESPUES, getContext());
                           Alarm.createAlarm(evento, getActivity(), cuenta);
                       }else{
                           ApointmentDB.updateStatusApointment(evento, Util.NO_RECORDAR_MAS, getContext());
                       }
                       toast =
                               Toast.makeText(getContext(), "El estado del evento fue actualizado satisfactoriamente", Toast.LENGTH_LONG);
                       toast.setGravity(Gravity.CENTER | Gravity.LEFT, 0, 0);
                       toast.show();
                       goMainActivity();

                   }
               }
        );

        btn_norecordarmas.setOnClickListener(new Button.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     ApointmentDB.updateStatusApointment(evento, Util.NO_RECORDAR_MAS, getContext());
                     toast =
                             Toast.makeText(getContext(), "El estado del evento fue actualizado satisfactoriamente y no será recordado", Toast.LENGTH_LONG);
                     toast.setGravity(Gravity.CENTER | Gravity.LEFT, 0, 0);
                     toast.show();
                     goMainActivity();

                 }
             }
        );
    }

    private Date setNewFechaYaLoHice(){
        Date date = new Date();
        if (evento.getTipoEvento().equals("Baño") || evento.getTipoEvento().equals("Shower")){
            flag_alarm = true;
            if (evento.getBathFrecuency()!=null) {
                date = Util.sumDaysToDate(evento.getBathFrecuency());
            }else{
                date = Util.sumDaysToDate(15);//15 días por default
            }
        }else if (evento.getTipoEvento().equals("Compra comida") || evento.getTipoEvento().equals("Buy food")){
            flag_alarm = true;
            if (evento.getFoodBuyRegularity()!=null){
                date = Util.sumDaysToDate(evento.getFoodBuyRegularity());
            }else{
                date = Util.sumDaysToDate(15);//15 días por default
            }
        }else if (evento.getTipoEvento().equals("Desparacitada") || evento.getTipoEvento().equals("Deworming")){
            flag_alarm = true;
            date = Util.sumMonthsToDate(new Date(),3);
            date = Util.sumDaysToDate(date,15);
        }else if (evento.getTipoEvento().equals("Visita al veterinario") || evento.getTipoEvento().equals("Visit to veterinarian")){
            flag_alarm = true;
            date = Util.sumMonthsToDate(new Date(),5);
            date = Util.sumDaysToDate(date,15);
        }
        else if (evento.getTipoEvento().equals("Cumpleaños") || evento.getTipoEvento().equals("Birthday")){
            flag_alarm = true;
            date = Util.sumYearsToDate(new Date(),1);
        }
        return date;
    }

    private Date setNewFecha(){
        Date date = new Date();
        if (evento.getTipoEvento().equals("Baño") || evento.getTipoEvento().equals("Shower")){
            date = Util.sumDaysToDate(3);//recordar cada 3 días por 5 veces
            if (evento.getCountReminder()<5) {
                flag_alarm = true;
            }else{
                date = Util.sumDaysToDate(evento.getBathFrecuency());
                flag_alarm = false;
            }
        }else if (evento.getTipoEvento().equals("Compra comida") || evento.getTipoEvento().equals("Buy food")){
            date = Util.sumDaysToDate(1);//recordar todos los dias por 5 veces
            if (evento.getCountReminder()<5) {
                flag_alarm = true;
            }else{
                date = Util.sumDaysToDate(evento.getFoodBuyRegularity());
                flag_alarm = false;
            }
        }else if (evento.getTipoEvento().equals("Cumpleaños") || evento.getTipoEvento().equals("Birthday")){
            date = Util.sumYearsToDate(new Date(),1);
        }else if (evento.getTipoEvento().equals("Desparacitada") || evento.getTipoEvento().equals("Deworming")){
            date = Util.sumDaysToDate(3);//recordar cada 3 dias por 5 veces
            if (evento.getCountReminder()<5) {
                flag_alarm = true;
            }else{
                date = Util.sumMonthsToDate(new Date(),3);//Se le recordará dentro de 3 meses y 15 dias
                date = Util.sumDaysToDate(date,15);//Se le recordará dentro de 6 meses
                flag_alarm = false;
            }
        }else if (evento.getTipoEvento().equals("Otro tipo de evento") || evento.getTipoEvento().equals("Other kind of event")){

        }else if (evento.getTipoEvento().equals("Salida de paseo") || evento.getTipoEvento().equals("Walkout")){

        }else if (evento.getTipoEvento().equals("Vacuna") || evento.getTipoEvento().equals("Vaccine")){

        }else if (evento.getTipoEvento().equals("Visita al veterinario") || evento.getTipoEvento().equals("Visit to veterinarian")){
            date = Util.sumDaysToDate(3);//recordar cada 3 dias por 5 veces
            if (evento.getCountReminder()<5) {
                flag_alarm = true;
            }else{
                date = Util.sumMonthsToDate(new Date(),5);//Se le recordará dentro de 5 meses y 15 dias
                date = Util.sumDaysToDate(date,15);//Se le recordará dentro de 6 meses
                flag_alarm = false;
            }
        }
        return date;
    }


    private void goMainActivity() {
        Intent refresh = new Intent(getActivity(), NavigationDrawerActivity.class);
        refresh.putExtra("email", email);
        refresh.putExtra("cuenta", cuenta);
        refresh.putExtra("isFromCreateAccount", "false");
        startActivity(refresh);
        getActivity().finish();
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

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if(id == IMAGE_LOADER){
            return new ImageLoader(getContext());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if(IMAGE_LOADER == loader.getId()){
            if (data!=null){
                impublicidad.setImageBitmap((Bitmap) data);
            }else{//Se agrega por defecto la imagen de mocatto
                impublicidad.setImageResource(R.drawable.img_publicidad);
            }
            imageLoader.reset();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

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
