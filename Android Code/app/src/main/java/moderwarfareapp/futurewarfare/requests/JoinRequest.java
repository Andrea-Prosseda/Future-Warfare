package moderwarfareapp.futurewarfare.requests;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by andrea on 14/07/16.
 */
//class used to send the JSON request to a specific URL
public class JoinRequest extends StringRequest {
    private static final String GAMES_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/getGames.php";

    public JoinRequest(Response.Listener<String> listener){
        super(Request.Method.GET, GAMES_URL, listener, null);
    }
    //this constructor run the request with a GET using the url GAMES_URL
    // when volley has done the request, listener is populated.
}