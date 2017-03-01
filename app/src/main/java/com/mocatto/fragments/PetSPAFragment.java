package com.mocatto.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.NavigationDrawerActivity;
import com.mocatto.R;
import com.mocatto.alarm.Alarm;
import com.mocatto.db.PetDB;
import com.mocatto.dto.Cuenta;
import com.mocatto.dto.Pet;
import com.mocatto.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PetSPAFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PetSPAFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PetSPAFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "mascota";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Pet mascota;
    private SimpleDateFormat simpleDateFormat;
    private String email;
    protected static final int REQ_CODE_PICK_IMAGE = 1;
    ImageView mImg;
    private Util util;
    private Spinner spOtros,spDondeBanio;
    private EditText txtEvento,txtCadaCuanto,txtFechaUltimoBanio,txtFrecuenciaBanio;
    private TextView lbEvento,lbCadaCuanto,lbFrecuenciaBanio,lbFechaUltimoBanio,lbDonde,lbOtros;

    private View view;
    private String errorMessage;
    private boolean isOKVerifications=true;
    ProgressDialog prgDialog;
    private byte[] photoPet;
    private boolean for_update;
    private Cuenta cuenta;
    private AdView mAdView;

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

    public PetSPAFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PetSPAFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PetSPAFragment newInstance(String param1, String param2) {
        PetSPAFragment fragment = new PetSPAFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static PetSPAFragment newInstance(Bundle args) {
        PetSPAFragment fragment = new PetSPAFragment();
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
        view = inflater.inflate(R.layout.fragment_pet_spa, container, false);
        getActivity().setTitle(getString(R.string.spa_pet));
        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        getInitialValues();
        startFields();
        startDatesAndCalendars();
        loadSpinners();
        startImageView();
        loadPetInformation();
        startButtonStart();
        showSpecieFields();
        return view;
    }

    private void callServiceRegisterPet() {
        prgDialog.show();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        RequestParams params = new RequestParams();
        //Log.println(Log.DEBUG,"PET","Datos enviados al servicio: "+params.toString());
        params.put("id",mascota.getId());
        params.put("name",mascota.getName());
        if (mascota.getSpecie().equals(getString(R.string.cat)) || mascota.getSpecie().equals(getString(R.string.feline))){
            params.put("specie",getString(R.string.feline));
        }else if (mascota.getSpecie().equals(getString(R.string.dog)) || mascota.getSpecie().equals(getString(R.string.canine))){
            params.put("specie",getString(R.string.canine));
        }
        params.put("race",mascota.getRace());
        params.put("gender",mascota.getGender());
        String fechaNacimiento = sd.format(mascota.getBirthDate());
        params.put("birthDate",fechaNacimiento);
        params.put("fur",mascota.getFur());
        params.put("diet",mascota.getDiet());
        String cual_dieta = "";
        if(mascota.getDietName()!=null){
            cual_dieta=mascota.getDietName();
            params.put("dietName",cual_dieta);
        }

        /*String cantidad_gr = "0";
        if(mascota.getFoodGrams()!=null){
            cantidad_gr=String.valueOf(mascota.getFoodGrams());
        }*/
        params.put("foodGrams",Util.integerToString(mascota.getFoodGrams()));
        params.put("foodBrand",mascota.getFoodBrand());
        String cual_marca = "";
        if(mascota.getFoodBrandName()!=null){
            cual_marca=mascota.getFoodBrandName();
            params.put("foodBrandName",cual_marca);
        }

        String cada_cuanto_compra_comida = "0";
        if(mascota.getFoodBuyRegularity()!=null){
            cada_cuanto_compra_comida=String.valueOf(mascota.getFoodBuyRegularity());
            params.put("foodBuyRegularity",cada_cuanto_compra_comida);
        }

        /*String lastFoodBuyDate=null;
        if (mascota.getLastFoodBuyDate()!=null){
            lastFoodBuyDate = sd.format(mascota.getLastFoodBuyDate());
        }*/
        params.put("lastFoodBuyDate",Util.dateToString(mascota.getLastFoodBuyDate()));
        String lastVaccine="";
        if (mascota.getLastVaccine()!=null){
            lastVaccine = sd.format(mascota.getLastVaccine());
            params.put("lastVaccine",lastVaccine);
        }
        params.put("vaccine",mascota.getVaccine());
        String value = "false";
        if (mascota.getWoopingCough()!=null){
            if (mascota.getWoopingCough().equals("Si") || mascota.getWoopingCough().equals("Yes")){
                value = "true";
            }
        }
        params.put("woopingCough", value);
        String fechaTosPerrera="";
        if (mascota.getWoopingCoughDate()!=null){
            fechaTosPerrera = sd.format(mascota.getWoopingCoughDate());
            params.put("woopingCoughDate",fechaTosPerrera);
        }


        String fechaLastWorm="";
        if (mascota.getLastWorm()!=null){
            fechaLastWorm = sd.format(mascota.getLastWorm());
            params.put("lastWorm",fechaLastWorm);
        }

        params.put("antiFlea",mascota.getAntiFlea());
        String fechaLastVetVisit="";
        if (mascota.getLastVetVisit()!=null){
            fechaLastVetVisit = sd.format(mascota.getLastVetVisit());
            params.put("lastVetVisit",fechaLastVetVisit);
        }

        value = "false";
        if (mascota.getSterilized()!=null){
            if (mascota.getSterilized().equals("Si") || mascota.getSterilized().equals("Yes")){
                value = "true";
            }
        }
        params.put("sterilized", value);
        String bathFrecuency = "0";
        if(mascota.getBathFrecuency()!=null){
            bathFrecuency=String.valueOf(mascota.getBathFrecuency());
            params.put("bathFrecuency",bathFrecuency);
        }


        String fechaLastBath="";
        if (mascota.getLastBath()!=null){
            fechaLastBath = sd.format(mascota.getLastBath());
            params.put("lastBath", fechaLastBath);
        }

        params.put("bathLocation",mascota.getBathLocation());
        params.put("others",mascota.getOthers());
        String event = "";
        if(mascota.getEvent()!=null){
            event=String.valueOf(mascota.getEvent());
            params.put("event",event);
        }

        String regularity = "";
        if(mascota.getRegularity()!=null){
            regularity=String.valueOf(mascota.getRegularity());
            params.put("regularity",regularity);
        }
        value = "false";
        if (mascota.getAidsLeukemiaVaccine()!=null){
            if (mascota.getAidsLeukemiaVaccine().equals("Si") || mascota.getAidsLeukemiaVaccine().equals("Yes")){
                value = "true";
            }
        }
        params.put("aidsLeukemiaVaccine",value);
        String fechaAidsLeukemiaVaccineDate="";
        if (mascota.getAidsLeukemiaVaccineDate()!=null){
            fechaAidsLeukemiaVaccineDate = sd.format(mascota.getAidsLeukemiaVaccineDate());
            params.put("aidsLeukemiaVaccineDate",fechaAidsLeukemiaVaccineDate);
        }

        params.put("email",mascota.getEmail());
        params.setUseJsonStreamer(true);
        Log.println(Log.DEBUG,"Servicio Rest",params.toString());
        invokeWS(params);
    }

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param params
     */
    public void invokeWS(RequestParams params){
        Log.println(Log.DEBUG,"PET savePet",params.toString());
        // Show Progress Dialog
        prgDialog.show();
        //Log.println(Log.INFO,"REST","parametros enviados en el servicio: "+params.toString());
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        //String url = "https://mocatto.herokuapp.com/rest/en/pet/savePet";
        String url = getString(R.string.service_save_pet);
        //String url = "http://192.168.1.52:8080/Mocatto/rest/en/pet/savePet";

        client.post(url,params, new JsonHttpResponseHandler() {
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
                prgDialog.hide();
                try {
                    int id =timeline.getInt("id");
                    Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
                    Pet pet = gson.fromJson(timeline.toString(), Pet.class);
                    Log.println(Log.DEBUG,"PET savePet",pet.toString());
                    mascota.setId(pet.getId());
                    if(PetDB.existPhoto(getActivity(),mascota.getId())){
                        PetDB.updatePhoto(mascota,getActivity());
                    }else {
                        PetDB.savePhoto(mascota, getActivity());
                    }
                    Alarm.createApointmentsFromPetInformation(mascota,getActivity(),cuenta);
                    Toast.makeText(getContext(), getString(R.string.mje_pet_register_sucessful), Toast.LENGTH_LONG).show();
                    Intent refresh = new Intent(getActivity(), NavigationDrawerActivity.class);
                    refresh.putExtra("email",  email);
                    refresh.putExtra("cuenta",  cuenta);
                    refresh.putExtra("isFromCreateAccount","false");
                    startActivity(refresh);
                    getActivity().finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                //Toast.makeText(getContext(), R.string.mje_error_register_pet, Toast.LENGTH_LONG).show();
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getContext(), R.string.recurso_no_encontrado, Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getContext(), R.string.error_desconocido, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadSpinners() {
        loadSpinnerWhereShower();
        loadSpinnerOhers();
    }

    private void loadSpinnerOhers() {
        spOtros = (Spinner) view.findViewById(R.id.spOtros);
        ArrayAdapter adaptadorOtros = ArrayAdapter.createFromResource(
                getActivity(), R.array.opcSiNo, android.R.layout.simple_spinner_item);
        adaptadorOtros.setDropDownViewResource(R.layout.spinner_item);
        spOtros.setAdapter(adaptadorOtros);
        spDondeBanio.setSelection(Util.setValueInSpinner(mascota.getBathLocation(),adaptadorOtros));
        spDondeBanio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.GRAY);
                ((TextView) adapterView.getChildAt(0)).setTextSize(18);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        txtEvento.setVisibility(View.GONE);
        lbEvento.setVisibility(View.GONE);
        txtCadaCuanto.setVisibility(View.GONE);
        lbCadaCuanto.setVisibility(View.GONE);
        spOtros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                String item = arg0.getSelectedItem().toString();
                if (item.equals(getString(R.string.si_spa))) {
                    txtEvento.setVisibility(View.VISIBLE);
                    lbEvento.setVisibility(View.VISIBLE);
                    txtCadaCuanto.setVisibility(View.VISIBLE);
                    lbCadaCuanto.setVisibility(View.VISIBLE);
                } else {
                    txtEvento.setVisibility(View.GONE);
                    lbEvento.setVisibility(View.GONE);
                    txtCadaCuanto.setVisibility(View.GONE);
                    lbCadaCuanto.setVisibility(View.GONE);
                }
                ((TextView) arg0.getChildAt(0)).setTextColor(Color.GRAY);
                ((TextView) arg0.getChildAt(0)).setTextSize(18);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // Hacer algo
            }
        });
    }

    private void loadSpinnerWhereShower() {
        spDondeBanio = (Spinner) view.findViewById(R.id.spDondeBanio);
        ArrayAdapter adaptadorDondeBanio = ArrayAdapter.createFromResource(
                getActivity(), R.array.opcDondeBanio, android.R.layout.simple_spinner_item);
        adaptadorDondeBanio.setDropDownViewResource(R.layout.spinner_item);
        spDondeBanio.setAdapter(adaptadorDondeBanio);
        spDondeBanio.setSelection(Util.setValueInSpinner(mascota.getBathLocation(),adaptadorDondeBanio));
    }

    private void startButtonStart() {
        //Obtener valores de los campos de la sección alimentación
        final TextView btnRegister4 = (TextView) view.findViewById(R.id.nextRegister4);
        btnRegister4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataPet();

                if (isOKVerifications){
                    //Consumir servicio rest para grabar mascota
                    callServiceRegisterPet();

                    /*Bundle args = new Bundle();
                    args.putString(ARG_PARAM1, email);
                    args.putSerializable(ARG_PARAM2, mascota);
                    MenuFragment fragment = MenuFragment.newInstance(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.main_fragment, fragment);
                    ft.commit();*/
                }else{
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    errorMessage="";
                }
            }
        });
    }

    private void loadPetInformation() {
        if (mascota!=null && for_update){
            txtFrecuenciaBanio.setText(String.valueOf(mascota.getBathFrecuency()));
            txtCadaCuanto.setText(mascota.getRegularity());
            //spOtros
            txtEvento.setText(mascota.getEvent());
            if(mascota.getLastBath()!=null) {
                txtFechaUltimoBanio.setText(simpleDateFormat.format(mascota.getLastBath()));
            }
        }
    }

    private void startFields(){
        lbFechaUltimoBanio = (TextView)view.findViewById(R.id.lbFechaUltimoBanio);
        txtFechaUltimoBanio = (EditText)view.findViewById(R.id.txtFechaUltimoBanio);
        lbOtros = (TextView)view.findViewById(R.id.lbOtros);
        spOtros = (Spinner)view.findViewById(R.id.spOtros);

        lbEvento= (TextView) view.findViewById(R.id.lbEvento);
        txtEvento = (EditText) view.findViewById(R.id.txtEvento);
        txtEvento.setImeOptions(EditorInfo.IME_ACTION_DONE);

        lbCadaCuanto= (TextView) view.findViewById(R.id.lbCadaCuanto);
        txtCadaCuanto = (EditText) view.findViewById(R.id.txtCadaCuanto);
        txtCadaCuanto.setImeOptions(EditorInfo.IME_ACTION_DONE);

        lbFrecuenciaBanio = (TextView)view.findViewById(R.id.lbFrecuenciaBanio);
        txtFrecuenciaBanio = (EditText)view.findViewById(R.id.txtFrecuenciaBanio);
        txtFrecuenciaBanio.setImeOptions(EditorInfo.IME_ACTION_DONE);

        lbDonde = (TextView)view.findViewById(R.id.lbDondeBanio);
        spDondeBanio = (Spinner)view.findViewById(R.id.spDondeBanio);

    }

    @NonNull
    private void getDataPet() {
        mascota.setOthers(spOtros.getSelectedItem().toString());
        mascota.setBathFrecuency(util.verificarIntegersNull(txtFrecuenciaBanio));//ojo que esta granbando 0
        mascota.setBathLocation(spDondeBanio.getSelectedItem().toString());
        mascota.setLastBath(util.verificarDateNull(txtFechaUltimoBanio));
        mascota.setEvent(txtEvento.getText().toString());
        mascota.setRegularity(txtCadaCuanto.getText().toString());
        mascota.setPhoto(photoPet);
        isOKVerifications=true;
        if (Util.isDateInTheFuture(mascota.getLastBath())){
            errorMessage = getString(R.string.mje_date_is_the_future);
            isOKVerifications=false;
        }
        //Log.println(Log.INFO, "Perfil Mascota", "Se esta ingreando la mascota " + mascota.toString());
    }

    private void startDatesAndCalendars() {
        txtFechaUltimoBanio.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showCalendar(txtFechaUltimoBanio,getActivity());
            }
        });
    }

    private void getInitialValues() {
        if (getArguments() != null) {
            email = getArguments().getString("email");
            mascota = (Pet)getArguments().getSerializable("mascota");
            cuenta = (Cuenta) getActivity().getIntent().getExtras().getSerializable("cuenta");
            for_update = getArguments().getBoolean("for_update");
        }
        util = new Util();
        mImg = (ImageView) view.findViewById(R.id.ivImagen);
        if (mascota.getPhoto()!=null) {
            mImg.setImageBitmap(BitmapFactory.decodeByteArray(mascota.getPhoto(), 0, mascota.getPhoto().length));
            photoPet = mascota.getPhoto();
        }
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }


    private void showSpecieFields() {
        //solo para perros
        lbFrecuenciaBanio.setVisibility(View.GONE);
        txtFrecuenciaBanio.setVisibility(View.GONE);
        lbFechaUltimoBanio.setVisibility(View.GONE);
        txtFechaUltimoBanio.setVisibility(View.GONE);
        lbDonde.setVisibility(View.GONE);
        spDondeBanio.setVisibility(View.GONE);

        if (mascota.getSpecie().equals(getString(R.string.dog))) {
            //Visualizar campos solo para caninos
            lbFrecuenciaBanio.setVisibility(View.VISIBLE);
            txtFrecuenciaBanio.setVisibility(View.VISIBLE);
            lbFechaUltimoBanio.setVisibility(View.VISIBLE);
            txtFechaUltimoBanio.setVisibility(View.VISIBLE);
            lbDonde.setVisibility(View.VISIBLE);
            spDondeBanio.setVisibility(View.VISIBLE);
        }
    }

    public void openGallery(View v){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("crop", "true");
        photoPickerIntent.putExtra("return-data", true);
        photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(photoPickerIntent,REQ_CODE_PICK_IMAGE);
    }

    private void startImageView() {
        mImg = (ImageView) view.findViewById(R.id.ivImagen);
        mImg.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(v);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Uri selectedImageUri = null;
        Uri selectedImage;

        String filePath = null;
        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if (resultCode == getActivity().RESULT_OK) {
                    if (imageReturnedIntent!=null) {
                        Bundle extras = imageReturnedIntent.getExtras();
                        Bitmap selectedBitmap = extras.getParcelable("data");
                        selectedBitmap = util.getCircleBitmap(selectedBitmap);
                        mImg = (ImageView) view.findViewById(R.id.ivImagen);
                        mImg.setImageBitmap(selectedBitmap);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        photoPet = stream.toByteArray();
                    }
                }
                break;
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
}
