package com.mocatto.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.R;
import com.mocatto.dto.Topic;
import com.mocatto.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddTopicCommunityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddTopicCommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTopicCommunityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "especie";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String email;
    private String specie;
    private String today;
    private EditText txtDescription,txtTitle;
    SimpleDateFormat curFormater;
    private Util util;
    private ImageView btnSaveTopic;
    private Topic topic;
    ProgressDialog prgDialog;
    View view;
    private AdView mAdView;


    public AddTopicCommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddTopicCommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTopicCommunityFragment newInstance(String param1, String param2) {
        AddTopicCommunityFragment fragment = new AddTopicCommunityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddTopicCommunityFragment newInstance(Bundle args) {
        AddTopicCommunityFragment fragment = new AddTopicCommunityFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_topic_community, container, false);

        getActivity().setTitle(getString(R.string.comunidad));

        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        startFormFields();
        startListenersForm();
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void startFormFields() {
        if (getArguments() != null) {
            email = getArguments().getString("email");
            specie = getArguments().getString("especie");
        }
        util = new Util();

        txtDescription = (EditText) view.findViewById(R.id.txtDescription);
        hideKeyboard(txtDescription,this.getActivity());
        txtTitle = (EditText) view.findViewById(R.id.txtTitle);
        hideKeyboard(txtTitle,this.getActivity());

        Calendar calendar = Calendar.getInstance();
        curFormater = new SimpleDateFormat("yyyy-MM-dd");
        today = curFormater.format(calendar.getTime());

        btnSaveTopic = (ImageView)view.findViewById(R.id.ivSaveTopic);

        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this.getActivity());
        // Set Progress Dialog Text
        prgDialog.setMessage(getString(R.string.request_in_progress));
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        prgDialog.setProgressStyle(R.style.progress_bar_style);
    }

    private void hideKeyboard(final EditText editText, final FragmentActivity activity){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager =(InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(editText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS );
                }
            }
        });
    }

    private void startListenersForm() {
        btnSaveTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean obligatoryIsGood = true;

                if (util.isEmpty(txtTitle) || util.isEmpty(txtDescription)){
                    obligatoryIsGood = false;
                }
                if (obligatoryIsGood) {
                    topic = new Topic(null,
                            txtTitle.getText().toString(),
                            txtDescription.getText().toString(),
                            email,
                            null,
                            0,
                            specie.equals("DOG") ? 1 : 2);
                    //Invocando el servicio rest
                    // Instantiate Http Request Param Object
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");


                    RequestParams params = new RequestParams();
                    params.put("id",topic.getId());
                    params.put("title",topic.getTitle());
                    params.put("description",topic.getDescription());
                    params.put("author",topic.getAuthor());

                    params.put("specieId", specie.equals("DOG") ? 1 : 2);
                    params.put("likes", 0);
                    params.setUseJsonStreamer(true);
                    invokeWS(params);

                } else {
                    Toast toast =
                            Toast.makeText(getContext(), R.string.mje_obligatory_form_topic, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.LEFT, 0, 0);
                    toast.show();
                }
            }
        });
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

        String URL = "http://mocatto.herokuapp.com/rest/en/community/saveTopic";
        //String URL = "http://10.0.2.2:8080/Mocatto/rest/en/community/saveTopic";

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
                prgDialog.hide();

                Toast.makeText(getContext(), R.string.mje_sucessful_topic, Toast.LENGTH_LONG).show();

                Bundle args = new Bundle();
                args.putString(ARG_PARAM1, email);
                if (specie.equals("DOG")) {
                    CommunityDogFragment fragment = CommunityDogFragment.newInstance(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.main_fragment, fragment);
                    ft.commit();
                } else {
                    CommunityCatFragment fragment = CommunityCatFragment.newInstance(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.main_fragment, fragment);
                    ft.commit();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
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
