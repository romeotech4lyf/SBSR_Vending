<?php

$mysqli = new mysqli("localhost", "techlyf2_vending", "Pass@2020", "techlyf2_vending");
 
if($mysqli === false){
    die("ERROR: Could not connect. " . $mysqli->connect_error);
}
 
echo "Connect Successfully. Host info: " . $mysqli->host_info;
?>       