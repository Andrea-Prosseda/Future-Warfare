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
    
    	$sql = "SELECT username, latitude, longitude FROM PLAYERSINGAME WHERE nameGame = '$nameGame'";
	
    	$result = $conn -> query ($sql) or die ($conn -> error);
		// check for empty result
		if( $result -> num_rows > 0) {
			$response["success"] = true;
	    	$response["users"] = array();
            // success
			while($row = mysqli_fetch_array($result)){
				$game = array();
            	$user["username"] = $row["username"];
            	$user["latitude"]	= $row["latitude"];
    	    	$user["longitude"]	= $row["longitude"];
            
                //mettere tutti i prodotti nell'array
                array_push($response["users"],$user);
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