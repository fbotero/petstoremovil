package com.mocatto.db;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.mocatto.R;
import com.mocatto.dto.Pet;
import com.mocatto.util.Util;

/**
 * Created by froilan.ruiz on 7/9/2016.
 */
public class PetDB {
    final static int version = 3;

    public static void savePhoto(Pet pet, Activity activity) {
        EventSQLiteOpenHelper admin = new EventSQLiteOpenHelper(activity, Util.BDNAME, null, Util.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues params = new ContentValues();
        params.put("id",pet.getId());
        params.put("image", pet.getPhoto());
        Log.println(Log.INFO,"savePhoto","datos a guardar: "+params.toString());
        bd.insert("Pet", null, params);
        bd.close();

        //Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();
    }

    public static byte[] getPetPhoto(Activity activity,Integer id) {
        EventSQLiteOpenHelper admin = new EventSQLiteOpenHelper(activity, Util.BDNAME, null, Util.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String query = "select id,image from Pet where id="+id;
        Log.println(Log.INFO,"GET","getPetPhoto: "+id);
        Cursor fila = bd.rawQuery(query, null);
        byte[] photo = null;
        if (fila!=null) {
            if(fila.moveToNext()) {
                photo = fila.getBlob(1);
            }
        }else {
            Toast.makeText(activity, R.string.no_found_pet_image, Toast.LENGTH_SHORT).show();
        }
        bd.close();
        return photo;
    }

    public static boolean existPhoto(Activity activity,Integer id) {
        EventSQLiteOpenHelper admin = new EventSQLiteOpenHelper(activity, Util.BDNAME, null, Util.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String query = "select id,image from Pet where id="+id;
        Log.println(Log.INFO,"EXIST","existPhoto: "+id);
        Cursor fila = bd.rawQuery(query, null);
        boolean retorno = false;
        byte[] photo = null;
        int idInTable;
        if (fila!=null) {
            if(fila.moveToFirst()) {
                idInTable = fila.getInt(0);
                photo = fila.getBlob(1);
                retorno=true;
            }

        }else {
            Toast.makeText(activity, R.string.no_found_pet_image, Toast.LENGTH_SHORT).show();
        }
        bd.close();
        return retorno;
    }

    public static void updatePhoto(Pet pet, Activity activity) {
        EventSQLiteOpenHelper admin = new EventSQLiteOpenHelper(activity, Util.BDNAME, null, Util.VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Log.println(Log.INFO,"EXIST","updatePhoto: "+pet.toString());
        ContentValues params = new ContentValues();
        params.put("id",pet.getId());
        params.put("image", pet.getPhoto());
        Log.println(Log.INFO,"updatePhoto","datos a guardar: "+params.toString());
        bd.update("Pet", params, "id="+pet.getId(), null);
        //bd.update("Pet", null, params);
        bd.close();

        //Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();
    }
}
