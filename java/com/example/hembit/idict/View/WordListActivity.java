package com.example.hembit.idict.View;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.hembit.idict.Model.ConnectionInfo;
import com.example.hembit.idict.R;

/**
 * Created by hembit on 18/01/2017.
 */

public class WordListActivity extends AppCompatActivity {

    private String TAG = WordListActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private String url;
    private ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordlist);

        getSupportActionBar().setTitle("Wordlist");

        url = ConnectionInfo.HOST + "api/list/";



    }
}
