package com.example.hembit.idict.View;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hembit.idict.R;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by hembit on 16/01/2017.
 */

public class SingleWordActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private TextView lbl_pronounce, lbl_meaning, lbl_wordtext;
    private String pronounce;
    private String word_text;
    private String meaning;
    private int word_id;
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
    private Object word_item;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_word);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            word_id = extras.getInt("word_id");
            pronounce = extras.getString("word_pronounce");
            meaning = extras.getString("word_meaning");
            word_text = extras.getString("word_text");
        }
        getSupportActionBar().setTitle(word_text);
        tts = new TextToSpeech(this, this);
        lbl_pronounce = (TextView) findViewById(R.id.pronounce);
        lbl_meaning = (TextView) findViewById(R.id.meaning);
        lbl_wordtext = (TextView) findViewById(R.id.word_text);
        btnSpeak = (ImageView) findViewById(R.id.word_sound);
        btnSpeak.setImageResource(R.mipmap.ic_sound_26);

        //lbl_wordtext.setText(list_data.get(position-1).getWord_text());
        lbl_pronounce.setText(pronounce);
        lbl_wordtext.setText(word_text);

        final SpannableStringBuilder str = new SpannableStringBuilder(meaning);
        for(int i = 0; i < word_kind.length; i++){
            int position_word_kind = meaning.indexOf(word_kind[i]);
            if (position_word_kind > 0){

                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), position_word_kind, position_word_kind + word_kind[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        lbl_meaning.setText(str);
        //Log.d(TAG,"check co data hem? "+ list_data.get(0).getWord_pronounce());

        // button on click event
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                speakOut(word_text);
            }

        });
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
                //speakOut(word_text);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut(String word_text) {

        String text = word_text.toString();

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
