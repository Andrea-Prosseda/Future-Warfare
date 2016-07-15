package moderwarfareapp.modernwarfare;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UserAreaActivity extends AppCompatActivity {
    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        connected = GlobalValue.getInstance().getConnected();   //the smartphone is connected with arduino's bluetooth?
        final TextView welcomeMessage = (TextView) findViewById(R.id.tvWelcomeMsg); //this field will contain the welcome message for the user
        final Button bcreateGame = (Button) findViewById(R.id.createGame);          //button used for the creation of the game
        final Button bjoinGame = (Button) findViewById(R.id.joinGame);              //button used to join in a existent game
        final Button bluetoothButton = (Button) findViewById(R.id.bluetoothButton); //button used to connect with arduino's bluetooth

        Intent intent = getIntent();    //take fields from the previous Activity
        final String name = intent.getStringExtra("name");
        final String username = intent.getStringExtra("username");
        final String nameGame = intent.getStringExtra("nameGame");
        if(nameGame!=null){
                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), "Disconnecting..", Toast.LENGTH_LONG).show();
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                };

                //this is the real JSON Request
                WaitingCreatorActivity.DeleteRequest deleteRequest = new WaitingCreatorActivity().new DeleteRequest(nameGame, responseListener);

                //must be add in this queue
                RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
                queue.add(deleteRequest);
        }

        String message = "Welcome " + name + "!";
        welcomeMessage.setText(message);    //welcomeMessage is set

        bcreateGame.setOnClickListener(new View.OnClickListener() {    //associo al tasto register now l'azione di passare alla schermata di Register
            @Override
            public void onClick(View v) {   //if createGame button is pressed, we are redirected in CreateGameActivity
                if (connected) {    //but only if we are connected
                    Intent createGameIntent = new Intent(UserAreaActivity.this, CreateGameActivity.class);
                    createGameIntent.putExtra("username", username);    //we give username and name to the next Activity
                    createGameIntent.putExtra("name", name);
                    UserAreaActivity.this.startActivity(createGameIntent);
                } else {    //error message
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                    builder.setMessage("Please connect the Gun").setNegativeButton("Retry", null).create().show();
                }
            }
        });

        bjoinGame.setOnClickListener(new View.OnClickListener() {    //associo al tasto register now l'azione di passare alla schermata di Register
            @Override
            public void onClick(View v) {   //if joinGame button is pressed, we are redirected in JoinGameActivity
                if (connected) {    //but only if we are connected
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {   //this is the response of the JSON request
                            try {
                                JSONObject jsonResponse = new JSONObject(response);     //take the JSON response
                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {  //if the exchange is correct
                                    String json = jsonResponse.toString();
                                    Intent joinGameIntent = new Intent(UserAreaActivity.this, JoinGameActivity.class);
                                    joinGameIntent.putExtra("username", username);
                                    joinGameIntent.putExtra("name", name);
                                    joinGameIntent.putExtra("json", json);
                                    UserAreaActivity.this.startActivity(joinGameIntent);
                                    //redirected to JoinGameActivity, passing it name and username and the Json of all the games
                                } else {    //error message
                                    AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                                    builder.setMessage("No Game Found").setNegativeButton("Retry", null).create().show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //this is the real JSON Request
                    JoinRequest joinRequest = new JoinRequest(responseListener);

                    //must be add in this queue
                    RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
                    queue.add(joinRequest);
                }
                else{ //if we are not connected, this message is shown
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                    builder.setMessage("Please connect the Gun").setNegativeButton("Retry", null).create().show();
                }
            }
        });

        //if the bluetooth button is pressed, we are redirected in DeviceList
        bluetoothButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent joinGameIntent = new Intent(UserAreaActivity.this, DeviceList.class);
                joinGameIntent.putExtra("username", username);
                joinGameIntent.putExtra("name", name);
                UserAreaActivity.this.startActivity(joinGameIntent);
                //redirected to DeviceList, passing it name and username
            }
        });
    }

    @Override
    public void onBackPressed() {
        //it disables the back button of the smartphone in this Activity
    }

    //inner class, used to send the JSON request to a specific URL
    class JoinRequest extends StringRequest {
        private static final String GAMES_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/getGames.php";

        public JoinRequest(Response.Listener<String> listener){
            super(Request.Method.GET, GAMES_URL, listener, null);
        }
        //this constructor run the request with a GET using the url GAMES_URL
        // when volley has done the request, listener is populated.
    }
}
