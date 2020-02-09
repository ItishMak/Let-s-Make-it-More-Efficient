package com.example.group32finalproject;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadData extends MainActivity{

    static class UploadFile extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024;

            String urlOfServer = "http://10.0.2.2/mkitmefficient/divide.php";

            InputStream input = null;
            DataOutputStream outputStream = null;

            HttpURLConnection con = null;

            try {
                //URL to upload the file
                URL url = new URL(urlOfServer);
                String fileName = params[1];


                String filePathName = params[0];
                String fileUploadPathName = filePathName + fileName;
                Log.d("fileName", fileName);



                //Opening the HTTP connection to the URL
                con = (HttpURLConnection) url.openConnection();

                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);

                con.setRequestMethod("POST");
                con.setRequestProperty("Connection", "Keep-Alive");
                con.setRequestProperty("ENCTYPE", "multipart/form-data");
                con.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
                con.setRequestProperty("uploaded_file", fileName);

                outputStream = new DataOutputStream(con.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//                outputStream.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename="" + fileName + """ + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name='uploaded_file';fileName='" +fileName+"'" + lineEnd);
                outputStream.writeBytes(lineEnd);

                FileInputStream fileInputStream = new FileInputStream(fileUploadPathName);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //Transfering the data using outputstream

                while (bytesRead > 0) {
                    try {
                        outputStream.write(buffer,0,bufferSize);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                        + lineEnd);

                //Closing all the streams
                fileInputStream.close();
                outputStream.flush();
                outputStream.close();


                //response code 200 corresponds to server status OK
                if (con.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }



            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);


        }
    }
}
