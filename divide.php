<?php
  
    $file_path = basename( $_FILES['uploaded_file']['name']);
    error_log(print_r($_FILES, true));
    error_log("jbqsjlgbejq");
    error_log(print_r($_FILES['uploaded_file']['tmp_name'], true));
    error_log("jbqsjlgbejq");
    error_log(print_r($file_path, true));
    if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path)) {
    	error_log("succeed");
        echo "success";
    } else{
    	error_log("failed");
        echo "fail";
    }


$firstline= `head -n1 weka_divide.txt`;
echo $firstline;

$output=shell_exec($firstline);

echo "<pre>$output</pre>";

?>
