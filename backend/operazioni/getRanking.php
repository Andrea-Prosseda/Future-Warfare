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
    
    	$sql = "SELECT username, lives FROM PLAYERSINGAME WHERE nameGame = '$nameGame' order by lives DESC";
	
    	$result = $conn -> query ($sql) or die ($conn -> error);
		// check for empty result
		if( $result -> num_rows > 0) {
			$response["success"] = true;
            $response["winners"] = array();
	    	
            // success
			while($row = mysqli_fetch_array($result)){
				$winner = array();
            	$winner["username"] = $row["username"];
            	$winner["lives"]	= $row["lives"];
                
                //mettere tutti i prodotti nell'array
                array_push($response["winners"],$winner);
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