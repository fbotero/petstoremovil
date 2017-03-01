package com.mocatto.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mocatto.R;
import com.mocatto.dto.Cuenta;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "param2";
    Context context;

    // TODO: Rename and change types of parameters
    private String email;
    private String mParam2;

    private Cuenta cuenta;
    private OnFragmentInteractionListener mListener;

    private Bundle args;
    private AdView mAdView;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(Bundle arguments) {
        MenuFragment fragment = new MenuFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            email = getArguments().getString("email");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            email = getArguments().getString("email");
            cuenta = (Cuenta) getActivity().getIntent().getExtras().getSerializable("cuenta");
            /*if (cuenta!=null){
                Log.println(Log.DEBUG,"CUENTA","....................................................");
                Log.println(Log.DEBUG,"CUENTA","Si trae los datos de la cuenta: "+cuenta.toString());
            }*/
        }

        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        getActivity().setTitle(R.string.menu);

        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("")
                .build();
        mAdView.loadAd(adRequest);


        args = new Bundle();
        args.putString(ARG_PARAM1, email);
        args.putSerializable("cuenta", cuenta);

        final ImageButton botonRegister = (ImageButton)view.findViewById(R.id.ibRegistro);
        botonRegister.setOnClickListener(new Button.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     PetProfileFragment fragment = PetProfileFragment.newInstance(args);
                     FragmentTransaction ft = getFragmentManager().beginTransaction();
                     ft.addToBackStack(null);
                     ft.replace(R.id.main_fragment, fragment);
                     ft.commit();
                 }
             }
        );

        final ImageButton botonSearch = (ImageButton) view.findViewById(R.id.ibBuscar);
        botonSearch.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MapMenuFragment fragment = MapMenuFragment.newInstance(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.main_fragment, fragment);
                    ft.commit();

                    // Llamado a el mapa
                    //Intent i = new Intent(getActivity(), MapsActivity.class);
                    //getActivity().startActivity(i);
                }
            }
        );
        /*
        botonSearch.setOnClickListener(new Button.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Toast.makeText(getContext(), "Proximamente se vera un mapa ", Toast.LENGTH_LONG).show();

               }
           }
        );
        */

        final ImageButton botonComunidad = (ImageButton) view.findViewById(R.id.ibComunidad);
        botonComunidad.setOnClickListener(new Button.OnClickListener() {
              @Override
              public void onClick(View v) {
                  SelectCommunityFragment fragment = SelectCommunityFragment.newInstance(args);
                  FragmentTransaction ft = getFragmentManager().beginTransaction();
                  ft.addToBackStack(null);
                  ft.replace(R.id.main_fragment, fragment);
                  ft.commit();
              }
          }
        );

        final ImageButton botonCalendario = (ImageButton) view.findViewById(R.id.ibCalendario);
        botonCalendario.setOnClickListener(new Button.OnClickListener() {
               @Override
               public void onClick(View v) {
                   CalendarFragment fragment = CalendarFragment.newInstance(args);
                   FragmentTransaction ft = getFragmentManager().beginTransaction();
                   ft.addToBackStack(null);
                   ft.replace(R.id.main_fragment, fragment);
                   ft.commit();
               }
           }
        );


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
