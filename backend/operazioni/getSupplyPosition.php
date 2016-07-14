<?php
	require_once '../config.php';
	
	// Create connection 
	$conn = new mysqli($hostname, $username, $password, $database);
	
	// Check connection
	if ( $conn -> connect_error) {
		die ("Connessione fallita: " . $conn -> connect_error);
	}
	
	// Array for JSON
	$response = array();
	
	// query
	$sql;
    $conn->query("SET NAMES 'utf8'");
	
    if(!$_REQUEST['nameGame']) {
		$response["success"] = false;
		// echoing JSON response
		echo json_encode($response);
	}
	else{
        $nameGame = $_REQUEST["nameGame"];
    
    	$sql = "SELECT nextCreator,latitude, longitude FROM SUPPLY WHERE nameGame = '$nameGame'";
	
    	$result = $conn -> query ($sql) or die ($conn -> error);
		// check for empty result
		if( $result -> num_rows > 0) {
        $row = mysqli_fetch_array($result);
			$response["success"] = true;
            $response["nextCreator"]	= $row["nextCreator"];
            $response["latitude"]	= $row["latitude"];
    	    $response["longitude"]	= $row["longitude"];
            echo json_encode($response);
        } else {
          $response["success"] = false;

          // echoing JSON response
          echo json_encode($response);
        }
    }
    $conn -> close();
?>