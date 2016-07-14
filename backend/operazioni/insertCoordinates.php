<?php
	require_once '../config.php';
	
    // Array for JSON
	$response = array();
	
    if (!$_REQUEST['nameGame'] || !$_REQUEST['username'] || !$_REQUEST['latitude'] || !$_REQUEST['longitude']){ 
		// campo richiesto mancante
		$response["success"] = false;
		// echoing JSON response
		echo json_encode($response);
	}
    else{
        // Create connection 
        $conn = new mysqli($hostname, $username, $password, $database);

        // Check connection
        if ( $conn -> connect_error) {
          die ("Connessione fallita: " . $conn -> connect_error);
        }

        $nameGame = $_REQUEST["nameGame"];
        $username = $_REQUEST["username"];
        $latitude = $_REQUEST["latitude"];
        $longitude = $_REQUEST["longitude"];

        // Creazione nuovo utente
     	$conn->query("SET NAMES 'utf8'");

        $sql = "UPDATE PLAYERSINGAME SET latitude = '$latitude', longitude = '$longitude' WHERE nameGame = '$nameGame' AND username = '$username'";

        if ( $conn -> query($sql) === TRUE ) {
            $response["success"] = true;
            // echoing JSON response
            echo json_encode($response);
        } 
        else {
            $response["success"] = false;
            // echoing JSON response
            echo json_encode($response);
        }
        $conn -> close();
	}
?>