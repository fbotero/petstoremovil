package com.mocatto.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mocatto.NavigationDrawerActivity;
import com.mocatto.R;
import com.mocatto.dto.Cuenta;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectCommunityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectCommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectCommunityFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EMAIL = "email";
    private static final String CUENTA = "cuenta";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String email;
    private AdView mAdView;
    private Cuenta cuenta;


    private OnFragmentInteractionListener mListener;

    public SelectCommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectCommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectCommunityFragment newInstance(String param1, String param2) {
        SelectCommunityFragment fragment = new SelectCommunityFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, param1);
        args.putString(CUENTA, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static SelectCommunityFragment newInstance(Bundle args) {
        SelectCommunityFragment fragment = new SelectCommunityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(EMAIL);
            mParam2 = getArguments().getString(CUENTA);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_select_community, container, false);
        getActivity().setTitle(getString(R.string.comunidad));
        if (getArguments() != null) {
            email = getArguments().getString("email");
            cuenta = (Cuenta) getActivity().getIntent().getExtras().getSerializable("cuenta");
        }

        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("")
                .build();
        mAdView.loadAd(adRequest);



        final ImageView dogCommunityButton = (ImageView) view.findViewById(R.id.ivCommunityDog);
        dogCommunityButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString(EMAIL, email);
                CommunityDogFragment fragment = CommunityDogFragment.newInstance(args);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.main_fragment, fragment);
                ft.commit();
            }
          }
        );

        final ImageView catCommunityButton = (ImageView) view.findViewById(R.id.ivCommunityCat);
        catCommunityButton.setOnClickListener(new Button.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Bundle args = new Bundle();
                      args.putString(EMAIL, email);
                      CommunityCatFragment fragment = CommunityCatFragment.newInstance(args);
                      FragmentTransaction ft = getFragmentManager().beginTransaction();
                      ft.addToBackStack(null);
                      ft.replace(R.id.main_fragment, fragment);
                      ft.commit();
                  }
              }
        );

        final ImageView botonVolverAComunidad = (ImageView) view.findViewById(R.id.fabVolverAmenu);
        botonVolverAComunidad.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();

                Intent refresh = new Intent(getActivity(), NavigationDrawerActivity.class);
                refresh.putExtra("email",  email);
                refresh.putExtra("cuenta",  cuenta);
                refresh.putExtra("isFromCreateAccount","false");
                startActivity(refresh);
                getActivity().finish();

            }
        });

        // Inflate the layout for this fragment
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
