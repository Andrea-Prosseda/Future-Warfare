<?php
	require_once '../config.php';
	
    // Array for JSON
	$response = array();
	
    if (!$_REQUEST['nameGame'] || !$_REQUEST['kindOfGame'] || !$_REQUEST['location'] || !$_REQUEST['players'] || !$_REQUEST['start'] || !$_REQUEST['date'] || !$_REQUEST['duration'] || !$_REQUEST['creator']){ 
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

        $nameGame 	= $_REQUEST["nameGame"];
        $kindOfGame	= $_REQUEST["kindOfGame"];
        $location 	= $_REQUEST["location"];
        $players 	= $_REQUEST["players"];
        $start 		= $_REQUEST["start"];
        $date 		= $_REQUEST["date"];
        $duration 	= $_REQUEST["duration"];
		$creator 	= $_REQUEST["creator"];
		
        // Creazione nuovo Game
     	$conn->query("SET NAMES 'utf8'");

        $sql = "INSERT INTO GAME (nameGame, kindOfGame, location,players,start,date,duration,creator) 
        VALUES ('$nameGame' , '$kindOfGame', '$location' , '$players' , '$start' , '$date' , '$duration', '$creator');";

        $conn -> query($sql);
        
		$sql = "INSERT INTO PLAYERSINGAME (nameGame,username) 
        VALUES ('$nameGame','$creator');";
        
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