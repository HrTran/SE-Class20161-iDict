package com.example.hembit.idict;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hembit.idict.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hembit on 23/12/2016.
 */

public class LoginActivity extends AppCompatActivity {
    private String userName;
    private String password;
    private String basicAuth = null;
    private boolean remember = true;
    private Toast toast;
    private Context context;
    private JSONObject jsonObject;

    private EditText emailText;
    private EditText passwordText;
    private CheckBox rememberCheckBox;
    private Button signInButton;
    private TextView createAccountTView;
    private TextView resultTView;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        // get view
        emailText = (EditText) findViewById(R.id.loginEmail);
        passwordText = (EditText) findViewById(R.id.loginPassword);
        rememberCheckBox = (CheckBox) findViewById(R.id.loginCheckbox);
        signInButton = (Button) findViewById(R.id.signInButton);
        resultTView = (TextView) findViewById(R.id.resultTView);
        createAccountTView = (TextView) findViewById(R.id.createAccoutTView);
        queue = Volley.newRequestQueue(this);

        // handle remember checkbox
        rememberCheckBox.setChecked(true);
        rememberCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    remember = true;
                }else{
                    remember = false;
                }
            }
        });

        // handle sign in button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = emailText.getText().toString();
                password = passwordText.getText().toString();

                if(isEmail(userName)){
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // if is connected to network
                        new LoginActivity.Communicator().execute(ConnectionInfo.HOST + "api/auth/login");
                    } else {
                        // if not, display a warning
                        toast = Toast.makeText(context, "No network connection available.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }else{
                    toast = Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        createAccountTView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.finish();
            }
        });
    }

    public void getUserInfo (){
        //JsonObjectRequest jsonObjectRequest = new JsonObjectRequest()
        String url = ConnectionInfo.HOST + "/v1/get/user_detail/" + userName;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        resultTView.setText("Response: " + response.toString());
                        jsonObject =  response;
                        User.getUser().initUser(basicAuth, jsonObject, context);
                        // save data if user choose to remember checkbox
                        User.getUser().rememberUser(context, remember);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                resultTView.setText(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap< String, String >();
                headers.put("Authorization", basicAuth);
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }
        };


        queue.add(jsObjRequest);
    }

    public void goToMainScreen(String token) {
        Toast.makeText(context, "goto", Toast.LENGTH_LONG);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("Logged_in",1);
        intent.putExtra("email_user",userName);
        intent.putExtra("token",token);
        LoginActivity.this.startActivity(intent);
        LoginActivity.this.finish();
    }

    public boolean isEmail(String email) {
        if(email.indexOf(' ') != -1){
            // if email has space, return false
            return false;
        }
        if(email.indexOf('.') == -1){
            // if email don't have . character, return false
            return false;
        }
        if(email.indexOf('@') == -1){
            // if email don't have @ character, return false
            return false;
        }
        return true;
    }

    public String downloadUrl(String myUrl) throws IOException {
        InputStream is = null;
        String userCredential = "email:" + userName + "&password:" + password;
        //byte[] postData       = userCredential.getBytes( StandardCharsets.UTF_8 );
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myUrl);

            Map<String,Object> params = new LinkedHashMap<>();
            params.put("email", userName);
            params.put("password", password);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn2 = (HttpURLConnection) url.openConnection();
            conn2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn2.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn2.setRequestMethod("POST");
            conn2.setDoOutput(true);
            conn2.getOutputStream().write(postDataBytes);

            conn2.connect();
            int response = conn2.getResponseCode();
            is = conn2.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;


            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }


    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private class Communicator extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "0";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            char c = result.charAt(0);
            String token = "";
            try {
                JSONObject jsonObject = new JSONObject(result);
                token = jsonObject.getString("token");
            } catch (JSONException e){
                Log.d("Login Activity", "" + e);
            }
            if(c == '{'){
                resultTView.setText("\" Done" + c + "\"");
                //getUserInfo();
                goToMainScreen(token);
            }else{
                basicAuth = null;
                resultTView.setText("\"" + result + "\"" + c);
            }
        }
    }

}


