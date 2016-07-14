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
public class RegisterRequest extends StringRequest {
    private static final String REGISTER_URL = "http://modernwarfareapp.altervista.org/backend/operazioni/register.php";
    private Map<String, String> params;

    public RegisterRequest(String name, String username, String password, Response.Listener<String> listener){
        super(Request.Method.POST, REGISTER_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("username", username);
        params.put("password", password);
    }
    //this constructor run the request with a POST using the url REGISTER_UR
    // when volley has done the request, listener is populated.

    public Map<String, String> getParams() {
        return params;
    }
}