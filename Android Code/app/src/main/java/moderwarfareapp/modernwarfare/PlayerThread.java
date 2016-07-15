package moderwarfareapp.modernwarfare;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gianlu on 13/05/16.
 */
public class PlayerThread extends Thread {
    private Handler handler;        //handler required to exchange information with the main activity
    private String nameGame;        //this field will contain the name of the game required
    private RequestQueue queue;     //the queue that contains JSON requests to process
    private boolean run = false;

    //this thread needs of handler, nameGame and the queue of the JSON request
    public PlayerThread (Handler handler, String nameGame, RequestQueue queue){
        this.handler = handler;
        this.nameGame = nameGame;
        this.queue = queue;
    }

    public void run (){
        run = true;
        while(run){
            Response.Listener<String> responseListener = new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {
                    //a JSON request is sent every 1s asking if the game is started
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if(success) { //if the exchange is correct
                            //if the creator has clicked on "start game" the game of current user must be started
                            notifyMessage("start");
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            };

            //this is the real JSON Request
            CheckStartedGameRequest checkStartedGameRequest = new CheckStartedGameRequest(nameGame,responseListener);

            //must be add in this queue
            queue.add(checkStartedGameRequest);

            try {
                Thread.sleep(1000); //for each iteration thread sleeps for 1s
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
        b.putString("start", ""+str);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    //when user want to start the game, thread must be stopped
    public void stopThread (){
        run = false;
    }

    //inner class, used to send the JSON request to a specific URL
    class CheckStartedGameRequest extends StringRequest {
        private static final String REQUEST_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/getStartValue.php";
        private Map<String, String> params;

        public CheckStartedGameRequest(String nameGame, Response.Listener<String> listener){
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