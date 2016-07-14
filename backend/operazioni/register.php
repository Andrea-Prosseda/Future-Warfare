<?php
	require_once '../config.php';
	
    // Array for JSON
	$response = array();
	
    if (!$_REQUEST['name'] || !$_REQUEST['username'] || !$_REQUEST['password']){ 
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

        $name = $_REQUEST["name"];
        $username = $_REQUEST["username"];
        $password = $_REQUEST["password"];

        // Creazione nuovo utente
     	  $conn->query("SET NAMES 'utf8'");

        $sql = "INSERT INTO USER (name, username, password) VALUES ('$name', '$username', '$password');";

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