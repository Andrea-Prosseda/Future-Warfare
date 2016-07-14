package moderwarfareapp.futurewarfare;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import moderwarfareapp.futurewarfare.requests.EnemiesCoordinatesGameRequest;

/**
 * Created by Gianlu on 12/05/16.
 */
public class MapsThread extends Thread {
    private Handler handler;        //handler required to exchange information with the main activity
    private String nameGame;        //this field will contain the name of the game required
    private RequestQueue queue;     //the queue that contains JSON requests to process
    private boolean run = true;

    //this thread needs of handler, nameGame and the queue of the JSON request
    public MapsThread (Handler handler, String nameGame, RequestQueue queue){
        this.handler = handler;
        this.nameGame = nameGame;
        this.queue = queue;
    }

    public void run (){
        while(run){
            Response.Listener<String> responseListenerCoordinate = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //a JSON request is sent every 6s + 15s to notify to MapsActivity to show users in the map
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {  //if the exchange is correct
                            JSONArray jsonArray = jsonResponse.getJSONArray("users");
                            String mess = jsonArray.toString();
                            notifyMessage("updateMap",mess);
                            //a mess contains all users coordinates is sent to MapsActivity thanks to handler
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            //this is the real JSON Request
            EnemiesCoordinatesGameRequest enemiesCoordinatesGameRequest = new EnemiesCoordinatesGameRequest(nameGame, responseListenerCoordinate);

            //must be add in this queue
            queue.add(enemiesCoordinatesGameRequest);

            try {
                Thread.sleep(6000);     //for each iteration thread sleeps for 6s, showing position of users on the map
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //after 6s a request to remove users coordinates on map is sent to MapsActivity
            String mess = "clear";
            notifyMessage("clear",mess);

            try {
                Thread.sleep(15000);    //then, for 15s, no positions are shown in the map
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

    //this method exchange information with MapsActivity thanks to handler
    private void notifyMessage(String mess, String str) {
        //when thread send this message to MapsActivity, it must clear enemies coordinates on the map
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString(mess, ""+str);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    //when the game ends thread must be stopped
    public void stopThread (){
        run = false;
    }
}