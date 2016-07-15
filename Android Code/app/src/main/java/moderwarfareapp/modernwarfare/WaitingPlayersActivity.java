package moderwarfareapp.modernwarfare;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WaitingPlayersActivity extends AppCompatActivity {
    private static  String nameGame, kindOfGame;
    private String username, name;
    private PlayerThread playerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_players);

        Intent intent = getIntent();

        username = intent.getStringExtra("username");
        nameGame = intent.getStringExtra("nameGame");
        name = intent.getStringExtra("name");
        kindOfGame = intent.getStringExtra("kindOfGame");


        final AnalogClock clock = (AnalogClock) findViewById(R.id.analogClock2);
        final Button bDeleteGame = (Button) findViewById(R.id.bUnsubscribe);

        RequestQueue queue = Volley.newRequestQueue(WaitingPlayersActivity.this);
        Handler handler = new MyHandler();
        playerThread = new PlayerThread(handler,nameGame,queue);

        bDeleteGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //user can delete the "enrollment" of a game
                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {   //a JSON request is sent with the request of delete
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success) {   //if the exchange is correct
                                AlertDialog.Builder builder = new AlertDialog.Builder(WaitingPlayersActivity.this);
                                builder.setTitle("Unsubscribed").setMessage("Press \"Ok\" to go back").setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(WaitingPlayersActivity.this, UserAreaActivity.class);
                                        intent.putExtra("name", name);
                                        intent.putExtra("username", username);
                                        WaitingPlayersActivity.this.startActivity(intent);
                                    }
                                }).create().show();
                                //redirected to UserAreaActivity, passing it name and username
                            }
                            else{   //error message
                                AlertDialog.Builder builder = new AlertDialog.Builder(WaitingPlayersActivity.this);
                                builder.setMessage("Register Failed, Try Another Username").setNegativeButton("Retry",null).create().show();
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                };

                //this is the real JSON Request
                DeleteSingleRequest deleteSingleRequest = new DeleteSingleRequest(nameGame, username, responseListener);

                //must be add in this queue
                RequestQueue queue = Volley.newRequestQueue(WaitingPlayersActivity.this);
                queue.add(deleteSingleRequest);
            }
        });

        //thread is started here
        playerThread.start();
    }

    //this handler helps the execution of the thread
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //this handler receive messages by thread
            Bundle bundle = msg.getData();
            if (bundle.containsKey("start")) {
                //if the game is started, thread must be stopped, than user can be redirected to MapsActivity
                Intent startIntent = new Intent(WaitingPlayersActivity.this, MapsActivity.class);
                startIntent.putExtra("nameGame", nameGame);
                startIntent.putExtra("name", name);
                startIntent.putExtra("username", username);
                startIntent.putExtra("kindOfGame", kindOfGame);
                GlobalValue.getInstance().setKindOfGame(kindOfGame);
                WaitingPlayersActivity.this.startActivity(startIntent);
                playerThread.stopThread();
            }
        }
    }
    @Override
    public void onBackPressed() {
        //it disables the back button of the smartphone in this Activity
    }

    //inner class, used to send the JSON request to a specific URL
    class DeleteSingleRequest extends StringRequest {
        private static final String DELETION_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/deleteSinglePlayer.php";
        private Map<String, String> params;

        public DeleteSingleRequest(String nameGame, String username, Response.Listener<String> listener){
            super(Request.Method.POST, DELETION_URL, listener, null);
            params = new HashMap<>();
            params.put("nameGame", nameGame);
            params.put("username", username);
            System.out.println(username);
        }
        //this constructior run the request with a POST using the url DELETION_URL
        // when volley has done the request, listener is populated.

        public Map<String, String> getParams() {
            return params;
        }
    }
}