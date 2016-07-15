package moderwarfareapp.modernwarfare;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

/**
 * Created by Gianlu on 12/05/16.
 */
public class SupplyCreatorThread extends Thread {
    private static final String myUsername = GlobalValue.getInstance().getUsername();       //get Username from GlobalValues
    private Handler handler;        //handler required to exchange information with the main activity
    private String nameGame;        //this field will contain the name of the game required
    private RequestQueue queue;     //the queue that contains JSON requests to process
    private LatLng myPosition;      //

    private boolean run = true;


    //this thread needs of handler, nameGame and the queue of the JSON request
    public SupplyCreatorThread(Handler handler, String nameGame, RequestQueue queue){
        this.handler = handler;
        this.nameGame = nameGame;
        this.queue = queue;
    }

    public void run (){
        while(run) {

            try {
                Thread.sleep(1000);      //thread sleeps 1s before requesting the "ability" to crate a supply
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean ableCreator = GlobalValue.getInstance().getAbleToCreate();  //boolean true to default
            if(ableCreator) {   //ableCreator allow the possibility to create or not a supply
                Response.Listener<String> responseListenerCreator = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //if we are authorized, this JSON request is sent to manage a single supply
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {  //if the exchange is correct
                                // System.out.println(jsonResponse.toString());
                                String userAble = jsonResponse.getString("nextCreator");    //real user authorized to create a supply

                                if (userAble.equals(myUsername)) {      //if we are the user authorized, then
                                    int randomvalue = (int) (Math.random() * 1000);     //we calculate a random number in range (1,100)
                                    if (randomvalue > 0) {      //here you can modify the probability to create a supply
                                        GlobalValue.getInstance().setAbleToCreate(false);           //current user is unauthorized to create a new supply

                                        myPosition = GlobalValue.getInstance().getMyPosition();

                                        //here we create a supply, in a random position in the map (near us) thanks this increment
                                        double supplementLatitude = getRandomIncrement();
                                        double supplementLongitude = getRandomIncrement();

                                        double supplyLatitude = myPosition.latitude + supplementLatitude;
                                        double supplyLongitude = myPosition.longitude + supplementLongitude;
                                        notifyMessage();        //MainActivity is notified with the creation and it must start waiting thread
                                        insertNewSupplyOnDB(supplyLatitude, supplyLongitude); //then the created supply is updated on DB
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //this is the real JSON Request
                SupplyCreatorRequest supplyCreatorRequest = new SupplyCreatorRequest(nameGame, responseListenerCreator);

                //must be add in this queue
                queue.add(supplyCreatorRequest);
            }
        }
    }

    public void insertNewSupplyOnDB(double supplyLatitude, double supplyLongitude){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //a JSON request is sent every 6s to notify to MapsActivity to show users in the map
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {  //if the exchange is correct
                        System.out.println("Supply Updated");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //this is the real JSON Request
        UpdateSupplyRequest  updateSupplyRequest = new UpdateSupplyRequest (nameGame, supplyLatitude, supplyLongitude, 50, responseListener);

        //must be add in this queue
        queue.add(updateSupplyRequest);
    }

    public double getRandomIncrement(){
        int nMin = 10; // numero minimo
        int nMax = 19; // numero massimo
        int tot = ((nMax-nMin) + 1);
        Random random = new Random();
        int temp_result = random.nextInt(tot) + nMin;
        String result = "0.00" + temp_result;
        double increment = Double.parseDouble(result);

        if((int)(Math.random()*10) < 5)
            increment*= -1;

        return increment;
    }

    private void notifyMessage() {
        //when thread send this message to MapsActivity, it must insert enemies coordinates on the map
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("wait","");
        msg.setData(b);
        handler.sendMessage(msg);
    }

    //when the game ends thread must be stopped
    public void stopThread (){
        run = false;
    }

    class SupplyCreatorRequest extends StringRequest {
        private static final String REQUEST_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/getSupplyCreator.php";
        private Map<String, String> params;

        public SupplyCreatorRequest(String nameGame, Response.Listener<String> listener){
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

    class UpdateSupplyRequest extends StringRequest {
        private static final String REQUEST_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/updateSupply.php";
        private Map<String, String> params;

        public UpdateSupplyRequest (String nameGame, double latitude, double longitude, double radius, Response.Listener<String> listener){
            super(Request.Method.POST, REQUEST_URL, listener, null);
            params = new HashMap<>();
            params.put("nameGame", nameGame);
            params.put("latitude", ""+latitude);
            params.put("longitude", ""+longitude);
            params.put("radius", ""+radius);
        }
        //this constructor run the request with a POST using the url REQUEST_URL
        // when volley has done the request, listener is populated.

        public Map<String, String> getParams() {
            return params;
        }
    }
}