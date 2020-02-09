package com.example.group32finalproject;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Download_Local extends MainActivity {

    public static class DownloadFile extends AsyncTask<Void, Void, Boolean> {

        private String downloadFileName= "dataset.model";
        String path = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mkitmefficient/"+downloadFileName);

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d(path,"path");

            String urlOfServer = "http://10.0.2.2/mkitmefficient/";
            InputStream inputStream = null;
            OutputStream outputStream = null;

            HttpURLConnection con = null;

            //Establishing Connection
            try {
                URL url = new URL(urlOfServer+ downloadFileName);
                con = (HttpURLConnection) url.openConnection();
                con.connect();

                if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return false;
                }

                inputStream = con.getInputStream();
                outputStream = new FileOutputStream(path);

                //Transfer model data

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    outputStream.write(data, 0, count);
                }
                if (total > 0) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
            finally
            {
                try {
                    if (outputStream != null)
                        outputStream.close();
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException ex) {
                    return false;
                }
                if (con != null)
                    con.disconnect();
            }
            return false;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
