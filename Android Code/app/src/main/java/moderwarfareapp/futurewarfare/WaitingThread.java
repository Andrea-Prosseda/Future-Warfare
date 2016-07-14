package moderwarfareapp.futurewarfare;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import org.json.JSONException;
import org.json.JSONObject;
import moderwarfareapp.futurewarfare.requests.UpdateNextCreator;

/**
 * Created by Andrea on 12/05/16.
 */
public class WaitingThread extends Thread {
    private Handler handler;
    private String nameGame;
    private RequestQueue queue;

    //this thread needs of handler, nameGame and the queue of the JSON request
    public WaitingThread(Handler handler, String nameGame, RequestQueue queue){
        this.handler = handler;
        this.nameGame = nameGame;
        this.queue = queue;
    }

    public void run (){
            try {
                Thread.sleep(30000); //30
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GlobalValue.getInstance().setAbleToCreate(true);
            notifyMessageRemoveSupply();
            setNextCreatorOnDB();

    }

    public void setNextCreatorOnDB(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //a JSON request is sent every 6s to notify to MapsActivity to show users in the map
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {  //if the exchange is correct
                        System.out.println("Next Creator Updated");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //this is the real JSON Request
        UpdateNextCreator updateNextCreator= new UpdateNextCreator(nameGame, responseListener);

        //must be add in this queue
        queue.add(updateNextCreator);
    }

    private void notifyMessageRemoveSupply() {
        //when thread send this message to MapsActivity, it must insert enemies coordinates on the map
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("remove","");
        msg.setData(b);
        handler.sendMessage(msg);
    }
}

