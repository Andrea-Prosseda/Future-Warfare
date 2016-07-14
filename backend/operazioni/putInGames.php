<?php
	require_once '../config.php';
	
    // Array for JSON
	$response = array();
	
    if (!$_REQUEST['nameGame'] || !$_REQUEST['username']){ 
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

        // Creazione nuovo utente
     	  $conn->query("SET NAMES 'utf8'");

        $sql = "INSERT INTO PLAYERSINGAME (nameGame, username) VALUES ('$nameGame', '$username');";

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