package com.example.hembit.idict.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.hembit.idict.R;

import org.json.JSONObject;

/**
 * Created by hembit on 23/12/2016.
 */

public class User {
    private String basicAuth;
    private int id;
    private int type;
    private static User user = new User();

    private User(){};

    public void initUser(String basicAuth, JSONObject jsonObject, Context context) {
        this.basicAuth = basicAuth;
        try {
            this.id = jsonObject.getInt("id");
            this.type = jsonObject.getInt("type");
        } catch (Exception e) {
            Toast.makeText(context, "Parsing json error", Toast.LENGTH_LONG);
        }
    }

    public static User getUser() {
        return user;
    }

    /**
     * Save information of user if the remember checkbox is checked
     * If the checkbox is uncheck the id is saved to -1
     * @param context
     * @param remember
     */
    public void rememberUser(Context context, boolean remember) {
        SharedPreferences preferenceSettings = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                context.MODE_PRIVATE
        );
        SharedPreferences.Editor preferenceEditor = preferenceSettings.edit();

        if(remember){
            preferenceEditor.putString("basicAuth", basicAuth);
            preferenceEditor.putInt("id", id);
            preferenceEditor.putInt("type", type);
        } else {
            preferenceEditor.putInt("id", -1);
        }

        preferenceEditor.commit();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBasicAuth() {
        return basicAuth;
    }

    public void setBasicAuth(String basicAuth) {
        this.basicAuth = basicAuth;
    }
}
