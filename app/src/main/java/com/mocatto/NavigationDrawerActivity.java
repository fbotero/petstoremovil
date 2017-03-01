package com.mocatto;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.db.ApointmentDB;
import com.mocatto.db.PetDB;
import com.mocatto.dto.Apointment;
import com.mocatto.dto.Cuenta;
import com.mocatto.dto.Pet;
import com.mocatto.fragments.AlarmViewFragment;
import com.mocatto.fragments.ApointmentListFragment;
import com.mocatto.fragments.InfoAppFragment;
import com.mocatto.fragments.MenuFragment;
import com.mocatto.fragments.MocattoValuesFragment;
import com.mocatto.fragments.PetProfileFragment;
import com.mocatto.fragments.PoliciesFragment;
import com.mocatto.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

import cz.msebera.android.httpclient.Header;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String email;
    private TextView tvEmail;
    private String isFromCreateAccount;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Bundle arguments;
    private Pet mascota;
    private ArrayList<Pet> listaMascotas;
    private Menu copiaMenu,copiaMenuSolo;

    private Cuenta cuenta;
    private Apointment evento;
    Hashtable hashTablePets;
    ArrayList apointmentsList;
    private boolean flag_menu=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cuenta = (Cuenta) getIntent().getExtras().getSerializable("cuenta");
        evento = (Apointment) getIntent().getExtras().getSerializable("evento");

        if (cuenta!=null){
            email = cuenta.getEmail();
        }else {
            email = (String) getIntent().getExtras().getSerializable("email");
        }

        isFromCreateAccount = (String) getIntent().getExtras().getSerializable("isFromCreateAccount");

        tvEmail = (TextView) findViewById(R.id.tvEmail);

        //Se debe ir a consultar las mascotas creadas para la cuenta de mail logueada
        hashTablePets = new Hashtable();
        callServiceGetPetsByEmail();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Abrir fragments.
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        //Argumentos
        arguments = new Bundle();
        arguments.putString("email", email);
        arguments.putSerializable("cuenta", cuenta);
        arguments.putParcelableArrayList("listaMascotas",listaMascotas);

        if (isFromCreateAccount!=null) {
            if (isFromCreateAccount.equals("true")) {
                PetProfileFragment petProfileFragment = PetProfileFragment.newInstance(arguments);
                fragmentTransaction.replace(R.id.main_fragment, petProfileFragment);
                setTitle(getString(R.string.perfil_pet));
            }else{
                MenuFragment menuFragment = MenuFragment.newInstance(arguments);
                fragmentTransaction.replace(R.id.main_fragment, menuFragment);
                setTitle(getString(R.string.menu));
            }
        } else {//se debe identificar la mascota seleccionada en el menú de 3 puntos y se debe cargar el perfil de dicha mascota
            if (evento!=null){//Se llega a este activity desde el lanzamiento de un evento.
                arguments.putSerializable("evento", evento);
                AlarmViewFragment alarmViewFragment = AlarmViewFragment.newInstance(arguments);
                fragmentTransaction.replace(R.id.main_fragment, alarmViewFragment);
                setTitle(getString(R.string.menu));
            }else{
                MenuFragment menuFragment = MenuFragment.newInstance(arguments);
                fragmentTransaction.replace(R.id.main_fragment, menuFragment);
                setTitle(getString(R.string.menu));
            }

        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void callServiceGetPetsByEmail() {
        RequestParams params = new RequestParams();
        params.put("email",email);
        AsyncHttpClient client = new AsyncHttpClient();
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
                    if (timeline != null) {
                        Log.println(Log.DEBUG, "Json", timeline.toString());
                        Pet[] listData=null;
                        try {
                            JSONArray json = timeline.getJSONArray("petDTO");
                            listData = new Gson().fromJson(json.toString(), Pet[].class);

                        }catch (org.json.JSONException ex){
                            JSONObject json = timeline.getJSONObject("petDTO");
                            Pet petCita = new Gson().fromJson(json.toString(), Pet.class);
                            listData = new  Pet[1];
                            listData[0]=petCita;
                        }
                        listaMascotas = new ArrayList<Pet>();

                        for (int i = 0; i < listData.length; i++) {
                            Pet pet = listData[i];
                            if (pet.getWoopingCough()!=null) {
                                pet.setWoopingCough(Util.changeStringValueYesOrTrue(pet.getWoopingCough(), getApplicationContext()));
                            }
                            if (pet.getSterilized()!=null) {
                                pet.setSterilized(Util.changeStringValueYesOrTrue(pet.getSterilized(), getApplicationContext()));
                            }
                            if (pet.getAidsLeukemiaVaccine()!=null) {
                                pet.setAidsLeukemiaVaccine(Util.changeStringValueYesOrTrue(pet.getAidsLeukemiaVaccine(), getApplicationContext()));
                            }
                            if (pet.getSpecie().equals(getString(R.string.cat)) || pet.getSpecie().equals(getString(R.string.feline))){
                                pet.setSpecie(getString(R.string.cat));
                            }else if (pet.getSpecie().equals(getString(R.string.dog)) || pet.getSpecie().equals(getString(R.string.canine))){
                                pet.setSpecie(getString(R.string.dog));
                            }

                            listaMascotas.add(pet);
                            hashTablePets.put(listData[i].getName(), listData[i].getSpecie());
                        }
                        arguments.putParcelableArrayList("listaMascotas", listaMascotas);

                        for (int i = 0; i < listaMascotas.size(); i++) {
                            Pet pet = listaMascotas.get(i);
                            copiaMenu.add(i, i, i, pet.getName());
                        }

                        flag_menu=true;
                        //Recarga menú de los 3 puntos
                        reloadMenu();
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

    private void reloadMenu(){
        invalidateOptionsMenu();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // En este punto se pueden agregar mascotas al menu, acá se deben consultar la lista de mascotas
        // y mostrar los nombre de cada una de ellas, tambien es el punto para cambiar la imagen
        copiaMenu = menu;
        copiaMenuSolo = menu;

        if (listaMascotas!=null) {
            for (int i = 0; i < listaMascotas.size(); i++) {
                Pet pet = listaMascotas.get(i);
                menu.add(i, i, i, pet.getName());
            }
        }

        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        setRedPointInBell();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_add_pet) {
            fragmentTransaction = fragmentManager.beginTransaction();
            arguments = new Bundle();
            arguments.putString("email", email);
            arguments.putSerializable("mascota", null);
            arguments.putSerializable("cuenta", cuenta);
            arguments.putSerializable("for_update", false);
            PetProfileFragment menuFragment = PetProfileFragment.newInstance(arguments);
            fragmentTransaction.replace(R.id.main_fragment, menuFragment);
            setTitle(getString(R.string.perfil_pet));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if(id==R.id.menu_image_campanita){
            Log.println(Log.INFO,"CAMPANITA","Mostrara el listado de notificaciones");
            if (apointmentsList!=null){
                //Redirecciona a la lista de eventos
                fragmentTransaction = fragmentManager.beginTransaction();
                arguments = new Bundle();
                arguments.putString("email", email);
                arguments.putSerializable("mascota", mascota);
                arguments.putSerializable("cuenta", cuenta);
                arguments.putParcelableArrayList("listaMascotas",listaMascotas);
                arguments.putParcelableArrayList("listaEventos", apointmentsList);
                ApointmentListFragment apointmentListFragment = ApointmentListFragment.newInstance(arguments);
                fragmentTransaction.replace(R.id.main_fragment, apointmentListFragment);
                setTitle(getString(R.string.reminder));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }else{
                Toast.makeText(getApplicationContext(), "No tiene notificaciones pendientes", Toast.LENGTH_LONG).show();
            }

        }else  {
            //Aqui se debe verificar si es gato o perro, si es gato muestra icono gato y si es perro
            //muestra icono de perro. Adicional debe setear una mascota, es decir debe crear el objeto Pet
            mascota = listaMascotas.get(id);
            mascota.setPhoto(PetDB.getPetPhoto(this,mascota.getId()));

            fragmentTransaction = fragmentManager.beginTransaction();
            arguments = new Bundle();
            arguments.putString("email", email);
            arguments.putSerializable("mascota", mascota);
            arguments.putSerializable("cuenta", cuenta);
            arguments.putSerializable("for_update", true);
            PetProfileFragment menuFragment = PetProfileFragment.newInstance(arguments);
            fragmentTransaction.replace(R.id.main_fragment,menuFragment);
            setTitle(getString(R.string.perfil_pet));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        arguments = new Bundle();
        arguments.putString("email", email);
        arguments.putSerializable("mascota", mascota);
        arguments.putSerializable("cuenta", cuenta);
        if (id == R.id.menu_menu) {
            setTitle(getString(R.string.menu));

            Intent refresh = new Intent(this, NavigationDrawerActivity.class);
            refresh.putExtra("email",  email);
            refresh.putExtra("cuenta",  cuenta);
            refresh.putExtra("isFromCreateAccount","false");
            startActivity(refresh);
            this.finish();

        } else if (id == R.id.menu_valores_mocatto) {
            setTitle(getString(R.string.valores_mocatto));
            fragmentTransaction = fragmentManager.beginTransaction();
            MocattoValuesFragment menuFragment = MocattoValuesFragment.newInstance(arguments);
            fragmentTransaction.replace(R.id.main_fragment, menuFragment);
            setTitle(getString(R.string.menu));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (id == R.id.menu_politicas) {
            setTitle(getString(R.string.condiciones_y_pol_ticas));
            fragmentTransaction = fragmentManager.beginTransaction();
            PoliciesFragment menuFragment = PoliciesFragment.newInstance(arguments);
            fragmentTransaction.replace(R.id.main_fragment, menuFragment);
            setTitle(getString(R.string.menu));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (id == R.id.menu_informacion) {
            setTitle(getString(R.string.informaci_n_de_la_aplicaci_n));
            fragmentTransaction = fragmentManager.beginTransaction();
            InfoAppFragment menuFragment = InfoAppFragment.newInstance(arguments);
            fragmentTransaction.replace(R.id.main_fragment, menuFragment);
            setTitle(getString(R.string.menu));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else if (id == R.id.menu_cerrar_sesion) {
            setTitle(getString(R.string.cerrar_sesi_n));
            logout();
            Intent itemintent = new Intent(NavigationDrawerActivity.this, LoginActivity.class);
            NavigationDrawerActivity.this.startActivity(itemintent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        //cerrando session facebook
        LoginManager.getInstance().logOut();
        //cerrando aplicacion
        Runtime garbage = Runtime.getRuntime();
        garbage.gc();
    }


    /**
     * Si tiene eventos lanzados cambia la imagen de la campanita
     */
    private void setRedPointInBell() {
        apointmentsList = (ArrayList<Apointment>) ApointmentDB.getApointmentsForListInActionBar(email,this,hashTablePets);
        if (apointmentsList!=null){
            if (apointmentsList.size()>0){
                MenuItem mMenuItem;
                mMenuItem = (MenuItem) copiaMenu.findItem(R.id.menu_image_campanita);
                mMenuItem.setIcon(getDrawable(R.drawable.ic_campanita_puntico));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (flag_menu){
            getMenuInflater().inflate(R.menu.navigation_drawer, copiaMenu);
        }else{
            getMenuInflater().inflate(R.menu.navigation_drawer, copiaMenuSolo);
        }
    }
}
