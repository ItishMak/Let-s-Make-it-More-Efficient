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
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import static java.lang.Thread.sleep;

public class SupportVectorMachine extends MainActivity {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
    String format = simpleDateFormat.format(new Date());

    final String folderPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/mkitmefficient/");
    String upLoadServerUri = "http://10.0.2.2/mkitmefficient/test.php";

    long timeTaken;
   
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.svm_copy);

        final Button SVMTrain = (Button) findViewById(R.id.DTTrain);

        final Button SVMTest = (Button) findViewById(R.id.DTTest);


        SVMTest.setEnabled(false);


        SVMTrain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
                
                long beginTime = System.currentTimeMillis();
                
                String weka_file = "weka_train";

                EditText parameter1 = (EditText) findViewById(R.id.BParameter1Value);
                EditText parameter2 = (EditText) findViewById(R.id.BParameter2Value);


                String c = parameter1.getText().toString();
                String s = parameter2.getText().toString();
                

                if(c.matches("")||s.matches("")) {

                    Toast.makeText(SupportVectorMachine.this,
                            "Please enter the parameters to train the mode", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    
                    String weka_content = "java -cp \"wekaSTRIPPED.jar\" weka.classifiers.functions.SMO -x "+ c +" -s " + s+ " -t \"trainset.arff\" -d dataset.model";

                    FileOperations file_op = new FileOperations();
                    
                    file_op.write(weka_file, weka_content);
                    
                    if(file_op.write(weka_file, weka_content)){
                        
                        Toast.makeText(getApplicationContext(), "Train Model is saved to server to server", Toast.LENGTH_SHORT).show();
                        
                    }else{
                        
                        Toast.makeText(getApplicationContext(), "No input to train", Toast.LENGTH_SHORT).show();

                    }
                    String weka_train_command = "weka_train.txt";

                    new UploadData.UploadFile().execute(folderPath,weka_train_command);

                    new File_PHP.updateData().execute(upLoadServerUri);
                    
                    long stopTime = System.currentTimeMillis();
                    
                    timeTaken = stopTime - beginTime;
                    
                }

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
                        Toast.makeText(SupportVectorMachine.this,
                                "Model is saved to Phone Storage", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(SupportVectorMachine.this,
                                "Model is not trained Yet! Please re-download the model", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SVMTest.setEnabled(true);
            }
        });


        SVMTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
                String result ="";
                try {
                    long beginTime = System.currentTimeMillis();
                    
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(getAssets().open("dataset1.arff")));
                    
                    Instances inst = new Instances(reader);
                    
                    int train_count = (int) Math.round(inst.numInstances() * res/100);
                    
                    int test_count = inst.numInstances() - train_count;
                    
                    Instances test = new Instances(inst, train_count, test_count);

                    TextView resultview = (TextView)findViewById(R.id.DTAnswers);
                    
                    ObjectInputStream o_stream = new ObjectInputStream(new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/mkitmefficient/dataset.model"));

                    Classifier cls = (weka.classifiers.functions.SMO) o_stream.readObject();
                    o_stream.close();
                    reader.close();
                    
                    test.setClassIndex(test.numAttributes() - 1);
                    
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
                String SVM_accuracies = "Model Used: SVM \n"+ result;
                String filename="Log";

                FileOperations file_op = new FileOperations();

                if(file_op.append(filename, SVM_accuracies)){
                    Toast.makeText(getApplicationContext(), filename+".txt created", Toast.LENGTH_SHORT).show();
                }else{
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
