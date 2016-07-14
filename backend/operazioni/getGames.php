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

		$val = 0;
        $sql = "SELECT * FROM GAME WHERE started = '0' order by nameGame";

	    $result = $conn -> query ($sql) or die ($conn -> error);
	    // check for empty result
    	if( $result -> num_rows > 0) {
	      // looping through all results
	      	$response["success"] = true;
	    	$response["games"] = array();
			while($row = mysqli_fetch_array($result)){
                // temp user array
                $game = array();
                $game["nameGame"]	= $row["nameGame"];
                $game["kindOfGame"]	= $row["kindOfGame"];
                $game["location"]	= $row["location"];
                $game["players"]	= $row["players"];
                $game["start"]		= $row["start"];
                $game["date"]		= $row["date"];
                $game["duration"]	= $row["duration"];
                $game["creator"]	= $row["creator"];  
                
                $nameGame = $row["nameGame"];
                
                $sql2 = "SELECT count(username) as num FROM PLAYERSINGAME WHERE nameGame = '$nameGame'";
					
				$result2 = $conn -> query ($sql2) or die ($conn -> error);
				// check for empty result
				if( $result2 -> num_rows > 0) {
					// success
					$row2 = mysqli_fetch_array($result2);
	    	       	$game["playersingame"] = $row2["num"];
                }
                //mettere tutti i prodotti nell'array
                array_push($response["games"],$game);
            }
    		echo json_encode($response);
        } else {
          $response["success"] = false;

          // echoing JSON response
          echo json_encode($response);
        }
    $conn -> close();
?>