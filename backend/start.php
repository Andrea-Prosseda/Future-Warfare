<?php 
	
	require_once 'config.php';

	
	// Create connection to server
	$conn = new mysqli ($hostname, $username, $password);
	
	// Check connection
	if ( $conn -> connect_error) {
		die ("Connessione fallita: " . $conn -> connect_error);
	}
	
	// Drop existing database
	$sql = "DROP DATABASE IF EXISTS my_modernwarfareapp";
	if ( $conn -> query($sql) === TRUE ) {
		echo "Database MODERNWARFARE eliminato con successo <br> ";
	} else {
		echo "Errore nell'eliminazione del database MODERNWARFARE" . $conn -> error; 
	}
	
	// Create database
	$sql = "CREATE DATABASE IF NOT EXISTS my_modernwarfareapp CHARACTER SET utf8 COLLATE utf8_general_ci";
    //$conn->query("SET NAMES 'utf8'");
	if ( $conn -> query($sql) === TRUE ) {
		echo "Database MODERNWARFARE creato con successo <br> ";
	} else {
		echo "Errore nella creazione del database MODERNWARFARE " . $conn -> error; 
	}
	
	$conn -> close();
	

	// Create connection 
	$conn = new mysqli($hostname, $username, $password, $database);


	// creazione della tabella User
	$sql = "CREATE TABLE IF NOT EXISTS USER (
	name VARCHAR(128) NOT NULL,
	username VARCHAR(128) NOT NULL PRIMARY KEY,
    password VARCHAR(128) NOT NULL
	)";
	if ($conn -> query($sql) === TRUE) {
		echo "Creazione tabella USER avvenuta con successo <br>";
	} else {
		echo "Errore nella creazione della tabella USER: " . $conn ->error;
	}

	// creazione della tabella Game
	$sql = "CREATE TABLE IF NOT EXISTS GAME (
    nameGame VARCHAR(128) NOT NULL PRIMARY KEY,
    kindOfGame VARCHAR(128) NOT NULL,
	location VARCHAR(128) NOT NULL,
    players VARCHAR(128) NOT NULL,
	start VARCHAR(128) NOT NULL,
    date VARCHAR(128) NOT NULL,
    duration VARCHAR(128) NOT NULL,
    creator VARCHAR(128) NOT NULL,
    started VARCHAR(128) NOT NULL default 0
	)";
	if ($conn -> query($sql) === TRUE) {
		echo "Creazione tabella GAME avvenuta con successo <br>";
	} else {
		echo "Errore nella creazione della tabella GAME: " . $conn ->error;
	}

	// creazione della tabella PlayersInGame
	$sql = "CREATE TABLE IF NOT EXISTS PLAYERSINGAME (
    nameGame VARCHAR(128) NOT NULL,
	username VARCHAR(128) NOT NULL,
    latitude VARCHAR(128),
    longitude VARCHAR(128),
    lives VARCHAR(128) NOT NULL default 3,
	PRIMARY KEY (nameGame, username)
	)";
	if ($conn -> query($sql) === TRUE) {
		echo "Creazione tabella PLAYERSINGAME avvenuta con successo <br>";
	} else {
		echo "Errore nella creazione della tabella PLAYERSINGAME: " . $conn ->error;
	}
    
    // creazione della tabella Approvvigionamenti
	$sql = "CREATE TABLE IF NOT EXISTS SUPPLY (
	nameGame VARCHAR(128) NOT NULL PRIMARY KEY,
    latitude VARCHAR(128),
    longitude VARCHAR(128),
    nextCreator VARCHAR(128)
	)";
	if ($conn -> query($sql) === TRUE) {
		echo "Creazione tabella SUPPLY avvenuta con successo <br>";
	} else {
		echo "Errore nella creazione della tabella SUPPLY: " . $conn ->error;
	}
    
	$conn -> close();
?> 