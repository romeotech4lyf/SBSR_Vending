<?php

include('conn.php');

$return_arr = array();

$fetch = mysqli_query("SELECT * FROM products",$conn); 



$sql = "SELECT * FROM products";
$result = mysqli_query($conn, $sql);

if (mysqli_num_rows($result) > 0) {
  
  while($row = mysqli_fetch_assoc($result)) {
    $row_array['id'] = $row['id'];
    $row_array['name'] = $row['name'];
    $row_array['url'] = $row['imageUrl'];
    $row_array['price'] = $row['price'];
    $row_array['countp'] = $row['count'];

    array_push($return_arr,$row_array);
  }

  echo json_encode($return_arr);
} else {
  echo "0 results";
}
?>
