package com.example.hembit.idict.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hembit.idict.Model.ConnectionInfo;
import com.example.hembit.idict.Model.History;
import com.example.hembit.idict.Model.Word;
import com.example.hembit.idict.Presenter.HistoryAdapter;
import com.example.hembit.idict.Presenter.WordAdapter;
import com.example.hembit.idict.R;
import com.example.hembit.idict.utilities.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.hembit.idict.R.id.meaning;
import static com.example.hembit.idict.R.id.pronounce;

/**
 * Created by hembit on 18/01/2017.
 */

public class HistoryActivity extends AppCompatActivity {
    private String token,url;
    private ListView listView;
    private ProgressDialog pDialog;
    List<History> listData = new ArrayList<History>();;
    private String TAG = HistoryActivity.class.getSimpleName();
    private Button clear_button;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_history);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            token = extras.getString("token");
        }
        getSupportActionBar().setTitle("History");
//        context = getApplicationContext();
        url = ConnectionInfo.HOST + "api/tracking/";

        listView = (ListView) findViewById(R.id.list_History);

        new HistoryActivity.GetJSONrequest_Loggedin().execute();

//        clear_button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Perform action on click
//                try {
//                    URL url2 = new URL(url);
//
//                    HttpURLConnection conn2 = (HttpURLConnection) url.openConnection();
//                    conn2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                    conn2.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
//                    conn2.setRequestMethod("DELETE");
//                    conn2.setDoOutput(true);
//                    conn2.getOutputStream().write(postDataBytes);
//
//                    conn2.connect();
//                    int response = conn2.getResponseCode();
//                    is = conn2.getInputStream();
//
//                    // Convert the InputStream into a string
//                    String contentAsString = readIt(is, len);
//                    return contentAsString;
//
//
//                    // Makes sure that the InputStream is closed after the app is
//                    // finished using it.
//                } finally {
//                    if (is != null) {
//                        is.close();
//                    }
//                }
//
//            }
//        });


    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetJSONrequest_Loggedin extends AsyncTask<Void, Void, Void> {

        String pronounce;
        String word_text;
        String meaning;
        Word A_word;
        int word_id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HistoryActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url,token);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    Boolean status = jsonObj.getBoolean("status");
                    if(status == true) {
                        JSONObject trackingList = jsonObj.getJSONObject("trackingList");
                        JSONObject list = trackingList.getJSONObject("list");
                        for (int i = 0; i < list.length(); i++) {
                            String word = list.names().get(i).toString();
                            int access_time = list.getInt(list.names().get(i).toString());
                            History item = new History(word, access_time);
                            listData.add(item);
                        }
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }



            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            listView.setAdapter(new HistoryAdapter(getApplicationContext(),listData));
            listView.setFastScrollEnabled(true);
            listView.setScrollingCacheEnabled(false);

        }

    }
}
