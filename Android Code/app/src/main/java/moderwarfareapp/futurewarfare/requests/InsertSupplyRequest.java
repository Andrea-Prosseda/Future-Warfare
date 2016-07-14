package moderwarfareapp.futurewarfare.requests;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrea on 14/07/16.
 */
//class used to send the JSON request to a specific URL
public class InsertSupplyRequest extends StringRequest {
    private static final String REQUEST_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/insertSupply.php";
    private Map<String, String> params;

    public InsertSupplyRequest(String nameGame, double latitude, double longitude, Response.Listener<String> listener){
        super(Request.Method.POST, REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("nameGame", nameGame);
        params.put("latitude", ""+latitude);
        params.put("longitude", ""+longitude);
    }
    //this constructor run the request with a POST using the url REQUEST_URL
    // when volley has done the request, listener is populated.

    public Map<String, String> getParams() {
        return params;
    }
}