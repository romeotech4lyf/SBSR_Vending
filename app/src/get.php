<?php
header("Content-type:application/json");
$mysqli = new mysqli("localhost", "techlyf2_vending", "Pass@2020", "techlyf2_vending");$query = mysqli_query($mysqli,"SELECT * FROM products");
$response = array();
while($row = mysqli_fetch_assoc($query)){
	array_push($response,
	array(
	"id" =>$row["id"],
	"name" =>$row["name"],
	"imageUrl" =>$row["imageUrl"],
    "price" =>$row["price"],
	"count" =>$row["count"]));
}
echo json_encode($response);
?>
