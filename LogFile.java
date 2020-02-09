package com.example.group32finalproject;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LogFile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_file);


        final TextView LogtextView = (TextView) findViewById(R.id.LogtextView);

        //Adding Scrollbar to the textview
        LogtextView.setMovementMethod(new ScrollingMovementMethod());

        String readfilename = "Log";
        FileOperations fop = new FileOperations();
        String text = fop.read("Log");
        if(text != null){
            LogtextView.setText(text);
        }
        else {
            //Incase of no logs/file found
            Toast.makeText(getApplicationContext(), "File not Found", Toast.LENGTH_SHORT).show();
            LogtextView.setText(null);
        }

    }
}
