package com.mocatto.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.NavigationDrawerActivity;
import com.mocatto.R;
import com.mocatto.alarm.Alarm;
import com.mocatto.db.PetDB;
import com.mocatto.dto.Cuenta;
import com.mocatto.dto.Data;
import com.mocatto.dto.Pet;
import com.mocatto.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PetProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PetProfileFragment#newInstance} factory method tofragmentTransaction = fragmentManager.beginTransaction();
 * create an instance of this fragment.
 */
public class PetProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "mascota";
    private static final String ARG_PARAM3 = "imagen_mascota";

    private Spinner spEspecie;
    private AutoCompleteTextView spRaza;
    private Pet mascota;
    private String email;
    private SimpleDateFormat simpleDateFormat;
    private EditText txtNombre,txtFechaNacimiento;
    private Spinner spPelaje,spSexo;
    protected static final int REQ_CODE_PICK_IMAGE = 1;
    protected static final int BREED_DOG = 1;
    protected static final int BREED_CAT = 2;
    ImageView mImg;
    private Util util;
    private boolean isOKVerifications=true;
    private View view;
    private byte[] photoPet;
    private String errorMessage;
    ProgressDialog prgDialog;
    ArrayAdapter adaptadorRazas;
    List razasPerro;
    List razasGato;
    ArrayAdapter adaptadorSexo;
    ArrayAdapter adaptadorEspecies;
    ArrayAdapter adaptadorPelaje;
    AdRequest adRequest;
    ScrollView scrollView;

    private OnFragmentInteractionListener mListener;
    private boolean for_update;
    private Cuenta cuenta;

    private AdView mAdView;

    public PetProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PetProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PetProfileFragment newInstance(String param1, String param2) {
        PetProfileFragment fragment = new PetProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static PetProfileFragment newInstance(Bundle args) {
        PetProfileFragment fragment = new PetProfileFragment();
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
        errorMessage="";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pet_profile, container, false);
        getActivity().setTitle(getString(R.string.perfil_pet));

        mAdView = (AdView) view.findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        scrollView = (ScrollView) view.findViewById(R.id.scrollView1);

        putScrollFocusUp();

        getInitialValues();
        callRestServices();
        getDataProfile();
        loadSpinners();
        loadPetInformation();//cuando existe la mascota
        startDatesAndCalendars();
        startImageView();
        startButtonRegister();

        return view;
    }

    private void putScrollFocusUp() {
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.smoothScrollTo(0,0);
    }

    private void callRestServices() {
        callServicelistBreeds(BREED_DOG);
        callServicelistBreeds(BREED_CAT);
    }

    public void callServicelistBreeds(final Integer specie) {
        RequestParams params = new RequestParams();
        params.put("specieId",specie);
        AsyncHttpClient client = new AsyncHttpClient();
        //String url = "https://mocatto.herokuapp.com/rest/en/util/getRaceList/"+specie;
        String url = getString(R.string.service_list_breeds)+specie;
        //String url = "http://192.168.19.246:8080/Mocatto/rest/en/util/getRaceList/"+specie;


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
                    if (specie==BREED_DOG) {
                        razasPerro = new ArrayList();
                        for (int i = 0; i < listData.length; i++) {
                            razasPerro.add(listData[i].getName());
                        }
                    }else if (specie==BREED_CAT) {
                        razasGato = new ArrayList();
                        for (int i = 0; i < listData.length; i++) {
                            razasGato.add(listData[i].getName());
                        }
                    }
                    loadSpinnerSpecies();
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

    private void startButtonRegister() {
        //Obtener valores de los campos de la sección perfil
        final TextView btnRegister1 = (TextView) view.findViewById(R.id.nextRegister1);
        btnRegister1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Capturando los datos de la primera pantalla de registro
                getPetProfile();
                Toast toast;
                if (isOKVerifications) {
                    //Consumir servicio Rest ingreso de datos
                    callServiceRegisterPet();
                    dialog();
                }else{
                    toast =
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.LEFT, 0, 0);
                    toast.show();
                    errorMessage="";
                }
            }
        });
    }

    private void dialog(){
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.mje_do_you_want_filling_the_profile);
        alertDialog.setPositiveButton(R.string.mje_answer_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle args = new Bundle();
                args.putString(ARG_PARAM1, email);
                args.putSerializable(ARG_PARAM2, mascota);
                args.putBoolean("for_update",  for_update);
                args.putSerializable("cuenta",cuenta);
                args.putString("isFromCreateAccount","false");
                PetHealthFragment fragment = PetHealthFragment.newInstance(args);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.main_fragment, fragment);
                ft.commit();

            }
        });
        alertDialog.setNegativeButton(R.string.mje_answer_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Retorna al menú
                // Refresh main activity upon close of dialog box
                Intent refresh = new Intent(getActivity(), NavigationDrawerActivity.class);
                refresh.putExtra("email",  email);
                refresh.putExtra("cuenta",  cuenta);
                refresh.putExtra("isFromCreateAccount","false");
                startActivity(refresh);
                getActivity().finish(); //
            }
        });
        alertDialog.show();

    }

    private void callServiceRegisterPet() {
        RequestParams params = new RequestParams();
        SimpleDateFormat simpleDateFormatSave = new SimpleDateFormat("yyyy-MM-dd");
        params.put("id",mascota.getId());
        params.put("name",mascota.getName());
        if (mascota.getSpecie().equals(getString(R.string.cat)) || mascota.getSpecie().equals(getString(R.string.feline))){
            params.put("specie",getString(R.string.feline));
        }else if (mascota.getSpecie().equals(getString(R.string.dog)) || mascota.getSpecie().equals(getString(R.string.canine))){
            params.put("specie",getString(R.string.canine));
        }
        params.put("race",mascota.getRace());
        params.put("gender",mascota.getGender());
        String fechaNacimiento = simpleDateFormatSave.format(mascota.getBirthDate());
        params.put("birthDate",fechaNacimiento);
        params.put("fur",mascota.getFur());

        if (for_update) {
            params.put("diet", mascota.getDiet());
            String cual_dieta = "";
            if (mascota.getDietName() != null) {
                cual_dieta = mascota.getDietName();
                params.put("dietName", cual_dieta);
            }

            /*String cantidad_gr = "0";
            if(mascota.getFoodGrams()!=null){
                cantidad_gr=String.valueOf(mascota.getFoodGrams());
            }*/
            params.put("foodGrams", Util.integerToString(mascota.getFoodGrams()));
            params.put("foodBrand", mascota.getFoodBrand());
            String cual_marca = "";
            if (mascota.getFoodBrandName() != null) {
                cual_marca = mascota.getFoodBrandName();
                params.put("foodBrandName", cual_marca);
            }

            String cada_cuanto_compra_comida = "0";
            if (mascota.getFoodBuyRegularity() != null) {
                cada_cuanto_compra_comida = String.valueOf(mascota.getFoodBuyRegularity());
                params.put("foodBuyRegularity", cada_cuanto_compra_comida);
            }

            /*String lastFoodBuyDate=null;
            if (mascota.getLastFoodBuyDate()!=null){
                lastFoodBuyDate = simpleDateFormatSave.format(mascota.getLastFoodBuyDate());
            }*/
            params.put("lastFoodBuyDate", Util.dateToString(mascota.getLastFoodBuyDate()));
            String lastVaccine = "";
            if (mascota.getLastVaccine() != null) {
                lastVaccine = simpleDateFormatSave.format(mascota.getLastVaccine());
                params.put("lastVaccine", lastVaccine);
            }
            params.put("vaccine", mascota.getVaccine());
            String value = "false";
            if (mascota.getWoopingCough()!=null){
                if (mascota.getWoopingCough().equals("Si") || mascota.getWoopingCough().equals("Yes")){
                    value = "true";
                }
            }
            params.put("woopingCough", value);
            String fechaTosPerrera = "";
            if (mascota.getWoopingCoughDate() != null) {
                fechaTosPerrera = simpleDateFormatSave.format(mascota.getWoopingCoughDate());
                params.put("woopingCoughDate", fechaTosPerrera);
            }

            String fechaLastWorm = "";
            if (mascota.getLastWorm() != null) {
                fechaLastWorm = simpleDateFormatSave.format(mascota.getLastWorm());
                params.put("lastWorm", fechaLastWorm);
            }

            params.put("antiFlea", mascota.getAntiFlea());
            String fechaLastVetVisit = "";
            if (mascota.getLastVetVisit() != null) {
                fechaLastVetVisit = simpleDateFormatSave.format(mascota.getLastVetVisit());
                params.put("lastVetVisit", fechaLastVetVisit);
            }
            value = "false";
            if (mascota.getSterilized()!=null){
                if (mascota.getSterilized().equals("Si") || mascota.getSterilized().equals("Yes")){
                    value = "true";
                }
            }
            params.put("sterilized", value);
            String bathFrecuency = "0";
            if (mascota.getBathFrecuency() != null) {
                bathFrecuency = String.valueOf(mascota.getBathFrecuency());
                params.put("bathFrecuency", bathFrecuency);
            }


            String fechaLastBath = "";
            if (mascota.getLastBath() != null) {
                fechaLastBath = simpleDateFormatSave.format(mascota.getLastBath());
                params.put("lastBath", fechaLastBath);
            }

            params.put("bathLocation", mascota.getBathLocation());
            params.put("others", mascota.getOthers());
            String event = "";
            if (mascota.getEvent() != null) {
                event = String.valueOf(mascota.getEvent());
                params.put("event", event);
            }

            String regularity = "";
            if (mascota.getRegularity() != null) {
                regularity = String.valueOf(mascota.getRegularity());
                params.put("regularity", regularity);
            }
            value = "false";
            if (mascota.getAidsLeukemiaVaccine()!=null){
                if (mascota.getAidsLeukemiaVaccine().equals("Si") || mascota.getAidsLeukemiaVaccine().equals("Yes")){
                    value = "true";
                }
            }
            params.put("aidsLeukemiaVaccine", value);
            String fechaAidsLeukemiaVaccineDate = "";
            if (mascota.getAidsLeukemiaVaccineDate() != null) {
                fechaAidsLeukemiaVaccineDate = simpleDateFormatSave.format(mascota.getAidsLeukemiaVaccineDate());
                params.put("aidsLeukemiaVaccineDate", fechaAidsLeukemiaVaccineDate);
            }
        }
        params.put("email",mascota.getEmail());
        params.setUseJsonStreamer(true);
        invokeWS(params);
        //Log.println(Log.INFO,"REST","parametros enviados en el servicio: "+params.toString());
    }

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param params
     */
    public void invokeWS(RequestParams params){
        // Show Progress Dialog
        prgDialog.show();

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        String url = getString(R.string.service_save_pet);

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
                    Pet pet = new Gson().fromJson(timeline.toString(), Pet.class);
                    mascota.setId(pet.getId());
                    if(PetDB.existPhoto(getActivity(),mascota.getId())){
                        PetDB.updatePhoto(mascota,getActivity());
                    }else {
                        PetDB.savePhoto(mascota, getActivity());
                    }
                    Alarm.createApointmentFromPetProfileInformation(mascota,getActivity(),cuenta);

                    Toast.makeText(getContext(), getString(R.string.mje_pet_register_sucessful), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void getPetProfile() {
        if(mascota!=null) {//Es función de actualización
            mascota.setName(txtNombre.getText().toString());
            mascota.setSpecie(spEspecie.getSelectedItem().toString());
            mascota.setGender(spSexo.getSelectedItem().toString());
            mascota.setRace(spRaza.getText().toString());
            //Log.println(Log.DEBUG,"DATE","FECHA NACIMIENTO: "+txtFechaNacimiento.getText().toString());
            mascota.setBirthDate(util.verificarDateNull(txtFechaNacimiento));
            //Log.println(Log.DEBUG,"DATE","FECHA NACIMIENTO tipo date: "+mascota.getBirthDate());
            mascota.setFur(spPelaje.getSelectedItem().toString());
            mascota.setPhoto(photoPet);
            mascota.setEmail(email);
        }else{//Es función de ingreso de mascota
            mascota = new Pet(null,
                    txtNombre.getText().toString(),
                    spEspecie.getSelectedItem().toString(),
                    spRaza.getText().toString(),
                    spSexo.getSelectedItem().toString(),
                    util.verificarDateNull(txtFechaNacimiento),
                    spPelaje.getSelectedItem().toString(),
                    email, photoPet);
        }

        isOKVerifications=true;

        if (!razasGato.contains(mascota.getRace()) && !razasPerro.contains(mascota.getRace())){
            errorMessage = getString(R.string.mje_race_is_into_list);
            isOKVerifications=false;
        }
        if (Util.isDateInTheFuture(mascota.getBirthDate())){
            errorMessage = getString(R.string.mje_date_is_the_future);
            isOKVerifications=false;
        }
        if (util.isEmpty(txtNombre) || util.isEmpty(spEspecie) || util.isEmpty(spRaza) ||
                util.isEmpty(spSexo) || util.isEmpty(txtFechaNacimiento) || util.isEmpty(spPelaje)){
            errorMessage+= " "+ getString(R.string.mje_information_fields_is_complete);
            isOKVerifications=false;
        }
    }


    private void getDataProfile() {
        //Capturar información del Perfil
        txtFechaNacimiento = (EditText)view.findViewById(R.id.txtFechaNacimiento);
        txtNombre = (EditText) view.findViewById(R.id.txtNombre);
        txtNombre.setImeOptions(EditorInfo.IME_ACTION_DONE);
        spRaza = (AutoCompleteTextView) view.findViewById(R.id.spRaza);
        spRaza.setImeOptions(EditorInfo.IME_ACTION_DONE);
        spEspecie = (Spinner) view.findViewById(R.id.spEspecie);
        spPelaje = (Spinner)view.findViewById(R.id.spPelaje);
        spSexo = (Spinner)view.findViewById(R.id.spSexo);
    }

    private void enableOrDisableAdmobBanner(View view, boolean b) {
        Log.println(Log.DEBUG,"enableOrDisables","b: "+b);
        if(!b){
            hideSoftInput(view);
        }
    }

    private void hideSoftInput(View view) {
        view.clearFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        View view1 = getActivity().getCurrentFocus();
        view1.clearFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }

    }

    private void loadPetInformation() {
        if (mascota!=null){
            //Se carga información de la mascota en el formulario
            //Imagen
            mImg = (ImageView) view.findViewById(R.id.ivImagen);
            if (mascota.getPhoto()!=null) {
                mImg.setImageBitmap(BitmapFactory.decodeByteArray(mascota.getPhoto(), 0, mascota.getPhoto().length));
                photoPet = mascota.getPhoto();
            }
            txtNombre.setText(mascota.getName());
            txtFechaNacimiento.setText(simpleDateFormat.format(mascota.getBirthDate()));
            spRaza.setText(mascota.getRace());

            //Sexo
            int positionSexo = 0;
            for (int index = 0; index < adaptadorSexo.getCount(); ++index) {
                String value = (String)adaptadorSexo.getItem(index);
                if (value.equals(mascota.getGender())){
                    positionSexo=index;
                }
            }
            spSexo.setSelection(positionSexo);

            //Pelaje
            int positionPelaje = 0;
            for (int index = 0; index < adaptadorPelaje.getCount(); ++index) {
                String value = (String)adaptadorPelaje.getItem(index);
                if (value.equals(mascota.getFur())){
                    positionPelaje=index;
                }
            }
            spPelaje.setSelection(positionPelaje);
        }
    }

    private void getInitialValues() {
        //mascota = null;
        util = new Util();
        for_update=false;
        if (getArguments() != null) {
            email = getArguments().getString("email");
            mascota = (Pet)getArguments().getSerializable("mascota");
            if (mascota!=null){
                for_update = true;
            }
            cuenta = (Cuenta) getActivity().getIntent().getExtras().getSerializable("cuenta");
        }
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
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

    public void openGallery(View v){
        try{
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerIntent.setType("image/*");
            photoPickerIntent.putExtra("crop", "true");
            photoPickerIntent.putExtra("return-data", true);
            photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(photoPickerIntent,REQ_CODE_PICK_IMAGE);
        }catch(ActivityNotFoundException e){
            String errorMessage = "Tu dispositivo no soporta el recorte de imagenes";
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
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
                        putScrollFocusUp();
                    }
                }
                break;
        }
    }

    private void startDatesAndCalendars() {
        txtFechaNacimiento.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showCalendar(txtFechaNacimiento,getActivity());
            }
        });
    }

    private void loadSpinners() {
        loadSpinnerSex();
        loadSpinnerFur();
    }

    private void loadSpinnerFur() {
        spPelaje = (Spinner) view.findViewById(R.id.spPelaje);
        adaptadorPelaje = ArrayAdapter.createFromResource(
                getActivity(), R.array.opcPelaje, android.R.layout.simple_spinner_item);
        //adaptadorPelaje.setDropDownViewResource(R.layout.spinner_item);
        adaptadorPelaje.setDropDownViewResource(R.layout.spinner_item);
        spPelaje.setAdapter(adaptadorPelaje);
        spPelaje.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void loadSpinnerSex() {
        spSexo = (Spinner) view.findViewById(R.id.spSexo);
        adaptadorSexo = ArrayAdapter.createFromResource(
                getActivity(), R.array.opcSexo, android.R.layout.simple_spinner_item);
        adaptadorSexo.setDropDownViewResource(R.layout.spinner_item);
        spSexo.setAdapter(adaptadorSexo);
        spSexo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void loadSpinnerSpecies() {
        spEspecie = (Spinner) view.findViewById(R.id.spEspecie);
        adaptadorEspecies = ArrayAdapter.createFromResource(
                getActivity(), R.array.opcEspecies, android.R.layout.simple_spinner_item);
        adaptadorEspecies.setDropDownViewResource(R.layout.spinner_item);
        spEspecie.setAdapter(adaptadorEspecies);
        spEspecie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (for_update) {
                    Thread timerTread = new Thread() {
                        public void run() {
                            try {
                                sleep(3500);//Se duerme 3 segundos para que alcance a cargar los datos
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    timerTread.start();
                }

                ((TextView) arg0.getChildAt(0)).setTextColor(Color.GRAY);
                ((TextView) arg0.getChildAt(0)).setTextSize(18);

                loadSpinnerRaces(arg0);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // Hacer algo
            }
        });
        if (mascota!=null) {
            //Specie
            int positionSpecie = 0;
            String especie = "";
            if (mascota.getSpecie().equals(getString(R.string.canine)) || mascota.getSpecie().equals(getString(R.string.dog))){
                especie=getString(R.string.dog);
            }else if (mascota.getSpecie().equals(getString(R.string.feline)) || mascota.getSpecie().equals(getString(R.string.cat))){
                especie=getString(R.string.cat);
            }

            for (int index = 0; index < adaptadorEspecies.getCount(); ++index) {
                String value = (String) adaptadorEspecies.getItem(index);
                if (value.equals(especie)) {
                    positionSpecie = index;
                }
            }
            spEspecie.setSelection(positionSpecie);
        }
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity)ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void loadSpinnerRaces(AdapterView<?> arg0) {
        // Hacer algo
        int mostrar = 0;
        String item = arg0.getSelectedItem().toString();
        //spRaza = (Spinner) findViewById(R.id.spRaza);
        spRaza = (AutoCompleteTextView) view.findViewById(R.id.spRaza);
        adaptadorRazas=null;

        if (item.equals(getString(R.string.dog))) {
            adaptadorRazas = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item, razasPerro);
        } else if (item.equals(getString(R.string.cat))) {
            adaptadorRazas = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item, razasGato);
        }
        adaptadorRazas.setDropDownViewResource(R.layout.spinner_item);
        spRaza.setAdapter(adaptadorRazas);

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