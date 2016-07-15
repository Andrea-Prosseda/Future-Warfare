package moderwarfareapp.modernwarfare;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

/**
 * Created by Gianlu on 12/05/16.
 */
public class CreatorThread extends Thread {
    private Handler handler;            //handler required to exchange information with the main activity
    private String nameGame;            //this field will contain the name of the game required
    private RequestQueue queue;         //the queue that contains JSON requests to process
    private boolean run = true;

    //this thread needs of handler, nameGame and the queue of the JSON request
    public CreatorThread (Handler handler, String nameGame, RequestQueue queue){
        this.handler = handler;
        this.nameGame = nameGame;
        this.queue = queue;
    }

    public void run (){
        while(run){
            Response.Listener<String> responseListener = new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {
                    //a JSON request is sent every 1s asking the number of players in the game
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if(success) {   //if the exchange is correct
                            //number of players is changed in WaitingCreatorActivity
                            String players = jsonResponse.getString("numberofplayers");
                            String mess = "Current Players: " + players;
                            notifyMessage(mess);
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            };

            //this is the real JSON Request
            NumberOfPlayerRequest numberOfPlayerRequest = new NumberOfPlayerRequest(nameGame,responseListener);

            //must be add in this queue
            queue.add(numberOfPlayerRequest);

            try {
                Thread.sleep(1000);     //for each iteration thread sleeps for 1s
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //this method exchanges information with WaitingCreatorActivity thanks to handler
    private void notifyMessage(String str) {
        //when thread send a message to the Main activiy, it must refresh the text with number of players
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("refresh", ""+str);
        msg.setData(b);
        //System.out.println("Messaggio inviato dal thread: " + str);
        handler.sendMessage(msg);
    }

    //when user want to start the game, thread must be stopped
    public void stopThread (){
        run = false;
    }

    //inner class, used to send the JSON request to a specific URL
    class NumberOfPlayerRequest extends StringRequest {
        private static final String REQUEST_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/getNofPlayers.php";
        private Map<String, String> params;

        public NumberOfPlayerRequest(String nameGame, Response.Listener<String> listener){
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
}