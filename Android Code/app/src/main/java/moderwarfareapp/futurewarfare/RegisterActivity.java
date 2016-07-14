package moderwarfareapp.futurewarfare;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import moderwarfareapp.futurewarfare.requests.RegisterRequest;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etName = (EditText) findViewById(R.id.etName);           //this field will contain the name of the user
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);   //this field will contain the username of the user
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);   //this field will contain the password of the user
        final Button bRegister = (Button) findViewById(R.id.bRegister);         //button register

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //when the button register is pressed, a JSON request is sent
                final String name = etName.getText().toString();            //get the name from the corresponding label
                final String username = etUsername.getText().toString();    //get the username from the corresponding label
                final String password = etPassword.getText().toString();    //get the password from the corresponding label

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {   //this is the response of the JSON request
                        try {
                            JSONObject jsonResponse = new JSONObject(response);     //take the JSON response
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {  //if the exchange is correct
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("Register Successful").setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class); //redirected to LoginActivity
                                        RegisterActivity.this.startActivity(intent);
                                    }
                                }).create().show();
                            } else {    //error message
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("Register Failed, Try Another Username").setNegativeButton("Retry", null).create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //this is the real JSON Request
                RegisterRequest registerRequest = new RegisterRequest(name, username, password, responseListener);

                //must be add in this queue
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }
}
