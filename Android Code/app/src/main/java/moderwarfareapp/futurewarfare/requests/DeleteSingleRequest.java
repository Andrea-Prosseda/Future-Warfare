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
public class DeleteSingleRequest extends StringRequest {
    private static final String DELETION_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/deleteSinglePlayer.php";
    private Map<String, String> params;

    public DeleteSingleRequest(String nameGame, String username, Response.Listener<String> listener){
        super(Request.Method.POST, DELETION_URL, listener, null);
        params = new HashMap<>();
        params.put("nameGame", nameGame);
        params.put("username", username);
        System.out.println(username);
    }
    //this constructor run the request with a POST using the url DELETION_URL
    // when volley has done the request, listener is populated.

    public Map<String, String> getParams() {
        return params;
    }
}