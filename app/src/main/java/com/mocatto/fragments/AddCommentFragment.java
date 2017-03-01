package com.mocatto.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.R;
import com.mocatto.adapters.CommentArrayAdapter;
import com.mocatto.dto.Comment;
import com.mocatto.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddCommentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddCommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCommentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View view;

    private int topicId;
    private int commentId;
    private String description;
    private String title;

    private EditText txtComment;
    private TextView txtDescription,txtVerDescripcion;
    private ImageView btnSaveComment;

    private Util util;
    private Comment comment;

    private JSONArray commentArray = new JSONArray();
    private ListView listView;
    private ArrayAdapter adaptador;

    ArrayList commentList = new ArrayList();

    private AdView mAdView;
    ProgressDialog prgDialog;
    private String verDescripcion;

    public AddCommentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCommentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddCommentFragment newInstance(String param1, String param2) {
        AddCommentFragment fragment = new AddCommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddCommentFragment newInstance(Bundle args) {
        AddCommentFragment fragment = new AddCommentFragment();
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
        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(getActivity());
        // Set Progress Dialog Text
        prgDialog.setMessage(getString(R.string.request_in_progress));
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        prgDialog.setProgressStyle(R.style.progress_bar_style);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_comment, container, false);
        getActivity().setTitle(getString(R.string.comentarios));

        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        startFormFields();
        startListenersForm();
        invokeCommentsWS(new RequestParams());
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

    @SuppressLint("WrongViewCast")
    private void startFormFields() {
        if (getArguments() != null) {
            topicId = getArguments().getInt("topicId");
            commentId = getArguments().getInt("commentId");
            description = getArguments().getString("description");
            title = getArguments().getString("title");
        }

        util = new Util();

        txtDescription = (TextView) view.findViewById(R.id.lbDescription);
        txtDescription.setText(title);

        txtVerDescripcion = (TextView) view.findViewById(R.id.tvVerDescripcion);
        txtVerDescripcion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent myIntent = new Intent(view.getContext(), agones.class);
                //startActivityForResult(myIntent, 0);

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create(); //Read Update
                alertDialog.setTitle("Descripción");
                alertDialog.setMessage(description);

                alertDialog.setButton("Cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();  //<-- See This!
            }

        });

        txtComment = (EditText) view.findViewById(R.id.txtDescription);
        hideKeyboard(txtComment,this.getActivity());

        btnSaveComment = (ImageView)view.findViewById(R.id.saveComment);

        final ImageView botonVolverAComunidad = (ImageView) view.findViewById(R.id.fabVolverAmenuComunidad);
        botonVolverAComunidad.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();

                SelectCommunityFragment fragment = SelectCommunityFragment.newInstance(args);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.main_fragment, fragment);
                ft.commit();

            }
        });

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
        btnSaveComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean obligatoryIsGood = true;

                if (txtComment.getText().toString().equals("")){
                    obligatoryIsGood = false;
                }
                if (obligatoryIsGood) {
                    comment = new Comment(null,txtComment.getText().toString(),null,topicId,0);

                    RequestParams params = new RequestParams();
                    params.put("id", comment.getId());
                    params.put("name", comment.getName());
                    params.put("topic", comment.getTopic());
                    params.put("likes", comment.getLikes());

                    if (topicId == 0) {
                        params.put("comment", commentId);
                    }

                    params.setUseJsonStreamer(true);
                    invokeWS(params);

                } else {
                    Toast toast =
                            Toast.makeText(getContext(), R.string.mje_obligatory_form_comment, Toast.LENGTH_LONG);
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
        prgDialog.show();
        String URL = "http://mocatto.herokuapp.com/rest/en/community/saveComment";
        //String URL = "http://10.0.2.2:8080/Mocatto/rest/en/community/saveComment";

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                Toast.makeText(getContext(), R.string.mje_sucessful_comment, Toast.LENGTH_LONG).show();

                Bundle args = new Bundle();
                args.putInt("topicId", topicId);
                args.putString("description", description);
                args.putString("title", title);
                if (topicId == 0) {
                    args.putInt("commentId", commentId);
                }

                AddCommentFragment fragment = AddCommentFragment.newInstance(args);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.main_fragment, fragment);
                ft.commit();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == 404){
                    Toast.makeText(getContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                else if(statusCode == 500){
                    Toast.makeText(getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void invokeCommentsWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();

        String URL = "";
        if (topicId != 0) {
            //URL = "http://10.0.2.2:8080/Mocatto/rest/en/community/getCommentsByTopic/" + topicId;
            URL = "http://mocatto.herokuapp.com/rest/en/community/getCommentsByTopic/" + topicId;
        } else {
            //URL = "http://10.0.2.2:8080/Mocatto/rest/en/community/getCommentsByComment/" + commentId;
            URL = "http://mocatto.herokuapp.com/rest/en/community/getCommentsByComment/" + commentId;
        }

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
                    commentArray = timeline.getJSONArray("commentDTO");
                    commentList = new ArrayList();
                    for (int i = 0; i < commentArray.length(); i++) {
                        String _comment = commentArray.getJSONObject(i).toString();

                        Comment dto = new Gson().fromJson(_comment, Comment.class);
                        commentList.add(dto);
                    }

                    viewComments();

                } catch (JSONException e) {
                    try {
                        String _comment = timeline.getJSONObject("commentDTO").toString();
                        Comment dto = new Gson().fromJson(_comment, Comment.class);
                        commentList = new ArrayList();
                        commentList.add(dto);
                        viewComments();
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

    private void viewComments() {
        listView = (ListView) view.findViewById(R.id.commentList);

        if (commentList!=null){
            //Inicializar el adaptador con la fuente de datos
            adaptador = new CommentArrayAdapter(getActivity(), commentList);
            //Relacionando la lista con el adaptador
            listView.setAdapter(adaptador);
        }
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
