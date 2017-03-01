package com.mocatto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mocatto.R;
import com.mocatto.dto.Apointment;
import com.mocatto.util.Util;

import java.util.List;

/**
 * Created by froilan.ruiz on 6/22/2016.
 */
public class ApointmentArrayAdapter extends ArrayAdapter<Apointment> {

    public ApointmentArrayAdapter(Context c, List<Apointment> objects) {
        super(c, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater)getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con image_list_view.xml
            listItemView = inflater.inflate(
                    R.layout.image_list_item,
                    parent,
                    false);
        }

        //Obteniendo instancias de los elementos
        TextView titulo = (TextView)listItemView.findViewById(R.id.text1);
        TextView subtitulo = (TextView)listItemView.findViewById(R.id.text2);
        ImageView categoria = (ImageView)listItemView.findViewById(R.id.category);

        //Obteniendo instancia de la Tarea en la posición actual
        Apointment item = (Apointment) getItem(position);
        titulo.setText(item.getTipoEvento()+": "+item.getNombre());
        subtitulo.setText(item.getFecha()+"  "+item.getHora());
        //se ubica que imagen cargar
        Util.setImageApointment(item,categoria,getContext());

        //Devolver al ListView la fila creada
        return listItemView;

    }
}
