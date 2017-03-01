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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mocatto.R;
import com.mocatto.adapters.ApointmentArrayAdapter;
import com.mocatto.dto.Apointment;
import com.mocatto.dto.Cuenta;
import com.mocatto.dto.Pet;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ApointmentListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ApointmentListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApointmentListFragment extends Fragment {

    private static final String EVENTO = "evento";
    private static final String CUENTA = "cuenta";
    private static final String EMAIL = "email";
    private static final String OPERATION = "operation";
    private static final String LISTA_MASCOTAS="listaMascotas";
    private static final String LISTA_APOINTMENTS = "listaEventos";

    private Apointment evento;
    private Cuenta cuenta;

    private OnFragmentInteractionListener mListener;
    private View view;
    private ListView listView;
    private String email;
    private ArrayAdapter adaptador;
    private ArrayList<Pet> itemsNamePet;
    ArrayList apointmentsList;

    public ApointmentListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ApointmentListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ApointmentListFragment newInstance(String param1, String param2) {
        ApointmentListFragment fragment = new ApointmentListFragment();
        Bundle args = new Bundle();
        args.putString(EVENTO, param1);
        args.putString(CUENTA, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ApointmentListFragment newInstance(Bundle args) {
        ApointmentListFragment fragment = new ApointmentListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //evento = (Apointment) getArguments().getSerializable(EVENTO);
            cuenta = (Cuenta)getArguments().getSerializable(CUENTA);
            email = getArguments().getString(EMAIL);
            itemsNamePet = getArguments().getParcelableArrayList(LISTA_MASCOTAS);
            apointmentsList = getArguments().getParcelableArrayList(LISTA_APOINTMENTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.println(Log.DEBUG,"LISTADO","Antes de la lista");
        view = inflater.inflate(R.layout.fragment_apointment_list, container, false);
        viewApointments();

        return view;
    }

    private void viewApointments() {
        listView = (ListView)view.findViewById(R.id.apointmentsList);

        //apointmentsList = (ArrayList<Apointment>) ApointmentDB.getApointmentsForListInActionBar(email,getActivity());
        if (apointmentsList!=null){
            Log.println(Log.DEBUG,"LISTADO","lista de eventos: "+apointmentsList.size());
            //Inicializar el adaptador con la fuente de datos
            adaptador = new ApointmentArrayAdapter(getActivity(), apointmentsList);
            //Relacionando la lista con el adaptador
            listView.setAdapter(adaptador);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Apointment eventoActual = (Apointment) adaptador.getItem(position);
                        /*String msg = "Elegiste el evento"+eventoActual.getNombre()+"-"+eventoActual.getHora();
                        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();*/

                    Bundle args = new Bundle();
                    args.putString(EMAIL, email);
                    args.putSerializable(EVENTO, eventoActual);
                    args.putSerializable(OPERATION, getString(R.string.operation_update));
                    args.putSerializable(CUENTA, cuenta);
                    args.putParcelableArrayList(LISTA_MASCOTAS,itemsNamePet);

                   // AddEventFragment fragment = AddEventFragment.newInstance(args);
                    AlarmViewFragment fragment = AlarmViewFragment.newInstance(args);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.main_fragment, fragment);
                    ft.commit();
                }
            });
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
