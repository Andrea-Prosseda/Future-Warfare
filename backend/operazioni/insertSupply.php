<?php
	require_once '../config.php';
	
    // Array for JSON
	$response = array();
	
    if (!$_REQUEST['nameGame'] || !$_REQUEST['latitude'] || !$_REQUEST['longitude']){ 
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
        $latitude = $_REQUEST["latitude"];
        $longitude = $_REQUEST["longitude"];
        $nextCreator;
        
        // Creazione nuovo utente
     	$conn->query("SET NAMES 'utf8'");
        
        $sql2 = "SELECT username FROM PLAYERSINGAME WHERE nameGame = '$nameGame'";
	
		$result = $conn -> query ($sql2) or die($conn->error);
		// check for empty result
		if( $result -> num_rows > 0) {
        	$val = rand(1,$result -> num_rows);
            $cont = 0;
            while($row = mysqli_fetch_array($result)){
            	$cont++;
                if ($cont == $val)
                	$nextCreator = $row["username"];
             }
         }
         
         $sql = "INSERT INTO SUPPLY  (nameGame,latitude,longitude,nextCreator) VALUES( 
        '$nameGame','$latitude','$longitude','$nextCreator');";

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