<?php
	require_once '../config.php';
	
	// Array for JSON
	$response = array();
	
	if (!$_REQUEST['nameGame']) {
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
			
			// query
			$conn->query("SET NAMES 'utf8'");
			$sql = "DELETE FROM GAME WHERE nameGame = '$nameGame'";

			// query2
			$sql2 = "DELETE FROM PLAYERSINGAME WHERE nameGame = '$nameGame'";
			
            if ( $conn -> query($sql) === TRUE &&  $conn -> query($sql2) == TRUE) {
				$response["success"] = true;
				
				echo json_encode($response);
			} else {
				$response["success"] = 0;				
				echo json_encode($response);
 			}
			$conn -> close();
	}
?> 