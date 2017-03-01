package com.mocatto.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.R;
import com.mocatto.adapters.TopicArrayAdapter;
import com.mocatto.dto.Topic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommunityCatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommunityCatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityCatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "especie";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String email;
    private String especie;

    private OnFragmentInteractionListener mListener;

    private View view;
    private ListView listView;
    private ArrayAdapter adaptador;

    private JSONArray topicArray = new JSONArray();

    ArrayList topicList = new ArrayList();

    public CommunityCatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunityCatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityCatFragment newInstance(String param1, String param2) {
        CommunityCatFragment fragment = new CommunityCatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static CommunityCatFragment newInstance(Bundle args) {
        CommunityCatFragment fragment = new CommunityCatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_community_cat, container, false);
        if (getArguments() != null) {
            email = getArguments().getString("email");
        }
        final ImageView botonAgregarTema = (ImageView) view.findViewById(R.id.fabAgregarTema);
        botonAgregarTema.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString(ARG_PARAM1, email);
                args.putString(ARG_PARAM2, "CAT");
                AddTopicCommunityFragment fragment = AddTopicCommunityFragment.newInstance(args);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.main_fragment, fragment);
                ft.commit();

            }
        });

        invokeWS(new RequestParams());

        return view;
    }

    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        String URL = "http://mocatto.herokuapp.com/rest/en/community/getTopicsBySpecie/2";
        //String URL = "http://10.0.2.2:8080/Mocatto/rest/en/community/getTopicsBySpecie/2";

        client.get(URL, params, new JsonHttpResponseHandler() {
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
                    topicArray = timeline.getJSONArray("topicDTO");
                    System.out.println("Locations: " + topicArray.length());

                    LatLng marker;

                    topicList = new ArrayList();
                    for (int i = 0; i < topicArray.length();  i++) {
                        String _topic = topicArray.getJSONObject(i).toString();

                        Topic dto = new Gson().fromJson(_topic, Topic.class);
                        topicList.add(dto);
                    }

                    viewTopics();

                } catch (JSONException e) {
                    try {
                        String _topic = timeline.getJSONObject("topicDTO").toString();
                        Topic dto = new Gson().fromJson(_topic, Topic.class);
                        topicList = new ArrayList();
                        topicList.add(dto);
                        viewTopics();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }

        });
    }

    private void viewTopics() {
        listView = (ListView) view.findViewById(R.id.cattopicList);

        if (topicList!=null){
            Log.println(Log.DEBUG,"LISTADO","lista de temas: " + topicList.size());
            //Inicializar el adaptador con la fuente de datos
            adaptador = new TopicArrayAdapter(getActivity(), topicList);
            //Relacionando la lista con el adaptador
            listView.setAdapter(adaptador);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
