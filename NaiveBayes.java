package com.example.group32finalproject;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import static java.lang.Thread.sleep;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class NaiveBayes extends MainActivity {

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
    String format = dateFormat.format(new Date());

    final String folderPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/mkitmefficient/");

    String upLoadServerUri = "http://10.0.2.2/mkitmefficient/test.php";

    long timeTaken;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bayes_copy);


        final Button BTest = (Button)findViewById(R.id.DTTest);

        BTest.setEnabled(false);

        final Button BTrain = (Button) findViewById(R.id.DTTrain);

        BTrain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText parameter1 = (EditText) findViewById(R.id.BParameter1Value);
                EditText parameter2 = (EditText) findViewById(R.id.BParameter2Value);

                String weka_file = "weka_train";

                long beginTime = System.currentTimeMillis();

                String p1 = parameter1.getText().toString();
                String p2 = parameter2.getText().toString();

                //validating parameter inputs

                if(p1.matches("")||p2.matches("")||p2.matches("")){ Toast.makeText(NaiveBayes.this,
                        "Please enter the parameters to train the model", Toast.LENGTH_SHORT).show();
                    return;}


                else{

                    //Sending the weka-command to server
                    String weka_content = "java -cp \"wekaSTRIPPED.jar\" weka.classifiers.bayes.NaiveBayes -x "+ p1 +" -s " + p2 + " -t \"trainset.arff\" -d dataset.model";

                    FileOperations file_op = new FileOperations();

                    file_op.write(weka_file, weka_content);

                    if(file_op.write(weka_file, weka_content)){
                        Toast.makeText(getApplicationContext(), "Train Model is saved to server to server", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "No input to train", Toast.LENGTH_SHORT).show();

                    }
                    String weka_train_command = "weka_train.txt";

                    //uploading the parameters to server
                    new UploadData.UploadFile().execute(folderPath,weka_train_command);

                    new File_PHP.updateData().execute(upLoadServerUri);

                    long stopTime = System.currentTimeMillis();

                    timeTaken = stopTime - beginTime;


//                    Downloading the trained model to the Local Storage in Mobile
                    new File_PHP.updateData().execute(upLoadServerUri);
                    try {
                        sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    new Download_Local.DownloadFile().execute();

                    File file = new File(folderPath+"dataset.model");
                    try {
                        sleep(500);

                        if(file.exists()){

                            Toast.makeText(NaiveBayes.this,
                                    "Model is saved to Phone Storage", Toast.LENGTH_SHORT).show();
                        }

                        else {
                            Toast.makeText(NaiveBayes.this,
                                    "Model is not trained Yet! Please re-download the model", Toast.LENGTH_SHORT).show();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    BTest.setEnabled(true);


                }}
        });




        BTest.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                String result="";

                try {
                    long beginTime = System.currentTimeMillis();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(getAssets().open("dataset1.arff")));

                    Instances inst = new Instances(reader);

                    int train_count = (int) Math.round(inst.numInstances() * res / 100);

                    int test_count = inst.numInstances() - train_count;

                    Instances test = new Instances(inst, train_count, test_count);


                    TextView resultview = (TextView)findViewById(R.id.BAnswers);
                    ObjectInputStream o_stream = new ObjectInputStream(new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/mkitmefficient/dataset.model"));

                    //Classifying using Trained Model
                    Classifier cls = (weka.classifiers.bayes.NaiveBayes) o_stream.readObject();
                    o_stream.close();
                    reader.close();

                    test.setClassIndex(test.numAttributes() - 1);

                    //Finding the accuracies of Model
                    Evaluation predictions = new Evaluation(test);
                    predictions.evaluateModel(cls, test);


                    long stopTime   = System.currentTimeMillis();
                    long totalTime = stopTime - beginTime;

                    double false_positve= predictions.falsePositiveRate(0);
                    double false_negative= predictions.falseNegativeRate(0);

                    double true_positive= predictions.truePositiveRate(0);
                    double true_negative= predictions.trueNegativeRate(0);


                    double half_total_error_rate=(false_positve+false_negative)/2;


                    result = predictions.toSummaryString("\nResults:\nExecution Time: "+ "\nTraining time: "+ (timeTaken) + " millisec"+
                                    "\nTesting time: "+ (totalTime) + " millisec" +
                                    format+"\nTrue-Postive-Rate: "+true_positive+
                                    "\nTrue-Negative-Rate: "+true_negative+ "\nFalse-Positive-Rate: "+
                                    false_positve + "\nFalse-Negative-Rate: "+ false_negative +
                                    " \nhalf_total_error_rate: "+ half_total_error_rate
                            ,true);




                    resultview.setMovementMethod(new ScrollingMovementMethod());
                    resultview.setText(result);

                } catch (Exception e) { // TODO Auto-generated catch block
                    e.printStackTrace();

                }
                String NB_accuracies = "Model Used: Naive Bayes \n"+ result;
                String filename="Log";

                FileOperations file_op = new FileOperations();

                if(file_op.append(filename, NB_accuracies)){
                    Toast.makeText(getApplicationContext(), filename+".txt created", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "No file", Toast.LENGTH_SHORT).show();

                }


            }

        });

    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
