<?php

$firstline= `head -n1 weka_train.txt`;
echo $firstline;
$output=shell_exec($firstline);
echo $output;
?>