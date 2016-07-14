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
public class JoinGameRequest extends StringRequest {
    private static final String JOININGAME_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/putInGames.php";
    private Map<String, String> params;

    public JoinGameRequest(String username, String nameGame, Response.Listener<String> listener){
        super(Request.Method.POST, JOININGAME_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("nameGame", nameGame);
    }
    //this constructor run the request with a POST using the url JOINGAME_URL
    // when volley has done the request, listener is populated.

    public Map<String, String> getParams() {
        return params;
    }
}