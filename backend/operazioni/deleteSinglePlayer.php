<?php
	require_once '../config.php';
	
	// Array for JSON
	$response = array();
	
	if (!$_REQUEST['nameGame'] || !$_REQUEST['username']) {
			// required field is missing
			$response["success"] = false;
			// echoing JSON response
			echo json_encode($response);
			
	}else{
			// Create connection 
			$conn = new mysqli($hostname, $username, $password, $database);
	
			// Check connection
			if ( $conn -> connect_error) {
				die ("Connessione fallita: " . $conn -> connect_error);
			}
			
			$nameGame = $_REQUEST['nameGame'];
			$username = $_REQUEST['username'];
			// query
			$conn->query("SET NAMES 'utf8'");
			$sql = "DELETE FROM PLAYERSINGAME WHERE nameGame = '$nameGame' AND username = '$username'";

            if ( $conn -> query($sql) === TRUE) {
				$response["success"] = true;
				
				echo json_encode($response);
			} else {
				$response["success"] = 0;				
				echo json_encode($response);
 			}
			$conn -> close();
	}
?> 