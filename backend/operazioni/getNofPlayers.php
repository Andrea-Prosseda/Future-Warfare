<?php
	header('Content-type: application/json; charset=utf-8');
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

		$sql = "SELECT count(username) as num FROM PLAYERSINGAME WHERE nameGame = '$nameGame'";
					
		$result = $conn -> query ($sql) or die ($conn -> error);
		// check for empty result
		if( $result -> num_rows > 0) {
			// success
			$row = mysqli_fetch_array($result);
           	$response["success"] = true;  
            $response["numberofplayers"] = $row["num"];;
		} else
    		$response["success"] = false;  
            
		// echoing JSON response
		echo json_encode($response);
	}	
    $conn -> close();
?>