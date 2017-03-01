package com.mocatto;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.mocatto.dto.Apointment;
import com.mocatto.dto.Cuenta;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreenActivity extends AppCompatActivity {

    private Cuenta cuenta;
    private Apointment evento;

    CallbackManager callbackManager;

    private void crearAccesoDirectoEnEscritorio(String nombre) {
        Intent shortcutIntent = new Intent();
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, getIntentShortcut());
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, nombre);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this.getApplicationContext(), R.drawable.ic_logo_mocatto));
        shortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        this.sendBroadcast(shortcutIntent);
    }

    public Intent getIntentShortcut(){
        Intent i = new Intent();
        i.setClassName(this.getPackageName(), this.getPackageName() + "." + this.getLocalClassName());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setTitle(getString(R.string.app_name));
        //crearAccesoDirectoEnEscritorio(getString(R.string.app_name));


        FacebookSdk.sdkInitialize(getApplicationContext());

        Thread timerTread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    //Facebook
                    //Cuando existe session abierta
                    Profile profile = Profile.getCurrentProfile();
                    if (profile != null) {
                        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                if (object!=null) {
                                    Log.println(Log.DEBUG, "facebook", object.toString());
                                    try {
                                        cuenta = new Cuenta();
                                        cuenta.setEmail(object.getString("email"));
                                        cuenta.setNombre(object.getString("name"));
                                        cuenta.setFechaNacimiento(object.getString("birthday"));
                                        cuenta.setGenero(object.getString("gender"));
                                        cuenta.setLocale(object.getString("locale"));
                                        cuenta.setUbicacion(object.getString("location"));
                                        cuenta.setUrl(object.getString("link"));
                                        //Toast.makeText(LoginActivity.this, cuenta.toString(), Toast.LENGTH_SHORT).show();
                                        Log.println(Log.INFO, "FACEBOOK", "DATOS DE FACE: " + cuenta.toString());

                                        Intent itemintent = new Intent(SplashScreenActivity.this, NavigationDrawerActivity.class);
                                        itemintent.putExtra("email", cuenta.getEmail());
                                        itemintent.putExtra("cuenta", cuenta);
                                        itemintent.putExtra("evento", (Parcelable) evento);
                                        itemintent.putExtra("isFromCreateAccount", "false");
                                        SplashScreenActivity.this.startActivity(itemintent);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday,locale,location,link");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }else{
                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };
        timerTread.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
