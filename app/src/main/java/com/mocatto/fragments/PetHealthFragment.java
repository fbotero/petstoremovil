package com.mocatto.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.imanoweb.calendarview.CustomCalendarView;
import com.mocatto.R;
import com.mocatto.dto.Cuenta;
import com.mocatto.dto.Pet;
import com.mocatto.util.Util;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PetHealthFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PetHealthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PetHealthFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "mascota";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private Pet mascota;
    private SimpleDateFormat simpleDateFormat;
    private String email;
    protected static final int REQ_CODE_PICK_IMAGE = 1;
    ImageView mImg;
    private Util util;
    private CustomCalendarView calendarView;

    private Spinner spVacunas;
    private Spinner spTosPerrera;
    private Spinner spUsaAntipulgas;
    private Spinner spEstaEsterilizada,spLeucemiaSida;
    private TextView lbLeucemiaSida,lbTosPerrera,lbFechaUltTosPerrera,lbUsoAntipulgas,lbUltVacunaLeucemiaSida;
    private EditText txtFechaUltimasVacunas,txtFechaUltTosPerrera,txtUltVacunaLeucemiaSida;
    private TextView lbFechaUltimaVacuna;
    private EditText txtFechaUltDesparacitada;
    private EditText txtFechaUltVacunaLeucemiaSida;
    private EditText txtFechaUltVisitaVeterinario;
    private CustomCalendarView calendarViewVacunaSidaLeucemia;
    private CustomCalendarView calendarViewUltVisitaVeterinario;
    private CustomCalendarView calendarViewTosPerrera;
    private CustomCalendarView calendarViewDesparacitada;

    private OnFragmentInteractionListener mListener;
    private String errorMessage;
    private boolean isOKVerifications=true;
    private byte[] photoPet;
    private boolean for_update;
    private Cuenta cuenta;
    private ArrayAdapter adaptadorVacunas=null;

    private AdView mAdView;

    public PetHealthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PetHealthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PetHealthFragment newInstance(String param1, String param2) {
        PetHealthFragment fragment = new PetHealthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static PetHealthFragment newInstance(Bundle args) {
        PetHealthFragment fragment = new PetHealthFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_pet_health, container, false);
        getActivity().setTitle(getString(R.string.health));
        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        getInitialValues();
        startFields();
        startDataCat();
        startDataDog();
        startDatesAndCalendars();
        startImageView();
        loadSpinners();
        showSpecieFields();
        loadPetInformation();

        startButtonStart();
        return view;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void showSpecieFields(){
        //solo para perros
        lbTosPerrera.setVisibility(View.GONE);
        spTosPerrera.setVisibility(View.GONE);
        lbFechaUltTosPerrera.setVisibility(View.GONE);
        txtFechaUltTosPerrera.setVisibility(View.GONE);
        lbUsoAntipulgas.setVisibility(View.GONE);
        spUsaAntipulgas.setVisibility(View.GONE);

        //solo para gato
        lbLeucemiaSida.setVisibility(View.GONE);
        spLeucemiaSida.setVisibility(View.GONE);
        lbUltVacunaLeucemiaSida.setVisibility(View.GONE);
        txtUltVacunaLeucemiaSida.setVisibility(View.GONE);

        //Log.println(Log.INFO,"MASCOTA",mascota.toString());

        if(mascota.getSpecie().equals(getString(R.string.dog))){
            //Visualizar campos solo para caninos
            lbTosPerrera.setVisibility(View.VISIBLE);
            spTosPerrera.setVisibility(View.VISIBLE);
            lbFechaUltTosPerrera.setVisibility(View.VISIBLE);
            txtFechaUltTosPerrera.setVisibility(View.VISIBLE);
            lbUsoAntipulgas.setVisibility(View.VISIBLE);
            spUsaAntipulgas.setVisibility(View.VISIBLE);
        }else if(mascota.getSpecie().equals(getString(R.string.cat))){
            //Visualizar campos solo para Felinos
            txtUltVacunaLeucemiaSida.setVisibility(View.VISIBLE);
            spLeucemiaSida.setVisibility(View.VISIBLE);
            lbUltVacunaLeucemiaSida.setVisibility(View.VISIBLE);
            lbLeucemiaSida.setVisibility(View.VISIBLE);
        }
    }

    private void startButtonStart() {
        //Obtener valores de los campos de la sección salud
        final TextView btnRegister3 = (TextView) view.findViewById(R.id.nextRegister3);
        btnRegister3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Capturando los datos de la segunda pantalla de registro (salud)
                getDataPet();
                if (isOKVerifications){
                    Bundle args = new Bundle();
                    args.putString(ARG_PARAM1, email);
                    args.putSerializable(ARG_PARAM2, mascota);
                    args.putSerializable("cuenta",cuenta);
                    args.putBoolean("for_update",  for_update);
                    PetFoodFragment fragment = PetFoodFragment.newInstance(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.main_fragment, fragment);
                    ft.commit();
                }else{
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    errorMessage="";
                }
            }
        });
    }

    private void loadPetInformation() {
        if (mascota!=null && for_update){
            //Se carga información de la mascota en el formulario
            //Imagen
            mImg = (ImageView) view.findViewById(R.id.ivImagen);
            if (mascota.getPhoto()!=null) {
                mImg.setImageBitmap(BitmapFactory.decodeByteArray(mascota.getPhoto(), 0, mascota.getPhoto().length));
                photoPet = mascota.getPhoto();
            }
            if (mascota.getLastVaccine()!=null){
                txtFechaUltimasVacunas.setText(simpleDateFormat.format(mascota.getLastVaccine()));
            }
            if (mascota.getWoopingCoughDate()!=null) {
                txtFechaUltTosPerrera.setText(simpleDateFormat.format(mascota.getWoopingCoughDate()));
            }
            if (mascota.getLastWorm()!=null){
                txtFechaUltDesparacitada.setText(simpleDateFormat.format(mascota.getLastWorm()));
            }
            if (mascota.getAidsLeukemiaVaccineDate()!=null){
                txtFechaUltVacunaLeucemiaSida.setText(simpleDateFormat.format(mascota.getAidsLeukemiaVaccineDate()));
            }
            if (mascota.getLastVetVisit()!=null){
                txtFechaUltVisitaVeterinario.setText(simpleDateFormat.format(mascota.getLastVetVisit()));
            }

            if (mascota.getVaccine()!=null) {
                Util.setValueInSpinner(mascota.getVaccine(),adaptadorVacunas);
            }
            //spTosPerrera - spUsaAntipulgas - spEstaEsterilizada - spLeucemiaSida
            //0 - NO     1 - SI
            if (mascota.getWoopingCough()!=null) {
                if (mascota.getWoopingCough().equals(getString(R.string.si))) {
                    spTosPerrera.setSelection(1);
                } else {
                    spTosPerrera.setSelection(0);
                }
            }
            if (mascota.getAntiFlea()!=null) {
                if (mascota.getAntiFlea().equals(getString(R.string.si))) {
                    spUsaAntipulgas.setSelection(1);
                } else {
                    spUsaAntipulgas.setSelection(0);
                }
            }
            if (mascota.getSterilized()!=null) {
                if (mascota.getSterilized().equals(getString(R.string.si))) {
                    spEstaEsterilizada.setSelection(1);
                } else {
                    spEstaEsterilizada.setSelection(0);
                }
            }
            if (mascota.getAidsLeukemiaVaccine()!=null) {
                if (mascota.getAidsLeukemiaVaccine().equals(getString(R.string.si))) {
                    spLeucemiaSida.setSelection(1);
                } else {
                    spLeucemiaSida.setSelection(0);
                }
            }
        }
    }

    private void printForm(){
        Log.println(Log.DEBUG,"FORM","-------------------------------------------------------------");
        Log.println(Log.DEBUG,"FORM","txtFechaUltimasVacunas: "+txtFechaUltimasVacunas.getText().toString());
        Log.println(Log.DEBUG,"FORM","spVacunas: "+spVacunas.getSelectedItem().toString());
        Log.println(Log.DEBUG,"FORM","spTosPerrera: "+spTosPerrera.getSelectedItem().toString());
        Log.println(Log.DEBUG,"FORM","txtFechaUltTosPerrera: "+txtFechaUltTosPerrera.getText().toString());
        Log.println(Log.DEBUG,"FORM","txtFechaUltDesparacitada: "+txtFechaUltDesparacitada.getText().toString());
        Log.println(Log.DEBUG,"FORM","spUsaAntipulgas: "+spUsaAntipulgas.getSelectedItem().toString());
        Log.println(Log.DEBUG,"FORM","txtFechaUltVisitaVeterinario: "+txtFechaUltVisitaVeterinario.getText().toString());
        Log.println(Log.DEBUG,"FORM","spEstaEsterilizada: "+spEstaEsterilizada.getSelectedItem().toString());
        Log.println(Log.DEBUG,"FORM","txtFechaUltVacunaLeucemiaSida: "+txtFechaUltVacunaLeucemiaSida.getText().toString());
        Log.println(Log.DEBUG,"FORM","spLeucemiaSida: "+spLeucemiaSida.getSelectedItem().toString());
        Log.println(Log.DEBUG,"FORM","-------------------------------------------------------------");
    }


    @NonNull
    private void getDataPet() {

       // printForm();

        mascota.setLastVaccine(util.verificarDateNull(txtFechaUltimasVacunas));
        mascota.setVaccine(spVacunas.getSelectedItem().toString());
        mascota.setWoopingCough(spTosPerrera.getSelectedItem().toString());
        mascota.setWoopingCoughDate(util.verificarDateNull(txtFechaUltTosPerrera));
        mascota.setLastWorm(util.verificarDateNull(txtFechaUltDesparacitada));
        mascota.setAntiFlea(spUsaAntipulgas.getSelectedItem().toString());
        mascota.setLastVetVisit(util.verificarDateNull(txtFechaUltVisitaVeterinario));
        mascota.setSterilized(spEstaEsterilizada.getSelectedItem().toString());

        mascota.setAidsLeukemiaVaccineDate(util.verificarDateNull(txtFechaUltVacunaLeucemiaSida));
        mascota.setAidsLeukemiaVaccine(spLeucemiaSida.getSelectedItem().toString());
        mascota.setPhoto(photoPet);

        isOKVerifications=true;
        if (mascota.getSpecie().equals(R.string.cat)){
            if (Util.isDateInTheFuture(mascota.getLastVaccine()) || Util.isDateInTheFuture(mascota.getLastWorm())
                    || Util.isDateInTheFuture(mascota.getLastVetVisit())){
                errorMessage = getString(R.string.mje_date_is_the_future);
                isOKVerifications=false;
            }
            if (mascota.getAidsLeukemiaVaccine().equals(R.string.si)){
                if (Util.isDateInTheFuture(mascota.getAidsLeukemiaVaccineDate())){
                    errorMessage = getString(R.string.mje_date_is_the_future);
                    isOKVerifications=false;
                }
            }
        }else{//Cuando es perro
            if (Util.isDateInTheFuture(mascota.getLastVaccine()) || Util.isDateInTheFuture(mascota.getLastWorm())
                    || Util.isDateInTheFuture(mascota.getLastVetVisit())){
                errorMessage = getString(R.string.mje_date_is_the_future);
                isOKVerifications=false;
            }
            if (mascota.getWoopingCough().equals(R.string.si)){
                if (Util.isDateInTheFuture(mascota.getWoopingCoughDate())){
                    errorMessage = getString(R.string.mje_date_is_the_future);
                    isOKVerifications=false;
                }
            }

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
                    }
                }
                break;
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

    private void startDataCat() {
        //Gato
        lbLeucemiaSida = (TextView)view.findViewById(R.id.lbLeucemiaSida);
        spLeucemiaSida= (Spinner)view.findViewById(R.id.spLeucemiaSida);
        lbUltVacunaLeucemiaSida = (TextView)view.findViewById(R.id.lbUltVacunaLeucemiaSida);
        txtUltVacunaLeucemiaSida = (EditText)view.findViewById(R.id.txtUltVacunaLeucemiaSida);
    }

    private void startDataDog() {
        //Perro
        lbFechaUltimaVacuna = (TextView)view.findViewById(R.id.lbFechaUltimaVacuna);
        txtFechaUltimasVacunas = (EditText)view.findViewById(R.id.txtFechaUltimasVacunas);
        lbTosPerrera = (TextView)view.findViewById(R.id.lbTosPerrera);
        spTosPerrera= (Spinner)view.findViewById(R.id.spTosPerrera);
        lbFechaUltTosPerrera = (TextView)view.findViewById(R.id.lbFechaUltTosPerrera);
        txtFechaUltTosPerrera= (EditText)view.findViewById(R.id.txtFechaUltTosPerrera);
        lbUsoAntipulgas = (TextView)view.findViewById(R.id.lbUsoAntipulgas);
        spUsaAntipulgas= (Spinner)view.findViewById(R.id.spUsaAntipulgas);

    }

    private void loadSpinners() {
        cargarSpinnerVacunas();
        cargarSpinnerTosPerrera();
        cargarSpinnerLeucemiaSIDA();
        cargarSpinnerUsaAntipulgas();
        cargarSpinnerEstaEstirilizado();
    }

    private void cargarSpinnerEstaEstirilizado() {
        spEstaEsterilizada = (Spinner) view.findViewById(R.id.spEstaEsterilizada);
        ArrayAdapter adaptadorEsterilizada = ArrayAdapter.createFromResource(
                getActivity(), R.array.opcSiNo, android.R.layout.simple_spinner_item);
        adaptadorEsterilizada.setDropDownViewResource(R.layout.spinner_item);
        spEstaEsterilizada.setAdapter(adaptadorEsterilizada);
        spEstaEsterilizada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void cargarSpinnerUsaAntipulgas() {
        spUsaAntipulgas = (Spinner) view.findViewById(R.id.spUsaAntipulgas);
        ArrayAdapter adaptadorUsaAntipulgas = ArrayAdapter.createFromResource(
                getActivity(), R.array.opcSiNo, android.R.layout.simple_spinner_item);
        adaptadorUsaAntipulgas.setDropDownViewResource(R.layout.spinner_item);
        spUsaAntipulgas.setAdapter(adaptadorUsaAntipulgas);
        spUsaAntipulgas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void cargarSpinnerTosPerrera() {
        spTosPerrera = (Spinner) view.findViewById(R.id.spTosPerrera);
        ArrayAdapter adaptadorTosPerrera = ArrayAdapter.createFromResource(
                getActivity(), R.array.opcSiNo, android.R.layout.simple_spinner_item);
        adaptadorTosPerrera.setDropDownViewResource(R.layout.spinner_item);
        spTosPerrera.setAdapter(adaptadorTosPerrera);


        spTosPerrera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                String item = arg0.getSelectedItem().toString();
                if (item.equals(getString(R.string.si))) {
                    txtFechaUltTosPerrera.setVisibility(View.VISIBLE);
                    lbFechaUltTosPerrera.setVisibility(View.VISIBLE);
                }else{
                    txtFechaUltTosPerrera.setVisibility(View.GONE);
                    lbFechaUltTosPerrera.setVisibility(View.GONE);
                }
                ((TextView) arg0.getChildAt(0)).setTextColor(Color.GRAY);
                ((TextView) arg0.getChildAt(0)).setTextSize(18);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // Hacer algo
            }
        });


    }

    private void cargarSpinnerLeucemiaSIDA() {
        spLeucemiaSida = (Spinner) view.findViewById(R.id.spLeucemiaSida);
        ArrayAdapter adaptadorLeucemia = ArrayAdapter.createFromResource(
                getActivity(), R.array.opcSiNo, android.R.layout.simple_spinner_item);
        adaptadorLeucemia.setDropDownViewResource(R.layout.spinner_item);
        spLeucemiaSida.setAdapter(adaptadorLeucemia);

        spLeucemiaSida.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                String item = arg0.getSelectedItem().toString();
                if (item.equals(getString(R.string.si))) {
                    txtFechaUltVacunaLeucemiaSida.setVisibility(View.VISIBLE);
                    lbUltVacunaLeucemiaSida.setVisibility(View.VISIBLE);

                }else{
                    txtFechaUltVacunaLeucemiaSida.setVisibility(View.GONE);
                    lbUltVacunaLeucemiaSida.setVisibility(View.GONE);
                }
                ((TextView) arg0.getChildAt(0)).setTextColor(Color.GRAY);
                ((TextView) arg0.getChildAt(0)).setTextSize(18);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // Hacer algo
            }
        });
    }

    private void cargarSpinnerVacunas() {
        spVacunas = (Spinner) view.findViewById(R.id.spVacunas);
        if (mascota.getSpecie().equals(getString(R.string.cat))){
            adaptadorVacunas = ArrayAdapter.createFromResource(
                    getActivity(), R.array.opcVacunasGato, android.R.layout.simple_spinner_item);
        }else if (mascota.getSpecie().equals(getString(R.string.dog))){
            adaptadorVacunas = ArrayAdapter.createFromResource(
                    getActivity(), R.array.opcVacunasPerro, android.R.layout.simple_spinner_item);
        }
        adaptadorVacunas.setDropDownViewResource(R.layout.spinner_item);
        spVacunas.setAdapter(adaptadorVacunas);
        spVacunas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void startFields(){
        calendarView = (CustomCalendarView) view.findViewById(R.id.calendar_view_ult_vacuna);
        calendarViewDesparacitada = (CustomCalendarView) view.findViewById(R.id.calendar_view_desparacitada);
        calendarViewTosPerrera = (CustomCalendarView) view.findViewById(R.id.calendar_view_tos_perrera);
        calendarViewUltVisitaVeterinario = (CustomCalendarView) view.findViewById(R.id.calendar_view_ult_visita_veterinario);
        calendarViewVacunaSidaLeucemia = (CustomCalendarView) view.findViewById(R.id.calendar_view_vacuna_sida);
        txtFechaUltimasVacunas = (EditText) view.findViewById(R.id.txtFechaUltimasVacunas);
        txtFechaUltTosPerrera = (EditText) view.findViewById(R.id.txtFechaUltTosPerrera);
        txtFechaUltDesparacitada = (EditText) view.findViewById(R.id.txtFechaUltDesparacitada);
        txtFechaUltVacunaLeucemiaSida = (EditText) view.findViewById(R.id.txtUltVacunaLeucemiaSida);
        txtFechaUltVisitaVeterinario = (EditText) view.findViewById(R.id.txtUltimaVisitaVeterinario);
    }


    private void startDatesAndCalendars() {
        txtFechaUltimasVacunas.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showCalendar(txtFechaUltimasVacunas,getActivity());
            }
        });

        txtFechaUltTosPerrera.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showCalendar(txtFechaUltTosPerrera,getActivity());
            }
        });

        txtFechaUltDesparacitada.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showCalendar(txtFechaUltDesparacitada,getActivity());
            }
        });

        txtFechaUltVacunaLeucemiaSida.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showCalendar(txtFechaUltVacunaLeucemiaSida,getActivity());
            }
        });

        txtFechaUltVisitaVeterinario.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showCalendar(txtFechaUltVisitaVeterinario,getActivity());
            }
        });
    }

    private void getInitialValues() {
        if (getArguments() != null) {
            email = getArguments().getString("email");
            mascota = (Pet)getArguments().getSerializable("mascota");
            for_update = getArguments().getBoolean("for_update");
            cuenta = (Cuenta) getActivity().getIntent().getExtras().getSerializable("cuenta");
        }
        util = new Util();
        mImg = (ImageView) view.findViewById(R.id.ivImagen);
        if (mascota.getPhoto()!=null) {
            mImg.setImageBitmap(BitmapFactory.decodeByteArray(mascota.getPhoto(), 0, mascota.getPhoto().length));
            photoPet = mascota.getPhoto();
        }

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
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
