package com.example.group32finalproject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import weka.core.Instances;



public class MainActivity extends AppCompatActivity {

    //Declaring the Parameters for different Views

    Activity mainActivity;
    static int res;
    String UploadServeruri = null;

    final String dataSetName = "dataset1.arff";

    final String dataSetPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/mkitmefficient/");


    Spinner spinner;
    String[] algorthims = {"SVM","RF", "NB", "DT"};
    String SpinnerValue;
    Button spinnerValue;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_copy);

        //Creating the correct directory
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/mkitmefficient/");
        Log.d("MAKE DIR", directory + "");
        if (!directory.exists()) {
            boolean res = directory.mkdir();
            Log.d("MAKE res", res + "");

//
        }

        spinner =(Spinner)findViewById(R.id.dropdown);
        spinnerValue = (Button)findViewById(R.id.confirm);
        spinnerValue.setEnabled(false);


        ArrayAdapter<String> algorithms = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, algorthims);

        spinner.setAdapter(algorithms);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                SpinnerValue = (String)spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        spinnerValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Need to write the Algorithms

                switch(SpinnerValue){


                    case "NB":
                        intent = new Intent(MainActivity.this, NaiveBayes.class);
                        startActivity(intent);
                        break;


                    case "DT":
                        intent = new Intent(MainActivity.this, DT.class);
                        startActivity(intent);
                        break;


                    case "SVM":
                        intent = new Intent(MainActivity.this, SupportVectorMachine.class);
                        startActivity(intent);
                        break;


                    case "RF":
                        intent = new Intent(MainActivity.this, RandomForest.class);
                        startActivity(intent);
                        break;


                }
            }

        });

        final Button UploadToFog = (Button) findViewById(R.id.Upload);
        UploadToFog.setEnabled(false);


        UploadServeruri = "http://10.0.2.2/mkitmefficient/divide.php";
//        Log.d("upload",UploadServeruri);

        File dataFile = new File(dataSetPath+ "dataset1.arff");

        if(dataFile.exists()){
        }

        else{
            Toast.makeText(MainActivity.this,
                    "Dataset is not in Local Storage", Toast.LENGTH_SHORT).show();
        }


        UploadToFog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                spinnerValue.setEnabled(true);

                new UploadData.UploadFile().execute(dataSetPath, dataSetName);
//                new UploadService.UploadFile().execute(uploadFilePath, "train.arff");

//                TextView theFact = (TextView) findViewById(R.id.text3);
                EditText percent = (EditText) findViewById(R.id.text3);
                String divper = percent.getText().toString();

                String dividefile = "weka_divide";

                String weka_command = "java -cp \"wekaSTRIPPED.jar\" weka.filters.unsupervised.instance.RemovePercentage -P " + divper +" -i \"dataset1.arff\" -o trainset.arff";

                FileOperations f_rwa = new FileOperations();

                f_rwa.write(dividefile, weka_command);

                if(f_rwa.write(dividefile, weka_command)){
                    Toast.makeText(getApplicationContext(), "Dataset uploaded to Fog server", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "No file", Toast.LENGTH_SHORT).show();

                }

                new UploadData.UploadFile().execute(dataSetPath,dividefile+".txt");

            }
        });

        Button divide = (Button)findViewById(R.id.divide);


        divide.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(getAssets().open("dataset1.arff")));

                    //Using instance classes
                    Instances instance = new Instances(reader);

                    EditText percent = (EditText) findViewById(R.id.text3);
                    res =  Integer.valueOf(percent.getText().toString());

                    //Finding the Size of Dataset
                    int train_count = (int) Math.round(instance.numInstances() * res / 100);
                    int test_count = instance.numInstances() - train_count;

                    //Splitting the Dataset
                    Instances train_data = new Instances(instance, 0, train_count);
                    Instances test_data = new Instances(instance, train_count, test_count);
                    int train = train_data.numInstances();
                    int test = test_data.numInstances();

                    TextView txtview = (TextView)findViewById(R.id.text4);

                    txtview.setText("No of Training Examples:"+ Integer.toString(train) + "\nNo of Testing Examples: " + Integer.toString(test));


                } catch (Exception e) { // TODO Auto-generated catch block
                    e.printStackTrace();

                }

                UploadToFog.setEnabled(true);

            }
        });

    }


    @Override
    public void onResume()

    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.accuracies:
                 mainActivity = this;
                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("RESULTS");
                alertDialog.setMessage("DISPLAYING ACCURACIES OF ALL ALGORITHMS!");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent activityChangeIntent = new Intent(mainActivity, LogFile.class);
                                MainActivity.this.startActivity(activityChangeIntent);

                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

}

