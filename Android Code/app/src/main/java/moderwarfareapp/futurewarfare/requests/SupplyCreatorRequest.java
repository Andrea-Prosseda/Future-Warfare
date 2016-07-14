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
public class SupplyCreatorRequest extends StringRequest {
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