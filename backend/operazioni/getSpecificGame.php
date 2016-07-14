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
    
    	$sql = "SELECT * FROM GAME WHERE nameGame = '$nameGame'";
	
    	$result = $conn -> query ($sql) or die ($conn -> error);
		// check for empty result
		if( $result -> num_rows > 0) {
			// success
			$row = mysqli_fetch_array($result);
    	    $response["success"] = true;
    	    $response["nameGame"]	= $row["nameGame"];
    	    $response["location"]	= $row["location"];
    	    $response["players"]	= $row["players"];
    	    $response["start"]		= $row["start"];
    	    $response["date"]		= $row["date"];
    	    $response["duration"]	= $row["duration"];
    	    $response["creator"]	= $row["creator"];  	      
	
    		$nameGame = $row["nameGame"];
                
            $sql2 = "SELECT count(username) as num FROM PLAYERSINGAME WHERE nameGame = '$nameGame'";
					
			$result2 = $conn -> query ($sql2) or die ($conn -> error);
			// check for empty result
			if( $result2 -> num_rows > 0) {
				// success
				$row2 = mysqli_fetch_array($result2);
	    		$response["playersingame"] = $row2["num"];
           	} 
			echo json_encode($response);
    	} else {
    		$response["success"] = false;
      		// echoing JSON response
      		echo json_encode($response);
    	}
    }
    $conn -> close();
?>