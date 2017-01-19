package com.example.hembit.idict;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private int isLogin = 0;
        private String user_email;
        private String token;
        private Context context;
        private String TAG = "";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            EditText searchView = (EditText) rootView.findViewById(R.id.searchView);
            final Button login_button = (Button) rootView.findViewById(R.id.button_login);
            Button history_button = (Button) rootView.findViewById(R.id.button_history);
            Button wordlist_button = (Button) rootView.findViewById(R.id.button_wordlist);
            Button logout_button = (Button) rootView.findViewById(R.id.button_logout);

            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null) {
                isLogin = extras.getInt("Logged_in");
                user_email = extras.getString("email_user");
                token = extras.getString("token");
            }

            searchView.setOnEditorActionListener(new EditText.OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                {
                    String input;
                    if(actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_NULL
                            || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        input= v.getText().toString();
                        Intent intent = new Intent(getActivity(), WordActivity.class);
                        intent.putExtra("Pass_word",input);
                        intent.putExtra("isLoggedin",isLogin);
                        intent.putExtra("token",token);
                        Log.d("Send token", "onEditorAction: " + token);
                        startActivity(intent);
                        return true; // consume.
                    }
                    return false; // pass on to other listeners.
                }
            });

            if (isLogin == 0) {
                login_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                });

            } else {
                login_button.setText("Xin chao, " + user_email);

            }

            history_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    Intent intent = new Intent(getActivity(), HistoryActivity.class);
                    startActivity(intent);
                }
            });
            wordlist_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    Intent intent = new Intent(getActivity(), WordListActivity.class);
                    startActivity(intent);
                }
            });

            logout_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    context = getContext();
                    if (isNetworkConnected(context)) {
                        // if is connected to network
                        String myUrl = ConnectionInfo.HOST + "api/auth/logout";
                        String response = null;
                        try {
                            URL url = new URL(myUrl);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            conn.setRequestProperty("token",token);
                            conn.setRequestMethod("GET");
                            // read the response
                            InputStream in = new BufferedInputStream(conn.getInputStream());
                            response = convertStreamToString(in);
                        } catch (MalformedURLException e) {
                            Log.e(TAG, "MalformedURLException: " + e.getMessage());
                        } catch (ProtocolException e) {
                            Log.e(TAG, "ProtocolException: " + e.getMessage());
                        } catch (IOException e) {
                            Log.e(TAG, "IOException: " + e.getMessage());
                        } catch (Exception e) {
                            Log.e(TAG, "Exception: " + e.getMessage());
                        }
                    } else {
                    // if not, display a warning
                        Toast.makeText(context, "No network connection available.", Toast.LENGTH_SHORT).show();
                    }
                    login_button.setText("Login");
                    isLogin = 0;
                    Intent i = new Intent(getActivity(), getActivity().getClass());  //your class
                    startActivity(i);
                    getActivity().finish();
                }
            });

            return rootView;
        }

        public static boolean isNetworkConnected(Context c) {
            ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conManager.getActiveNetworkInfo();
            return ( netInfo != null && netInfo.isConnected() );
        }

        public static String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
