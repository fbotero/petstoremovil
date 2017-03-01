package com.mocatto.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mocatto.MapsActivity;
import com.mocatto.R;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link MapMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapMenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView storeButton;
    private ImageView vetButton;
    private ImageView hotelButton;
    private ImageView spaButton;
    private ImageView petFriendlyButton;
    private ImageView emergencyButton;
    private AdView mAdView;


    private OnFragmentInteractionListener mListener;

    public MapMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapMenuFragment newInstance(String param1, String param2) {
        MapMenuFragment fragment = new MapMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static MapMenuFragment newInstance(Bundle args) {
        MapMenuFragment fragment = new MapMenuFragment();
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
        View view = inflater.inflate(R.layout.fragment_map_menu, container, false);

        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        storeButton = (ImageView) view.findViewById(R.id.store);
        storeButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                moveToMap(1);
            }
        });

        vetButton = (ImageView) view.findViewById(R.id.vet);
        vetButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                moveToMap(2);
            }
        });

        hotelButton = (ImageView) view.findViewById(R.id.hotel);
        hotelButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                moveToMap(3);
            }
        });

        spaButton = (ImageView) view.findViewById(R.id.spa);
        spaButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                moveToMap(4);
            }
        });

        petFriendlyButton = (ImageView) view.findViewById(R.id.petFriendly);
        petFriendlyButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                moveToMap(5);
            }
        });

        emergencyButton = (ImageView) view.findViewById(R.id.emergency);
        emergencyButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                moveToMap(6);
            }
        });

        return view;
    }

    private void moveToMap(int query) {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        intent.putExtra("query", query);
        getActivity().startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }

        // Refresh the state of the +1 button each time the activity receives focus.
        //mPlusOneButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
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

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }


    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}
