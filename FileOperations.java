package com.example.group32finalproject;

import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperations {

    public FileOperations() {

    }

    public Boolean write(String fname, String fcontent){
        try {

            String fpath = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/mkitmefficient/")+fname+".txt";


            File file = new File(fpath);

            // If file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();

                FileOperations fop1 = new FileOperations();
                fop1.write(fname, fcontent);
            }else{

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(fcontent);
                bw.close();}

            Log.d("Success","Success");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public Boolean append(String fname, String fcontent){
        try {

            String fpath = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/mkitmefficient/")+fname+".txt";

            File file = new File(fpath);

            // If file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();

            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("\n \n"+fcontent);
            bw.close();

            Log.d("File appended","Success");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String read(String fname){

        BufferedReader br = null;
        String response = null;

        try {

            StringBuffer output = new StringBuffer();
            String fpath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/mkitmefficient/"+fname+".txt";

            br = new BufferedReader(new FileReader(fpath));
            String line = "";
            while ((line = br.readLine()) != null) {
                output.append(line+"\n");
            }
            response = output.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }
        return response;

    }
}
