package moderwarfareapp.futurewarfare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import moderwarfareapp.futurewarfare.requests.DeleteRequest;
import moderwarfareapp.futurewarfare.requests.StartGameRequest;

public class WaitingCreatorActivity extends AppCompatActivity {
private static String nameGame, kindOfGame;
    private TextView numberOfPlayersInGame;     //this textview shows number of user joined in the game
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_creator);

        final AnalogClock clock = (AnalogClock) findViewById(R.id.analogClock);     //a clock is shown in this Activity
        final Button bDeleteGame = (Button) findViewById(R.id.bDeleteGame);
        final Button bStartGame = (Button) findViewById(R.id.bStartGame);


        //take fields from the previous Activity
        Intent intent = getIntent();
        nameGame = intent.getStringExtra("nameGame");
        final String nameuser     = intent.getStringExtra("name");
        final String username = intent.getStringExtra("username");
        kindOfGame = intent.getStringExtra("kindOfGame");
        GlobalValue.getInstance().setKindOfGame(kindOfGame);
        numberOfPlayersInGame = (TextView) findViewById(R.id.remaining);

        //request queue and the handler is passed to the support thread of this Activity
        RequestQueue queue = Volley.newRequestQueue(WaitingCreatorActivity.this);
        Handler handler = new MyHandler();
        final CreatorThread creator = new CreatorThread(handler, nameGame, queue);


        bStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //if startGame button is pressed, we are redirected in MapsActivity
                //thread is stopped
                creator.stopThread();

                //this is the response of the JSON request
                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success) {   //if the exchange is correct
                                Intent startIntent = new Intent(WaitingCreatorActivity.this, MapsActivity.class);
                                startIntent.putExtra("username", username);
                                startIntent.putExtra("name",nameuser);
                                startIntent.putExtra("nameGame", nameGame);
                                startIntent.putExtra("kindOfGame",kindOfGame);
                                WaitingCreatorActivity.this.startActivity(startIntent);
                                //redirected to MapsActivity, passing it name username and the name of the Game
                            }else{   //error message
                                AlertDialog.Builder builder = new AlertDialog.Builder(WaitingCreatorActivity.this);
                                builder.setMessage("Error on starting game").setNegativeButton("Retry",null).create().show();
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                };

                //this is the real JSON Request: when a game is started, it must not compare in the Join List of Games
                StartGameRequest startGameRequest = new StartGameRequest(nameGame,responseListener);

                //must be add in this queue
                RequestQueue queue = Volley.newRequestQueue(WaitingCreatorActivity.this);
                queue.add(startGameRequest);

            }
        });

        bDeleteGame.setOnClickListener(new View.OnClickListener() {     //user can delete the Game that he has created
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success) {   //if the exchange is correct
                                AlertDialog.Builder builder = new AlertDialog.Builder(WaitingCreatorActivity.this);
                                builder.setTitle("Match Deleted").setMessage("Press \"Ok\" to go back").setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(WaitingCreatorActivity.this, UserAreaActivity.class);
                                        intent.putExtra("name", nameuser);
                                        intent.putExtra("username", username);
                                        WaitingCreatorActivity.this.startActivity(intent);
                                    }
                                }).create().show();
                                //redirected to UserAreaActivity, passing it name and username
                            }
                            else{   //error message
                                AlertDialog.Builder builder = new AlertDialog.Builder(WaitingCreatorActivity.this);
                                builder.setMessage("Error deleting match").setNegativeButton("Retry",null).create().show();
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                };

                //this is the real JSON Request
                DeleteRequest deleteRequest = new DeleteRequest(nameGame, responseListener);

                //must be add in this queue
                RequestQueue queue = Volley.newRequestQueue(WaitingCreatorActivity.this);
                queue.add(deleteRequest);
            }
        });

        //thread is started here
        creator.start();
    }

    //this handler helps the execution of the thread
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //this handler receives messages by thread
            Bundle bundle = msg.getData();
            if(bundle.containsKey("refresh")) {
                String value = bundle.getString("refresh");
                numberOfPlayersInGame.setText(value);
                //System.out.println("Messaggio ricevuto: " + value);
            }
        }
    }

    @Override
    public void onBackPressed() {
        //it disables the back button of the smartphone in this Activity
    }
}