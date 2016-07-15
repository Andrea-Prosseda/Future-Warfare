package moderwarfareapp.modernwarfare;

import android.Manifest;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;      //fields required to manage map in this Activity
    private boolean mPermissionDenied = false;
    private UiSettings mUiSettings;
    private GoogleMap mMap;

    private Marker[] marks;                             //this array contains markers of all players connected in the same game
    private Circle circle;                              //this field will contain the supply area

    private Handler handler;                            //handler required to exchange information with the main activity
    private MapsThread mapsThread;                      //this thread updates positions of players on the map (notifying when insert and delete them)
    private BluetoothThread btThread;                   //this thread manages the messages exchange with arduino's bluetooth (asking #shots and #dead)
    private WaitingThread waitThread;                   //this thread wait 30s before deleting the supply
    private CheckWinnerThread winnerThread;             //this thread check if there is only 1 player connected in the game, then notify the end of the game
    private CheckSupplyThread checkSupplyThread;        //this thread periodically checks and updates supply position
    private SupplyCreatorThread supplyCreatorThread;    //this thread periodically checks if current player is able to create a new supply


    //game details
    private String name, username, nameGame, players, start, date, location, creator, kindOfGame;
    private long duration;

    //player details
    private final int totalShot = 100;
    private String shot = "100";                        //remaining shots [current shots]
    private String dead = "0";                          //number of dead in the game
    private int lives = 3;                              //used if kindOfGame is DeathMatch
    private LatLng myPosition;                          //updated with current position of the player, used in inRange() and other methods
    private LatLng old_supply= new LatLng(0,0);         //used in inRange(), to control if we are still (or we was) on the supply area

    private TextView textViewTime;                      //this textview will contain the remaining time of the game

    private boolean firstIteration = true;              //used to insert (only 1 time) a fake supply on DB
    private boolean firstTime = true;                   //used to start (only 1 time) counter in getDataGame method (this method is used many times)
    private boolean deathMatch;                         //used to check if current game is a DeathMatch or not. If is true, we have different rules


    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    //take fields from the previous Activity
    Intent intent = getIntent();
    nameGame = intent.getStringExtra("nameGame");
    username = intent.getStringExtra("username");
    name = intent.getStringExtra("name");
    kindOfGame = intent.getStringExtra("kindOfGame");

    if(kindOfGame.equals("Death Match"))
        deathMatch = true;
    else
        deathMatch = false;

    //data of the game must be load at the start of the Activity
    getDataGame();

    //this textview show remaining time of the game
    textViewTime = (TextView) findViewById(R.id.textViewTime);

    SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

    //is the "i" button
    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            getDataGame();
            if(deathMatch) {
                Snackbar.make(view, "Game: " + nameGame + "\t\t\t\t" + "Shot: " + shot + "/" + totalShot + "\t\t\t\t" + "Lives: " + lives +
                        "\t\t\t\t" + "Players: " + players + "\t\t\t\t" + "Time: " + duration + " min", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else{
                Snackbar.make(view, "Game: " + nameGame + "\t\t\t\t" + "Shot: " + shot + "/" + totalShot + "\t\t\t\t" + "Dead: " + dead +
                        "\t\t\t\t" + "Players: " + players + "\t\t\t\t" + "Time: " + duration + " min", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    });

    //handlers help the execution of the thread
    RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
    handler = new MyHandler();
    mapsThread = new MapsThread(handler, nameGame, queue);
    mapsThread.start();
    //mapsThread used to manage positions of enemies

    btThread = new BluetoothThread(handler);
    btThread.start();
    //btThread used to communicate with arduino

    if(deathMatch) {
        winnerThread = new CheckWinnerThread(handler, nameGame, queue);
        winnerThread.start();
    }

    //now we want to check if gps location is enabled and works
    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

    if (!gps) { //if gps is off a message is shown
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage("Please turn on your GPS").setNegativeButton("Ok", null).create().show();
    } else {
        //if it is on, user has to allow permission, mandatory to use the map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setMessage("Please allow permission on GPS").setNegativeButton("Ok", null).create().show();
            return;
        }
        //position of the player is update every 20s or every 50 meters
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000, // 2 seconds interval between updates
                50, // 50 meters between updates
            new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //if the location is changed, take latitude and longitude and send them to server with a JSON request
                    String latitude = String.valueOf(location.getLatitude());
                    String longitude = String.valueOf(location.getLongitude());

                    myPosition = new LatLng(location.getLatitude(),location.getLongitude());
                    GlobalValue.getInstance().setMyPosition(myPosition);

                    if (firstIteration){
                        if(creator.equals(username)){
                            Response.Listener<String> responseListenerRandom = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //a JSON request is sent every 6s to notify to MapsActivity to show enemies in the map
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");
                                        if (success) {  //if the exchange is correct
                                            System.out.println("Initialization complete");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            //this is the real JSON Request
                            InsertSupplyRequest insertSupplyRequest= new InsertSupplyRequest(nameGame, 0.0, 0.0, 0.0, responseListenerRandom);
                            RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);

                            //must be add in this queue
                            queue.add(insertSupplyRequest);
                        }

                        firstIteration = false;
                        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);

                        supplyCreatorThread = new SupplyCreatorThread(handler, nameGame, queue);
                        supplyCreatorThread.start();

                        checkSupplyThread = new CheckSupplyThread(handler, nameGame, queue);
                        checkSupplyThread.start();
                    }

                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if(!success){   //message error
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                    builder.setMessage("Error on updating coordiates").setNegativeButton("Ok",null).create().show();
                                }
                            }
                            catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    };

                    //this is the real JSON Request
                    InsertCoordinatesRequest insertCoordinatesRequest = new InsertCoordinatesRequest(nameGame, username, latitude, longitude, responseListener);

                    //must be add in this queue
                    RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                    queue.add(insertCoordinatesRequest);
                }

                @Override
                public void onProviderDisabled(String provider) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

            });
        }
    }

    //this handler helps the execution of the thread
    private class MyHandler extends Handler {

        //receives messages by thread
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            JSONArray jsonArray = new JSONArray();

            //if message contains "updateMap", markers of user positions must be shown in the map
            if(bundle.containsKey("updateMap")) {
                String value = bundle.getString("updateMap");
                try {
                    jsonArray = new JSONArray(value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                marks = new Marker[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jo = null;
                    try {
                        jo = jsonArray.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        if((!jo.isNull("latitude") && !jo.isNull("longitude")) && !jo.getString("username").equals(username)) {
                            marks[i] = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(jo.getDouble("latitude"), jo.getDouble("longitude")))
                                    .title(jo.getString("username"))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            //if message contains "clear", the map will be totally clean but if there is a supply this will reloaded
            else if (bundle.containsKey("clear")) {
                if (circle != null) {
                        LatLng pos = circle.getCenter();
                        mMap.clear();      //if the message is "clear" markers must be deleted from the map
                        circle = mMap.addCircle(new CircleOptions()
                                .center(pos)
                                .radius(50)
                                .strokeColor(Color.BLACK)
                                .strokeWidth(3)
                                .fillColor(Color.HSVToColor(80, new float[]{105, 1, 1})));
                } else {
                    mMap.clear();      //if the message is "clear" markers must be deleted from the map
                }
            }

            //check if a player is hit, increasing the #dead in frindly match or decreasing the #lives in Death match showing when you are hit
            else if (bundle.containsKey("morto")) {      //is the manager of number of deads
                String temp = bundle.getString("morto");
                temp = temp.substring(0, temp.length() - 2);

                if (!dead.equals(temp) && deathMatch) {
                    lives--;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle("You Are Dead").setMessage("Remaining lives: " + lives).setPositiveButton("Ok", null).create().show();
                }
                dead = temp;
                if (lives == 0) {       //furthermore check if your lives are 0, then your game ends
                    gameOver();
                }
            }

            //updating the #shot, taking them by Arduino
            else if (bundle.containsKey("colpi")){      //is the manager of remaining shots
                shot = bundle.getString("colpi");
                shot = shot.substring(0,shot.length()-2);
            }

            //winnersthread notify to this activity if currentPlayer is the last one player in the game
            else if (bundle.containsKey("lastPlayer")){
                System.out.println("entraaaa");
                getWinners();
            }

            //CheckSupplyThread periodically update the supply position
            else if(bundle.containsKey("supply")) {
                String lat = bundle.getString("latitude");
                String lon = bundle.getString("longitude");

                if(circle!=null)
                    circle.remove();

                circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)))
                        .radius(50)
                        .strokeColor(Color.BLACK)
                        .strokeWidth(3)
                        .fillColor(Color.HSVToColor(80, new float[]{105, 1, 1})));

                if(inRange()){
                    if(old_supply.latitude != circle.getCenter().latitude && old_supply.longitude != circle.getCenter().longitude) {
                        old_supply = new LatLng(circle.getCenter().latitude, circle.getCenter().longitude);
                        sendMessageBluetooth("6");
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        builder.setMessage("Shots reloaded").setPositiveButton("Ok",null).create().show();
                        System.out.println("supply taken");
                    }
                }

            }

            //WaintingThread notify to remove the supply after 30 second
            else if(bundle.containsKey("remove")){
                if(circle!=null) {
                    circle.remove();
                    circle = null;
                }
            }

            //SupplyCreatorThread notify to WaintingThread to start when he insert a supply on the DB
            else if (bundle.containsKey("wait")) {
                RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                waitThread = new WaitingThread(handler, nameGame, queue);
                waitThread.start();
            }
        }
    }

    //check if the player reach the supply area
    public boolean inRange(){
        if(circle==null) {
            return false;
        }
        LatLng center = circle.getCenter();
        double distance = Math.sqrt(Math.pow(myPosition.latitude - center.latitude, 2) + Math.pow(myPosition.longitude - center.longitude, 2));
        if(distance < 0.0011616970345136063) {
            return true;
        }
        return false;
    }

    //when player is dead 3 times, his game ends
    public void gameOver(){
        textViewTime.setText("");
        mapsThread.stopThread();       //threads must be stopped too
        btThread.stopThread();

        if(supplyCreatorThread.isAlive())
            supplyCreatorThread.stopThread();       //stop these thread if they are started
        if(checkSupplyThread.isAlive())
            checkSupplyThread.stopThread();

        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                //to mantein the server clean, at the end of the game each player of a game is cancelled
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(!success) {  //message error
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        builder.setMessage("Error deleting match").setNegativeButton("Retry",null).create().show();
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        //this is the real JSON Request
        WaitingPlayersActivity.DeleteSingleRequest deletePlayerFromGame = new WaitingPlayersActivity().new DeleteSingleRequest(nameGame, username, responseListener);

        //must be add in this queue
        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
        queue.add(deletePlayerFromGame);


        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("You Lose!").setMessage("You are dead 3 times").setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MapsActivity.this, UserAreaActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("username",username);
                MapsActivity.this.startActivity(intent);
                //when the game is over, user is redirected in UserAreaActivity, passing username and name of the player
            }
        }).create().show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //when map is ready, location and some settings are enabled
        mMap = map;

        enableMyLocation();
        mUiSettings = mMap.getUiSettings();

        mUiSettings.setCompassEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    //Enables the My Location layer if the fine location permission has been granted.
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }


    private void getDataGame(){
        //onCreation of this Activity data of the game must be load thanks to this JSON request
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(success) {
                        players = jsonResponse.getString("playersingame");
                        duration = jsonResponse.getLong("duration");
                        location = jsonResponse.getString("location");
                        start = jsonResponse.getString("start");
                        date = jsonResponse.getString("date");
                        creator = jsonResponse.getString("creator");
                        // data are populated, so a timer can startx

                        //start the counter!
                        if(firstTime){
                            final CounterClass timer = new CounterClass(duration * 60000, 1000); //rimettere lo 0
                            timer.start();
                            firstTime = false;
                        }

                    }
                    else{   //message error
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        builder.setMessage("Error on upload detail game").setNegativeButton("Retry",null).create().show();
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        //this is the real JSON Request
        DetailGameRequest detailGameRequest = new DetailGameRequest(nameGame,responseListener);

        //must be add in this queue
        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
        queue.add(detailGameRequest);
    }

    private void getWinners(){
        //when the game is over, the game must be deleted from the server
        textViewTime.setText("");
        mapsThread.stopThread();       //threads must be stopped too
        btThread.stopThread();
        winnerThread.stopThread();

        if(supplyCreatorThread != null)
            supplyCreatorThread.stopThread();   //stop the thread only if it started
        if(checkSupplyThread != null)
            checkSupplyThread.stopThread();     //stop the thread only if it started

        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                //a JSON request is sent when the player left is only one, and update the lives of each player
                //in order to create a ranking
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    System.out.println(success);
                }
                catch (JSONException e){
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setMessage("Error on the ranking").setNegativeButton("Ok",null).create().show();
                }
            }
        };
        //this is the real JSON Request
        UpdateLives updateLives = new UpdateLives(username, nameGame, String.valueOf(lives), responseListener);

        //must be add in this queue
        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
        queue.add(updateLives);

        Response.Listener<String> responseListener2 = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                //a JSON request is sent when the player left is only one and show the rankin based on the lives left
                //who has more lives will be first in the ranking
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    String winnerUsername;
                    String winnerLives;
                    String totalString = "Ranking\n\t";
                    if(success) {
                        int rank;
                        JSONArray jsonArray = jsonResponse.getJSONArray("winners");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            winnerUsername = jo.getString("username");
                            winnerLives = jo.getString("lives");
                            rank = i+1;
                            totalString += rank + ") " +winnerUsername + " with " + winnerLives + " lives\n\t";
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        builder.setTitle("You Win").setMessage(totalString).setPositiveButton("Ok",new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MapsActivity.this, UserAreaActivity.class);
                                intent.putExtra("name",name);
                                intent.putExtra("username",username);
                                intent.putExtra("nameGame",nameGame);
                                MapsActivity.this.startActivity(intent);
                                //when the game is over, user is redirected in UserAreaActivity, passing username and name of the player
                            }
                        }).create().show();
                    }
                    else{   //message error
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        builder.setMessage("Error on the ranking").setNegativeButton("Ok",null).create().show();
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setMessage("Error on the ranking").setNegativeButton("Ok",null).create().show();
                }
            }
        };
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //this is the real JSON Request
        GetRankingRequest rankingRequest = new GetRankingRequest(nameGame, responseListener2);

        //must be add in this queue
        queue = Volley.newRequestQueue(MapsActivity.this);
        queue.add(rankingRequest);
    }

    // class used to start the game timer
    private class CounterClass extends CountDownTimer {
        //timer of the game, when it ends, game finishes
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            textViewTime.setText(hms);
        }

        @Override
        public void onFinish() {

            if(deathMatch)
                getWinners();

            if(!deathMatch){
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Game Over").setMessage("Details of Game:\n"+ "\n\t\t\tPlayer: " + username + "\n\t\t\tDead: " + dead + "\n\t\t\tRemaining Shot: " + shot + "/" + totalShot + "\n\t\t\tGame: " + nameGame + "\n\t\t\tTime: " + duration + " min" + "\n\t\t\tDate: " + date + "\n\t\t\tLocation: " + location
                ).setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MapsActivity.this, UserAreaActivity.class);
                        intent.putExtra("name",name);
                        intent.putExtra("username",username);
                        intent.putExtra("nameGame", nameGame);
                        MapsActivity.this.startActivity(intent);
                        //when the game is over, user is redirected in UserAreaActivity, passing username and name of the player
                    }
                }).create().show();
            }
        }

    }

    //inner classes, used to send the JSON request to a specific URL
    class DetailGameRequest extends StringRequest {
        private static final String REQUEST_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/getSpecificGame.php";
        private Map<String, String> params;

        public DetailGameRequest(String nameGame, Response.Listener<String> listener){
            super(Request.Method.POST, REQUEST_URL, listener, null);
            params = new HashMap<>();
            params.put("nameGame", nameGame);
        }
        //this constructor run the request with a POST using the url REQUEST_URL
        // when volley has done the request, listener is populated.

        public Map<String, String> getParams() {
            return params;
        }
    }

    //inner classes, used to send the JSON request to a specific URL
    class InsertCoordinatesRequest extends StringRequest {
        private static final String REQUEST_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/insertCoordinates.php";
        private Map<String, String> params;

        public InsertCoordinatesRequest(String nameGame, String username, String latitude, String longitude, Response.Listener<String> listener){
            super(Request.Method.POST, REQUEST_URL, listener, null);
            params = new HashMap<>();
            params.put("nameGame", nameGame);
            params.put("username", username);
            params.put("latitude", latitude);
            params.put("longitude", longitude);
        }
        //this constructor run the request with a POST using the url REQUEST_URL
        // when volley has done the request, listener is populated.

        public Map<String, String> getParams() {
            return params;
        }
    }

    //inner classes, used to send the JSON request to a specific URL
    class UpdateLives extends StringRequest {
        private static final String REQUEST_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/updateLives.php";
        private Map<String, String> params;

        public UpdateLives(String username,String nameGame, String lives, Response.Listener<String> listener){
            super(Request.Method.POST, REQUEST_URL, listener, null);
            params = new HashMap<>();
            params.put("username", username);
            params.put("nameGame", nameGame);
            params.put("lives", lives);
        }
        //this constructor run the request with a POST using the url REQUEST_URL
        // when volley has done the request, listener is populated.

        public Map<String, String> getParams() {
            return params;
        }
    }

    //inner classes, used to send the JSON request to a specific URL
    class GetRankingRequest extends StringRequest {
        private static final String REQUEST_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/getRanking.php";
        private Map<String, String> params;

        public GetRankingRequest(String nameGame, Response.Listener<String> listener){
            super(Request.Method.POST, REQUEST_URL, listener, null);
            params = new HashMap<>();
            params.put("nameGame", nameGame);
        }
        //this constructor run the request with a POST using the url REQUEST_URL
        // when volley has done the request, listener is populated.

        public Map<String, String> getParams() {
            return params;
        }
    }

    //inner classes, used to send the JSON request to a specific URL
    class InsertSupplyRequest extends StringRequest {
        private static final String REQUEST_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/insertSupply.php";
        private Map<String, String> params;

        public InsertSupplyRequest(String nameGame, double latitude, double longitude, double radius, Response.Listener<String> listener){
            super(Request.Method.POST, REQUEST_URL, listener, null);
            params = new HashMap<>();
            params.put("nameGame", nameGame);
            params.put("latitude", ""+latitude);
            params.put("longitude", ""+longitude);
            params.put("radius", ""+radius);
        }
        //this constructor run the request with a POST using the url REQUEST_URL
        // when volley has done the request, listener is populated.

        public Map<String, String> getParams() {
            return params;
        }
    }

    // method used to comunicate with ArduinoSide
    private void sendMessageBluetooth(String message) {
        BluetoothSocket btSocket = GlobalValue.getInstance().getSocket();
        OutputStream outStream;

        if(btSocket!=null) {
            try {
                outStream = btSocket.getOutputStream();
                byte[] msgBuffer = message.getBytes();
                outStream.write(msgBuffer);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Displays a dialog with error message explaining that the location permission is missing.
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    //it disables the back button of the smartphone in this Activity
    public void onBackPressed() {
    }
}
