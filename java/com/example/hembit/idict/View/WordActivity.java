package com.example.hembit.idict.View;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hembit.idict.Model.ConnectionInfo;
import com.example.hembit.idict.utilities.HttpHandler;
import com.example.hembit.idict.Presenter.WordAdapter;
import com.example.hembit.idict.Model.Word;
import com.example.hembit.idict.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.hembit.idict.R.id.meaning;
import static com.example.hembit.idict.R.id.pronounce;

/**
 * Created by hembit on 23/12/2016.
 */

public class WordActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private TextView lbl_pronounce, lbl_meaning, lbl_wordtext;
    private ListView listView;
    private CharSequence Searched_word;
    private List<Word> list_data = new ArrayList<Word>();
    private String TAG = WordActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private String url;
    private ViewGroup viewGroup;
    String[] word_kind = {
            "danh từ",
            "động từ",
            "tính từ",
            "thán từ",
            "nội động từ",
            "ngoại động từ",
            "phó từ",
            "trạng từ"
    };
    private TextToSpeech tts;
    private ImageView btnSpeak;
    private int isLogin = 0;
    private String token;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wordlist);
        viewGroup = (ViewGroup) findViewById(R.id.content_view);
        viewGroup.addView(View.inflate(this, R.layout.fragment_suggestion_list, null));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Searched_word = extras.getCharSequence("Pass_word");
            isLogin = extras.getInt("isLoggedin");
            token = extras.getString("token");
        }
        getSupportActionBar().setTitle(Searched_word);
        tts = new TextToSpeech(this, this);
        url = ConnectionInfo.HOST + "api/search/word/" + Searched_word;

        listView = (ListView) findViewById(R.id.list_suggestionword);
        lbl_pronounce = (TextView) findViewById(pronounce);
        lbl_meaning = (TextView) findViewById(meaning);
        if(Searched_word != null){
            if(isLogin == 1) {
                new  GetJSONrequest_Loggedin().execute();
            } else {
                new GetJSONrequest_notLogin().execute();
            }
            Log.d(TAG,"print out word: " + Searched_word);
        } else {
            Log.d(TAG,"Null search");
            Toast.makeText(getApplicationContext(),"Error: Searched word is null",
                    Toast.LENGTH_LONG).show();
        }

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
            pDialog = new ProgressDialog(WordActivity.this);
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
                    Boolean found = jsonObj.getBoolean("found");
                    Log.d(TAG,"print out: " + found);
                    if (found == true){
                        //setContentView(R.layout.fragment_word);
                        JSONObject word = jsonObj.getJSONObject("word");
                        word_id = word.getInt("_id");
                        try {
                            pronounce = word.getString("pronounce");
                        } catch (JSONException e){}
                        word_text = word.getString("word");
                        meaning = word.getString("meaning");
                        A_word = new Word(word_id,pronounce,meaning, word_text);
                        list_data.add(A_word);
                        Log.d(TAG,"print out meaning " + meaning);
                    } else {


                        JSONObject soundex = jsonObj.getJSONObject("soundex");
                        JSONArray word_array = soundex.getJSONArray("words");

                        for (int i = 0; i < word_array.length(); i++) {
                            JSONObject word = word_array.getJSONObject(i);
                            word_id = word.getInt("_id");
                            try{
                                pronounce = word.getString("pronounce");
                            } catch (JSONException e){}
                            word_text = word.getString("word");
                            meaning = word.getString("meaning");
                            A_word = new Word(word_id,pronounce,meaning,word_text);
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
            if (list_data.size() == 1) {
                // Change layout
                viewGroup.removeAllViews();
                viewGroup.addView(View.inflate(getApplicationContext(), R.layout.fragment_word, null));

                lbl_pronounce = (TextView) findViewById(R.id.pronounce);
                lbl_meaning = (TextView) findViewById(R.id.meaning);
                lbl_wordtext = (TextView) findViewById(R.id.word_text);
                btnSpeak = (ImageView) findViewById(R.id.word_sound);

                btnSpeak.setImageResource(R.mipmap.ic_sound_26);
                lbl_pronounce.setText(list_data.get(0).getWord_pronounce());
                lbl_wordtext.setText(list_data.get(0).getWord_text());

                final SpannableStringBuilder str = new SpannableStringBuilder(list_data.get(0).getWord_meaning());
                Log.d(TAG,"check co data hem? "+ list_data.get(0).getWord_pronounce()
                        + list_data.get(0).getWord_text()
                        + list_data.get(0).getWord_meaning());
                for(int i = 0; i < word_kind.length; i++){
                    int position = list_data.get(0).getWord_meaning().indexOf(word_kind[i]);
                    if (position > 0){

                        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), position, position + word_kind[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                lbl_meaning.setText(str);
                //Log.d(TAG,"check co data hem? "+ list_data.get(0).getWord_pronounce());

                // button on click event
                btnSpeak.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        speakOut();
                    }

                });
            }
            listView.setAdapter(new WordAdapter(getApplicationContext() , list_data));
            listView.setFastScrollEnabled(true);
            listView.setScrollingCacheEnabled(false);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    final Object word_item = listView.getItemAtPosition(position);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                /* Create an Intent that will start the SingleWordActivity. */
                            Intent SingleWordIntent = new Intent(getApplicationContext(), SingleWordActivity.class);
                            SingleWordIntent.putExtra("word_id",((Word) word_item).getWordIdId());
                            SingleWordIntent.putExtra("word_pronounce",((Word) word_item).getWord_pronounce());
                            SingleWordIntent.putExtra("word_meaning",((Word) word_item).getWord_meaning());
                            SingleWordIntent.putExtra("word_text",((Word) word_item).getWord_text());
                            startActivity(SingleWordIntent);
                        }
                    }, 0);



                }
            });
        }

    }


    private class GetJSONrequest_notLogin extends AsyncTask<Void, Void, Void> {

        String pronounce;
        String word_text;
        String meaning;
        Word A_word;
        int word_id;

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
            String jsonStr = sh.makeServiceCall(url,token);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    Boolean status = jsonObj.getBoolean("status");
                    Boolean found = jsonObj.getBoolean("found");
                    Log.d(TAG,"print out: " + found);
                    if (found == true){
                        //setContentView(R.layout.fragment_word);
                        JSONObject word = jsonObj.getJSONObject("word");
                        word_id = word.getInt("_id");
                        try {
                            pronounce = word.getString("pronounce");
                        } catch (JSONException e){}
                        word_text = word.getString("word");
                        meaning = word.getString("meaning");
                        A_word = new Word(word_id,pronounce,meaning, word_text);
                        list_data.add(A_word);
                        Log.d(TAG,"print out meaning " + meaning);
                    } else {


                        JSONObject soundex = jsonObj.getJSONObject("soundex");
                        JSONArray word_array = soundex.getJSONArray("words");

                        for (int i = 0; i < word_array.length(); i++) {
                            JSONObject word = word_array.getJSONObject(i);
                            word_id = word.getInt("_id");
                            try{
                                pronounce = word.getString("pronounce");
                            } catch (JSONException e){}
                            word_text = word.getString("word");
                            meaning = word.getString("meaning");
                            A_word = new Word(word_id,pronounce,meaning,word_text);
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
            if (list_data.size() == 1) {
                // Change layout
                viewGroup.removeAllViews();
                viewGroup.addView(View.inflate(getApplicationContext(), R.layout.fragment_word, null));

                lbl_pronounce = (TextView) findViewById(R.id.pronounce);
                lbl_meaning = (TextView) findViewById(R.id.meaning);
                lbl_wordtext = (TextView) findViewById(R.id.word_text);
                btnSpeak = (ImageView) findViewById(R.id.word_sound);

                btnSpeak.setImageResource(R.mipmap.ic_sound_26);
                lbl_pronounce.setText(list_data.get(0).getWord_pronounce());
                lbl_wordtext.setText(list_data.get(0).getWord_text());

                final SpannableStringBuilder str = new SpannableStringBuilder(list_data.get(0).getWord_meaning());
                Log.d(TAG,"check co data hem? "+ list_data.get(0).getWord_pronounce()
                                                + list_data.get(0).getWord_text()
                                                 + list_data.get(0).getWord_meaning());
                for(int i = 0; i < word_kind.length; i++){
                    int position = list_data.get(0).getWord_meaning().indexOf(word_kind[i]);
                    if (position > 0){

                        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), position, position + word_kind[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                lbl_meaning.setText(str);
                //Log.d(TAG,"check co data hem? "+ list_data.get(0).getWord_pronounce());

                // button on click event
                btnSpeak.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        speakOut();
                    }

                });
            }
            listView.setAdapter(new WordAdapter(getApplicationContext() , list_data));
            listView.setFastScrollEnabled(true);
            listView.setScrollingCacheEnabled(false);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    final Object word_item = listView.getItemAtPosition(position);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                /* Create an Intent that will start the SingleWordActivity. */
                            Intent SingleWordIntent = new Intent(getApplicationContext(), SingleWordActivity.class);
                            SingleWordIntent.putExtra("word_id",((Word) word_item).getWordIdId());
                            SingleWordIntent.putExtra("word_pronounce",((Word) word_item).getWord_pronounce());
                            SingleWordIntent.putExtra("word_meaning",((Word) word_item).getWord_meaning());
                            SingleWordIntent.putExtra("word_text",((Word) word_item).getWord_text());
                            startActivity(SingleWordIntent);
                        }
                    }, 0);



                }
            });
        }

    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                //btnSpeak.setEnabled(true);
                //speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut() {

        String text = Searched_word.toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(text);
        } else {
            ttsUnder20(text);
        }
        //tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }


}
