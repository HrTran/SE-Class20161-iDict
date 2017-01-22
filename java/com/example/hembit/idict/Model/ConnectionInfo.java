package com.example.hembit.idict.Model;

import android.util.Base64;

/**
 * Created by hembit on 28/12/2016.
 */

public class ConnectionInfo {
    public static final String HOST = "http://52.36.12.106/";
    private String credentials = "sam@sam.com" + ":" + "sam123";
    public static String au = "1";

    private String auth = "Basic "
            + Base64.encodeToString(credentials.getBytes(),
            Base64.NO_WRAP);

    private static final ConnectionInfo connectionInfo = new ConnectionInfo();

    private ConnectionInfo(){};

    public static ConnectionInfo getInstance() {
        return connectionInfo;
    }

    public String getAuth() {
        return auth;
    }

}

