package com.mocatto.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mocatto.R;
import com.mocatto.dto.Comment;
import com.mocatto.fragments.AddCommentFragment;

import java.util.List;

import cz.msebera.android.httpclient.Header;


public class CommentArrayAdapter extends ArrayAdapter<Comment> {

    public CommentArrayAdapter(Context c, List<Comment> objects) {
        super(c, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con image_list_view.xml
            listItemView = inflater.inflate(
                    R.layout.comment_list_item,
                    parent,
                    false);
        }

        //Obteniendo instancia del Tema en la posición actual
        final Comment comment = (Comment) getItem(position);

        //Obteniendo instancias de los elementos
        TextView subtitulo = (TextView) listItemView.findViewById(R.id.subtitulo);

        ImageView button = (ImageView) listItemView.findViewById(R.id.comment);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("commentId", comment.getId());
                //args.putString(ARG_PARAM1, email);
                //args.putString(ARG_PARAM2, "DOG");
                AddCommentFragment fragment = AddCommentFragment.newInstance(args);

                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.main_fragment, fragment);
                ft.commit();
            }
        });


        subtitulo.setText(comment.getName());

        //Devolver al ListView la fila creada
        return listItemView;
    }

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param params
     */
    public void invokeWS(RequestParams params) {
        String URL = "http://mocatto.herokuapp.com/rest/en/community/saveComment";
        //String URL = "http://10.0.2.2:8080/Mocatto/rest/en/community/saveComment";

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getContext(), R.string.like_topic_sucessful, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    Toast.makeText(getContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}