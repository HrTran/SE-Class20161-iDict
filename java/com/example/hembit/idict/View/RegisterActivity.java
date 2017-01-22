package com.example.hembit.idict.View;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hembit.idict.Model.ConnectionInfo;
import com.example.hembit.idict.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hembit on 18/01/2017.
 */

public class RegisterActivity extends AppCompatActivity {
    private String name;
    private String userName;
    private String password;
    private Toast toast;
    private Context context;
    private String basicAuth = null;

    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private Button registerButton;
    private TextView resultTView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");
        context = getApplicationContext();

        // get view
        nameText = (EditText) findViewById(R.id.registerName);
        emailText = (EditText) findViewById(R.id.registerEmail);
        passwordText = (EditText) findViewById(R.id.registerPassword);
        registerButton = (Button) findViewById(R.id.registerButton);
        resultTView = (TextView) findViewById(R.id.registerResult);



        // handle sign in button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameText.getText().toString();
                userName = emailText.getText().toString();
                password = passwordText.getText().toString();

                if(isEmail(userName)){
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // if is connected to network
                        new RegisterActivity.Communicator().execute(ConnectionInfo.HOST + "api/auth/signup");
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
    }

    public void goToLoginScreen() {
        Toast.makeText(context, "goto", Toast.LENGTH_LONG);
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        RegisterActivity.this.startActivity(intent);
        RegisterActivity.this.finish();
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
        String userCredential = "name:"+ name + "&email:" + userName + "&password:" + password;
        basicAuth = "Basic " + new String(
                Base64.encodeToString(userCredential.getBytes(),
                        Base64.DEFAULT));
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myUrl);

            Map<String,Object> params = new LinkedHashMap<>();
            params.put("name",name);
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

            Boolean status = false;
            String message = "";
            try {
                JSONObject jsonObject = new JSONObject(result);
                status = jsonObject.getBoolean("status");
                message = jsonObject.getString("message");

            } catch (JSONException e){
                Log.d("Login Activity", "" + e);
            }
            if(status == true){
                resultTView.setText("\"" + result + "\"");
                Toast.makeText(context,"Sign up completed!",Toast.LENGTH_SHORT);
                goToLoginScreen();
            }else{
                basicAuth = null;
                resultTView.setText("\"" + result + "\"");
                Toast.makeText(context,"" + message,Toast.LENGTH_SHORT);
            }
        }
    }

}
