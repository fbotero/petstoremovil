package com.mocatto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.dto.City;
import com.mocatto.dto.Country;
import com.mocatto.dto.Cuenta;
import com.mocatto.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CreateAccountActivity extends AppCompatActivity {

    String email;
    EditText txtMail;
    EditText txtNombre;
    EditText txtContrasenia;
    EditText txtRepetirContrasenia;
    Cuenta cuenta;
    Util util;
    // Progress Dialog Object
    ProgressDialog prgDialog;
    private Spinner spCiudad,spPais;
    List listCountries;
    List listCities;
    Country[] listCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setTitle(getString(R.string.crear_cuenta));

        obtenerValoresFormulario();
        //loadSpinners();
        callServicelistCountries();
        util = new Util();

        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage(getString(R.string.request_in_progress));
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        prgDialog.setProgressStyle(R.style.progress_bar_style);

        txtMail.setText(email);
        final Button botonCrearCuenta = (Button) findViewById(R.id.btnCrearCuenta);
        botonCrearCuenta.setOnClickListener(new Button.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //Verificar contraseña
                                                    if (!verificarContrasenia()) {
                                                        Toast toast =
                                                                Toast.makeText(getApplicationContext(), "Las contraseñas ingresadas no coinciden", Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER | Gravity.LEFT, 0, 0);
                                                        toast.show();
                                                    } else {
                                                        verificarCamposObligatorios();
                                                    }

                                                }
                                            }
        );
    }

    private void hideKeyboard(final EditText editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(editText.getApplicationWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY );
                }
            }
        });
    }


    public void callServicelistCountries() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://mocatto.herokuapp.com/rest/en/util/getCountryList/";
        //String url = "http://192.168.19.246:8080/Mocatto/rest/en/util/getCountryList/";


        client.get(url, new JsonHttpResponseHandler() {
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
                    listCountry = new Gson().fromJson(json.toString(), Country[].class);

                    listCountries = new ArrayList();
                    for (int i = 0; i < listCountry.length; i++) {
                        listCountries.add(listCountry[i].getName());
                    }
                    loadSpinnerPais();
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

    public void callServicelistCities(final Integer country) {
        RequestParams params = new RequestParams();
        params.put("id",country);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://mocatto.herokuapp.com/rest/en/util/getCityList/"+country;
        //String url = "http://192.168.19.246:8080/Mocatto/rest/en/util/getCityList/"+country;
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
                    Log.println(Log.INFO,"CITY",timeline.toString());
                    JSONArray json = timeline.getJSONArray("returnDTO");
                    City[] listData = new Gson().fromJson(json.toString(), City[].class);
                    listCities = new ArrayList();
                    for (int i = 0; i < listData.length; i++) {
                        listCities.add(listData[i].getName());
                    }

                    loadSpinnerCiudad();
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

    private void loadSpinners(){
        //loadSpinnerPais();
        //loadSpinnerCiudad();
    }

    private void loadSpinnerCiudad() {
        /*ArrayAdapter adaptadorCiudad = ArrayAdapter.createFromResource(
                this, R.array.opcCiudad, android.R.layout.simple_spinner_item);*/
        ArrayAdapter adaptadorCiudad = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listCities);

        adaptadorCiudad.setDropDownViewResource(R.layout.spinner_item);
        spCiudad.setAdapter(adaptadorCiudad);
    }

    private void loadSpinnerPais() {
        /*ArrayAdapter adaptadorPais = ArrayAdapter.createFromResource(
                this, R.array.opcPais, android.R.layout.simple_spinner_item);*/

        ArrayAdapter adaptadorPais = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listCountries);

        adaptadorPais.setDropDownViewResource(R.layout.spinner_item);
        spPais.setAdapter(adaptadorPais);

        spPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                String paisSeleccionado= arg0.getSelectedItem().toString();
                Integer item=0;
                for (int i=0;i<listCountry.length;i++){
                    if (listCountry[i].getName().equals(paisSeleccionado)){
                        item = listCountry[i].getId();
                        break;
                    }
                }
                callServicelistCities(item);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // Hacer algo
            }
        });
    }

    private boolean verificarContrasenia() {
        return txtContrasenia.getText().toString().equals(txtRepetirContrasenia.getText().toString());
    }

    private void verificarCamposObligatorios() {
        //Verificar obligatoriedad de campos
        if (!util.isEmpty(txtMail) && !util.isEmpty(txtNombre)) {
            //Obtener valores de la cuenta
            cuenta = new Cuenta();

            cuenta.setEmail(txtMail.getText().toString());
            cuenta.setContrasenia(txtContrasenia.getText().toString());
            cuenta.setRepetirContrasenia(txtRepetirContrasenia.getText().toString());
            cuenta.setNombre(txtNombre.getText().toString());
            cuenta.setPais(spPais.getSelectedItem().toString());
            cuenta.setCiudad(spCiudad.getSelectedItem().toString());
            llamarServicioCrearCuenta();
        } else {
            Toast toast =
                    Toast.makeText(getApplicationContext(), "Uno o varios campos no fueron diligenciados", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER | Gravity.LEFT, 0, 0);
            toast.show();
        }
    }

    private void llamarServicioCrearCuenta() {
        //Llamar al servicio Rest que crea la cuenta
        RequestParams params = new RequestParams();
        params.put("email",cuenta.getEmail());
        params.put("name",cuenta.getNombre());
        params.put("password",cuenta.getContrasenia());
        params.put("telephone","123456");
        //params.put("country",cuenta.getPais());
        //params.put("city",cuenta.getCiudad());
        params.setUseJsonStreamer(true);
        invokeWS(params);

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
        client.post("https://mocatto.herokuapp.com/rest/en/user/saveUser",params,new JsonHttpResponseHandler() {
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
                    // Hide Progress Dialog
                    prgDialog.hide();
                    Log.println(Log.INFO,"REST","dto: "+timeline.toString());
                    //int id =timeline.getInt("id");
                    Cuenta account = new Gson().fromJson(timeline.toString(), Cuenta.class);
                    Toast.makeText(getApplicationContext(), getString(R.string.mje_sucessful_create_account), Toast.LENGTH_LONG).show();
                    Intent intentCrearCuenta = new Intent(CreateAccountActivity.this, NavigationDrawerActivity.class);
                    intentCrearCuenta.putExtra("email", txtMail.getText().toString());
                    intentCrearCuenta.putExtra("isFromCreateAccount", "true");
                    CreateAccountActivity.this.startActivity(intentCrearCuenta);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getString(R.string.mje_failure_create_account), Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found 1", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end 1", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "1 Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
            /*
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }
*/
        });



        /*client.post("https://mocatto.herokuapp.com/rest/en/user/saverUser",params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
                prgDialog.hide();
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(responseBody.toString());
                    // When the JSON response has status boolean value assigned with true
                    if(obj.getBoolean("status")){
                        // Set Default Values for Edit View controls
                        //setDefaultValues();
                        // Display successfully registered message using Toast
                        Toast.makeText(getApplicationContext(), R.string.mje_sucessful_create_account, Toast.LENGTH_LONG).show();
                    }
                    // Else display error message
                    else{
                        Toast.makeText(getApplicationContext(), R.string.mje_failure_create_account+"  /// "+obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), R.string.mje_failure_create_account, Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }

        });*/
    }

    private void obtenerValoresFormulario() {
        email = (String) getIntent().getExtras().getSerializable("email");
        txtMail = (EditText) findViewById(R.id.txtMail);
        hideKeyboard(txtMail);
        spPais = (Spinner) findViewById(R.id.spPais);
        spCiudad = (Spinner) findViewById(R.id.spCiudad);
        txtNombre = (EditText) findViewById(R.id.txtNombre);
        hideKeyboard(txtNombre);
        txtContrasenia = (EditText) findViewById(R.id.txtContrasenia);
        hideKeyboard(txtContrasenia);
        txtRepetirContrasenia = (EditText) findViewById(R.id.txtRepetirContrasenia);
        hideKeyboard(txtRepetirContrasenia);
    }
}
