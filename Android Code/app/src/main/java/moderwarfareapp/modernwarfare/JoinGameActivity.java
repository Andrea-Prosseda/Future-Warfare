package moderwarfareapp.modernwarfare;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class JoinGameActivity extends AppCompatActivity {

    private JSONArray jsonArray;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        //take fields from the previous Activity
        final String json = getIntent().getExtras().getString("json");
        final String username = getIntent().getExtras().getString("username");
        final String name= getIntent().getExtras().getString("name");

        //management of the list, it will contain all the games not started yet
        ListView listView = (ListView) findViewById(R.id.list_Item);
        CustomAdapter customAdapter = new CustomAdapter(this, R.layout.row_layout);
        listView.setAdapter(customAdapter);

        //support variables, used to add items on listView
        String game, kindOfGame, creator, location, players, date, duration, start, playersingame;
        Item item;

        try {
            //parsing the JSON and assigning game attributes to support variables
            jsonObject = new JSONObject(json);
            jsonArray = jsonObject.getJSONArray("games");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                game = jo.getString("nameGame");
                creator = "".concat(jo.getString("creator")).concat("]");
                location = "Place: ".concat(jo.getString("location"));
                players = "Max Players: ".concat(jo.getString("players"));
                date = jo.getString("date");
                duration = "Duration: ".concat(jo.getString("duration")).concat(" min");
                start = "At: ".concat(jo.getString("start"));
                playersingame = "Players ".concat(jo.getString("playersingame")).concat("/").concat(jo.getString("players"));
                kindOfGame = jo.getString("kindOfGame");

                //item is created, then is added to the Adapter
                item = new Item(game, kindOfGame, creator, location, players, date, duration, start, playersingame);
                customAdapter.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String nameGame = String.valueOf(((Item) parent.getItemAtPosition(position)).getGame());
                final String selectedKindOfGame = String.valueOf(((Item) parent.getItemAtPosition(position)).getKindOfGame());

                //if listView item is pressed, is shown this message
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinGameActivity.this);
                builder.setMessage("Are you sure to join this game?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //if the user agree, a JSON request is sent, registering him in the game
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");

                                    if (success) {  //if the register is done
                                                    //the user is redirected in the WaitingPlayersActivity
                                        Intent intentJoinGame = new Intent(JoinGameActivity.this, WaitingPlayersActivity.class);
                                        intentJoinGame.putExtra("nameGame", nameGame);
                                        intentJoinGame.putExtra("name", name);
                                        intentJoinGame.putExtra("username", username);
                                        intentJoinGame.putExtra("kindOfGame", selectedKindOfGame);
                                        JoinGameActivity.this.startActivity(intentJoinGame);
                                                //passing it name, name of the game and username
                                    } else {    //message error
                                        AlertDialog.Builder builder = new AlertDialog.Builder(JoinGameActivity.this);
                                        builder.setMessage("Join Failed, Try Again").setNegativeButton("Retry", null).create().show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        //this is the real JSON Request
                        JoinGameRequest joinGameRequest = new JoinGameRequest(username, nameGame, responseListener);

                        //must be add in this queue
                        RequestQueue queue = Volley.newRequestQueue(JoinGameActivity.this);
                        queue.add(joinGameRequest);

                //if the user reject, he returned to listView
                    }
                }).setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();

                AlertDialog dialog = builder.create();
            }
        });
    }

    //inner class, used to send the JSON request to a specific URL
    class JoinGameRequest extends StringRequest {
        private static final String JOININGAME_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/putInGames.php";
        private Map<String, String> params;

        public JoinGameRequest(String username, String nameGame, Response.Listener<String> listener){
            super(Request.Method.POST, JOININGAME_URL, listener, null);
            params = new HashMap<>();
            params.put("username", username);
            params.put("nameGame", nameGame);
        }
        //this constructor run the request with a POST using the url JOINGAME_URL
        // when volley has done the request, listener is populated.

        public Map<String, String> getParams() {
            return params;
        }
    }
}