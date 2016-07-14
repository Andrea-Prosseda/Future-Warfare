package moderwarfareapp.futurewarfare;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import moderwarfareapp.futurewarfare.requests.LoginRequest;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);       //this field will contain the username of the user
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);       //this field will contain the password of the user
        final Button bLogin = (Button) findViewById(R.id.bLogin);                   //button Login in the Login Activity
        final TextView registerLink = (TextView) findViewById(R.id.tvRegisterHere); //link to the RegisterActivity

        registerLink.setOnClickListener(new View.OnClickListener() {    //associo al tasto register now l'azione di passare alla schermata di Register
            @Override
            public void onClick(View v) {   //if registerLink is pressed, we are redirected in RegisterActivity
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //if bLogin is pressed
                final String username = etUsername.getText().toString();    //get the username from the corresponding label
                final String password = etPassword.getText().toString();    //get the password from the corresponding label

                Response.Listener<String> responseListener = new Response.Listener<String>(){   //this is the response of the JSON request
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);     //take the JSON response
                            boolean success = jsonResponse.getBoolean("success");

                            if(success) {   //if the exchange is correct
                                final String name = jsonResponse.getString("name");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login Successful").setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(LoginActivity.this, UserAreaActivity.class);
                                        intent.putExtra("name",name);
                                        intent.putExtra("username",username);
                                        GlobalValue.getInstance().setUsername(username);  //username is set as a global value, accessible from all the project
                                        LoginActivity.this.startActivity(intent);
                                    }
                                }).create().show();
                                //redirected to UserAreaActivity, passing it name and username
                            }
                            else{   //error message
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login Failed").setNegativeButton("Retry",null).create().show();
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                };

                //this is the real JSON Request
                LoginRequest loginRequest = new LoginRequest(username,password,responseListener);

                //must be add in this queue
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }
    @Override
    public void onBackPressed() {
        //it disables the back button of the smartphone in this Activity
    }
}
