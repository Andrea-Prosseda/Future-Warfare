package moderwarfareapp.modernwarfare;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateGameActivity extends AppCompatActivity {
    private static  RadioGroup radioGroup;
    private static RadioButton radioButton;
    private static String kindOfGame;   //It's a string that will contains the kind of the game (Death Match or Friendly Match)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        final EditText etNameGame = (EditText) findViewById(R.id.nameGame);     //this field will contain the name of the game created by user
        final EditText etLocation = (EditText) findViewById(R.id.location);     //this field will contain the location of the game
        final EditText etPlayers = (EditText) findViewById(R.id.players);       //this field will contain number of players that user want in the game
        final EditText etStart = (EditText) findViewById(R.id.start);           //this field will contain the starting time
        final EditText etDate = (EditText) findViewById(R.id.date);             //this field will contain the starting date
        final EditText etDuration = (EditText) findViewById(R.id.duration);     //this field will contain the duration of the game
        final Button bfinalCreate = (Button) findViewById(R.id.bfinalCreate);   //button "creation of the game"
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);                //contains the two radioButton (one for Death Match and one for Friendly Match)

        Intent createGameIntent = getIntent();  //take fields from the previous Activity
        final String creator = createGameIntent.getStringExtra("username");
        final String name = createGameIntent.getStringExtra("name");


        bfinalCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //if finalCreate button is pressed, we are redirected in WaitingCreatorActivity
                //get all fields from the corresponding labels in the Activity
                final String nameGame = etNameGame.getText().toString();
                final String location = etLocation.getText().toString();
                final String players = etPlayers.getText().toString();
                final String start = etStart.getText().toString();
                final String date = etDate.getText().toString();;
                final String duration = etDuration.getText().toString();;

                //manage the radio group
                int selected_id = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selected_id);
                kindOfGame = radioButton.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {   //this is the response of the JSON request
                        try{
                            JSONObject jsonResponse = new JSONObject(response);     //take the JSON response
                            boolean success = jsonResponse.getBoolean("success");

                            if(success) {   //if the exchange is correct
                                Intent intent = new Intent(CreateGameActivity.this, WaitingCreatorActivity.class);
                                intent.putExtra("nameGame",nameGame);
                                intent.putExtra("name",name);
                                intent.putExtra("username",creator);
                                intent.putExtra("kindOfGame", kindOfGame);
                                CreateGameActivity.this.startActivity(intent);
                                //redirected to WaitingCreatorActivity, passing it name username and the name of the game just created
                            }
                            else{   //error message
                                AlertDialog.Builder builder = new AlertDialog.Builder(CreateGameActivity.this);
                                builder.setMessage("Creation Failed").setNegativeButton("Retry",null).create().show();
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                };

                //this is the real JSON Request
                CreateGameRequest createGameRequest = new CreateGameRequest(nameGame, kindOfGame, location, players, start, date, duration, creator, responseListener);

                //must be add in this queue
                RequestQueue queue = Volley.newRequestQueue(CreateGameActivity.this);
                queue.add(createGameRequest);
            }
        });
    }

    //inner class, used to send the JSON request to a specific URL
    class CreateGameRequest extends StringRequest {
        private static final String CREATION_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/createGame.php";
        private Map<String, String> params;

        public CreateGameRequest(String nameGame, String kindOfGame, String location, String players, String start, String date, String duration, String creator, Response.Listener<String> listener){
            super(Request.Method.POST, CREATION_URL, listener, null);
            params = new HashMap<>();
            params.put("nameGame", nameGame);
            params.put("kindOfGame", kindOfGame);
            params.put("location", location);
            params.put("players", players);
            params.put("start", start);
            params.put("date", date);
            params.put("duration", duration);
            params.put("creator", creator);
        }
        //this constructor run the request with a POST using the url CREATION_URL
        // when volley has done the request, listener is populated.

        public Map<String, String> getParams() {
            return params;
        }
    }
}
