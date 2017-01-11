package com.example.hembit.idict;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hembit.idict.Controller.AppController;
import com.example.hembit.idict.Controller.WordAdapter;
import com.example.hembit.idict.Model.User;
import com.example.hembit.idict.Model.Word;
import com.example.hembit.idict.WordActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.hembit.idict.R.id.meaning;
import static com.example.hembit.idict.R.id.pronounce;

/**
 * Created by hembit on 23/12/2016.
 */

public class WordActivity extends AppCompatActivity{
    private Word word;
    private String basicAuth = null;
    private RequestQueue queue;
    private TextView lbl_pronounce, lbl_meaning;
    private ListView listView;
    private CharSequence Searched_word;
    private List<Word> list_data = new ArrayList<Word>();
    private String TAG = WordActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private String url;
    private ViewGroup viewGroup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wordlist);
        viewGroup = (ViewGroup) findViewById(R.id.content_view);
        viewGroup.addView(View.inflate(this, R.layout.fragment_suggestion_list, null));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Searched_word = extras.getCharSequence("Pass_word");
        }
        getSupportActionBar().setTitle(Searched_word);
        url = ConnectionInfo.HOST + "api/search/word/" + Searched_word;

        listView = (ListView) findViewById(R.id.list_suggestionword);
        lbl_pronounce = (TextView) findViewById(pronounce);
        lbl_meaning = (TextView) findViewById(meaning);
        if(Searched_word != null){
            //getUserInfo();
            new GetJSONrequest().execute();
            Log.d(TAG,"print out" + Searched_word);
        } else {
            Log.d(TAG,"Null search");
            Toast.makeText(getApplicationContext(),"Error: Searched word is null",
                    Toast.LENGTH_LONG).show();
        }



    }

    public void getUserInfo(){
        //JsonObjectRequest jsonObjectRequest = new JsonObjectRequest()

        Log.d(TAG,"print out " + url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            // Parsing json object response
                            // response will be a json object
                            Boolean status = response.getBoolean("status");
                            Boolean found = response.getBoolean("found");
                            Log.d(TAG,"print out" + found);
                            if (found == true){
                                setContentView(R.layout.fragment_word);
                                JSONObject word = response.getJSONObject("word");
                                int id = word.getInt("_id");
                                String pronounce = word.getString("pronounce");
                                String word_text = word.getString("word");
                                String meaning = word.getString("meaning");
                                lbl_pronounce.setText(pronounce);
                                lbl_meaning.setText(meaning);
                                Log.d(TAG,"print out" + word_text);
                            } else {
                                setContentView(R.layout.fragment_suggestion_list);
                                JSONObject soundex = response.getJSONObject("soundex");
                                JSONArray word_array = soundex.getJSONArray("words");

                                for (int i = 0; i < word_array.length(); i++) {
                                    JSONObject word = word_array.getJSONObject(i);
                                    int id = word.getInt("_id");
                                    String pronounce = word.getString("pronounce");
                                    String word_text = word.getString("word");
                                    String meaning = word.getString("meaning");
                                    Word A_word = new Word(id,"","",word_text);
                                    list_data.add(A_word);
                                }

                                listView.setAdapter(new WordAdapter(getApplicationContext() , list_data));
                                listView.setFastScrollEnabled(true);
                                listView.setScrollingCacheEnabled(false);

                                // Khi người dùng click vào các ListItem
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                                        Object o = listView.getItemAtPosition(position);
//                                        NoticeItems noticeItems = (NoticeItems) o;
//                                        Toast.makeText(getActivity(), "Selected :" + " " + noticeItems, Toast.LENGTH_LONG).show();
                                    }
                                });


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> headers = new HashMap< String, String >();
//                headers.put("Authorization", basicAuth);
//                headers.put("Content-Type","application/x-www-form-urlencoded");
//                return headers;
//            }
        };
        if (jsObjRequest != null) {
            Log.d(TAG,jsObjRequest.toString());
//            AppController.getInstance().addToRequestQueue(jsObjRequest);
            queue.add(jsObjRequest);
        } else {
            Log.d(TAG,"json request is null");
        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetJSONrequest extends AsyncTask<Void, Void, Void> {

        String pronounce;
        String word_text;
        String meaning;
        Word A_word;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(WordActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    Boolean status = jsonObj.getBoolean("status");
                    Boolean found = jsonObj.getBoolean("found");
                    Log.d(TAG,"print out" + found);
                    if (found == true){
                        //setContentView(R.layout.fragment_word);
                        JSONObject word = jsonObj.getJSONObject("word");
                        int id = word.getInt("_id");
                        pronounce = word.getString("pronounce");
                        word_text = word.getString("word");
                        meaning = word.getString("meaning");
//                        lbl_pronounce.setText(pronounce);
//                        lbl_meaning.setText(meaning);
                        A_word = new Word(id,pronounce,meaning,"");
                        list_data.add(A_word);
                        Log.d(TAG,"print out" + word_text);
                    } else {


                        JSONObject soundex = jsonObj.getJSONObject("soundex");
                        JSONArray word_array = soundex.getJSONArray("words");

                        for (int i = 0; i < word_array.length(); i++) {
                            JSONObject word = word_array.getJSONObject(i);
                            int id = word.getInt("_id");
                            pronounce = word.getString("pronounce");
                            word_text = word.getString("word");
                            meaning = word.getString("meaning");
                            A_word = new Word(id,"","",word_text);
                            list_data.add(A_word);
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
//            ListAdapter adapter = new SimpleAdapter(
//                    WordActivity.this, contactList,
//                    R.layout.list_item, new String[]{"name", "email",
//                    "mobile"}, new int[]{R.id.name,
//                    R.id.email, R.id.mobile});
//
//            lv.setAdapter(adapter);
            if (list_data.size() == 1) {
                viewGroup.removeAllViews();
                viewGroup.addView(View.inflate(getApplicationContext(), R.layout.fragment_word, null));
                //setContentView(R.layout.fragment_word);
                lbl_pronounce = (TextView) findViewById(R.id.pronounce);
                lbl_meaning = (TextView) findViewById(R.id.meaning);
                lbl_pronounce.setText(list_data.get(0).getWord_pronounce());
                lbl_meaning.setText(list_data.get(0).getWord_meaning());
                Log.d(TAG,"check co data hem? "+ list_data.get(0).getWord_pronounce());
            }
            listView.setAdapter(new WordAdapter(getApplicationContext() , list_data));
            listView.setFastScrollEnabled(true);
            listView.setScrollingCacheEnabled(false);
        }

    }
}
