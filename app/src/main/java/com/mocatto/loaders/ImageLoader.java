package com.mocatto.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;

import com.google.gson.Gson;
import com.mocatto.R;
import com.mocatto.dto.Advertising;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import cz.msebera.android.httpclient.util.ByteArrayBuffer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageLoader extends AsyncTaskLoader {
    OkHttpClient client = new OkHttpClient();
    Gson gson = new Gson();
    Advertising advertising;

    public ImageLoader(Context context) {
        super(context);
    }

    @Override
    public Object loadInBackground() {
        return getImageFromUrl();
    }

    private Bitmap getImageFromUrl(){
        Bitmap bitmap=null;
        try {
            String response = runService(getContext().getString(R.string.service_img_advertising));
            advertising = gson.fromJson(response,Advertising.class);
            if (advertising!=null){
                if (advertising.getImage()!=null){
                    bitmap = BitmapFactory.decodeStream(new java.net.URL(advertising.getImage()).openStream());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
        return bitmap;
    }

    private String runService(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private final String PATH = "/data/data/com.helloandroid.imagedownloader/";  //put the downloaded file here


    public void DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
        try {
            URL url = new URL(imageURL);
            File file = new File(fileName);

            long startTime = System.currentTimeMillis();

                        /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();

                        /*
                         * Define InputStreams to read from the URLConnection.
                         */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

                        /*
                         * Read bytes to the Buffer until there is nothing more to read(-1).
                         */
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

                        /* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
