package com.mocatto;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.dto.Apointment;
import com.mocatto.dto.Cuenta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * A login screen that offers login via email/password.
 * Comentarios iniciales para revisar si desde android estudio esta realizando el push
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Cuenta cuenta;
    private Apointment evento;

    CallbackManager callbackManager;
    private AccessToken accessToken;
    private SimpleDateFormat simpleDateFormat;

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

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        //populateAutoComplete();
        mEmailView.setImeOptions(EditorInfo.IME_ACTION_DONE);



        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cuenta = new Cuenta();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        hideKeyboard(mPasswordView);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        final TextView botonRegister = (TextView) findViewById(R.id.lbCrearCuenta);
        botonRegister.setOnClickListener(new Button.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Intent intentCrearCuenta = new Intent(LoginActivity.this, CreateAccountActivity.class);
                                                 intentCrearCuenta.putExtra("email", mEmailView.getText().toString());
                                                 LoginActivity.this.startActivity(intentCrearCuenta);
                                             }
                                         }
        );
        botonRegister.requestFocus();


        //Facebook
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) mLoginFormView.findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends","user_location"));

        //Cuando existe session abierta
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        cuenta.setEmail(object.getString("email"));
                        cuenta.setNombre(object.getString("name"));
                        cuenta.setFechaNacimiento(object.getString("birthday"));
                        cuenta.setGenero(object.getString("gender"));
                        cuenta.setLocale(object.getString("locale"));
                        cuenta.setUbicacion(object.getString("location"));
                        cuenta.setUrl(object.getString("link"));
                        //mEmailView.setText(cuenta.getEmail());
                        //Toast.makeText(LoginActivity.this, cuenta.toString(), Toast.LENGTH_SHORT).show();
                        Log.println(Log.INFO,"FACEBOOK","DATOS DE FACE: "+cuenta.toString());

                        Intent itemintent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                        itemintent.putExtra("email",  cuenta.getEmail());
                        itemintent.putExtra("cuenta",  cuenta);
                        itemintent.putExtra("evento", (Parcelable) evento);
                        itemintent.putExtra("isFromCreateAccount", "false");
                        LoginActivity.this.startActivity(itemintent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday,locale,location,link");
            request.setParameters(parameters);
            request.executeAsync();
        }


        //Cuando no se tiene sessión abierta
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.println(Log.INFO,"FACEBOOK" ,loginResult.getAccessToken().getUserId());
                System.out.println("Login correcto con facebook-- Token: "+ loginResult.getAccessToken().getToken());

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                // Application code
                                try {
                                    cuenta.setEmail(object.getString("email"));
                                    cuenta.setNombre(object.getString("name"));
                                    cuenta.setFechaNacimiento(object.getString("birthday"));
                                    cuenta.setGenero(object.getString("gender"));
                                    mEmailView.setText(cuenta.getEmail());
                                    Log.println(Log.INFO,"FACEBOOK","DATOS DE FACE: "+cuenta.toString());

                                    Intent itemintent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                                    itemintent.putExtra("email",  mEmailView.getText().toString());
                                    itemintent.putExtra("cuenta",  cuenta);
                                    itemintent.putExtra("evento", (Parcelable) evento);//siempre desde acá va null
                                    itemintent.putExtra("isFromCreateAccount", "false");
                                    LoginActivity.this.startActivity(itemintent);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                );
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                mEmailView.setText("Acceso Incorrecto ....");
                System.out.println("Error: "+error.getMessage());
            }

        });

        //hideKeyboard();
    }

    private void printHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.activity", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);
                Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //populateAutoComplete();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            //focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            //focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            //focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            System.out.println("esta pasando por acá... no cumple con condiciones y sacara error");
            //focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            System.out.println("esta pasando por acá... cumplio con condiciones");
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // L77ogs 'install' and 'app activate' App Events.
            AppEventsLogger.activateApp(getApplicationContext());

            mAuthTask = null;
            showProgress(false);

            if (success) {
                Log.println(Log.INFO,"Datos SI llega ACA","Sale este mensaje si hacemos un registro correcto opcion propia de la app");
                callServiceVerifyUser();

                /*Intent itemintent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                itemintent.putExtra("email",  mEmailView.getText().toString());
                itemintent.putExtra("isFromCreateAccount", "false");
                LoginActivity.this.startActivity(itemintent);*/
                //finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        public void callServiceVerifyUser() {
            RequestParams params = new RequestParams();
            params.put("email",mEmailView.getText().toString());
            params.put("password",mPasswordView.getText().toString());
            params.setUseJsonStreamer(true);
            AsyncHttpClient client = new AsyncHttpClient();
            String url = getString(R.string.service_verify_user);
            //String url = "https://mocatto.herokuapp.com/rest/en/user/loginUser";
            //String url = "http://192.168.19.246:8080/Mocatto/en/user/loginUser";


            client.post(url,params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                    System.out.println(timeline);
                    Log.println(Log.INFO,"REST","onSuccess 1 "+timeline);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    System.out.println(responseString);
                    Log.println(Log.INFO,"REST","failure 2 "+responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                    try {
                        Log.println(Log.INFO,"REST","retorno dto:"+timeline.toString());
                        Integer id =timeline.getInt("hashKey");
                        Log.println(Log.INFO,"REST","Id hashKey: "+id);
                        Intent itemintent = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
                        itemintent.putExtra("email",  mEmailView.getText().toString());
                        itemintent.putExtra("evento", (Parcelable) evento);
                        itemintent.putExtra("isFromCreateAccount", "false");
                        itemintent.putExtra("hashKey", id);
                        LoginActivity.this.startActivity(itemintent);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), R.string.mje_user_no_ok, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    throwable.printStackTrace();
                    Log.println(Log.INFO,"REST","failure 3 "+responseString);
                }
            });
        }

        @Override
        protected void onCancelled() {
            // Logs 'app deactivate' App Event.
            AppEventsLogger.deactivateApp(getApplicationContext());

            mAuthTask = null;
            showProgress(false);
        }

    }
}

