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
public class CreateGameRequest extends StringRequest {
    private static final String CREATION_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/createGame.php";
    private Map<String, String> params;

    public CreateGameRequest(String nameGame, String kindOfGame, String location, String players, String start, String date, String duration, String creator, Response.Listener<String> listener){
        super(Request.Method.POST, CREATION_URL, listener, null);
        params = new HashMap<>();
        params.put("nameGame", nameGame);
        params.put("kindOfGame", kindOfGame);
        params.put("location", location);
        params.put("players", players);
        params.put("start", start);
        params.put("date", date);
        params.put("duration", duration);
        params.put("creator", creator);
    }
    //this constructor run the request with a POST using the url CREATION_URL
    // when volley has done the request, listener is populated.

    public Map<String, String> getParams() {
        return params;
    }
}