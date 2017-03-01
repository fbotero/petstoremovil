package com.mocatto.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.dto.Data;
import com.mocatto.dto.Pet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by froilan.ruiz on 7/6/2016.
 */
public class PetsRest {
    private List listPetsByEmail = null;
    private Pet pet;

    public List callServicelistPetsByEmail(String email) {
        RequestParams params = new RequestParams();
        params.put("email",email);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://mocatto.herokuapp.com/rest/en/pet/getPetsByEmail/"+email;

        Log.println(Log.INFO,"REST","esta pasasndo antes de llamar al servicio");
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
                    System.out.println("valor json: "+timeline.getJSONArray("returnDTO")+"    "+timeline.getJSONObject("returnDTO"));
                    JSONObject json = timeline.getJSONObject("returnDTO");
                    Data data = new Gson().fromJson(json.toString(), Data.class);
                    System.out.println("datos: "+data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }
            /*
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }
*/
        });
        return listPetsByEmail;
    }

    public List callServicegetPet(String id) {
        RequestParams params = new RequestParams();
        params.put("email",id);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://mocatto.herokuapp.com/rest/en/pet/getPet/"+id;

        Log.println(Log.INFO,"REST","esta pasasndo antes de llamar al servicio");
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
                    System.out.println("valor json: "+timeline.getJSONArray("returnDTO")+"    "+timeline.getJSONObject("returnDTO"));
                    JSONObject json = timeline.getJSONObject("returnDTO");
                    Data data = new Gson().fromJson(json.toString(), Data.class);
                    System.out.println("datos: "+data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }
            /*
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }
*/
        });
        return listPetsByEmail;
    }
}
