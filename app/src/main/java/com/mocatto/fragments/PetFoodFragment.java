package com.mocatto.fragments;

import android.app.Activity;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.imanoweb.calendarview.CustomCalendarView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.R;
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
 * {@link PetFoodFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PetFoodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PetFoodFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "mascota";
    protected static final int BREED_DOG = 1;
    protected static final int BREED_CAT = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View view;
    private Pet mascota;
    private SimpleDateFormat simpleDateFormat;
    private String email;
    protected static final int REQ_CODE_PICK_IMAGE = 1;
    ImageView mImg;
    private Util util;
    private CustomCalendarView calendarViewUltCompraComida;
    private AutoCompleteTextView spMarcaComida;
    private Spinner spTipoDieta;
    private EditText txtCualDieta,txtCualMarca;
    private EditText txtCantidad_gr,txtCadaCuantoCompraComida,txtFechaUltimaCompraComida;
    private TextView lbCualMarca,lbCualDieta;
    private String errorMessage;
    private boolean isOKVerifications=true;
    private List listaMarcas=null;
    private byte[] photoPet;
    List marcasPerro;
    List marcasGato;
    List dietaPerro;
    List dietaGato;
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

    public PetFoodFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PetFoodFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PetFoodFragment newInstance(String param1, String param2) {
        PetFoodFragment fragment = new PetFoodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static PetFoodFragment newInstance(Bundle args) {
        PetFoodFragment fragment = new PetFoodFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pet_food, container, false);
        getActivity().setTitle(getString(R.string.alimentacion_pet));
        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        getInitialValues();
        callRestServices();
        startFields();
        startDatesAndCalendars();
        //loadSpinners();
        startImageView();
        loadPetInformation();
        startButtonStart();
        return view;
    }

    private void callRestServices() {
        if(mascota.getSpecie().equals(getString(R.string.dog))){
            callServicelistDiets(BREED_DOG);
            callServicelistFoodBrands(BREED_DOG);
        }else{
            callServicelistDiets(BREED_CAT);
            callServicelistFoodBrands(BREED_CAT);
        }
    }
    //2 gato, 1 perro
    public void callServicelistDiets(final Integer specie) {
        RequestParams params = new RequestParams();
        params.put("specieId",specie);
        AsyncHttpClient client = new AsyncHttpClient();
        //String url = "https://mocatto.herokuapp.com/rest/en/util/getDietList/"+specie;
        String url = getString(R.string.service_list_diets)+specie;

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
                        dietaPerro = new ArrayList();
                        for (int i = 0; i < listData.length; i++) {
                            //System.out.println("datos: "+listData[i].toString());
                            //System.out.println("id: " + listData[i].getId() + "   name: " + listData[i].getName());
                            dietaPerro.add(listData[i].getName());
                        }
                    }else if (specie==BREED_CAT) {
                        dietaGato = new ArrayList();
                        for (int i = 0; i < listData.length; i++) {
                            //System.out.println("datos: "+listData[i].toString());
                            //System.out.println("id: " + listData[i].getId() + "   name: " + listData[i].getName());
                            dietaGato.add(listData[i].getName());
                        }
                    }
                    loadSpinnerDietKind();
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

    //2 gato, 1 perro
    public void callServicelistFoodBrands(final Integer specie) {
        RequestParams params = new RequestParams();
        params.put("specieId",specie);
        AsyncHttpClient client = new AsyncHttpClient();
        //String url = "https://mocatto.herokuapp.com/rest/en/util/getFoodBrandList/"+specie;
        String url = getString(R.string.service_list_food_brands)+specie;

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
                Data data;
                try {
                    JSONArray json = timeline.getJSONArray("returnDTO");
                    Data[] listData = new Gson().fromJson(json.toString(), Data[].class);
                    if (specie==BREED_DOG) {
                        marcasPerro = new ArrayList();
                        for (int i = 0; i < listData.length; i++) {
                            //System.out.println("datos: "+listData[i].toString());
                            //System.out.println("id: " + listData[i].getId() + "   name: " + listData[i].getName());
                            marcasPerro.add(listData[i].getName());
                        }
                    }else if (specie==BREED_CAT) {
                        marcasGato = new ArrayList();
                        for (int i = 0; i < listData.length; i++) {
                            //System.out.println("datos: "+listData[i].toString());
                            //System.out.println("id: " + listData[i].getId() + "   name: " + listData[i].getName());
                            marcasGato.add(listData[i].getName());
                        }
                    }
                    loadSpinnerFoodBrand();
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

    private void startButtonStart() {
        //Obtener valores de los campos de la sección alimentación
        final TextView btnRegister2 = (TextView) view.findViewById(R.id.nextRegister2);
        btnRegister2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataPet();
                if (isOKVerifications){
                    Bundle args = new Bundle();
                    args.putString(ARG_PARAM1, email);
                    args.putSerializable(ARG_PARAM2, mascota);
                    args.putSerializable("cuenta",cuenta);
                    args.putBoolean("for_update",  for_update);
                    PetSPAFragment fragment = PetSPAFragment.newInstance(args);
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
            txtCualDieta.setText(mascota.getDietName());
            if (mascota.getFoodGrams()!=null) {
                txtCantidad_gr.setText(String.valueOf(mascota.getFoodGrams()));
            }
            spMarcaComida.setText(mascota.getFoodBrand());
            txtCualMarca.setText(mascota.getFoodBrandName());
            if (mascota.getFoodBuyRegularity()!=null){
                txtCadaCuantoCompraComida.setText(String.valueOf(mascota.getFoodBuyRegularity()));
            }
            if (mascota.getLastFoodBuyDate()!=null){
                txtFechaUltimaCompraComida.setText(simpleDateFormat.format(mascota.getLastFoodBuyDate()));
            }
        }
    }

    @NonNull
    private void getDataPet() {
        mascota.setDiet(spTipoDieta.getSelectedItem().toString());
        mascota.setDietName(txtCualDieta.getText().toString());
        //if (txtCantidad_gr.getText()!=null && txtCantidad_gr.getText().equals("")) {
            mascota.setFoodGrams(Integer.parseInt(txtCantidad_gr.getText().toString()));
        /*}else{
            mascota.setFoodGrams(0);
        }*/
        mascota.setFoodBrand(spMarcaComida.getText().toString());
        mascota.setFoodBrandName(txtCualMarca.getText().toString());
        //if (txtCadaCuantoCompraComida.getText()!=null && txtCadaCuantoCompraComida.getText().equals("")) {
            mascota.setFoodBuyRegularity(Integer.parseInt(txtCadaCuantoCompraComida.getText().toString()));
        /*}else {
            mascota.setFoodBuyRegularity(0);
        }*/
        mascota.setLastFoodBuyDate(util.verificarDateNull(txtFechaUltimaCompraComida));
        mascota.setPhoto(photoPet);
        isOKVerifications=true;
        if (Util.isDateInTheFuture(mascota.getLastFoodBuyDate())){
            errorMessage = getString(R.string.mje_date_is_the_future);
            isOKVerifications=false;
        }
    }

    private void hideSoftInput(View view) {
        view.clearFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void enableOrDisableAdmobBanner(View view, boolean b) {
        if(!b){
            hideSoftInput(view);
        }
    }

    private void startFields(){
        txtFechaUltimaCompraComida = (EditText)view.findViewById(R.id.txtFechaUltCompraComida);
        calendarViewUltCompraComida = (CustomCalendarView)view.findViewById(R.id.calendar_view_ult_compra_comida);
        spTipoDieta = (Spinner)view.findViewById(R.id.spTipoDieta);

        txtCualDieta = (EditText) view.findViewById(R.id.txtCualDieta);
        txtCualDieta.setImeOptions(EditorInfo.IME_ACTION_DONE);

        txtCantidad_gr = (EditText)view.findViewById(R.id.txtGramos);
        txtCantidad_gr.setImeOptions(EditorInfo.IME_ACTION_DONE);
        spMarcaComida= (AutoCompleteTextView) view.findViewById(R.id.spMarcaComida);
        spMarcaComida.setImeOptions(EditorInfo.IME_ACTION_DONE);
        txtCualMarca = (EditText) view.findViewById(R.id.txtCualMarca);
        txtCualMarca.setImeOptions(EditorInfo.IME_ACTION_DONE);
        txtCadaCuantoCompraComida = (EditText)view.findViewById(R.id.txtDuracionDias);
        txtCadaCuantoCompraComida.setImeOptions(EditorInfo.IME_ACTION_DONE);

    }

    private void hideKeyboard(final EditText editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(editText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS );
                }
            }
        });
    }

    private void startDatesAndCalendars() {
        txtFechaUltimaCompraComida.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //util.iniciarCalendario(txtFechaUltimaCompraComida,calendarViewUltCompraComida);
                //calendarViewUltCompraComida.setVisibility(View.VISIBLE);
                Util.showCalendar(txtFechaUltimaCompraComida,getActivity());
            }
        });

    }

    private void loadSpinners(){
        loadSpinnerDietKind();
        loadSpinnerFoodBrand();
    }

    private void loadSpinnerFoodBrand() {
        spMarcaComida = (AutoCompleteTextView) view.findViewById(R.id.spMarcaComida);
        /*ArrayAdapter adaptadorMarcaComida = ArrayAdapter.createFromResource(
                getActivity(), R.array.opcMarcaComida, android.R.layout.simple_dropdown_item_1line);*/
        ArrayAdapter adaptadorMarcaComida=null;
        if(mascota.getSpecie().equals(getString(R.string.dog))){
            adaptadorMarcaComida = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, marcasPerro);
        }else {
            adaptadorMarcaComida = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, marcasGato);
        }

        adaptadorMarcaComida.setDropDownViewResource(R.layout.spinner_item);
        spMarcaComida.setAdapter(adaptadorMarcaComida);


        spMarcaComida.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                txtCualMarca = (EditText) view.findViewById(R.id.txtCualMarca);
                lbCualMarca = (TextView) view.findViewById(R.id.lbCualMarca);
                txtCualMarca.setVisibility(View.GONE);
                lbCualMarca.setVisibility(View.GONE);
                if (s.toString().equals(getString(R.string.otra_food)) || s.toString().equals("Otra") || s.toString().equals("Other") ) {
                    txtCualMarca.setVisibility(View.VISIBLE);
                    lbCualMarca.setVisibility(View.VISIBLE);
                } else {
                    txtCualMarca.setVisibility(View.GONE);
                    lbCualMarca.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadSpinnerDietKind() {
        spTipoDieta = (Spinner) view.findViewById(R.id.spTipoDieta);
        /*ArrayAdapter adaptadorTipoDieta = ArrayAdapter.createFromResource(
                getActivity(), R.array.opcTipoDieta, android.R.layout.simple_spinner_item);*/

        ArrayAdapter adaptadorTipoDieta=null;
        if(mascota.getSpecie().equals(getString(R.string.dog))){
            adaptadorTipoDieta = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, dietaPerro);
        }else {
            adaptadorTipoDieta = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, dietaGato);
        }
        adaptadorTipoDieta.setDropDownViewResource(R.layout.spinner_item);
        spTipoDieta.setAdapter(adaptadorTipoDieta);
        spTipoDieta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                String item = arg0.getSelectedItem().toString();
                txtCualDieta = (EditText) view.findViewById(R.id.txtCualDieta);
                lbCualDieta = (TextView) view.findViewById(R.id.lbCualDieta);
                if (item.equals(getString(R.string.otra_food))) {
                    txtCualDieta.setVisibility(View.VISIBLE);
                    lbCualDieta.setVisibility(View.VISIBLE);
                } else {
                    txtCualDieta.setVisibility(View.GONE);
                    lbCualDieta.setVisibility(View.GONE);
                }
                ((TextView) arg0.getChildAt(0)).setTextColor(Color.GRAY);
                ((TextView) arg0.getChildAt(0)).setTextSize(18);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // Hacer algo
            }
        });
        int pos = Util.setValueInSpinner(mascota.getFoodBrand(),adaptadorTipoDieta);
        spTipoDieta.setSelection(pos);
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
