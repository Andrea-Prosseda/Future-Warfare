package moderwarfareapp.futurewarfare;

import moderwarfareapp.futurewarfare.requests.GetSupplyPosition;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andrea on 12/05/16.
 */
public class CheckSupplyThread extends Thread {
    private Handler handler;        //handler required to exchange information with the main activity
    private String nameGame;        //this field will contain the name of the game required
    private RequestQueue queue;     //the queue that contains JSON requests to process
    private boolean run = true;

    //this thread needs of handler, nameGame and the queue of the JSON request
    public CheckSupplyThread(Handler handler, String nameGame, RequestQueue queue){
        this.handler = handler;
        this.nameGame = nameGame;
        this.queue = queue;
    }

    public void run (){
        while(run){         //while the thread must be run, while creator starts the game
            Response.Listener<String> responseListenerSupplyPosition = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //a JSON request is sent every 1s to check if there is a supply
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            double supplyLatitude = jsonResponse.getDouble("latitude");
                            double supplyLongitude = jsonResponse.getDouble("longitude");
                            notifyMessageSupply(supplyLatitude, supplyLongitude);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            //this is the real JSON Request
            GetSupplyPosition getSupplyPosition = new GetSupplyPosition(nameGame, responseListenerSupplyPosition);

            //must be add in this queue
            queue.add(getSupplyPosition);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void notifyMessageSupply(double lat, double lon) {
        //when thread send this message to MapsActivity, it must insert the supply
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("supply","");
        b.putString("latitude",Double.toString(lat));
        b.putString("longitude",Double.toString(lon));
        msg.setData(b);
        handler.sendMessage(msg);
    }

    //when the game ends thread must be stopped
    public void stopThread (){
        run = false;
    }
}