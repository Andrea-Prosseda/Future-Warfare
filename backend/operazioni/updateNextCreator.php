<?php
	require_once '../config.php';
	
    // Array for JSON
	$response = array();
	
    if (!$_REQUEST['nameGame']){ 
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
        $nextCreator;
        
        // Creazione nuovo utente
     	$conn->query("SET NAMES 'utf8'");
         
		
        $sql = "SELECT username FROM PLAYERSINGAME WHERE nameGame = '$nameGame'";
		
		$result = $conn -> query ($sql) or die($conn->error);
		// check for empty result
		if( $result -> num_rows > 0) {
        	$val = mt_rand(1,($result -> num_rows)*10)%($result -> num_rows)+1;
            $cont = 0;
            while($row = mysqli_fetch_array($result)){
            	$cont++;
                if ($cont == $val)
                	$nextCreator = $row["username"];
             }
         }
         
         $sql2 = "UPDATE EXTRA SET nextCreator = '$nextCreator' WHERE nameGame = '$nameGame'";
        
        if ( $conn -> query($sql2) === TRUE ) {
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