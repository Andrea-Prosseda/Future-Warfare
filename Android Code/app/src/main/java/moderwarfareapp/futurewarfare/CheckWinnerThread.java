package moderwarfareapp.futurewarfare;

import moderwarfareapp.futurewarfare.requests.DetailGameRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gianlu on 12/05/16.
 */
public class CheckWinnerThread extends Thread {
    private Handler handler;        //handler required to exchange information with the main activity
    private String players;         //this field will contain number of user connected in a game, in String type
    private String nameGame;        //this field will contain the name of the game required
    private RequestQueue queue;     //the queue that contains JSON requests to process
    private boolean run = true;

    //this thread needs of handler, nameGame and the queue of the JSON request
    public CheckWinnerThread (Handler handler, String nameGame, RequestQueue queue){
        this.handler = handler;
        this.nameGame = nameGame;
        this.queue = queue;
    }

    public void run (){
        while(run){
            Response.Listener<String> responseListener = new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {       //a JSON request is sent to ask number of players connected in a game
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if(success) {      //if the exchange is correct
                            players = jsonResponse.getString("playersingame");
                            if(players.equals("1"))     //if 1 player remaining MainActivity is warned (to end the game)
                                notifyMessage("lastPlayer");
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            };

            //this is the real JSON Request
            DetailGameRequest detailGameRequests = new DetailGameRequest(nameGame,responseListener);

            //must be add in this queue
            queue.add(detailGameRequests);

            try {
                Thread.sleep(2000);     //for each iteration thread sleeps for 2s
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //this method exchange information with MapsActivity thanks to handler
    private void notifyMessage(String mess) {
        //when thread send this message to MapsActivity, it must end the game, because it contains only 1 player
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString(mess, "");
        msg.setData(b);
        handler.sendMessage(msg);
    }

    //when the game ends thread must be stopped
    public void stopThread (){
        run = false;
    }
}